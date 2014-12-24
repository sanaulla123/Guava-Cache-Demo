package info.sanaulla.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import info.sanaulla.Constants;
import info.sanaulla.Util;
import info.sanaulla.model.Author;
import info.sanaulla.model.Book;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by msanaulla on 12/24/2014.
 */
public class BookService {
    private static LoadingCache<String, Book> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .recordStats()
            .build(new CacheLoader<String, Book>() {
                @Override
                public Book load(String s) throws IOException {
                    return  getBookDetailsFromHttp(s);
                }
            });
    public static Book getBookDetails(String isbn13) throws IOException, ExecutionException {
        Book book = cache.get(isbn13);
        return book;
    }

    public static CacheStats getCacheStats(){
        return cache.stats();
    }

    private static Book getBookDetailsFromHttp(String isbn13) throws IOException{
        Properties properties = Util.getProperties();
        String key = properties.getProperty(Constants.BOOK_API_KEY);
        String url = "http://isbndb.com/api/v2/json/"+key+"/book/"+ isbn13;
        String response = Util.getHttpResponse(url);
        Map bookMap = Util.getObjectMapper().readValue(response,Map.class);
        List bookDataList = (List)bookMap.get("data");
        if ( bookDataList.size() < 1){
            return null;
        }
        Map bookData = (Map) bookDataList.get(0);
        Book book = new Book();
        book.setTitle(bookData.get("title").toString());
        List authorDataList = (List)bookData.get("author_data");
        for(Object authorDataObj : authorDataList){
            Map authorData = (Map)authorDataObj;
            Author author = new Author();
            author.setId(authorData.get("id").toString());
            author.setName(authorData.get("name").toString());
            book.addAuthor(author);
        }
        book.setIsbn13(bookData.get("isbn13").toString());
        book.setPublisher(bookData.get("publisher_name").toString());
        book.setSummary(bookData.get("summary").toString());
        return book;
    }
}
