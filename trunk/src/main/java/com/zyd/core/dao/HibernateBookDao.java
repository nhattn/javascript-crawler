package com.zyd.core.dao;

import java.util.List;

import org.hibernate.Session;

import com.zyd.core.Utils;
import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.Book;
import com.zyd.core.dom.BookFilter;
import com.zyd.core.dom.Chapter;
import com.zyd.core.util.ZydException;

public class HibernateBookDao {
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

    public Book updateBook(Book book) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.update(book);
        session.getTransaction().commit();
        return book;
    }

    public Book findBookById(Book book, boolean loadChapter) {
        if (book.getId() == null)
            return null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Book foundBook = (Book) session.load(Book.class, book.getId());
        if (loadChapter && foundBook != null) {
            foundBook.getChapters().size();
        }
        session.getTransaction().commit();
        return foundBook;
    }

    public Book findBookByNameAuthor(Book book, boolean loadChapter) {
        if (book.getName() == null || book.getAuthor() == null)
            return null;
        Book r = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Book where name=:name and author=:author").setParameter("name", book.getName()).setParameter("author", book.getAuthor()).list();
        if (result.size() == 0) {
        } else if (result.size() > 1) {
            throw new ZydException("More than one book found");
        } else {
            r = (Book) result.get(0);
        }
        if (r != null && loadChapter == true) {
            r.getChapters().size();
        }
        session.getTransaction().commit();
        return r;
    }

    public int deleteAllBooks() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int r = session.createQuery("delete Book").executeUpdate();
        session.getTransaction().commit();
        return r;
    }

    public int deleteAllChapters() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        int r = session.createQuery("delete Chapter").executeUpdate();
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

    /**
     * book has to have an id, chapter need to have a name.
     * @param book
     * @param chapter
     * @param whether name is exact name
     * @return
     */
    public List<Chapter> findChapterInBookByName(Book book, Chapter chapter, boolean useLike) {
        String hql, name;
        if (useLike) {
            hql = "from Chapter c where c.book = :book and c.name like :name";
            name = "%" + chapter.name + "%";
        } else {
            hql = "from Chapter c where c.book = :book and c.name = :name";
            name = chapter.name;
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List list = (List<Chapter>) session.createQuery(hql).setParameter("book", book).setParameter("name", name).list();
        session.getTransaction().commit();
        return list;
    }

    /**
     * book has to have an id, or is already persistent with hibernate, 
     * chapter need to have an name at least.
     * @param book
     * @param chapter
     * @return
     */
    public Chapter addChapterToBook(Book book, Chapter chapter) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Book b = (Book) session.load(Book.class, book.getId());
        chapter.setId(Utils.nextChapterId());
        chapter.setSequence(0);
        b.addChapter(chapter);
        session.getTransaction().commit();
        return chapter;
    }
}
