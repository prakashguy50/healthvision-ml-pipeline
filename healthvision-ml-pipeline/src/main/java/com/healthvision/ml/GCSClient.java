package com.healthvision.ml;

import com.google.cloud.storage.*;
import java.io.FileInputStream; // Import for FileInputStream
import java.io.IOException; // Import for IOException

public class GCSClient {
    private final Storage storage;
    private final String bucketName;
    
    public GCSClient() {
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucketName = System.getProperty("gcs.bucket");
    }
    
    public void uploadModel(String modelPath) throws IOException {
        BlobId blobId = BlobId.of(bucketName, "models/" + modelPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        
        // Ensure FileInputStream is properly closed after use
        try (FileInputStream fileInputStream = new FileInputStream(modelPath)) {
            storage.create(blobInfo, fileInputStream.readAllBytes());
        }
    }
    
    public void downloadModel(String modelName, String localPath) throws IOException {
        Blob blob = storage.get(bucketName, "models/" + modelName);
        blob.downloadTo(java.nio.file.Paths.get(localPath));
    }
}
