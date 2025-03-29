package com.healthvision.ml;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GCSClient {
    private final Storage storage;
    
    public GCSClient() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }
    
    public void uploadModel(String localPath, String gcsPath) throws Exception {
        String[] parts = gcsPath.replace("gs://", "").split("/", 2);
        BlobId blobId = BlobId.of(parts[0], parts[1]);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(localPath)));
    }
    
    public void downloadModel(String gcsPath, String localPath) throws Exception {
        String[] parts = gcsPath.replace("gs://", "").split("/", 2);
        storage.get(BlobId.of(parts[0], parts[1]))
              .downloadTo(Paths.get(localPath));
    }
}