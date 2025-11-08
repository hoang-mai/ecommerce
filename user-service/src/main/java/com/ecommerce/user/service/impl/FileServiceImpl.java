package com.ecommerce.user.service.impl;

import com.ecommerce.library.utils.MessageError;
import com.ecommerce.user.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private void deleteFilesInDirectory(String directory) {
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
}
