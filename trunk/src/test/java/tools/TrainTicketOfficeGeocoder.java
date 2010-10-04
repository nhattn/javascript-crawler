package tools;

import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import tools.Geocoder.Address;

import com.zyd.core.db.HibernateUtil;

/**
 * script for backup and restore the files
 *    mysqldump -uroot -proot crawler layer_com_zuiyidong_layer_train_ticketoffice > /y/workspace/webcrawl/backup/ticketoffice.sql
 *    mysql -uroot -proot crawler < /y/workspace/webcrawl/backup/ticketoffice.sql
 * 
 * This class takes the train ticket office entity, and scans for those with lng field is null, 
 * then trings to do geocoding for the city+address string. For address with lng field value set, it won't redo it.
 * 
 * It also normalizes the address string by removing extra '(' and '#' etc..
 *
 */
public class TrainTicketOfficeGeocoder {
    public static void main(String[] args) throws Exception {
        processOffices();
    }

    public static void processOffices() throws Exception {
        List<HashMap> offices = HibernateUtil.loadObject(HibernateUtil.EntityNames.TrainTicketOffice);
        for (int i = 0, len = offices.size(); i < len; i++) {
            Double lng = (Double) offices.get(i).get("lng");
            if (lng != null)
                continue;
            else
                processObject(offices.get(i));

        }
    }

    public static void processObject(HashMap obj) throws Exception {
        System.out.println("processing id " + obj.get("id") + obj.get("address"));
        String city = (String) obj.get("city");
        String province = (String) obj.get("province");
        String address = (String) obj.get("address");
        String fullAddress = address;
        if (fullAddress == null) {
            System.err.println("full address is null ----- ");
            return;
        }
        int i = fullAddress.indexOf('（');
        if (i > 0) {
            fullAddress = fullAddress.substring(0, i);
        }
        i = fullAddress.indexOf('(');
        if (i > 0) {
            fullAddress = fullAddress.substring(0, i);
        }
        if (fullAddress.indexOf(city) == -1) {
            fullAddress = city + fullAddress;
        }
        Address addr = Geocoder.goecodeAddress(fullAddress);
        if (addr.state != Address.State_OK) {
            System.out.println("Error state : " + addr.state + ", " + obj);
        } else {
            System.out.println(addr.lng + ", " + addr.lat);
            updateGps(addr, obj);
        }
        Thread.sleep(1000);
    }

    public static void updateGps(Address address, HashMap obj) throws Exception {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        String naddress = address.address;
        naddress = naddress.replace('#', '号');
        naddress = naddress.replace("号号", "号");
        int start = naddress.indexOf('(');
        if (start == -1)
            start = naddress.indexOf('（');
        int end = naddress.indexOf(')');
        if (end == -1) {
            end = naddress.indexOf('）');
        }
        if (end == -1 && start != -1) {
            naddress = naddress.substring(0, start);
        }
        naddress = naddress.trim();
        try {
            Query query = session.createQuery("update " + HibernateUtil.EntityNames.TrainTicketOffice
                    + " set lng=:lng, lat=:lat, address=:address where id=:id");
            query.setDouble("lng", address.lng);
            query.setDouble("lat", address.lat);
            query.setString("address", naddress);
            query.setInteger("id", (Integer) obj.get("id"));
            query.executeUpdate();
            trx.commit();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            trx.rollback();
        }
    }
}
