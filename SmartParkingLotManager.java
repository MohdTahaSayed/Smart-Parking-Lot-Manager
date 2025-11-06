import java.util.*;
import java.io.*;

class Vehicle {
    String regNo;
    String owner;
    int slotId; // -1 => waiting, -2 => temporarily moved

    Vehicle(String regNo, String owner, int slotId) {
        this.regNo = regNo;
        this.owner = owner;
        this.slotId = slotId;
    }

    @Override
    public String toString() {
        String state = (slotId == -1) ? "Waiting" : (slotId == -2) ? "TemporarilyMoved" : "Slot " + slotId;
        return regNo + " | " + owner + " | " + state;
    }
}

public class SmartParkingManager {
    private final int capacity;
    private final TreeSet<Integer> freeSlots; // smallest free slot first
    private final String[] slots;             // index 1..capacity -> regNo or null
    private final Queue<Vehicle> waitingQueue; // FIFO
    private final Stack<Map.Entry<Vehicle,Integer>> tempStack; // moved cars (vehicle, origSlot)
    private final HashMap<String, Vehicle> vehicleMap; // regNo -> Vehicle

    public SmartParkingManager(int capacity) {
        this.capacity = capacity;
        this.freeSlots = new TreeSet<>();
        for (int i = 1; i <= capacity; i++) freeSlots.add(i);
        this.slots = new String[capacity + 1];
        this.waitingQueue = new LinkedList<>();
        this.tempStack = new Stack<>();
        this.vehicleMap = new HashMap<>();
    }

    // Enter vehicle: park if free slot exists (smallest-numbered), else enqueue
    public void enterVehicle(String regNo, String owner) {
        if (vehicleMap.containsKey(regNo)) {
            System.out.println("[WARN] Vehicle already exists in system: " + regNo);
            return;
        }
        if (!freeSlots.isEmpty()) {
            int slot = freeSlots.first();
            freeSlots.remove(slot);
            slots[slot] = regNo;
            Vehicle v = new Vehicle(regNo, owner, slot);
            vehicleMap.put(regNo, v);
            System.out.println("[PARKED] " + regNo + " -> Slot " + slot + " (" + owner + ")");
        } else {
            Vehicle v = new Vehicle(regNo, owner, -1);
            waitingQueue.add(v);
            vehicleMap.put(regNo, v);
            System.out.println("[WAITING] Parking full — added to waiting queue: " + regNo + " (" + owner + ")");
        }
    }

    // Exit vehicle by registration number — handles moved cars and waiting queue
    public void exitVehicle(String regNo) {
        Vehicle v = vehicleMap.get(regNo);
        if (v == null) {
            System.out.println("[ERROR] Vehicle not found: " + regNo);
            return;
        }
        if (v.slotId == -1) { // in waiting queue
            boolean removed = waitingQueue.removeIf(x -> x.regNo.equals(regNo));
            vehicleMap.remove(regNo);
            System.out.println(removed ? "[REMOVED] Removed from waiting queue: " + regNo
                                      : "[INFO] Was not in waiting queue: " + regNo);
            return;
        }

        int leavingSlot = v.slotId;
        System.out.println("[EXIT] Vehicle leaving: " + regNo + " from slot " + leavingSlot);

        // Move blocking cars parked after leavingSlot to tempStack (preserve original slot)
        for (int i = capacity; i > leavingSlot; i--) {
            if (slots[i] != null) {
                String rn = slots[i];
                Vehicle mv = vehicleMap.get(rn);
                slots[i] = null;
                mv.slotId = -2; // temporarily moved
                tempStack.push(new AbstractMap.SimpleEntry<>(mv, i));
                freeSlots.add(i); // mark slot as free temporarily
                System.out.println("  [MOVE-OUT] " + rn + " from slot " + i);
            }
        }

        // Remove leaving car
        slots[leavingSlot] = null;
        freeSlots.add(leavingSlot);
        vehicleMap.remove(regNo);
        System.out.println("  [FREED] Slot " + leavingSlot);

        // Move back cars to their ORIGINAL slots when possible
        while (!tempStack.isEmpty()) {
            Map.Entry<Vehicle,Integer> entry = tempStack.pop();
            Vehicle back = entry.getKey();
            int orig = entry.getValue();
            if (freeSlots.contains(orig)) {
                freeSlots.remove(orig);
                slots[orig] = back.regNo;
                back.slotId = orig;
                System.out.println("  [MOVE-BACK] " + back.regNo + " -> Original slot " + orig);
            } else if (!freeSlots.isEmpty()) {
                int assign = freeSlots.first();
                freeSlots.remove(assign);
                slots[assign] = back.regNo;
                back.slotId = assign;
                System.out.println("  [MOVE-BACK-FALLBACK] " + back.regNo + " -> Slot " + assign + " (orig " + orig + " busy)");
            } else {
                System.out.println("  [ERROR] No free slot to move back " + back.regNo);
            }
            // ensure vehicleMap has accurate mapping (it already does)
            vehicleMap.put(back.regNo, back);
        }

        // Assign waiting vehicles (FIFO) to available slots (smallest-first)
        while (!waitingQueue.isEmpty() && !freeSlots.isEmpty()) {
            Vehicle next = waitingQueue.poll();
            int assign = freeSlots.first();
            freeSlots.remove(assign);
            slots[assign] = next.regNo;
            next.slotId = assign;
            vehicleMap.put(next.regNo, next);
            System.out.println("  [ASSIGNED] From waiting queue: " + next.regNo + " -> Slot " + assign + " (" + next.owner + ")");
        }
    }

