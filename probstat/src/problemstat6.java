import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    int maxTokens;
    double refillRate; // tokens per second
    double tokens;
    long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    /**
     * Refill tokens based on elapsed time
     */
    private void refill() {

        long now = System.currentTimeMillis();

        double tokensToAdd =
                ((now - lastRefillTime) / 1000.0) * refillRate;

        tokens = Math.min(maxTokens, tokens + tokensToAdd);

        lastRefillTime = now;
    }

    /**
     * Try to consume one token
     */
    public synchronized boolean allowRequest() {

        refill();

        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return (int) tokens;
    }
}

class RateLimiter {

    // clientId -> TokenBucket
    private ConcurrentHashMap<String, TokenBucket> clients =
            new ConcurrentHashMap<>();

    private final int MAX_REQUESTS = 1000;
    private final int WINDOW_SECONDS = 3600;

    /**
     * Check if request is allowed
     */
    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(MAX_REQUESTS,
                        MAX_REQUESTS / (double) WINDOW_SECONDS));

        TokenBucket bucket = clients.get(clientId);

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            return "Allowed (" +
                    bucket.getRemainingTokens() +
                    " requests remaining)";
        }

        return "Denied (0 requests remaining, retry later)";
    }

    /**
     * Get client rate limit status
     */
    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "Client not found";
        }

        int remaining = bucket.getRemainingTokens();

        int used = MAX_REQUESTS - remaining;

        return "{used: " + used +
                ", limit: " + MAX_REQUESTS +
                ", remaining: " + remaining + "}";
    }
}

public class problemstat6{

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String clientId = "abc123";

        for (int i = 0; i < 5; i++) {

            System.out.println(
                    "checkRateLimit(" + clientId + ") → "
                            + limiter.checkRateLimit(clientId));
        }

        System.out.println();

        System.out.println(
                "getRateLimitStatus(\"abc123\") → "
                        + limiter.getRateLimitStatus(clientId));
    }
}