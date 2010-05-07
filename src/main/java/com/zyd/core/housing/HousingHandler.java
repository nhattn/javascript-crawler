package com.zyd.core.housing;

import java.util.HashMap;

import com.zyd.core.Handler;
import com.zyd.core.Utils;

public class HousingHandler implements Handler {

    public String getName() {
        return "housing";
    }

    public Object process(HashMap<String, String> values) {
        String tel = values.get(Columns.Tel);
        if (tel != null && tel.length() > 100) {
            values.put(Columns.Tel, Utils.ocrImageNumber(tel).trim());
        }
        System.out.println(values);
        return true;
    }

    final static class Columns {
        public final static String Long = "lo";
        public final static String Lat = "la";
        public final static String RentalType = "rentalType";
        public final static String SubRentalType = "subRentalType";
        public final static String Price = "price";
        public final static String PaymentType = "paymentType";
        public final static String PriceUit = "priceUit";
        public final static String Size = "size";
        public final static String HouseType = "houseType";
        public final static String Time = "time";
        public final static String Address = "address";
        public final static String District1 = "district1";
        public final static String District3 = "district3";
        public final static String District5 = "district5";
        public final static String Tel = "tel";
        public final static String Contact = "contact";
        public final static String Photo = "photo";
        public final static String Description1 = "description1";
        public final static String Description2 = "description2";
        public final static String Floor = "floor";
        public final static String TotalFloor = "totalFloor";
        public final static String IsAgent = "isAgent";
        public final static String Equipment = "equipment";
        public final static String Decoration = "decoration";
    }
}
