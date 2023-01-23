package com.ticket.foru.Model;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Item {

    String name,pic,description,category,subCategory,userId,itemId,quantity,originalPrice,askingPrice,date,city,number,latitude,longitude,distance;
    public static final Comparator<Item> NAMEASC = new Comparator<Item>() {
        @Override
        public int compare(Item itemOne, Item itemTwo) {
            return itemOne.getName().toLowerCase(Locale.getDefault()).compareTo(itemTwo.getName().toLowerCase(Locale.getDefault()));
        }
    };

    public static final Comparator<Item> DISASC = new Comparator<Item>() {
        @Override
        public int compare(Item itemOne, Item itemTwo) {
            Double tripRateOne = new Double(itemOne.getDistance());
            Double tripRateTwo = new Double(itemTwo.getDistance());
            return tripRateOne.compareTo(tripRateTwo);
        }
    };

    public static final Comparator<Item> PRICEASC = new Comparator<Item>() {
        @Override
        public int compare(Item itemOne, Item itemTwo) {
            Integer tripRateOne = 0;
            Integer tripRateTwo = 0;
            if(!TextUtils.isEmpty(itemOne.getAskingPrice())){
                tripRateOne = new Integer(itemOne.getAskingPrice());
            }
            if(!TextUtils.isEmpty(itemTwo.getAskingPrice())){
                tripRateTwo = new Integer(itemTwo.getAskingPrice());
            }
            return tripRateOne.compareTo(tripRateTwo);
        }
    };

    public static final Comparator<Item> DATEASC = new Comparator<Item>() {
        @Override
        public int compare(Item itemOne, Item itemTwo) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date d1 = null;
            Date d2 = null;
            try {
                    d1 = sdf.parse(itemOne.getDate());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                    d2 = sdf.parse(itemTwo.getDate());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return d1.compareTo(d2);
        }
    };

    public Item(String name, String pic, String description,String quantity,String price, String category, String subCategory, String userId, String itemId,String askingPrice,String date,String latitude,String longitude,String distance) {
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
        this.latitude=latitude;
        this.longitude=longitude;
        this.distance=distance;
    }

    public Item(String name, String pic, String description,String quantity,String price, String category, String subCategory, String userId, String itemId,String askingPrice,String date,String city,String number,String latitude,String longitude,String distance) {
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
        this.latitude=latitude;
        this.longitude=longitude;
        this.distance=distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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
