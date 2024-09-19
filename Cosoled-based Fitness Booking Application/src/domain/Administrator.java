package domain;
import app.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Administrator extends Person {
    private String adminId;
    public int adminCount = 0;

    // ArrayList to store registered users
    public static final List<Administrator> registeredAdmin = new ArrayList<>();
    
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId){
        this.adminId = adminId;
    }

    public Administrator() {
        super("", "", "");
    }

    public Administrator(String adminId, String username, String phone, String password) {
        super(username, phone, password);
        setAdminId(adminId);
    }

    @Override
    public void login() {
        // load the admin data to registeredAdmin arraylist from admin.txt
        try {
            loadAdmin();
        } catch (IOException e) {
            System.out.println("Error to load the data from admin file.");
            e.printStackTrace();
            return;
        }
        // Check if phone number is in the database
        int adminIndex = -1;
        do {
            System.out.println("*====================LOGIN====================*");
            System.out.println("Enter [esc] at phone number or password to return");
            System.out.print("Enter your phone number: ");
            String phone = MainMenu.input.nextLine();

            // Check for esc to return
            if (phone.equalsIgnoreCase("esc")) {
                return;
            }

            
            for (int i = 0; i <registeredAdmin.size(); i++) {
                if (phone.equals(registeredAdmin.get(i).getPhone())) {
                    adminIndex = i;
                    break;
                }
            }

            // phone number not in the database
            if (adminIndex == -1) {
                System.out.println("Invalid phone number");
                System.out.println("Press Enter to continue...");
                MainMenu.input.nextLine();
                continue;
            }

            do{
                System.out.print("Enter your password: ");
                String password = MainMenu.input.nextLine();

                // Check for esc to return
                if (password.equalsIgnoreCase("esc")) {
                    return;
                }

                // to check if the admin enter the same password both time
                if (password.equals(registeredAdmin.get(adminIndex).getPassword())) {    
                    System.out.println("Admin located");
                    System.out.println("Press Enter to continue...");
                    MainMenu.input.nextLine();
                    MainMenu.adminMenu();
                    break;
                }else{
                    System.out.println("Incorrect password");
                    System.out.println("Press Enter to continue...");
                    MainMenu.input.nextLine();
                }
            } while (true);
            // break;
        } while (adminIndex==-1);
    }

    public static void loadAdmin() throws IOException {
        Administrator newAdmin = new Administrator();

        try (BufferedReader reader = new BufferedReader(new FileReader("admin.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\*");
                if (data.length == 4) {
                    newAdmin = new Administrator(data[0], data[2], data[1],data[3]);
                    Administrator.registeredAdmin.add(newAdmin);
                    newAdmin.adminCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data from file.");
            e.printStackTrace();

        }
    }
}


