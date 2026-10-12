package com.vitortheof.payment_service.repository;

import com.vitortheof.payment_service.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
}
