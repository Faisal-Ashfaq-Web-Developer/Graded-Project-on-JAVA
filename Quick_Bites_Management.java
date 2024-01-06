import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.text.ParseException;


class Quick_Bites_Management {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Welcome to Quick-Bites Restaurant Management System");
            System.out.println("1. Place Order");
            System.out.println("2. Cancel Order");
            System.out.println("3. Daily Collection Report");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    placeOrder();
                    break;
                case 2:
                    cancelOrder();
                    break;
                case 3:
                    generateCollectionReport();
                    break;
                case 4:
                    System.out.println("Exiting Quick-Bites Restaurant Management System. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void placeOrder() {
        System.out.println("=== Place Order ===");
        // Display menu
        GetFileData displayMenu = new GetFileData("menu.csv");
        displayMenu.displayCompleteData();

        // Get order details from the user
        System.out.print("Enter MenuID to add to the order: ");
        int menuId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Calculate total bill amount
        MenuItem menuItem = displayMenu.getMenuItem(menuId);
        double totalAmount = menuItem.getPrice() * quantity;

        // Create an Order object
        Order order = new Order(menuId, quantity, menuItem.getName(), quantity, totalAmount, new Date(), "Pending");

        // Save the order to the order details file
        GetFileData orderData = new GetFileData("order_details.csv");
        orderData.saveOrder(order);

        System.out.println("Order placed successfully!");
    }

    private static void cancelOrder() {
        System.out.println("=== Cancel Order ===");
        // Display pending orders
        GetFileData displayOrders = new GetFileData("order_details.csv");
        displayOrders.displayPendingOrders();

        // Get order ID to cancel
        System.out.print("Enter OrderID to cancel: ");
        int orderId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Cancel the order
        Order orderToCancel = displayOrders.getOrder(orderId);
        if (orderToCancel != null) {
            orderToCancel.setStatus("Cancelled");
            displayOrders.updateOrderStatus(orderToCancel);
            System.out.println("Order cancelled successfully!");
        } else {
            System.out.println("Order not found. Please enter a valid OrderID.");
        }
    }

    private static void generateCollectionReport() {
        System.out.println("=== Daily Collection Report ===");
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            CollectionReport collectionReport = new CollectionReport(date);

            // Display total collection for the given date
            GetFileData orderData = new GetFileData("order_details.csv");
            double totalCollection = orderData.getTotalCollectionForDate(date);
            collectionReport.setTotalCollection(totalCollection);

            collectionReport.displayCollectionReport();
        } catch (Exception e) {
            System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
        }
    }
}

class GetFileData {
    private String fileName;

    GetFileData(String fileName) {
        this.fileName = fileName;
    }

    void displayCompleteData() {
        // Implementation to display complete data from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void displayPendingOrders() {
        // Implementation to display pending orders from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                if ("Pending".equals(orderData[6])) { // Assuming status is at index 6
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    MenuItem getMenuItem(int menuId) {
        // Implementation to retrieve menu item details by MenuID
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] menuItemData = line.split(",");
                if (Integer.parseInt(menuItemData[0]) == menuId) { // Assuming MenuID is at index 0
                    return new MenuItem(Integer.parseInt(menuItemData[0]), menuItemData[1], Double.parseDouble(menuItemData[2])); // Assuming name is at index 1 and price is at index 2
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    Order getOrder(int orderId) {
        // Implementation to retrieve order details by OrderID
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                if (Integer.parseInt(orderData[0]) == orderId) { // Assuming OrderID is at index 0
                    return new Order(Integer.parseInt(orderData[0]), Integer.parseInt(orderData[1]), orderData[2], Integer.parseInt(orderData[3]), Double.parseDouble(orderData[4]), new SimpleDateFormat("yyyy-MM-dd").parse(orderData[5]), orderData[6]); // Assuming MenuID is at index 1, itemName is at index 2, quantity is at index 3, totalAmount is at index 4, date is at index 5, and status is at index 6
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    double getTotalCollectionForDate(Date date) {
        // Implementation to calculate total collection for a given date
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            double totalCollection = 0.0;
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                Date orderDate = new SimpleDateFormat("yyyy-MM-dd").parse(orderData[5]); // Assuming date is at index 5
                if (orderDate.equals(date)) {
                    totalCollection += Double.parseDouble(orderData[4]); // Assuming totalAmount is at index 4
                }
            }
            return totalCollection;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    void saveOrder(Order order) {
        // Implementation to save order details to the file
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(order.getOrderId() + "," + order.getMenuId() + "," + order.getItemName() + "," + order.getQuantity() + "," + order.getTotalAmount() + "," + new SimpleDateFormat("yyyy-MM-dd").format(order.getDate()) + "," + order.getStatus() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateOrderStatus(Order order) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            StringBuilder content = new StringBuilder();
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                int orderIdFromFile = Integer.parseInt(orderData[0]); // Assuming OrderID is at index 0

                // Check if the current line corresponds to the order to be updated
                if (orderIdFromFile == order.getOrderId()) {
                    // Update the status in the line
                    orderData[6] = order.getStatus(); // Assuming status is at index 6
                    line = String.join(",", orderData);
                }

                // Append the line (either modified or not) to the content
                content.append(line).append("\n");
            }

            reader.close();

            // Write the modified content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content.toString());
            writer.close();

            System.out.println("Order status updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MenuItem {
    private int menuId;
    private String name;
    private double price;

    // Constructor, getters, and setters

    public MenuItem(int menuId, String name, double price) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    // Additional methods or modifications as needed
}


class Order {
    private int orderId;
    private int menuId;
    private String itemName;
    private int quantity;
    private double totalAmount;
    private Date date;
    private String status;

    public Order(int orderId, int menuId, String itemName, int quantity, double totalAmount, Date date, String status) {
        this.orderId = orderId;
        this.menuId = menuId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.date = date;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

class CollectionReport {
    private Date date;
    private double totalCollection;

    // Constructor, getters, and setters

    public CollectionReport(Date date) {
        this.date = date;
        this.totalCollection = 0.0; // Default value, update as needed
    }

    public Date getDate() {
        return date;
    }

    public double getTotalCollection() {
        return totalCollection;
    }

    public void setTotalCollection(double totalCollection) {
        this.totalCollection = totalCollection;
    }

    // Additional methods or modifications as needed

    void displayCollectionReport() {
        // Implementation to display the collection report
        System.out.println("Date: " + date);
        System.out.println("Total Collection: $" + totalCollection);
    }
}
