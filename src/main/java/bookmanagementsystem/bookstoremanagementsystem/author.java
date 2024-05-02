package bookmanagementsystem.bookstoremanagementsystem;

public class author {
    String authorID;
    String authorName;
    String authorDescription;

    public author(String authorID, String authorName, String authorDescription) {
        this.authorID = authorID;
        this.authorName = authorName;
        this.authorDescription = authorDescription;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorDescription() {
        return authorDescription;
    }

    public void setAuthorDescription(String authorDescription) {
        this.authorDescription = authorDescription;
    }


}
