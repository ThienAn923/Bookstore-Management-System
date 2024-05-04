package bookmanagementsystem.bookstoremanagementsystem;

public class position {
    String positionID;
    String positionName;
    float positionSalary;

    public position(String positionID, String positionName, float positionSalary) {
        this.positionID = positionID;
        this.positionName = positionName;
        this.positionSalary = positionSalary;
    }

    public String getPositionID() {
        return positionID;
    }

    public void setPositionID(String positionID) {
        this.positionID = positionID;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public float getPositionSalaryCoefficient() {
        return positionSalary;
    }

    public void setPositionSalaryCoefficient(float positionSalary) {
        this.positionSalary = positionSalary;
    }
}
