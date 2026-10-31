
package csd230.lab1.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MAGAZINE")
public class MagazineEntity extends PublicationEntity {

    private Integer orderQty;
    private LocalDateTime currentIssue;

    public MagazineEntity() {
    }

    public MagazineEntity(String title, Double price, Integer copies,
                          Integer orderQty, LocalDateTime currentIssue) {
        super(title, price, copies);
        this.orderQty = orderQty;
        this.currentIssue = currentIssue;
    }

    public Integer getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Integer orderQty) {
        this.orderQty = orderQty;
    }

    public LocalDateTime getCurrentIssue() {
        return currentIssue;
    }

    public void setCurrentIssue(LocalDateTime currentIssue) {
        this.currentIssue = currentIssue;
    }

    @Override
    public String toString() {
        return "Mag{issue=" + currentIssue + ", " + super.toString() + "}";
    }
}
