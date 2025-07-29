package com.ytasakawa.rpg_used_javafx_ytasakawa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloApplication extends Application {
    private Label playerNameLabel;
    private Label playerHpLabel;
    private Label playerMpLabel;
    private Label playerLevelExpLabel;
    private Label playerPotionLabel;
    private Label playerWeaponLabel;
    private Label playerGoldLabel;

    private HBox enemyStatsDisplayBox;

    //private Label enemyNameLabel;
    //private Label enemyHpLabel;
    //private List<Label> enemyHpLabels;

    private TextArea messageTextArea;

    private Button attackButton;
    private Button magicButton;
    private Button potionButton;
    private Button escapeButton;
    private Button multiAttackButton;

    private HBox magicButtonsBox;
    private Button fireMagicButton;
    private Button iceMagicButton;
    private Button thunderMagicButton;
    private Button windMagicButton;
    private Button backButton;

    private HBox townButtonsBox;
    private Button innButton;
    private Button itemShopButton;
    private Button weaponShopButton;
    private Button exitTownButton;

    private VBox weaponShopVBox;
    private Button buyIronSwordButton;
    private Button buyNaginataButton;
    private Button backFromWeaponShopButton;
    private ScrollPane weaponShopScrollPane;

    private Player player;
    private Enemy[] currentEnemies;
    private Stage primaryStage;
    private GameMap gameMap;
    private Town town;

    private enum GameState {
        EXPLORING,
        BATTLE,
        MAGIC_SELECT,
        GAMEOVER,
        WIN_ROUND,
        IN_TOWN,
        WEAPON_SHOP
    }
    private GameState currentState;

    private Random random = new Random();

    private int playerX = 5;
    private int playerY = 5;
    private static final int MAP_DISPLAY_WIDTH = 10;
    private static final int MAP_DISPLAY_HEIGHT = 10;
    private static final int TILE_SIZE = 32;

    private int stepsSinceLastEncounter = 0;
    private static final int ENCOUNTER_THRESHOLD = 3;
    private static final int ENCOUNTER_PROBABILITY = 3;

    private ImageView mapView;
    private ImageView playerView;
    private StackPane mapAndPlayerPane;
    private HBox enemyImageDisplayBox;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        player = new Player("勇者", 30, 1);
        gameMap = new GameMap(MAP_DISPLAY_WIDTH, MAP_DISPLAY_HEIGHT);
        town = new Town();
        currentState = GameState.EXPLORING;

        playerNameLabel = new Label("名前: " + player.getName());
        playerHpLabel = new Label("HP: " + player.getHp() + "/" + player.getMaxHp());
        playerMpLabel = new Label("MP: " + player.getMp() + "/" + player.getMaxMp());
        playerLevelExpLabel = new Label("Lv: " + player.getLevel() + " Exp: " + player.getExp() + "/" + player.getExpToLevelUp());
        playerPotionLabel = new Label("ポーション: " + player.getPotionCount() + "個");
        playerWeaponLabel = new Label("装備: " + player.getEquippedWeapon().getName() + "(攻: " + player.getEquippedWeapon().getAttackPower() + ")");
        playerGoldLabel = new Label("所持金: " + player.getGold() + " GOLD");

//        enemyHpLabels = new ArrayList<>();
//        enemyNameLabel = new Label("敵: ");
//        enemyHpLabel = new Label("敵HP: ");
//        enemyHpLabels.add(enemyHpLabel);
//        enemyNameLabel.setVisible(false);
//        enemyHpLabel.setVisible(false);

        messageTextArea = new TextArea("===YUIT QUEST START! ===\n矢印キーで移動してください。");
        messageTextArea.setEditable(false);
        messageTextArea.setWrapText(true);
        messageTextArea.setPrefRowCount(5);

        attackButton = new Button("攻撃する");
        attackButton.setOnAction(e -> handleAttack());

        magicButton = new Button("まほう");
        magicButton.setOnAction(e -> handleMagicSelect());

        potionButton = new Button("ポーションを使う");
        potionButton.setOnAction(e -> handleUsePotion());

        escapeButton = new Button("逃げる");
        escapeButton.setOnAction(e -> handleEscape());

        multiAttackButton = new Button("範囲攻撃");
        multiAttackButton.setOnAction(e -> handleMultiAttack());


        fireMagicButton = new Button("火");
        fireMagicButton.setOnAction(e -> handleMagicAttack(Element.FIRE));

        iceMagicButton = new Button("氷");
        iceMagicButton.setOnAction(e -> handleMagicAttack(Element.ICE));

        thunderMagicButton = new Button("雷");
        thunderMagicButton.setOnAction(e -> handleMagicAttack(Element.THUNDER));

        windMagicButton = new Button("風");
        windMagicButton.setOnAction(e -> handleMagicAttack(Element.WIND));

        backButton = new Button("戻る");
        backButton.setOnAction(e -> updateUIForState(GameState.BATTLE));

        magicButtonsBox = new HBox(10, fireMagicButton, iceMagicButton, thunderMagicButton, windMagicButton, backButton);
        magicButtonsBox.setAlignment(Pos.CENTER);
        magicButtonsBox.setVisible(false);

        HBox actionButtonsBox = new HBox(10, attackButton, magicButton, potionButton, escapeButton, multiAttackButton);
        actionButtonsBox.setAlignment(Pos.CENTER);
        multiAttackButton.setVisible(false);

        innButton = new Button("宿屋 (HP/MP全回復)");
        innButton.setOnAction(e -> handleInn());

        final int POTION_PRICE = 10;
        itemShopButton = new Button("道具屋(ポーション " + POTION_PRICE + " GOLD)");
        itemShopButton.setOnAction(e -> handleItemShop(POTION_PRICE));

        weaponShopButton = new Button("武器屋 (装備購入)");
        weaponShopButton.setOnAction(e -> handleWeaponShop());

        exitTownButton = new Button("村を出る");
        exitTownButton.setOnAction(e -> handleExitTown());

        townButtonsBox = new HBox(10, innButton, itemShopButton, weaponShopButton, exitTownButton);
        townButtonsBox.setAlignment(Pos.CENTER);
        townButtonsBox.setVisible(false);

        Weapon ironSword = town.getWeaponsForSale().get(0);
        buyIronSwordButton = new Button(ironSword.getName() + " (攻" + ironSword.getAttackPower() + ", " + ironSword.getPrice() + " GOLD)");
        buyIronSwordButton.setOnAction(e -> handleBuyWeapon(ironSword));

        Weapon naginata = town.getWeaponsForSale().get(1);
        buyNaginataButton = new Button(naginata.getName() + " (攻" + naginata.getAttackPower() + ", " + naginata.getPrice() + " GOLD)");
        buyNaginataButton.setOnAction(e -> handleBuyWeapon(naginata));

        backFromWeaponShopButton = new Button("戻る");
        backFromWeaponShopButton.setOnAction(e -> updateUIForState(GameState.IN_TOWN));

        weaponShopVBox = new VBox(10, new Label("武器屋の品揃え:"), buyIronSwordButton, buyNaginataButton, backFromWeaponShopButton);
        weaponShopVBox.setAlignment(Pos.CENTER);
//        weaponShopVBox.setVisible(false);
        weaponShopScrollPane = new ScrollPane(weaponShopVBox);
        weaponShopScrollPane.setFitToWidth(true);
        weaponShopScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        weaponShopScrollPane.setVisible(false);

        try {
            Image mapImage = new Image(getClass().getResourceAsStream("/MAP.jpg"));
            mapView = new ImageView(mapImage);
            mapView.setPreserveRatio(true);
            mapView.setFitWidth(MAP_DISPLAY_WIDTH * TILE_SIZE);
            mapView.setFitHeight(MAP_DISPLAY_HEIGHT * TILE_SIZE);

            Image playerImage = new Image(getClass().getResourceAsStream("/Player2D.png"));
            playerView = new ImageView(playerImage);
            playerView.setFitWidth(TILE_SIZE);
            playerView.setFitHeight(TILE_SIZE);

            mapAndPlayerPane = new StackPane(mapView, playerView);
            mapAndPlayerPane.setAlignment(Pos.TOP_LEFT);
            mapAndPlayerPane.setPrefSize(MAP_DISPLAY_WIDTH * TILE_SIZE, MAP_DISPLAY_HEIGHT * TILE_SIZE);
            mapAndPlayerPane.setMinSize(MAP_DISPLAY_WIDTH * TILE_SIZE, MAP_DISPLAY_HEIGHT * TILE_SIZE);

            updatePlayerPositionOnMap();

        } catch (Exception e) {
            System.err.println("画像ファイルの読み込みに失敗しました: " + e.getMessage());
            mapView = new ImageView();
            playerView = new ImageView();
            mapAndPlayerPane = new StackPane();
        }

        enemyImageDisplayBox = new HBox(10);
        enemyImageDisplayBox.setAlignment(Pos.CENTER);
        enemyImageDisplayBox.setPrefHeight(200);
        enemyImageDisplayBox.setVisible(false);

        VBox root = new VBox(10);
//        root.setPadding(new Insets(20));
//        root.setAlignment(Pos.TOP_CENTER);


        VBox playerStatsBox = new VBox(5, playerNameLabel, playerHpLabel, playerMpLabel, playerLevelExpLabel, playerPotionLabel, playerWeaponLabel, playerGoldLabel);
        playerStatsBox.setAlignment(Pos.TOP_LEFT);
        playerStatsBox.setPadding(new Insets(0, 0, 10, 0));

        enemyStatsDisplayBox = new HBox(20);
        enemyStatsDisplayBox.setAlignment(Pos.TOP_RIGHT);
        enemyStatsDisplayBox.setPadding(new Insets(0, 0, 10, 0));
        //enemyStatsHBox.getChildren().addAll(enemyNameLabel, enemyHpLabel);

        HBox topInfoBox = new HBox(50, playerStatsBox, enemyStatsDisplayBox);
        topInfoBox.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(
                topInfoBox,
                enemyImageDisplayBox,
                mapAndPlayerPane,
                messageTextArea,
                actionButtonsBox,
                magicButtonsBox,
                townButtonsBox,
                weaponShopScrollPane
        );

        Scene scene = new Scene(root, 600, 750);
        primaryStage.setTitle("YUIT QUEST");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(this::handleKeyPress);
        scene.getRoot().requestFocus();

        updatePlayerStatsUI();
        updateUIForState(GameState.EXPLORING);
    }

    private void updatePlayerPositionOnMap() {
        playerView.setTranslateX(playerX * TILE_SIZE);
        playerView.setTranslateY(playerY * TILE_SIZE);
    }

    private void handleKeyPress(KeyEvent event) {
        if (currentState == GameState.EXPLORING) {
            int oldPlayerX = playerX;
            int oldPlayerY = playerY;

            int newPlayerX = playerX;
            int newPlayerY = playerY;

            switch (event.getCode()) {
                case UP:
                    newPlayerY = Math.max(0, playerY - 1);
                    break;
                case DOWN:
                    newPlayerY = Math.min(gameMap.getHeight() - 1, playerY + 1);
                    break;
                case LEFT:
                    newPlayerX = Math.max(0, playerX - 1);
                    break;
                case RIGHT:
                    newPlayerX = Math.min(gameMap.getWidth() - 1, playerX + 1);
                    break;
                default:
                    return;

            }

            if (gameMap.isWalkable(newPlayerX, newPlayerY)) {
                playerX = newPlayerX;
                playerY = newPlayerY;
            } else {
                appendMessage("\nそこには進めない！");
                return;
            }

            if (playerX != oldPlayerX || playerY != oldPlayerY) {
                updatePlayerPositionOnMap();
                appendMessage("\n現在地: (" + playerX + ", " + playerY + ")");

                if (gameMap.getTileType(playerX, playerY) == MapTileType.TOWN) {
                    appendMessage("\n村に到着した！");
                    updateUIForState(GameState.IN_TOWN);
                } else if (gameMap.getTileType(playerX, playerY) == MapTileType.CAVE) {
                    appendMessage("\n洞窟の奥からドラゴンが現れた！");
                    startBossEncounter();
                } else {
                    stepsSinceLastEncounter++;
                    checkRandomEncounter();
                }
            }
        }
    }

    private void checkRandomEncounter() {
        if (stepsSinceLastEncounter >= ENCOUNTER_THRESHOLD && random.nextInt(ENCOUNTER_PROBABILITY) == 0) {
            appendMessage("\n何かが現れた！");
            stepsSinceLastEncounter = 0;
            startNormalEncounter();
        }
    }

    private void startNormalEncounter(){
        int numEnemies = random.nextInt(3) + 1;
        currentEnemies = new Enemy[numEnemies];
        StringBuilder encounterMessage = new StringBuilder("\n");
        for (int i =0; i < numEnemies; i++) {
            currentEnemies[i] = generateRandomEnemy();
            encounterMessage.append("敵！").append(currentEnemies[i].getName()).append("」が現れた！ ");
        }
        appendMessage(encounterMessage.toString());

        updateEnemyStatsUI();
        loadEnemyImages();
        updateUIForState(GameState.BATTLE);
    }

    private void startBossEncounter() {
        currentEnemies = new Enemy[1];

        currentEnemies[0] = new Enemy("ドラゴン", 200, 20, 500, Element.ICE, 1000);
        appendMessage("\nボス「ドラゴン」が現れた！");

        updateEnemyStatsUI();
        loadEnemyImages();
        updateUIForState(GameState.BATTLE);
    }

    private void handleAttack() {
        if (currentState != GameState.BATTLE) return;

        Enemy targetEnemy = null;
        for (Enemy enemy : currentEnemies) {
            if (enemy.isAlive()) {
                targetEnemy = enemy;
                break;
            }
        }

        if (targetEnemy == null) {
            appendMessage("攻撃する敵がいません。");
            return;
        }

        int playerAttackDamage = player.getEquippedWeapon().getAttackPower() + player.getLevel();
        targetEnemy.takeDamage(playerAttackDamage);
        appendMessage(player.getName() + "のこうげき！ " + targetEnemy.getName() + "に" + playerAttackDamage + "のダメージ！");

        checkBattleEnd();
        if (currentState == GameState.BATTLE) {
            enemyTurn();
        }
    }

    private void handleMultiAttack() {
        if (currentState != GameState.BATTLE) return;

        if (player.getEquippedWeapon().getName().equals("薙刀")) {
            int baseDamage = player.getEquippedWeapon().getAttackPower() / 2 + player.getLevel();

            appendMessage(player.getName() + "の範囲攻撃！");
            for (Enemy enemy : currentEnemies) {
                if (enemy.isAlive()) {
                    enemy.takeDamage(baseDamage);
                    appendMessage(enemy.getName() + "に" + baseDamage + "のダメージ！");
                }
            }
            checkBattleEnd();
            if (currentState == GameState.BATTLE) {
                enemyTurn();
            }
        } else {
            appendMessage("範囲攻撃は薙刀を装備している時のみ使えます！");
        }
    }

    private void handleMagicSelect() {
        if (currentState != GameState.BATTLE) return;
        appendMessage("\nどの魔法を使いますか？");
        updateUIForState(GameState.MAGIC_SELECT);
    }

    private void handleMagicAttack(Element element) {
        if (currentState != GameState.MAGIC_SELECT) return;

        int magicCost = 3;
        if (player.getMp() < magicCost) {
            appendMessage("MPが足りない！ 魔法が使えない！");
            updateUIForState(GameState.BATTLE);
            return;
        }

        Enemy targetEnemy = null;
        for (Enemy enemy : currentEnemies) {
            if (enemy.isAlive()) {
                targetEnemy = enemy;
                break;
            }
        }

        if (targetEnemy == null) {
            appendMessage("魔法をつかう敵がいない！");
            updateUIForState(GameState.BATTLE);
            return;
        }

        int baseDamage = 10 + player.getLevel();
        boolean isWeak = (element == targetEnemy.getWeakElement());
        int damage = isWeak ? (int) (baseDamage * 1.3) : baseDamage;

        player.magicAttack(targetEnemy, element);
        appendMessage(player.getName() + "の" + elementToString(element) + "のまほう！ " + targetEnemy.getName() + "に" + damage + "のダメージ！");
        if (isWeak) {
            appendMessage("弱点を突いた！");
        }
        appendMessage("MPを " + magicCost + " 消費した (残りMP: " + player.getMp() + ")");

        updatePlayerStatsUI();

        checkBattleEnd();
        if (currentState == GameState.BATTLE) {
            enemyTurn();
        }
    }

    private void handleUsePotion() {
        if (currentState != GameState.BATTLE) return;

        if (player.getPotionCount() > 0) {
            player.usePotion();
            appendMessage(player.getName() + "はポーションを使った！HPが回復した！ (残り: " + player.getPotionCount() + "個) ");
            updatePlayerStatsUI();
            enemyTurn();
        } else {
            appendMessage("ポーションがもうありません！");
        }
    }

    private void handleEscape() {
        if (currentState != GameState.BATTLE) return;

        if (random.nextBoolean()) {
            appendMessage(player.getName() + "はうまく逃げ出した！");
            currentEnemies = null;
            updateEnemyStatsUI();
            updateUIForState(GameState.EXPLORING);
        } else {
            appendMessage("しかし逃げ道をふさがれた！");
            enemyTurn();
        }
    }

    private void checkBattleEnd() {
        boolean allEnemiesDead = true;
        int totalExp = 0;
        int totalGold = 0;
        if (currentEnemies != null) {
            for (Enemy enemy : currentEnemies) {
                if (enemy.isAlive()) {
                    allEnemiesDead = false;
                    break;
                } else {
                    totalExp += enemy.getExp();
                    totalGold += enemy.getGoldDrop();
                }
            }
        } else {
            allEnemiesDead = true;
        }


        if (allEnemiesDead) {
            if (currentEnemies != null && currentEnemies.length == 1 && currentEnemies[0].getName().equals("ドラゴン") && !currentEnemies[0].isAlive()) {
                appendMessage("\n*** ドラゴンを打ち倒し。世界に平和が訪れた！ ***");
                appendMessage("ゲームクリア！おめでとう！");
                player.addGold(totalGold);
                updatePlayerStatsUI();
                updateUIForState(GameState.GAMEOVER);
            } else {
                appendMessage("\n敵をすべてたおした！");
                player.gainExp(totalExp);
                player.addGold(totalGold);
                appendMessage(totalGold + " GOLD を手に入れた！");
                updatePlayerStatsUI();
                updateUIForState(GameState.WIN_ROUND);
            }
        }
    }

    private void enemyTurn() {
        for (Enemy enemy : currentEnemies) {
            if (player.isAlive() && enemy.isAlive()) {
                enemy.attack(player);
                appendMessage(enemy.getName() + "のはんげき！ " + player.getName() + "に" + enemy.getAttackPower() + "のダメージ！");
                updatePlayerStatsUI();

                if (!player.isAlive()) {
                    appendMessage("\n" + player.getName() + "は力尽きた... Game Over");
                    updateUIForState(GameState.GAMEOVER);
                    return;
                }
            }
        }
    }

    private void updatePlayerStatsUI() {
        playerNameLabel.setText("名前: " + player.getName());
        playerHpLabel.setText("HP: " + player.getHp() + "/" + player.getMaxHp());
        playerMpLabel.setText("MP: " + player.getMp() + "/" + player.getMaxMp());
        playerLevelExpLabel.setText("Lv: " + player.getLevel() + " Exp: " + player.getExpToLevelUp());
        playerPotionLabel.setText("ポーション: " + player.getPotionCount() + "個");
        playerWeaponLabel.setText("装備: " + player.getEquippedWeapon().getName() + " (攻:" + player.getEquippedWeapon().getAttackPower() + ")");
        playerGoldLabel.setText("所持金: " + player.getGold() + " GOLD");
    }

    private void updateEnemyStatsUI() {
        enemyStatsDisplayBox.getChildren().clear();

        if (currentEnemies != null && currentEnemies.length > 0) {
            boolean anyEnemyAlive = false;
            for (int i = 0; i < currentEnemies.length; i++) {
                Enemy enemy = currentEnemies[i];
                if (enemy.isAlive()) {
                    anyEnemyAlive = true;
                    Label nameLabel = new Label("敵" + (i + 1) + ": " + enemy.getName());
                    Label hpLabel = new Label("HP: " + enemy.getHp() + "/" + enemy.getMaxHp());
                    VBox enemyIndividualStatsBox = new VBox(2, nameLabel, hpLabel);
                    enemyIndividualStatsBox.setAlignment(Pos.TOP_LEFT);
                    enemyStatsDisplayBox.getChildren().add(enemyIndividualStatsBox);
                }
            }
            enemyStatsDisplayBox.setVisible(anyEnemyAlive);
        } else {
            enemyStatsDisplayBox.setVisible(false);
        }
    }

