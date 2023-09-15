package com.example.Problem2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLQuery {
    /**
     * Detects the type of SQL query in the given string and executes the appropriate method.
     *
     * @param sqlQuery the SQL query string to be executed
     * @param username the username associated with the query
     * @throws IOException if there is an I/O error while reading or writing files
     */
    public static void DetectSQLQuery(String sqlQuery, String username) throws IOException {

        // file path for storing credentials
        String filePath = "Credentials\\" + username + "\\";

        // split the query into an array of strings
        String[] sqlQueryArray = sqlQuery.toLowerCase().split(" ");
        int sqlQueryArrayLength = sqlQueryArray.length;

        // loop through the array of words to determine the type of SQL query
        for (String s : sqlQueryArray) {
            // execute the appropriate method based on the query type
            if (s.equalsIgnoreCase("CREATE") && ((sqlQueryArray[sqlQueryArrayLength - 1].contains(";") || sqlQueryArray[sqlQueryArrayLength - 1].equals(";")))) {
                createQuery(sqlQueryArray, sqlQuery, filePath);
            }
            if (s.equalsIgnoreCase("SELECT") && ((sqlQueryArray[sqlQueryArrayLength - 1].contains(";") || sqlQueryArray[sqlQueryArrayLength - 1].equals(";")))) {
                selectQuery(sqlQueryArray, filePath, sqlQuery);
            }
            if (s.equalsIgnoreCase("INSERT") && ((sqlQueryArray[sqlQueryArrayLength - 1].contains(";") || sqlQueryArray[sqlQueryArrayLength - 1].equals(";")))) {
                insertQuery(sqlQueryArray, filePath);
            }
            if (s.equalsIgnoreCase("UPDATE") && ((sqlQueryArray[sqlQueryArrayLength - 1].contains(";") || sqlQueryArray[sqlQueryArrayLength - 1].equals(";")))) {
                updateQuery(sqlQueryArray, sqlQuery, filePath);
            }
            if (s.equalsIgnoreCase("DELETE") && ((sqlQueryArray[sqlQueryArrayLength - 1].contains(";") || sqlQueryArray[sqlQueryArrayLength - 1].equals(";")))) {
                deleteQuery(sqlQueryArray, sqlQuery, filePath);
            }
        }
    }

    /**
     * This method updates the specified table in the database with the provided values, based on the specified WHERE clause.
     * <p>
     * //     * @param filePath      a string representing the path to the user's database folder
     *
     * @param sqlQueryArray an array of strings containing the components of the SQL query
     * @param updateQuery   a string representing the full update query
     * @throws IOException if an I/O error occurs
     */
    public static void updateQuery(String[] sqlQueryArray, String updateQuery, String databaseName) throws IOException {
        String[] list1 = new File(databaseName).list();
        assert list1 != null;
        String delimiter = null;
        for (String s : list1) {
            if (sqlQueryArray[1].equals(s.split("\\.")[0])) {
                StringBuilder fileContents = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(databaseName + "\\" + s))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContents.append(line).append("\n");
                    }
                    delimiter = line.split(",")[0];
                }

                // Get the headers from the first line of the file
                String[] headers = fileContents.toString().split("\n")[0].split(delimiter);

                // Parse the update query
                Pattern pattern = Pattern.compile("^update\\s+(\\w+)\\s+set\\s+(.+?)\\s+where\\s+(.+);", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(updateQuery);
                if (matcher.matches()) {
                    String[] assignments = matcher.group(2).split(",");
                    String whereClause = matcher.group(3);

                    // Update the values in the file
                    String[] rows = fileContents.toString().split("\n");
                    boolean checkrowupdatestatus = false;
                    for (int i = 1; i < rows.length; i++) {
                        String[] values = rows[i].split(delimiter);
                        boolean matchesWhereClause = checkWhereCondition(whereClause, headers, values);
                        if (matchesWhereClause) {
                            for (String assignment : assignments) {
                                String[] parts = assignment.trim().split("=");
                                String column = parts[0].trim();
                                String value = parts[1].trim();
                                int columnIndex = getColumnIndex(column, headers);
                                values[columnIndex] = value;
                            }
                            rows[i] = String.join(delimiter, values);
                            checkrowupdatestatus = true;
                        }
                    }

                    // Write the updated file
                    if (checkrowupdatestatus) {
                        try (FileWriter writer = new FileWriter(databaseName + "\\" + s)) {
                            writer.write(String.join("\n", rows));
                        }
                    }
                }
            }
        }
    }

    /**
     * Finds the index of the given column in the headers array.
     *
     * @param column  the column to search for
     * @param headers the array of column headers
     * @return the index of the column in the headers array
     * @throws IllegalArgumentException if the column does not exist in the headers array
     */
    private static int getColumnIndex(String column, String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(column)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column does not exist: " + column);
    }

    /**
     * Evaluates the given where clause against the given headers and values.
     *
     * @param whereClause the where clause to evaluate
     * @param headers     the headers of the table
     * @param values      the values of the row
     * @return true if the where clause evaluates to true, false otherwise
     */
    public static boolean checkWhereCondition(String whereClause, String[] headers, String[] values) {
        Pattern pattern = Pattern.compile("(\\w+)\\s*([=!<>]+)\\s*([^=<>]+)");
        Matcher matcher = pattern.matcher(whereClause);

        // Evaluate the condition for each match
        while (matcher.find()) {
            String column = matcher.group(1);
            String operator = matcher.group(2);
            String expectedValue = matcher.group(3);
            int columnIndex = getColumnIndex(column, headers);
            String actualValue = values[columnIndex];

            switch (operator) {
                case "=" -> {
                    if (!actualValue.equals(expectedValue)) {
                        return false;
                    }
                }
                case "<>" -> {
                    if (actualValue.equals(expectedValue)) {
                        return false;
                    }
                }
                case "<" -> {
                    if (Double.parseDouble(actualValue) >= Double.parseDouble(expectedValue)) {
                        return false;
                    }
                }
                case ">" -> {
                    if (Double.parseDouble(actualValue) <= Double.parseDouble(expectedValue)) {
                        return false;
                    }
                }
                case "<=" -> {
                    if (Double.parseDouble(actualValue) > Double.parseDouble(expectedValue)) {
                        return false;
                    }
                }
                case ">=" -> {
                    if (Double.parseDouble(actualValue) < Double.parseDouble(expectedValue)) {
                        return false;
                    }
                }
                default -> throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }

        return true;
    }

    /**
     * Deletes the rows from the table that match the given where clause.
     *
     * @param sqlQueryArray the array of sql query
     * @param deleteQuery   the delete query
     * @param filePath      the path of the database
     * @throws IOException if an I/O error occurs
     */
    public static void deleteQuery(String[] sqlQueryArray, String deleteQuery, String filePath) throws IOException {
        String databaseName = null;
        String delimiterpath;
        String delimiter = null;
        File userFolder = new File(filePath);
        String[] list = userFolder.list();
        assert list != null;
        for (String s1 : list) {
            File file = new File(filePath + s1);
            if (file.exists() && file.isDirectory()) {
                databaseName = filePath + s1;
            }
        }
        delimiterpath = databaseName + "\\" + sqlQueryArray[3] + "delimiter.txt";
        BufferedReader delimiterReader = new BufferedReader(new FileReader(delimiterpath));
        delimiter = delimiterReader.readLine();
        delimiterReader.close();
        assert databaseName != null;
        String[] list1 = new File(databaseName).list();
        assert list1 != null;
        for (String s : list1) {
            if (sqlQueryArray[2].equals(s.split("\\.")[0])) {
                StringBuilder fileContents = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(databaseName + "\\" + s))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContents.append(line).append("\n");
                    }
                }

                // Extract the table headers from the file contents
                String[] headers = fileContents.toString().split("\n")[0].split(delimiter);

                // Parse the delete query using the provided regex
                Pattern pattern = Pattern.compile("^delete\\s+from\\s+(\\w+)\\s+where\\s+(.+);", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(deleteQuery);
                if (matcher.matches()) {
                    String whereClause = matcher.group(2);

                    // Delete the rows matching the WHERE clause from the file contents
                    String[] rows = fileContents.toString().split("\n");
                    boolean checkrowdeletestatus = false;
                    List<String> newRows = new ArrayList<>();
                    newRows.add(rows[0]);
                    for (int i = 1; i < rows.length; i++) {
                        String[] values = rows[i].split(delimiter);
                        boolean matchesWhereClause = checkWhereCondition(whereClause, headers, values);
                        if (!matchesWhereClause) {
                            newRows.add(rows[i]);
                        } else {
                            checkrowdeletestatus = true;
                        }
                    }

                    // Write the updated contents back to the file
                    if (checkrowdeletestatus) {
                        try (FileWriter writer = new FileWriter(databaseName + "\\" + s)) {
                            writer.write(String.join("\n", newRows));
                        }
                    } else {
                        throw new IllegalArgumentException("No rows found matching where clause: " + whereClause);
                    }
                }
            }
        }
    }

    /**
     * Selects the rows from the table that match the given where clause.
     *
     * @param sqlQueryArray the array of sql query
     * @param filePath      the path of the database
     * @param sqlQuery      the select query
     * @throws IOException if an I/O error occurs
     */
    public static void selectQuery(String[] sqlQueryArray, String filePath, String sqlQuery) throws IOException {
        String delimiterpath;
        String delimiter;
        if (sqlQueryArray[1].equals("*")) {
            String databaseName = null;
            File userFolder = new File(filePath);
            String[] list = userFolder.list();
            for (String s1 : list) {
                File file = new File(filePath + s1);
                if (file.exists() && file.isDirectory()) {
                    databaseName = filePath + s1;
                }
            }
            delimiterpath = databaseName + "\\" + sqlQueryArray[sqlQueryArray.length - 1].replaceAll(";", "") + "delimiter.txt";
            BufferedReader delimiterReader = new BufferedReader(new FileReader(delimiterpath));
            delimiter = delimiterReader.readLine();
            delimiterReader.close();
            assert databaseName != null;
            String[] list1 = new File(databaseName).list();
            assert list1 != null;
            int count = 0;
            for (String s : list1) {
                if (sqlQueryArray[2].equalsIgnoreCase("FROM") && sqlQueryArray[3].replace(";", "").equals(s.split("\\.")[0])) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(databaseName + "\\" + s));
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (count == 0) {
                                System.out.println(line.replaceAll(delimiter, ","));
                                count++;
                                continue;
                            }
                            line = line.replaceAll(delimiter, ",");
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (Arrays.toString(sqlQueryArray).substring(sqlQuery.indexOf("select"), sqlQuery.indexOf("from")).contains(",") || sqlQueryArray[1].contains(",")) {
            String databaseName = null;
            File userFolder = new File(filePath);
            String[] list = userFolder.list();
            assert list != null;
            for (String s1 : list) {
                File file = new File(filePath + s1);
                if (file.exists() && file.isDirectory()) {
                    databaseName = filePath + s1;
                }
            }
            delimiterpath = databaseName + "\\" + sqlQueryArray[3].replaceAll(";", "") + "delimiter.txt";
            BufferedReader delimiterReader = new BufferedReader(new FileReader(delimiterpath));
            delimiter = delimiterReader.readLine();
            delimiterReader.close();
            assert databaseName != null;
            String[] list1 = new File(databaseName).list();
            assert list1 != null;
            for (String s : list1) {
                if (sqlQueryArray[1].equals(s.split("\\.")[0])) {
                    StringBuilder fileContents = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new FileReader(databaseName + "\\" + s))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            fileContents.append(line).append("\n");
                        }
                    }

                    // Extract the table headers from the file contents
                    String[] headers = fileContents.toString().split("\n")[0].split(delimiter);

                    // Parse the select query using the provided regex
                    Pattern pattern = Pattern.compile("^select\\s+(.+?)\\s+where\\s+(.+?);", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(sqlQuery);
                    if (matcher.matches()) {
                        String[] columns = matcher.group(1).split(",");
                        String whereClause = matcher.group(2);

                        // Search for rows matching the where clause
                        String[] rows = fileContents.toString().split("\n");
                        for (int i = 1; i < rows.length; i++) {
                            String[] values = rows[i].split(delimiter);
                            boolean matchesWhereClause = checkSelectWhereCondition(whereClause, headers, values);
                            if (matchesWhereClause) {
                                // Print the requested columns for the matching row
                                for (String column : columns) {
                                    int columnIndex = getColumnIndex(column.trim(), headers);
                                    System.out.print(values[columnIndex] + "\t");
                                }
                                System.out.println();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the given row matches the where clause for the select query.
     *
     * @param whereClause the where clause
     * @param headers     the table headers
     * @param values      the row values
     * @return true if the row matches the where clause, false otherwise
     */
    public static boolean checkSelectWhereCondition(String whereClause, String[] headers, String[] values) {
        String[] whereParts = whereClause.split("=");
        String column = whereParts[0].trim();
        String value = whereParts[1].trim().replaceAll("'", "");
        int columnIndex = getColumnIndex(column, headers);
        return values[columnIndex].equals(value);


    }

    /**
     * Inserts data into a table specified by the given SQL query array.
     *
     * @param sqlQueryArray an array of strings representing the SQL query to be executed
     * @param filePath      the path to the directory containing the database files
     * @throws IOException if an error occurs while reading or writing files
     */
    public static void insertQuery(String[] sqlQueryArray, String filePath) throws IOException {
        File userFolder = new File(filePath);
        String databaseName = null;
        String delimiterpath;
        String[] list = userFolder.list();
        String columnNames = sqlQueryArray[2].substring(sqlQueryArray[2].indexOf("(") + 1, sqlQueryArray[2].indexOf(")"));
        System.out.println(columnNames);
        String values = sqlQueryArray[3].substring(sqlQueryArray[3].indexOf("(") + 1, sqlQueryArray[3].indexOf(")"));
        System.out.println(values);
        for (String s1 : list) {
            File file = new File(filePath + s1);
            if (file.exists() && file.isDirectory()) {
                databaseName = filePath + s1;
            }
        }
        String tablepath = databaseName + "\\" + sqlQueryArray[2].split("\\(")[0] + ".txt";
        delimiterpath = databaseName + "\\" + sqlQueryArray[2].split("\\(")[0] + "delimiter.txt";
        BufferedReader delimiterReader = new BufferedReader(new FileReader(delimiterpath));
        String delimiter = delimiterReader.readLine();
        delimiterReader.close();
        try (BufferedReader reader = new BufferedReader(new FileReader(tablepath))) {
            FileWriter fileWriter = new FileWriter(tablepath, true);
            String firstLine = reader.readLine();
            for (String columns : columnNames.split(",")) {
                if (!firstLine.contains(columns)) {
                    System.out.println("Column " + '"' + columns + '"' + " does not exist");
                    return;
                }
            }
            fileWriter.write("\n");
            String[] valueNames = values.split(",");
            int valuesLength = valueNames.length;
            for (String values1 : valueNames) {
                if (values1.equals(valueNames[valuesLength - 1])) {
                    fileWriter.write(values1);
                    fileWriter.close();
                } else {
                    fileWriter.write(values1 + delimiter);
                    fileWriter.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Creates a table specified by the given SQL query array.
     *
     * @param sqlQueryArray an array of strings representing the SQL query to be executed
     * @param sqlQuery      the SQL query to be executed
     * @param filePath      the path to the directory containing the database files
     * @throws IOException if an error occurs while reading or writing files
     */
    public static void createQuery(String[] sqlQueryArray, String sqlQuery, String filePath) throws IOException {
        File userFolder = new File(filePath);
        String databaseName = null;
        String[] list = userFolder.list();
        assert list != null;
        String tablepath = null;
        String delimiterpath = null;

        if (sqlQueryArray[1].equalsIgnoreCase("TABLE")) {
            String delimiter = generateDelimiter();
            String tablename = sqlQueryArray[2].split("\\(")[0];
            for (String s1 : list) {
                File file = new File(filePath + s1);
                if (file.exists() && file.isDirectory()) {
                    databaseName = filePath + s1;
                }
                tablepath = databaseName + "\\" + tablename + ".txt";
                delimiterpath = databaseName + "\\" + tablename + "delimiter.txt";
                Path path = Paths.get(tablepath);
                Path path1 = Paths.get(delimiterpath);
                if (!Files.exists(path)) {
                    Files.createFile(path);
                } else if (!Files.exists(path1)) {
                    Files.createFile(path1);
                } else {
                    System.out.println("Table already exists");
                    return;
                }
            }
            Pattern p = Pattern.compile("\\((.*)\\)");
            Matcher m = p.matcher(sqlQuery);
            assert tablepath != null;
            FileWriter fileWriter = new FileWriter(tablepath);
            FileWriter fileWriter1 = new FileWriter(delimiterpath);
            if (m.find()) {
                String columns = m.group(1);
                String[] columnNames = columns.split(",");
                int columnNamesLength = columnNames.length;
                for (String cn : columnNames) {
                    String tcn = cn.trim();
                    String[] cnp = tcn.split(" ");
                    String answer = cnp[0];
                    if (cn.equals(columnNames[columnNamesLength - 1])) {
                        fileWriter.write(answer);
                        fileWriter.flush();
                        fileWriter1.write(delimiter);
                        fileWriter1.flush();
                    } else {
                        fileWriter.write(answer + delimiter);
                        fileWriter.flush();
                    }
                }
                System.out.println("Table created successfully");
            }
            fileWriter.close();


        } else if (sqlQueryArray[1].equalsIgnoreCase("DATABASE")) {
            File databaseFolder = new File(filePath + sqlQueryArray[2].replaceAll(";", ""));
            for (String s1 : list) {
                File file = new File(filePath + s1);
                if (file.exists() && file.isDirectory()) {
                    System.out.println("You can't create more than one database");
                    System.out.println("You already have a database named " + s1 + ". Please use that database");
                }
                if (!Files.exists(databaseFolder.toPath())) {
                    Files.createDirectories(databaseFolder.toPath());
                }
            }
        }
    }

    /**
     * generates a random delimiter
     *
     * @return a random delimiter
     */
    public static String generateDelimiter() {
        Random random = new Random();
        String randomAlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789";
        int length = 10;  // change this to the desired length of the string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char character = randomAlphaNumericString.charAt(random.nextInt(randomAlphaNumericString.length()));
            sb.append(character);
        }
        return sb.toString();
    }

    /**
     * Asks the user if they want to continue with the program or exit.
     *
     * @return true if the user wants to continue, false if the user wants to exit.
     */
    public static boolean askQuery() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nDo you want to continue? (Y/N) or write 'exit' to exit");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("yes")) {
            return true;
        } else if (answer.equalsIgnoreCase("N") || answer.equalsIgnoreCase("no")) {
            return false;
        } else if (answer.equalsIgnoreCase("exit")) {
            System.out.println("Thank you for using our database");
            System.exit(0);
        } else {
            System.out.println("Please enter a valid input");
            askQuery();
        }
        return false;
    }
}

