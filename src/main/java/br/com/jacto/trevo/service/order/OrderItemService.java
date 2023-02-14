package br.com.jacto.trevo.service.order;

import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ClientRepository;
import br.com.jacto.trevo.repository.OrderItemRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ProductRepository productRepository;


    public List<OrderItemDto> getAll() {
        return orderItemRepository.findAll().stream().map(OrderItemDto::new).toList();
    }

    public Optional<OrderItemDto> getId(UUID id) {
        return orderItemRepository.findById(id).map(OrderItemDto::new);
    }

    public Optional<OrderItem> create(OrderItemForm orderItem) {
        Optional<Client> findClient = clientRepository.findById(orderItem.getClientId());
        Optional<Product> findProduct = productRepository.findByProductName(orderItem.getProductName());

        if(findClient.isEmpty() || findProduct.isEmpty()){
            return Optional.empty();
        }

        OrderItem order = new OrderItem(orderItem.getQuantity(), findClient.get(), findProduct.get());
        order.setClient(findClient.get());
        order.setProduct(findProduct.get());
        return Optional.of(orderItemRepository.save(order));
    }

    public void delete(UUID id) {
        orderItemRepository.deleteById(id);
    }

    public Optional<OrderItem> update(OrderItemUpdateForm orderItem) {

        Optional<OrderItem> findOrder = orderItemRepository.findById(orderItem.getOrderItemId());
        clientRepository.findById(orderItem.getClientId()).orElseThrow();

        if (findOrder.isEmpty()) {
            return findOrder;
        }

        if (orderItem.getProductName() != null && !orderItem.getProductName().trim().isEmpty()) {
            Optional<Product> findProduct = Optional.of(productRepository.findByProductName(orderItem.getProductName()).orElseThrow());
            findOrder.get().setProduct(findProduct.get());
        }

        if (orderItem.getQuantity() != null) {
            findOrder.get().setQuantity(orderItem.getQuantity());
        }

        return Optional.of(orderItemRepository.save(findOrder.get()));
    }


}
