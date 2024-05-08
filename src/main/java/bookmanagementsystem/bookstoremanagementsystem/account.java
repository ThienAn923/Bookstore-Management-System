package bookmanagementsystem.bookstoremanagementsystem;

public class account {
    String accountID;
    String accountUsername;
    String accountPassword;
    boolean accountAuthorized;
    boolean accountHidden;
    String staffID;

    public account(String accountID, String accountUsername, String accountPassword, boolean accountAuthorized, boolean accountHidden, String staffID) {
        this.accountID = accountID;
        this.accountUsername = accountUsername;
        this.accountPassword = accountPassword;
        this.accountAuthorized = accountAuthorized;
        this.accountHidden = accountHidden;
        this.staffID = staffID;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public boolean isAccountAuthorized() {
        return accountAuthorized;
    }

    public void setAccountAuthorized(boolean accountAuthorized) {
        this.accountAuthorized = accountAuthorized;
    }

    public boolean isAccountHidden() {
        return accountHidden;
    }

    public void setAccountHidden(boolean accountHidden) {
        this.accountHidden = accountHidden;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }
}
