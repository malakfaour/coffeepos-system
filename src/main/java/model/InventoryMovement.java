package model;

public class InventoryMovement {

    private int id;
    private int productId;
    private int changeQty;    
    private String reason;     
    private Integer referenceId; 
    private String createdAt;


    public InventoryMovement(int productId, int changeQty, String reason, Integer referenceId) {
        this.productId = productId;
        this.changeQty = changeQty;
        this.reason = reason;
        this.referenceId = referenceId;
    }


    public InventoryMovement(int id, int productId, int changeQty, String reason, Integer referenceId, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.changeQty = changeQty;
        this.reason = reason;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    public InventoryMovement() {}

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getChangeQty() {
        return changeQty;
    }

    public String getReason() {
        return reason;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
