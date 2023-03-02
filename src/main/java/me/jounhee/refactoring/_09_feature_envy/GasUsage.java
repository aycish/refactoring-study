package me.jounhee.refactoring._09_feature_envy;

public class GasUsage {

    private double amount;

    private double pricePerUnit;

    public GasUsage(double amount, double pricePerUnit) {
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
    }

    public double getAmount() {
        return amount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    double getGasUsagePrice(Bill bill) {
        return amount * pricePerUnit;
    }
}
