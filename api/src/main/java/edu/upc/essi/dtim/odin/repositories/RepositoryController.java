package edu.upc.essi.dtim.odin.repositories;

import edu.upc.essi.dtim.odin.repositories.POJOs.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RepositoryController {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);
    @Autowired
    private RepositoryService repositoryService;

    /**
     * Test database connection (for JDBC repositories)
     *
     * @param requestData object with the necessary parameters to perform the database connection.
     * @return A boolean indicating if the connection was successful.
     */
    @PostMapping("/test-connection")
    public Boolean testConnection(@RequestBody Map<String, String> requestData) {
        return repositoryService.testConnectionFromRequest(requestData);
    }

    /**
     * Retrieve the tables of a database (for JDBC repositories)
     *
     * @param repositoryId Identification of the repository whose tables will be retrieved (the repository has a
     *                     parameter with the database URL, so we can connect to it and extract the information).
     * @return A list with the tables names, sizes and other info.
     */
    @GetMapping(value = "/{id}/tables")
    public ResponseEntity<Object> retrieveDBTables(@PathVariable("id") String repositoryId) {
        logger.info("Get tables received from repository: " + repositoryId);
        List<TableInfo> tables = repositoryService.retrieveTablesInfo(repositoryId);
        return new ResponseEntity<>(tables, HttpStatus.OK);
    }

    /**
     * Creates a new repository
     *
     * @param projectId Identification of the project to which the new repository will belong to.
     * @param repositoryData object with the necessary data to create the new repository.
     * @return If the task was successful return a ResponseEntity with an OK HTTP code.
     */
    @PostMapping(value = "/project/{projectID}/repository")
    public ResponseEntity<Object> postRepository(@PathVariable("projectID") String projectId,
                                                 @RequestBody Map<String, String> repositoryData) {
        logger.info("Post repository received for project " + projectId + " with repo name " + repositoryData.get("repositoryName") + " and type " + repositoryData.get("repositoryType"));
        repositoryService.postRepository(repositoryData, projectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes a repository from a specific project, and its associated datasets.
     *
     * @param projectID     The ID of the project from which to delete the repository.
     * @param repositoryID  The ID of the repository to delete.
     * @return If the task was successful return a ResponseEntity with an OK HTTP code.
     */
    @DeleteMapping("/project/{projectID}/repository/{repositoryID}")
    public ResponseEntity<Boolean> deleteDataset(@PathVariable("projectID") String projectID,
                                                 @PathVariable("repositoryID") String repositoryID) {
        logger.info("Delete repository " + repositoryID + " from project: " +  projectID);
        repositoryService.deleteRepositoryFromProject(projectID, repositoryID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Edits a repository (name) in a specific project.
     *
     * @param repositoryID   The ID of the repository to edit.
     * @param repositoryName The new name for the repository.
     * @return If the task was successful return a ResponseEntity with an OK HTTP code.
     */
    @PutMapping("project/{projectID}/repository/{repositoryID}")
    public ResponseEntity<Boolean> putRepository(@RequestParam("repositoryID") String repositoryID,
                                                 @RequestParam("repositoryName") String repositoryName) {
        logger.info("Edit request received for editing repository with ID: " +  repositoryID);
        repositoryService.editDataset(repositoryID, repositoryName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

