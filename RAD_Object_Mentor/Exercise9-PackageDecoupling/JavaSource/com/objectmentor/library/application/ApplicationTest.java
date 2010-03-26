package com.objectmentor.library.application;

import com.objectmentor.library.gateways.*;
import com.objectmentor.library.libraryRules.Library;
import com.objectmentor.library.mocks.*;
import com.objectmentor.library.models.Book;
import com.objectmentor.library.online.WorldCatIsbnService;
import com.objectmentor.library.services.*;
import com.objectmentor.library.web.controller.*;
import junit.framework.TestCase;

public class ApplicationTest extends TestCase {
	private MockIsbnService isbnService;

	private MockCompactDiscService compactDiscService;

	private MediaGateway mediaGateway;

	private MockComputerGateway computerGateway;

	private Library library;

	private Application application;

	protected void setUp() throws Exception {
		super.setUp();
		isbnService = new MockIsbnService();
		compactDiscService = new MockCompactDiscService();
		mediaGateway = new MockMediaGateway();
		PatronGateway patronGateway = new MockPatronGateway();
		CardPrinter cardPrinter = new MockCardPrinter();
		computerGateway = new MockComputerGateway();
		ServiceProvider serviceProvider = new TestServiceProvider(isbnService,
				compactDiscService, mediaGateway, (BookGateway) mediaGateway,
				(CompactDiscGateway) mediaGateway, patronGateway, cardPrinter,
				computerGateway);
		application = new Application(serviceProvider);
		library = application.getLibrary();
	}

	class TestServiceProvider implements ServiceProvider {
		private IsbnService isbnService;

		private CompactDiscService compactDiscService;

		private MediaGateway mediaGateway;

		private PatronGateway patronGateway;

		private CardPrinter cardPrinter;

		private ComputerGateway computerGateway;

		private final CompactDiscGateway cdGateway;

		private final BookGateway bookGateway;

		public TestServiceProvider(IsbnService isbnService,
				CompactDiscService compactDiscService, MediaGateway mediaGateway,
				BookGateway bookGateway, CompactDiscGateway cdGateway,
				PatronGateway patronGateway, CardPrinter cardPrinter,
				ComputerGateway computerGateway) {
			this.isbnService = isbnService;
			this.compactDiscService = compactDiscService;
			this.mediaGateway = mediaGateway;
			this.bookGateway = bookGateway;
			this.cdGateway = cdGateway;
			this.patronGateway = patronGateway;
			this.cardPrinter = cardPrinter;
			this.computerGateway = computerGateway;
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

		public MediaGateway getMediaGateway() {
			return mediaGateway;
		}

		public PatronGateway getPatronGateway() {
			return patronGateway;
		}

		public BookGateway getBookGateway() {
			return bookGateway;
		}

		public CompactDiscGateway getCompactDiscGateway() {
			return cdGateway;
		}

	}

	public void testGetLibraryNeverReturnsNull() {
		assertNotNull(library);
	}

	public void testGetMediaGatewayWithEmptyConstructor() {
		assertEquals(mediaGateway, application.getMediaGateway());

	}

	public void testAcceptBookThrowsIsbnDoesNotExistExceptionIfIsbnIsNull() {
		try {
			library.acceptBook(null);
			fail();
		} catch (IsbnDoesNotExistException e) {
		}
	}

	public void testAcceptBookThrowsIsbnDoesNotExistExceptionIfIsbnIsEmpty() {
		try {
			library.acceptBook("");
			fail();
		} catch (IsbnDoesNotExistException e) {
		}
	}

	public void testAcceptBookWithValidIsbn() {
		Book book = new Book("ISBN", "War and Peace", "Tolstoy");
		isbnService.setBookToReturn(book);
		library.acceptBook("ISBN");
		assertEquals(1, mediaGateway.mediaCount());
	}

	public void testUseIsbnService() {
		Application application = new Application(new MockServiceProvider());
		assertEquals(MockIsbnService.class, application.getIsbnService().getClass());
		application = new Application(new OnLineServiceProvider());
		assertEquals(WorldCatIsbnService.class, application.getIsbnService()
				.getClass());
	}

}
