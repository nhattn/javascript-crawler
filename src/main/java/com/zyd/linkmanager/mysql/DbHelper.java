package com.zyd.linkmanager.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.Counter;
import com.zyd.linkmanager.Link;

public class DbHelper {
    private static Logger logger = Logger.getLogger(DbHelper.class);
    public final static String LinkTablePrefix = "LinkTable_";
    // index will be created on these columns
    private final static String[] LinkTableIndexColumns = new String[] { "url", "state" };
    private final static long TableIdSuffix = 1000000000000L;

    /**
     * Several thing happens when create a new link table.
     * 
     * 1) An entry will be inserted into LinkTableMap. The assigned id will be taken
     * 2) A new LinkTable will be created called LinkTable_[new id]
     * 3) indexes will be created on (url, state) of newly created LinkTable
     * 4) reset auto increment to preagreed numbers. info.id + "00000000000"
     * @param tableStringUid
     * @return
     * @throws Exception
     */
    public final static LinkTableInfo createLinkTable(final String tableStringUid) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final LinkTableInfo info = new LinkTableInfo();
        info.tableStringUid = tableStringUid;
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    LinkTableInfo ninfo = addLinkTableInfo(info, connection);
                    info.tableId = ninfo.tableId;
                    createLinkTable(info.getTableName(), connection);
                    for (int i = 0; i < LinkTableIndexColumns.length; i++) {
                        String indexCol = LinkTableIndexColumns[i];
                        createIndexOnTable(info.getTableName(), indexCol, info.getTableName() + "_index_" + indexCol, connection);
                    }
                    alterAutoIncrementIndex(info.getTableName(), info.tableId * TableIdSuffix, connection);
                }
            });
        } catch (HibernateException he) {
            trx.rollback();
            throw he;
        }
        trx.commit();
        return info;
    }

    /**
     * All link table info is stored in LinkTableMap(id, uid)
     * @param tableStringUid
     * @param con
     * @throws SQLException
     */
    public static LinkTableInfo getLinkTableInfoByUid(final String tableStringUid) {
        final LinkTableInfo r = new LinkTableInfo();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement pstm = connection.prepareStatement("select id, tableId from LinkTableMap where uid=?");
                    pstm.setString(1, tableStringUid);
                    ResultSet rset = pstm.executeQuery();
                    if (rset.next() != false) {
                        r.tableStringUid = tableStringUid;
                        r.id = rset.getInt(1);
                        r.tableId = rset.getInt(2);
                    }
                    rset.close();
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            throw e;
        }
        trx.commit();
        if (r.id > 0)
            return r;
        return null;
    }

    public final static Link addNewLinkToTable(final String tableName, final String url) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final Link link = new Link();
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement pst = connection.prepareStatement("insert into " + tableName + "(url, createTime, tryCount, state) values(?,?,0,0)", java.sql.Statement.RETURN_GENERATED_KEYS);
                    Date now = new Date();
                    link.setCreateTime(now);
                    link.setState(Link.STATE_NOT_PROCESSED);
                    link.setUrl(url);
                    pst.setString(1, url);
                    pst.setTimestamp(2, new Timestamp(now.getTime()));
                    pst.executeUpdate();
                    ResultSet r = pst.getGeneratedKeys();
                    r.next();
                    link.setId(r.getLong(1));
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            throw e;
        }
        trx.commit();
        return link;
    }

    public final static ArrayList<Link> loadUnprocessedLink(final String tableName, final int count) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final ArrayList<Link> r = new ArrayList<Link>();
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    ResultSet rset = connection.createStatement().executeQuery("select id, url, createTime from " + tableName + " where state = 0 limit " + count);
                    while (rset.next()) {
                        Link link = new Link();
                        link.setCreateTime(new Date(rset.getTimestamp("createTime").getTime()));
                        link.setId(rset.getLong("id"));
                        link.setUrl(rset.getString("url"));
                        link.setState(Link.STATE_NOT_PROCESSED);
                        r.add(link);
                    }
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            throw e;
        }
        trx.commit();
        return r;
    }

    public static void clearAllLinkTable(Connection con) throws SQLException {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ResultSet r = con.createStatement().executeQuery("select id, uid from LinkTableMap");
        while (r.next()) {
            ids.add(r.getInt(1));
        }
        r.close();
        for (Integer i : ids) {
            String tableName = LinkTablePrefix + i.intValue();
            try {
                dropTable(tableName, con);
            } catch (SQLException e) {
                logger.warn("Can not delete table " + tableName, e);
            }
        }
        executeSql("delete from LinkTableMap", con);
    }

    public static boolean containsLink(final String url, final String tableName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final Counter counter = new Counter();
        counter.total = 0;
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement stmt = connection.prepareStatement("select url from " + tableName + " where url = ? limit 1");
                    stmt.setString(1, url);
                    ResultSet rset = stmt.executeQuery();
                    if (rset.next())
                        counter.total++;
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            throw e;
        }
        trx.commit();
        return counter.total != 0;
    }

    public static boolean updateLinkStatus(final long linkId, final int state, final Date finishedTime, final String tableName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final Counter counter = new Counter();
        counter.total = 0;
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement stmt = connection.prepareStatement("update " + tableName + " set state=?, finishtime=?, trycount=(trycount+1) where id=?");
                    stmt.setInt(1, state);
                    stmt.setTimestamp(2, new Timestamp(finishedTime.getTime()));
                    stmt.setLong(3, linkId);
                    counter.total = stmt.executeUpdate();
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            throw e;
        }
        trx.commit();
        return counter.total == 1;
    }

    public static int batchUpdateLinkStatus(final ArrayList<Link> links, final int state, final String tableName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final Counter counter = new Counter();
        counter.total = 0;
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    StringBuffer buf = new StringBuffer("update ");
                    buf.append(tableName);
                    buf.append(" set state=");
                    buf.append(state);
                    buf.append(" where id in (");
                    for (Link link : links) {
                        buf.append(link.getId());
                        buf.append(',');
                    }
                    buf.deleteCharAt(buf.length() - 1);
                    buf.append(')');
                    counter.total = connection.createStatement().executeUpdate(buf.toString());
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            throw e;
        }
        trx.commit();
        return (int) counter.total;
    }

    public static boolean updateLinkStatus(final Link link, final int state, final String tableName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final Counter counter = new Counter();
        counter.total = 0;
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    counter.total = connection.createStatement().executeUpdate("update " + tableName + " set state=" + state + " where id=" + link.getId());
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            throw e;
        }
        trx.commit();
        return counter.total == 1;
    }

    public static boolean isTableExist(final String tableName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final Counter counter = new Counter();
        counter.total = 0;
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    connection.createStatement().executeQuery("select 1>2 from " + tableName);
                }
            });
        } catch (HibernateException e) {
            trx.rollback();
            return false;
        }
        trx.commit();
        return true;
    }

    private static void executeSql(String sql, Connection con) throws SQLException {
        con.createStatement().execute(sql);
    }

    private static void createLinkTable(String tableName, Connection con) throws SQLException {
        String sql = "create table " + tableName + "(id bigint NOT NULL AUTO_INCREMENT,url varchar(1000),createTime timestamp,finishTime timestamp,tryCount tinyint, state tinyint, PRIMARY KEY (id))";
        executeSql(sql, con);
    }

    private static void dropTable(String tableName, Connection con) throws SQLException {
        String sql = "drop table " + tableName;
        executeSql(sql, con);
    }

    private static void createIndexOnTable(String tableName, String indexColumnName, String indexName, Connection con) throws SQLException {
        StringBuffer buf = new StringBuffer();
        buf.append("create index ");
        buf.append(indexName);
        buf.append(" on ");
        buf.append(tableName);
        buf.append(" (");
        buf.append(indexColumnName);
        buf.append(')');
        executeSql(buf.toString(), con);
    }

    /**
     * take a link table with uid, return LinkTableInfo with assigned id
     * @param info
     * @param con
     * @return
     * @throws SQLException
     */
    private static LinkTableInfo addLinkTableInfo(LinkTableInfo info, Connection con) throws SQLException {
        PreparedStatement pstm = con.prepareStatement("insert into LinkTableMap(uid) values(?)", java.sql.Statement.RETURN_GENERATED_KEYS);
        pstm.setString(1, info.tableStringUid);
        pstm.execute();
        ResultSet rset = pstm.getGeneratedKeys();
        rset.next();
        int generatedId = rset.getInt(1);

        pstm = con.prepareStatement("update LinkTableMap set tableId=? where id=?");
        pstm.setInt(1, generatedId);
        pstm.setInt(2, generatedId);
        pstm.executeUpdate();
        info.id = generatedId;
        info.tableId = generatedId;
        return info;
    }

    private static void deleteLinkTableInfoById(int id, Connection con) throws SQLException {
        PreparedStatement pstm = con.prepareStatement("delete from LinkTableMap where id=?");
        pstm.setInt(1, id);
        pstm.execute();
    }

    private static void alterAutoIncrementIndex(String tableName, long indexStart, Connection con) throws SQLException {
        StringBuffer buf = new StringBuffer("ALTER TABLE ");
        buf.append(tableName);
        buf.append(" AUTO_INCREMENT = ");
        buf.append(indexStart);
        con.createStatement().execute(buf.toString());
    }
}
