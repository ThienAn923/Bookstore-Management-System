package bookmanagementsystem.bookstoremanagementsystem;

import java.sql.Date;

public class invoice {
    String invoiceID;
    Date dateOfCreate;
    int totalMoney;
    String taxNumber;
    int point;
    int moneyReceive;
    int moneyPayBack;
    String staffID;
    String customerID;
    String promotionID;

    public invoice(String invoiceID, Date dateOfCreate, int totalMoney, String taxNumber, int point, int moneyReceive, int moneyPayBack, String staffID, String customerID, String promotionID) {
        this.invoiceID = invoiceID;
        this.dateOfCreate = dateOfCreate;
        this.totalMoney = totalMoney;
        this.taxNumber = taxNumber;
        this.point = point;
        this.moneyReceive = moneyReceive;
        this.moneyPayBack = moneyPayBack;
        this.staffID = staffID;
        this.customerID = customerID;
        this.promotionID = promotionID;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public Date getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(Date dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getMoneyReceive() {
        return moneyReceive;
    }

    public void setMoneyReceive(int moneyReceive) {
        this.moneyReceive = moneyReceive;
    }

    public int getMoneyPayBack() {
        return moneyPayBack;
    }

    public void setMoneyPayBack(int moneyPayBack) {
        this.moneyPayBack = moneyPayBack;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getPromotionID() {
        return promotionID;
    }

    public void setPromotionID(String promotionID) {
        this.promotionID = promotionID;
    }
}
