package bookmanagementsystem.bookstoremanagementsystem;

import java.sql.Date;

public class pricehistory {
    Date dateOfChange;
    String bookID;
    String bookPrice;

    public pricehistory(Date dateOfChange, String bookID, String bookPrice) {
        this.dateOfChange = dateOfChange;
        this.bookID = bookID;
        this.bookPrice = bookPrice;
    }

    public Date getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(Date dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }
}
