package bookmanagementsystem.bookstoremanagementsystem;

public class shelve {
    String shelveID;
    String shelveName;
    String AreaID;

    public shelve(String shelveID, String shelveName, String areaID) {
        this.shelveID = shelveID;
        this.shelveName = shelveName;
        AreaID = areaID;
    }

    public String getShelveID() {
        return shelveID;
    }

    public void setShelveID(String shelveID) {
        this.shelveID = shelveID;
    }

    public String getShelveName() {
        return shelveName;
    }

    public void setShelveName(String shelveName) {
        this.shelveName = shelveName;
    }

    public String getAreaID() {
        return AreaID;
    }

    public void setAreaID(String areaID) {
        AreaID = areaID;
    }
}
