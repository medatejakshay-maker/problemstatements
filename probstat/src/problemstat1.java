import java.util.*;

class UsernameAvailabilityChecker {

    // Stores username -> userId
    private HashMap<String, Integer> users = new HashMap<>();

    // Stores username -> attempt frequency
    private HashMap<String, Integer> attempts = new HashMap<>();

    /**
     * Check if username is available
     */
    public boolean checkAvailability(String username) {

        // Track attempt frequency
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        // O(1) lookup
        return !users.containsKey(username);
    }

    /**
     * Register a username
     */
    public void registerUsername(String username, int userId) {
        users.put(username, userId);
    }

    /**
     * Suggest alternative usernames
     */
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            String suggestion = username + i;
            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Modify with dot
        String dotVersion = username.replace("_", ".");
        if (!users.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }

    /**
     * Get most attempted username
     */
    public String getMostAttempted() {

        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }
}

public class problemstat1 {

    public static void main(String[] args) {

        UsernameAvailabilityChecker checker = new UsernameAvailabilityChecker();

        // Existing users
        checker.registerUsername("john_doe", 101);
        checker.registerUsername("admin", 102);
        checker.registerUsername("player1", 103);

        // Check availability
        System.out.println("checkAvailability(\"john_doe\") → "
                + checker.checkAvailability("john_doe"));

        System.out.println("checkAvailability(\"jane_smith\") → "
                + checker.checkAvailability("jane_smith"));

        // Suggestions
        System.out.println("suggestAlternatives(\"john_doe\") → "
                + checker.suggestAlternatives("john_doe"));

        // Simulate multiple attempts
        for (int i = 0; i < 5; i++) {
            checker.checkAvailability("admin");
        }

        for (int i = 0; i < 3; i++) {
            checker.checkAvailability("john_doe");
        }

        // Most attempted username
        System.out.println("getMostAttempted() → "
                + checker.getMostAttempted());
    }
}
