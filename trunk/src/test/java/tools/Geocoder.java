package tools;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tj.common.util.test.HttpTestUtil;

public class Geocoder {

    public static Geocoder.Address goecodeAddress(String address) throws Exception {
        Geocoder.Address r = new Geocoder.Address();
        try {
            r.address = address;
            String s = HttpTestUtil.httpGetForString(Geocoder.GeocodingUrl + URLEncoder.encode(address, "UTF8"), null);
            JSONObject addr = new JSONObject(s);
            if ("OK".equals(addr.getString("status")) == false) {
                if ("ZERO_RESULTS".equals(addr.getString("status")) == false) {
                    r.state = Address.State_Banned;
                } else {
                    r.state = Address.State_NoMatch;
                }
                return r;
            }

            if (addr.has("results") == false) {
                r.state = Address.State_NoMatch;
                return r;
            }

            JSONArray results = addr.getJSONArray("results");
            if (results.length() > 1) {
                r.state = Address.State_MoreThanOne;
                return r;
            } else if (results.length() == 0) {
                r.state = Address.State_NoMatch;
                return r;
            }

            JSONObject e = results.getJSONObject(0);
            JSONArray comps = e.getJSONArray("address_components");
            for (int i = 0, len = comps.length(); i < len; i++) {
                JSONObject o = comps.getJSONObject(i);
                JSONArray types = o.getJSONArray("types");
                if (types.length() < 2)
                    continue;
                String a = types.getString(0), b = types.getString(1), name = o.getString("long_name");
                if (TrainStationGeocoding.isSameStrings("locality", "political", a, b)) {
                    r.city = name;
                } else if (TrainStationGeocoding.isSameStrings("administrative_area_level_1", "political", a, b)) {
                    r.province = name;
                } else if (TrainStationGeocoding.isSameStrings("sublocality", "political", a, b)) {
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
//                e.printStackTrace();
                r.state = Address.State_NetworkError;
                return r;
            }
            System.err.println("Can not handle " + address);
            //            throw e;

            r.state = Address.State_NetworkError;
            return r;
        }
    }

    public static class Address {
        public final static int State_OK = 0;
        public final static int State_MoreThanOne = 1;
        public final static int State_NoMatch = 2;
        public final static int State_Banned = 3;
        public final static int State_NetworkError = 4;

        public double lng, lat;
        public String province, city, district, address;
        public int state;

        @Override
        public String toString() {
            return "province=" + province + ", city=" + city + ", district=" + district + ", address=" + address + ", lng=" + lng + ", lat=" + lat + ", state="
                    + state;
        }
    }

    static String GeocodingUrl = "http://maps.google.com/maps/api/geocode/json?sensor=false&language=zh&address=";
}
