package com.finatiol.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finatiol.productos.entity.ProductoEntity;

public interface ProductoRepository
        extends JpaRepository<
                ProductoEntity,
                Long> {
}
