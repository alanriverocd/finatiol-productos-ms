package com.finatiol.productos.service;

import com.finatiol.productos.dto.ProductoResponseDTO;
import com.finatiol.productos.entity.ProductoEntity;
import com.finatiol.productos.entity.ProductoImagenEntity;
import com.finatiol.productos.repository.ProductoImagenRepository;
import com.finatiol.productos.repository.ProductoRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductoServiceImplTest {

    @Test
    void listarProductosDebePoderSerializarseYDeserializarseEnCacheRedis() {
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        ProductoImagenRepository productoImagenRepository = mock(ProductoImagenRepository.class);
        MinioService minioService = mock(MinioService.class);
        ProductoServiceImpl service = new ProductoServiceImpl(
                productoRepository,
                productoImagenRepository,
                minioService,
                new SimpleMeterRegistry());

        ProductoEntity producto = new ProductoEntity();
        producto.setId(9L);
        producto.setNombre("SMART TV HISENSE 32'");
        producto.setDescripcion("Television con roku integrado y UHD para mejor definicion en la imagen");
        producto.setPrecio(4200.0);
        producto.setStock(2);
        producto.setActivo(true);
        producto.getImagenes().add(new ProductoImagenEntity(
                producto,
                "http://localhost:9000/productos/9/imagen.png",
                "9/imagen.png",
                0));
        producto.getImagenes().getFirst().setId(8L);

        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<ProductoResponseDTO> response = service.listarProductos();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        Object restored = assertDoesNotThrow(() -> serializer.deserialize(serializer.serialize(response)));

        assertInstanceOf(List.class, restored);
        List<?> restoredList = (List<?>) restored;
        assertEquals(1, restoredList.size());
        assertInstanceOf(ProductoResponseDTO.class, restoredList.getFirst());
        ProductoResponseDTO restoredProducto = (ProductoResponseDTO) restoredList.getFirst();
        assertEquals(1, restoredProducto.getImagenes().size());
    }
}