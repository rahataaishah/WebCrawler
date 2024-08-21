//read me file for web crawler project

1. Team Members :

Aaishah Rahat
Phoebe Mcpherson 

2. Instructions on how to run the code: 

Install netbeans / eclipse as well as JDK
Run SpiderTest.java for verifying solutions

3. Specifics related to the code:

Our web crawler operates in two different modes of operation: online and offline. 
The online part crawls and gives priority to pages with most words from list (most 
occurences of word ) which is option C that is listed in the course project details.The 
output of the online is the search results page that stores the integer value that 
corresponds with the number of times the word occured. 
The offline part crawls by maintaining a hashmap data structure of the words found in 
the crawled pages. The input is the search query, and the output generates a search 
results list with the URL's that contain the search words and is ordered by pageRelevance. 
