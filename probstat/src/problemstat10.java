import java.util.*;

class VideoData {

    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

/* LRU Cache using LinkedHashMap */
class LRUCache<K, V> extends LinkedHashMap<K, V> {

    private int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

class MultiLevelCache {

    private LRUCache<String, VideoData> L1;
    private HashMap<String, VideoData> L2;
    private HashMap<String, VideoData> database;

    private HashMap<String, Integer> accessCount;

    private int l1Hits = 0;
    private int l2Hits = 0;
    private int l3Hits = 0;

    public MultiLevelCache() {

        L1 = new LRUCache<>(10000);
        L2 = new HashMap<>();
        database = new HashMap<>();
        accessCount = new HashMap<>();

        /* Simulated database content */
        database.put("video_123", new VideoData("video_123", "Movie Data"));
        database.put("video_999", new VideoData("video_999", "Sports Highlights"));
    }

    public VideoData getVideo(String videoId) {

        long start = System.nanoTime();

        /* L1 Cache */
        if (L1.containsKey(videoId)) {

            l1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        /* L2 Cache */
        if (L2.containsKey(videoId)) {

            l2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData data = L2.get(videoId);

            promoteToL1(videoId, data);

            return data;
        }

        System.out.println("L2 Cache MISS");

        /* L3 Database */
        if (database.containsKey(videoId)) {

            l3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData data = database.get(videoId);

            L2.put(videoId, data);

            accessCount.put(videoId,
                    accessCount.getOrDefault(videoId, 0) + 1);

            return data;
        }

        System.out.println("Video not found.");
        return null;
    }

    private void promoteToL1(String videoId, VideoData data) {

        accessCount.put(videoId,
                accessCount.getOrDefault(videoId, 0) + 1);

        if (accessCount.get(videoId) > 2) {

            L1.put(videoId, data);

            System.out.println("Promoted to L1 Cache");
        }
    }

    public void invalidate(String videoId) {

        L1.remove(videoId);
        L2.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    public void getStatistics() {

        int total = l1Hits + l2Hits + l3Hits;

        System.out.println("\nCache Statistics:");

        if (total == 0) return;

        System.out.println("L1 Hit Rate: "
                + (l1Hits * 100.0 / total) + "%");

        System.out.println("L2 Hit Rate: "
                + (l2Hits * 100.0 / total) + "%");

        System.out.println("L3 Hit Rate: "
                + (l3Hits * 100.0 / total) + "%");

        double avg =
                (l1Hits * 0.5 + l2Hits * 5 + l3Hits * 150) / total;

        System.out.println("Average Access Time: "
                + avg + " ms");
    }
}

public class problemstat10 {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        System.out.println("First Request:");
        cache.getVideo("video_123");

        System.out.println("\nSecond Request:");
        cache.getVideo("video_123");

        System.out.println("\nAnother Video:");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}
