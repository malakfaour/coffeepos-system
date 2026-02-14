package model;

import java.time.LocalDateTime;

public class CashRegisterClosing {

    private Integer id;
    private User cashier;
    private LocalDateTime closingDateTime;
    private double expectedTotal;
    private double countedTotal;
    private double difference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }

    public LocalDateTime getClosingDateTime() {
        return closingDateTime;
    }

    public void setClosingDateTime(LocalDateTime closingDateTime) {
        this.closingDateTime = closingDateTime;
    }

    public double getExpectedTotal() {
        return expectedTotal;
    }

    public void setExpectedTotal(double expectedTotal) {
        this.expectedTotal = expectedTotal;
    }

    public double getCountedTotal() {
        return countedTotal;
    }

    public void setCountedTotal(double countedTotal) {
        this.countedTotal = countedTotal;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }
}
