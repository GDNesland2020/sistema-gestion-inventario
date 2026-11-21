
package com.inventario.inventario.controller;

import com.inventario.inventario.dto.MovimientoDTO;
import com.inventario.inventario.model.Movimiento;
import com.inventario.inventario.service.MovimientoService;
import com.inventario.inventario.dto.MovimientoRespuestaDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@Tag(name = "Movimientos", description = "Registro y consulta de entradas y salidas de inventario")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    // Registrar entrada de productos
   @PostMapping("/entrada")
   @Operation(summary = "Registrar entrada de productos")
public ResponseEntity<MovimientoRespuestaDTO> registrarEntrada(
        @Valid @RequestBody MovimientoDTO datos) {

    Movimiento movimiento = movimientoService.registrarEntrada(
            datos.getIdProducto(),
            datos.getCantidad(),
            datos.getObservacion()
    );

    MovimientoRespuestaDTO respuesta = convertirARespuestaDTO(movimiento);

    return ResponseEntity.status(201).body(respuesta);
}

    private MovimientoRespuestaDTO convertirARespuestaDTO(Movimiento movimiento) {
    return new MovimientoRespuestaDTO(
            movimiento.getIdMovimiento(),
            movimiento.getTipoMovimiento(),
            movimiento.getCantidad(),
            movimiento.getFecha(),
            movimiento.getObservacion(),
            movimiento.getProducto().getIdProducto(),
            movimiento.getProducto().getSku(),
            movimiento.getProducto().getNombre()
    );
}

    // Registrar salida de productos
    @PostMapping("/salida")
    @Operation(summary = "Registrar salida de productos")
public ResponseEntity<MovimientoRespuestaDTO> registrarSalida(
        @Valid @RequestBody MovimientoDTO datos) {

    Movimiento movimiento = movimientoService.registrarSalida(
            datos.getIdProducto(),
            datos.getCantidad(),
            datos.getObservacion()
    );

    MovimientoRespuestaDTO respuesta = convertirARespuestaDTO(movimiento);

    return ResponseEntity.status(201).body(respuesta);
}

    // Consultar todos los movimientos
    @GetMapping
    @Operation(summary = "Consultar todos los movimientos")
public List<MovimientoRespuestaDTO> listar() {
    return movimientoService.listar().stream()
            .map(this::convertirARespuestaDTO)
            .toList();
}

    // Consultar movimientos de un producto
    @GetMapping("/producto/{idProducto}")
    @Operation(summary = "Consultar movimientos de un producto")
public List<MovimientoRespuestaDTO> listarPorProducto(
        @PathVariable Long idProducto) {

    return movimientoService.listarPorProducto(idProducto).stream()
            .map(this::convertirARespuestaDTO)
            .toList();
}
}
