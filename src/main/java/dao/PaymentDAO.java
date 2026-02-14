package dao;

import model.Payment;

public interface PaymentDAO {
    void save(Payment payment) throws Exception;
}
