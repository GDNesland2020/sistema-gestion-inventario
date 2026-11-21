
package com.inventario.inventario.service;

import com.inventario.inventario.model.Movimiento;
import com.inventario.inventario.model.Producto;
import com.inventario.inventario.repository.MovimientoRepository;
import com.inventario.inventario.repository.ProductoRepository;
import com.inventario.inventario.exception.RecursoNoEncontradoException;
import com.inventario.inventario.exception.ReglaNegocioException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public MovimientoService(
            MovimientoRepository movimientoRepository,
            ProductoRepository productoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    // Registrar una entrada
    @Transactional
    public Movimiento registrarEntrada(
            Long idProducto, Integer cantidad, String observacion) {

        Producto producto = buscarProducto(idProducto);

        if (cantidad == null || cantidad <= 0) {
            throw new ReglaNegocioException(
                    "La cantidad debe ser mayor que cero");
        }

        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(observacion);
        movimiento.setProducto(producto);

        return movimientoRepository.save(movimiento);
    }

    // Registrar una salida
    @Transactional
    public Movimiento registrarSalida(
            Long idProducto, Integer cantidad, String observacion) {

        Producto producto = buscarProducto(idProducto);

        if (cantidad == null || cantidad <= 0) {
            throw new ReglaNegocioException(
                    "La cantidad debe ser mayor que cero");
        }

        if (cantidad > producto.getStock()) {
            throw new ReglaNegocioException(
                    "No hay suficiente stock disponible");
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(observacion);
        movimiento.setProducto(producto);

        return movimientoRepository.save(movimiento);
    }

    // Consultar todos los movimientos
    public List<Movimiento> listar() {
        return movimientoRepository.findAll();
    }

    // Consultar movimientos de un producto
    public List<Movimiento> listarPorProducto(Long idProducto) {
        buscarProducto(idProducto);
        return movimientoRepository.findByProductoIdProducto(idProducto);
    }

    // Buscar producto
    private Producto buscarProducto(Long idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + idProducto));
    }
}
