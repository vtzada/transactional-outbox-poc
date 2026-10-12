package com.vitortheof.orderservice.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EventoProcessadoRepository extends JpaRepository<EventoProcessado, UUID> {}