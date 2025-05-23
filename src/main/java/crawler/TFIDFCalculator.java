package crawler;

import java.util.*;

public class TFIDFCalculator {

    // Compute the TF weight (1 + log(TF)) for a given term frequency (TF)
    public static double computeTfWeight(int tf) {
        return tf > 0 ? 1 + Math.log10(tf) : 0;
    }

    // Compute the smoothed IDF (Inverse Document Frequency)
    public static double computeIdf(int N, int df) {
        return df > 0 ? Math.log10(1.0 + (double) N / df) : 0;
    }

    // Compute the TF-IDF score by multiplying TF weight with IDF
    public static double computeTfidf(int tf, double idf) {
        return computeTfWeight(tf) * idf;
    }

    // Compute cosine similarity between two document vectors
    public static double cosineSimilarity(Map<String, Double> docVector, Map<String, Double> queryVector) {
        double dotProduct = 0.0;
        double normDoc = 0.0;
        double normQuery = 0.0;

        for (String term : queryVector.keySet()) {
            if (docVector.containsKey(term)) {
                dotProduct += docVector.get(term) * queryVector.get(term);
            }
        }

        for (double value : docVector.values()) {
            normDoc += value * value;
        }
        for (double value : queryVector.values()) {
            normQuery += value * value;
        }

        normDoc = Math.sqrt(normDoc);
        normQuery = Math.sqrt(normQuery);

        return (normDoc == 0.0 || normQuery == 0.0) ? 0.0 : dotProduct / (normDoc * normQuery);
    }

    // Compute the TF-IDF vectors for all documents
    public static Map<Integer, Map<String, Double>> computeTfidfVectors(Map<String, List<TermTF>> tfData, int N) {
        Map<String, Integer> df = new HashMap<>();
        Map<String, Double> idf = new HashMap<>();
        Map<Integer, Map<String, Double>> docVectors = new HashMap<>();

        for (String term : tfData.keySet()) {
            df.put(term, tfData.get(term).size());
        }

        for (String term : df.keySet()) {
            idf.put(term, computeIdf(N, df.get(term)));
        }

        for (String term : tfData.keySet()) {
            List<TermTF> postings = tfData.get(term);
            for (TermTF posting : postings) {
                int docId = posting.docId;
                int tf = posting.tf;
                double tfidf = computeTfidf(tf, idf.get(term));

                docVectors.putIfAbsent(docId, new HashMap<>());
                docVectors.get(docId).put(term, tfidf);
            }
        }

        return docVectors;
    }

    // Helper class for term frequency in a document
    public static class TermTF {
        public int docId;
        public int tf;

        public TermTF(int docId, int tf) {
            this.docId = docId;
            this.tf = tf;
        }
    }
}
