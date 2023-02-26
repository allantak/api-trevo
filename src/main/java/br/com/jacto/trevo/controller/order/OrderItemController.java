package br.com.jacto.trevo.controller.order;

import br.com.jacto.trevo.controller.order.dto.OrderItemCreateDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
import br.com.jacto.trevo.service.order.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Tag(name = "Pedido", description = "Gerenciamento de pedidos")
public class OrderItemController {

    @Autowired
    OrderItemService orderItemService;


    @GetMapping
    @Operation(summary = "Lista todos os pedidos registrado")
    public List<OrderItemDto> getOrderItem() {
        return orderItemService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Mostra o id do pedido")
    public ResponseEntity<OrderItemDto> getOrderItemId(@PathVariable UUID id) {
        Optional<OrderItemDto> findOrder = orderItemService.getId(id);
        return findOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Registro de pedidos", description = "Para registrar um pedido é obrigatorio os id de produto e cliente")
    public ResponseEntity<OrderItemCreateDto> createOrderItem(@RequestBody @Valid OrderItemForm orderItem, UriComponentsBuilder uriBuilder) {
        Optional<OrderItemCreateDto> order = orderItemService.create(orderItem);
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        URI uri = uriBuilder.path("/orders/{id}").buildAndExpand(order.get().getOrderItemId()).toUri();
        return ResponseEntity.created(uri).body(order.get());
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualização do pedido")
    public ResponseEntity<OrderItemDto> updateOrderItem(@RequestBody @Valid OrderItemUpdateForm orderItem) {
        Optional<OrderItemDto> update = orderItemService.update(orderItem);
        return update.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Delete de pedido")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable UUID id) {
        Boolean findOrder = orderItemService.delete(id);
        return findOrder ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
