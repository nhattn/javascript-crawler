package tools;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tj.common.util.test.HttpTestUtil;
import com.zyd.core.db.HibernateUtil;

/**
 * this class does geocoding for train station.
 *
 */
public class TrainGeocoding {

    public static void main(String[] args) throws Exception {
        InputStream ins = TrainGeocoding.class.getClassLoader().getResourceAsStream("trainstations.list");
        List l = IOUtils.readLines(ins, "UTF-8");
        ins.close();
        for (Object o : l) {
            String name = o.toString().trim();
            if (name.length() == 0)
                continue;
            Address r = goecodeAddress(name + "火车站");
            if (r == null) {
                r = goecodeAddress("中国" + name);
            }
            if (r == null) {
                r = goecodeAddress(name);
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

    static String GeocodingUrl = "http://maps.google.com/maps/api/geocode/json?sensor=false&language=zh&address=";

    public static void update(Address address, String stationName) {
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

    public static Address goecodeAddress(String address) throws Exception {
        try {
            Address r = new Address();
            r.address = address;
            String s = HttpTestUtil.httpGetForString(GeocodingUrl + URLEncoder.encode(address, "UTF8"), null);
            JSONObject addr = new JSONObject(s);
            if ("OK".equals(addr.getString("status")) == false) {
                System.err.println("wrong status for : " + address + " " + addr.getString("status"));
                if ("ZERO_RESULTS".equals(addr.getString("status")) == false) {
                    System.err.println("Wrong status, exit :" + addr.getString("status"));
                    System.exit(0);
                }
                return null;
            }

            if (addr.has("results") == false) {
                System.err.println("no match for : " + address);
                return null;
            }

            JSONArray results = addr.getJSONArray("results");
            if (results.length() > 1) {
                System.err.println(address + " has more than one matches " + results.length());
            } else if (results.length() == 0) {
                System.err.println("no match for : " + address);
                return null;
            }
            JSONObject e = results.getJSONObject(0);
            JSONArray comps = e.getJSONArray("address_components");
            for (int i = 0, len = comps.length(); i < len; i++) {
                JSONObject o = comps.getJSONObject(i);
                JSONArray types = o.getJSONArray("types");
                if (types.length() < 2)
                    continue;
                String a = types.getString(0), b = types.getString(1), name = o.getString("long_name");
                if (isSameStrings("locality", "political", a, b)) {
                    r.city = name;
                } else if (isSameStrings("administrative_area_level_1", "political", a, b)) {
                    r.province = name;
                } else if (isSameStrings("sublocality", "political", a, b)) {
                    r.district = name;
                }
            }
            JSONObject l = e.getJSONObject("geometry").getJSONObject("location");
            r.lat = Double.parseDouble(l.getString("lat"));
            r.lng = Double.parseDouble(l.getString("lng"));
            return r;
        } catch (Exception e) {
            if (e.getClass().getCanonicalName().startsWith("java.net")) {
                System.err.println("Network exception for :" + address);
                e.printStackTrace();
                return null;
            }
            System.err.println("Can not handle " + address);
            throw e;
        }
    }

    public static boolean isSameStrings(String a, String b, String a1, String b1) {
        if (a.equals(a1) && b.equals(b1))
            return true;
        if (a.equals(b1) && b.equals(a1))
            return true;
        return false;

    }

    static class Address {
        public double lng, lat;
        public String province, city, district, address;

        @Override
        public String toString() {
            return "province=" + province + ", city=" + city + ", district=" + district + ", address=" + address + ", lng=" + lng + ", lat=" + lat;
        }
    }
}
