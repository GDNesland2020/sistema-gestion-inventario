
package com.inventario.inventario.repository;

import com.inventario.inventario.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByProductoIdProducto(Long idProducto);

    List<Movimiento> findByTipoMovimiento(String tipoMovimiento);

    boolean existsByProductoIdProducto(Long idProducto);
}
