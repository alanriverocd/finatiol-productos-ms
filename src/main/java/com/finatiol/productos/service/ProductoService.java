package com.finatiol.productos.service;

import com.finatiol.productos.dto.ProductoRequestDTO;
import com.finatiol.productos.dto.ProductoResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO request, List<MultipartFile> imagenes);

    ProductoResponseDTO agregarImagenes(Long productoId, List<MultipartFile> imagenes);

    void eliminarImagenDeProducto(Long productoId, Long imagenId);

    List<ProductoResponseDTO> listarProductos();

    ProductoResponseDTO obtenerProductoPorId(Long id);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request);

    void eliminarProducto(Long id);

    void descontarStock(Long id, Integer cantidad);

    List<ProductoResponseDTO> obtenerProductosActivos();

    Page<ProductoResponseDTO> obtenerProductosPaginados(int page, int size);

    List<ProductoResponseDTO> buscarProductos(String nombre);

    Long contarProductosActivos();
}
