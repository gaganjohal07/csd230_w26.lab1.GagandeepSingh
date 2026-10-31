
package csd230.lab1.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DISCMAG")
public class DiscMagEntity extends MagazineEntity {

    private Boolean hasDisc;

    public DiscMagEntity() {
    }

    public DiscMagEntity(String title, Double price, Integer copies,
                         Integer orderQty, LocalDateTime currentIssue,
                         Boolean hasDisc) {
        super(title, price, copies, orderQty, currentIssue);
        this.hasDisc = hasDisc;
    }

    public Boolean getHasDisc() {
        return hasDisc;
    }

    public void setHasDisc(Boolean hasDisc) {
        this.hasDisc = hasDisc;
    }

    @Override
    public String toString() {
        return "DiscMag{disc=" + hasDisc + ", " + super.toString() + "}";
    }
}
