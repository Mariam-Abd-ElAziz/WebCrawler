package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;

public class WebCrawler {
    private static final int MAX_PAGES = 10;
    private final Set<String> visitedUrls = new HashSet<>();
    private final Queue<String> urlQueue = new LinkedList<>();
    private int docCount = 0;

    public WebCrawler(String[] seedUrls) {
        Collections.addAll(urlQueue, seedUrls);
    }

    public void startCrawling() {
        while (!urlQueue.isEmpty() && docCount < MAX_PAGES) {
            String currentUrl = urlQueue.poll();

            if (currentUrl == null || visitedUrls.contains(currentUrl)) continue;

            try {
                // Respect Wikipedia's crawling delay policy
                Thread.sleep(1000);
                System.out.println("\nFetching: " + currentUrl);

                Document doc = Jsoup.connect(currentUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .get();

                visitedUrls.add(currentUrl);
                docCount++;
                System.out.println("✅ Crawled (" + docCount + "/" + MAX_PAGES + "): " + currentUrl);

                // Only look at links in the main article content
//                Elements links = doc.select("#mw-content-text a[href^='/wiki/']");
                Elements links = doc.select("#mw-content-text a[href^='/wiki/']:not(.reference a)");

                System.out.println("Found " + links.size() + " potential links");
                int validLinks = 0;

                for (Element link : links) {
                    String url = link.absUrl("href");
                    url = normalizeUrl(url);

                    if (isValidArticle(url) && !visitedUrls.contains(url)) {
                        urlQueue.add(url);
                        validLinks++;
//                        System.out.println("  ➕ Valid link: " + url);
                    } else {
//                        System.out.println("  ❌ Invalid link: " + url);
                    }
                }

                System.out.println("Added " + validLinks + " new URLs to queue");

            } catch (Exception e) {
                System.err.println("Error crawling " + currentUrl + ": " + e.getMessage());
            }
        }
    }

    private String normalizeUrl(String url) {
        return url.split("#")[0].split("\\?")[0];
    }

    private boolean isValidArticle(String url) {
        // Check basic Wikipedia structure
        if (!url.startsWith("https://en.wikipedia.org/wiki/")) {
            return false;
        }

        // Extract the part after /wiki/
        String pagePart = url.substring("https://en.wikipedia.org/wiki/".length());

        // Exclude pages contain colons IN THE PAGE NAME
        return !pagePart.contains(":") &&           // Exclude special pages
                !pagePart.contains("#") &&           // Exclude fragments
                !pagePart.matches(".*\\.(jpg|png|pdf|svg)$");  // Exclude files
    }
    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }
}