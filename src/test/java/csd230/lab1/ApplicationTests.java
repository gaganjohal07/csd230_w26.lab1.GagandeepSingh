package csd230.lab1;

import csd230.lab1.entities.BookEntity;
import csd230.lab1.entities.CartEntity;
import csd230.lab1.repositories.BookEntityRepository;
import csd230.lab1.repositories.CartEntityRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
		replace = AutoConfigureTestDatabase.Replace.NONE
)
class ApplicationTests {

	@Autowired
	private BookEntityRepository bookRepository;

	@Autowired
	private CartEntityRepository cartRepository;

	@Test
	void crudTest() {

		BookEntity book = new BookEntity(
				"Java Testing",
				25.99,
				10,
				"Test Author",
				"CRUD-001"
		);

		BookEntity savedBook = bookRepository.save(book);

		assertNotNull(savedBook.getId());

		Optional<BookEntity> foundBook =
				bookRepository.findById(savedBook.getId());

		assertTrue(foundBook.isPresent());
		assertEquals("Java Testing", foundBook.get().getTitle());

		foundBook.get().setPrice(30.99);
		bookRepository.save(foundBook.get());

		Optional<BookEntity> updatedBook =
				bookRepository.findById(savedBook.getId());

		assertTrue(updatedBook.isPresent());
		assertEquals(30.99, updatedBook.get().getPrice(), 0.001);

		bookRepository.delete(updatedBook.get());

		Optional<BookEntity> deletedBook =
				bookRepository.findById(savedBook.getId());

		assertTrue(deletedBook.isEmpty());
	}

	@Test
	void derivedQueryTest() {

		BookEntity book = new BookEntity(
				"Spring Data Book",
				35.99,
				5,
				"Spring Author",
				"ISBN-TEST-001"
		);

		bookRepository.save(book);

		List<BookEntity> results =
				bookRepository.findByIsbn("ISBN-TEST-001");

		assertFalse(results.isEmpty());
		assertEquals(
				"ISBN-TEST-001",
				results.get(0).getIsbn()
		);
	}

	@Test
	void cartProductRelationshipPersistenceTest() {

		BookEntity book1 = new BookEntity(
				"Book One",
				20.00,
				10,
				"Author One",
				"CART-001"
		);

		BookEntity book2 = new BookEntity(
				"Book Two",
				30.00,
				5,
				"Author Two",
				"CART-002"
		);

		bookRepository.save(book1);
		bookRepository.save(book2);

		CartEntity cart = new CartEntity();

		cart.addProduct(book1);
		cart.addProduct(book2);

		CartEntity savedCart = cartRepository.save(cart);

		assertNotNull(savedCart.getId());

		Optional<CartEntity> foundCart =
				cartRepository.findById(savedCart.getId());

		assertTrue(foundCart.isPresent());

		CartEntity loadedCart = foundCart.get();

		assertEquals(2, loadedCart.getProducts().size());

		assertTrue(
				loadedCart.getProducts().contains(book1)
		);

		assertTrue(
				loadedCart.getProducts().contains(book2)
		);
	}
}