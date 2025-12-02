package com.delivery.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_FILE = "database/ods.db";   // Real DB location
    private static final String SCHEMA_FILE = "/database/schema.sql";  // resource file
    private static final String SEED_FILE = "/database/seed.sql";      // resource file

    static {
        try {
            Class.forName("org.sqlite.JDBC");

            // Check if database exists
            if (!Files.exists(Paths.get(DB_FILE))) {
                System.out.println("Database not found. Creating a new database...");
                initializeDatabase();
            }

        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    // Return a connection to SQLite DB
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
    }

    // Create DB using schema.sql + seed.sql
    private static void initializeDatabase() throws Exception {
        runSqlScript(SCHEMA_FILE);
        runSqlScript(SEED_FILE);
        System.out.println("Database created and initialized successfully!");
    }

    // Reads SQL script from resources folder & executes it
    private static void runSqlScript(String resourcePath) throws Exception {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Database.class.getResourceAsStream(resourcePath))
            )) {
                if (reader == null) {
                    throw new RuntimeException("Unable to load resource: " + resourcePath);
                }

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);

                    // Execute when a complete statement is found
                    if (line.trim().endsWith(";")) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sb.toString());
                        }
                        sb = new StringBuilder();
                    }
                }

                conn.commit();
            }
        }
    }
}
