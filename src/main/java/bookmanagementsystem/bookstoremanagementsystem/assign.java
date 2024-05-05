package bookmanagementsystem.bookstoremanagementsystem;

import java.util.Date;

public class assign {
    String staffID;
    String departmentID;
    String positionID;
    Date dateOfAssignment;

    public assign(String staffID, String departmentID, String positionID, Date dateOfAssignment) {
        this.staffID = staffID;
        this.departmentID = departmentID;
        this.positionID = positionID;
        this.dateOfAssignment = dateOfAssignment;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    public String getPositionID() {
        return positionID;
    }

    public void setPositionID(String positionID) {
        this.positionID = positionID;
    }

    public Date getDateOfAssignment() {
        return dateOfAssignment;
    }

    public void setDateOfAssignment(Date dateOfAssignment) {
        this.dateOfAssignment = dateOfAssignment;
    }
}
