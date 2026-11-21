
package com.inventario.inventario.controller;

import com.inventario.inventario.dto.ProductoDTO;
import com.inventario.inventario.dto.ProductoRespuestaDTO;
import com.inventario.inventario.model.Producto;
import com.inventario.inventario.model.Categoria;
import com.inventario.inventario.service.ProductoService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import com.inventario.inventario.dto.ProductoRespuestaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión de productos e inventario")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Convertir DTO a entidad
    private Producto convertirAEntidad(ProductoDTO datos) {

        Producto producto = new Producto();

        producto.setSku(datos.getSku());
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setStockMinimo(datos.getStockMinimo());

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(datos.getIdCategoria());

        producto.setCategoria(categoria);

        return producto;
    }

    // Convertir entidad a DTO de respuesta
private ProductoRespuestaDTO convertirARespuestaDTO(Producto producto) {

    return new ProductoRespuestaDTO(
            producto.getIdProducto(),
            producto.getSku(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getStockMinimo(),
            producto.getCategoria().getIdCategoria(),
            producto.getCategoria().getNombre()
    );
}

    // Registrar un producto
  @PostMapping
  @Operation(summary = "Registrar un producto")
public ResponseEntity<ProductoRespuestaDTO> guardar(
        @Valid @RequestBody ProductoDTO datos) {

    Producto producto = convertirAEntidad(datos);

    Producto productoGuardado = productoService.guardar(producto);

    return ResponseEntity.status(201)
            .body(convertirARespuestaDTO(productoGuardado));
}

    // Consultar todos los productos
   @GetMapping
   @Operation(summary = "Listar todos los productos")
public List<ProductoRespuestaDTO> listar() {
    return productoService.listar().stream()
            .map(this::convertirARespuestaDTO)
            .toList();
}

@GetMapping("/bajo-stock")
@Operation(summary = "Consultar productos con stock bajo")
public List<ProductoRespuestaDTO> listarProductosBajoStock() {
    return productoService.listarProductosBajoStock().stream()
            .map(this::convertirARespuestaDTO)
            .toList();
}

@GetMapping("/agotados")
@Operation(summary = "Consultar productos agotados")
public List<ProductoRespuestaDTO> listarProductosAgotados() {
    return productoService.listarProductosAgotados().stream()
            .map(this::convertirARespuestaDTO)
            .toList();
}

    // Buscar producto por ID
   @GetMapping("/{id}")
   @Operation(summary = "Buscar producto por ID")
public ProductoRespuestaDTO buscarPorId(@PathVariable Long id) {
    return convertirARespuestaDTO(productoService.buscarPorId(id));
}

    // Actualizar producto
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
public ProductoRespuestaDTO actualizar(
        @PathVariable Long id,
        @Valid @RequestBody ProductoDTO datos) {

    Producto producto = convertirAEntidad(datos);

    Producto productoActualizado = productoService.actualizar(id, producto);

    return convertirARespuestaDTO(productoActualizado);
}

    // Eliminar producto
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
