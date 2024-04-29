package bookmanagementsystem.bookstoremanagementsystem;

public class floor{
    private String floorID;

    public floor(String floorID, String floorName) {
        this.floorID = floorID;
        this.floorName = floorName;
    }

    private String floorName;

    public String getFloorID() {
        return floorID;
    }

    public void setFloorID(String floorID) {
        this.floorID = floorID;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }
}
