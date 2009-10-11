package com.zyd.web.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zyd.web.dom.Chapter;

public class ChapterManager {
    private static ChapterManager instance = new ChapterManager();

    private static HashMap<String, Chapter> chapterCache = new HashMap<String, Chapter>();
    private static List<Chapter> chapterList = new ArrayList<Chapter>();

    private ChapterManager() {
    }

    public static ChapterManager getInstance() {
        return instance;
    }

    public void clearChapters() {
        chapterCache.clear();
    }      
    
    

}
