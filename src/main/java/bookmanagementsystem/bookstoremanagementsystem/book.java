package bookmanagementsystem.bookstoremanagementsystem;

import java.sql.Date;

public class book {
    private String bookID;
    private String bookName;
    private int bookRepublish;
    private String bookDescription;
    private String shelveID;
    private String publicsherID;
    private int bookQuantity;

    public book(String bookID, String bookName, int bookRepublish, String bookDescription, String shelveID, String publicsherID, int bookQuantity) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.bookRepublish = bookRepublish;
        this.bookDescription = bookDescription;
        this.shelveID = shelveID;
        this.publicsherID = publicsherID;
        this.bookQuantity = bookQuantity;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookRepublish() {
        return bookRepublish;
    }

    public void setBookRepublish(int bookRepublish) {
        this.bookRepublish = bookRepublish;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public String getShelveID() {
        return shelveID;
    }

    public void setShelveID(String shelveID) {
        this.shelveID = shelveID;
    }

    public String getPublicsherID() {
        return publicsherID;
    }

    public void setPublicsherID(String publicsherID) {
        this.publicsherID = publicsherID;
    }

    public int getBookQuantity() {
        return bookQuantity;
    }

    public void setBookQuantity(int bookQuantity) {
        this.bookQuantity = bookQuantity;
    }
}
