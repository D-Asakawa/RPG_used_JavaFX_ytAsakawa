package com.ytasakawa.rpg_used_javafx_ytasakawa;

public class Weapon {
    private String name;
    private int attackPower;
    private int price;

    public Weapon(String name, int attackPower) {
        this.name = name;
        this.attackPower = attackPower;
        this.price = 0;
    }

    public Weapon(String name, int attackPower, int price) {
        this.name = name;
        this.attackPower = attackPower;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getPrice() {
        return price;
    }
}
