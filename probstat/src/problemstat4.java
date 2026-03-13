import java.util.*;

class PlagiarismDetector {

    // n-gram size (5 words)
    private static final int N = 5;

    // ngram -> documents containing it
    private HashMap<String, Set<String>> index = new HashMap<>();

    // document -> its ngrams
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    /**
     * Break text into n-grams
     */
    private List<String> generateNGrams(String text) {

        List<String> ngrams = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    /**
     * Add document to database
     */
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNGrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());

            index.get(gram).add(docId);
        }
    }

    /**
     * Analyze a new document
     */
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNGrams(text);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String gram : ngrams) {

            if (index.containsKey(gram)) {

                for (String doc : index.get(gram)) {

                    matchCounts.put(doc,
                            matchCounts.getOrDefault(doc, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {

            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity =
                    (matches * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + matches + " matching n-grams with \"" +
                            otherDoc + "\"");

            System.out.println(
                    "Similarity: " +
                            String.format("%.2f", similarity) + "%");

            if (similarity > 60) {
                System.out.println("⚠ PLAGIARISM DETECTED");
            } else if (similarity > 10) {
                System.out.println("Suspicious similarity");
            }

            System.out.println();
        }
    }
}

public class problemstat4 {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        // Existing essays
        detector.addDocument(
                "essay_089.txt",
                "machine learning is a field of artificial intelligence that focuses on data driven models");

        detector.addDocument(
                "essay_092.txt",
                "machine learning is a field of artificial intelligence that focuses on data driven models and predictive algorithms");

        // New submission
        String newEssay =
                "machine learning is a field of artificial intelligence that focuses on data driven models and predictive systems";

        System.out.println("Analyzing essay_123.txt\n");

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}
