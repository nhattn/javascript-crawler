import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zyd.Constants;
import com.zyd.core.busi.TemplateManager;

public class MiscTest extends TestCase {
	public static void main(String[] args) {
		// testLoadConstants();
		System.out.println(System.getProperty("java.lib.path"));
		System.out.println(Charset.defaultCharset());
	}

	public void testLoadConstants() {
		System.out.println(new Constants());
	}

	public void testLoadTemplate() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("ContextConfig.xml");
		TemplateManager man = (TemplateManager) context.getBean("templateManager");
		System.out.println(man.getTemplate("nextlink.js"));
	}

	public void testReadChinse() throws IOException {
		System.out.println(IOUtils.toString(new FileInputStream("/y/workspace/webcrawl/src/test/resources/house1.prop"), "UTF-8"));
	}

}
