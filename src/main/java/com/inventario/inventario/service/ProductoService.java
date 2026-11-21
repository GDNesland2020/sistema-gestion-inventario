
package com.inventario.inventario.service;

import com.inventario.inventario.model.Producto;
import com.inventario.inventario.model.Categoria;
import com.inventario.inventario.repository.ProductoRepository;
import com.inventario.inventario.repository.CategoriaRepository;
import com.inventario.inventario.repository.MovimientoRepository;
import com.inventario.inventario.exception.RecursoNoEncontradoException;
import com.inventario.inventario.exception.ReglaNegocioException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoRepository movimientoRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            MovimientoRepository movimientoRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // Registrar un producto
    public Producto guardar(Producto producto) {

        if (productoRepository.existsBySku(producto.getSku())) {
            throw new ReglaNegocioException(
                    "El SKU ya está registrado"
            );
        }

        Categoria categoria = categoriaRepository.findById(
                producto.getCategoria().getIdCategoria()
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "La categoría no existe"
                )
        );

        producto.setCategoria(categoria);

        return productoRepository.save(producto);
    }

    // Consultar todos los productos
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public List<Producto> listarProductosBajoStock() {
    return productoRepository.findProductosBajoStock();
}

public List<Producto> listarProductosAgotados() {
    return productoRepository.findProductosAgotados();
}

    // Buscar producto por ID
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Producto no encontrado con ID: " + id
                        )
                );
    }

    // Actualizar producto
    public Producto actualizar(Long id, Producto datos) {

        Producto producto = buscarPorId(id);

        if (!producto.getSku().equals(datos.getSku())
                && productoRepository.existsBySku(datos.getSku())) {
            throw new ReglaNegocioException(
                    "El SKU ya está registrado"
            );
        }

        Categoria categoria = categoriaRepository.findById(
                datos.getCategoria().getIdCategoria()
        ).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        "La categoría no existe"
                )
        );

        producto.setSku(datos.getSku());
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setStockMinimo(datos.getStockMinimo());
        producto.setCategoria(categoria);

        return productoRepository.save(producto);
    }

    // Eliminar producto
    public void eliminar(Long id) {

        Producto producto = buscarPorId(id);

        if (movimientoRepository.existsByProductoIdProducto(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el producto porque tiene movimientos registrados"
            );
        }

        productoRepository.delete(producto);
    }
}
