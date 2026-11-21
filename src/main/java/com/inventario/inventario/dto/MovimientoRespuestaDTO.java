package com.inventario.inventario.dto;

import java.time.LocalDateTime;

public class MovimientoRespuestaDTO {

    private Long idMovimiento;
    private String tipoMovimiento;
    private Integer cantidad;
    private LocalDateTime fecha;
    private String observacion;
    private Long idProducto;
    private String skuProducto;
    private String nombreProducto;

    public MovimientoRespuestaDTO() {
    }

    public MovimientoRespuestaDTO(Long idMovimiento, String tipoMovimiento,
            Integer cantidad, LocalDateTime fecha, String observacion,
            Long idProducto, String skuProducto, String nombreProducto) {

        this.idMovimiento = idMovimiento;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.observacion = observacion;
        this.idProducto = idProducto;
        this.skuProducto = skuProducto;
        this.nombreProducto = nombreProducto;
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public String getSkuProducto() {
        return skuProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }
}
