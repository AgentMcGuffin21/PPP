package com.objectmentor.library.offline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.objectmentor.library.gateways.MediaGateway;
import com.objectmentor.library.models.Book;
import com.objectmentor.library.models.CompactDisc;
import com.objectmentor.library.models.LoanReceipt;
import com.objectmentor.library.models.Media;
import com.objectmentor.library.models.MediaCopy;

public class InMemoryMediaGateway implements MediaGateway {

	private Map<String, List> mediaCopies = new HashMap<String, List>();
	private long lastCopyId = 0;

	public InMemoryMediaGateway() {
		super();
	}

	public MediaCopy addCopy(Media media) {
		lastCopyId += 1;
		MediaCopy mediaCopy = new MediaCopy(media, "" + lastCopyId);
		String mediaId = media.getId();
		List<MediaCopy> copies = mediaCopies.get(mediaId);
		if (copies == null) {
			copies = new LinkedList<MediaCopy>();
			mediaCopies.put(mediaId, copies);
		}
		copies.add(mediaCopy);
		return mediaCopy;
	}

	protected String getLastId() {
		return "" + lastCopyId;
	}

	public List<MediaCopy> addCopies(Media media, int numberOfNewCopies) {
		ArrayList<MediaCopy> list = new ArrayList<MediaCopy>();
		for (int i = 0; i < numberOfNewCopies; i++)
			list.add(addCopy(media));
		return list;
	}

	public MediaCopy findBookCopy(String isbn) {
		List copies = this.mediaCopies.get(isbn);
		if (copies != null) {
			for (Iterator i = copies.iterator(); i.hasNext();) {
				MediaCopy copy = (MediaCopy) i.next();
				if (copy.isBookCopy())
					return copy;
			}
			return null;
		} else
			return null;
	}

	public MediaCopy findCdCopy(String barCode) {
		List copies = this.mediaCopies.get(barCode);
		if (copies != null) {
			for (Iterator i = copies.iterator(); i.hasNext();) {
				MediaCopy copy = (MediaCopy) i.next();
				if (copy.isCdCopy())
					return copy;
			}
			return null;
		} else
			return null;
	}

	public List<LoanReceipt> findAllLoanReceiptsFor(String patronId) {
		List<LoanReceipt> receipts = new LinkedList<LoanReceipt>();
		Collection<List> copyLists = mediaCopies.values();
		for (Iterator<List> i = copyLists.iterator(); i.hasNext();) {
			List copyList = i.next();
			for (int j = 0; j < copyList.size(); j++) {
				MediaCopy mediaCopy = (MediaCopy) copyList.get(j);
				LoanReceipt receipt = mediaCopy.getLoanReceipt();
				if (receipt != null
						&& receipt.getBorrower().getId().equals(patronId))
					receipts.add(receipt);
			}
		}
		return receipts;
	}

	public void clear() {
		lastCopyId = 0;
		mediaCopies.clear();
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
		List copies = findAllCopies(id);
		for (int i = 0; i < copies.size(); i++) {
			MediaCopy copy = (MediaCopy) copies.get(i);
			if (!copy.isLoaned())
				return copy;
		}
		return null;
	}

	public MediaCopy findCopyById(String copyId) {
		Collection<List> listsOfCopies = mediaCopies.values();
		for (Iterator<List> i = listsOfCopies.iterator(); i.hasNext();) {
			List copies = i.next();
			for (int j = 0; j < copies.size(); j++) {
				MediaCopy mediaCopy = (MediaCopy) copies.get(j);
				if (mediaCopy.getId().equals(copyId))
					return mediaCopy;
			}
		}
		return null;
	}

	public void delete(MediaCopy copy) {
		for (Iterator<List> iter = mediaCopies.values().iterator(); iter
				.hasNext();) {
			List list = iter.next();
			if (list.remove(copy)) {
				return;
			}
		}
	}

	public List<String> findAllISBNs() {
		return findAllKeysForValuesOfType(Book.class);
	}

	public int copyCount() {
		int count = 0;
		for (Iterator<List> iter = mediaCopies.values().iterator(); iter
				.hasNext();) {
			List list = iter.next();
			count += list.size();
		}
		return count;
	}

	public Map<String, String> findAllISBNsAndTitles() {
		List<String> isbns = findAllKeysForValuesOfType(Book.class);
		Map<String, String> map = new HashMap<String, String>();
		for (Iterator<String> iter = isbns.iterator(); iter.hasNext();) {
			String isbn = iter.next();
			List copies = mediaCopies.get(isbn);
			if (copies.size() > 0) {
				String title = ((MediaCopy) copies.get(0)).getMedia()
						.getTitle();
				map.put(isbn, title);
			}
		}
		return map;
	}

	public List<List> findAllCDs() {
		return findAllCollectionsOfType(CompactDisc.class);
	}

	protected List<String> findAllKeysForValuesOfType(Class type) {
		List<String> list = new ArrayList<String>();
		for (Iterator<String> iter = mediaCopies.keySet().iterator(); iter
				.hasNext();) {
			String key = iter.next();
			Media media = getFirstInList(mediaCopies.get(key));
			if (media != null && media.getClass().equals(type))
				list.add(key);
		}
		return list;
	}

	protected List<List> findAllCollectionsOfType(Class type) {
		List<List> list = new ArrayList<List>();
		List<String> keys = findAllKeysForValuesOfType(type);
		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String key = iter.next();
			list.add(mediaCopies.get(key));
		}
		return list;
	}

	protected Media getFirstInList(List list) {
		return list.size() > 0 ? ((MediaCopy) list.get(0)).getMedia() : null;
	}

	public List findAllCopies() {
		List allCopies = new ArrayList();
		for (Iterator<List> i = mediaCopies.values().iterator(); i.hasNext();) {
			List copies = i.next();
			allCopies.addAll(copies);
		}
		return allCopies;
	}

}