package info.sanaulla;

import com.google.common.cache.CacheStats;
import info.sanaulla.model.Book;
import info.sanaulla.service.BookService;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, ExecutionException {
        Book book = BookService.getBookDetails("9780849303159");
        System.out.println(Util.getObjectMapper().writeValueAsString(book));
        book = BookService.getBookDetails("9780849303159");
        book = BookService.getBookDetails("9780849303159");
        book = BookService.getBookDetails("9780849303159");
        book = BookService.getBookDetails("9780849303159");
        CacheStats cacheStats = BookService.getCacheStats();
        System.out.println(cacheStats.toString());
    }
}
