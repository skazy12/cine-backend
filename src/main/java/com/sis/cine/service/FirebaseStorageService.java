package com.sis.cine.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageService {

    private final Storage storage;

    public FirebaseStorageService(FirebaseApp firebaseApp) {
        this.storage = StorageClient.getInstance(firebaseApp).bucket().getStorage();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        BlobId blobId = BlobId.of("siscine.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        Blob blob = storage.create(blobInfo, file.getInputStream());

        return fileName;
    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }
    public String generateSignedUrl(String fileName) {
        BlobInfo blobInfo = BlobInfo.newBuilder("siscine.appspot.com", fileName).build();
        URL signedUrl = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
        return signedUrl.toString();
    }
}