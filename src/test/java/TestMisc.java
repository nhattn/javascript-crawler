import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import junit.framework.TestCase;

public class TestMisc extends TestCase {
    public void testReadResource() throws Exception {
        InputStream is = getClass().getResourceAsStream("/test.prop");
        Properties p = new Properties();
        Reader r = new InputStreamReader(is);
        p.load(r);
        r.close();
        System.out.println(p.get("aa"));
    }
}
