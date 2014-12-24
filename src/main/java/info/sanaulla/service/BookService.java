package info.sanaulla.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import info.sanaulla.Constants;
import info.sanaulla.Util;
import info.sanaulla.model.Author;
import info.sanaulla.model.Book;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by msanaulla on 12/24/2014.
 */
public class BookService {
    private static LoadingCache<String, Optional<Book>> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .recordStats()
            .build(new CacheLoader<String, Optional<Book>>() {
                @Override
                public Optional<Book> load(String s) throws IOException {
                    return getBookDetailsFromGoogleBooks(s);
                }
            });

    public static Optional<Book> getBookDetails(String isbn13) throws IOException, ExecutionException {
        Optional<Book> book = cache.get(isbn13);
        return book;
    }

    public static CacheStats getCacheStats(){
        return cache.stats();
    }

    private static Optional<Book> getBookDetailsFromIsbnDb(String isbn13) throws IOException{
        Properties properties = Util.getProperties();
        String key = properties.getProperty(Constants.BOOK_API_KEY);
        String url = "http://isbndb.com/api/v2/json/"+key+"/book/"+ isbn13;
        String response = Util.getHttpResponse(url);
        Book book = null;
        Map bookMap = Util.getObjectMapper().readValue(response,Map.class);
        Object bookDataListObj = bookMap.get("data");
        if ( bookDataListObj == null || !(bookDataListObj instanceof List)){
            return Optional.fromNullable(book);
        }
        List bookDataList = (List)bookMap.get("data");
        if ( bookDataList.size() < 1){
            return Optional.fromNullable(book);
        }
        Map bookData = (Map) bookDataList.get(0);
        book = new Book();
        book.setTitle(bookData.get("title").toString());
        List authorDataList = (List)bookData.get("author_data");
        for(Object authorDataObj : authorDataList){
            Map authorData = (Map)authorDataObj;
            Author author = new Author();
            author.setName(authorData.get("name").toString());
            book.addAuthor(author);
        }
        book.setIsbn13(bookData.get("isbn13").toString());
        return Optional.fromNullable(book);
    }

    private static Optional<Book> getBookDetailsFromGoogleBooks(String isbn13) throws IOException{
        Properties properties = Util.getProperties();
        String key = properties.getProperty(Constants.GOOGLE_API_KEY);
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn13;
        String response = Util.getHttpResponse(url);
        Map bookMap = Util.getObjectMapper().readValue(response,Map.class);
        Object bookDataListObj = bookMap.get("items");
        Book book = null;
        if ( bookDataListObj == null || !(bookDataListObj instanceof List)){
            return Optional.fromNullable(book);
        }

        List bookDataList = (List)bookDataListObj;
        if ( bookDataList.size() < 1){
            return Optional.fromNullable(null);
        }

        Map bookData = (Map) bookDataList.get(0);
        Map volumeInfo = (Map)bookData.get("volumeInfo");
        book = new Book();
        book.setTitle(getFromJsonResponse(volumeInfo,"title",""));
        book.setPublisher(getFromJsonResponse(volumeInfo,"publisher",""));
        List authorDataList = (List)volumeInfo.get("authors");
        for(Object authorDataObj : authorDataList){
            Author author = new Author();
            author.setName(authorDataObj.toString());
            book.addAuthor(author);
        }
        book.setIsbn13(isbn13);
        book.setSummary(getFromJsonResponse(volumeInfo,"description",""));
        book.setPageCount(Integer.parseInt(getFromJsonResponse(volumeInfo, "pageCount", "0")));
        book.setPublishedDate(getFromJsonResponse(volumeInfo,"publishedDate",""));

        return Optional.fromNullable(book);
    }

    private static String getFromJsonResponse(Map jsonData, String key, String defaultValue){
        return Optional.fromNullable(jsonData.get(key)).or(defaultValue).toString();
    }
}
