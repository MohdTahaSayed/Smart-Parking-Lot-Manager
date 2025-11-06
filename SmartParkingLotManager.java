import java.util.*;

class Vehicle {
    String number;
    String owner;
    int slot;

    Vehicle(String number, String owner, int slot) {
        this.number = number;
        this.owner = owner;
        this.slot = slot;
    }
}

public class SmartParkingLotManager {
    static final int TOTAL_SLOTS = 5;
    static Stack<Integer> availableSlots = new Stack<>();
    static Queue<Vehicle> waitingQueue = new LinkedList<>();
    static HashMap<String, Vehicle> parkedVehicles = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Initialize available slots (1 to TOTAL_SLOTS)
        for (int i = TOTAL_SLOTS; i >= 1; i--) {
            availableSlots.push(i);
        }

        while (true) {
            System.out.println("\n===== SMART PARKING LOT MANAGER =====");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Exit Vehicle");
            System.out.println("3. Show Parked Vehicles");
            System.out.println("4. Show Waiting Queue");
            System.out.println("5. Search Vehicle Details");
            System.out.println("6. Exit Program");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    parkVehicle(sc);
                    break;
                case 2:
                    exitVehicle(sc);
                    break;
                case 3:
                    showParkedVehicles();
                    break;
                case 4:
                    showWaitingQueue();
                    break;
                case 5:
                    searchVehicle(sc);
                    break;
                case 6:
                    System.out.println("Thank you! Exiting system...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    static void parkVehicle(Scanner sc) {
        System.out.print("Enter vehicle number: ");
        String number = sc.nextLine();
        System.out.print("Enter owner name: ");
        String owner = sc.nextLine();

        if (!availableSlots.isEmpty()) {
            int slot = availableSlots.pop();
            Vehicle v = new Vehicle(number, owner, slot);
            parkedVehicles.put(number, v);
            System.out.println("✅ Vehicle parked at slot " + slot);
        } else {
            System.out.println("⚠️ Parking full! Vehicle added to waiting queue.");
            waitingQueue.add(new Vehicle(number, owner, -1));
        }
    }

    static void exitVehicle(Scanner sc) {
        System.out.print("Enter vehicle number to exit: ");
        String number = sc.nextLine();

        if (parkedVehicles.containsKey(number)) {
            Vehicle v = parkedVehicles.remove(number);
            availableSlots.push(v.slot);
            System.out.println("🚗 Vehicle " + number + " exited from slot " + v.slot);

            if (!waitingQueue.isEmpty()) {
                Vehicle next = waitingQueue.poll();
                int newSlot = availableSlots.pop();
                next.slot = newSlot;
                parkedVehicles.put(next.number, next);
                System.out.println("✅ Vehicle from waiting queue parked at slot " + newSlot);
            }
        } else {
            System.out.println("❌ Vehicle not found in parking lot.");
        }
    }

    static void showParkedVehicles() {
        if (parkedVehicles.isEmpty()) {
            System.out.println("No vehicles currently parked.");
        } else {
            System.out.println("\n🚘 Currently Parked Vehicles:");
            for (Vehicle v : parkedVehicles.values()) {
                System.out.println("Slot " + v.slot + ": " + v.number + " (Owner: " + v.owner + ")");
            }
        }
    }

    static void showWaitingQueue() {
        if (waitingQueue.isEmpty()) {
            System.out.println("No vehicles in waiting queue.");
        } else {
            System.out.println("\n⏳ Waiting Queue:");
            for (Vehicle v : waitingQueue) {
                System.out.println(v.number + " (Owner: " + v.owner + ")");
            }
        }
    }

    static void searchVehicle(Scanner sc) {
        System.out.print("Enter vehicle number to search: ");
        String number = sc.nextLine();

        if (parkedVehicles.containsKey(number)) {
            Vehicle v = parkedVehicles.get(number);
            System.out.println("✅ Vehicle found in Slot " + v.slot + " (Owner: " + v.owner + ")");
        } else {
            boolean foundInQueue = false;
            for (Vehicle v : waitingQueue) {
                if (v.number.equals(number)) {
                    System.out.println("🚧 Vehicle is in waiting queue (Owner: " + v.owner + ")");
                    foundInQueue = true;
                    break;
                }
            }
            if (!foundInQueue)
                System.out.println("❌ Vehicle not found in system.");
        }
    }
}
