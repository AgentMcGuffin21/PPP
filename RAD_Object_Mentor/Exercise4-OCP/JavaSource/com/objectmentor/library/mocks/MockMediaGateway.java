package com.objectmentor.library.mocks;

import com.objectmentor.library.gateways.MediaGateway;
import com.objectmentor.library.models.*;

import java.util.*;

public class MockMediaGateway implements MediaGateway {
  public MediaCopy addedMediaCopy;
  private Map<String, List<MediaCopy>> mediaCopies = new HashMap<String, List<MediaCopy>>();
  private static long lastId = 0;

  public MediaCopy addCopy(Media media) {
    lastId += 1;
    MediaCopy mediaCopy = new MediaCopy(media, "" + lastId);
    String id = media.getId();
    List<MediaCopy> copies = mediaCopies.get(id);
    if (copies == null) {
      copies = new LinkedList<MediaCopy>();
      mediaCopies.put(id, copies);
    }
    copies.add(mediaCopy);
    addedMediaCopy = mediaCopy;
    return mediaCopy;
  }

	public List<MediaCopy> addCopies(Media book, int numberOfNewBooks) {
		ArrayList<MediaCopy> list = new ArrayList<MediaCopy>();
		for (int i=0; i<numberOfNewBooks; i++)
			list.add(addCopy(book));
		return list;
	}

  public MediaCopy findCopy(String isbn) {
    List<MediaCopy> copies = this.mediaCopies.get(isbn);
    if (copies != null)
      return (MediaCopy) copies.get(0);
    else
      return null;
  }

  public int mediaCount() {
    return mediaCopies.size();
  }

  public List<MediaCopy> findAllCopies(String isbn) {
    List<MediaCopy> copies = this.mediaCopies.get(isbn);
    if (copies == null)
      return new ArrayList<MediaCopy>();
    return copies;
  }

  public boolean contains(String id) {
    return mediaCopies.containsKey(id);
  }

  public MediaCopy findAvailableCopy(String id) {
    List<MediaCopy> copies = findAllCopies(id);
    for (int i = 0; i < copies.size(); i++) {
      MediaCopy copy = (MediaCopy) copies.get(i);
      if (!copy.isLoaned())
        return copy;
    }
    return null;
  }

  public MediaCopy findCopyById(String copyId) {
    Collection<List<MediaCopy>> listsOfCopies = mediaCopies.values();
    for (Iterator<List<MediaCopy>> i = listsOfCopies.iterator(); i.hasNext();) {
      List<MediaCopy> copies = i.next();
      for (int j = 0; j < copies.size(); j++) {
        MediaCopy mediaCopy = (MediaCopy) copies.get(j);
        if (mediaCopy.getId().equals(copyId))
          return mediaCopy;
      }
    }
    return null;
  }

	public void delete(String id) {
		mediaCopies.remove(id);
	}

	public List<String> findAllISBNs() {
		return new ArrayList<String>(mediaCopies.keySet());
	}

}
