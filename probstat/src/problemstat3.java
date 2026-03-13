import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private final int MAX_CACHE_SIZE = 5;

    // LRU cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache =
            new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };

    private int hits = 0;
    private int misses = 0;

    /**
     * Resolve domain name
     */
    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            long time = System.nanoTime() - start;
            System.out.println("Cache HIT → " + entry.ipAddress + " (" + time / 1_000_000.0 + " ms)");
            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
            System.out.println("Cache EXPIRED → querying upstream");
        }

        misses++;

        // Simulate upstream DNS lookup
        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 5)); // TTL = 5 seconds

        long time = System.nanoTime() - start;

        System.out.println("Cache MISS → " + ip + " (" + time / 1_000_000.0 + " ms)");

        return ip;
    }

    /**
     * Simulated upstream DNS lookup
     */
    private String queryUpstreamDNS(String domain) {

        try {
            Thread.sleep(100); // simulate 100ms DNS query
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "172.217." + new Random().nextInt(100) + "." + new Random().nextInt(255);
    }

    /**
     * Cache statistics
     */
    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("Cache Stats:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }

    /**
     * Remove expired entries periodically
     */
    public void cleanupExpiredEntries() {

        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> entry = it.next();

            if (entry.getValue().isExpired()) {
                it.remove();
            }
        }
    }
}

public class problemstat3 {

    public static void main(String[] args) throws Exception {

        DNSCache cache = new DNSCache();

        cache.resolve("google.com");
        cache.resolve("google.com");

        Thread.sleep(6000); // wait for TTL expiration

        cache.resolve("google.com");

        cache.resolve("facebook.com");
        cache.resolve("youtube.com");
        cache.resolve("amazon.com");
        cache.resolve("netflix.com");
        cache.resolve("openai.com"); // triggers LRU eviction

        cache.getCacheStats();
    }
}