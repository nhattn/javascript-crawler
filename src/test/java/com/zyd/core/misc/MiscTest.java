package com.zyd.core.misc;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.zyd.ATestUtil;
import com.zyd.core.Utils;
import com.zyd.core.busi.TemplateManager;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.objecthandler.House;
import com.zyd.core.util.Ocr;
import com.zyd.core.util.SpringContext;

public class MiscTest extends TestCase {

    public void testLoadTemplate() {
        ATestUtil.setUpSpring();
        TemplateManager man = (TemplateManager) SpringContext.getContext().getBean("templateManager");
        String s = man.getTemplate("nextlink.js");
        assertNotNull(s);
        assertTrue(s.length() > 0);
    }

    public void testGetShortestDomain() {
        String[] domains = new String[] { "http://www.abc.com", "abc.com", "http://www.abc.com.cn", "abc.com.cn", "http://abc.com", "abc.com",
                "http://abc.com.cn", "abc.com.cn", "http://ww.abc.com.cn", "ww.abc.com.cn", "http://ww.abc.com.cn:8010", "ww.abc.com.cn:8010",
                "http://127.0.0.1:8080/refererurl_0", "127.0.0.1:8080", "ftp://www.abc.com.cn", null, "http://29xdomain.com/s1002172", "29xdomain.com",
                "http://9xdomain.com/s1002172", "9xdomain.com" };
        for (int i = 0; i < domains.length; i++) {
            assertEquals(domains[i + 1], Utils.getShortestDomain(domains[i]));
            i++;
        }
    }

    public void testGetDomain() {
        String[] domains = new String[] { "http://www.abc.com", "abc.com", "http://www.abc.com.cn", "abc.com.cn", "http://abc.com", "abc.com",
                "http://abc.com.cn", "abc.com.cn", "http://ww.abc.com.cn", "abc.com.cn", "http://ww.abc.com.cn:8010", "abc.com.cn", "ftp://www.abc.com.cn",
                null, "http://29xdomain.com/s1002172", "29xdomain.com", "http://9xdomain.com/s1002172", "9xdomain.com", "http://tj.ganji.com/index.ht",
                "ganji.com", "http://tj.ganji.com.cn/index.html", "ganji.com.cn" };
        for (int i = 0; i < domains.length; i++) {
            assertEquals(domains[i], domains[i + 1], Utils.getDomain(domains[i]));
            i++;
        }
    }

    public final static String number13482172316 = "iVBORw0KGgoAAAANSUhEUgAAALAAAAAaCAYAAAAXMNbWAAAD3klEQVR4nO1bPZLqMAzea70rvI4TUHMADpA6J0jJESjeUNEys8WmTckwNFvSpsqrzHiFJOvPLMy4ULEbO/6k77Ms2+FjnuelWbN3tQ/u4TDtltVxvfz59/duVNvL7boM027ZnLY/+qyO66Ub++Xz+0sN7nK7PozPYchxaDHk+POxNqftMkw7FW5N3OZ5fvBPYlHYo3nT+g6x7M8HFItYwJgASmD254Mo6N3Yq4LRjb0YgxQHxHC5Xclxclsd16IJoImbV8Be7FG8eXyXYFEJmALBgfn8/noYkHqPNJvBd2oxpLGGaffw//358CP4WMCoQF5uV3R8S9zyviXDCPVij+LN4zuXqJJtTlu9gFfHNUo+BwLOcixDcLMJwwGXRQoDbJeLFM5uiCEtWXmfRDAkh8pG1rhJDAo1x+nFHsGbx3dMvAkTlSxYAaeslf72EoFliVKfPAjY0oL1KbXhnnOBKom/VtwokuH4Edi9vFl9x1YAbc3NbuJqCLgUxLx9amsRMCTWSqQ1BlEC5rJvTRxa3ixjwhXCstGvKmBsGSuBzEuBNKslGGAJkWcEmIm0pwq/KWAu+9bCYeFNOybMvt3Y30uXVBZ1Y1+csKECTo5jRT1Wp0HLhZaTZQlICgoUL7chkLxX2j9CwDALaieeFLuXN4vvsFbGauccN1UqhQuYAlCawdxSKcWAZQ4oaK2IuM1hbQF7s68Uu4c3q+/Y5pwzavI9RcCSmZQ7BMmSYiidi6ZdspQIT/b2ChjGUpt9Ndg9vFl9xxLNMO2Wy+16v9CAz7EJWK0Gzm9VoIhgMCBYCFSCAdtxD9MOFbREiBip0SRyxk3omtg1vHl8l4gTlhUYb9VPIeb58awPLuXYQT11iJ8/T06Xaj3sSprLaFh77VLqiZsn+0Zgl/IWKWDKl1K7pwi4dCTDLV+cJQHDbIERhp05SgWg3cR44wb90WTPCOxS3jy+S7+XeAkBl97jFbAUY6ldpACscbNm32jxevyQ9JFMUskEeoqAPTdCEgzwuSUY8ATDcisUETdL9o3G7uVN4rvkbB62wUqYEAGnq0TsTh0734u+SIC1Glbsc/UcVl5oNmxRArZkXw/2WrxJfedqdWktHyJgahMG+1Li8mKgvsrqxh79zC8nmfqMj/syrJaAtdnXi70Wb1LfMfyb0xY9I6Y2kG4BY+KhzHKRIA1I6RKDylDaA3WpGLV9sKtVreA12GvypvHd8g13qIBTMKiv+tOvAjxLsjYg1K8LsE3NqwjYUvt6sdfiTeu79Vc0IgE3a/bK1gTc7K2tCbjZW1sTcLO3tibgZm9tTcDN3tr+Ay02ZsKz6ivBAAAAAElFTkSuQmCC";
    public final static String cntext = "你应该看到这段中文文";

    public void testOcrSetup() throws Exception {
        assertEquals("13482172316", Ocr.ocrImageNumber(number13482172316, "png"));
    }

    public void testChinese() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        HashMap values = new HashMap();
        values.put(House.Columns.SubRentalType, cntext);
        session.save("House", values);
        tx.commit();

        List list = session.createQuery("from House").list();
        assertTrue(list.size() > 0);

        boolean have = false;
        for (Object o : list) {
            HashMap m = (HashMap) o;
            String s = (String) m.get(House.Columns.SubRentalType);
            if (cntext.equals(s)) {
                have = true;
                break;
            }
        }
        assertTrue(have);
    }

    public void testHibernateRollback() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        HashMap values = new HashMap();
        boolean hasError = false;
        try {
            values.put(House.Columns.SubRentalType, "dkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            session.save("House", values);
            tx.commit();
        } catch (Exception e) {
            hasError = true;
        }
        assertTrue(hasError);

        session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
        values = new HashMap();
        values.put(House.Columns.SubRentalType, cntext);
        session.save("House", values);
        tx.commit();
    }

}
