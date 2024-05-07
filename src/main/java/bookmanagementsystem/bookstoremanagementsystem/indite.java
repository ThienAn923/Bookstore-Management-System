package bookmanagementsystem.bookstoremanagementsystem;

public class indite {
    String authorID;
    String bookID;
    boolean isMainAuthor;

    public indite(String authorID, String bookID, boolean isMainAuthor) {
        this.authorID = authorID;
        this.bookID = bookID;
        this.isMainAuthor = isMainAuthor;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public boolean isMainAuthor() {
        return isMainAuthor;
    }

    public void setMainAuthor(boolean mainAuthor) {
        isMainAuthor = mainAuthor;
    }
}
