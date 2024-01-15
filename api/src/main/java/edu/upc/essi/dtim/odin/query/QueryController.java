package edu.upc.essi.dtim.odin.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upc.essi.dtim.NextiaCore.queries.Workflow;
import edu.upc.essi.dtim.odin.query.pojos.QueryDataSelection;
import edu.upc.essi.dtim.odin.query.pojos.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class QueryController {
    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);
    @Autowired
    private QueryService queryService;

    // TODO: Description
    @PostMapping(value = "/query/{id}/graphical")
    public ResponseEntity<QueryResult> queryFromGraphicalToSPARQL(@PathVariable("id") String id,
                                                                  @RequestBody QueryDataSelection body) {
        logger.info("Getting query");
        QueryResult res = queryService.getQueryResult(body, id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // TODO: Description
    // TODO; change path to /project/{projectID}/query
    @PostMapping("/storeQuery")
    public ResponseEntity<Boolean> storeQuery(@RequestParam("CSVPath") String CSVPath,
                                              @RequestParam("queryName") String queryName,
                                              @RequestParam("projectID") String projectID,
                                              @RequestParam("queryLabel") String queryLabel) {
        logger.info("Storing query");
        queryService.storeQuery(CSVPath, queryName, projectID, queryLabel);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/project/{projectID}/query/{queryID}/workflow")
    public ResponseEntity<Boolean> storeWorkflow(@PathVariable("projectID") String projectID,
                                                 @PathVariable("queryID") String queryID,
                                                 @RequestBody Workflow workflow) {
        logger.info("Storing workflow");
        queryService.storeWorkflow(queryID, workflow);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
