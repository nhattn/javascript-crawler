package com.zyd.core.busi.house;

import com.zyd.Constants;

public class HouseData {
    public int totalRentalCount;
    public int totalSaleCount;
    public int totalSaleSize;
    public int totalSalePrice;

    public int getAverageSaleUnitPrice() {
        if (totalSaleSize != 0) {
            return (int) ((totalSalePrice * 10000 / totalSaleSize));
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
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

        return buf.toString();
    }
}
