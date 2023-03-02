package me.jounhee.refactoring._09_feature_envy;

public class Bill {

    private ElectricityUsage electricityUsage;

    private GasUsage gasUsage;

    public double calculateBill() {
        return electricityUsage.getElecticityUsagePrice() + gasUsage.getGasUsagePrice(this);
    }
}
