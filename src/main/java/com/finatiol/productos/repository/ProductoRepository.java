package com.finatiol.productos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.finatiol.productos.entity.ProductoEntity;

public interface ProductoRepository
        extends JpaRepository<ProductoEntity, Long>,
        PagingAndSortingRepository<ProductoEntity, Long> {

    List<ProductoEntity> findByActivoTrue();

    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);
}
