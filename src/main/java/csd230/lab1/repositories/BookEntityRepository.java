package csd230.lab1.repositories;

import csd230.lab1.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookEntityRepository extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findByIsbn(String isbn);

    List<BookEntity> findByTitle(String title);

    List<BookEntity> findByTitleLike(String title);

    @Query("SELECT b FROM BookEntity b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    List<BookEntity> findBooksByPriceRange(
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice
    );
}