package model;

public class Payment {

    private int id;
    private int saleId;    
    private String method;  
    private double paidAmount;

    public Payment() {}

    public Payment(int saleId, String method, double paidAmount) {
        this.saleId = saleId;
        this.method = method;
        this.paidAmount = paidAmount;
    }

    public int getId() {
        return id;
    }

    public int getSaleId() {
        return saleId;
    }

    public String getMethod() {
        return method;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
}
