package domain;
import app.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cart {

    public static List<String> cartList = new ArrayList<>();

    public static void updateOrderStatus() {

        String fileName = "cart.txt";
        StringBuilder sb = new StringBuilder();

        Set<String> phoneNumbers = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\*");
                phoneNumbers.add(parts[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Available phone numbers:");
        for (String phoneNumber : phoneNumbers) {
            System.out.println(phoneNumber);
        }

        System.out.println("Editing an item");
        System.out.print("Enter the user's phone number to fulfill all orders OR Type 'esc' to return: ");
        String phoneNumberToSearch = MainMenu.input.nextLine();
        if (phoneNumberToSearch.equalsIgnoreCase("esc")) {
            return; // Return to the main menu
        }
    
        boolean statusToUpdate = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\*");
                if (parts[0].equals(phoneNumberToSearch) && parts[6].equals("Unfulfilled") && parts[7].equals("Paid")) {
                    parts[6] = "fulfilled"; // Update deliveryStatus
                    statusToUpdate = true;
                }
                sb.append(String.join("*", parts)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        if(statusToUpdate){
            // Write back to the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
                bw.write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Order status is successfully updated.");
            MainMenu.input.nextLine();
            Report.billingStatement();
        }else{
            System.out.println("There is no order of this customer can be updated.");
            MainMenu.input.nextLine();
        }
    }
    

    // Method to save the cart to a file
    public static void saveCart() {
        String fileName = "cart.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String cartItem : cartList) {
                writer.println(cartItem);
            }
            System.out.println("Cart saved to " + fileName);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load the cart from a file
    public static void loadCart() {
        try (BufferedReader reader = new BufferedReader(new FileReader("cart.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                cartList.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading data from file.");
            e.printStackTrace();
        }
    }

    // Implementation of the showCart method
    public static void showCart(String currentUserPhone) {

        if (cartList.isEmpty()) {
            System.out.println("No item is found in the cart.");
            System.out.print("Press Enter to continue...");
            MainMenu.input.nextLine();
        }else{
            System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------");
            String format = "| %-8s | %-10s | %-30s | %-15s | %-17s |%-18s |%-18s |%n";

            System.out.format(format, "Item ID", "Item Name", "Item Description", "Price", "Quantity","Order Status", "Payment Status");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

            for (String cartItem : cartList) {
                String[] itemDetails = cartItem.split("\\*");
                if (itemDetails[0].equals(currentUserPhone) && itemDetails[7].equals("Pending")) {
                    System.out.format(format, itemDetails[1], itemDetails[2], itemDetails[3], "RM" + itemDetails[4],
                            itemDetails[5], itemDetails[6], itemDetails[7]);
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    // Method for making a addToCart //addon
    public static void addToCart(String currentUserPhone) {
        Inventory.inventoryList.clear();
        String option;
        
        Inventory.loadInventory();
        
        loadCart();
        while (true) {
            try {
                Inventory.showInventory();

                System.out.print("Enter options using itemId (or 'esc' to exit and save): ");
                option = MainMenu.input.nextLine();

                if (option.equalsIgnoreCase("esc")) {
                    saveCart();
                    Inventory.inventoryList.clear();
                    cartList.clear();
                    return;
                }

                System.out.print("Enter quantity: ");
                int quantity = MainMenu.input.nextInt();

                int index = Inventory.findItemIndex(option);
                if (index == -1) {
                    System.out.println("Item not found.");
                    MainMenu.input.nextLine();
                } else if (quantity <= 0) {
                    System.out.println("Quantity must over 0.");
                    MainMenu.input.nextLine();
                } else {

                    int existedOrderQuantity = 0;
                    int cartIndexToModify = -1;

                    // Check if the class exists in the cart for the current user
                    for (int i = 0 ; i<cartList.size(); i++){
                        String cartItem = Cart.cartList.get(i);
                        String[] cartDetails = cartItem.split("\\*");
                        if (cartDetails[0].equals(currentUserPhone) && cartDetails[1].equals(option)
                                && cartDetails[cartDetails.length - 1].equals("Pending")) {
                            existedOrderQuantity = Integer.parseInt(cartDetails[5]);
                            cartIndexToModify = i;
                            break;
                        }
                    }

                    String cartItem = Inventory.inventoryList.get(index);
                    String[] itemDetails = cartItem.split("\\*");
                    int availableSlots = Integer.parseInt(itemDetails[4]);

                    if(cartIndexToModify == -1){
                        if (availableSlots >= quantity) {
                        // add the ordered item and the quantity into cart
                        int totalQuantity = quantity + existedOrderQuantity;
                        itemDetails[4] = Integer.toString(totalQuantity);
                        cartItem = String.join("*", itemDetails);
                        cartList.add(currentUserPhone + "*" + cartItem + "*Unfulfilled*Pending");
                        System.out.println("Item added to cart.");
                        MainMenu.input.nextLine();

                        // Decrease available slots
                        availableSlots = availableSlots - quantity;
                        itemDetails[4] = Integer.toString(availableSlots);

                        // Update the inventoryList list
                        Inventory.inventoryList.set(index, String.join("*", itemDetails));

                        Inventory.saveInventory();
                        } else {
                            System.out.println("Quantity exceeded");
                            MainMenu.input.nextLine();

                        }
                    }else{
                        if (availableSlots >= quantity) {
                            // add the quantity of the existing item into cart
                            int totalQuantity = quantity + existedOrderQuantity;
                            itemDetails[4] = Integer.toString(totalQuantity);
                            cartItem = String.join("*", itemDetails);
                            cartList.set(cartIndexToModify, currentUserPhone + "*" + cartItem + "*Unfulfilled*Pending");
                            System.out.println("Quantity of item added.");
                            MainMenu.input.nextLine();
    
                            // Decrease available slots
                            availableSlots = availableSlots - quantity;
                            itemDetails[4] = Integer.toString(availableSlots);

                            // Update the inventoryList list
                            Inventory.inventoryList.set(index, String.join("*", itemDetails));
    
                            Inventory.saveInventory();
                            } else {
                                System.out.println("Quantity exceeded");
                                MainMenu.input.nextLine();
    
                            }
                    }
                    
                }
            } catch (Exception e){
                System.out.println("Invalid input. Please enter a number >0");
                MainMenu.input.nextLine();
            }
        }
    }

    // Method for canceling a addToCart
    public static void modifyCart(String currentUserPhone) {

        Inventory.inventoryList.clear();
        Inventory.loadInventory();
        loadCart();

        while (true) {
            try {
                boolean itemExist = false;
                for (String cartItems : cartList) {
                    String[] cartItem = cartItems.split("\\*");
                    if (cartItem[0].equals(currentUserPhone) && cartItem[7].equals("Pending"))
                        itemExist = true;
                }

                if (!itemExist) {
                    System.out.println("No previous item in your cart.");
                    MainMenu.input.nextLine();
                    Inventory.inventoryList.clear();
                    cartList.clear();
                    return;
                }

                showCart(currentUserPhone);
                boolean itemExistInCart = false;

                System.out.print("Enter item id to modify quantity (Enter \"esc\" to exit): ");
                String itemId = MainMenu.input.nextLine();

                if (itemId.equalsIgnoreCase("esc")) {
                    Inventory.inventoryList.clear();
                    cartList.clear();
                    return;
                }
                // Find the item from the cart
                int cartIndexToModify = -1;
                int orderedQuantity = 0;
                for (int i = 0; i < cartList.size(); i++) {
                    String cartItem = cartList.get(i);
                    String[] cartDetails = cartItem.split("\\*");
                    if (cartDetails[0].equals(currentUserPhone) && cartDetails[1].equals(itemId) && cartDetails[7].equals("Pending")) {
                        itemExistInCart = true;
                        cartIndexToModify = i;
                        orderedQuantity = Integer.parseInt(cartDetails[5]);
                        break;
                    }
                }

                System.out.println("Enter quantity:");
                int modifiedQuantity = MainMenu.input.nextInt();

                // Load and update the cart.txt file
                try (BufferedReader reader = new BufferedReader(new FileReader("cart.txt"))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\*");
                        if (parts[0].equals(currentUserPhone) && parts[1].equals(itemId)
                                && parts[7].equals("Pending")) {

                            itemExistInCart = true;

                            int index = Inventory.findItemIndex(itemId);

                            // Update inventoryList
                            String cartItem = Inventory.inventoryList.get(index);
                            String[] itemDetails = cartItem.split("\\*");
                            int availableItems = Integer.parseInt(itemDetails[4]);

                            // To find out the total quantity of the item
                            int totalQuantity = availableItems + orderedQuantity;
                            // to check if the total quantity is enough
                            if (totalQuantity > modifiedQuantity) {

                                // Increase available slots
                                availableItems = totalQuantity - modifiedQuantity;
                                itemDetails[4] = Integer.toString(availableItems);

                                if (modifiedQuantity == 0) {
                                    cartList.remove(cartIndexToModify);
                                } else {
                                    String[] newCart = {currentUserPhone, itemDetails[0], itemDetails[1], itemDetails[2],
                                            itemDetails[3], Integer.toString(modifiedQuantity), "Unfulfilled", "Pending"};

                                    Cart.cartList.set(cartIndexToModify, String.join("*", newCart));
                                }

                                // Update the cart
                                saveCart();

                                // Update the inventory list
                                Inventory.inventoryList.set(index, String.join("*", itemDetails));
                                Inventory.saveInventory();

                                System.out.println("Item is modified successfully.");
                                MainMenu.input.nextLine();
                            } else {
                                System.out.println("The available items are not enough.\n");
                                MainMenu.input.nextLine();
                            }
                        }

                    }

                    // Save the updated lines back to addToCart.txt

                    if (!itemExistInCart) {
                        System.out.println("Item not found in your cart.");
                        MainMenu.input.nextLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                MainMenu.input.nextLine();
            }
        }
    }
}
