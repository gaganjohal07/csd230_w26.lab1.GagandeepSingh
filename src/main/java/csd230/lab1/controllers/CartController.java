package csd230.lab1.controllers;

import csd230.lab1.entities.CartEntity;
import csd230.lab1.entities.OrderEntity;
import csd230.lab1.entities.ProductEntity;
import csd230.lab1.entities.PublicationEntity;
import csd230.lab1.repositories.CartEntityRepository;
import csd230.lab1.repositories.OrderEntityRepository;
import csd230.lab1.repositories.ProductEntityRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private final CartEntityRepository cartRepository;
    private final ProductEntityRepository productRepository;
    private final OrderEntityRepository orderRepository;

    public CartController(
            CartEntityRepository cartRepository,
            ProductEntityRepository productRepository,
            OrderEntityRepository orderRepository) {

        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/cart")
    public String showCart(Model model) {

        CartEntity cart = cartRepository.findById(1L)
                .orElseGet(() -> cartRepository.save(new CartEntity()));

        model.addAttribute("cart", cart);

        return "cartDetails";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId) {

        CartEntity cart = cartRepository.findById(1L)
                .orElseGet(() -> cartRepository.save(new CartEntity()));

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found"));

        cart.addProduct(product);
        cartRepository.save(cart);

        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout() {

        // Get default cart
        CartEntity cart = cartRepository.findById(1L)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart not found"));

        // Do nothing if cart is empty
        if (cart.getProducts().isEmpty()) {
            return "redirect:/cart";
        }

        // Create new order
        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());

        double total = 0.0;

        // Make a copy so we can safely clear the cart later
        List<ProductEntity> products =
                new ArrayList<>(cart.getProducts());

        // Process every product
        for (ProductEntity product : products) {

            total += product.getPrice();

            // Reduce stock for publications
            if (product instanceof PublicationEntity publication) {

                if (publication.getCopies() <= 0) {
                    return "redirect:/cart";
                }

                publication.setCopies(
                        publication.getCopies() - 1
                );

                productRepository.save(publication);
            }

            // Add product to completed order
            order.addProduct(product);
        }

        // Store final total
        order.setTotalAmount(total);

        // Save completed order
        OrderEntity savedOrder = orderRepository.save(order);

        // Clear shopping cart
        cart.getProducts().clear();
        cartRepository.save(cart);

        // Go to order confirmation
        return "redirect:/orders/" + savedOrder.getId();
    }

    @GetMapping("/orders/{id}")
    public String showOrder(
            @PathVariable Long id,
            Model model) {

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found"));

        model.addAttribute("order", order);

        return "orderDetails";
    }
}