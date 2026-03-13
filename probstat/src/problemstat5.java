import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    public PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsEngine {

    // Page URL -> visit count
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // Page URL -> unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // Traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    /**
     * Process incoming event
     */
    public synchronized void processEvent(PageEvent event) {

        // Update page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Track traffic source
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    /**
     * Get top 10 pages
     */
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);

        return result;
    }

    /**
     * Print dashboard
     */
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====\n");

        System.out.println("Top Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url +
                    " - " + views + " views (" +
                    unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values()
                .stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent =
                    (entry.getValue() * 100.0) / total;

            System.out.println(entry.getKey() +
                    ": " + String.format("%.1f", percent) + "%");
        }

        System.out.println("\n===============================\n");
    }
}

public class problemstat5 {

    public static void main(String[] args) throws Exception {

        AnalyticsEngine engine = new AnalyticsEngine();

        Random random = new Random();

        String[] pages = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-update",
                "/world/economy",
                "/entertainment/movie-review"
        };

        String[] sources = {
                "google", "facebook", "direct", "twitter"
        };

        // Simulate streaming events
        for (int i = 1; i <= 50; i++) {

            PageEvent event = new PageEvent(
                    pages[random.nextInt(pages.length)],
                    "user_" + random.nextInt(20),
                    sources[random.nextInt(sources.length)]
            );

            engine.processEvent(event);

            // Update dashboard every 5 seconds
            if (i % 10 == 0) {
                engine.getDashboard();
                Thread.sleep(5000);
            }
        }
    }
}
