package com.objectmentor.library.web.servlet;

import junit.framework.TestCase;

import com.objectmentor.library.mocks.MockHttpServletRequest;
import com.objectmentor.library.web.controller.ActionResult;
import com.objectmentor.library.web.controller.TestController;

public class ServletHelperTest extends TestCase {
  
  public void testStrippedDownURIReturnsSlashOnlyForContextPathOnly() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/Lib", "/Lib");
    assertEquals("/", ServletHelper.requestPath(request));
  }
  
  public void testStrippedDownURIReturnsSlashOnlyForContextPathPlusSlashOnly() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/Lib", "/Lib/");
    assertEquals("/", ServletHelper.requestPath(request));
  }
  
  public void testStrippedDownURIReturnsSlashPlusRootForJSP() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/Lib", "/Lib/foo.jsp");
    assertEquals("/foo", ServletHelper.requestPath(request));
  }
  
  public void testStrippedDownURIReturnsSlashPlus2DirRootForJSP() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/Lib", "/Lib/foo/bar.jsp");
    assertEquals("/foo/bar", ServletHelper.requestPath(request));
  }
  
  public void testStrippedDownURIRemovesArguments() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/Lib", "/Lib/foo/bar.jsp?abracadabra");
    assertEquals("/foo/bar", ServletHelper.requestPath(request));
  }
  
  public void testLoadAndCallClass() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET");
    ActionResult result = 
    	ServletHelper.loadAndCall("com.objectmentor.library.web.controller.TestController", request);
    assertNotNull(result);
    assertTrue(TestController.getWasCalled);
    assertSame(request, TestController.theRequest);
  }

  public void testArrayToString() {
  	assertEquals("<null>", ServletHelper.arrayToString(null));
  	assertEquals("", ServletHelper.arrayToString(new Object[0]));
  	assertEquals("1, 2", ServletHelper.arrayToString(new Integer[]{new Integer(1), new Integer(2)}));
  	assertEquals("abc, xyz", ServletHelper.arrayToString(new String[]{"abc", "xyz"}));
  	assertEquals("", ServletHelper.arrayToString(new Object[0]));
  }
}
