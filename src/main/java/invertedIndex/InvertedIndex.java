package invertedIndex;

import java.util.*;

public class InvertedIndex {


    public Map<Integer, SourceRecord> sources;
    public HashMap<String, List<Posting>> invertedIndex;
    public InvertedIndex() {
        sources = new HashMap<Integer, SourceRecord>();
        invertedIndex = new HashMap<String, List<Posting>>();
    }
    private static String removeSuffix(String word, String suffix) {
        if (word.endsWith(suffix)) {
            return word.substring(0, word.length() - suffix.length());
        }
        return word;
    }

    public static String stemWord(String word) {
        if (word.length() <= 3) {
            return word;
        }

        String[] suffixes = {"ation", "tion", "sion", "ible", "able", "ment", "ness", "fully", "edly", "ing", "ly", "ed", "es", "s"};

        for (String suffix : suffixes) {
            if (word.endsWith(suffix)) {
                String stemmed = removeSuffix(word, suffix);
                if (stemmed.length() >= 3) {
                    return stemmed;
                }
            }
        }

        return word;
    }


    public void printPostingList(Posting p) {

        System.out.print("[");
        while (p != null) {

            System.out.print("" + p.docId );
            p = p.next;

            if (p != null) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }
    public void printInvertedIndex() {
        for (Map.Entry<String, List<Posting>> entry : invertedIndex.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
   public static boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
    public void processDocument(SourceRecord document, int documentId) {
        // Tokenize the document content
        String[] tokens = document.getContent().toLowerCase().split("\\W+");
        Map<String, Integer> termFrequency = new HashMap<>();

        // Calculate term frequency (TF) and apply stemming
        for (String token : tokens) {
            if (stopWord(token)) continue;  // Skip stop words

            // Stem the token
            token = stemWord(token);

            // Update term frequency map with stemmed token
            termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
        }

        // After processing the terms, update the inverted index
        updateInvertedIndex(termFrequency, documentId);
    }

    private void updateInvertedIndex(Map<String, Integer> termFrequency, int docId) {
        // Update inverted index with the term frequency map
        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();

            // Add new term entry to the inverted index
            invertedIndex.putIfAbsent(term, new ArrayList<>());
            invertedIndex.get(term).add(new Posting(docId, tf));
        }
    }





}
