// w1998799 - 20220112 - Vidath Kumarasiri
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class FoodCenterProgram {
    private static final int MAX_QUEUE_SIZE_1 = 2;
    private static final int MAX_QUEUE_SIZE_2 = 3;
    private static final int MAX_QUEUE_SIZE_3 = 5;
    private static final int MAX_BURGERS = 50;
    private static final int BURGERS_PER_CUSTOMER = 5;

    private static int stock = MAX_BURGERS;
    private static final Queue<String> queue1 = new LinkedList<>();
    private static final Queue<String> queue2 = new LinkedList<>();
    private static final Queue<String> queue3 = new LinkedList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String preference;
        do {
            displayMenu();
            System.out.print("Enter your preference: ");
            preference = scanner.nextLine().toUpperCase();

            switch (preference) {
                case "100":
                case "VFQ":
                    viewAllQueues();
                    break;
                case "101":
                case "VEQ":
                    viewEmptyQueues();
                    break;
                case "102":
                case "ACQ":
                    addCustomerToQueue(scanner);
                    break;
                case "103":
                case "RCQ":
                    removeCustomerFromQueue(scanner);
                    break;
                case "104":
                case "PCQ":
                    removeServedCustomer();
                    break;
                case "105":
                case "VCS":
                    viewCustomersSorted();
                    break;
                case "106":
                case "SPD":
                    storeProgramData();
                    break;
                case "107":
                case "LPD":
                    loadProgramData();
                    break;
                case "108":
                case "STK":
                    viewRemainingStock();
                    break;
                case "109":
                case "AFS":
                    addBurgersToStock(scanner);
                    break;
                case "999":
                case "EXT":
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!preference.equals("999") && !preference.equals("EXT"));
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\nFoodies Fave Food Center");
        System.out.println("************************");
        System.out.println("100 or VFQ: View all Queues.");
        System.out.println("101 or VEQ: View all Empty Queues.");
        System.out.println("102 or ACQ: Add customer to a Queue.");
        System.out.println("103 or RCQ: Remove a customer from a Queue. (From a specific location)");
        System.out.println("104 or PCQ: Remove a served customer.");
        System.out.println("105 or VCS: View Customers Sorted in alphabetical order");
        System.out.println("106 or SPD: Store Program Data into file.");
        System.out.println("107 or LPD: Load Program Data from file.");
        System.out.println("108 or STK: View Remaining burgers Stock.");
        System.out.println("109 or AFS: Add burgers to Stock.");
        System.out.println("999 or EXT: Exit the Program.");
    }

    private static void viewAllQueues() {
        System.out.println("*****************");
        System.out.println("*   Cashiers    *");
        System.out.println("*****************");

        Queue<String>[] queues = new Queue[]{queue1, queue2, queue3};
        int maxQueueSize = Math.max(Math.max(MAX_QUEUE_SIZE_1, MAX_QUEUE_SIZE_2), MAX_QUEUE_SIZE_3);

        for (int i = 0; i < maxQueueSize; i++) {
            for (Queue<String> queue : queues) {
                String queueStatus = getQueueStatus(queue, getMaxQueueSize(queue));

                if (i < queue.size()) {
                    System.out.print("| X ");
                } else if (i < getMaxQueueSize(queue)) {
                    System.out.print("| O ");
                } else {
                    System.out.print("|   ");
                }
            }

            System.out.println("|");
        }
    }

    private static String getQueueStatus(Queue<String> queue, int maxSize) {
        StringBuilder sb = new StringBuilder();
        int occupiedSlots = queue.size();
        int emptySlots = maxSize - occupiedSlots;

        for (int i = 0; i < occupiedSlots; i++) {
            sb.append("O ");
        }

        for (int i = 0; i < emptySlots; i++) {
            sb.append("X ");
        }

        return sb.toString().trim();
    }

    private static void viewEmptyQueues() {
        System.out.println("Empty Queues:");

        if (queue1.isEmpty()) {
            System.out.println("Queue 1 is empty.");
        }

        if (queue2.isEmpty()) {
            System.out.println("Queue 2 is empty.");
        }

        if (queue3.isEmpty()) {
            System.out.println("Queue 3 is empty.");
        }
    }

    private static void addCustomerToQueue(Scanner scanner) {
        System.out.print("Enter the queue number (1, 2, or 3): ");
        int queueNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Queue<String> queue = getQueue(queueNumber);
        if (queue == null) {
            System.out.println("Invalid queue number. Please try again.");
            return;
        }

        if (queue.size() == getMaxQueueSize(queue)) {
            System.out.println("Queue " + queueNumber + " is full. Cannot add more customers.");
            return;
        }

        System.out.print("Enter the customer name: ");
        String customerName = scanner.nextLine();

        if (stock < BURGERS_PER_CUSTOMER) {
            System.out.println("Not enough burgers in stock to serve the customer.");
            return;
        }

        queue.offer(customerName);
        stock -= BURGERS_PER_CUSTOMER;

        if (stock <= 10) {
            System.out.println("Warning: Low stock! Remaining burgers: " + stock);
        }

        System.out.println("Customer " + customerName + " added to Queue " + queueNumber + ".");
    }



    private static void removeCustomerFromQueue(Scanner scanner) {
        System.out.print("Enter the queue number (1, 2, or 3): ");
        int queueNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Queue<String> queue = getQueue(queueNumber);
        if (queue == null) {
            System.out.println("Invalid queue number. Please try again.");
            return;
        }

        if (queue.isEmpty()) {
            System.out.println("Queue " + queueNumber + " is empty. Cannot remove customer.");
            return;
        }

        System.out.print("Enter the position of the customer to remove (1-" + queue.size() + "): ");
        int position = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (position < 1 || position > queue.size()) {
            System.out.println("Invalid position. Please try again.");
            return;
        }

        String removedCustomer;
        Queue<String> tempQueue = new LinkedList<>();

        // Remove customers before the specified position and store them in the temporary queue
        for (int i = 1; i < position; i++) {
            tempQueue.offer(queue.poll());
        }

        // Remove the customer at the specified position
        removedCustomer = queue.poll();

        // Add the customers back to the original queue
        while (!tempQueue.isEmpty()) {
            queue.offer(tempQueue.poll());
        }

        stock += BURGERS_PER_CUSTOMER;

        System.out.println("Customer " + removedCustomer + " removed from Queue " + queueNumber + ".");
    }


    private static void removeServedCustomer() {
        if (!queue1.isEmpty()) {
            String removedCustomer = queue1.poll();
            stock += BURGERS_PER_CUSTOMER;
            System.out.println("Customer " + removedCustomer + " removed.");
        } else if (!queue2.isEmpty()) {
            String removedCustomer = queue2.poll();
            stock += BURGERS_PER_CUSTOMER;
            System.out.println("Customer " + removedCustomer + " removed.");
        } else if (!queue3.isEmpty()) {
            String removedCustomer = queue3.poll();
            stock += BURGERS_PER_CUSTOMER;
            System.out.println("Customer " + removedCustomer + " removed.");
        } else {
            System.out.println("No customers to remove.");
        }
    }

    private static void viewCustomersSorted() {
        System.out.println("Customers Sorted in alphabetical order:");

        Queue<String> allCustomers = new LinkedList<>();
        allCustomers.addAll(queue1);
        allCustomers.addAll(queue2);
        allCustomers.addAll(queue3);

        String[] sortedCustomers = sortCustomers(allCustomers.toArray(new String[0]));

        for (String customer : sortedCustomers) {
            System.out.println(customer);
        }
    }

    private static String[] sortCustomers(String[] customers) {
        // Bubble sort algorithm (not efficient for large arrays, but fine for small inputs)
        int n = customers.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (customers[j].compareTo(customers[j + 1]) > 0) {
                    String temp = customers[j];
                    customers[j] = customers[j + 1];
                    customers[j + 1] = temp;
                }
            }
        }
        return customers;
    }

    private static void storeProgramData() {
        try (PrintWriter writer = new PrintWriter("program_data.txt")) {
            writer.println(stock);

            writer.println(queue1.size());
            for (String customer : queue1) {
                writer.println(customer);
            }

            writer.println(queue2.size());
            for (String customer : queue2) {
                writer.println(customer);
            }

            writer.println(queue3.size());
            for (String customer : queue3) {
                writer.println(customer);
            }

            System.out.println("Program data stored successfully.");
        } catch (IOException e) {
            System.out.println("Error storing program data: " + e.getMessage());
        }
    }

    private static void loadProgramData() {
        try (Scanner fileScanner = new Scanner(new File("program_data.txt"))) {
            stock = Integer.parseInt(fileScanner.nextLine());

            int queue1Size = Integer.parseInt(fileScanner.nextLine());
            for (int i = 0; i < queue1Size; i++) {
                queue1.offer(fileScanner.nextLine());
            }

            int queue2Size = Integer.parseInt(fileScanner.nextLine());
            for (int i = 0; i < queue2Size; i++) {
                queue2.offer(fileScanner.nextLine());
            }

            int queue3Size = Integer.parseInt(fileScanner.nextLine());
            for (int i = 0; i < queue3Size; i++) {
                queue3.offer(fileScanner.nextLine());
            }

            System.out.println("Program data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Program data file not found.");
        }
    }

    private static void viewRemainingStock() {
        System.out.println("Remaining burgers in stock: " + stock);
    }

    private static void addBurgersToStock(Scanner scanner) {
        System.out.print("Enter the number of burgers to add to stock: ");
        int burgersToAdd = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (burgersToAdd <= 0) {
            System.out.println("Invalid number of burgers. Please try again.");
            return;
        }

        stock += burgersToAdd;
        System.out.println("Burgers added to stock. Remaining burgers: " + stock);
    }

    private static Queue<String> getQueue(int queueNumber) {
        switch (queueNumber) {
            case 1:
                return queue1;
            case 2:
                return queue2;
            case 3:
                return queue3;
            default:
                return null;
        }
    }

    private static int getMaxQueueSize(Queue<String> queue) {
        if (queue == queue1) {
            return MAX_QUEUE_SIZE_1;
        } else if (queue == queue2) {
            return MAX_QUEUE_SIZE_2;
        } else if (queue == queue3) {
            return MAX_QUEUE_SIZE_3;
        } else {
            return 0;
        }
    }


}
