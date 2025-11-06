# 🚗 Smart Parking Lot Manager

### 📘 Overview
**Smart Parking Lot Manager** is a Java-based console application that simulates real-world parking operations using fundamental **data structures** — **Queue**, **Stack**, and **HashMap**.  
It allows users to **enter** and **exit** cars, **track available and occupied slots**, manage a **waiting queue**, and **retrieve vehicle details** instantly.

---

### 🎯 Features
- 🅿️ **User-defined parking capacity** (entered at runtime)  
- 🚘 **Car entry** with automatic slot assignment  
- 🚙 **Car exit** with proper slot rearrangement using Stack  
- ⏳ **Waiting queue** for cars when parking is full (FIFO)  
- 🔍 **Quick vehicle lookup** using HashMap  
- 📋 **Display all parked and waiting vehicles**  
- 🧹 Efficiently demonstrates how data structures solve real-world problems

---

### 🧠 Data Structures Used
| Data Structure | Purpose | Implementation |
|----------------|----------|----------------|
| **Queue (FIFO)** | To manage cars waiting to enter when the parking lot is full | `Queue<Vehicle> waitingQueue = new LinkedList<>();` |
| **Stack (LIFO)** | To temporarily hold cars when an inner car needs to exit | `Stack<Vehicle> tempStack = new Stack<>();` |
| **HashMap** | To quickly store and retrieve vehicle details by number | `HashMap<String, Vehicle> parkedMap = new HashMap<>();` |

---

### 🖥️ How It Works
1. The user is prompted to **set the parking capacity**.
2. Cars can **enter** the parking if slots are available; otherwise, they go into a **waiting queue**.
3. When a car **exits**, the system uses a **Stack** to temporarily move vehicles if the car is not at the exit end.
4. Each car’s details are stored in a **HashMap** for fast lookup.
5. Users can **view** current parking status and **search** for any vehicle.

---
3. Compile:
   ```bash
   javac SmartParkingManager.java
