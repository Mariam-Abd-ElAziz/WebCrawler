package crawler;

import invertedIndex.InvertedIndex;
import invertedIndex.SourceRecord;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        String[] seedUrls = {
                "https://en.wikipedia.org/wiki/List_of_pharaohs",
                "https://en.wikipedia.org/wiki/Pharaoh"
        };

        WebCrawler crawler = new WebCrawler(seedUrls);
        crawler.startCrawling();
        crawler.printCrawledURLs();
        Map<String, List<TFIDFCalculator.TermTF>> tfData = crawler.getTermFrequencies();
        int N = crawler.getTotalDocumentCount();
        InvertedIndex invertedIndex=crawler.getInvertedIndex();
        Map<Integer, SourceRecord> sources= invertedIndex.sources;
        Map<Integer, Map<String, Double>> tfidfVectors = TFIDFCalculator.computeTfidfVectors(tfData, N);
        for (Map.Entry<Integer, Map<String, Double>> entry : tfidfVectors.entrySet()) {
            int docId = entry.getKey();
            Map<String, Double> tfidfVector = entry.getValue();
            //  System.out.println("DocID: " + docId + " TF-IDF Vector: " + tfidfVector);
        }
        QueryProcessor queryProcessor = new QueryProcessor(tfidfVectors, tfData, N);

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nCrawler ready. You can now search documents!");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            System.out.print("\nEnter query: ");
            String query = scanner.nextLine();

            if (query.equalsIgnoreCase("exit")) {
                System.out.println("Exiting. Goodbye!");
                break;
            }

            List<QueryProcessor.Result> results = queryProcessor.processQuery(query);

            if (results.isEmpty()) {
                System.out.println("No matching documents found.");
            } else {
                System.out.println("Top matching documents:");
                for (QueryProcessor.Result result : results) {
                    SourceRecord source = sources.get(result.docId);

                    System.out.println("URL: " + source.URL+" ,DocID: " + result.docId +" ,Score: " + result.score);

                }
            }

        }


        scanner.close();
    }

}

