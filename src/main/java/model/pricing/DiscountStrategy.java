// model/pricing/DiscountStrategy.java
package model.pricing;
public interface DiscountStrategy {
    double apply(double subtotal);
}