package com.vitortheof.orderservice.controller;

import com.vitortheof.orderservice.controller.dto.CriarPedidoRequest;
import com.vitortheof.orderservice.domain.Pedido;
import com.vitortheof.orderservice.repository.PedidoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository repository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody @Valid CriarPedidoRequest request) {
        Pedido pedido = repository.save(Pedido.novo(request.valor()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", pedido.getId(), "status", pedido.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscar(@PathVariable UUID id) {
        return repository.findById(id)
                .map(p -> ResponseEntity.ok(Map.<String, Object>of(
                        "id", p.getId(),
                        "valor", p.getValor(),
                        "status", p.getStatus(),
                        "atualizadoEm", p.getAtualizadoEm())))
                .orElse(ResponseEntity.notFound().build());
    }
}

