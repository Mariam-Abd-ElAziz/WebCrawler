package crawler;

public class Main {
    public static void main(String[] args) {
        String[] seedUrls = {
                "https://en.wikipedia.org/wiki/List_of_pharaohs",
                "https://en.wikipedia.org/wiki/Pharaoh"
        };

        WebCrawler crawler = new WebCrawler(seedUrls);
        crawler.startCrawling();

        System.out.println("\nCrawled Pages:");
        crawler.getVisitedUrls().forEach(url ->
                System.out.println("→ " + url));
    }
}