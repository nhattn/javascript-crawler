package misc;

import org.hibernate.Session;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.House;

public class HibernateTest {
    public static void main(String[] args) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        House house = new House();
        house.setId(11l);
        house.setLa(11.11f);
        session.save(house);
        session.flush();
        session.getTransaction().commit();
        System.out.println("done");
    }
}
