package com.objectmentor.library.web.controller;

import com.objectmentor.library.gateways.BookGateway;
import com.objectmentor.library.gateways.CompactDiscGateway;
import com.objectmentor.library.gateways.ComputerGateway;
import com.objectmentor.library.gateways.MediaGateway;
import com.objectmentor.library.gateways.PatronGateway;
import com.objectmentor.library.mocks.CardPrinter;
import com.objectmentor.library.offline.InMemoryCardPrinter;
import com.objectmentor.library.offline.InMemoryCompactDiscService;
import com.objectmentor.library.offline.InMemoryComputerGateway;
import com.objectmentor.library.offline.InMemoryIsbnService;
import com.objectmentor.library.offline.InMemoryMediaGateway;
import com.objectmentor.library.offline.InMemoryPatronGateway;
import com.objectmentor.library.services.CompactDiscService;
import com.objectmentor.library.services.IsbnService;

/**
 * Construct Application with this ServiceProvider if running
 * the app off-line.  
 */
public class OffLineServiceProvider implements ServiceProvider {
  
  private CardPrinter cardPrinter;
  private CompactDiscService compactDiscService;
  private ComputerGateway computerGateway;
  private IsbnService isbnService;
  private InMemoryMediaGateway mediaGateway;
  private PatronGateway patronGateway;
 
  public OffLineServiceProvider() {
    cardPrinter = new InMemoryCardPrinter();
    compactDiscService = new InMemoryCompactDiscService();
    computerGateway = new InMemoryComputerGateway();
    isbnService = new InMemoryIsbnService();
    patronGateway = new InMemoryPatronGateway();
    mediaGateway = new InMemoryMediaGateway();
  }
  
  public CardPrinter getCardPrinter() {
    return cardPrinter;
  }

  public CompactDiscService getCompactDiscService() {
    return compactDiscService;
  }

  public ComputerGateway getComputerGateway() {
    return computerGateway;
  }

  public IsbnService getIsbnService() {
    return isbnService;
  }

  public PatronGateway getPatronGateway() {
    return patronGateway;
  }

  public MediaGateway getMediaGateway() {
  	return mediaGateway;
  }

  public BookGateway getBookGateway() {
		return mediaGateway;
	}

	public CompactDiscGateway getCompactDiscGateway() {
		return mediaGateway;
	}

}
