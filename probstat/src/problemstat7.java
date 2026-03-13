import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();

    // queries that pass through this prefix
    List<String> queries = new ArrayList<>();
}

class AutocompleteSystem {

    private TrieNode root = new TrieNode();

    // query -> frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    /**
     * Insert query into Trie
     */
    public void insertQuery(String query, int freq) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());

            node = node.children.get(c);

            node.queries.add(query);
        }
    }

    /**
     * Search suggestions by prefix
     */
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }

            node = node.children.get(c);
        }

        // Min heap to get top 10 results
        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) ->
                        frequencyMap.get(a) - frequencyMap.get(b));

        for (String query : node.queries) {

            pq.offer(query);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> results = new ArrayList<>();

        while (!pq.isEmpty()) {
            results.add(pq.poll());
        }

        Collections.reverse(results);

        return results;
    }

    /**
     * Update query frequency (when user searches again)
     */
    public void updateFrequency(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);
    }

    /**
     * Display suggestions with frequency
     */
    public void printSuggestions(String prefix) {

        List<String> suggestions = search(prefix);

        System.out.println("Suggestions for \"" + prefix + "\":");

        int rank = 1;

        for (String query : suggestions) {

            System.out.println(rank + ". " + query +
                    " (" + frequencyMap.get(query) + " searches)");

            rank++;
        }

        System.out.println();
    }
}

public class problemstat7{

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        // Insert queries
        system.insertQuery("java tutorial", 1234567);
        system.insertQuery("javascript", 987654);
        system.insertQuery("java download", 456789);
        system.insertQuery("java 21 features", 100);
        system.insertQuery("java streams", 50000);
        system.insertQuery("java hashmap", 20000);

        // Search prefix
        system.printSuggestions("jav");

        // Update frequency
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("Updated frequency of 'java 21 features': "
                + 3);

        system.printSuggestions("java");
    }
}