package me.jounhee.refactoring._08_shotgun_surgery._27_move_field;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Customer {

    private String name;
    private CustomerContract contract;

    public Customer(String name, double discountRate) {
        this.name = name;
        this.contract = new CustomerContract(dateToday(), discountRate);
    }

    public double getDiscountRate() {
        return this.contract.getDiscountRate();
    }

    public void setDiscountRate(double discountRate) {
        this.contract.setDiscountRate(discountRate);
    }

    public double applyDiscount(double amount) {
        BigDecimal value = BigDecimal.valueOf(amount);
        return value.subtract(value.multiply(BigDecimal.valueOf(getDiscountRate()))).doubleValue();
    }

    private LocalDate dateToday() {
        return LocalDate.now();
    }
}
