package org.teammanager.customizable_inventory_manager;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryApplication {
    public static void main(String[] args) {
        // Bootstrapping Spring Boot before JavaFX
        new Thread(() -> {
            Application.launch(JavaFxInventoryApplication.class, args);
        }).start();
    }
}