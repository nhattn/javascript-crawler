import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zyd.core.busi.TemplateManager;

public class MiscTest {
    public static void main(String[] args) {
        testLoadTemplate();
    }

    public static void testLoadTemplate() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ContextConfig.xml");
        TemplateManager man = (TemplateManager) context.getBean("templateManager");
        System.out.println(man.getTemplate("nextlink.js"));
    }
}
