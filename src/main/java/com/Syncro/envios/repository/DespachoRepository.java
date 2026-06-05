package com.Syncro.envios.repository;

import com.Syncro.envios.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    Optional<Despacho> findByPedidoId(Long pedidoId);
    List<Despacho> findByEmpresaId(Long empresaId);
    List<Despacho> findByEstado(String estado);
    boolean existsByPedidoId(Long pedidoId);
}
