package com.zyd.web;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.hibernate.jdbc.Work;

import com.zyd.ATestUtil;
import com.zyd.core.db.HibernateUtil;
import com.zyd.linkmanager.mysql.DbHelper;

public class TestDbHelper extends TestCase {
    @Override
    protected void setUp() throws Exception {
        ATestUtil.setUpSpring();
        deleteAllTableInfo();
    }

    public void testAddLinkTableOneStep() throws Exception {
        DbHelper.createLinkTable("koubei_com");
    }

    private void deleteAllTableInfo() throws Exception {
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        HibernateUtil.getSessionFactory().getCurrentSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                DbHelper.clearAllLinkTable(connection);
            }
        });
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
    }
}
