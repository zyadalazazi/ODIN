import shutil

from flask import Flask, request, send_file
from flask_cors import CORS

from api.functions import *
from pipeline_translator.pipeline_translator import translate_graph_folder, translate_graph
from dataset_annotator.annotator import annotate_dataset

app = Flask(__name__)
CORS(app)

# TODO Change folder
files_folder = os.path.abspath(r'./api/temp_files')


@app.get('/problems')
def get_problems():
    ontology_only_problems = get_custom_ontology_only_problems()
    problems = {n.fragment: n for n in ontology_only_problems.subjects(RDF.type, tb.Problem)}
    return problems


@app.post('/abstract_planner')
def run_abstract_planner():
    intent_graph = get_graph_xp()

    intent_name = request.json.get('intent_name', '')
    dataset = request.json.get('dataset', '')
    problem = request.json.get('problem', '')
    ontology = Graph().parse(data=request.json.get('ontology', ''), format='turtle')

    intent_graph.add((ab.term(intent_name), RDF.type, tb.Intent))
    intent_graph.add((ab.term(intent_name), tb.overData, URIRef(dataset)))
    intent_graph.add((ab.term(intent_name), tb.tackles, URIRef(problem)))

    intent = intent_graph
    abstract_plans, algorithm_implementations = abstract_planner(ontology, intent)
    return {"abstract_plans": abstract_plans, "intent": intent.serialize(format="turtle"),
            "algorithm_implementations": algorithm_implementations}


def convert_strings_to_uris(obj):
    if isinstance(obj, list):  # If the object is a list
        return [convert_strings_to_uris(item) for item in obj]  # Recursively process each item in the list
    elif isinstance(obj, dict):  # If the object is a dictionary (only the "first" level)
        # Recursively process each value in the dictionary + transform key to URI
        return {URIRef(key): convert_strings_to_uris(value) for key, value in obj.items()}
    else:
        return URIRef(obj)  # Convert non-list, non-dictionary values to URIs


@app.post('/logical_planner')
def run_logical_planner():
    plan_ids = request.json.get('plan_ids', '')
    intent_json = request.json.get('intent_graph', '')
    algorithm_implementations = request.json.get('algorithm_implementations', '')
    ontology = Graph().parse(data=request.json.get('ontology', ''), format='turtle')

    # The algorithms come from the frontend in String format. We need to change them back to URIRefs
    algorithm_implementations_uris = convert_strings_to_uris(algorithm_implementations)

    intent = Graph().parse(data=intent_json, format='turtle')
    intent.print()

    impls = [impl
             for alg, impls in algorithm_implementations_uris.items() if str(alg) in plan_ids
             for impl in impls]

    workflow_plans = workflow_planner(ontology, impls, intent)
    logical_plans = logical_planner(ontology, workflow_plans)

    return logical_plans


@app.post('/workflow_plans/knime/all')
def download_all_knime():
    graphs = request.json.get("graphs", "")
    ontology = Graph().parse(data=request.json.get('ontology', ''), format='turtle')

    folder = os.path.join(files_folder, 'rdf_to_trans')
    knime_folder = os.path.join(files_folder, 'knime')

    if os.path.exists(folder):
        shutil.rmtree(folder)
    if os.path.exists(knime_folder):
        shutil.rmtree(knime_folder)
    os.mkdir(folder)
    os.mkdir(knime_folder)

    for graph_id, graph_content in graphs.items():
        graph = Graph().parse(data=graph_content, format='turtle')
        file_path = os.path.join(folder, f'{graph_id}.ttl')
        graph.serialize(file_path, format='turtle')

    translate_graph_folder(ontology, folder, knime_folder, keep_folder=False)

    compress(knime_folder, knime_folder + '.zip')
    return send_file(knime_folder + '.zip', as_attachment=True)


@app.post('/workflow_plans/knime')
def download_knime():
    plan_graph = Graph().parse(data=request.json.get("graph", ""), format='turtle')
    ontology = Graph().parse(data=request.json.get('ontology', ''), format='turtle')

    file_path = os.path.join(files_folder, f'{uuid.uuid4()}.ttl')
    plan_graph.serialize(file_path, format='turtle')

    knime_file_path = file_path[:-4] + '.knwf'
    translate_graph(ontology, file_path, knime_file_path)

    return send_file(knime_file_path, as_attachment=True)


@app.post('/annotate_dataset')
def annotate_dataset_from_frontend():
    path = request.json.get('path', '')
    label = request.json.get('label', '')
    data_product_name = path[path.rfind("\\") + 1:-4]

    new_path = path[0:path.rfind("\\") + 1] + data_product_name + "_annotated.ttl"
    annotate_dataset(path, new_path, label)

    custom_ontology = get_custom_ontology(new_path)
    datasets = {n.fragment: n for n in custom_ontology.subjects(RDF.type, dmop.TabularDataset)}
    return {"ontology": custom_ontology.serialize(format="turtle"),
            "data_product_uri": datasets[data_product_name + ".csv"]}