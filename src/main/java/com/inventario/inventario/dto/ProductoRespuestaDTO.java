
package com.inventario.inventario.dto;

import java.math.BigDecimal;

public class ProductoRespuestaDTO {

    private Long idProducto;
    private String sku;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private Long idCategoria;
    private String nombreCategoria;

    public ProductoRespuestaDTO() {
    }

    public ProductoRespuestaDTO(
            Long idProducto,
            String sku,
            String nombre,
            String descripcion,
            BigDecimal precio,
            Integer stock,
            Integer stockMinimo,
            Long idCategoria,
            String nombreCategoria) {

        this.idProducto = idProducto;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public String getSku() {
        return sku;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }
}
