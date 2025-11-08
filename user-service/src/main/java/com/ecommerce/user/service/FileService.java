package com.ecommerce.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file, String directory);

    /**
     * Lấy presigned URL từ object path
     *
     * @param objectPath đường dẫn object trong MinIO
     * @return presigned URL có hiệu lực 7 ngày
     */
    String getPresignedUrl(String objectPath);
}
