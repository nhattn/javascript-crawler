public class Test {
    public static final double PI = 3.14159265358979323846;
    public final static double EARTHRADIUS = 6371000;

    public static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (double) (180 / 3.1415926);
        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        return 6366000 * tt;
    }

    public static double gps2m2(double lat1, double lng1, double lat2, double lng2) {
        lat1 = lat1 / 180.0 * PI;
        lat2 = lat2 / 180.0 * PI;
        lng1 = lng1 / 180.0 * PI;
        lng2 = lng2 / 180.0 * PI;

        return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1)) * EARTHRADIUS;
    }

    public static double[] distanceFrom(double lat1, double lng1, double distance, double bearing) {
        double dr = distance / EARTHRADIUS;
        lat1 = lat1 / 180.0 * PI;
        lng1 = lng1 / 180.0 * PI;
        bearing = bearing / 180.0 * PI;
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dr) + Math.cos(lat1) * Math.sin(dr) * Math.cos(bearing));
        double lng2 = lng1 + Math.atan2(Math.sin(bearing) * Math.sin(dr) * Math.cos(lat1), Math.cos(dr) - Math.sin(lat1) * Math.sin(lat2));
        return new double[] { Math.toDegrees(lat2), Math.toDegrees(lng2) };
    }

    public static void main(String[] args) {
        System.out.println(gps2m(39.054385f, 116.076736f, 39.084771f, 116.158791f));
        System.out.println(gps2m2(39.054385f, 116.076736f, 39.084771f, 116.158791f));
        double[] d2 = distanceFrom(30, 100, 1112000, 180);
        System.out.println(gps2m2(39.054385, 116.076736, 39.084771,116.158791));

//        System.out.println(d2[0] + "," + d2[1]);
        System.out.println(Integer.parseInt("2592000000"));
    }
}
