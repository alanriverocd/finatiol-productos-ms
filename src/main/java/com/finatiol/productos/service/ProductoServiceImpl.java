package com.finatiol.productos.service;

import com.finatiol.productos.dto.ProductoRequestDTO;
import com.finatiol.productos.dto.ProductoResponseDTO;
import com.finatiol.productos.entity.ProductoEntity;
import com.finatiol.common.exception.ResourceNotFoundException;
import com.finatiol.productos.exception.ProductoNoEncontradoException;
import com.finatiol.productos.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl
        implements ProductoService {

    private final ProductoRepository
            productoRepository;

    public ProductoServiceImpl(
            ProductoRepository productoRepository) {

        this.productoRepository =
                productoRepository;
    }

    @Override
    public ProductoResponseDTO crearProducto(
            ProductoRequestDTO request) {

        ProductoEntity producto =
                new ProductoEntity();

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setActivo(true);

        return toResponseDTO(
                productoRepository.save(producto));
    }

    @Override
    public List<ProductoResponseDTO> listarProductos() {

        return productoRepository
                .findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public ProductoResponseDTO obtenerProductoPorId(Long id) {

        ProductoEntity producto =
                productoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ProductoNoEncontradoException(
                                        "Producto no encontrado con id: " + id));

        return toResponseDTO(producto);
    }

    @Override
    public ProductoResponseDTO actualizarProducto(
            Long id,
            ProductoRequestDTO request) {

        ProductoEntity producto =
                productoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ProductoNoEncontradoException(
                                        "Producto no encontrado con id: " + id));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());

        return toResponseDTO(
                productoRepository.save(producto));
    }

    @Override
    public void eliminarProducto(Long id) {

        ProductoEntity producto =
                productoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ProductoNoEncontradoException(
                                        "Producto no encontrado con id: " + id));

        productoRepository.delete(producto);
    }

    @Override
    public void descontarStock(Long id, Integer cantidad) {

        ProductoEntity producto =
                productoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Producto no encontrado"));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    @Override
    public List<ProductoResponseDTO> obtenerProductosActivos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public Page<ProductoResponseDTO> obtenerProductosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @Override
    public List<ProductoResponseDTO> buscarProductos(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private ProductoResponseDTO toResponseDTO(
            ProductoEntity producto) {

        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getActivo()
        );
    }
}
