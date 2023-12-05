package edu.upc.essi.dtim.odin.nextiaInterfaces.nextiaDataLayer;

import edu.upc.essi.dtim.NextiaCore.datasources.dataset.Dataset;
import org.springframework.web.multipart.MultipartFile;

public interface DataLayerInterface {
    /**
     * @param dataset The dataset to upload to DataLayer
     */
    void uploadToDataLayer(Dataset dataset);

    void deleteDataset(String UUID);

    String reconstructFile(MultipartFile multipartFile, String repositoryIdAndName);
}