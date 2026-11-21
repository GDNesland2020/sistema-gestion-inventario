
package com.inventario.inventario.service;

import com.inventario.inventario.model.Producto;
import com.inventario.inventario.model.Categoria;
import com.inventario.inventario.repository.ProductoRepository;
import com.inventario.inventario.repository.CategoriaRepository;
import com.inventario.inventario.repository.MovimientoRepository;
import com.inventario.inventario.exception.ReglaNegocioException;
import com.inventario.inventario.exception.RecursoNoEncontradoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void guardarNoDebePermitirSkuDuplicado() {

        Producto producto = new Producto();
        producto.setSku("TEC001");

        when(productoRepository.existsBySku("TEC001"))
                .thenReturn(true);

        assertThrows(
                ReglaNegocioException.class,
                () -> productoService.guardar(producto)
        );

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void guardarNoDebePermitirCategoriaInexistente() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(99L);

        Producto producto = new Producto();
        producto.setSku("TEC999");
        producto.setCategoria(categoria);

        when(productoRepository.existsBySku("TEC999"))
                .thenReturn(false);

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> productoService.guardar(producto)
        );

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void eliminarNoDebePermitirProductoConMovimientos() {

        Producto producto = new Producto();
        producto.setIdProducto(1L);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(movimientoRepository.existsByProductoIdProducto(1L))
                .thenReturn(true);

        assertThrows(
                ReglaNegocioException.class,
                () -> productoService.eliminar(1L)
        );

        verify(productoRepository, never()).delete(any(Producto.class));
    }
}

