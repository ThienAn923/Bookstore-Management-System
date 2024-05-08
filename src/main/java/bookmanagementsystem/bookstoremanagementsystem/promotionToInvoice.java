package bookmanagementsystem.bookstoremanagementsystem;

public class promotionToInvoice {
    String invoiceID;
    String promotionID;

    public promotionToInvoice(String invoiceID, String promotionID) {
        this.invoiceID = invoiceID;
        this.promotionID = promotionID;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getPromotionID() {
        return promotionID;
    }

    public void setPromotionID(String promotionID) {
        this.promotionID = promotionID;
    }
}
