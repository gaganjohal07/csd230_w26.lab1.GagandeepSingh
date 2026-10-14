
package csd230.lab1;

import com.github.javafaker.Faker;

import csd230.lab1.entities.BookEntity;
import csd230.lab1.entities.CartEntity;
import csd230.lab1.entities.MagazineEntity;
import csd230.lab1.entities.ProductEntity;
import csd230.lab1.entities.UserEntity;

import csd230.lab1.repositories.BookEntityRepository;
import csd230.lab1.repositories.CartEntityRepository;
import csd230.lab1.repositories.ProductEntityRepository;
import csd230.lab1.repositories.UserEntityRepository;

import jakarta.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final ProductEntityRepository productRepository;
	private final BookEntityRepository bookRepository;
	private final CartEntityRepository cartRepository;

	private final UserEntityRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public Application(
			ProductEntityRepository productRepository,
			BookEntityRepository bookRepository,
			CartEntityRepository cartRepository,
			UserEntityRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		this.productRepository = productRepository;
		this.bookRepository = bookRepository;
		this.cartRepository = cartRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) {

		Faker faker = new Faker();

		// ===================================
		// CREATE
		// ===================================

		System.out.println("\n===== CREATE =====");

		BookEntity book1 = new BookEntity(
				faker.book().title(),
				29.99,
				10,
				faker.book().author(),
				"ISBN-001"
		);

		BookEntity book2 = new BookEntity(
				faker.book().title(),
				39.99,
				5,
				faker.book().author(),
				"ISBN-002"
		);

		MagazineEntity magazine = new MagazineEntity(
				"Technology Magazine",
				12.99,
				20,
				50,
				LocalDateTime.now()
		);

		productRepository.save(book1);
		productRepository.save(book2);
		productRepository.save(magazine);

		CartEntity cart1 = new CartEntity();
		CartEntity cart2 = new CartEntity();

		cart1.addProduct(book1);
		cart1.addProduct(magazine);

		cart2.addProduct(book1);
		cart2.addProduct(book2);

		cartRepository.save(cart1);
		cartRepository.save(cart2);

		System.out.println("Products and carts created.");

		// ===================================
		// READ
		// ===================================

		System.out.println("\n===== READ =====");

		List<ProductEntity> allProducts =
				productRepository.findAll();

		for (ProductEntity product : allProducts) {
			System.out.println(product);
		}

		List<CartEntity> allCarts =
				cartRepository.findAll();

		for (CartEntity cart : allCarts) {

			System.out.println("Cart ID: " + cart.getId());

			for (ProductEntity product : cart.getProducts()) {
				System.out.println("  Product: " + product);
			}
		}

		// ===================================
		// DERIVED QUERY
		// ===================================

		System.out.println("\n===== DERIVED QUERY =====");

		List<BookEntity> booksByIsbn =
				bookRepository.findByIsbn("ISBN-001");

		for (BookEntity book : booksByIsbn) {
			System.out.println("Found by ISBN: " + book);
		}

		// ===================================
		// LIKE QUERY
		// ===================================

		System.out.println("\n===== LIKE QUERY =====");

		List<BookEntity> booksByTitle =
				bookRepository.findByTitleLike("%a%");

		for (BookEntity book : booksByTitle) {
			System.out.println("Found by title LIKE: " + book);
		}

		// ===================================
		// CUSTOM PRICE QUERY
		// ===================================

		System.out.println("\n===== CUSTOM PRICE QUERY =====");

		List<BookEntity> booksByPrice =
				bookRepository.findBooksByPriceRange(20.00, 40.00);

		for (BookEntity book : booksByPrice) {
			System.out.println("Book in price range: " + book);
		}

		// ===================================
		// UPDATE
		// ===================================

		System.out.println("\n===== UPDATE =====");

		Optional<BookEntity> optionalBook =
				bookRepository.findById(book1.getId());

		if (optionalBook.isPresent()) {

			BookEntity bookToUpdate = optionalBook.get();

			bookToUpdate.setPrice(24.99);

			bookRepository.save(bookToUpdate);

			System.out.println(
					"Updated book: " + bookToUpdate
			);
		}

		// ===================================
		// DELETE
		// ===================================

		System.out.println("\n===== DELETE =====");

		BookEntity temporaryBook = new BookEntity(
				"Temporary Book",
				9.99,
				1,
				"Temporary Author",
				"ISBN-DELETE"
		);

		bookRepository.save(temporaryBook);

		System.out.println(
				"Temporary book created with ID: "
						+ temporaryBook.getId()
		);

		bookRepository.delete(temporaryBook);

		System.out.println("Temporary book deleted.");

		// ===================================
		// CREATE USERS - LECTURE 2.6
		// ===================================

		System.out.println("\n===== CREATE USERS =====");

		// Create ADMIN account if it does not exist
		if (userRepository.findByUsername("admin") == null) {

			UserEntity admin = new UserEntity(
					"admin",
					passwordEncoder.encode("admin"),
					"ADMIN"
			);

			userRepository.save(admin);

			System.out.println("Admin user created.");
		}

		// Create USER account if it does not exist
		if (userRepository.findByUsername("user") == null) {

			UserEntity user = new UserEntity(
					"user",
					passwordEncoder.encode("user"),
					"USER"
			);

			userRepository.save(user);

			System.out.println("Regular user created.");
		}

		System.out.println(
				"Test accounts: admin/admin and user/user"
		);

		System.out.println("\n===== DEMO COMPLETE =====");
	}
}
