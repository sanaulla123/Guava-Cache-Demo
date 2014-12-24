package info.sanaulla.service;

import com.google.common.cache.CacheStats;
import info.sanaulla.model.Author;
import info.sanaulla.model.Book;
import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.*;
public class BookServiceTest {

    @Test
    public void testGetBookDetails() throws Exception {
        String isbn13 = "9780849303159";
        Book book = BookService.getBookDetails(isbn13).get();
        assertNotNull(book);
        assertNotNull(book.getTitle());
        assertNotNull(book.getIsbn13());
        assertNotNull(book.getSummary());
        assertNotNull(book.getAuthors());
        assertNotNull(book.getPublisher());
        assertNotNull(book.getPageCount());
        assertNotNull(book.getPublishedDate());
        assertEquals(1, book.getAuthors().size());
        assertEquals(book.getTitle(), "Principles of Solid Mechanics");
        assertEquals(book.getIsbn13(), isbn13);
        assertEquals(book.getPageCount().intValue(),446);
        assertEquals(book.getPublisher(), "");
        assertEquals(book.getSummary(), "Evolving from more than 30 years of research and teaching experience" +
                ", \"Principles of Solid Mechanics\" offers an in-depth treatment of the application of the " +
                "full-range theory of deformable solids for analysis and design. Unlike other texts, it is not " +
                "either a civil or mechanical engineering text, but both. It treats not only analysis but " +
                "incorporates design along with experimental observation. Principles of Solid Mechanics serves " +
                "as a core course textbook for advanced seniors and first-year graduate students. The author " +
                "focuses on basic concepts and applications, simple yet unsolved problems, inverse strategies for " +
                "optimum design, unanswered questions, and unresolved paradoxes to intrigue students and encourage " +
                "further study. He includes plastic as well as elastic behavior in terms of a unified field theory " +
                "and discusses the properties of field equations and requirements on boundary conditions crucial for " +
                "understanding the limits of numerical modeling. Designed to help guide students with little " +
                "experimental experience and no exposure to drawing and graphic analysis, the text presents " +
                "carefully selected worked examples. The author makes liberal use of footnotes and includes " +
                "over 150 figures and 200 problems. This, along with his approach, allows students to see " +
                "the full range, non-linear response of structures.");
        Author author = book.getAuthors().get(0);
        assertNotNull(author);
        assertEquals(author.getName(), "Rowland Richards");
    }

    @Test
    public void testGetBookDetails_InvalidIsbn()throws Exception{
        String isbn13 = "invalid_isbn";
        Book book = BookService.getBookDetails(isbn13).orNull();
        assertNull(book);
    }

    @Test
    public void testGetCacheStats(){
        CacheStats cacheStats = BookService.getCacheStats();
        assertNotNull(cacheStats);
        System.out.println(cacheStats.toString());
        assertNotNull(cacheStats.hitCount());
        assertNotNull(cacheStats.missCount());
        assertNotNull(cacheStats.loadSuccessCount());
    }
}