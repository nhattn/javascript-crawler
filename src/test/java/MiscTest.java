import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zyd.Constants;
import com.zyd.core.busi.TemplateManager;

public class MiscTest extends TestCase {
    public static void main(String[] args) {
        //        testLoadConstants();
        System.out.println(System.getProperty("java.lib.path"));
    }

    public void testLoadConstants() {
        System.out.println(new Constants());
    }

    public void testLoadTemplate() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ContextConfig.xml");
        TemplateManager man = (TemplateManager) context.getBean("templateManager");
        System.out.println(man.getTemplate("nextlink.js"));
    }
}
