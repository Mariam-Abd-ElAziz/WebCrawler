package crawler;

import java.util.*;
import java.lang.Math;

public class TFIDFCalculator {

    public static double computeTfWeight(int tf) {
        return tf > 0 ? 1 + Math.log10(tf) : 0;
    }

    public static double computeIdf(int N, int df) {
        return df > 0 ? Math.log10((double) N / df) : 0;
    }

    public static double computeTfidf(int tf, double idf) {
        return computeTfWeight(tf) * idf;
    }

    public static double cosineSimilarity(Map<String, Double> docVector, Map<String, Double> queryVector) {
        double dotProduct = 0.0;
        double normDoc = 0.0;
        double normQuery = 0.0;

        // Calculate dot product
        for (String term : queryVector.keySet()) {
            if (docVector.containsKey(term)) {
                dotProduct += docVector.get(term) * queryVector.get(term);
            }
        }

        // Calculate norms
        for (double value : docVector.values()) {
            normDoc += value * value;
        }
        for (double value : queryVector.values()) {
            normQuery += value * value;
        }

        normDoc = Math.sqrt(normDoc);
        normQuery = Math.sqrt(normQuery);

        if (normDoc == 0.0 || normQuery == 0.0) {
            return 0.0;
        } else {
            return dotProduct / (normDoc * normQuery);
        }
    }
}
