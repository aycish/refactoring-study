package me.jounhee.refactoring._08_shotgun_surgery._27_move_field;

import java.time.LocalDate;

public class CustomerContract {

    private LocalDate startDate;
    private double discountRate;
    public CustomerContract(LocalDate startDate, double discountRate) {
        this.discountRate = discountRate;
        this.startDate = startDate;
    }

    double getDiscountRate() {
        return this.discountRate;
    }

    void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
