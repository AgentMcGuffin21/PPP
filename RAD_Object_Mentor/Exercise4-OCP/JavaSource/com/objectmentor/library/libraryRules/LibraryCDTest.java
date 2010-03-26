package com.objectmentor.library.libraryRules;

import com.objectmentor.library.mocks.MockTimeSource;
import com.objectmentor.library.models.*;
import com.objectmentor.library.utils.DateUtil;
import junit.framework.*;

import java.util.*;

public class LibraryCDTest extends TestCase {
  private MediaCopy copy;
  private LibraryWithMockServices library;
  private Patron james;

  protected void setUp() throws Exception {
    library = new LibraryWithMockServices();
    james = new Patron(DateUtil.dateFromString("1/1/2000"));
    library.getPatronGateway().add(james);
    library.getMockCompactDiscService().setCDToReturn(new Media("The Wall", "1111", Media.COMPACT_DISC));
    copy = library.acceptCD("1111");
  }

  public void testAcceptCd() throws Exception {
    MediaCopy foundCopy = library.getMediaGateway().findCopyById(copy.getId());
    assertEquals(copy, foundCopy);
  }

  public void testReturnDateIsFiveDaysFromToday() throws Exception {
    Date today = DateUtil.dateFromString("1/1/2000");
    Date due = DateUtil.dateFromString("1/6/2000");
    TimeSource.timeSource = new MockTimeSource(today);
    LoanReceipt r = library.loan(copy.getId(), james.getId());
    assertEquals(due, r.getDueDate());
  }

  public void testFineIfReturnedOneDayLate() throws Exception {
    TimeSource.timeSource = new MockTimeSource("1/1/2000");
    library.loan(copy.getId(), james.getId());
    TimeSource.timeSource = new MockTimeSource("1/7/2000");
    ReturnReceipt rr = library.returnCopy(copy.getId());
    assertEquals(ReturnReceipt.LATE, rr.getStatus());
    Assert.assertEquals(new Money(150), rr.getFines());
  }

  public void testChargeForDamage() throws Exception {
    library.loan(copy.getId(), james.getId());
    List<ReturnCondition> conditions = new ArrayList<ReturnCondition>();
    ReturnCondition damaged = new DamagedCondition();
    conditions.add(damaged);
    ReturnReceipt rr = library.returnCopy(copy.getId(), conditions);
    assertEquals(new Money(1500), rr.getCharges().get(damaged.getConditionName()));
  }


}
