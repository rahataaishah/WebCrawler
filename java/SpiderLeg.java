
/**
 * @COSC 2351 Data Structures [Web Crawler Project]
 * @author marianky
 */
//imports necessary libraries , uses jsoup library
import java.io.IOException;
import java.io.BufferedReader; //   ++
import java.io.FileReader; //   ++
import java.util.List;  //  ++
import java.util.ArrayList; //  ++
import java.util.LinkedList;//  ++
import java.util.HashMap; //    ++

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg {

    //This sets a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    //This isnt' entirely the decent way of doing this
    //private static final String USER_AGENT
    //        = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    //This is the decent polite way of doing this!!

    /* identify user agent id (shows name of browser and version) to know who's sending the request */
    //setting user agent to be our own crawler ; final variable of type string 
    private static final String USER_AGENT = "Data Structures testing crawler (hbu.edu)";
    // ++ initializing total matched words as zero
    private int matchedWords = 0;
    //creating another linked list that will contain all the links that we will parse from this page
    private final List<String> links = new LinkedList<>();
    //creating a document object coming from jsoup , containing the html code of a web page 
    private Document htmlDocument;
    //  ++ Creating HashMap for keeping track of word occurrences in HTML document (needed for relelevance) 
    private final HashMap<String, Integer> wordSet = new HashMap<>();
    //  ++ implementing an arraylist for the stopwords
    private ArrayList<String> stopWords = new ArrayList<>();

    /**
     * This performs all the work. It makes an HTTP request, checks the
     * response, and then gathers up all the links on the page. Perform a
     * searchForWord after the successful crawl
     *
     * @param url - The URL to visit
     * @return whether or not the crawl was successful
     */

    /* The Crawl function will take the URL and tells it to visit that URL page, taking http request to server, 
    respond back with the content, save the content into html object, and will parse document looking 
    for useful information. The function returns a boolean , whether we will be able to visit the page 
    successfully or not. 
     */
    public boolean crawl(String url) {
        //  ++ reading stop words from file and setting it globally
        getStopWords("stopwords.txt");
        //try and catch
        try {
            //open up a connection object, give it the url you want to visit, and cast to user agent
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            //return html content of the page
            Document htmlDocument = connection.get();
            //page will be downloaded and stored into object
            this.htmlDocument = htmlDocument;
            //identifies whether connection was successful or not 
            if (connection.response().statusCode() == 200) // 200 is the HTTP OK status code
            //successfully opened up a connection, downloaded contents of page, and are currently visiting that page 
            {
                System.out.println("\n**Visiting** Received webpage at " + url);
            }
            //if response come back and content type is not html
            if (!connection.response().contentType().contains("text/html")) {
                //do not proceed if not html
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }

            /*Select function looks into the html document file for anything that has the word a into it. 
            It will parse and search for hyper reference links , and return in list of objects called "Elements". 
             */
            Elements linksOnPage = htmlDocument.select("a[href]");

            //  ++ crawling for relevance 
            /*Taking the entire body of text / html document (consisting of title and text)
            and placing it in a wordSet <String (word), Integer(number of times the word occured)> */
            String wordOnPage = htmlDocument.body().text();

            //for stopwords
            for (String word : wordOnPage.split((" "))) {
                word = word.toLowerCase();
                // Checking if the word is not in stopwords or length is less than 3
                if (!this.stopWords.contains(word) && (word.length() > 3)) {
                    int count = wordSet.getOrDefault(word, 0);
                    wordSet.put(word, count + 1);
                }
            }
            //prints size of my list
            System.out.println("Found (" + linksOnPage.size() + ") links");
            //loop over all the links
            for (Element link : linksOnPage) {
                //add links to linked list ; absURl function gives you the full path to add to string of links 
                this.links.add(link.absUrl("href"));
            }
            return true;
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            System.out.println("Something went wrong while processing the html page");
            return false;
        }
    }

    // Function to get stop words from file
    public void getStopWords(String filename) {
        ArrayList<String> stopWords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                stopWords.add(line.toLowerCase());
            }
        } catch (IOException ioe) {
        }
        this.stopWords = stopWords;
    }

    /**
     * Performs a search on the body of the HTML document that is retrieved.
     * This method should only be called after a successful crawl.
     *
     * @param searchWord - The word or string to look for
     * @return whether or not the word was found
     */

    /* This function looks into html buddy searching for word. Code is extracting 
    that text and storing into a string. Use string operation to returns true/false. 
     */
    public boolean searchForWord(List<String> searchWord) {
        // This method should only be used after a successful crawl.
        if (this.htmlDocument == null) {
            System.out.println("**ERROR** Crawl() method should be invoked before calling search method");
            return false;
        }
        System.out.println("Searching for words " + searchWord + "...");
        
        //how many times this word was used on the html page
        String bodyText = this.htmlDocument.body().text();

        //  ++ implementing a string function that counts the number of occurrences 
        int matchedCounts = 0;
        for (String word : searchWord) {
            //lower casing the word and checking if it matches
            for (String bodyWord : bodyText.toLowerCase().split(" ")) {
                // if yes, increasing word count by one
                if (bodyWord.contains(word.toLowerCase())) {
                    matchedCounts = matchedCounts + 1;
                }
            }
        }
        //Setting total word count globally
        this.matchedWords = matchedCounts;
        return true;
    }

    public List<String> getLinks() {
        //method that returns all the links scraped from the provided URL
        return this.links;
    }

    public HashMap<String, Integer> getWordSet() {
        //method that return all the words from a url (Used for calculating relevance)
        return this.wordSet;
    }

    //finds number of matching words in URL
    public int getMatchedCounts() {
        //method that returns matched word counts
        return this.matchedWords;
    }

    //Dictionary Crawling : returning pages that contain the word and sorting by relevance 
    //method that returns the relevance hashmap along with current url
    public HashMap<String, Integer> getRelevance(String searchQuery, HashMap<String, HashMap<String, Integer>> pageRelevance) {
        String[] searchWord = searchQuery.toLowerCase().split(" ");
        //counter initially set to 0
        int counter = 0;
        // utiliting hashmap
        HashMap<String, Integer> releventUrl = new HashMap<>();
        while (counter < pageRelevance.size()) {
            String url = (String) pageRelevance.keySet().toArray()[counter]; //array of keys
            HashMap<String, Integer> wordSet = pageRelevance.get(url);
            //relevance initially set to 0
            int relevance = 0;
            //looping through
            for (String word : searchWord) {
                if (wordSet.containsKey(word)) {
                    // adds relevance as counting of all the words matched in the word set
                    int count = wordSet.getOrDefault(word, 0);
                    relevance = relevance + count;
                }
            }
            releventUrl.put(url, relevance);
            counter = counter + 1; //incrementing counter
        }
        return releventUrl; 
    }
}
