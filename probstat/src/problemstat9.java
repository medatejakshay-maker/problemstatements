import java.util.*;

class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long timestamp;

    public Transaction(int id, int amount, String merchant,
                       String account, long timestamp) {

        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.timestamp = timestamp;
    }
}

class FraudDetector {

    /**
     * Classic Two-Sum
     */
    public List<int[]> findTwoSum(List<Transaction> txns, int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        List<int[]> results = new ArrayList<>();

        for (Transaction t : txns) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                results.add(new int[]{
                        map.get(complement).id,
                        t.id
                });
            }

            map.put(t.amount, t);
        }

        return results;
    }

    /**
     * Two-Sum within time window
     */
    public List<int[]> twoSumWithinWindow(List<Transaction> txns,
                                          int target,
                                          long windowMillis) {

        HashMap<Integer, List<Transaction>> map = new HashMap<>();

        List<int[]> results = new ArrayList<>();

        for (Transaction t : txns) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                for (Transaction prev : map.get(complement)) {

                    if (Math.abs(t.timestamp - prev.timestamp)
                            <= windowMillis) {

                        results.add(new int[]{prev.id, t.id});
                    }
                }
            }

            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }

        return results;
    }

    /**
     * Duplicate detection
     */
    public List<String> detectDuplicates(List<Transaction> txns) {

        HashMap<String, Set<String>> map = new HashMap<>();

        List<String> results = new ArrayList<>();

        for (Transaction t : txns) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(t.account);
        }

        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {

            if (entry.getValue().size() > 1) {

                results.add("Duplicate: " + entry.getKey() +
                        " accounts=" + entry.getValue());
            }
        }

        return results;
    }

    /**
     * K-Sum using recursion
     */
    public List<List<Integer>> findKSum(List<Transaction> txns,
                                        int k,
                                        int target) {

        List<List<Integer>> results = new ArrayList<>();

        backtrack(txns, k, target, 0,
                new ArrayList<>(), results);

        return results;
    }

    private void backtrack(List<Transaction> txns,
                           int k,
                           int target,
                           int start,
                           List<Integer> current,
                           List<List<Integer>> results) {

        if (k == 0 && target == 0) {
            results.add(new ArrayList<>(current));
            return;
        }

        if (k <= 0 || target < 0) return;

        for (int i = start; i < txns.size(); i++) {

            current.add(txns.get(i).id);

            backtrack(txns,
                    k - 1,
                    target - txns.get(i).amount,
                    i + 1,
                    current,
                    results);

            current.remove(current.size() - 1);
        }
    }
}

public class problemstat9 {

    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        long now = System.currentTimeMillis();

        transactions.add(new Transaction(1, 500,
                "Store A", "acc1", now));

        transactions.add(new Transaction(2, 300,
                "Store B", "acc2", now + 1000));

        transactions.add(new Transaction(3, 200,
                "Store C", "acc3", now + 2000));

        transactions.add(new Transaction(4, 500,
                "Store A", "acc4", now + 3000));

        FraudDetector detector = new FraudDetector();

        System.out.println("Two-Sum Results:");
        List<int[]> pairs =
                detector.findTwoSum(transactions, 500);

        for (int[] p : pairs) {
            System.out.println(Arrays.toString(p));
        }

        System.out.println("\nTwo-Sum within 1 hour:");
        List<int[]> windowPairs =
                detector.twoSumWithinWindow(transactions,
                        500,
                        3600000);

        for (int[] p : windowPairs) {
            System.out.println(Arrays.toString(p));
        }

        System.out.println("\nDuplicate Detection:");
        System.out.println(
                detector.detectDuplicates(transactions));

        System.out.println("\nK-Sum (k=3 target=1000):");
        System.out.println(
                detector.findKSum(transactions,
                        3,
                        1000));
    }
}
