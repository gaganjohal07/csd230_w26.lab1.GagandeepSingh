
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

		// Create sample products only if database is empty
		if (productRepository.count() == 0) {

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

			System.out.println("Sample products created.");

		} else {
			System.out.println(
					"Products already exist. Skipping sample products."
			);
		}

		// No shared demo carts are created.
		// CartController creates a separate cart for each user.

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

			if (cart.getUser() != null) {
				System.out.println(
						"Owner: " + cart.getUser().getUsername()
				);
			} else {
				System.out.println("Owner: No user assigned");
			}

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

		// Update sample book only when it was just created
		// This avoids changing prices after every restart.
		if (bookRepository.findByIsbn("ISBN-001").size() > 0) {

			Optional<BookEntity> optionalBook =
					bookRepository.findById(
							bookRepository.findByIsbn("ISBN-001")
									.get(0)
									.getId()
					);

			if (optionalBook.isPresent()) {

				BookEntity bookToUpdate = optionalBook.get();

				// Demonstrate update without repeatedly changing data
				if (bookToUpdate.getPrice() == 29.99) {

					bookToUpdate.setPrice(24.99);
					bookRepository.save(bookToUpdate);

					System.out.println(
							"Updated book: " + bookToUpdate
					);
				}
			}
		}

		// ===================================
		// DELETE
		// ===================================

		System.out.println("\n===== DELETE =====");

		// Avoid creating duplicate temporary books
		List<BookEntity> temporaryBooks =
				bookRepository.findByIsbn("ISBN-DELETE");

		for (BookEntity temporaryBook : temporaryBooks) {
			bookRepository.delete(temporaryBook);
		}

		System.out.println(
				"Temporary demo books cleaned up if present."
		);

		// ===================================
		// CREATE USERS - LECTURE 2.6
		// ===================================

		System.out.println("\n===== CREATE USERS =====");

		// Create ADMIN account if missing
		if (userRepository.findByUsername("admin") == null) {

			UserEntity admin = new UserEntity(
					"admin",
					passwordEncoder.encode("admin"),
					"ADMIN"
			);

			userRepository.save(admin);

			System.out.println("Admin user created.");
		}

		// Create regular USER account if missing
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