private String elementToString(Element element) {
        switch (element) {
            case FIRE: return "火";
            case ICE: return "氷";
            case THUNDER: return "雷";
            case WIND: return "風";
            default: return "？";
        }
}

private void updateUIForState(GameState newState) {
        currentState = newState;

        attackButton.setVisible(false);
        magicButton.setVisible(false);
        potionButton.setVisible(false);
        escapeButton.setVisible(false);
        multiAttackButton.setVisible(false);
        magicButtonsBox.setVisible(false);
        townButtonsBox.setVisible(false);
//        weaponShopVBox.setVisible(false);
        weaponShopScrollPane.setVisible(false);

        mapAndPlayerPane.setVisible(false);
        enemyImageDisplayBox.setVisible(false);

        if (currentState == GameState.EXPLORING) {
            mapAndPlayerPane.setVisible(true);
            updateEnemyStatsUI();
            appendMessage("\n現在地: (" + playerX + ", " + playerY + ")\n矢印キーで移動してください。");
            if (primaryStage.getScene() != null) {
                primaryStage.getScene().getRoot().requestFocus();
            }
        } else if (currentState == GameState.BATTLE) {
            mapAndPlayerPane.setVisible(false);
            enemyImageDisplayBox.setVisible(true);
            attackButton.setVisible(true);
            magicButton.setVisible(true);
            potionButton.setVisible(true);
            escapeButton.setVisible(true);
            if (player.getEquippedWeapon().getName().equals("薙刀")) {
                multiAttackButton.setVisible(true);
            }
            updateEnemyStatsUI();
        } else if (currentState == GameState.MAGIC_SELECT) {
            mapAndPlayerPane.setVisible(false);
            enemyImageDisplayBox.setVisible(true);
            magicButtonsBox.setVisible(true);
        } else if (currentState == GameState.GAMEOVER) {
            mapAndPlayerPane.setVisible(false);
            enemyImageDisplayBox.setVisible(false);
        } else if (currentState == GameState.WIN_ROUND) {
            mapAndPlayerPane.setVisible(false);
            enemyImageDisplayBox.setVisible(false);
            currentEnemies = null;
            updateEnemyStatsUI();
            appendMessage("冒険を続ける...");
            updateUIForState(GameState.EXPLORING);
        } else if (currentState == GameState.IN_TOWN) {
            mapAndPlayerPane.setVisible(true);
            townButtonsBox.setVisible(true);
            //enemyNameLabel.setVisible(false);
            //enemyHpLabel.setVisible(false);
            updateEnemyStatsUI();
            appendMessage("\n村に到着した。何をしますか？");
        } else if (currentState == GameState.WEAPON_SHOP) {
            mapAndPlayerPane.setVisible(true);
            weaponShopScrollPane.setVisible(true);
            appendMessage("\n武器屋：いらっしゃい！");
    }
}

