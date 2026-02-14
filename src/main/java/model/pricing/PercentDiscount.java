// model/pricing/PercentDiscount.java
package model.pricing;
public class PercentDiscount implements DiscountStrategy {
    private final double percent; // e.g., 0.10 for 10%
    public PercentDiscount(double percent) { this.percent = percent; }
    public double apply(double subtotal) { return subtotal * percent; }
}