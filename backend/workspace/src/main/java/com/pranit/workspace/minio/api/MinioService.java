package com.pranit.workspace.minio.api;

import java.io.InputStream;

public interface MinioService {

    void upload(String objectName, InputStream inputStream, long size, String contentType) throws Exception;

    InputStream download(String objectName) throws Exception;

    void delete(String objectName) throws Exception;

    boolean exists(String objectName) throws Exception;

    String getPresignedUrl(String objectName) throws Exception;
}
