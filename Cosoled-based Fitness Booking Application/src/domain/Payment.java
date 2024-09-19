package domain;
import app.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Payment {

    // Method to get user addToCarts
    public static List<String> getUserOrder(String currentUserPhone) {
        List<String> cart = new ArrayList<>();
        String fileName = "cart.txt";

        // Load and display the user's addToCarts from addToCart.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\*");
                if (parts.length >= 2 && parts[0].equals(currentUserPhone)) {
                    cart.add(line);
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return cart;
    }

    public static void payment(ClientUser currentUser) {
        // Retrieve the user's cart information
        List<String> cart = getUserOrder(currentUser.getPhone());

        Cart.loadCart();
        if (cart.isEmpty()) {
            System.out.println("No bookings found for the current user.");
            System.out.print("Press Enter to continue...");
            MainMenu.input.nextLine();
        } else {
            double totalPrice = calculateTotalPrice(cart);
            
                // To check if there are items still not paid
                boolean pendingStatus = false;
                for (String cartItem : cart) {
                    String[] cartDetails = cartItem.split("\\*");
                    if (cartDetails[0].equals(currentUser.getPhone()) && cartDetails[7].equals("Pending")) {
                        pendingStatus = true;
                    }
                }
                
                if (pendingStatus) {

                    Cart.showCart(currentUser.getPhone());

                    // Calculate the total price of the current user's booking lists

                    System.out.println("Total Price: RM" + totalPrice);

                    // Ask the user if they want to continue with payment
                    System.out.print("Do you want to continue with payment? (y/n): ");
                    String choice = MainMenu.input.nextLine();
                    System.out.println();

                    if (choice.equalsIgnoreCase("y")) {
                        
                            // Update booking status to "Paid"
                            updatePaymentStatus(cart, currentUser.getPhone());

                            System.out.println("Payment successful!");
                            MainMenu.input.nextLine();

                    }else {
                        System.out.println("Payment canceled.");
                        MainMenu.input.nextLine();
                        
                    }
                } else {
                    System.out.println("No items found in cart....");
                    System.out.print("Press Enter to continue...");
                    MainMenu.input.nextLine();
                    
                }
            
        Cart.cartList.clear();
        }
    }

    // Calculate the total price of pending bookings
    private static double calculateTotalPrice(List<String> cart) {
        double totalPrice = 0;
        for (String cartItem : cart) {
            String[] parts = cartItem.split("\\*");
            if (parts.length >= 8 && parts[parts.length - 1].equals("Pending")) {
                double price = Double.parseDouble(parts[4]) * Double.parseDouble(parts[5]);
                totalPrice += price;
            }
        }
        return totalPrice;
    }

    // Update the payment status of bookings to "Paid"
    public static void updatePaymentStatus(List<String> cart, String currentUserPhone) {
        String fileName = "Cart.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String> updatedCart = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\*");
                if (parts.length >= 8) {
                    String orderPhone = parts[0];
                    String paymentStatus = parts[7];
                    // Check if the phone matches the current user and payment status is "Pending"
                    if (orderPhone.equals(currentUserPhone) && paymentStatus.equals("Pending")) {
                        // Update the payment status to "Paid"
                        parts[7] = "Paid";
                    }
                }
                updatedCart.add(String.join("*", parts));
            }

            // Rewrite the entire file with updated bookings
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                for (String updatedCartItem : updatedCart) {
                    writer.println(updatedCartItem);
                }
            }

            catch (IOException e) {
                System.out.println("Error writing updated data to file.");
                e.printStackTrace();
            }
        }

        catch (IOException e) {
            System.out.println("Error reading data from file.");
            e.printStackTrace();
        }
    }

}
