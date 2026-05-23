package com.finatiol.productos.service;

import com.finatiol.productos.dto.ProductoRequestDTO;
import com.finatiol.productos.dto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO request);

    List<ProductoResponseDTO> listarProductos();

    ProductoResponseDTO obtenerProductoPorId(Long id);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request);

    void eliminarProducto(Long id);
}
