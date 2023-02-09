package br.com.jacto.trevo.controller.order;


import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemCreateDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.order.OrderItem;
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
    public ResponseEntity<Optional<OrderItemDto>> getOrderItemId(@PathVariable UUID id) {
        Optional<OrderItemDto> findOrder = orderItemService.getId(id);
        if(findOrder.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(findOrder);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<OrderItemCreateDto> createOrderItem(@RequestBody @Valid OrderItemForm orderItem, UriComponentsBuilder uriBuilder) {
        Optional<OrderItem> order = orderItemService.create(orderItem);
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        URI uri = uriBuilder.path("/orders/{id}").buildAndExpand(order.get().getOrderItemId()).toUri();
        return ResponseEntity.created(uri).body(new OrderItemCreateDto(order.get()));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Optional<OrderItemDto>> updateOrderItem(@RequestBody @Valid OrderItemUpdateForm orderItem) {
        Optional<OrderItemDto> update = orderItemService.update(orderItem).map(OrderItemDto::new);
        if (update.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<OrderItemDto> deleteOrderItem(@PathVariable UUID id) {
        Optional<OrderItemDto> findClient = orderItemService.getId(id);
        if (findClient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
