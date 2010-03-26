package com.objectmentor.library.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.objectmentor.library.data.DataServices;
import com.objectmentor.library.models.BookCopy;
import com.objectmentor.library.models.BookTitle;
import com.objectmentor.library.models.Patron;

public class MockDataServices implements DataServices {
  public BookCopy addedBookCopy;
  private Map bookCopies = new HashMap();
  private static long lastId = 0;

  public BookCopy addCopy(BookTitle bookTitle) {
    BookCopy bookCopy = new BookCopy(bookTitle, "" + (++lastId));
    String isbn = bookTitle.getIsbn();
    List copies = (List) bookCopies.get(isbn);
    if (copies == null) {
      copies = new LinkedList();
      bookCopies.put(isbn, copies);
    }
    copies.add(bookCopy);
    addedBookCopy = bookCopy;
    return bookCopy;
  }

  public BookCopy findCopy(String isbn) {
    List copies = (List) bookCopies.get(isbn);
    if (copies != null)
      return (BookCopy) copies.get(0);
    else
      return null;
  }

  public int bookCount() {
    return bookCopies.size();
  }

  public List findAllCopies(String isbn) {
    List copies = (List) bookCopies.get(isbn);
    if (copies == null)
      return new ArrayList();
    return copies;
  }

  public boolean containsTitle(String isbn) {
    return bookCopies.containsKey(isbn);
  }

  public BookCopy findAvailableCopy(String isbn) {
    List copies = findAllCopies(isbn);
    for (int i = 0; i < copies.size(); i++) {
      BookCopy copy = (BookCopy) copies.get(i);
      if (!copy.isBorrowed())
        return copy;
    }
    return null;
  }

  public BookCopy findCopyById(String copyId) {
    Collection listsOfCopies = bookCopies.values();
    for (Iterator i = listsOfCopies.iterator(); i.hasNext();) {
      List copies = (List) i.next();
      for (int j = 0; j < copies.size(); j++) {
        BookCopy bookCopy = (BookCopy) copies.get(j);
        if (bookCopy.getId().equals(copyId))
          return bookCopy;
      }
    }
    return null;
  }
  
  public String wasLastCalledWithThisIsbn;
  Map titles = new HashMap();


  public void setBookToReturn(BookTitle title) {
    titles.put(title.getIsbn(), title);
  }

  public BookTitle findTitleByIsbn(String isbn) {
    wasLastCalledWithThisIsbn = isbn;
    return (BookTitle) titles.get(isbn);
  }
  
  Map patronMap = new HashMap();

  public int countActivePatrons() {
    return patronMap.size();
  }

  public void addPatron(Patron patron) {
    patronMap.put(patron.getId(), patron);
  }

  public Patron findPatronById(String id) {
    return (Patron) patronMap.get(id);
  }

}
