package domain;
import app.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.Date;
import java.util.HashSet;

public class Report{

    // Method to display billing statement
    public static void billingStatement(){
        Set<String> phoneNumbers = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader("cart.txt"))) {
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

        System.out.println("Enter the phone number of the user: ");
        String currentUserPhone = MainMenu.input.nextLine();

        Cart.cartList.clear();
        Cart.loadCart();
        boolean billingNotExist = true;

        for (String item : Cart.cartList) {
            String[] parts = item.split("\\*");
            if (parts[0].equals(currentUserPhone) && parts[parts.length - 1].equals("Paid")) {
                billingNotExist = false;
                break;
            }
        }

        if (billingNotExist) {
            System.out.println("No billing statement found for the user.");
            System.out.print("Press Enter to continue...");
            MainMenu.input.nextLine();
        }else {
            showBilling(currentUserPhone);
            System.out.println("Do you want to print out the billig statement?(Y/N)");
            String ans = MainMenu.input.nextLine();
            if(ans.equalsIgnoreCase("y")){
                System.out.println("You have successfully printed out the billing statement.");
            }else{
                System.out.println("Print cancelled.");
            }
            MainMenu.input.nextLine();
        }
    }

    public static void showBilling(String currentUserPhone) {
        
        Cart.cartList.clear();
        Cart.loadCart();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        
        System.out.println("-------------------------------------------------Billing Statement-------------------------------------------------");
        System.out.println("Order Item      Description            Price(RM)          Quantity             Total Price(RM)");
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        for (String item : Cart.cartList) {
            String[] parts = item.split("\\*");
            if (parts.length >= 1 && parts[parts.length - 1].equals("Paid") && parts[0].equals(currentUserPhone)) {
                String orderId = parts[1];
                String desc= parts[3];
                double price = Double.parseDouble(parts[4]);
                int quantity = Integer.parseInt(parts[5]);
                double total = price * quantity;
                String format = "%-15s %-22s %-18.2f %-20d %.2f %n";

                System.out.format(format, orderId, desc, price, quantity, total);
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        System.out.println("Timestamp: " + timestamp);
        System.out.println("-------------------------------------------------------------------------------------------------------------------"); 
        MainMenu.input.nextLine();
    }

    // Method to display inventory report
    public static void showInventory(){

        Inventory.inventoryList.clear();
        Inventory.loadInventory();

        if (Inventory.inventoryList.isEmpty()) {
            System.out.println("No inventory found.");
            System.out.println("Press Enter to continue...");
            MainMenu.input.nextLine();
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            System.out.println("\n---------------------------------------------------------------------------------------------------------------");
            String format = " %-20s %-20s";
            System.out.format(format, "Date and Time: ", timestamp);
            Inventory.showInventory();
            MainMenu.input.nextLine();

            System.out.println("Do you want to print out the Inventory Report?(Y/N)");
            String ans = MainMenu.input.nextLine();
            if(ans.equalsIgnoreCase("y")){
                System.out.println("You have successfully printed out the Inventory Report.");
            }else{
                System.out.println("Print cancelled.");
            }
            MainMenu.input.nextLine();
        }
    }


    // Method to generate sales report
    public static void salesReport(){
        Cart.cartList.clear();
        Cart.loadCart();
        double totalSales = 0.0;
        
        if (Cart.cartList.isEmpty()) {
            System.out.println("No sales found.");
            System.out.println("Press Enter to continue...");
            MainMenu.input.nextLine();
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());
            System.out.println("\n-----------------------------------------------------------------------------------------------------------------");
            String dateTimeFormat = " %-20s %-20s";
            System.out.format(dateTimeFormat, "Date and Time :", timestamp);

            System.out.println("\n-----------------------------------------------------------------------------------------------------------------");
            String format = " %-15s  %-20s  %-10s  %-20s %-20s %n";

            System.out.format(format, "Item ID", "Name", "Price", "Quantity Sold", "Total Price");
            System.out.println("-----------------------------------------------------------------------------------------------------------------");

            for (String cartItem : Cart.cartList) {
                String[] itemDetails = cartItem.split("\\*");
                String itemId = itemDetails[1];
                String name = itemDetails[2];
                double price = Double.parseDouble(itemDetails[4]);
                int quantitySold = Integer.parseInt(itemDetails[5]);
                double totalPrice =  price * quantitySold;
                totalSales += totalPrice;
                String paymentStatus = itemDetails[7];
                if(paymentStatus.equalsIgnoreCase("Paid")){
                    System.out.format(format, itemId, name, "RM " + price, quantitySold, "RM " + totalPrice);
                }
            }
            
            System.out.println("-----------------------------------------------------------------------------------------------------------------");
            System.out.println("Total Sales: RM " + totalSales);
            MainMenu.input.nextLine();

            System.out.println("Do you want to print out the Sales Report?(Y/N)");
            String ans = MainMenu.input.nextLine();
            if(ans.equalsIgnoreCase("y")){
                System.out.println("You have successfully printed out the Sales Report.");
            }else{
                System.out.println("Print cancelled.");
            }
            MainMenu.input.nextLine();
            
        }
        Cart.cartList.clear();
    }
}