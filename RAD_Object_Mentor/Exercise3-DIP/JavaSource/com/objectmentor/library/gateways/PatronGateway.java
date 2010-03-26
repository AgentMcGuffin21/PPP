package com.objectmentor.library.gateways;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.objectmentor.library.models.Patron;

public class PatronGateway {

  private static int lastId = 0;
  Map<String, Patron> patrons = new HashMap<String, Patron>();

  protected static class PatronComparatorById implements Comparator<Patron> {

    public int compare(Patron p0, Patron p1) {
      return p0.getId().compareTo(p1.getId());
    }
  }

  public int getNextId() {
	  return ++lastId;
  }
  
  public Map<String, Patron> getPatronMap() {
	  return patrons;
  }

  public PatronGateway() {
    super();
  }

  public List<Patron> getPatronList() {
    ArrayList<Patron> list = new ArrayList<Patron>(patrons.values());
    Collections.sort(list, new PatronComparatorById());
    return list;
  }

}