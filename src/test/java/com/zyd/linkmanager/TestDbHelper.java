package com.zyd.linkmanager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.hibernate.jdbc.Work;

import com.zyd.ATestUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.linkmanager.mysql.DbHelper;
import com.zyd.linkmanager.mysql.LinkTableInfo;

public class TestDbHelper extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        deleteAllLinkTableInfo();
    }

    public void testAddLinkTableOneStep() throws Exception {
        for (int i = 0; i < 3; i++) {
            LinkTableInfo info = DbHelper.createLinkTable("domain_com_" + i);
            assertTrue(info.tableId > 0);
            assertEquals("domain_com_" + i, info.tableStringUid);
            assertTrue(DbHelper.isTableExist(info.getTableName()));

            LinkTableInfo ninfo = DbHelper.getLinkTableInfoByUid("domain_com_" + i);
            assertEquals(ninfo.id, info.id);
            assertEquals(ninfo.tableId, info.tableId);
            assertEquals(ninfo.getTableName(), info.getTableName());
            assertEquals(ninfo.tableStringUid, info.tableStringUid);
        }
    }

    public void testAddAndFinsihLink() throws Exception {
        LinkTableInfo info = DbHelper.createLinkTable("dianping_com");
        HashMap<String, Link> links = new HashMap<String, Link>();
        for (int i = 0; i < 100; i++) {
            Link link = DbHelper.addNewLinkToTable(info.getTableName(), "http://www.dianping.com/link_url_" + i);
            assertNotNull(link);
            assertTrue(link.getId() > 0);
            links.put(link.getUrl(), link);
        }

        // make sure links can be loaded back
        ArrayList<Link> loadedLinks = DbHelper.loadUnprocessedLink(info.getTableName(), 101);
        assertEquals(100, loadedLinks.size());
        for (Link l : loadedLinks) {
            assertEquals(Link.STATE_NOT_PROCESSED, l.getState());
            assertTrue(links.containsKey(l.getUrl()));
        }

        // make sure link is finished correctly
        for (Link l : loadedLinks) {
            assertTrue(DbHelper.updateLinkStatus(l.getId(), Link.STATE_FINISHED_OK, new Date(), info.getTableName()));
        }

        // it can not be loaded back
        ArrayList<Link> loadedLinks2 = DbHelper.loadUnprocessedLink(info.getTableName(), 101);
        assertEquals(0, loadedLinks2.size());

        // verify the link is managed by db
        for (Link l : loadedLinks) {
            assertTrue(DbHelper.containsLink(l.getUrl(), info.getTableName()));
        }
    }

    public static void testBatchUpdateLinkState() throws Exception {
        LinkTableInfo info = DbHelper.createLinkTable("dianping_com");
        for (int i = 0; i < 100; i++) {
            DbHelper.addNewLinkToTable(info.getTableName(), "http://www.dianping.com/link_url_" + i);
        }

        // change state to processing
        ArrayList<Link> links = DbHelper.loadUnprocessedLink(info.getTableName(), 100);
        assertEquals(100, DbHelper.batchUpdateLinkStatus(links, Link.STATE_PROCESSING, info.getTableName()));

        // make sure it can not be loaded again
        links = DbHelper.loadUnprocessedLink(info.getTableName(), 100);
        assertEquals(0, links.size());
    }

    public static void deleteAllLinkTableInfo() throws Exception {
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        HibernateUtil.getSessionFactory().getCurrentSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                DbHelper.clearAllLinkTable(connection);
            }
        });
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
    }

}
