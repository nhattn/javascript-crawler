package tools;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.zyd.core.db.HibernateUtil;

/**
 * this class does geocoding for train station. 
 */
public class TrainStationGeocoding {
    public static void main(String[] args) throws Exception {
        InputStream ins = TrainStationGeocoding.class.getClassLoader().getResourceAsStream("trainstations.list");
        List l = IOUtils.readLines(ins, "UTF-8");
        ins.close();
        for (Object o : l) {
            String name = o.toString().trim();
            if (name.length() == 0)
                continue;
            Geocoder.Address r = Geocoder.goecodeAddress(name + "火车站");
            if (r == null) {
                r = Geocoder.goecodeAddress("中国" + name);
            }
            if (r == null) {
                r = Geocoder.goecodeAddress(name);
            }

            if (r == null) {
                System.err.println("Can not geocode :" + name);
            } else {
                System.out.println(r);
                update(r, name);
            }

            Thread.sleep(1000);
        }
    }

    public static void update(Geocoder.Address address, String stationName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            Query query = session.createQuery("update " + HibernateUtil.EntityNames.TrainStation
                    + " set lng=:lng, lat=:lat, city=:city, province=:province, district=:district where name=:name");
            query.setDouble("lng", address.lng);
            query.setDouble("lat", address.lat);
            query.setString("name", stationName);
            query.setString("province", address.province);
            query.setString("city", address.city);
            query.setString("district", address.district);
            query.executeUpdate();
            trx.commit();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            trx.rollback();
        }

    }

    public static boolean isSameStrings(String a, String b, String a1, String b1) {
        if (a.equals(a1) && b.equals(b1))
            return true;
        if (a.equals(b1) && b.equals(a1))
            return true;
        return false;

    }
}
