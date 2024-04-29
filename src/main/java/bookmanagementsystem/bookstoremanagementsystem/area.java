package bookmanagementsystem.bookstoremanagementsystem;

public class area {
    private String areaID;
    private String areaName;
    private String floorID;

    public area(String areaID, String areaName, String floorID) {
        this.areaID = areaID;
        this.areaName = areaName;
        this.floorID = floorID;
    }

    public String getFloorID() {
        return floorID;
    }

    public void setFloorID(String floorID) {
        this.floorID = floorID;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