    // Fast lookup using HashMap
    public void findVehicle(String regNo) {
        Vehicle v = vehicleMap.get(regNo);
        if (v == null) {
            System.out.println("[NOT FOUND] " + regNo);
            return;
        }
        if (v.slotId == -1) System.out.println("[FOUND] In waiting queue: " + v);
        else if (v.slotId == -2) System.out.println("[FOUND] Temporarily moved: " + v);
        else System.out.println("[FOUND] " + v);
    }

    // Display full status
    public void displayStatus() {
        System.out.println("\n--- Parking Status ---");
        for (int i = 1; i <= capacity; i++) {
            System.out.printf("Slot %2d : %s%n", i, (slots[i] == null ? "[Empty]" : slots[i]));
        }
        System.out.println("Free slots (smallest-first): " + freeSlots);
        System.out.println("Waiting queue:");
        if (waitingQueue.isEmpty()) System.out.println("  [Empty]");
        else for (Vehicle w : waitingQueue) System.out.println("  " + w.regNo + " | " + w.owner);
        System.out.println("Total vehicles tracked (parked + waiting): " + vehicleMap.size());
        System.out.println("----------------------\n");
    }

    // Interactive console
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter parking capacity (positive integer): ");
        int cap = 0;
        while (cap <= 0) {
            try {
                String line = br.readLine();
                cap = Integer.parseInt(line.trim());
                if (cap <= 0) System.out.print("Please enter a positive integer: ");
            } catch (Exception e) {
                System.out.print("Invalid input. Enter a positive integer for capacity: ");
            }
        }

        SmartParkingManager mgr = new SmartParkingManager(cap);
        System.out.println("Smart Parking Manager started with capacity = " + cap + ".");

        while (true) {
            System.out.println("\nMenu: 1-Enter 2-Exit 3-Find 4-Status 5-Quit");
            System.out.print("Choice: ");
            String ch = br.readLine().trim();
            if (ch.equals("1")) {
                System.out.print("Registration No: ");
                String reg = br.readLine().trim();
                System.out.print("Owner Name: ");
                String owner = br.readLine().trim();
                mgr.enterVehicle(reg, owner);
            } else if (ch.equals("2")) {
                System.out.print("Registration No to exit: ");
                String reg = br.readLine().trim();
                mgr.exitVehicle(reg);
            } else if (ch.equals("3")) {
                System.out.print("Registration No to find: ");
                String reg = br.readLine().trim();
                mgr.findVehicle(reg);
            } else if (ch.equals("4")) {
                mgr.displayStatus();
            } else if (ch.equals("5")) {
                System.out.println("Exiting. Bye.");
                break;
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }
}

