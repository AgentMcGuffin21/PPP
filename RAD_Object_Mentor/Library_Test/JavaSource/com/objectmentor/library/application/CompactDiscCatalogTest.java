package com.objectmentor.library.application;

import junit.framework.TestCase;

import com.objectmentor.library.application.gateways.CdDoesNotExistException;
import com.objectmentor.library.application.models.CompactDisc;
import com.objectmentor.library.application.models.MediaCopy;
import com.objectmentor.library.mocks.MockCompactDiscService;
import com.objectmentor.library.mocks.MockMediaGateway;

public class CompactDiscCatalogTest extends TestCase {

  private MockMediaGateway mockMediaGateway;
  private CompactDiscCatalog catalog;
  private MockCompactDiscService compactDiscService;

  protected void setUp() throws Exception {
    compactDiscService = new MockCompactDiscService();
    mockMediaGateway = new MockMediaGateway();
    catalog = new CompactDiscCatalog(compactDiscService, mockMediaGateway);
  }

  public void testAddBookShouldLookUpCDBarCode() {
    addCDCopy("barCode");
    assertEquals("barCode", compactDiscService.barCodeLastPassedToFind);
  }

  public void testAddBookShouldAddCDReturnedByCDService() {
    MediaCopy mediaCopy = addCDCopy("barCode");
    assertEquals(mockMediaGateway.addedMediaCopy.getMedia(), mediaCopy
      .getMedia());
  }

  public void testUnfoundCD() {
    try {
      catalog.addCDCopy("NON-EXISTENT BarCode");
      fail();
    }
    catch (CdDoesNotExistException e) {
    }
  }

  private MediaCopy addCDCopy(String barCode) {
    compactDiscService.setCDToReturn(new CompactDisc(barCode, "Double Rainbow", "Joe Henderson"));
    return catalog.addCDCopy(barCode);
  }

}
