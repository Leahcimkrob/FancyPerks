package de.oliver.gui.inventoryClick.impl;

import de.oliver.FancyPerks;
import de.oliver.gui.customInventories.impl.PerksInventory;
import de.oliver.gui.inventoryClick.InventoryItemClick;
import de.oliver.perks.Perk;
import de.oliver.perks.PerkRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;

public class TogglePerkInventoryItemClick implements InventoryItemClick {

    public static final TogglePerkInventoryItemClick INSTANCE = new TogglePerkInventoryItemClick();

    private final static List<NamespacedKey> REQUIRED_KEYS = Collections.singletonList(
            Perk.PERK_KEY
    );

    private TogglePerkInventoryItemClick(){ }

    @Override
    public String getId() {
        return "togglePerk";
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        Player p = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if(item != null && InventoryItemClick.hasKeys(item, REQUIRED_KEYS)){
            event.setCancelled(true);

            String perkName = item.getItemMeta().getPersistentDataContainer().get(Perk.PERK_KEY, PersistentDataType.STRING);
            Perk perk = PerkRegistry.getPerkByName(perkName);

            if(perk == null){
                return;
            }

            boolean hasPerk = FancyPerks.getInstance().getPerkManager().hasPerkEnabled(p, perk);

            if(hasPerk){
                perk.revoke(p);
                event.setCurrentItem(PerksInventory.getDisabledPerkItem(perk));
            } else {
                if(!p.hasPermission("FancyPerks.perk." + perk.getSystemName())){
                    p.sendMessage(Component.text("You don't have permission to use this perk").color(TextColor.color(255,0,0)));
                    return;
                }

                perk.grant(p);
                event.setCurrentItem(PerksInventory.getEnabledPerkItem(perk));
            }
        }
    }
}