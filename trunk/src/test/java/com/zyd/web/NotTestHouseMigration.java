package com.zyd.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tj.common.util.test.HttpTestUtil;
import com.zyd.ATestConstants;
import com.zyd.ATestUtil;
import com.zyd.Constants;
import com.zyd.core.db.HibernateUtil;
import com.zyd.linkmanager.Link;

public class NotTestHouseMigration extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        watchedList = new HashSet<String>();
        for (Link link : Constants.WATCH_LIST) {
            watchedList.add(link.getUrl());
        }
    }

    int pageSize = 100;
    public static HashSet<String> watchedList;

    public void testReadData() throws Exception {
        int start = 0;
        while (true) {
            System.out.println("Loading start from " + start);
            List r = loadData(start);
            if (r.size() == 0) {
                System.out.println("All loaded, stopping1");
                break;
            }
            createObjects(r);
            if (r.size() != pageSize) {
                System.out.println("All loaded, stopping2");
                break;
            }
            start = start + pageSize;
        }
    }

    public List loadData(int start) throws Exception {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query q = session.createQuery("from OHouse");
        q.setMaxResults(pageSize);
        q.setFirstResult(start);
        List r = q.list();
        session.getTransaction().commit();
        return r;
    }

    HashMap<String, Map> linkMap = new HashMap<String, Map>();

    public void createObjects(List r) throws Exception {
        linkMap.clear();
        // get a list of all the links
        for (Object o : r) {
            Map object = (Map) o;
            String link = (String) object.get("referer");
            if (link != null) {
                linkMap.put(link, object);
            }
        }
        if (linkMap.size() == 0) {
            System.err.println("No valid object with links are returned, won't create anything.");
        }

        Set<String> allLink = linkMap.keySet();
        createLink(new ArrayList<String>(allLink));

        int watchedLinkCount = 0;
        while (true) {
            String link = ATestUtil.getNextLink();
            while (watchedList.contains(link) == true) {
                link = ATestUtil.getNextLink();
                if (watchedLinkCount++ > 3) {
                    break;
                }
            }
            if (allLink.contains(link) == false) {
                System.out.println("Error, returned link is not what we added: " + link);
                return;
            }
            createObject(linkMap.remove(link));
            if (linkMap.size() == 0) {
                break;
            }
        }
    }

    public void createObject(Map obj) throws Exception {
        ArrayList<String> keys = new ArrayList<String>(obj.keySet());
        for (String key : keys) {
            Object v = obj.remove(key);
            if (v != null) {
                obj.put(key, v.toString());
            }
        }
        Object o = obj.remove("lo");
        if (o != null && "0.0".equals(o) == false) {
            obj.put("lng", o);
        }
        o = obj.remove("la");
        if (o != null && "0.0".equals(o) == false) {
            obj.put("lat", o);
        }
        obj.put("objectid", "House");
        obj.remove("createTime");
        obj.remove("updateTime");
        String r = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_OBJECT_URL, obj, obj.get("referer").toString());
        JSONObject objx = new JSONObject(r);
        assertTrue(objx.getBoolean("result"));
    }

    public void createLink(ArrayList<String> links) throws Exception {
        JSONArray arr = new JSONArray();
        for (String link : links) {
            arr.put(link);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("data", arr.toString());
        String s = HttpTestUtil.httpPostForString(ATestConstants.SERVICE_LINK_URL, params);
        JSONObject o = new JSONObject(s);
        if (o.has("result") == false) {
            System.out.println("Server returned links that has no result jons, raw data is :" + s);
            return;
        }
        if (links.size() != o.getInt("result")) {
            System.out.println("Server created wrong number of links" + o.getInt("result") + " than we posted:" + links.size());
        }
    }
}
