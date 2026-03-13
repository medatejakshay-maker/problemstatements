import java.util.*;

class InventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> inventory = new HashMap<>();

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();

    /**
     * Add product with stock
     */
    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    /**
     * Check stock availability
     */
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    /**
     * Purchase item (thread-safe)
     */
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = inventory.getOrDefault(productId, 0);

        if (stock > 0) {
            stock--;
            inventory.put(productId, stock);

            return "Success, " + stock + " units remaining";
        }
        else {

            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

            int position = queue.size() + 1;
            queue.put(userId, position);

            return "Added to waiting list, position #" + position;
        }
    }

    /**
     * View waiting list
     */
    public void showWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        System.out.println("Waiting List:");

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() + " → Position #" + entry.getValue());
        }
    }
}

public class problemstat2 {

    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        // Add product with 100 units
        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println("checkStock(\"IPHONE15_256GB\") → "
                + manager.checkStock("IPHONE15_256GB") + " units available");

        // Simulate purchases
        for (int i = 1; i <= 102; i++) {

            String result = manager.purchaseItem("IPHONE15_256GB", 10000 + i);

            if (i <= 3 || i > 100) { // print some sample outputs
                System.out.println("purchaseItem(\"IPHONE15_256GB\", userId="
                        + (10000 + i) + ") → " + result);
            }
        }

        // Show waiting list
        manager.showWaitingList("IPHONE15_256GB");
    }
}