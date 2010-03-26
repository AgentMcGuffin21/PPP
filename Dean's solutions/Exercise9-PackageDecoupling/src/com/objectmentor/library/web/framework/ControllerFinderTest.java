package com.objectmentor.library.web.framework;

import com.objectmentor.library.mocks.MockHttpServletRequest;
import junit.framework.TestCase;

public class ControllerFinderTest extends TestCase {

  private ControllerFinder finder;

  protected void setUp() throws Exception {
    finder = new ControllerFinder("some.path");
  }

  public void testShouldHandleNameInSubDirectory() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/Library", "/Library/foo/bar.do");
    assertEquals("some.path.FooController", finder.controllerClassName(request));
  }

  public void testShouldHandleNameIn2ndSubDirectory() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/Library", "/Library/foo/bar/jones.do");
    assertEquals("some.path.foo.BarController", finder.controllerClassName(request));
  }

}
