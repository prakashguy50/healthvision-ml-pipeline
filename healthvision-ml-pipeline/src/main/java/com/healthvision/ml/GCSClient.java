package com.healthvision.ml;

import com.google.cloud.storage.*;

public class GCSClient {
    private final Storage storage;
    private final String bucketName;
    
    public GCSClient() {
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucketName = System.getProperty("gcs.bucket");
    }
    
    public void uploadModel(String modelPath) throws Exception {
        BlobId blobId = BlobId.of(bucketName, "models/" + modelPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, new FileInputStream(modelPath).readAllBytes());
    }
    
    public void downloadModel(String modelName, String localPath) throws Exception {
        Blob blob = storage.get(bucketName, "models/" + modelName);
        blob.downloadTo(java.nio.file.Paths.get(localPath));
    }
}