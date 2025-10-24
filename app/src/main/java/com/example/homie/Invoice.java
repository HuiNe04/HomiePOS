package com.example.homie;

public class Invoice {
    public String idInvoice;
    public String invoiceName;
    public String type;
    public String date;
    public double vatPercent;
    public double total;

    public Invoice(String idInvoice, String invoiceName, String type, String date, double vatPercent, double total) {
        this.idInvoice = idInvoice;
        this.invoiceName = invoiceName;
        this.type = type;
        this.date = date;
        this.vatPercent = vatPercent;
        this.total = total;
    }
}
