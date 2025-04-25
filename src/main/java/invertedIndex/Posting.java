package invertedIndex;

/**
 *
 * @author ehab
 */
public class Posting {
    public final int docId;
    public final int termFrequency;
    public Posting next = null;

    public Posting(int docId, int termFrequency) {
        this.docId = docId;
        this.termFrequency = termFrequency;
    }

    public int getDocId() {
        return docId;
    }

    public int getTermFrequency() {
        return termFrequency;
    }

    @Override
    public String toString() {
        return "{DocID: " + docId + ", TF: " + termFrequency + "}";
    }
}
