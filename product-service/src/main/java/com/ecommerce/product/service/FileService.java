package com.ecommerce.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    String uploadFile(MultipartFile file, String directory);

    /**
     * Upload nhiều file cùng lúc
     *
     * @param files danh sách file cần upload
     * @param directory thư mục đích trong MinIO
     * @return danh sách đường dẫn của các file đã upload
     */
    List<String> uploadFiles(List<MultipartFile> files, String directory);

    /**
     * Lấy presigned URL từ object path
     *
     * @param objectPath đường dẫn object trong MinIO
     * @return presigned URL có hiệu lực 7 ngày
     */
    String getPresignedUrl(String objectPath);

    /**
     * Xóa tất cả các file trong thư mục đã cho
     *
     * @param directory đường dẫn thư mục trong MinIO
     */
    void deleteFilesInDirectory(String directory);

    /**
     * Xóa một file cụ thể
     *
     * @param objectPath đường dẫn file cần xóa trong MinIO
     */
    void deleteFile(String objectPath);
}
