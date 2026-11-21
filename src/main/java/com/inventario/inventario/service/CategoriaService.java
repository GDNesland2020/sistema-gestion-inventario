
package com.inventario.inventario.service;

import com.inventario.inventario.model.Categoria;
import com.inventario.inventario.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import com.inventario.inventario.exception.RecursoNoEncontradoException;
import com.inventario.inventario.exception.ReglaNegocioException;
import com.inventario.inventario.repository.ProductoRepository;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(
        CategoriaRepository categoriaRepository,
        ProductoRepository productoRepository) {

    this.categoriaRepository = categoriaRepository;
    this.productoRepository = productoRepository;
}

    // Registrar una categoria
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Consultar todas las categorias
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    // Buscar una categoria por ID
    // Buscar una categoria por ID
public Categoria buscarPorId(Long id) {
    return categoriaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Categoria no encontrada con ID: " + id));
}

    // Actualizar una categoria
    public Categoria actualizar(Long id, Categoria datos) {
        Categoria categoria = buscarPorId(id);

        categoria.setNombre(datos.getNombre());
        categoria.setDescripcion(datos.getDescripcion());

        return categoriaRepository.save(categoria);
    }

    // Eliminar una categoria
public void eliminar(Long id) {

    Categoria categoria = buscarPorId(id);

    if (productoRepository.existsByCategoriaIdCategoria(id)) {
        throw new ReglaNegocioException(
                "No se puede eliminar la categoría porque tiene productos asociados"
        );
    }

    categoriaRepository.delete(categoria);
}
}
