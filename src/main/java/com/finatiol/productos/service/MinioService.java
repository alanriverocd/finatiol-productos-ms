package com.finatiol.productos.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    String subirImagen(MultipartFile file, String objectName);

    void eliminarImagen(String objectName);
}
