
/**
 * @COSC 2351 Data Structures [Web Crawler Project]
 * @author marianky
 */

//imports necessary libraries 
import java.util.LinkedList;
import java.util.List;
import java.util.Map; //    ++
import java.util.HashMap; //  ++
import java.util.LinkedHashMap;  //  ++
import java.util.Map.Entry;//   ++
import java.util.Scanner; //    ++
import java.util.stream.Collectors; //  ++

public class Spider {
    //Member Variables 

    //static, final means constant (unchangeable value)
    private static final int MAX_PAGES_TO_SEARCH = 100;
    //created a hash map object that takes strings of pagesVisited because of the unique values
    private final HashMap<String, Integer> pagesVisited = new HashMap<>();
    //  ++ creating HashMap
    private final HashMap<String, HashMap<String, Integer>> pageRelevance = new HashMap<>();
    //uses linked list (for placing the pages already visited) instead of array because of performance reasons
    private final List<String> pagesToVisit = new LinkedList<>();
    //  ++ scanner for taking inputs
    private final Scanner scanner = new Scanner(System.in);

    /**
     * The main launching point for the Spider's functionality. Internally it
     * creates spider legs that make an HTTP request and parse the response (the
     * web page).
     *
     * @param url - The starting point of the spider
     * @param searchWord - The word or string that you are searching for
     */

    /* The Search function will create a spider leg , where every leg will take a URl to visit 
    and that spider leg object will be in charge of sending a http request, downloading the 
    contents of the page locally in memory, parse contents of page, finding other 
    links, and then searching for the word.
     */
    public void search(String url, List<String> searchWord, int MAX_PAGES_TO_SEARCH) {
        //while loop that keeps going as long as pages visited list has not reached maximum (100)
        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            //creating a string called currentURl (the one currently visiting and downloading the content) 
            String currentUrl;
            //create a spider leg object
            SpiderLeg leg = new SpiderLeg();
            //checking if pagesToVisit is empty, meaning this is my very first page to crawl in
            if (this.pagesToVisit.isEmpty()) {
                //current URL is what we are passing
                currentUrl = url;
            } else {
                //taking a URL from the linked list, checking its not in the pages visited, and returns the URL
                currentUrl = this.nextUrl();
            }
            //call spiderleg object and the crawl function and give it the current URL to visit
            boolean success = leg.crawl(currentUrl);  // getting the hyperlinks from the url

            if (success) {
                success = leg.searchForWord(searchWord);
                if (success) {
                    System.out.println("**Success** Words were found at " + currentUrl);
                    //get links returns this list of links 
                    //take all those links and adds them (using a linked list) to pages visited 
                    this.pagesToVisit.addAll(leg.getLinks());

                    // this is where we need to add the URL to the dictionary
                    //  ++ Putting url and matched words in a hash table
                    if (leg.getMatchedCounts() > 0) {
                        this.pagesVisited.put(currentUrl, leg.getMatchedCounts());
                    }
                }
            }
        }
        //  ++ sorting hashmap
        Map<String, Integer> sortedMapDesc = sortByValue(this.pagesVisited);
        //  ++ printing it in sequence
        System.out.println(sortedMapDesc);
    }

     /* The Search Relevance function gives some ranking to the URL for relevance. It creates a word set
    consisting of the word, and the list of where we found this word (URL) and utilizing 
    the idea of relevancy by giving priority with words that occured the most in the list. */
    
    public void searchRelevance(String url, int MAX_PAGES_TO_SEARCH) {
        //while loop that keeps going as long as page relevance list not reached maximum (100)
        while (this.pageRelevance.size() < MAX_PAGES_TO_SEARCH) {
            //creating a string called currentURl 
            String currentUrl;
            //create a spider leg object
            SpiderLeg leg = new SpiderLeg();
            //checking if pagesToVisit is empty, meaning this is my very first page to crawl in
            if (this.pagesToVisit.isEmpty()) {
                //current URL is what we are passing
                currentUrl = url;
            } else {
                //taking a URL from the linked list, checking its not in the pages visited, and returns the URL
                currentUrl = this.nextUrl();
            }
            //call spiderleg object and the crawl function and give it the current URL to visit
            boolean success = leg.crawl(currentUrl); // getting the hyperlinks from the url

            if (success) {
                //get links returns this list of links ;take all those links and adds them to pages visited 
                this.pagesToVisit.addAll(leg.getLinks());
                //`++ Taking url and all the words found in that url in hashmap
                this.pageRelevance.put(currentUrl, leg.getWordSet());
            }
        }
        System.out.println();
        System.out.println("Completed downloading contents of file!");
        // Taking input from user for searching as a query
        System.out.println("Enter search query:");
        String searchQuery = scanner.next();

        //creates a spider leg object
        SpiderLeg leg = new SpiderLeg();

        // Getting url and relevance number in the form of hashmap and passing it to the searchQuery
        HashMap<String, Integer> releventUrl = leg.getRelevance(searchQuery, this.pageRelevance);
        // Sorting the hashmap 
        Map<String, Integer> sortedMapDesc = sortByValue(releventUrl);
        // printing hashmap
        System.out.println(sortedMapDesc);
    }

    /**
     * Returns the next URL to visit (in the order that they were found). We
     * also do a check to make sure this method doesn't return a URL that has
     * already been visited.
     *
     * @return
     */
    public String nextUrl() {
        String nextUrl;
        do {
            //removes from 0 from the linked list
            //check if its in pages visited, until you pick one that is not in the pages visited list
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.containsKey(nextUrl));
        //returns string that contains the next URL
        return nextUrl;
    }

    //  ++ Function for sorting the hashmap , and converting back to map
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {
        List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        // Sorting the list using the ternary operator based on values
        // if (condition) 
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()) == 0
                //if condition is true, the part after question mark is executed
                ? o2.getKey().compareTo(o1.getKey())
                //if condition is false, then the part after colon gets executed instead
                : o2.getValue().compareTo(o1.getValue()));
        //retrieving key and value to store in a linkedhashmap
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }
}
