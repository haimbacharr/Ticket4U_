package com.example.ticket4u.Model;

public class Item {

    String name,pic,description,category,subCategory,userId,itemId,quantity,price;

    public Item(String name, String pic, String description,String quantity,String price, String category, String subCategory, String userId, String itemId) {
        this.name = name;
        this.pic = pic;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.userId = userId;
        this.itemId = itemId;
        this.price=price;
        this.quantity=quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
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
