package com.healthvision.ml;

import com.google.cloud.storage.*;
import com.google.auth.oauth2.GoogleCredentials;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class GCSClient {
    private final Storage storage;
    private static final String PROJECT_ID = "healthvision-ml-dev";
    private static final String BUCKET_NAME = "healthvision-ml-20250328-23525";

    public GCSClient() throws IOException {
        // Initialize with explicit credentials
        this.storage = StorageOptions.newBuilder()
            .setCredentials(getCredentials())
            .setProjectId(PROJECT_ID)
            .build()
            .getService();
    }

    private GoogleCredentials getCredentials() throws IOException {
        // Try getting application default credentials
        GoogleCredentials credentials;
        try {
            credentials = GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            throw new IOException(
                "Failed to get application default credentials. Please ensure:\n" +
                "1. You're authenticated with 'gcloud auth application-default login'\n" +
                "2. GOOGLE_APPLICATION_CREDENTIALS is set if using service account\n" +
                "Original error: " + e.getMessage()
            );
        }
        return credentials;
    }

    public void uploadModel(String localPath, String gcsPath) throws Exception {
        try {
            // Validate local file
            if (!Files.exists(Paths.get(localPath))) {
                throw new IllegalArgumentException("Local file not found: " + localPath);
            }

            // Parse GCS path (format: gs://bucket-name/path/to/file)
            String[] parts = gcsPath.replace("gs://", "").split("/", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException(
                    "Invalid GCS path format. Expected: gs://bucket-name/path, got: " + gcsPath
                );
            }

            String bucketName = parts[0];
            String objectName = parts[1];

            // Verify bucket exists
            if (!storage.get(bucketName).exists()) {
                throw new IllegalArgumentException(
                    "Bucket does not exist or you don't have permissions: " + bucketName
                );
            }

            System.out.printf("Uploading %s to %s%n", localPath, gcsPath);
            
            // Upload the file
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, Files.readAllBytes(Paths.get(localPath)));
            
            System.out.println("Upload successful!");
            System.out.printf("Public URL: https://storage.googleapis.com/%s/%s%n", 
                           bucketName, objectName);

        } catch (StorageException e) {
            throw new RuntimeException(
                "GCS operation failed. Please verify:\n" +
                "1. You have 'storage.objects.create' permission\n" +
                "2. Bucket exists (" + BUCKET_NAME + ")\n" +
                "3. Network connectivity is available\n" +
                "Error details: " + e.getMessage(), 
                e
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "Unexpected error during upload: " + e.getMessage(), 
                e
            );
        }
    }

    public void downloadModel(String gcsPath, String localPath) throws Exception {
        try {
            // Parse GCS path
            String[] parts = gcsPath.replace("gs://", "").split("/", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException(
                    "Invalid GCS path format. Expected: gs://bucket-name/path, got: " + gcsPath
                );
            }

            String bucketName = parts[0];
            String objectName = parts[1];

            System.out.printf("Downloading %s to %s%n", gcsPath, localPath);
            
            // Download the file
            Blob blob = storage.get(BlobId.of(bucketName, objectName));
            if (blob == null) {
                throw new IllegalArgumentException(
                    "GCS file not found: " + gcsPath + 
                    "\nCheck if file exists and you have 'storage.objects.get' permission"
                );
            }
            blob.downloadTo(Paths.get(localPath));
            
            System.out.println("Download successful!");

        } catch (StorageException e) {
            throw new RuntimeException(
                "GCS download failed. Please verify:\n" +
                "1. File exists in bucket\n" +
                "2. You have 'storage.objects.get' permission\n" +
                "Error details: " + e.getMessage(), 
                e
            );
        }
    }
}