package com.zyd.ncore.busi;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class ATestData {

    static {
        try {
            Properties p = new Properties();
            Reader r = new InputStreamReader(ATestData.class.getResourceAsStream("/data.prop"));
            p.load(r);
            r.close();
            book1 = p.getProperty("book1");
            book2 = p.getProperty("book2");
            book2_chapters = p.getProperty("book2_chapters");
            book3 = p.getProperty("book3");
            book4 = p.getProperty("book4");
            bookchapters = p.getProperty("bookchapters");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * µÁÄ¹Ú¤³Ç
     * ·±Òôº£
     * book with several chapters. chapterList
     */
    public static String book1;

    /**
     * ºÚÆì
     * ×ÏîÎºÞ
     * book cover, without chapters.
     */
    public static String book2;

    /**
     * ºÚÆì
     * ×ÏîÎºÞ
     * book with chapters in different volumes, chapter list
     */
    public static String book2_chapters;

    /**
     * ¼ÀµìºèÃÉÖ®°®
     * ´óÎÑ
     * bookcover, might have some problem
     */
    public static String book3;

    /**
     * 1 chapter from qidian 
     * 500 chapters
     * @return
     */
    public static String book4;

    /**
     * 1 page from qidian listing
     * 100 books
     * @return
     */
    public static String bookchapters;

}
