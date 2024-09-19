package domain;
import app.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class ClientUser extends Person {

    private int userAge;
    private String userSex;
    private String address;

    // ArrayList to store registered usersf
    public static List<ClientUser> registeredUsers = new ArrayList<>();

    // Getter and Setter methods for all attributes
    public String getAddress() {
        return address;
    }

    public int getUserAge() {
        return userAge;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Constructor
    public ClientUser(String phone, String username, String address, int userAge, String userSex, String password) {
        super(username, phone, password);
        this.address = address;
        this.userAge = userAge;
        this.userSex = userSex;
    }

    // Default Constructors
    public ClientUser() {
        super("", "", "");
    }

    
    public void userRegister() {
        ClientUser newUser;
      
        System.out.println("\n*====================REGISTER====================*");
        System.out.println("Enter [esc] at any prompt to cancel registration.");
    
        System.out.print("Enter your phone number: ");
        phone = MainMenu.input.nextLine();
    
        if (phone.equalsIgnoreCase("esc")) {
            return; // Exit registration process if user enters 'esc'
        }

        boolean isExist = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\*");
                String userPhone = userData[0]; // Phone number is at index 0
                if (phone.equals(userPhone)) {
                    isExist = true; // Phone number already exists
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data from file.");
            e.printStackTrace();
            return; // Exit registration process if an error occurs
        }
    
        if (isExist) {
            System.out.println("\nPhone number already exists");
            return; // Exit registration process if phone number is already registered
        }
    
        System.out.print("Enter your full name: ");
        username = MainMenu.input.nextLine();
    
        System.out.print("Enter your address: ");
        address = MainMenu.input.nextLine();
    
        System.out.print("Enter your sex [M/F]: ");
        userSex = MainMenu.input.nextLine().toUpperCase();
    
        System.out.print("Enter your age: ");
        userAge = MainMenu.input.nextInt();
        MainMenu.input.nextLine(); // Consume newline character left by nextInt()
    
        System.out.print("Enter your password (Password must be more than 6 characters): ");
        password = MainMenu.input.nextLine();
    
        System.out.print("Confirm your password: ");
        String password2 = MainMenu.input.nextLine();
    
        // Validate input
        List<String> validationErrors = new ArrayList<>();    
    
        if (!Pattern.matches("^01[0-9]{8,9}$", phone)) {
            validationErrors.add("Invalid phone number. Please enter a 10 to 11 digits phone number.");
        }
    
        if (username.trim().isEmpty() || !username.matches("[a-zA-Z ]+")) {
            validationErrors.add("Invalid username. Username can contain only alphabets and spaces.");
        }
    
        if (!userSex.equals("M") && !userSex.equals("F")) {
            validationErrors.add("Invalid sex.");
        }
    
        if (userAge < 0 || userAge > 100) {
            validationErrors.add("Invalid age.");
        }    
    
        if (password.length() < 6 || !password.matches("^[a-zA-Z0-9]+$") || !password.equals(password2)) {
            validationErrors.add("Invalid password.");
        }
    
        // Display validation errors if any
        if (!validationErrors.isEmpty()) {
            System.out.println("\nRegistration failed due to the following errors:");
            for (String error : validationErrors) {
                System.out.println(error);
            }
            return;
        }
    
        // Save user's information if no error
        newUser = new ClientUser(phone, username, address, userAge, userSex, password);
        registeredUsers.add(newUser);
        System.out.println("\nRegistered successfully");
        try {
            saveUser();
        } catch (IOException e) {
            System.out.println("Error saving user data to file.");
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void login() {
        registeredUsers.clear(); // Clear the list of registered users. To ensure no duplication data in arraylist

        try {
            loadUser();
        } catch (IOException e) {
            System.out.println("Error to load the data from file.");
            e.printStackTrace();
            return;
        }

        do {
            System.out.println("\n\n*=====================LOGIN=====================*");
            System.out.println("Enter [esc] at phone number or password to return");
            System.out.print("Enter your phone number: ");
            String phone = MainMenu.input.nextLine();

            // Check for esc to return
            if (phone.equalsIgnoreCase("esc")) {
                return;
            }

            // Check if phone number is in the database
            boolean isValidUser = false;
            String currentUserPhone = null;
            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] userData = line.split("\\*");
                    String userPhone = userData[0];
                    if (phone.equals(userPhone)) {
                        isValidUser = true;
                        currentUserPhone = userPhone;
                        break;
                    }
                }
            }
            catch (IOException e) {
                System.out.println("Error to load the data from file.");
                e.printStackTrace();
            }

            if (!isValidUser) {
                System.out.println("Invalid account");
                System.out.println("[1] Re-enter information");
                System.out.println("[2] Register");
                int choice = MainMenu.input.nextInt();
                MainMenu.input.nextLine();

                switch (choice) {
                    case 1:
                        continue;// Continue the login process if the phone number is invalid.
                
                    case 2:
                        userRegister();
                        break;
                
                    default:
                        break;
                }
            }

            String correctPassword = null;
            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] userData = line.split("\\*");
                    String userPhone = userData[0];
                    String userPassword = userData[5];
                    if (phone.equals(userPhone)) {
                        correctPassword = userPassword;
                        break;
                    }
                }
            }

            catch (IOException e) {
                System.out.println("Error to load the data from file.");
                e.printStackTrace();
            }

            if (correctPassword == null) {
                System.out.println("Error reading password from file.");
                return; // Handle the error as appropriate
            }

            do {
                System.out.print("Enter your password: ");
                String password = MainMenu.input.nextLine();

                // Check for esc to return
                if (password.equalsIgnoreCase("esc")) {
                    return;
                }

                if (!password.equals(correctPassword)) {
                    System.out.println("Incorrect password");
                    System.out.println("Press Enter to continue...");
                    MainMenu.input.nextLine();
                }
                else {
                    System.out.println("User located");
                    System.out.println("Press Enter to continue...");
                    MainMenu.input.nextLine();
                    MainMenu.userMenu(currentUserPhone); // Bring user to the main after successful login
                    break; // Exit the password entry loop after successful login.
                }
            } while (true);
            return; // Return to the main menu.
        } while (true);
    }

    // To save the data of user into user.txt
    public void saveUser() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("users.txt"))) {
            for (ClientUser user : registeredUsers) {
                writer.println(
                        user.getPhone() + "*" +
                                user.getUsername() + "*" +
                                user.getAddress() + "*" +
                                user.getUserAge() + "*" +
                                user.getUserSex() + "*" +
                                user.getPassword());
            }
            writer.close();
        }

        catch (IOException e) {
            System.out.println("Error saving data to file.");
            throw e;
        }
    }

    // To load the data of user to the registeredUsers arraylist from user.txt
    public void loadUser() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\*");
                if (data.length == 6) {
                    phone = data[0];
                    username = data[1];
                    address = data[2];
                    userAge = Integer.parseInt(data[3]);
                    userSex = data[4];
                    password = data[5];

                    ClientUser newUser = new ClientUser(phone, username, address, userAge, userSex, password);
                    registeredUsers.add(newUser);
                }
            }
        }

        catch (IOException e) {
            System.out.println("Error loading data from file.");
            e.printStackTrace();
        }
    }

    public void profile(ClientUser currentUser) {
        String option;

        do {
            System.out.println("\n*========================================*");
            System.out.println("*                Profile                 *");
            System.out.println("*----------------------------------------*");
            System.out.println("  Name: " + currentUser.getUsername());
            System.out.println("  Age: " + currentUser.getUserAge());
            System.out.println("  Sex: " + currentUser.getUserSex());
            System.out.println("  Address: " + currentUser.getAddress());
            System.out.println("  Phone Number: " + currentUser.getPhone());
            System.out.println("\n  [1] Change Address                ");
            System.out.println("  [2] Exit                             ");
            System.out.println("*========================================*");
            System.out.print("Enter option: ");
            option = MainMenu.input.nextLine();

            switch (option) {
                case "1":
                    System.out.println("\nCurrent address: " + currentUser.getAddress());
                    System.out.print("Enter your new address: ");
                    address = MainMenu.input.nextLine();
                    MainMenu.input.nextLine();
                    currentUser.setAddress(address);
                    break;
                case "2":
                    return;
                default:
                    System.out.println("Invalid option. Please try again!");
                    break;
            }
        } while (option != "3");
    }

}