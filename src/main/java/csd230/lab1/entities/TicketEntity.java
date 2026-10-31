
package csd230.lab1.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TICKET")
public class TicketEntity extends ProductEntity {

    private String description;

    @Column(name = "ticket_price")
    private Double price;

    public TicketEntity() {
    }

    public TicketEntity(String description, Double price) {
        this.description = description;
        this.price = price;
    }

    @Override
    public void sellItem() {
        System.out.println("Selling Ticket: " + description + " for $" + price);
    }

    @Override
    public double getPrice() {
        return price != null ? price : 0.0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{desc='" + description + "', price=" + price + "}";
    }
}