private void appendMessage(String message) {
        messageTextArea.appendText("\n" + message);
        messageTextArea.setScrollTop(Double.MAX_VALUE);
}

public Enemy generateRandomEnemy() {
        int type = random.nextInt(3);

        switch (type) {
            case 0:
                return new Enemy("スライム", 10,3,5, Element.FIRE, 5);
            case 1:
                return new Enemy("ゴブリン", 15, 5, 10, Element.ICE, 10);
            case 2:
                return new Enemy("オオカミ", 20, 6, 12, Element.THUNDER, 15);
            default:
                return new Enemy("謎の敵", 12, 4, 8, Element.WIND, 8);
        }
}



private void loadEnemyImages() {
    enemyImageDisplayBox.getChildren().clear();
        if (currentEnemies != null) {
            for (Enemy enemy : currentEnemies) {
                if (enemy.isAlive()) {
                    String imagePath = "";
                    switch (enemy.getName()) {
                        case "スライム":
                            imagePath = "/slime.png";
                            break;
                        case "ゴブリン":
                            imagePath = "/goblin.png";
                            break;
                        case "オオカミ":
                            imagePath = "/wolf.png";
                            break;
                        case "ドラゴン":
                            imagePath = "/dragon.png";
                            break;
                        default:
                            System.err.println("対応する画像ファイルが指定されていません: " + enemy.getName());
                            continue;
                    }

                    try {
                        Image enemyImage = new Image(getClass().getResourceAsStream(imagePath));
                        ImageView view = new ImageView(enemyImage);
                        view.setFitWidth(100);
                        view.setFitHeight(100);
                        view.setPreserveRatio(true);

                        enemyImageDisplayBox.getChildren().add(view);
                    } catch (NullPointerException e) {
                        System.err.println("敵の画像ファイルが見つかりません: " + imagePath + ". エラー: " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("敵画像のロード中に予期せぬエラーが発生しました: " + e.getMessage());
                    }
                }
            }
        }
    }

private void handleInn() {
        town.restAtInn(player);
        updatePlayerStatsUI();
        appendMessage("宿屋で休んだ！HPとMPが全回復した！");
}

private void handleItemShop(int price) {
        if (town.buyPotion(player, price)) {
            updatePlayerStatsUI();
            appendMessage("ポーションを1個購入した！ " + price + " GOLDを消費した。");
        } else {
            appendMessage("ゴールドが足りません！ ポーションを購入出来ませんでした。");
        }
}

private void handleWeaponShop() {
        updateUIForState(GameState.WEAPON_SHOP);
}

private void handleBuyWeapon(Weapon weapon) {
        if (player.getEquippedWeapon().getName().equals(weapon.getName())) {
            appendMessage(weapon.getName() + "はすでに装備している！");
            return;
        }

        if (town.buyWeapon(player, weapon)) {
            updatePlayerStatsUI();
            appendMessage(weapon.getName() + "を装備した！ " + weapon.getPrice() + " GOLDを消費した。");
        } else {
            appendMessage("ゴールドが足りません！ " + weapon.getName() + "を購入できませんでした。");
        }

        updateUIForState(GameState.WEAPON_SHOP);
}

private void handleExitTown() {
        appendMessage("村を出た。探索を再開します。");
        updateUIForState(GameState.EXPLORING);
}

public static void main(String[] args) {
        launch(args);
    }
}