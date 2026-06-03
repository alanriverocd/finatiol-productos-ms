package com.finatiol.productos.repository;

import com.finatiol.productos.entity.ProductoImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagenEntity, Long> {

    List<ProductoImagenEntity> findByProductoIdOrderByOrdenAsc(Long productoId);

    int countByProductoId(Long productoId);
}
