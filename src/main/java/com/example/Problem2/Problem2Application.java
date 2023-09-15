package com.example.Problem2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

@SpringBootApplication

public class Problem2Application {

    /**
     * This class represents the entry point of the Problem2 application. It runs the main loop that prompts the user for login credentials
     * and executes SQL queries if the user is authenticated. It uses the AuthenticateUser class to authenticate the user and the SQLQuery class
     * to handle SQL queries.
     *
     * @author [Vasu Makwana]
     * @version 1.0
     * @since [15/02/2023]
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        SpringApplication.run(Problem2Application.class, args);
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;
        String username = null;
        while (!loggedIn) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            scanner.reset();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            scanner.reset();
            System.out.println("Security question: What is your favourite place?");
            System.out.print("\nEnter the answer for your security question: ");
            String securityAnswer = scanner.nextLine();
            scanner.reset();
            AuthenticateUser auth = new AuthenticateUser();
            loggedIn = auth.Login(username, password, securityAnswer);
        }
        while (SQLQuery.askQuery()) {
            System.out.println("Enter your SQL query: ");
            String query = scanner.nextLine();
            scanner.reset();
            SQLQuery.DetectSQLQuery(query, username);
        }

    }

}
