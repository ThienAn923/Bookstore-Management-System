package bookmanagementsystem.bookstoremanagementsystem;

import java.sql.Date;

public class book {
    private String bookID;

    public book(String bookID, int bookRepublish, int bookCost, String bookAuthor, String bookDescription, String bookImage, String bookLocation, String bookCategory, String bookPublisher) {
        this.bookID = bookID;
        this.bookRepublish = bookRepublish;
        this.bookCost = bookCost;
        this.bookAuthor = bookAuthor;
        this.bookDescription = bookDescription;
        this.bookImage = bookImage;
        this.bookLocation = bookLocation;
        this.bookCategory = bookCategory;
        this.bookPublisher = bookPublisher;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public int getBookRepublish() {
        return bookRepublish;
    }

    public void setBookRepublish(int bookRepublish) {
        this.bookRepublish = bookRepublish;
    }

    public int getBookCost() {
        return bookCost;
    }

    public void setBookCost(int bookCost) {
        this.bookCost = bookCost;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookLocation() {
        return bookLocation;
    }

    public void setBookLocation(String bookLocation) {
        this.bookLocation = bookLocation;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }


    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    private int bookRepublish;
    private  int bookCost;
    private String bookAuthor;
    private String bookDescription;
    private  String bookImage;
    private String bookLocation;
    private String bookCategory;
    private String bookPublisher;
}
