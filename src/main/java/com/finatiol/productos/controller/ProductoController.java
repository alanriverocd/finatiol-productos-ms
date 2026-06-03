package com.finatiol.productos.controller;

import com.finatiol.common.response.SuccessResponse;
import com.finatiol.common.constants.productos.SuccessCodes;
import com.finatiol.common.constants.productos.SuccessMessages;
import com.finatiol.productos.dto.ActualizarStockDTO;
import com.finatiol.productos.dto.ApiResponse;
import com.finatiol.productos.dto.ProductoRequestDTO;
import com.finatiol.productos.dto.ProductoResponseDTO;
import com.finatiol.productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "API para gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear producto con hasta 10 imágenes opcionales")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> crearProducto(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam Double precio,
            @RequestParam Integer stock,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes) {

        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre(nombre);
        request.setDescripcion(descripcion);
        request.setPrecio(precio);
        request.setStock(stock);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        SuccessCodes.PRODUCTO_CREADO,
                        SuccessMessages.PRODUCTO_CREADO,
                        productoService.crearProducto(request, imagenes)));
    }

    @PostMapping(value = "/{id}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Agregar imágenes a un producto existente (máx. 10 en total)")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> agregarImagenes(
            @PathVariable Long id,
            @RequestParam("imagenes") List<MultipartFile> imagenes) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.PRODUCTO_ACTUALIZADO,
                SuccessMessages.PRODUCTO_ACTUALIZADO,
                productoService.agregarImagenes(id, imagenes)));
    }

    @DeleteMapping("/{id}/imagenes/{imagenId}")
    @Operation(summary = "Eliminar una imagen específica de un producto")
    public ResponseEntity<ApiResponse<Void>> eliminarImagen(
            @PathVariable Long id,
            @PathVariable Long imagenId) {

        productoService.eliminarImagenDeProducto(id, imagenId);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.PRODUCTO_ACTUALIZADO,
                SuccessMessages.PRODUCTO_ACTUALIZADO,
                null));
    }

    @GetMapping
    @Operation(summary = "Listar productos")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listarProductos() {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.PRODUCTOS_OBTENIDOS,
                SuccessMessages.PRODUCTOS_OBTENIDOS,
                productoService.listarProductos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> obtenerProducto(
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.PRODUCTO_OBTENIDO,
                SuccessMessages.PRODUCTO_OBTENIDO,
                productoService.obtenerProductoPorId(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.PRODUCTO_ACTUALIZADO,
                SuccessMessages.PRODUCTO_ACTUALIZADO,
                productoService.actualizarProducto(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<ApiResponse<Void>> eliminarProducto(
            @PathVariable Long id) {

        productoService.eliminarProducto(id);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.PRODUCTO_ELIMINADO,
                SuccessMessages.PRODUCTO_ELIMINADO,
                null));
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Descontar stock")
    public ResponseEntity<Void> descontarStock(
            @PathVariable Long id,
            @RequestBody ActualizarStockDTO request) {

        productoService.descontarStock(id, request.getCantidad());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar productos activos")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> obtenerProductosActivos() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.PRODUCTOS_OBTENIDOS,
                SuccessMessages.PRODUCTOS_OBTENIDOS,
                productoService.obtenerProductosActivos()));
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar productos paginados")
    public ResponseEntity<SuccessResponse<Page<ProductoResponseDTO>>> obtenerProductosPaginados(
            @RequestParam int page,
            @RequestParam int size) {
        return ResponseEntity.ok(new SuccessResponse<>(
                SuccessCodes.PRODUCTOS_OBTENIDOS,
                SuccessMessages.PRODUCTOS_OBTENIDOS,
                200,
                productoService.obtenerProductosPaginados(page, size)));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre")
    public ResponseEntity<SuccessResponse<List<ProductoResponseDTO>>> buscarProductos(
            @RequestParam String nombre) {
        return ResponseEntity.ok(new SuccessResponse<>(
                SuccessCodes.PRODUCTOS_OBTENIDOS,
                SuccessMessages.PRODUCTOS_OBTENIDOS,
                200,
                productoService.buscarProductos(nombre)));
    }

    @GetMapping("/resumen")
    @Operation(summary = "Total de productos activos (para dashboard)")
    public ResponseEntity<Long> resumenProductosActivos() {
        return ResponseEntity.ok(productoService.contarProductosActivos());
    }
}
