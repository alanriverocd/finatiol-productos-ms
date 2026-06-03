package com.finatiol.productos.service;

import com.finatiol.productos.dto.ProductoRequestDTO;
import com.finatiol.productos.dto.ProductoResponseDTO;
import com.finatiol.productos.dto.ProductoImagenDTO;
import com.finatiol.productos.entity.ProductoEntity;
import com.finatiol.productos.entity.ProductoImagenEntity;
import com.finatiol.common.exception.ResourceNotFoundException;
import com.finatiol.productos.exception.ProductoNoEncontradoException;
import com.finatiol.productos.repository.ProductoRepository;
import com.finatiol.productos.repository.ProductoImagenRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl
        implements ProductoService {

    private static final int MAX_IMAGENES = 10;

    private final ProductoRepository productoRepository;

    private final ProductoImagenRepository productoImagenRepository;

    private final MinioService minioService;

    private final Counter stockDescuentosCounter;

    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            ProductoImagenRepository productoImagenRepository,
            MinioService minioService,
            MeterRegistry meterRegistry) {

        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
        this.minioService = minioService;
        this.stockDescuentosCounter = Counter.builder("productos_stock_descuentos_total")
                .description("Total de descuentos de stock aplicados")
                .register(meterRegistry);
    }

    @Override
    @CacheEvict(value = {"productos", "producto"}, allEntries = true)
    public ProductoResponseDTO crearProducto(
            ProductoRequestDTO request,
            List<MultipartFile> imagenes) {

        ProductoEntity producto = new ProductoEntity();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setActivo(true);

        ProductoEntity guardado = productoRepository.save(producto);

        if (imagenes != null && !imagenes.isEmpty()) {
            List<MultipartFile> validas = imagenes.stream()
                    .filter(img -> img != null && !img.isEmpty())
                    .limit(MAX_IMAGENES)
                    .toList();
            int orden = 0;
            for (MultipartFile img : validas) {
                String ext = obtenerExtension(img.getOriginalFilename());
                String objectName = guardado.getId() + "/" + UUID.randomUUID() + ext;
                String url = minioService.subirImagen(img, objectName);
                guardado.getImagenes().add(new ProductoImagenEntity(guardado, url, objectName, orden++));
            }
            guardado = productoRepository.save(guardado);
        }

        return toResponseDTO(guardado);
    }

    @Override
    @CacheEvict(value = {"productos", "producto"}, allEntries = true)
    public ProductoResponseDTO agregarImagenes(Long productoId, List<MultipartFile> imagenes) {

        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                        "Producto no encontrado con id: " + productoId));

        int actuales = producto.getImagenes().size();
        if (actuales >= MAX_IMAGENES) {
            throw new IllegalStateException(
                    "El producto ya tiene el máximo de " + MAX_IMAGENES + " imágenes");
        }

        List<MultipartFile> validas = imagenes.stream()
                .filter(img -> img != null && !img.isEmpty())
                .limit(MAX_IMAGENES - actuales)
                .toList();

        int orden = actuales;
        for (MultipartFile img : validas) {
            String ext = obtenerExtension(img.getOriginalFilename());
            String objectName = producto.getId() + "/" + UUID.randomUUID() + ext;
            String url = minioService.subirImagen(img, objectName);
            producto.getImagenes().add(new ProductoImagenEntity(producto, url, objectName, orden++));
        }

        return toResponseDTO(productoRepository.save(producto));
    }

    @Override
    @CacheEvict(value = {"productos", "producto"}, allEntries = true)
    public void eliminarImagenDeProducto(Long productoId, Long imagenId) {

        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                        "Producto no encontrado con id: " + productoId));

        ProductoImagenEntity imagen = productoImagenRepository.findById(imagenId)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                        "Imagen no encontrada con id: " + imagenId));

        if (!imagen.getProducto().getId().equals(productoId)) {
            throw new IllegalArgumentException("La imagen no pertenece al producto indicado");
        }

        minioService.eliminarImagen(imagen.getObjectName());
        producto.getImagenes().remove(imagen);
        productoRepository.save(producto);
    }

    @Override
    @Cacheable("productos")
    public List<ProductoResponseDTO> listarProductos() {

        return productoRepository
                .findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Cacheable(value = "producto", key = "#id")
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
    @CacheEvict(value = {"productos", "producto"}, allEntries = true)
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
    @CacheEvict(value = {"productos", "producto"}, allEntries = true)
    public void eliminarProducto(Long id) {

        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(
                        "Producto no encontrado con id: " + id));

        for (ProductoImagenEntity imagen : new ArrayList<>(producto.getImagenes())) {
            minioService.eliminarImagen(imagen.getObjectName());
        }

        productoRepository.delete(producto);
    }

    @Override
    @CacheEvict(value = {"productos", "producto"}, allEntries = true)
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
        stockDescuentosCounter.increment();
    }

    @Override
    public List<ProductoResponseDTO> obtenerProductosActivos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toCollection(ArrayList::new));
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
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Long contarProductosActivos() {
        return (long) productoRepository.findByActivoTrue().size();
    }

    private ProductoResponseDTO toResponseDTO(ProductoEntity producto) {

        List<ProductoImagenDTO> imagenes = producto.getImagenes().stream()
                .map(img -> new ProductoImagenDTO(img.getId(), img.getUrl(), img.getOrden()))
            .collect(Collectors.toCollection(ArrayList::new));

        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getActivo(),
                imagenes
        );
    }

    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return "." + filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
