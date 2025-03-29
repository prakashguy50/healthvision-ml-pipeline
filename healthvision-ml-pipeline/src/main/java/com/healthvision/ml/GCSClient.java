package com.healthvision.ml;

import com.google.cloud.storage.*;
import java.nio.file.Paths;
import java.nio.file.Files;

public class GCSClient {
    private final Storage storage;
    
    public GCSClient() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }
    
    public void uploadModel(String localPath, String gcsPath) throws Exception {
        try {
            // Validate local file
            if (!Files.exists(Paths.get(localPath))) {
                throw new IllegalArgumentException("Local file not found: " + localPath);
            }
            
            // Parse GCS path
            String[] parts = gcsPath.replace("gs://", "").split("/", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid GCS path format. Use: gs://bucket/path");
            }
            
            // Upload file
            BlobId blobId = BlobId.of(parts[0], parts[1]);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            
            System.out.printf("Uploading %s to %s%n", localPath, gcsPath);
            storage.create(blobInfo, Files.readAllBytes(Paths.get(localPath)));
            System.out.println("Upload successful!");
            
        } catch (StorageException e) {
            throw new RuntimeException("GCS operation failed: " + e.getMessage(), e);
        }
    }
    
    public void downloadModel(String gcsPath, String localPath) throws Exception {
        try {
            // Parse GCS path
            String[] parts = gcsPath.replace("gs://", "").split("/", 2);
            
            // Download file
            Blob blob = storage.get(BlobId.of(parts[0], parts[1]));
            if (blob == null) {
                throw new IllegalArgumentException("GCS file not found: " + gcsPath);
            }
            
            System.out.printf("Downloading %s to %s%n", gcsPath, localPath);
            blob.downloadTo(Paths.get(localPath));
            System.out.println("Download successful!");
            
        } catch (StorageException e) {
            throw new RuntimeException("GCS download failed: " + e.getMessage(), e);
        }
    }
}