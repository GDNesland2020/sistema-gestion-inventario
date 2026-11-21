
package com.inventario.inventario.service;

import com.inventario.inventario.model.Categoria;
import com.inventario.inventario.repository.CategoriaRepository;
import com.inventario.inventario.repository.ProductoRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void eliminarNoDebePermitirCategoriaConProductos() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(productoRepository.existsByCategoriaIdCategoria(1L))
                .thenReturn(true);

        assertThrows(
                ReglaNegocioException.class,
                () -> categoriaService.eliminar(1L)
        );

        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }

    @Test
    void buscarCategoriaInexistenteDebeLanzarExcepcion() {

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> categoriaService.buscarPorId(99L)
        );
    }
}

