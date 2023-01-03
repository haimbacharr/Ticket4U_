package com.example.ticket4u.Model;

public class Item {

    String name,pic,description,category,subCategory,userId,itemId,quantity,originalPrice,askingPrice,date,city,number;

    public Item(String name, String pic, String description,String quantity,String price, String category, String subCategory, String userId, String itemId,String askingPrice,String date) {
        this.name = name;
        this.pic = pic;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.userId = userId;
        this.itemId = itemId;
        this.originalPrice=price;
        this.quantity=quantity;
        this.askingPrice=askingPrice;
        this.date=date;
    }
    public Item(String name, String pic, String description,String quantity,String price, String category, String subCategory, String userId, String itemId,String askingPrice,String date,String city,String number) {
        this.name = name;
        this.pic = pic;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.userId = userId;
        this.itemId = itemId;
        this.originalPrice=price;
        this.quantity=quantity;
        this.askingPrice=askingPrice;
        this.date=date;
        this.city=city;
        this.number=number;
    }

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getAskingPrice() {
        return askingPrice;
    }

    public String getDate() {
        return date;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getName() {
        return name;
    }

    public String getPic() {
        return pic;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getUserId() {
        return userId;
    }

    public String getItemId() {
        return itemId;
    }
}
