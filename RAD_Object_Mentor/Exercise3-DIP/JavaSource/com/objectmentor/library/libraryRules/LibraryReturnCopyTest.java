package com.objectmentor.library.libraryRules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.objectmentor.library.mocks.MockTimeSource;
import com.objectmentor.library.models.MediaCopy;
import com.objectmentor.library.models.Patron;
import com.objectmentor.library.models.ReturnReceipt;
import com.objectmentor.library.utils.DateUtil;
import com.objectmentor.library.web.controller.Application;

public class LibraryReturnCopyTest extends TestCase {
  private Library library;
  private MediaCopy copy;
  private Patron patron;

  protected void setUp() throws Exception {
    Application application = new Application();
    library = application.getLibrary();
    copy = library.acceptBook("0679600841"); //War and Peace
    patron = new Patron(DateUtil.dateFromString("1/1/2000"));
    patron.setId("" + application.getPatronGateway().getNextId());
    application.getPatronGateway().getPatronMap().put(patron.getId(), patron);
  }

  public void testCanReturnOnTimeBookThatWasBorrowed() throws Exception {
    library.loan(copy, patron);
    ReturnReceipt returnReceipt = library.returnCopy(copy);
    Assert.assertEquals(new Money(0), returnReceipt.getFines());
    assertEquals(copy, returnReceipt.getCopy());
    assertFalse(returnReceipt.getCopy().isLoaned());
    assertEquals(ReturnReceipt.OK, returnReceipt.getStatus());
    Map<String, Money> charges = returnReceipt.getCharges();
    assertTrue(charges.isEmpty());
  }

  public void testShouldSetStatusToUnBorrowedBookOnAttemptToReturnUnBorrowedBook() throws Exception {
    ReturnReceipt returnReceipt = library.returnCopy(copy);
    assertEquals(ReturnReceipt.UNBORROWED_BOOK, returnReceipt.getStatus());
    assertEquals(copy, returnReceipt.getCopy());
  }

  public void testShouldHaveFineAndBeLateIfOneDayLate() throws Exception {
    Date borrowDate = DateUtil.dateFromString("12/19/2006");
    Date returnDate = DateUtil.dateFromString("1/3/2007"); // fifteen days later.
    TimeSource.timeSource = new MockTimeSource(borrowDate);
    library.loan(copy, patron);
    TimeSource.timeSource = new MockTimeSource(returnDate);
    ReturnReceipt receipt = library.returnCopy(copy);

    assertEquals(ReturnReceipt.LATE, receipt.getStatus());
    Assert.assertEquals(new Money(50), receipt.getFines());
  }


  public void testShouldHaveFineAndBeLateIfTwoDaysLate() throws Exception {
    Date borrowDate = DateUtil.dateFromString("12/19/2006");
    Date returnDate = DateUtil.dateFromString("1/4/2007"); // sixteen days later.
    TimeSource.timeSource = new MockTimeSource(borrowDate);
    library.loan(copy, patron);
    TimeSource.timeSource = new MockTimeSource(returnDate);
    ReturnReceipt receipt = library.returnCopy(copy);

    assertEquals(ReturnReceipt.LATE, receipt.getStatus());
    Assert.assertEquals(new Money(100), receipt.getFines());
  }

  public void testChargeIfDamaged() throws Exception {
    library.loan(copy, patron);
    ReturnCondition damaged = new DamagedCondition();
    List<ReturnCondition> conditions = new ArrayList<ReturnCondition>();
    conditions.add(damaged);
    ReturnReceipt returnReceipt = library.returnCopy(copy, conditions);
    Map<String, Money> charges = returnReceipt.getCharges();
    String conditionName = damaged.getConditionName();
    assertTrue(charges.containsKey(conditionName));
    assertEquals(new Money(500), charges.get(conditionName));
  }


}
