import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.zyd.web.core.LinkManager;
import com.zyd.web.dom.Book;

public class TestLinkManager extends TestCase {

    Book[] books = null;
    List<Book> bookList = null;
    LinkManager lm;

    @Override
    protected void setUp() throws Exception {
        books = new Book[10];
        bookList = new ArrayList<Book>();
        lm = LinkManager.getInstance();

        for (int i = 0; i < 10; i++) {
            Book b = new Book();
            b.author = "书的作者" + i;
            b.name = "名称" + i;

            b.cat1 = "大分类1";
            b.cat1 = "大分类2";

            b.tempLink = "http://www.qidian.com/booklink.aspx?booklinkid=" + i;
            books[i] = b;
            bookList.add(b);
        }
    }

    public void testAddLinks() {
        lm.clearLinks();
        for (Book b : books) {
            lm.addLink(b.tempLink);
        }
        assertEquals("Adding link", 10, lm.getSize());
    }

    public void testAddDuplicateLink() {
        lm.clearLinks();
        for (Book b : books) {
            lm.addLink(b.tempLink);
        }
        for (Book b : books) {
            lm.addLink(b.tempLink);
        }
        assertEquals("Adding link", 10, lm.getSize());
    }

    public void testNextLink() {
        lm.clearLinks();
        for (Book b : books) {
            lm.addLink(b.tempLink);
        }
        for (int i = 0; i < 10; i++) {
            assertNotNull("Get Next Link", lm.nextLink());
        }
        assertEquals("Link empty", 0, lm.getSize());
    }

}
