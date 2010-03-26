package com.objectmentor.library.web.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.objectmentor.library.application.gateways.BookGateway;
import com.objectmentor.library.application.gateways.IsbnDoesNotExistException;
import com.objectmentor.library.application.models.MediaCopy;
import com.objectmentor.library.web.framework.ActionResult;

public class BooksController extends MediaController {

  private static final String MANAGEMENT_RESOURCE_PATH = "books/manage.jsp";

  protected void makeErrorStringForNewCopiesForExistingId(ActionResult result,
                                                          String id, String newCopyNumber) {
    result.appendToErrorMessage("Number of new books for ISBN \"" + id + "\" must be a valid integer. (It was \"" + newCopyNumber + "\".)");
  }

  protected void doAccept(ActionResult result, String id, int n) {
    acceptBooks(result, id, n);
  }

  protected void handleNewMediaCopiesForNewId(ActionResult result, boolean noBooksOnEntry) {
    String isbn = getParameter("newIsbn");
    try {
      int ncopies = getIntegerParameter("newIsbnNumberOfCopies", 1);
      if (ncopies <= 0) {
        result.appendToErrorMessage(makeInvalidNumberOfCopiesString());
      } else if (isValidFormFieldEntry(isbn)) {
        doAccept(result, isbn, ncopies);
      } else if (noBooksOnEntry) {
        result.appendToErrorMessage(makeInvalidIdString("ISBN"));
      }
    }
    catch (NumberFormatException e) {
      result.appendToErrorMessage(makeInvalidNumberFormatString());
    }
  }

  protected ActionResult finishMediaCopiesRequest(ActionResult result) {
    render(MANAGEMENT_RESOURCE_PATH);
    BookGateway bookGateway = getApplication().getBookGateway();
    Map isbnsAndTitles = bookGateway.findAllISBNsAndTitles();
    getRequest().setAttribute("isbnTitleMap", isbnsAndTitles);
    List isbns = bookGateway.findAllISBNs();
    Iterator iter = isbns.iterator();
    while (iter.hasNext()) {
      String isbn = (String) iter.next();
      List copies = bookGateway.findAllCopies(isbn);
      getRequest().setAttribute("copies_" + isbn, copies);
    }
    return result;
  }

  private void acceptBooks(ActionResult result, String isbn, int numberOfNewBooks) {
    if (isNullOrEmpty(isbn)) {
      handleNoIsbnSpecified(result);
      return;
    }
    try {
      List mediaCopies = getLibrary().acceptBooks(isbn, numberOfNewBooks);
      if (acceptFailed(mediaCopies, numberOfNewBooks)) {
        handleBookAcceptanceFailed(result, isbn);
      } else {
        result.appendToInfoMessage(makeSuccessfulAcceptanceResultString(isbn, mediaCopies));
      }
    }
    catch (IsbnDoesNotExistException e) {
      handleUnknownId(result, "ISBN", isbn);
    }
  }

  private void handleBookAcceptanceFailed(ActionResult result, String isbn) {
    result.appendToErrorMessage("Adding new books with ISBN \"" + isbn + "\" failed!");
  }

  private void handleNoIsbnSpecified(ActionResult result) {
    result.appendToErrorMessage("No ISBN specified!");
  }

  private String makeSuccessfulAcceptanceResultString(String isbn, List mediaCopies) {
    MediaCopy copy = (MediaCopy) mediaCopies.get(0);
    StringBuffer buff = new StringBuffer();
    buff.append("Added ")
      .append(makePluralNumberString(mediaCopies.size()))
      .append(" of book with ISBN = \"")
      .append(isbn)
      .append("\", title = \"")
      .append(copy.getMedia().getTitle())
      .append("\".");
    return buff.toString();
  }

  protected boolean acceptFailed(List mediaCopies, int expectedNumberOfNewCopies) {
    return mediaCopies == null
      || mediaCopies.size() != expectedNumberOfNewCopies;
  }

}
