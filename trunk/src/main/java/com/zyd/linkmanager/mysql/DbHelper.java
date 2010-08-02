package com.zyd.linkmanager.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import com.mysql.jdbc.MysqlErrorNumbers;
import com.mysql.jdbc.Statement;
import com.zyd.core.db.HibernateUtil;
import com.zyd.linkmanager.Link;

public class DbHelper {
    private static Logger logger = Logger.getLogger(DbHelper.class);
    public final static String LinkTablePrefix = "LinkTable_";

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
    public final static LinkTableInfo createLinkTable(final String tableStringUid) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        final LinkTableInfo info = new LinkTableInfo();
        info.tableUid = tableStringUid;
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    LinkTableInfo ninfo = addLinkTableInfo(info, connection);
                    info.id = ninfo.id;
                    String tableName = LinkTablePrefix + Integer.toString(info.id);
                    createLinkTable(tableName, connection);
                    for (int i = 0; i < LinkTableIndexColumns.length; i++) {
                        String indexCol = LinkTableIndexColumns[i];
                        createIndexOnTable(tableName, indexCol, tableName + "_index_" + indexCol, connection);
                    }
                    alterAutoIncrementIndex(tableName, info.id * TableIdSuffix, connection);
                }
            });
        } catch (Exception e) {
            trx.rollback();
            return null;
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
    public static LinkTableInfo getLinkTableInfoByUid(String tableStringUid, Connection con) throws SQLException {
        LinkTableInfo r = null;
        PreparedStatement pstm = con.prepareStatement("select id from LinkTableMap where uid=?");
        pstm.setString(1, tableStringUid);
        ResultSet rset = pstm.executeQuery();
        if (rset.next() != false) {
            r = new LinkTableInfo();
            r.tableUid = tableStringUid;
            r.id = rset.getInt(1);
        }
        rset.close();
        return r;
    }

    public final static Link addNewLinkToTable(String tableName, String url, Connection con) throws Exception {
        Link link = new Link();
        PreparedStatement pst = con.prepareStatement("insert into " + tableName + " values(url, createTime, tryCount, state) values(?,?,?,?)", java.sql.Statement.RETURN_GENERATED_KEYS);
        Date date = new Date();
        java.sql.Timestamp time = new Timestamp(date.getTime());
        link.setCreateTime(date);
        link.setState(Link.STATE_NOT_PROCESSED);
        link.setUrl(url);

        pst.setString(1, url);
        pst.setTimestamp(2, time);
        pst.setInt(3, 0);
        pst.setInt(4, 0);
        pst.executeUpdate();
        ResultSet r = pst.getGeneratedKeys();
        r.next();
        link.setId(r.getLong(1));
        return link;
    }

    public final static Link nextUnprocessedLink(String tableName, Connection con) throws SQLException {
//        con.prepareStatement("select id, url , createTime, finishTime, tryCount, state from "+tableName+" where state = "+);
        return null;
    }

    private final static String tableSql = "create table $TableName$ (id bigint NOT NULL AUTO_INCREMENT,url varchar(1000),createTime timestamp,finishTime timestamp,tryCount tinyint, state tinyint, PRIMARY KEY (id))";

    private static void createLinkTable(String tableName, Connection con) throws SQLException {
        String sql = tableSql.replace("$TableName$", tableName);
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
        PreparedStatement pstm = con.prepareStatement("insert into LinkTableMap(uid) values(?)");
        pstm.setString(1, info.tableUid);
        pstm.execute();
        return getLinkTableInfoByUid(info.tableUid, con);
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

    private static void executeSql(String sql, Connection con) throws SQLException {
        con.createStatement().execute(sql);
    }
}
