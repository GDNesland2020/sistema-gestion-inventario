
package com.inventario.inventario.controller;

import com.inventario.inventario.dto.CategoriaDTO;
import com.inventario.inventario.model.Categoria;
import com.inventario.inventario.service.CategoriaService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de categorías de productos")

public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // Registrar una categoría
    @Operation(summary = "Registrar una categoría")
    @PostMapping
    public ResponseEntity<Categoria> guardar(
            @Valid @RequestBody CategoriaDTO datos) {

        Categoria categoria = new Categoria(
                datos.getNombre(),
                datos.getDescripcion()
        );

        return ResponseEntity.status(201)
                .body(categoriaService.guardar(categoria));
    }

    // Consultar todas las categorías
    @Operation(summary = "Listar todas las categorías")
    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listar();
    }

    // Buscar una categoría por ID
    @Operation(summary = "Buscar categoría por ID")
    @GetMapping("/{id}")
    public Categoria buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id);
    }

    // Actualizar una categoría
    @Operation(summary = "Actualizar una categoría")
    @PutMapping("/{id}")
    public Categoria actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO datos) {

        Categoria categoria = new Categoria(
                datos.getNombre(),
                datos.getDescripcion()
        );

        return categoriaService.actualizar(id, categoria);
    }

    // Eliminar una categoría
    @Operation(summary = "Eliminar una categoría")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
