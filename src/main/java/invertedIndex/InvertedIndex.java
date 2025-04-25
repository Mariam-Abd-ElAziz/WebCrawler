package invertedIndex;

import java.util.*;

public class InvertedIndex {


    public Map<Integer, SourceRecord> sources;
    public HashMap<String, List<Posting>> invertedIndex;
    public InvertedIndex() {
        sources = new HashMap<Integer, SourceRecord>();
        invertedIndex = new HashMap<String, List<Posting>>();
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
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
    public void processDocument(SourceRecord document,int documentId) {
        String[] tokens = document.getContent().toLowerCase().split("\\W+");
        Map<String, Integer> termFrequency = new HashMap<>();

        // Calculate term frequency (TF)
        for (String token : tokens) {
            if(stopWord(token)) continue;
            termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
        }

        updateInvertedIndex(tokens, documentId);
    }
    private void updateInvertedIndex(String[] tokens, int docId) {
        Map<String, Integer> termFrequency = new HashMap<>();

        // Calculate term frequency (TF)
        for (String token : tokens) {
            termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
        }

        // Update inverted index
        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();

            // Add new term entry to the inverted index
            invertedIndex.putIfAbsent(term, new ArrayList<>());
            invertedIndex.get(term).add(new Posting(docId, tf));
        }
    }




}
