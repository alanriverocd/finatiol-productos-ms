package com.finatiol.productos.dto;

public class ProductoImagenDTO {

    private Long id;
    private String url;
    private Integer orden;

    public ProductoImagenDTO() {
    }

    public ProductoImagenDTO(Long id, String url, Integer orden) {
        this.id = id;
        this.url = url;
        this.orden = orden;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
