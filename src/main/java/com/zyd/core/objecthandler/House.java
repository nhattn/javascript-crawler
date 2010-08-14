package com.zyd.core.objecthandler;

import java.util.HashMap;
import java.util.HashSet;

import com.tj.common.CommonUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.util.Ocr;

@SuppressWarnings("unchecked")
public class House extends Handler {
    public final static String EntityName = HibernateUtil.EntityNames.House;
    private final static HashSet CDataColumns = new HashSet();

    public String getEntityName() {
        return EntityName;
    }

    @Override
    protected boolean beforeCreate(HashMap values) {
        String tel = (String) values.get(Columns.Tel);
        if (tel != null && tel.length() > 100) {
            String type = CommonUtil.getFileSuffix((String) values.get(Columns.TelImageName));
            values.put(Columns.Tel, Ocr.ocrImageNumber(tel, type));
        }
        return true;
    }

    public SearchResult query(HashMap params) {
        SearchResult r = super.query(params);
        r.cdataColumns = CDataColumns;
        return r;
    }

    public final static class Columns extends Handler.Columns {
        public final static String RentalType = "rentalType";
        public final static String SubRentalType = "subRentalType";
        public final static String Price = "price";
        public final static String PaymentType = "paymentType";
        public final static String PriceUit = "priceUit";
        public final static String Size = "size";
        public final static String HouseType = "houseType";
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
        public final static String TelImageName = "telImageName";
    }

}
