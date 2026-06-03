package com.finatiol.productos.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductoResponseDTO {

    private Long id;

    private String nombre;

    private String descripcion;

    private Double precio;

    private Integer stock;

    private Boolean activo;

    private List<ProductoImagenDTO> imagenes;

    public ProductoResponseDTO() {
        this.imagenes = new ArrayList<>();
    }

    public ProductoResponseDTO(
            Long id,
            String nombre,
            String descripcion,
            Double precio,
            Integer stock,
            Boolean activo,
            List<ProductoImagenDTO> imagenes) {

        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.activo = activo;
        this.imagenes = imagenes != null ? imagenes : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<ProductoImagenDTO> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ProductoImagenDTO> imagenes) {
        this.imagenes = imagenes;
    }
}

