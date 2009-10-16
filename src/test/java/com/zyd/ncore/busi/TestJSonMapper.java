package com.zyd.ncore.busi;

import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import com.zyd.ncore.dom.Book;
import com.zyd.ncore.dom.Chapter;

public class TestJSonMapper extends TestCase {
    JSonMapper jm;

    @Override
    protected void setUp() throws Exception {
        jm = JSonMapper.getInstance();
    }

    public void testParseBookList() {
        String s = ATestData.book4;
        String[] names = new String[] { "羽皇", "谁仙谁魔", "九龙大帝", "极品偷生记", "禁书目录之我的前身是金闪", "异世之中华神系", "鲜花插在牛鼻上", "虚拟圣骑", "异界之丧尸召唤", "战栗传说", "网游之术士三代", "王者归来之九世轮回", "星武仙途", "证魂道", "独孤剑说", "末日之丧尸横行",
                "黑色漫步", "天道记", "傲笑天涯行", "宦仕行", "三国之征服", "上位不是偶然", "传奇进化", "混阳", "幻想之神界纵横", "魔妖英雄传", "异界之萨满传奇", "穿越回归之完美", "邪灵道", "印术师传奇", "漂亮女家教", "御时", "意阕天", "墓变", "莫归路", "莫风天下", "潇洒修仙人",
                "魔兽世界之日暮之泪", "家督的野望", "双圜记", "彭氏军史评论", "混沌鼎天", "官气", "天法道", "我是仙仙仙", "一世孽障", "潜龙无影", "天书之天书一现", "篮球也是一种生活", "红书传", "狼魂之灵", "金龙之玩转异世", "杯莫停", "官路迷情", "天道玲珑", "流传", "末世降临", "三到延安",
                "极品战士", "易尘记", "大宋之风流才子", "万法修真", "道凡仙缘", "重生之凤凰传奇", "另类者", "罗宾的魔法世界", "逆命者", "天争传", "明朝眼镜店", "猎杀龙骑士", "大油戏", "再说一遍我不是女生", "炼魂牧师", "错落魂", "黑暗新纪元", "科技女皇", "地狱叛乱", "残风传说", "王子之潜龙游水",
                "练爱", "网游之纹章雷人传", "非凡校园", "重生之权情天下", "异界第一流氓剑客", "炼爱都市行", "天问降", "九品莲台", "异世重界", "守望心", "禅唱新生", "图腾骑士", "现代奇幻录", "重生之亲战隋唐", "大道之鼎", "宙神之傲世天下", "天宇山下的传奇", "兽血高校", "魔尘道", "星野茫茫", "星修破天" };
        String[] authors = new String[] { "永恒炽天使", "小火文飞", "龙昊尊", "大狂热", "画虎不成反...", "爱小说的咖啡", "北理一枝花", "圣骑", "月薪三千三", "人终究要耐...", "介果", "无影道", "偷吃豆腐", "三月梦溪", "游手好闲啊", "般般枪手", "风月先生", "闲龙舞天下", "仲秋",
                "金匕", "流氓写手", "夜的呢喃", "五大夫", "抱枕入眠", "柳之鑫", "星尘灭绝", "九天邪", "Sky天雨", "落砚书生", "一盏茶", "慕释迦", "琨宁", "云伤无心", "剑君白", "即成风", "楠瓜酒", "靖花香", "飞行者墓园", "东方胜", "燕徨", "彭志文", "秋风冷雨", "鸿蒙树",
                "最后的堕落...", "八百四千万亿", "白庆", "龙鱼", "馗神K", "凯斯文", "君鹏君", "风羽翼", "尘骏", "黄舒", "番茄酱我爱", "子润", "天宇时光", "彷徨此生", "站立的黄河", "浪漫爱人", "东方未白", "午后方晴", "记忆在回眸", "骑着蜗牛追...", "明月百年心", "即成风", "阿懒老爷",
                "恶魔强", "灵有殇", "清宵好梦", "蚂蚁菜", "狗打包子", "君若寰", "寂寞的石头", "君若寰", "补天缺", "天堂无我", "巴陵修罗", "秋之枫", "此世今生811", "梦中飞翔", "雷公藤", "半碗小米粥", "叶惊龙", "佚枫", "墨小妙", "浅浅衣", "见龙在野", "血色妖君", "啃橘子", "沧海潇潇",
                "烟云梦", "寒江秋水", "蓝豹", "忘程墨迹", "憨豆", "义撒天涯", "傻傻游江湖", "百部郎", "独奚", "凤双飞" };
        String[] links = new String[] { "http://www.qidian.com/Book/1156308.aspx", "http://www.qidian.com/Book/1289050.aspx", "http://www.qidian.com/Book/1361200.aspx",
                "http://www.qidian.com/Book/1383445.aspx", "http://www.qidian.com/Book/1387552.aspx", "http://www.qidian.com/Book/1389049.aspx", "http://www.qidian.com/Book/113446.aspx",
                "http://www.qidian.com/Book/1346598.aspx", "http://www.qidian.com/Book/1389768.aspx", "http://www.qidian.com/Book/1391683.aspx", "http://www.qidian.com/Book/1375950.aspx",
                "http://www.qidian.com/Book/1387375.aspx", "http://www.qidian.com/Book/1374236.aspx", "http://www.qidian.com/Book/1367804.aspx", "http://www.qidian.com/Book/1361752.aspx",
                "http://www.qidian.com/Book/1371191.aspx", "http://www.qidian.com/Book/1048205.aspx", "http://www.qidian.com/Book/1392745.aspx", "http://www.qidian.com/Book/1388503.aspx",
                "http://www.qidian.com/Book/1287884.aspx", "http://www.qidian.com/Book/1367882.aspx", "http://www.qidian.com/Book/1380167.aspx", "http://www.qidian.com/Book/1354118.aspx",
                "http://www.qidian.com/Book/1392322.aspx", "http://www.qidian.com/Book/1385690.aspx", "http://www.qidian.com/Book/1309242.aspx", "http://www.qidian.com/Book/1342521.aspx",
                "http://www.qidian.com/Book/1390623.aspx", "http://www.qidian.com/Book/1392742.aspx", "http://www.qidian.com/Book/1390848.aspx", "http://www.qidian.com/Book/66912.aspx",
                "http://www.qidian.com/Book/1389960.aspx", "http://www.qidian.com/Book/1377586.aspx", "http://www.qidian.com/Book/1356801.aspx", "http://www.qidian.com/Book/1375462.aspx",
                "http://www.qidian.com/Book/1368596.aspx", "http://www.qidian.com/Book/1216409.aspx", "http://www.qidian.com/Book/1243682.aspx", "http://www.qidian.com/Book/1333118.aspx",
                "http://www.qidian.com/Book/1298236.aspx", "http://www.qidian.com/Book/30994.aspx", "http://www.qidian.com/Book/1392551.aspx", "http://www.qidian.com/Book/1363420.aspx",
                "http://www.qidian.com/Book/1371440.aspx", "http://www.qidian.com/Book/1363504.aspx", "http://www.qidian.com/Book/1392226.aspx", "http://www.qidian.com/Book/1121826.aspx",
                "http://www.qidian.com/Book/1392127.aspx", "http://www.qidian.com/Book/1370786.aspx", "http://www.qidian.com/Book/1051964.aspx", "http://www.qidian.com/Book/1347686.aspx",
                "http://www.qidian.com/Book/1392208.aspx", "http://www.qidian.com/Book/1227085.aspx", "http://www.qidian.com/Book/1385737.aspx", "http://www.qidian.com/Book/1391721.aspx",
                "http://www.qidian.com/Book/1390543.aspx", "http://www.qidian.com/Book/1381751.aspx", "http://www.qidian.com/Book/1332450.aspx", "http://www.qidian.com/Book/1102847.aspx",
                "http://www.qidian.com/Book/1386013.aspx", "http://www.qidian.com/Book/1229431.aspx", "http://www.qidian.com/Book/1390273.aspx", "http://www.qidian.com/Book/1360158.aspx",
                "http://www.qidian.com/Book/1204680.aspx", "http://www.qidian.com/Book/1390146.aspx", "http://www.qidian.com/Book/1391492.aspx", "http://www.qidian.com/Book/1391976.aspx",
                "http://www.qidian.com/Book/1374366.aspx", "http://www.qidian.com/Book/1391962.aspx", "http://www.qidian.com/Book/1390605.aspx", "http://www.qidian.com/Book/1387870.aspx",
                "http://www.qidian.com/Book/1261750.aspx", "http://www.qidian.com/Book/1257268.aspx", "http://www.qidian.com/Book/1307465.aspx", "http://www.qidian.com/Book/1370779.aspx",
                "http://www.qidian.com/Book/1390428.aspx", "http://www.qidian.com/Book/1389707.aspx", "http://www.qidian.com/Book/1359538.aspx", "http://www.qidian.com/Book/1386798.aspx",
                "http://www.qidian.com/Book/1392276.aspx", "http://www.qidian.com/Book/1390022.aspx", "http://www.qidian.com/Book/1378413.aspx", "http://www.qidian.com/Book/1315829.aspx",
                "http://www.qidian.com/Book/1348829.aspx", "http://www.qidian.com/Book/1373739.aspx", "http://www.qidian.com/Book/1328396.aspx", "http://www.qidian.com/Book/1081725.aspx",
                "http://www.qidian.com/Book/1389914.aspx", "http://www.qidian.com/Book/1391984.aspx", "http://www.qidian.com/Book/1382876.aspx", "http://www.qidian.com/Book/1226369.aspx",
                "http://www.qidian.com/Book/1388512.aspx", "http://www.qidian.com/Book/1366725.aspx", "http://www.qidian.com/Book/1387353.aspx", "http://www.qidian.com/Book/1348150.aspx",
                "http://www.qidian.com/Book/1034992.aspx", "http://www.qidian.com/Book/1384475.aspx", "http://www.qidian.com/Book/96434.aspx", "http://www.qidian.com/Book/1387632.aspx",
                "http://www.qidian.com/Book/1372650.aspx" };
        String[] categories = new String[] { "都市生活", "古典仙侠", "架空历史", "奇幻修真", "动漫同人", "异界大陆", "休闲美文", "西方奇幻", "异界大陆", "东方玄幻", "虚拟网游", "东方玄幻", "古武机甲", "古典仙侠", "奇幻修真", "进化变异", "奇幻修真", "现代修真", "古典仙侠",
                "官场沉浮", "秦汉三国", "异术超能", "进化变异", "奇幻修真", "东方玄幻", "西方奇幻", "异界大陆", "职场励志", "奇幻修真", "西方奇幻", "都市生活", "异界大陆", "奇幻修真", "灵异奇谈", "恩怨情仇", "异界大陆", "奇幻修真", "虚拟网游", "外国历史", "东方玄幻", "历史传记", "古典仙侠",
                "官场沉浮", "灵异奇谈", "古典仙侠", "恩怨情仇", "异界大陆", "东方玄幻", "都市生活", "传统武侠", "东方玄幻", "异界大陆", "传统武侠", "都市生活", "奇幻修真", "西方奇幻", "亡灵骷髅", "文集评论", "异术超能", "东方玄幻", "两宋元明", "奇幻修真", "奇幻修真", "异术超能", "异术超能",
                "异界大陆", "异界大陆", "传统武侠", "两宋元明", "异界大陆", "东方玄幻", "青春校园", "西方奇幻", "都市生活", "异界大陆", "未来世界", "异术超能", "古典仙侠", "异术超能", "古典仙侠", "虚拟网游", "东方玄幻", "官场沉浮", "异界大陆", "都市生活", "东方玄幻", "奇幻修真", "异界大陆",
                "西方奇幻", "传统武侠", "虚拟网游", "西方奇幻", "架空历史", "奇幻修真", "未来世界", "异界征战", "青春校园", "东方玄幻", "东方玄幻", "异界大陆" };
        String[] totalChar = new String[] { "749122", "206542", "84288", "99288", "30327", "30576", "30712", "84538", "41840", "13090", "77827", "46882", "38154", "218712", "176111", "29862",
                "37977", "2162", "49467", "221416", "38951", "6530", "301769", "9142", "19409", "60421", "226580", "11743", "3550", "14985", "145806", "18707", "79442", "99786", "60003", "129900",
                "321291", "332481", "162504", "205522", "485364", "17401", "212929", "26013", "59470", "10215", "25002", "5152", "68511", "67949", "121675", "36973", "29116", "47950", "9513",
                "15668", "34668", "165060", "195449", "87865", "1435214", "4964", "197536", "1749757", "23237", "14361", "8611", "74482", "15034", "8392", "32389", "177270", "1026991", "33610",
                "58096", "12284", "23255", "71110", "54388", "121624", "24917", "20885", "435930", "172708", "24881", "0", "14705", "15419", "4718", "41755", "328915", "4827", "58643", "9672",
                "236701", "167363", "52190", "17740", "21872", "67620" };
        HashSet<Book> bookSet = new HashSet<Book>();
        for (int i = 0; i < names.length; i++) {
            Book book = new Book();
            book.setName(names[i]);
            book.setAuthor(authors[i]);
            book.setAllChapterUrl(links[i]);
            book.setCategory(categories[i]);
            book.setTotalChar(Integer.parseInt(totalChar[i]));
            bookSet.add(book);
        }
        List<Book> list = jm.parseBookList(s);
        assertEquals(100, list.size());
        for (Book book : list) {
            assertNotNull(bookSet.remove(book));
        }        
        assertEquals(0, bookSet.size());
    }

    public void testParseBookWithChapter() {
        String s = ATestData.bookchapters;
        Book book = jm.parseBook(s);
        assertNotNull(book);
        assertNotNull(book.getName());
        assertNotNull(book.getAuthor());
        assertTrue(book.getName().toLowerCase().indexOf("error") < 0);
        assertNotNull(book.getChapters());
        assertEquals(500, book.getChapters().size());
        for (Chapter c : book.getChapters()) {
            String url = c.getChapterUrl();
            assertNotNull(url);
            assertNotNull(c.getName());
            assertTrue(s.indexOf(url) > 0);
        }
    }

    public void testBookHash() {
        Book b1 = new Book();
        b1.setName("boo1name");
        b1.setAuthor("b1author");

        Book b2 = new Book();
        b2.setName(b1.getName());
        b2.setAuthor(b1.getAuthor());

        assertEquals(b1.hashCode(), b2.hashCode());
        assertEquals(b1, b2);

        HashSet<Book> set = new HashSet<Book>();
        set.add(b1);
        set.remove(b2);
        assertEquals(0, set.size());
    }
}
