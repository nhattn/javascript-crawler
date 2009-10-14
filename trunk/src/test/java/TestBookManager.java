import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.zyd.web.core.BookManager;
import com.zyd.web.dom.Book;
import com.zyd.web.service.booklist;

public class TestBookManager extends TestCase {

    Book[] books = null;
    List<Book> bookList = null;
    BookManager bm;

    @Override
    protected void setUp() throws Exception {
        books = new Book[10];
        bookList = new ArrayList<Book>();
        bm = BookManager.getInstance();

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

    public void testAddBookList() {
        bm.clearBooks();
        int added = bm.addBookList(bookList);
        assertEquals("Add booklist", 10, added);
        for (Book b : books) {
            assertTrue(bm.hasBook(b));
        }
    }

    public void testAddSingleBook() {
        bm.clearBooks();
        for (Book b : books) {
            bm.addBook(b);
        }
        for (Book b : books) {
            assertTrue(bm.hasBook(b));
        }
    }

    public void testAddDuplicateBook() {
        bm.clearBooks();
        bm.addBookList(bookList);
        int added = bm.addBookList(bookList);
        assertEquals("Add duplicate book", 0, added);
    }
}
