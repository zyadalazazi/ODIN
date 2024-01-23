package edu.upc.essi.dtim.NextiaDataLayer.dataLayer;

import edu.upc.essi.dtim.NextiaCore.datasources.dataset.Dataset;
import edu.upc.essi.dtim.NextiaCore.queries.Query;
import edu.upc.essi.dtim.NextiaDataLayer.utils.ResultSetSpark;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.sql.*;

public class DLSpark extends DataLayer {
    SparkConf conf = new SparkConf().setAppName("Spark").setMaster("local");
    JavaSparkContext sc = new JavaSparkContext(conf);
    SparkSession spark = SparkSession.builder().config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension").config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog").appName("Spark").getOrCreate();
    public DLSpark(String dataStorePath){
        super(dataStorePath);
    }

    @Override
    public void uploadToFormattedZone(Dataset d, String tableName) {
        String path = dataStorePath + "landingZone\\" + d.getUUID();

        org.apache.spark.sql.Dataset<Row> df = spark.read().parquet(path);
        df.write().format("delta").save(dataStorePath + "DeltaLake\\formattedZone\\" + tableName);
    }

    @Override
    public void removeFromFormattedZone(String tableName) {
        deleteFilesFromDirectory(dataStorePath + "DeltaLake\\formattedZone\\" + tableName);
    }

    @Override
    public ResultSet executeQuery(String sql, Dataset[] datasets) {
        return new ResultSetSpark(spark.sql(sql));
    }

    @Override
    public ResultSet executeQuery(String sql) {
        return new ResultSetSpark(spark.sql(sql));
    }

    @Override
    public void execute(String sql) {

    }

    @Override
    public void close() {
        // Remove all the files in the temporal zone (/tmp)
        deleteFilesFromDirectory(dataStorePath + "tmp");
    }

    @Override
    public void copyToExploitationZone(String UUID) {

    }

    @Override
    public void uploadToTemporalExploitationZone(String sql, String UUID) {
    }

    @Override
    public void removeFromExploitationZone(String tableName) {

    }

    @Override
    public String materialize(Dataset dataset, String zone, String format) {
        return null;
    }

    @Override
    public void test() {

    }
}
