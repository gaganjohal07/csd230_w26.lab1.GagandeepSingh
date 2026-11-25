package csd230.lab1.controllers;

import csd230.lab1.entities.CartEntity;
import csd230.lab1.entities.UserEntity;
import csd230.lab1.entities.ProductEntity;
import csd230.lab1.repositories.CartEntityRepository;
import csd230.lab1.repositories.UserEntityRepository;
import csd230.lab1.repositories.ProductEntityRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/rest/carts")
public class CartRestController {

    private final CartEntityRepository repository;
    private final UserEntityRepository userRepository;
    private final ProductEntityRepository productRepository;

    public CartRestController(
            CartEntityRepository repository,
            UserEntityRepository userRepository,
            ProductEntityRepository productRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    private UserEntity currentUser(
            Principal principal, boolean forUpdate) {

        UserEntity user = principal == null ? null :
                forUpdate
                ? userRepository.findForCartUpdate(principal.getName())
                : userRepository.findByUsername(principal.getName());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return user;
    }

    @GetMapping("/mine")
    @Transactional(readOnly = true)
    @Operation(summary = "Get the signed-in user's cart")
    public ResponseEntity<CartEntity> mine(Principal principal) {

        CartEntity cart = repository.findByUser(
                currentUser(principal, false));

        if (cart == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/mine/products/{productId}")
    @Transactional
    @Operation(summary = "Add product to the signed-in user's cart")
    public CartEntity addMine(
            @PathVariable Long productId,
            Principal principal) {

        UserEntity user = currentUser(principal, true);

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        CartEntity cart = repository.findByUser(user);

        if (cart == null) {
            cart = new CartEntity();
            cart.setUser(user);
        }

        cart.addProduct(product);

        return repository.save(cart);
    }

    @DeleteMapping("/mine/products/{productId}")
    @Transactional
    @Operation(summary = "Remove product from the signed-in user's cart")
    public ResponseEntity<CartEntity> removeMine(
            @PathVariable Long productId,
            Principal principal) {

        UserEntity user = currentUser(principal, true);

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        CartEntity cart = repository.findByUser(user);

        if (cart == null) {
            return ResponseEntity.noContent().build();
        }

        cart.getProducts().remove(product);
        product.getCarts().remove(cart);

        return ResponseEntity.ok(cart);
    }

    @GetMapping
    @Operation(summary = "Get all carts")
    public List<CartEntity> getAllCarts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cart by ID")
    public ResponseEntity<CartEntity> getCart(@PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}