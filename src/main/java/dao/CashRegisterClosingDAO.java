package dao;

import model.CashRegisterClosing;

import java.time.LocalDate;
import java.util.List;

public interface CashRegisterClosingDAO {

    void create(CashRegisterClosing closing) throws Exception;

    List<CashRegisterClosing> findByCashierAndDate(int cashierId, LocalDate date) throws Exception;

    List<CashRegisterClosing> findAll() throws Exception;
}
