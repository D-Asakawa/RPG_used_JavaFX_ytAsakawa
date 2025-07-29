package com.ytasakawa.rpg_used_javafx_ytasakawa;

public class Enemy {
    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;
    private int exp;
    private Element weakElement;
    private int goldDrop;

    public Enemy(String name, int hp, int attackPower, int exp, Element weakElement, int goldDrop) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.attackPower = attackPower;
        this.exp = exp;
        this.weakElement = weakElement;
        this.goldDrop = goldDrop;
    }

    public void attack(Player player) {
        player.takeDamage(attackPower);
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public boolean isAlive() {
        return hp > 0;
    }


    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getExp() {
        return exp;
    }

    public Element getWeakElement() {
        return weakElement;
    }

    public int getGoldDrop() {
        return goldDrop;
    }
}