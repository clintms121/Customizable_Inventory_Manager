
package mainPackage;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

    // User class to hold user data
    public class User {
        private int id;
        private String username;
        private String passwordHash;

        public User(int id, String username, String passwordHash) {
            this.id = id;
            this.username = username;
            this.passwordHash = passwordHash;
        }

        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getPasswordHash() { return passwordHash; }
    }

