package app;
import domain.*;

import java.util.Scanner;
import java.io.IOException;
public class MainMenu {

    public static Scanner input = new Scanner(System.in);
    public static void main(String [] args){
        ClientUser newUser = new ClientUser();
        String option;
        // Load user data from users.txt to arraylist
        try {
            newUser.loadUser();
        }
        catch (IOException e) {
            System.out.println("Error to load the data from file.");
            e.printStackTrace();
            return;
        }

        do {
            System.out.print("""
                \n
                *========================================*
                *              ~Welcome to~              *
                *    Electronic Shop Inventory System    *
                *----------------------------------------*
                *   [1] User                             *
                *   [2] Admin                            *
                *   [3] Exit                             *
                *========================================*
                Enter option:\s""");
            option = input.nextLine();

            switch (option) {
                case "1" -> userInterface(); // Display user interface
                case "2" -> adminInterface(); // Display admin interface
                case "3" -> System.exit(0); // Terminate program
                default -> System.out.println("Invalid option. Please try again!");
            }
        } while (option != "3");
        input.close();
    }

    public static void userInterface(){
        ClientUser newUser = new ClientUser();
        String option;

        do {
            System.out.print("""
                \n
                *========================================*
                *               Client User              *
                *----------------------------------------*
                *   [1] Login                            *
                *   [2] Register                         *
                *   [3] Exit                             *
                *========================================*
                Enter option:\s""");
            option = input.nextLine();

            switch (option) {
                case "1" -> newUser.login(); // User login
                case "2" -> newUser.userRegister(); // User register
                case "3" -> {
                    return; // Return to main
                }
                default -> System.out.println("Invalid option. Please try again!");
            }
        } while (option != "3");
        input.close();
    }

    public static void adminInterface(){
        Administrator newAdmin = new Administrator();
        String option;

        do {
            System.out.print("""
                \n
                *========================================*
                *                 Admin                  *
                *----------------------------------------*
                *   [1] Login                            *
                *   [2] Exit                             *
                *========================================*
                Enter option:\s""");
            option = input.nextLine();

            switch (option) {
                case "1" -> newAdmin.login();
                case "2" -> { // Return to main
                    return;
                }
                default -> System.out.println("Invalid option. Please try again!");
            }
        } while (!option.equals("3"));
        input.close();
    }

    public static void userMenu(String currentUserPhone){
        String option;
        
        ClientUser currentUser = null;
 
        for (ClientUser user : ClientUser.registeredUsers) {

            String phone = user.getPhone();
            if (phone.equals(currentUserPhone)) {
                currentUser = user;
                break; // Exit the loop once the user is found
}

        }
        
        do {
            System.out.print("""
                \n
                *========================================*
                *               User Menu                *
                *----------------------------------------*
                *   [1] Make Orders                      *
                *   [2] Make Payment                     *
                *   [3] Manage Profile                   *
                *   [4] Logout                           *
                *========================================*
                Enter option:\s""");
            option = input.nextLine();

            switch (option) {
                case "1" -> makeOrderMenu(currentUserPhone);
                case "2" -> Payment.payment(currentUser);
                case "3" -> currentUser.profile(currentUser);
                case "4" -> {
                    System.out.println("Exiting...");
                    return; // Logout and return to main
                }
                default -> System.out.println("Invalid input. Please try again");
            }
        } while (true);
    }

    public static void makeOrderMenu(String currentUserPhone){
        String option;

        do {
            System.out.print("""
                \n
                *========================================*
                *               User Menu                *
                *----------------------------------------*
                *   [1] Add item to cart                 *
                *   [2] Modify cart                      *
                *   [3] Exit                             *
                *========================================*
                Enter option:\s""");
            option = input.nextLine();

            switch (option) {
                case "1" -> Cart.addToCart(currentUserPhone);
                case "2" -> Cart.modifyCart(currentUserPhone);
                case "3" -> {
                    System.out.println("Exiting...");
                    return; // Logout and return to main
                }
                default -> System.out.println("Invalid input. Please try again");
            }
        } while (true);
    }

    public static void adminMenu() {
        Inventory.loadInventory();
        int choice;
        do {
            System.out.print("""
                    \n
                    *============================================*
                    *                  Admin Menu                *
                    *--------------------------------------------*
                    *   [1] Manage Items                         *
                    *   [2] Update Customer Orders               *
                    *   [3] Manage Billing Statement             *
                    *   [4] Generate Inventory Report            *
                    *   [5] Generate Sales Report                *
                    *   [6] Logout                               *
                    *============================================*
                    Enter option:\s""");
            choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> managementInventoryMenu();
                case 2 -> updateCustomerOrdersMenu();
                case 3 -> Report.billingStatement();
                case 4 -> Report.showInventory();
                case 5 -> Report.salesReport();
                case 6 -> {
                    System.out.println("Exiting...");
                    return; // logout
                }
                default -> {
                    System.out.println("Invalid choice, please try again.");
                    input.nextLine();
                }
            }
        } while (choice != 6);
    }

    public static void managementInventoryMenu() {
        
        int choice;
        do {
            Inventory.showInventory();
            
            System.out.print("""
                Above is current inventory.
                *============================================*
                *                  Manage Item               *
                *--------------------------------------------*
                *   [1] Create Item                          *
                *   [2] Modify Item                          *
                *   [3] Remove Item                          *
                *   [4] Exit                                 *
                *============================================*
                Enter option:\s""");
            choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> Inventory.createItem(input);
                case 2 -> Inventory.modifyItem(input);
                case 3 -> Inventory.removeItem(input);
                case 4 -> {
                    Inventory.saveInventory();
                    Inventory.inventoryList.clear(); // Clear the inventory array list after saved
                    System.out.println("Exiting...");
                    return; // Return to main
                }
                default -> {
                    System.out.println("Invalid choice, please try again.");
                    input.nextLine();
                }
            }
        } while (choice != 4);
    }

    public static void updateCustomerOrdersMenu() {
        int choice;
        do {
            System.out.print("""
                    \n
                    *============================================*
                    *            Update Customer Order           *
                    *--------------------------------------------*
                    *   [1] Update Order Status                  *
                    *   [2] Exit                                 *
                    *============================================*
                    Enter option:\s""");
            choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> Cart.updateOrderStatus();
                case 2 -> {
                    System.out.println("Exiting...");
                    return; // Return to main
                }
                default -> {
                    System.out.println("Invalid choice, please try again.");
                    input.nextLine();
                }
            }
        } while (choice != 2);
    }
}