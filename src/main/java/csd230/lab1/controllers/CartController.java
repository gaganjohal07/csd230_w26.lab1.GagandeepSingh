
package csd230.lab1.controllers;

import csd230.lab1.entities.CartEntity;
import csd230.lab1.entities.OrderEntity;
import csd230.lab1.entities.ProductEntity;
import csd230.lab1.entities.PublicationEntity;
import csd230.lab1.entities.UserEntity;

import csd230.lab1.repositories.CartEntityRepository;
import csd230.lab1.repositories.OrderEntityRepository;
import csd230.lab1.repositories.ProductEntityRepository;
import csd230.lab1.repositories.UserEntityRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private final CartEntityRepository cartRepository;
    private final ProductEntityRepository productRepository;
    private final OrderEntityRepository orderRepository;
    private final UserEntityRepository userRepository;

    public CartController(
            CartEntityRepository cartRepository,
            ProductEntityRepository productRepository,
            OrderEntityRepository orderRepository,
            UserEntityRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // Find or create the logged-in user's cart
    private CartEntity getUserCart(Principal principal) {

        UserEntity user = userRepository.findByUsername(
                principal.getName()
        );

        if (user == null) {
            throw new IllegalArgumentException(
                    "Logged-in user not found"
            );
        }

        CartEntity cart = cartRepository.findByUser(user);

        if (cart == null) {

            cart = new CartEntity();
            cart.setUser(user);

            cart = cartRepository.save(cart);
        }

        return cart;
    }

    // Show logged-in user's cart

    @GetMapping("/cart")
    @Transactional
    public String showCart(Model model, Principal principal) {

        CartEntity cart = getUserCart(principal);

        model.addAttribute("cart", cart);
        model.addAttribute("username", principal.getName());

        return "cartDetails";
    }


    // Add product to logged-in user's cart
    @PostMapping("/cart/add/{productId}")
    @Transactional
    public String addToCart(
            @PathVariable Long productId,
            Principal principal
    ) {

        CartEntity cart = getUserCart(principal);

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found"
                        )
                );

        cart.addProduct(product);

        cartRepository.save(cart);

        return "redirect:/cart";
    }

    // Checkout logged-in user's cart
    @PostMapping("/cart/checkout")
    @Transactional
    public String checkout(Principal principal) {

        CartEntity cart = getUserCart(principal);

        // Do nothing if cart is empty
        if (cart.getProducts().isEmpty()) {
            return "redirect:/cart";
        }

        // Create new order
        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());

        double total = 0.0;

        // Copy products before clearing cart
        List<ProductEntity> products =
                new ArrayList<>(cart.getProducts());

        // Check all stock before changing anything
        for (ProductEntity product : products) {

            if (product instanceof PublicationEntity publication) {

                if (publication.getCopies() <= 0) {
                    return "redirect:/cart";
                }
            }
        }

        // Process every product
        for (ProductEntity product : products) {

            total += product.getPrice();

            // Reduce stock for publications
            if (product instanceof PublicationEntity publication) {

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

        // Clear only this user's cart
        for (ProductEntity product : products) {
            product.getCarts().remove(cart);
        }

        cart.getProducts().clear();
        cartRepository.save(cart);

        // Go to order confirmation
        return "redirect:/orders/" + savedOrder.getId();
    }

    // Show order confirmation
    @GetMapping("/orders/{id}")
    public String showOrder(
            @PathVariable Long id,
            Model model
    ) {

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found"
                        )
                );

        model.addAttribute("order", order);

        return "orderDetails";
    }
}
