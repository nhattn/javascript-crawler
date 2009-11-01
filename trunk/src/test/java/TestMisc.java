import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Properties;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestMisc extends TestCase {
	public void testReadResource() throws Exception {
		InputStream is = getClass().getResourceAsStream("/test.prop");
		Properties p = new Properties();
		Reader r = new InputStreamReader(is);
		p.load(r);
		r.close();
		System.out.println(p.get("aa"));
	}

	public void testAA() {
		String[] s = new String[] { "现代黑道传奇录", "医典天术", "让子弹飞翔",
				"用毒高手在现代", "农妇山泉有点田", "异界之轩辕剑魂", "离魂记",
				"宅男的一亩二分地", "天行剑", "庄周之燕", "网游之枪舞天下",
				"双子、六指", "红情", "山野痞夫", "谁来爱我", "穿越之欲生欲死",
				"三场骄子", "最后一场人鬼之战", "开国功贼", "穷鬼闯天下",
				"天风", "魔美双修", "北野妖话", "九州仙侠录", "狱警日记",
				"不灭的村庄", "执能者", "爱情就在前面飞", "重生之衙内", "国策",
				"重生奇迹", "异钢", "异界吕布之最强龙骑", "酒醉良天",
				"重生1978", "强清霸世", "球场上的暴君", "兽人军士", "天道计划",
				"三国召唤师", "官路沉浮", "囚禁", "地狱召唤", "大宋海贼",
				"仙途", "纵情少年", "异世第一阵", "二流", "迷失在一六二九",
				"民国投机者", "超级学生", "云的抗日", "法师路", "破封领域",
				"异世之极乐宗师", "重生为官", "炼魂牧师", "过期情人",
				"狐狸传奇", "修正人生", "九容", "冰封乾坤", "天王",
				"网游之星际执政官", "斗横剑舞", "全能特工", "流氓老师",
				"带着军队玩穿越", "极品小公子", "官路风流", "多宝道人",
				"大清拆迁工", "重生之齐人之福", "凡人修真传", "天下第一丁",
				"仙界医生在都市", "问鼎记", "冥神在人间", "权倾天下之绝世悍将",
				"医者杀心", "修真种植大户", "一六二二", "寄生战士",
				"网游之纵横天下", "网游之大道无形", "网游之近战法师",
				"洪荒之第一神经", "创圣演武", "降临异世", "贼胆", "星际游轮",
				"无限修仙", "冒牌大英雄", "天生神匠", "重生之官路商途",
				"凡人修仙传", "小地主", "导演万岁", "王牌进化", "时空天书",
				"超级魂晶", "随身装着一口泉", "重生之控卫之王",
				"魔兽牧师在异世", "异世画魂", "贾似道的古玩人生",
				"极品小道异界纵横", "碧血大明", "不同凡想", "重生之红星传奇",
				"穿越1999", "传奇警察", "武斗者传说", "签约封神", "异能雷帝",
				"异世之白金龙", "网游江湖再会", "腾龙神剑", "最后的修仙者",
				"超级炼器", "秦歌一曲", "神龙德鲁伊", "星际之超级帝国",
				"大唐酒徒", "重生1991", "驱魔笔记", "大明官商", "明朝上门女婿",
				"异世界之宅神物语", "影剑", "大宋帝国风云录",
				"地下城魔王养成日志", "九阳神功", "符医天下", "宦海无涯",
				"长生界", "大魔王", "疯狂大地主", "流氓高手II", "穿越之弄潮者",
				"官仙", "穿越之弄潮者", "重生之商途", "命运天盘", "凌天传说",
				"人间仙路", "游戏小工之元素操控师", "纵横第二世界",
				"异界重生之打造快乐人生", "妖皇传", "独自去偷爱", "异界仙莲",
				"天命", "姐姐的弟弟叫一白", "异世枪神", "灵事警察", "憨魂",
				"网游之问天", "搅乱三国", "官 像", "神界秘史", "阳神",
				"重生之升官发财", "异行录", "欲乱我的皇兄", "特种兵痞", "霸天",
				"我的美女上司", "我的老婆是警花", "幻世修仙", "都市旷世高手",
				"狼神绝", "戏游异界", "灵魂诀", "潘多拉的夕阳",
				"命运让我爱上你", "浪迹香都", "黑暗公子哥", "紫幻修神",
				"帝王受", "封魔榜", "超极品流氓", "异侠逍遥游", "韦小宝传人",
				"圣人传奇", "太乙迷途", "混沌至尊", "网游之神话", "乱世姬情",
				"梦游八国", "魔法圣经", "五灵杀", "转运奇仙", "校魂",
				"天使恶魔变形记", "魔屠异世", "少年枭雄", "超级农民",
				"伊拉克风云", "与校花同居的日子" };
		HashSet<String> ss = new HashSet<String>();
		for (String x : ss) {
			if (ss.add(x) == false) {
				System.out.println(x);
				;
			}
		}
	}

	public void testSpringSetup() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath*:**/ContextConfig.xml");
		System.out.println(ctx.getBeanDefinitionCount());
		JdbcTemplate tmp = (JdbcTemplate) ctx.getBean("jdbcTemplate");
		while(true)
		System.out.println(tmp.queryForInt("select count(0) from book"));
		
	}
}
