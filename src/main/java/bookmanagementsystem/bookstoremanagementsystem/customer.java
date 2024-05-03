package bookmanagementsystem.bookstoremanagementsystem;

public class customer {
    String customerID;
    String CustomerName;
    String customerPhoneNumber;
    int customerPoint;
    boolean customerGender;

    public customer(String customerID, String customerName, String customerPhoneNumber, int customerPoint, boolean customerGender) {
        this.customerID = customerID;
        CustomerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerPoint = customerPoint;
        this.customerGender = customerGender;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public int getCustomerPoint() {
        return customerPoint;
    }

    public void setCustomerPoint(int customerPoint) {
        this.customerPoint = customerPoint;
    }

    public boolean isCustomerGender() {
        return customerGender;
    }

    public void setCustomerGender(boolean customerGender) {
        this.customerGender = customerGender;
    }
}
