package bookmanagementsystem.bookstoremanagementsystem;

public class provider {
    String providerID;
    String providerName;

    public provider(String providerID, String providerName) {
        this.providerID = providerID;
        this.providerName = providerName;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}
