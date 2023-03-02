package me.jounhee.refactoring._07_divergent_change._24_split_phase;

public class PriceOrder {

    public double priceOrder(Product product, int quantity, ShippingMethod shippingMethod) {
        final PriceData priceData = calculatePriceData(product, quantity);
        final double price = applyShipping(priceData, shippingMethod);
        return price;
    }

    private static PriceData calculatePriceData(Product product, int quantity) {
        final double basePrice = product.basePrice() * quantity;
        final double discount = Math.max(quantity - product.discountThreshold(), 0)
                * product.basePrice() * product.discountRate();
        return new PriceData(basePrice, discount, quantity);
    }

    private static double applyShipping(PriceData priceData, ShippingMethod shippingMethod) {
        final double shippingPerCase = (priceData.basePrice() > shippingMethod.discountThreshold()) ?
                shippingMethod.discountedFee() : shippingMethod.feePerCase();
        final double shippingCost = priceData.discount() * shippingPerCase;
        final double price = priceData.basePrice() - priceData.discount() + shippingCost;
        return price;
    }
}
