package bookmanagementsystem.bookstoremanagementsystem;

import java.util.Date;

public class staff {
    String staffID;
    String staffName;
    String staffPhoneNumber;
    String staffEmail;
    Date staffBirthday;
    boolean staffGender;
    String staffDescription;


    public staff(String staffID, String staffName, String staffPhoneNumber, String staffEmail, Date staffBirthday, boolean staffGender, String staffDescription) {
        this.staffID = staffID;
        this.staffName = staffName;
        this.staffPhoneNumber = staffPhoneNumber;
        this.staffEmail = staffEmail;
        this.staffBirthday = staffBirthday;
        this.staffGender = staffGender;
        this.staffDescription = staffDescription;

    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffPhoneNumber() {
        return staffPhoneNumber;
    }

    public void setStaffPhoneNumber(String staffPhoneNumber) {
        this.staffPhoneNumber = staffPhoneNumber;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public Date getStaffBirthday() {
        return staffBirthday;
    }

    public void setStaffBirthday(Date staffBirthday) {
        this.staffBirthday = staffBirthday;
    }

    public boolean isStaffGender() {
        return staffGender;
    }

    public void setStaffGender(boolean staffGender) {
        this.staffGender = staffGender;
    }

    public String getStaffDescription() {
        return staffDescription;
    }

    public void setStaffDescription(String staffDescription) {
        this.staffDescription = staffDescription;
    }

}
