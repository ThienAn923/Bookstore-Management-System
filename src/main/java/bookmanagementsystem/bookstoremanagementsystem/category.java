package bookmanagementsystem.bookstoremanagementsystem;

public class category {
    String categoryID;
    String CategoryName;

    public category(String categoryID, String categoryName) {
        this.categoryID = categoryID;
        CategoryName = categoryName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}
