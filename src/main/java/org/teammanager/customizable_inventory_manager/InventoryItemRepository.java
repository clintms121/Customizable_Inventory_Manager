package org.teammanager.customizable_inventory_manager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teammanager.customizable_inventory_manager.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
