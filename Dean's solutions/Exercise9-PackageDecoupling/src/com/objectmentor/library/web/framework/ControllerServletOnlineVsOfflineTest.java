package com.objectmentor.library.web.framework;

import com.objectmentor.library.application.Application;
import com.objectmentor.library.mocks.*;
import com.objectmentor.library.offline.InMemoryIsbnService;
import junit.framework.TestCase;

import javax.servlet.ServletException;
import java.io.IOException;

public class ControllerServletOnlineVsOfflineTest extends TestCase {
  private ControllerServlet servlet;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  protected void setUp() throws Exception {
    super.setUp();
    servlet = new ControllerServlet();
    response = new MockHttpServletResponse();
    request = new MockHttpServletRequest("POST", "/Library",
                                         "/Library/test/action.do");
  }

  public void testRunningOnlineByDefault() {
    assertTrue(servlet.getRunningOnline());
  }

  public void testRunningOfflineSetThroughRequestParameterWithTrueValue()
    throws Exception {
    request.setParameter("offline", "true");
    doGet();
    assertFalse(servlet.getRunningOnline());
  }

  public void testRunningOfflineSetThroughRequestParameterWithEmptyValueIsTrue()
    throws Exception {
    request.setParameter("offline", "");
    doGet();
    assertFalse(servlet.getRunningOnline());
  }

  public void testRunningOfflineSetThroughRequestParameterWithFalseSetsOnline()
    throws Exception {
    request.setParameter("offline", "false");
    doGet();
    assertTrue(servlet.getRunningOnline());
  }

  public void testRunningOnlineSetThroughRequestParameterWithTrueValue()
    throws Exception {
    request.setParameter("online", "true");
    doGet();
    assertTrue(servlet.getRunningOnline());
  }

  public void testRunningOnlineSetThroughRequestParameterWithEmptyValueIsTrue()
    throws Exception {
    request.setParameter("online", "");
    doGet();
    assertTrue(servlet.getRunningOnline());
  }

  public void testRunningOnlineSetThroughRequestParameterWithFalseSetsOffline()
    throws Exception {
    request = new MockHttpServletRequest("POST", "/Library",
                                         "/Library/test/action.do");
    request.setParameter("online", "false");
    doGet();
    assertFalse(servlet.getRunningOnline());
  }

  public void testRunningOfflineSetsMockIsbnServiceOnApplicationAndLibraryAndCatalog()
    throws Exception {
    request = new MockHttpServletRequest("POST", "/Library",
                                         "/Library/test/action.do");
    request.setParameter("offline", "");
    doGet();
    Application application = (Application) request.getSession().getAttribute(
      "Application");
    assertEquals(InMemoryIsbnService.class, application.getIsbnService()
      .getClass());
  }

  private void doGet() throws IOException {
    try {
      servlet.doGet(request, response);
    }
    catch (ServletException e) {
    }
  }

}
