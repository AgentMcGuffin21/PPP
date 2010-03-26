package com.objectmentor.library.web.controller.patrons;

import com.objectmentor.library.application.PatronRegistrar;
import com.objectmentor.library.application.libraryRules.Library;
import com.objectmentor.library.application.models.*;
import com.objectmentor.library.utils.*;
import com.objectmentor.library.web.controller.utils.ControllerTestHelper;
import com.objectmentor.library.web.framework.ActionResult;
import com.objectmentor.library.web.framework.mocks.MockHttpServletRequest;
import junit.framework.TestCase;

import java.util.*;

public class BooksController_LoanTest extends TestCase {
  private ControllerTestHelper helper;
  private MediaCopy media;
  private MockHttpServletRequest request;
  private BooksController booksController;

  protected void setUp() throws Exception {
    helper = new ControllerTestHelper();
    booksController = new BooksController();
  }

  private void makeRequest(String method, String action) {
    request = helper.createMockRequest(method);
    helper.setActionName(action);
  }

  private void makeLoanPost(String action) {
    makeRequest("POST", action);
    request.getSession().setAttribute("transaction", "loan");
  }

  public void testLoanRendersIdentifyPatron() throws Exception {
    makeRequest("POST", "loanBook");
    booksController.handle(request);
    assertTrue(helper.shouldRender("patrons/books/identifyPatron.jsp"));
    assertEquals("loan", request.getSession().getAttribute("transaction"));
  }

  public void testMatchPatronMatchesId() throws Exception {
    makeLoanPost("matchPatron");
    helper.loadLibraryWithBookAndPatron();
    Patron patron = helper.getThePatron();
    request.setParameter("patronPattern", patron.getId());
    booksController.handle(request);
    assertTrue(helper.shouldRender("patrons/books/loanToPatron.jsp"));
    assertEquals(helper.getThePatron(), request.getSession().getAttribute("patron"));
  }


  public void testMatchPatronGenerateErrorMessageIfBadId() throws Exception {
    makeLoanPost("matchPatron");
    helper.getLibrary();
    request.setParameter("patronPattern", "NoSuchPatronId");
    ActionResult result = booksController.handle(request);
    assertTrue(helper.shouldRender("patrons/books/identifyPatron.jsp"));
    assertTrue(result.getErrorMessage().length() > 0);
  }

  public void testMatchPatronRendersPatronSelectorIfMoreThanOneMatch() throws Exception {
    makeLoanPost("matchPatron");
    PatronRegistrar registrar = helper.getPatronRegistrar();
    Patron bill = new Patron("Bill", "E", "Bob", null, null);
    Patron bob = new Patron("Bob", "E", "Boy", null, null);
    registrar.registerPatron(bill);
    registrar.registerPatron(bob);
    request.setParameter("patronPattern", "B");
    booksController.handle(request);
    assertTrue(helper.shouldRender("patrons/books/patronSelector.jsp"));
    Set patrons = (Set) request.getAttribute("patrons");
    assertTrue(patrons.contains(bill));
    assertTrue(patrons.contains(bob));
  }

  public void testSelectPatron() throws Exception {
    makeLoanPost("selectPatron");
    Date today = DateUtil.dateFromString("1/5/2006");
    TimeSource.timeSource = new MockTimeSource(today);
    helper.loadLibraryWithBookAndPatron();
    Patron patron = helper.getThePatron();
    request.setParameter("selectedPatron", patron.getId());
    ActionResult result = booksController.handle(request);
    assertTrue(helper.shouldRender("patrons/books/loanToPatron.jsp"));
    assertEquals(patron, request.getSession().getAttribute("patron"));
    assertAttributeEquals(today, "date");
    assertTrue(result.getInfoMessage().length() == 0);
    assertTrue(result.getWarningMessage().length() == 0);
    assertTrue(result.getErrorMessage().length() == 0);
  }

  public void testSelectPatronWithSomeLoanedBooks() throws Exception {
    makeLoanPost("selectPatron");
    Date today = DateUtil.dateFromString("1/5/2006");
    TimeSource.timeSource = new MockTimeSource(today);
    Library lib = helper.loadLibraryWithBookAndPatron();
    Patron patron = helper.getThePatron();
    request.setParameter("selectedPatron", patron.getId());
    MediaCopy copy = helper.getTheMediaCopy();
    LoanReceipt r = lib.loan(copy, patron);
    ActionResult result = booksController.handle(request);
    List loanRecords = (List) request.getAttribute("loanRecords");
    assertEquals(1, loanRecords.size());
    LoanRecord loanRecord = (LoanRecord) loanRecords.get(0);
    assertEquals(copy.getMedia().getTitle(), loanRecord.title);
    assertEquals(r.getDueDate(), loanRecord.dueDate);
    assertEquals(new Money(0), loanRecord.fine);
    assertTrue(result.getInfoMessage().length() == 0);    // TODO shouldn't be empty
    assertTrue(result.getWarningMessage().length() == 0);
    assertTrue(result.getErrorMessage().length() == 0);
  }


  private void assertAttributeEquals(Object expected, String parameter) {
    assertEquals(expected, request.getAttribute(parameter));
  }


  public void testCheckOutExistingBook() throws Exception {
    finishSetupWithBookThatCanBeBorrowed();
    booksController.handle(request);
    LoanReceipt receipt = booksController.getReceipt();
    assertEquals(LoanReceipt.OK, receipt.getStatus());
    assertEquals(helper.getThePatron(), receipt.getBorrower());
  }

  public void testCheckOutFailsIfBookAlreadyBorrowed() throws Exception {
    finishSetupWithBookThatCanBeBorrowed();
    booksController.handle(request);
    ActionResult result = booksController.handle(request);  // repeat!
    assertTrue(result.getErrorMessage().length() > 0);
    LoanReceipt receipt = booksController.getReceipt();
    assertEquals(LoanReceipt.ALREADY_BORROWED, receipt.getStatus());
  }

  public void testCheckOutFailsIfBookDoesNotExist() throws Exception {
    finishSetupWithBookThatCanBeBorrowed();
    request.setParameter("copyId", "0");
    ActionResult result = booksController.handle(request);

    assertTrue(result.getErrorMessage().length() > 0);
  }

  public void testCheckOutFailsIfBookCannotBeBorrowed() throws Exception {
    finishSetupWithBookThatCannotBeBorrowed();
    ActionResult result = booksController.handle(request);
    assertTrue(result.getErrorMessage().length() > 0);
  }

  private void finishSetupWithBookThatCanBeBorrowed() {
    makeLoanPost("loanCopy");
    helper.loadLibraryWithBookAndPatron();
    media = helper.getTheMediaCopy();
    request.setParameter("copyId", media.getId());
    request.getSession().setAttribute("patron", helper.getThePatron());
  }

  private void finishSetupWithBookThatCannotBeBorrowed() {
    makeLoanPost("loanCopy");
    helper.loadLibraryWithBookAndPatronWhereBookCannotBeBorrowed();
    media = helper.getTheMediaCopy();
    request.setParameter("copyId", "-1");
    request.setParameter("borrowerId", helper.getThePatron().getId());
    request.getSession().setAttribute("patron", helper.getThePatron());
  }

}
