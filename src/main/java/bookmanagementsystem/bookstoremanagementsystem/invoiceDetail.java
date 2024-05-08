package bookmanagementsystem.bookstoremanagementsystem;

public class invoiceDetail {
    String invoiceID;
    String bookID;
    int number;

    public invoiceDetail(String invoiceID, String bookID, int number) {
        this.invoiceID = invoiceID;
        this.bookID = bookID;
        this.number = number;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
