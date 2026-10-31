
package csd230.lab1.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BOOK")
public class BookEntity extends PublicationEntity {

    private String isbn;
    private String author;

    public BookEntity() {
    }

    public BookEntity(String title, Double price, Integer copies, String author) {
        super(title, price, copies);
        this.author = author;
    }

    public BookEntity(String title, Double price, Integer copies, String author, String isbn) {
        super(title, price, copies);
        this.author = author;
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "isbn='" + isbn + '\'' +
                ", author='" + author + '\'' +
                ", " + super.toString() +
                '}';
    }
}
