package com.ytasakawa.rpg_used_javafx_ytasakawa;

public class Player {
    private String name;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private int level;
    private int exp;
    private int expToLevelUp;
    private int potionCount;
    private Weapon equippedWeapon;
    private int gold;

    public Player(String name, int initialHp, int level) {
        this.name = name;
        this.maxHp = initialHp;
        this.hp = initialHp;
        this.maxMp = 10;
        this.mp = 10;
        this.level = level;
        this.exp = 0;
        this.expToLevelUp = 10;
        this.potionCount = 3;
        this.equippedWeapon = new Weapon("ひのきの棒", 5);
        this.gold = 0;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void attack(Enemy enemy) {
        int damage = equippedWeapon.getAttackPower() + level;
        enemy.takeDamage(damage);
    }

    public void multiTargetAttack(Enemy[] enemies) {
        int damage = equippedWeapon.getAttackPower() / 2 + level;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.takeDamage(damage);
            }
        }
    }

    public void magicAttack(Enemy enemy, Element element) {
        int magicCost = 3;
        if (mp >= magicCost) {
            int baseDamage = 10 + level;
            boolean isWeak = (element == enemy.getWeakElement());
            int damage = isWeak ? (int)(baseDamage * 1.3) : baseDamage;
            mp -= magicCost;
            enemy.takeDamage(damage);
        }
    }


    public void gainExp(int amount) {
        exp += amount;

        while (exp >= expToLevelUp) {
            levelUp();
        }
    }

    private void levelUp() {
        exp -= expToLevelUp;
        level++;
        maxHp += 5;
        hp = maxHp;
        maxMp += 5;
        mp = maxMp;
        expToLevelUp += 5;
    }

    public void usePotion() {
        if (potionCount > 0) {
            int heal = 20;
            hp = Math.min(maxHp, hp + heal);
            potionCount--;
        }
    }

    public void healFull() {
        hp = maxHp;
        mp = maxMp;
    }

    public void addPotion(int amount) {
        potionCount += amount;
    }

    public void equipWeapon(Weapon newWeapon) {
        this.equippedWeapon = newWeapon;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public boolean deductGold(int amount) {
        if (this.gold >= amount) {
            this.gold -= amount;
            return true;
        }
        return false;
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

    public int getMp() {
        return mp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public int getLevel() {
        return level;
    }

    public int getExp(){
        return exp;
    }

    public int getExpToLevelUp() {
        return expToLevelUp;
    }

    public int getPotionCount() {
        return potionCount;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getGold() {
        return gold;
    }
}
