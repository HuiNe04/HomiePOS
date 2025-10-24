package com.example.homie;

public class InvoiceDetailItem {
    public String idProduct;
    public int quantity;
    public double price;
    public double subtotal;

    public InvoiceDetailItem(String idProduct, int quantity, double price, double subtotal) {
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }
}
