package domain;
import app.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Inventory {

    // ArrayList to store inventoryList
    public static final List<String> inventoryList = new ArrayList<>();

    // Method to display the inventory
    public static void showInventory() {
        inventoryList.clear();
        loadInventory();
        if (inventoryList.isEmpty()) {
            System.out.println("No item is found in the inventory.");
            System.out.print("Press Enter to continue...");
            MainMenu.input.nextLine();
        } else {
            System.out.println("\n---------------------------------------------------------------------------------------------------------------");
            String format = "| %-15s | %-20s | %-30s | %-10s | %-20s |%n";

            System.out.format(format, "Item ID", "Name", "Description", "Price", "Quantity Left");
            System.out.println("---------------------------------------------------------------------------------------------------------------");

            for (String inventoryItem : inventoryList) {
                String[] itemDetails = inventoryItem.split("\\*");
                System.out.format(format, itemDetails[0], itemDetails[1], itemDetails[2], "RM" + itemDetails[3],
                        itemDetails[4]);

            }
            System.out.println("---------------------------------------------------------------------------------------------------------------");
        }
    }

    // Method to create item for inventory
    public static void createItem(Scanner GetData) {

        System.out.println("Adding a new item");

        String itemID = null;
        boolean itemValid = false;
        while(!itemValid){
            boolean itemExist = false;

            System.out.print("Enter Item ID OR type 'esc' to return: ");
            itemID = GetData.nextLine();

            if (itemID.equalsIgnoreCase("esc")) {
                return; // Return to the main menu
            }

            // Check if the input matches the item id pattern
            if(itemID.isEmpty() || itemID.isBlank()){
                System.out.println("Invalid item id. Class id can't be blanks.");
                System.out.println("Press Enter to continue...");
                GetData.nextLine();
            }else if(!Pattern.matches("[a-zA-Z0-9]+", itemID)){
                System.out.println("Invalid item id. Please enter a item id only consist of letter and number.");
                System.out.println("Press Enter to continue...");
                GetData.nextLine();
            }else{
                // To avoid the overlap of item id
                for(String inventory : inventoryList){
                    String[] itemDetails = inventory.split("\\*");
                    if(itemDetails[0].equals(itemID)){
                        System.out.println("This item id already exists.");
                        System.out.println("Press Enter to continue...");
                        GetData.nextLine();
                        itemExist = true;
                    }
                }
            }
            if(!itemExist){
                itemValid = true;
            }

        }

        System.out.print("Enter Item Name: ");
        String itemName = GetData.nextLine();

        System.out.print("Enter Item Description: ");
        String itemDesc = GetData.nextLine();
        

        double itemPrice = 0;
        while (true) {
            try {
                System.out.print("Enter Price: ");
                itemPrice = Integer.parseInt(GetData.nextLine());
                if (itemPrice >= 0) {
                    break;
                } else {
                    System.out.println("Please enter a non-negative price.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid price.");
            }
        }

        int itemQuantity = 0;
        while (true) {
            try {
                System.out.print("Enter Quantity: ");
                itemQuantity = Integer.parseInt(GetData.nextLine());
                if (itemQuantity >= 0) {
                    break;
                } else {
                    System.out.println("Please enter a non-negative integer for quantity.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer for quantity.");
            }
        }

        String newClass = String.format("%s*%s*%s*%.2f*%s",itemID, itemName, itemDesc, itemPrice, itemQuantity);

        inventoryList.add(newClass);
        System.out.println("Item added successfully!");
        saveInventory();
    }

    // Method to edit item's quantity
    public static void modifyItem(Scanner GetData){

        while (true) {

            System.out.println("Editing an item");
            System.out.print("Enter the Item ID to edit OR Type 'esc' to return: ");
            String pickItemID = GetData.nextLine();

            if (pickItemID.equalsIgnoreCase("esc")) {
                return; // Return to the main menu
            }

            int index = findItemIndex(pickItemID);
            if (index == -1) {
                System.out.println("Item not found.");
                GetData.nextLine();
            } else {
                // Display the Content before the Editing
                System.out.println("Current details:");
                List<String> DisplayEditItem = Collections.singletonList(inventoryList.get(index));
                System.out.println("--------------------------------------------------------------------------------------------");
                String format = "| %-8s | %-15s | %-30s | %-8s | %-15s |%n";

                System.out.format(format, "Item ID", "Name", "Description", "Price", "Quantity");
                System.out.println("--------------------------------------------------------------------------------------------");

                for (String inventory : DisplayEditItem) {
                    String[] itemDetails = inventory.split("\\*");
                    System.out.format(format, itemDetails[0], itemDetails[1], itemDetails[2], "RM" + itemDetails[3],
                            itemDetails[4]);
                }
                System.out.println("--------------------------------------------------------------------------------------------");
                System.out.print("Press Enter to continue...");
                MainMenu.input.nextLine();

                System.out.print("Enter new Item Name: ");
                String newItemName = GetData.nextLine();

                //GetData.nextLine();
                System.out.print("Enter new Class Description: ");
                String newClassDesc = GetData.nextLine();

                double newClassPrice = 0;
                while (true) {
                    try {
                        System.out.print("Enter Price: ");
                        newClassPrice = Double.parseDouble(GetData.nextLine());
                        if (newClassPrice >= 0) {
                            break;
                        } else {
                            System.out.println("Please enter a non-negative price.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid price.");
                    }
                }

                int newItemQuantity = 0;
                while (true) {
                    try {
                        System.out.print("Enter Slot Available: ");
                        newItemQuantity = Integer.parseInt(GetData.nextLine());
                        if (newItemQuantity >= 0) {
                            break;
                        } else {
                            System.out.println("Please enter a non-negative integer for slot available.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid integer for slot available.");
                    }
                }

                String editedClass = String.format(
                        "%s*%s*%s*%.2f*%s", pickItemID, newItemName, newClassDesc, newClassPrice, newItemQuantity);

                inventoryList.set(index, editedClass);
                System.out.println("Class edited successfully!");
                saveInventory();
            }
        }
    }

    // Method to remove item from inventory
    public static void removeItem(Scanner GetData){

        System.out.println("Removing an item");

        System.out.print("Enter the Item ID to remove: ");
        String targetItemID = GetData.nextLine();

        if(targetItemID.equals("esc")){
            return;
        }
        int index = Inventory.findItemIndex(targetItemID);
        if (index == -1) {
            System.out.println("Item not found.");
            return;
        }

        System.out.println("Are you sure you want to remove this class? (Y/N): ");
        String confirmation = GetData.nextLine();

        if (confirmation.equalsIgnoreCase("Y")) {
            inventoryList.remove(index);
            System.out.println("Class removed successfully!");
            saveInventory();
        } else {
            System.out.println("Class removal cancelled.");
        }
    }

    // Method to load inventory from a file
    public static void loadInventory(){
        try (BufferedReader reader = new BufferedReader(new FileReader("inventory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                inventoryList.add(line);
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to save inventory to a file
    public static void saveInventory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.txt"))) {
            for (String inventory : inventoryList) {
                writer.write(inventory);
                writer.newLine();
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to find the index of an item by itemID
    public static int findItemIndex(String selectItemId) {
        for (int i = 0; i < inventoryList.size(); i++) {
            String[] itemDetails = inventoryList.get(i).split("\\*");
            if (itemDetails[0].equals(selectItemId)) {
                return i;
            }
        }
        return -1;
    }
}
