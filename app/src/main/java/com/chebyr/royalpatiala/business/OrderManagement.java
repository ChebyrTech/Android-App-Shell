package com.chebyr.royalpatiala.business;

/**
 * Order Management business logic
 */

public interface OrderManagement
{
    int CustomerOrder = 1;
    int StockOrder = 2;

    int createOrder(String key, int orderType, int quantity); // return OrderID
    void modifyOrder(int orderID, int orderType, int quantity);
    void cancelOrder(int orderID);
}
