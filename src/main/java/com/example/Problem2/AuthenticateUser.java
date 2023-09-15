package com.example.Problem2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class AuthenticateUser {

    String fileLocation = "Credentials\\";

    /**
     * Method to authenticate user
     *
     * @param username
     * @param password
     * @param securityAnswer
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean Login(String username, String password, String securityAnswer) throws IOException, NoSuchAlgorithmException {
        if (username.equals("") || password.equals("") || securityAnswer.equals("")) {
            System.out.println("Login Failed");
            return false;
        }
        String filePath = fileLocation + username + "\\";
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        File file = new File(filePath + username + ".txt");
        if (file.exists()) {
            Scanner scanner = new Scanner(file);
            String fileUsername = scanner.nextLine().split(": ")[1];
            String filePassword = scanner.nextLine().split(": ")[1];
            String fileSecurityAnswer = scanner.nextLine().split(": ")[1];
            if (username.equals(fileUsername) && EncryptPassword.encryptPassword(password).equals(filePassword) && securityAnswer.equals(fileSecurityAnswer)) {
                System.out.println("Login Successful");
            } else {
                System.out.println("Login Failed");
                return false;
            }
        } else {
            try {
                System.out.println("\nUser does not exist. Creating new user");
                createNewUser(username, password, securityAnswer);
                return false;
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Method to create new user
     *
     * @param username
     * @param password
     * @param securityAnswer
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public void createNewUser(String username, String password, String securityAnswer) throws IOException, NoSuchAlgorithmException {
        String filePath = fileLocation + username + "\\";
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        File file = new File(filePath + username + ".txt");
        if (!file.exists()) {
            file.createNewFile();
            FileWriter fw = new FileWriter(filePath + username + ".txt");
            fw.write("Username: " + username + System.lineSeparator());
            fw.write("Password: " + EncryptPassword.encryptPassword(password) + System.lineSeparator());
            fw.write("Security Answer: " + securityAnswer);
            System.out.println("\nUser created");
            System.out.println("please login again");
            fw.close();
        } else {
            System.out.println("\nUser already exists");
        }
    }
}
