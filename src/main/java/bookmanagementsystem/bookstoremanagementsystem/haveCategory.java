package bookmanagementsystem.bookstoremanagementsystem;

public class haveCategory {
    String bookID;
    String categoryID;
    boolean isMainCategory;

    public haveCategory(String bookID, String categoryID, Boolean isMainCategory) {
        this.bookID = bookID;
        this.categoryID = categoryID;
        this.isMainCategory = isMainCategory;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public boolean isIsMainCategory() {
        return isMainCategory;
    }

    public void setIsMainCategory(boolean isMainCategory) {
        this.isMainCategory = isMainCategory;
    }
}
