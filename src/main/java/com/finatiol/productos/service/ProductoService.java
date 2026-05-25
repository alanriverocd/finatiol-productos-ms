package com.finatiol.productos.service;

import com.finatiol.productos.dto.ProductoRequestDTO;
import com.finatiol.productos.dto.ProductoResponseDTO;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO request);

    List<ProductoResponseDTO> listarProductos();

    ProductoResponseDTO obtenerProductoPorId(Long id);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request);

    void eliminarProducto(Long id);

    void descontarStock(Long id, Integer cantidad);

    List<ProductoResponseDTO> obtenerProductosActivos();

    Page<ProductoResponseDTO> obtenerProductosPaginados(int page, int size);

    List<ProductoResponseDTO> buscarProductos(String nombre);
}
