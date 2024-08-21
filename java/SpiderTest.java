
//imports necessary libraries
import java.util.LinkedList;
import java.util.List;

/**
 * @COSC 2351 Data Structures [Web Crawler Project]
 * @author marianky
 */
public class SpiderTest {

    /**
     * The main method creates a spider object (which creates spider legs) and
     * crawls the web.
     *
     */
    public static void main(String[] args) {
        //create a spider object 
        Spider spider = new Spider();

        // Making a dummy word list for searching
        List<String> searchWord = new LinkedList<>();
        searchWord.add("Cyber");
        searchWord.add("Test");

        //invoke the search and search relevance function 
        //arguments: starting point from where you want to crawl and word your looking for while navigating the internet
        spider.search("http://www.hbu.edu", searchWord, 5);
        spider.searchRelevance("https://www.hbu.edu", 5);
    }
}
