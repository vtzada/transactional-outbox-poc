package com.vitortheof.payment_service.controller;

import com.vitortheof.payment_service.controller.dto.AprovarPagamentoRequest;
import com.vitortheof.payment_service.domain.Pagamento;
import com.vitortheof.payment_service.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> aprovar(@RequestBody @Valid AprovarPagamentoRequest request) {
        Pagamento p = pagamentoService.aprovar(request.pedidoId(), request.valor());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", p.getId(), "status", p.getStatus()));
    }
}
