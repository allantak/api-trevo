package br.com.jacto.trevo.service.order;

import br.com.jacto.trevo.controller.order.dto.OrderItemCreateDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
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
    private AccountRepository accountRepository;
    @Autowired
    private ProductRepository productRepository;


    public List<OrderItemDto> getAll() {
        return orderItemRepository.findAll().stream().map(OrderItemDto::new).toList();
    }

    public Optional<OrderItemDto> getId(UUID id) {
        return orderItemRepository.findById(id).map(OrderItemDto::new);
    }

    public Optional<OrderItemCreateDto> create(OrderItemForm orderItem) {
        Optional<Account> findClient = accountRepository.findById(orderItem.getAccountId());
        Optional<Product> findProduct = productRepository.findByProductName(orderItem.getProductName());

        if (findClient.isEmpty() || findProduct.isEmpty()) {
            return Optional.empty();
        }

        if (orderItem.getQuantity() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que 0");
        }

        OrderItem order = new OrderItem(orderItem.getQuantity(), findClient.get(), findProduct.get());
        order.setAccount(findClient.get());
        order.setProduct(findProduct.get());
        return Optional.of(orderItemRepository.save(order)).map(OrderItemCreateDto::new);
    }

    public Boolean delete(UUID id) {
        Optional<OrderItem> findOrder = orderItemRepository.findById(id);
        if (findOrder.isEmpty()) {
            return false;
        }
        orderItemRepository.deleteById(id);
        return true;
    }

    public Optional<OrderItemDto> update(OrderItemUpdateForm orderItem) {

        Optional<OrderItem> findOrder = orderItemRepository.findById(orderItem.getOrderItemId());
        Optional<Account> findClient = accountRepository.findById(orderItem.getAccountId());

        if (findOrder.isEmpty() || findClient.isEmpty()) {
            return Optional.empty();
        }

        if (orderItem.getQuantity() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que 0");
        }

        if (orderItem.getProductName() != null && !orderItem.getProductName().trim().isEmpty()) {
            Optional<Product> findProduct = productRepository.findByProductName(orderItem.getProductName());
            if (findProduct.isEmpty()) return Optional.empty();
            findOrder.get().setProduct(findProduct.get());
        }

        if (orderItem.getQuantity() != null) {
            findOrder.get().setQuantity(orderItem.getQuantity());
        }

        return Optional.of(orderItemRepository.save(findOrder.get())).map(OrderItemDto::new);
    }


}
