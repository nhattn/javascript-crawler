package com.zyd.core.busi.userreg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.objecthandler.ObjectHelper;

public class SiteRegisterManager {
    public String nextUser() throws Exception {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria c = session.createCriteria("RegisterUser");
        HashMap<String, Object[]> qparams = new HashMap<String, Object[]>();
        qparams.put("count", new Integer[] { 0 });
        ObjectHelper.buildHibernateCriteria(c, qparams);
        c.setMaxResults(1);
        List list = c.list();
        if (list.size() == 0)
            return null;
        Map result = (Map) list.get(0);
        int count = ((Integer) result.get("count")).intValue();
        result.put("count", new Integer(count + 1));
        session.getTransaction().commit();
        return (String) result.get("name");
    }

    public boolean addSiteUser(String username, String password, String email, String site) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("name", username);
        values.put("count", new Integer(0));
        values.put("site", site);
        values.put("email", email);
        session.save("SiteUser", values);
        session.getTransaction().commit();
        return true;
    }
    
//    public boolean updateSiteUser(String username, String password)
}
