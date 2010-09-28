package com.zyd.core.objecthandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import com.zyd.core.db.HibernateUtil;

public class TrainRoute extends Handler {

    @Override
    public String getEntityName() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean beforeCreate(HashMap values) {
        throw new UnsupportedOperationException();
    }

    /**
     * does not support sorting and ordering, returns all result in one batch, will not return more than 55 result
     */
    @Override
    public SearchResult query(HashMap params) {
        String src = (String) params.get(Columns.src), dest = (String) params.get(Columns.dest);
        if (src == null || dest == null) {
            //TODO: should return null?
            return null;
        }
        ArrayList<HashMap<String, String>> r = queryRoute(src, dest);
        return new SearchResult(r, r.size(), 0, -1);
    }

    private final static ArrayList<HashMap<String, String>> queryRoute(final String src, final String dest) {
        String s = "SELECT t3.name as trainName, t3.id as trainId, t3.trainNum as trainNum, t3.origin as trainOrigin, t3.dest as trainDest, t3.totalMile as trainTotalMile, t3.totalTime as trainTotalTime, "
                + "t1.name as station1, t1.id as stationId1, t1.seq as seq1, t1.totalMile as totalMile1, t1.totalTime as totalTime1, t1.arriveAt as arriveAt1, "
                + "t2.name as station2, t2.id as stationId2, t2.seq as seq2, t2.totalMile as totalMile2, t2.totalTime as totalTime2, t2.arriveAt as arriveAt2 "
                + "from layer_com_zuiyidong_layer_trainstation t1, layer_com_zuiyidong_layer_trainstation t2, layer_com_zuiyidong_layer_train t3 "
                + "where t1.trainId = t2.trainId and t1.seq <t2.seq  and t1.trainId=t3.id and ";
        if (src.indexOf('%') != -1 || dest.indexOf('%') != -1) {
            s = s + "t1.name like ? and  t2.name like ? order by arriveAt1";
        } else {
            s = s + "t1.name = ? and  t2.name = ? order by arriveAt1";
        }

        final String sql = s;
        final ArrayList<HashMap<String, String>> r = new ArrayList<HashMap<String, String>>();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction trx = session.beginTransaction();
        try {
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement pst = connection.prepareStatement(sql);
                    pst.setString(1, src);
                    pst.setString(2, dest);
                    ResultSet rset = pst.executeQuery();
                    ResultSetMetaData meta = rset.getMetaData();
                    int columnCount = meta.getColumnCount();
                    String[] columnNames = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        columnNames[i] = meta.getColumnLabel(i + 1);
                        System.out.println(columnNames[i]);
                    }
                    while (rset.next()) {
                        HashMap<String, String> values = new HashMap<String, String>();
                        for (int i = 0; i < columnCount; i++) {
                            System.out.println(columnNames[i]);
                            values.put(columnNames[i], rset.getString(columnNames[i]));
                        }
                        r.add(values);
                    }
                }
            });
        } catch (HibernateException he) {
            trx.rollback();
            throw he;
        }
        trx.commit();
        return r;
    }

    /**
     * does not support standard columns attributes, no position info     
     */
    public class Columns extends Handler.Columns {
        public final static String src = "src";
        public final static String dest = "dest";
    }
}
