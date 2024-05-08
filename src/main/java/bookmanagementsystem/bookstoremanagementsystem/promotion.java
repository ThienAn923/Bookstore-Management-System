package bookmanagementsystem.bookstoremanagementsystem;

import java.sql.Date;

public class promotion {
    String promotionID;
    String promotionName;
    String promotionDescription;
    Date promotionStartDay;
    Date promotionEndDay;
    float promotionPercentage;

    public promotion(String promotionID, String promotionName, String promotionDescription, Date promotionStartDay, Date promotionEndDay, float promotionPercentage) {
        this.promotionID = promotionID;
        this.promotionName = promotionName;
        this.promotionDescription = promotionDescription;
        this.promotionStartDay = promotionStartDay;
        this.promotionEndDay = promotionEndDay;
        this.promotionPercentage = promotionPercentage;
    }

    public String getPromotionID() {
        return promotionID;
    }

    public void setPromotionID(String promotionID) {
        this.promotionID = promotionID;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    public void setPromotionDescription(String promotionDescription) {
        this.promotionDescription = promotionDescription;
    }

    public Date getPromotionStartDay() {
        return promotionStartDay;
    }

    public void setPromotionStartDay(Date promotionStartDay) {
        this.promotionStartDay = promotionStartDay;
    }

    public Date getPromotionEndDay() {
        return promotionEndDay;
    }

    public void setPromotionEndDay(Date promotionEndDay) {
        this.promotionEndDay = promotionEndDay;
    }

    public float getPromotionPercentage() {
        return promotionPercentage;
    }

    public void setPromotionPercentage(float promotionPercentage) {
        this.promotionPercentage = promotionPercentage;
    }
}
