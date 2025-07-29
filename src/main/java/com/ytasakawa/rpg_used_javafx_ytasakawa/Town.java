package com.ytasakawa.rpg_used_javafx_ytasakawa;

import java.util.ArrayList;
import java.util.List;

public class Town {
    private List<Weapon> weaponsForSale;

    public Town() {
        weaponsForSale = new ArrayList<>();
        weaponsForSale.add(new Weapon("鉄の剣", 10, 100));
        weaponsForSale.add(new Weapon("薙刀", 8, 150));
    }

    public void restAtInn(Player player) {
        player.healFull();
    }

    public boolean buyPotion(Player player, int price) {
        if (player.deductGold(price)) {
            player.addPotion(1);
            return true;
        }
        return false;
    }

    public List<Weapon> getWeaponsForSale() {
        return weaponsForSale;
    }

    public boolean buyWeapon(Player player, Weapon weapon) {
        if (player.deductGold(weapon.getPrice())) {
            player.equipWeapon(weapon);
            return true;
        }
        return false;
    }
}
