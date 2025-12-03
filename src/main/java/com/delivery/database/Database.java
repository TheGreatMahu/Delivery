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

    private static final String DB_FILE = "F:/OOP DELIVERY/ods.db";  
    private static final String SCHEMA_FILE = "/database/schema.sql";
    private static final String SEED_FILE = "/database/seed.sql";

    static {
        try {
            Class.forName("org.sqlite.JDBC");

            if (!Files.exists(Paths.get(DB_FILE))) {
                System.out.println("Database not found. Creating...");
                initializeDatabase();
            }

        } catch (Exception e) {
            System.err.println("DB Init Error: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
    }

    private static void initializeDatabase() throws Exception {
        Files.createDirectories(Paths.get("F:/OOP DELIVERY"));
        runSqlScript(SCHEMA_FILE);
        runSqlScript(SEED_FILE);
        System.out.println("Database created + seeded successfully!");
    }

    private static void runSqlScript(String path) throws Exception {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Database.class.getResourceAsStream(path))
            )) {
                if (reader == null) {
                    throw new RuntimeException("Cannot load " + path);
                }

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);

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
