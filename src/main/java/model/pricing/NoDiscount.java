
// model/pricing/NoDiscount.java
package model.pricing;
public class NoDiscount implements DiscountStrategy {
    public double apply(double subtotal) { return 0.0; }
}