package com.ecommerce.product.service.impl;

import com.ecommerce.library.utils.MessageError;
import com.ecommerce.product.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(MessageError.FILE_EMPTY);
        }
        if (!isValidImageType(file.getContentType())) {
            throw new IllegalArgumentException(MessageError.FILE_INVALID_TYPE);
        }
        try {
            deleteFilesInDirectory(directory);

            String objectName = directory + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException(MessageError.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Lấy presigned URL từ object path
     * URL có hiệu lực trong 7 ngày
     *
     * @param objectPath đường dẫn object trong MinIO
     * @return presigned URL
     */
    public String getPresignedUrl(String objectPath) {
        if (objectPath == null || objectPath.isEmpty()) {
            return null;
        }
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectPath)
                            .expiry(7 * 24 * 60 * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(MessageError.FILE_UPLOAD_FAILED);
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/webp")
        );
    }

    /**
     * Xóa tất cả các file trong thư mục chỉ định
     *
     * @param directory đường dẫn thư mục cần xóa file
     */
    public void deleteFilesInDirectory(String directory) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(directory + "/")
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build()
                );
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Upload nhiều file cùng lúc
     *
     * @param files danh sách file cần upload
     * @param directory thư mục đích trong MinIO
     * @return danh sách đường dẫn của các file đã upload
     */
    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String directory) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException(MessageError.FILE_EMPTY);
        }

        List<String> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; // Bỏ qua file rỗng
            }

            if (!isValidImageType(file.getContentType())) {
                throw new IllegalArgumentException(MessageError.FILE_INVALID_TYPE);
            }

            try {
                String objectName = directory + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
                uploadedFiles.add(objectName);

                // Thêm delay nhỏ để tránh trùng timestamp
                Thread.sleep(1);
            } catch (Exception e) {
                throw new RuntimeException(MessageError.FILE_UPLOAD_FAILED + ": " + file.getOriginalFilename());
            }
        }

        return uploadedFiles;
    }

    /**
     * Xóa một file cụ thể
     *
     * @param objectPath đường dẫn file cần xóa trong MinIO
     */
    @Override
    public void deleteFile(String objectPath) {
        if (objectPath == null || objectPath.isEmpty()) {
            throw new IllegalArgumentException("Object path cannot be null or empty");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + objectPath);
        }
    }
}
