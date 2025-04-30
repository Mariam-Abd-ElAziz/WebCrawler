package crawler;
import invertedIndex.InvertedIndex; // make sure you import your InvertedIndex class

import java.util.*;

public class QueryProcessor {

    private final Map<Integer, Map<String, Double>> docVectors;
    private final Map<String, List<TFIDFCalculator.TermTF>> invertedIndex;
    private final int numDocuments;

    public QueryProcessor(Map<Integer, Map<String, Double>> docVectors,
                          Map<String, List<TFIDFCalculator.TermTF>> invertedIndex,
                          int numDocuments) {
        this.docVectors = docVectors;
        this.invertedIndex = invertedIndex;
        this.numDocuments = numDocuments;
    }

    public List<Result> processQuery(String query) {
        // Step 1: Tokenize and normalize the query
        String[] tokens = query.toLowerCase().split("\\W+");

        // Step 2: Apply stemming and stopword removal
        Map<String, Integer> queryTf = new HashMap<>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                if (!InvertedIndex.stopWord(token)) { // skip stop words
                    String stemmed = InvertedIndex.stemWord(token); // use same stemmer
                    queryTf.put(stemmed, queryTf.getOrDefault(stemmed, 0) + 1);
                }
            }
        }
        // Step 3: Build the query vector

        Map<String, Double> queryVector = new HashMap<>();
        for (String term : queryTf.keySet()) {
            int tf = queryTf.get(term);
            int df = invertedIndex.containsKey(term) ? invertedIndex.get(term).size() : 0;
            double idf = TFIDFCalculator.computeIdf(numDocuments, df);
            double tfidf = TFIDFCalculator.computeTfidf(tf, idf);
            queryVector.put(term, tfidf);
        }

        // Step 4: Calculate cosine similarity with each document
        List<Result> results = new ArrayList<>();
        for (int docId : docVectors.keySet()) {
            Map<String, Double> docVector = docVectors.get(docId);
            double score = TFIDFCalculator.cosineSimilarity(docVector, queryVector);

            if (score > 0) {
                results.add(new Result(docId, score));
            }
        }

        // Step 5: Sort the results by score descending and return top 10 documents
        results.sort((a, b) -> Double.compare(b.score, a.score));

        return results.subList(0, Math.min(10, results.size()));
    }

    public static class Result {
        public int docId;
        public double score;
        public String url;

        public Result(int docId, double score) {
            this.docId = docId;
            this.score = score;

        }
    }
}
