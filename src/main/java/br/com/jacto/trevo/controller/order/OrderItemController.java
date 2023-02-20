package br.com.jacto.trevo.controller.order;

import br.com.jacto.trevo.controller.order.dto.OrderItemCreateDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
import br.com.jacto.trevo.service.order.OrderItemService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderItemController {

    @Autowired
    OrderItemService orderItemService;


    @GetMapping
    public List<OrderItemDto> getOrderItem() {
        return orderItemService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> getOrderItemId(@PathVariable UUID id) {
        Optional<OrderItemDto> findOrder = orderItemService.getId(id);
        return findOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
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
    public ResponseEntity<OrderItemDto> updateOrderItem(@RequestBody @Valid OrderItemUpdateForm orderItem) {
        Optional<OrderItemDto> update = orderItemService.update(orderItem);
        return update.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteOrderItem(@PathVariable UUID id) {
        Boolean findOrder = orderItemService.delete(id);
        ;
        return findOrder ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
