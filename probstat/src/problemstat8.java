import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    public ParkingSpot() {
        this.occupied = false;
    }
}

class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int size = 0;

    private int totalProbes = 0;
    private int parkOperations = 0;

    public ParkingLot(int capacity) {

        this.capacity = capacity;
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    /**
     * Custom hash function for license plate
     */
    private int hash(String plate) {

        int hash = 0;

        for (char c : plate.toCharArray()) {
            hash = (hash * 31 + c) % capacity;
        }

        return hash;
    }

    /**
     * Park vehicle using linear probing
     */
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {

            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        size++;
        totalProbes += probes;
        parkOperations++;

        System.out.println(
                "parkVehicle(\"" + plate + "\") → Assigned spot #" +
                        index + " (" + probes + " probes)"
        );
    }

    /**
     * Exit vehicle
     */
    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].occupied) {

            if (table[index].licensePlate.equals(plate)) {

                long durationMillis =
                        System.currentTimeMillis() - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);

                double fee = hours * 5; // $5 per hour

                table[index].occupied = false;
                table[index].licensePlate = null;

                size--;

                System.out.println(
                        "exitVehicle(\"" + plate + "\") → Spot #" + index +
                                " freed, Duration: " +
                                String.format("%.2f", hours) +
                                "h, Fee: $" +
                                String.format("%.2f", fee)
                );

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found.");
    }

    /**
     * Find nearest available spot
     */
    public int findNearestSpot() {

        for (int i = 0; i < capacity; i++) {

            if (!table[i].occupied) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Parking statistics
     */
    public void getStatistics() {

        double occupancy =
                (size * 100.0) / capacity;

        double avgProbes =
                parkOperations == 0 ? 0 :
                        (double) totalProbes / parkOperations;

        System.out.println("\nParking Statistics:");
        System.out.println("Occupancy: " +
                String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " +
                String.format("%.2f", avgProbes));
    }
}

public class problemstat8 {

    public static void main(String[] args) throws Exception {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        int nearest = lot.findNearestSpot();

        System.out.println("Nearest available spot: #" + nearest);

        lot.getStatistics();
    }
}
