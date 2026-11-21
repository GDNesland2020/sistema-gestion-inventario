
package com.inventario.inventario.service;

import com.inventario.inventario.model.Movimiento;
import com.inventario.inventario.model.Producto;
import com.inventario.inventario.repository.MovimientoRepository;
import com.inventario.inventario.repository.ProductoRepository;
import com.inventario.inventario.exception.ReglaNegocioException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    private Producto producto;

    @BeforeEach
    void configurar() {
        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Portátil Lenovo");
        producto.setStock(10);
    }

    @Test
    void registrarEntradaDebeAumentarStock() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Movimiento resultado =
                movimientoService.registrarEntrada(1L, 5, "Compra de equipos");

        assertEquals(15, producto.getStock());
        assertEquals("ENTRADA", resultado.getTipoMovimiento());
        assertEquals(5, resultado.getCantidad());

        verify(productoRepository).save(producto);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void registrarSalidaDebeDisminuirStock() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(movimientoRepository.save(any(Movimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Movimiento resultado =
                movimientoService.registrarSalida(1L, 3, "Venta de producto");

        assertEquals(7, producto.getStock());
        assertEquals("SALIDA", resultado.getTipoMovimiento());
        assertEquals(3, resultado.getCantidad());
    }

    @Test
    void registrarSalidaNoDebePermitirStockInsuficiente() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        assertThrows(
                ReglaNegocioException.class,
                () -> movimientoService.registrarSalida(1L, 20, "Prueba")
        );

        assertEquals(10, producto.getStock());

        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }

    @Test
    void registrarEntradaNoDebeAceptarCantidadCero() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        assertThrows(
                ReglaNegocioException.class,
                () -> movimientoService.registrarEntrada(1L, 0, "Prueba")
        );

        assertEquals(10, producto.getStock());

        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }
}
