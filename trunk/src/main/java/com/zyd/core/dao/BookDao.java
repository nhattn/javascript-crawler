package com.zyd.core.dao;

import java.util.List;

import org.hibernate.Session;

import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.core.util.ZydException;

public class BookDao {
    /**
     * returns the new added book with an id if book is added successfully.
     * @param book
     * @return
     */
    public Book addBook(Book book) {
        book.setId(Utils.nextBookId());
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(book);
        session.getTransaction().commit();
        return book;
    }

    public Book findBookById(Book book) {
        if (book.getId() == null)
            return null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Book foundBook = (Book) session.load(Book.class, book.getId());
        return foundBook;
    }

    public Book findBookByNameAuthor(Book book) {
        if (book.getName() == null || book.getAuthor() == null)
            return null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Book where name=:name and author=:author").setParameter("name", book.getName()).setParameter("author", book.getAuthor()).list();
        session.getTransaction().commit();
        if (result.size() == 0) {
            return null;
        } else if (result.size() > 1) {
            throw new ZydException("More than one book found");
        } else {
            return (Book) result.get(0);
        }
    }

    public int deleteAllBooks() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int r = session.createQuery("delete Book").executeUpdate();
        session.getTransaction().commit();
        return r;
    }

    public List listBook(BookFilter filter) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List r = session.createQuery("from Book").setFirstResult(filter.start).setMaxResults(filter.count).list();
        session.getTransaction().commit();
        return r;
    }
}
