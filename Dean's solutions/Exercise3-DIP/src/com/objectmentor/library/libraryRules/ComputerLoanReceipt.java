package com.objectmentor.library.libraryRules;

import com.objectmentor.library.models.Computer;
import com.objectmentor.library.models.Patron;

public class ComputerLoanReceipt {

  public static final String OK = "Ok";
  public static final String COMPUTER_NOT_AVAILABLE = "notAvailable";

  private String status;

  public ComputerLoanReceipt(Computer computer, Patron patron) {
    status = OK;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;

  }

}
