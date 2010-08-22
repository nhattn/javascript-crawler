package com.zyd.core.busi.house;

import java.util.Date;

import com.zyd.Constants;

public class HouseData {
    public final static int TYPE_BY_DAY = 1;
    public final static int TYPE_BY_WEEK = 2;
    public final static int TYPE_BY_MONTH = 3;

    public int totalRentalCount;
    public int totalSaleCount;
    public int totalSaleSize;
    public int totalSalePrice;
    public int type;
    public String city;
    public Date date;

    public int getAverageSaleUnitPrice() {
        if (totalSaleSize != 0) {
            return (int) ((totalSalePrice * 10000 / totalSaleSize));
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("city :" + city);
        buf.append(Constants.LINE_SEPARATOR);

        buf.append("type :" + type);
        buf.append(Constants.LINE_SEPARATOR);

        buf.append("totalSaleCount :" + totalSaleCount);
        buf.append(Constants.LINE_SEPARATOR);
        buf.append("totalSaleSize :" + totalSaleSize);
        buf.append(Constants.LINE_SEPARATOR);
        buf.append("totalSalePrice :" + totalSalePrice);
        buf.append(Constants.LINE_SEPARATOR);

        buf.append("averageSaleUnitPrice :" + getAverageSaleUnitPrice());
        buf.append(Constants.LINE_SEPARATOR);

        buf.append("totalRentCount :" + totalRentalCount);
        buf.append(Constants.LINE_SEPARATOR);

        buf.append("date :" + date);
        buf.append(Constants.LINE_SEPARATOR);

        return buf.toString();
    }
}
