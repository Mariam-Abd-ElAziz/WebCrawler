package crawler;

import invertedIndex.Posting;
import invertedIndex.InvertedIndex;
import invertedIndex.SourceRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import crawler.TFIDFCalculator;
import crawler.TFIDFCalculator.TermTF;
import java.util.*;



public class WebCrawler {
    private static final int MAX_CRAWLED_PAGES_NO = 10;
    private final Set<String> visitedURLs = new HashSet<>();
    private final Queue<String> URLsQueue = new LinkedList<>();
    private int crawledPagesCount = 0;
    private InvertedIndex invertedIndex=new InvertedIndex();
    private final Map<Integer, SourceRecord> docRecords = invertedIndex.sources;



    //initialize the queue with the seeds
    public WebCrawler(String[] seedURLs) {
        Collections.addAll(URLsQueue, seedURLs);
    }

    public void startCrawling() {
        while (!URLsQueue.isEmpty() && crawledPagesCount < MAX_CRAWLED_PAGES_NO) {
            String currentURL = URLsQueue.poll();

            if (currentURL == null) continue;

            try {
                // Politeness: Respect Wikipedia's crawling delay policy
                Thread.sleep(1000);

                System.out.println("\nFetching: " + currentURL);

                //will be used in the Jsoup connection as the value of userAgent header of the http request
                //to pretend to be a normal browser on Windows
                //to avoid getting blocked by websites that restrict bots or scrapers.
                String dummyBrowserName = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
                Document doc = Jsoup.connect(currentURL)
                        .userAgent(dummyBrowserName)
                        .get();

                //reflect the successful URL visit
                visitedURLs.add(currentURL);
                crawledPagesCount++;
                int docId = crawledPagesCount;
                System.out.println("✅ Crawled (" + crawledPagesCount + "/" + MAX_CRAWLED_PAGES_NO + "): " + currentURL);
                // Extract text and tokenize
                String text = doc.body().text();
                SourceRecord record = new SourceRecord(docId, currentURL, text);
                docRecords.put(docId, record);

                invertedIndex.processDocument(record,docId);
                //get all the links in the main article content only
                Elements linksInPage = doc.select("#mw-content-text a[href^='/wiki/']:not(.reference a)");
                System.out.println("Found " + linksInPage.size() + " potential links");

                //normalize the links, then add the valid ones only to the queue
                int validLinksNo = 0;
                for (Element link : linksInPage) {
                    String URL = link.absUrl("href");
                    URL = normalizeURL(URL);

                    if (isValidArticle(URL) && !visitedURLs.contains(URL)) {

                        URLsQueue.add(URL);
                        validLinksNo++;
                    }
                }

                System.out.println("Added " + validLinksNo + " new URLs to queue");

            } catch (Exception e) {
                System.err.println("Error crawling " + currentURL + ": " + e.getMessage());
            }
        }
        invertedIndex.printInvertedIndex();
    }

    public void printCrawledURLs () {
        System.out.println("\nCrawled Pages:");
        this.visitedURLs.forEach(url ->
                System.out.println("→ " + url));
    }



    private String normalizeURL(String URL) {
        //remove any fragments or query parameters from the url
        URL = URL.split("#")[0].split("\\?")[0];
        return URL;
    }

    private boolean isValidArticle(String URL) {
        // Check if the URL references a Wikipedia article or not
        String wikipediaArticle_URLPrefix = "https://en.wikipedia.org/wiki/";
        if (!URL.startsWith(wikipediaArticle_URLPrefix)) {
            return false;
        }

        // Extract the part after /wiki/ (article name)
        String pagePart = URL.substring(wikipediaArticle_URLPrefix.length());

        // Return true if the URL is of a normal article, which is, it's not any of the following:
        return !pagePart.contains(":") &&                                   // Exclude special pages
                !pagePart.contains("#") &&                                  // Exclude fragments
                !pagePart.matches(".*\\.(jpg|png|pdf|svg)$");         // Exclude files
    }

    public int getTotalDocumentCount() {
        return crawledPagesCount;
    }



    // Method to get the term frequencies for all crawled documents
    public Map<String, List<TermTF>> getTermFrequencies() {
        Map<String, List<TermTF>> termFrequencies = new HashMap<>();

        // Loop over each document and its term frequency map
        for (Map.Entry<Integer, SourceRecord> entry : docRecords.entrySet()) {
            SourceRecord record = entry.getValue();
            String[] tokens = record.getContent().toLowerCase().split("\\W+");

            // A map to store term frequencies for the current document
            Map<String, Integer> termFrequency = new HashMap<>();

            // Calculate term frequency (TF)
            for (String token : tokens) {
                if (stopWord(token)) continue;
                termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
            }

            // For each term, create a TermTF object and add it to the list
            for (Map.Entry<String, Integer> termEntry : termFrequency.entrySet()) {
                String term = termEntry.getKey();
                int tf = termEntry.getValue();

                termFrequencies.putIfAbsent(term, new ArrayList<>());
                termFrequencies.get(term).add(new TermTF(record.getId(), tf));
            }
        }

        return termFrequencies;
    }
    boolean stopWord(String word) {
        Set<String> stopWords = Set.of("the", "to", "be", "for", "from", "in", "a", "into", "by", "or", "and", "that");
        return word.length() < 2 || stopWords.contains(word);
    }
    public InvertedIndex getInvertedIndex(){
        return invertedIndex;
    }
}

