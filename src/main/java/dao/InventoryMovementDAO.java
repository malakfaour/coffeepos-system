package dao;

import java.util.List;
import model.InventoryMovement;

public interface InventoryMovementDAO {
    void record(InventoryMovement movement) throws Exception;
    List<InventoryMovement> lowStock(int threshold) throws Exception;
}
