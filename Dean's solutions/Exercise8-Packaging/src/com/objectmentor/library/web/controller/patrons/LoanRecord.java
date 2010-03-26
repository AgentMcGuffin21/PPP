package com.objectmentor.library.web.controller.patrons;

import com.objectmentor.library.libraryRules.Money;

import java.util.Date;

public class LoanRecord {
  public String id;
  public String title;
  public Date dueDate;
  public Money fine;
}
