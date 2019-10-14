package indi.scw.book.service.impl;

import indi.scw.book.pojo.Book;
import indi.scw.book.pojo.BookChannel;
import indi.scw.book.pojo.BookChannelConfig;
import indi.scw.book.pojo.Chapter;
import indi.scw.book.pojo.PageList;
import indi.scw.book.service.BookChannelService;
import indi.scw.book.service.BookService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.beans.annotation.Service;
import scw.core.Init;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.CloneUtils;
import scw.core.utils.ConfigUtils;

@Service
public class BookChannelServiceImpl implements BookChannelService, Init {
	private Map<Integer, BookService> bookServiceMap = new HashMap<Integer, BookService>();
	private Map<Integer, BookChannel> bookChannelMap = new LinkedHashMap<Integer, BookChannel>();

	public void init() {
		List<BookChannelConfig> list = ConfigUtils.xmlToList(
				BookChannelConfig.class, "classpath:/book-channel.xml");
		for (BookChannelConfig config : list) {
			if(config.isDisable()){
				continue;
			}
			
			bookServiceMap.put(config.getId(), (BookService) InstanceUtils
					.newInstance(config.getServiceName()));
			bookChannelMap.put(config.getId(), CloneUtils.copy(config, BookChannel.class));
		}
	}

	public List<BookChannel> getBookChannelList() {
		return new ArrayList<BookChannel>(bookChannelMap.values());
	}

	private BookService getBookService(int channelId) {
		BookService bookService = bookServiceMap.get(channelId);
		if (bookService == null) {
			throw new NotFoundException("找不到此服务：" + channelId);
		}
		return bookService;
	}

	public PageList<Book> searchBook(String name, int page, int channelId) {
		try {
			return getBookService(channelId).searchBook(name, page);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PageList<Chapter> getChapterPageList(String bookId, int page,
			int channelId) {
		try {
			return getBookService(channelId).getChapterPageList(bookId, page);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public String getChapterContent(String chapterId, int channelId) {
		try {
			return getBookService(channelId).getChapterContent(chapterId);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

}
