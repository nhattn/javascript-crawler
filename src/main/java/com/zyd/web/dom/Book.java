package com.zyd.web.dom;

import java.util.ArrayList;
import java.util.List;

public class Book {
    public String name = null;
    public String author = null;
    public String cat1 = null;
    public String cat2 = null;
    public String allChapterLink = null;
    public String updateTime = null;
    public String hit = null;
    
    public WebSite site = null;
    public List<Chapter> chapters = new ArrayList<Chapter>();
    
}
