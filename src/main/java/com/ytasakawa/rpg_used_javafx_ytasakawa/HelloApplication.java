package com.ytasakawa.rpg_used_javafx_ytasakawa;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
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
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.Animation;
import javafx.util.Duration;
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

    private VBox resultBox;
    private Label resultLabelExp;
    private Label resultLabelGold;
    private Label resultLabelLevelUp;
    private Button continueButton;
    private ScrollPane resultScrollPane;

    private VBox buttonPanel;

    private Player player;
    private Enemy[] currentEnemies;
    private Enemy selectedTarget;
    private Stage primaryStage;
    private GameMap gameMap;
    private Town town;


    private enum GameState {
        TITLE,
        EXPLORING,
        BATTLE,
        MAGIC_SELECT,
        BATTLE_RESULT,
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
    private StackPane mainContentPane;

    private MediaPlayer bgmPlayer;
    private MediaPlayer sePlayer;
    private ImageView effectImageView;
    private List<ImageView> multiEffectViews = new ArrayList<>();

    private int gainedExp;
    private int gainedGold;
    private boolean leveledUp;
    private String currentBgm = "";

    private boolean isTransitioning = false;

    private StackPane rootPane;
    private VBox titlePane;
    private VBox gamePane;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        player = new Player("勇者", 30, 1);
        gameMap = new GameMap(MAP_DISPLAY_WIDTH, MAP_DISPLAY_HEIGHT);
        town = new Town();
        currentState = GameState.TITLE;

        playerNameLabel = new Label("名前: " + player.getName());
        playerHpLabel = new Label("HP: " + player.getHp() + "/" + player.getMaxHp());
        playerMpLabel = new Label("MP: " + player.getMp() + "/" + player.getMaxMp());
        playerLevelExpLabel = new Label("Lv: " + player.getLevel() + " Exp: " + player.getExp() + "/" + player.getExpToLevelUp());
        playerPotionLabel = new Label("ポーション: " + player.getPotionCount() + "個");
        playerWeaponLabel = new Label("装備: " + player.getEquippedWeapon().getName() + "(攻: " + player.getEquippedWeapon().getAttackPower() + ")");
        playerGoldLabel = new Label("所持金: " + player.getGold() + " GOLD");

        messageTextArea = new TextArea("===YUIT QUEST START! ===\n矢印キーで移動してください。");
        messageTextArea.setEditable(false);
        messageTextArea.setWrapText(true);
        messageTextArea.setPrefRowCount(5);
        messageTextArea.setPrefHeight(100);

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
        weaponShopScrollPane = new ScrollPane(weaponShopVBox);
        weaponShopScrollPane.setFitToWidth(true);
        weaponShopScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        weaponShopScrollPane.setVisible(false);

        resultLabelExp = new Label();
        resultLabelGold = new Label();
        resultLabelLevelUp = new Label();
        continueButton = new Button("続ける");
        continueButton.setOnAction(e -> handleContinue());
        resultBox = new VBox(20, resultLabelExp, resultLabelGold, resultLabelLevelUp, continueButton);
        resultBox.setAlignment(Pos.CENTER);

        resultScrollPane = new ScrollPane(resultBox);
        resultScrollPane.setFitToWidth(true);
        resultScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        resultScrollPane.setVisible(false);

        ImageView playerViewTemp = null;
        try {
            Image mapImage = new Image(getClass().getResourceAsStream("/MAP.jpg"));
            mapView = new ImageView(mapImage);
            mapView.setPreserveRatio(true);
            mapView.setFitWidth(MAP_DISPLAY_WIDTH * TILE_SIZE);
            mapView.setFitHeight(MAP_DISPLAY_HEIGHT * TILE_SIZE);

            Image playerImage = new Image(getClass().getResourceAsStream("/Player2D.png"));
            playerViewTemp = new ImageView(playerImage);
            playerViewTemp.setFitWidth(TILE_SIZE);
            playerViewTemp.setFitHeight(TILE_SIZE);

            mapAndPlayerPane = new StackPane(mapView, playerViewTemp);
            mapAndPlayerPane.setAlignment(Pos.TOP_LEFT);
            mapAndPlayerPane.setPrefSize(MAP_DISPLAY_WIDTH * TILE_SIZE, MAP_DISPLAY_HEIGHT * TILE_SIZE);
            mapAndPlayerPane.setMinSize(MAP_DISPLAY_WIDTH * TILE_SIZE, MAP_DISPLAY_HEIGHT * TILE_SIZE);
        } catch (Exception e) {
            System.err.println("画像ファイルの読み込みに失敗しました: " + e.getMessage());
            mapView = new ImageView();
            playerViewTemp = new ImageView();
            mapAndPlayerPane = new StackPane();
        }
        playerView = playerViewTemp;
        if(playerView != null) {
            updatePlayerPositionOnMap();
        }

        enemyImageDisplayBox = new HBox(10);
        enemyImageDisplayBox.setAlignment(Pos.CENTER);
        enemyImageDisplayBox.setPrefHeight(200);
        enemyImageDisplayBox.setVisible(false);

        gamePane = new VBox(10);
        gamePane.setPadding(new Insets(20));
        gamePane.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(messageTextArea, Priority.ALWAYS);

        VBox playerStatsBox = new VBox(5, playerNameLabel, playerHpLabel, playerMpLabel, playerLevelExpLabel, playerPotionLabel, playerWeaponLabel, playerGoldLabel);
        playerStatsBox.setAlignment(Pos.TOP_LEFT);
        playerStatsBox.setPadding(new Insets(0, 0, 10, 0));

        enemyStatsDisplayBox = new HBox(20);
        enemyStatsDisplayBox.setAlignment(Pos.TOP_RIGHT);
        enemyStatsDisplayBox.setPadding(new Insets(0, 0, 10, 0));

        HBox topInfoBox = new HBox(50, playerStatsBox, enemyStatsDisplayBox);
        topInfoBox.setAlignment(Pos.TOP_CENTER);

        effectImageView = new ImageView();
        effectImageView.setFitWidth(100);
        effectImageView.setFitHeight(100);
        effectImageView.setPreserveRatio(true);
        effectImageView.setVisible(false);

        mainContentPane = new StackPane();
        mainContentPane.setAlignment(Pos.CENTER);
        mainContentPane.getChildren().addAll(mapAndPlayerPane, enemyImageDisplayBox, resultScrollPane, effectImageView);

        buttonPanel = new VBox(10, actionButtonsBox, magicButtonsBox, townButtonsBox, weaponShopScrollPane);
        buttonPanel.setAlignment(Pos.CENTER);

        playBGM("bgm_field.mp3");

        gamePane.getChildren().addAll(
                topInfoBox,
                mainContentPane,
                messageTextArea,
                buttonPanel
        );

        titlePane = new VBox(20);
        titlePane.setAlignment(Pos.CENTER);
        try {
            Image titleImage = new Image(getClass().getResourceAsStream("/title.png"));
            ImageView titleView = new ImageView(titleImage);
            titleView.setPreserveRatio(true);
            titleView.setFitWidth(500);

            Label startLabel = new Label ("キーを押してゲームを開始してください");
            startLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

            titlePane.getChildren().addAll(titleView, startLabel);
            titlePane.setStyle("-fx-background-color: black;");
        } catch (Exception e) {
            System.err.println("タイトル画像の読込に失敗しました: " + e.getMessage());
            titlePane.getChildren().add(new Label("画像の読込に失敗"));
        }

        rootPane = new StackPane();
        rootPane.getChildren().addAll(gamePane, titlePane);

        Scene scene = new Scene(rootPane, 600, 750);
        primaryStage.setTitle("YUIT QUEST");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(this::handleKeyPress);
        scene.getRoot().requestFocus();

        updatePlayerStatsUI();
        updateUIForState(GameState.TITLE);
    }

    private void updatePlayerPositionOnMap() {
        if(playerView != null){
            playerView.setTranslateX(playerX * TILE_SIZE);
            playerView.setTranslateY(playerY * TILE_SIZE);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (currentState == GameState.TITLE) {
            if (!isTransitioning) {
                isTransitioning = true;

                String soundFile = getClass().getResource("/se_start.mp3").toExternalForm();
                Media media = new Media(soundFile);
                MediaPlayer startSePlayer = new MediaPlayer(media);
                startSePlayer.setOnEndOfMedia(() -> {
                    javafx.application.Platform.runLater(() -> {
                        updateUIForState(GameState.EXPLORING);
                    });
                });
                startSePlayer.play();
            }
            return;
        }
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

                MapTileType currentTileType = gameMap.getTileType(playerX, playerY);

                if (currentTileType == MapTileType.TOWN) {
                    appendMessage("\n村に到着した！");
                    updateUIForState(GameState.IN_TOWN);
                } else if (currentTileType == MapTileType.CAVE) {
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

    private void startNormalEncounter() {
        int numEnemies = random.nextInt(3) + 1;
        currentEnemies = new Enemy[numEnemies];
        StringBuilder encounterMessage = new StringBuilder("\n");
        MapTileType playerCurrentTileType = gameMap.getTileType(playerX, playerY);
        for (int i = 0; i < numEnemies; i++) {
            currentEnemies[i] = generateRandomEnemy(playerCurrentTileType);
            encounterMessage.append("敵「").append(currentEnemies[i].getName()).append("」が現れた！ ");
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

        if (selectedTarget == null || !selectedTarget.isAlive()) {
            appendMessage("攻撃するターゲットがいません。モンスターをクリックして選択してください。");
            return;
        }
        setActionButtonsDisabled(true);

        Enemy targetEnemy = selectedTarget;

        int playerAttackDamage = player.getEquippedWeapon().getAttackPower() + player.getLevel();
        targetEnemy.takeDamage(playerAttackDamage);
        appendMessage(player.getName() + "のこうげき！ " + targetEnemy.getName() + "に" + playerAttackDamage + "のダメージ！");

        playSE("se_attack.mp3");

        SpriteData attackSpriteData = new SpriteData(120, 120, 9, 1);

        Runnable afterEffectActions = () -> {
            if (!targetEnemy.isAlive()) {
                appendMessage(targetEnemy.getName() + " を倒した！");
            }

            updateEnemyStatsUI();
            loadEnemyImages();
            checkBattleEnd();

            if (currentState == GameState.BATTLE) {
                enemyTurn();
            } else {
                setActionButtonsDisabled(false);
            }
        };
        displayEffect("/effect_attack.png", attackSpriteData, targetEnemy, afterEffectActions);
    }

    private void handleMultiAttack() {
        if (currentState != GameState.BATTLE || !player.getEquippedWeapon().getName().equals("薙刀")) {
            appendMessage("範囲攻撃は薙刀装備時のみ使えます。");
            return;
        }

        setActionButtonsDisabled(true);
        appendMessage(player.getName() + "の範囲攻撃！");
        playSE("se_attack.mp3");
        SpriteData multiAttackSpriteData = new SpriteData(640, 480, 16, 1);

        Runnable afterEffectActions = () -> {
            int baseDamage = player.getEquippedWeapon().getAttackPower() / 2 + player.getLevel();
            for (Enemy enemy : currentEnemies) {
                if (enemy.isAlive()) {
                    enemy.takeDamage(baseDamage);
                    appendMessage(enemy.getName() + "に" + baseDamage + "のダメージ！");
                    if (!enemy.isAlive()) {
                        appendMessage(enemy.getName() + " を倒した！");
                    }
                }
            }

            updateEnemyStatsUI();
            loadEnemyImages();
            checkBattleEnd();

            if (currentState == GameState.BATTLE) {
                enemyTurn();
            } else {
                setActionButtonsDisabled(false);
            }
        };
        List<Enemy> aliveEnemies = new ArrayList<>();
        if (currentEnemies != null) {
            for (Enemy e : currentEnemies) {
                if (e.isAlive()) {
                    aliveEnemies.add(e);
                }
            }
        }

        displayMultiTargetEffect("/effect_multiattack.png", multiAttackSpriteData, aliveEnemies.toArray(new Enemy[0]), afterEffectActions);
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

        if (selectedTarget == null || !selectedTarget.isAlive()) {
            appendMessage("魔法をつかうターゲットがいません。");
            updateUIForState(GameState.BATTLE);
            return;
        }

        setActionButtonsDisabled(true);

        Enemy targetEnemy = selectedTarget;

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

        playSE("se_magic.mp3");

        Runnable afterEffectActions = () -> {
            if (!targetEnemy.isAlive()) {
                appendMessage(targetEnemy.getName() + " を倒した！");
            }
            updatePlayerStatsUI();
            loadEnemyImages();
            checkBattleEnd();

            if (currentState == GameState.BATTLE) {
                enemyTurn();
            } else {
                setActionButtonsDisabled(false);
            }
        };

        String effectPath;
        SpriteData magicSpriteData;

        switch (element) {
            case FIRE:
                effectPath = "/effect_fire.png";
                magicSpriteData = new SpriteData (320, 240, 10, 1);
                break;
            case ICE:
                effectPath = "/effect_ice.png";
                magicSpriteData = new SpriteData (192, 192, 10, 1);
                break;
            case THUNDER:
                effectPath = "/effect_thunder.png";
                magicSpriteData = new SpriteData (192, 192, 10, 1);
                break;
            case WIND:
                effectPath = "/effect_wind.png";
                magicSpriteData = new SpriteData (192, 192, 10, 1);
                break;
            default:
                appendMessage("しかし、何も起こらなかった！");
                setActionButtonsDisabled(false);
                return;
        }
        displayEffect(effectPath, magicSpriteData, targetEnemy, afterEffectActions);
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
        int playerLevelBefore = player.getLevel();

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
                appendMessage("\n*** ドラゴンを打ち倒し、世界に平和が訪れた！ ***");
                appendMessage("ゲームクリア！おめでとう！");
                player.addGold(totalGold);
                updatePlayerStatsUI();
                updateUIForState(GameState.GAMEOVER);

                if (bgmPlayer != null) {
                    bgmPlayer.stop();
                }
                playBGM("bgm_game_clear.mp3");

                attackButton.setVisible(false);
                magicButton.setVisible(false);
                potionButton.setVisible(false);
                escapeButton.setVisible(false);
                multiAttackButton.setVisible(false);
                magicButtonsBox.setVisible(false);
            } else {
                appendMessage("\n敵をすべてたおした！");
                player.gainExp(totalExp);
                player.addGold(totalGold);

                gainedExp = totalExp;
                gainedGold = totalGold;
                leveledUp = (player.getLevel() > playerLevelBefore);

                updateUIForState(GameState.BATTLE_RESULT);
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
        setActionButtonsDisabled(false);
    }

    private void updatePlayerStatsUI() {
        playerNameLabel.setText("名前: " + player.getName());
        playerHpLabel.setText("HP: " + player.getHp() + "/" + player.getMaxHp());
        playerMpLabel.setText("MP: " + player.getMp() + "/" + player.getMaxMp());
        playerLevelExpLabel.setText("Lv: " + player.getLevel() + " Exp: " + player.getExp() + "/" + player.getExpToLevelUp());
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
            case FIRE:
                return "火";
            case ICE:
                return "氷";
            case THUNDER:
                return "雷";
            case WIND:
                return "風";
            default:
                return "？";
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
        weaponShopScrollPane.setVisible(false);

        titlePane.setVisible(false);
        gamePane.setVisible(false);
        mapAndPlayerPane.setVisible(false);
        enemyImageDisplayBox.setVisible(false);
        resultScrollPane.setVisible(false);

        if (currentState == GameState.TITLE) {
            titlePane.setVisible(true);
            playBGM("bgm_title.mp3");
        } else if (currentState == GameState.EXPLORING) {
            gamePane.setVisible(true);
            mapAndPlayerPane.setVisible(true);
            updateEnemyStatsUI();
            appendMessage("\n現在地: (" + playerX + ", " + playerY + ")\n矢印キーで移動してください。");
            if (primaryStage.getScene() != null) {
                primaryStage.getScene().getRoot().requestFocus();
            }
            playBGM("bgm_field.mp3");
        } else if (currentState == GameState.BATTLE) {
            gamePane.setVisible(true);
            enemyImageDisplayBox.setVisible(true);
            attackButton.setVisible(true);
            magicButton.setVisible(true);
            potionButton.setVisible(true);
            escapeButton.setVisible(true);
            if (player.getEquippedWeapon().getName().equals("薙刀")) {
                multiAttackButton.setVisible(true);
            }
            updateEnemyStatsUI();
            if (currentEnemies != null && currentEnemies.length == 1 && currentEnemies[0].getName().equals("ドラゴン")) {
                playBGM ("bgm_boss_battle.mp3");
            } else {
                playBGM("bgm_nomal_battle.mp3");
            }
        } else if (currentState == GameState.MAGIC_SELECT) {
            gamePane.setVisible(true);
            enemyImageDisplayBox.setVisible(true);
            magicButtonsBox.setVisible(true);
        } else if (currentState == GameState.BATTLE_RESULT) {
            gamePane.setVisible(true);
            resultScrollPane.setVisible(true);
            resultLabelExp.setText("経験値 " + gainedExp + " を獲得した！");
            resultLabelGold.setText("所持金が " + gainedGold + " GOLD増えた！");
            if (leveledUp) {
                resultLabelLevelUp.setText("レベルアップ！");
            } else {
                resultLabelLevelUp.setText("");
            }
            appendMessage("\n戦闘に勝利した！");

            if (bgmPlayer != null) {
                bgmPlayer.stop();
                currentBgm = "";
            }
            playSE("se_result.mp3");
        } else if (currentState == GameState.GAMEOVER) {
            gamePane.setVisible(true);
            playBGM("bgm_gameover.mp3");
        } else if (currentState == GameState.IN_TOWN) {
            gamePane.setVisible(true);
            mapAndPlayerPane.setVisible(true);
            townButtonsBox.setVisible(true);
            updateEnemyStatsUI();
            appendMessage("\n村に到着した。何をしますか？");
            playBGM("bgm_town.mp3");
        } else if (currentState == GameState.WEAPON_SHOP) {
            gamePane.setVisible(true);
            mapAndPlayerPane.setVisible(true);
            weaponShopScrollPane.setVisible(true);
            appendMessage("\n武器屋：いらっしゃい！");
        }
    }

    private void appendMessage(String message) {
        messageTextArea.appendText("\n" + message);
        messageTextArea.setScrollTop(Double.MAX_VALUE);
    }

    public Enemy generateRandomEnemy(MapTileType tileType) {
        int type;

        if (tileType == MapTileType.FOREST) {
            type = random.nextInt(2);
            switch (type) {
                case 0:
                    return new Enemy("キラービー", 12, 4, 8, Element.WIND, 7);
                case 1:
                    return new Enemy("ゾンビ", 18, 6, 15, Element.FIRE, 12);
                default:
                    return new Enemy("キラービー", 12, 4, 8, Element.WIND, 7);
            }
        } else if (tileType == MapTileType.MOUNTAIN_BROWN) {
            type = random.nextInt(2);
            switch (type) {
                case 0:
                    return new Enemy("ガルーダ", 15, 5, 10, Element.WIND, 10);
                case 1:
                    return new Enemy("亡霊剣士", 25, 8, 20, Element.ICE, 10);
                default:
                    return new Enemy("ガルーダ", 15, 5, 10, Element.WIND, 10);
            }
        } else {
            type = random.nextInt(3);
            switch (type) {
                case 0:
                    return new Enemy("スライム", 10, 3, 5, Element.FIRE, 5);
                case 1:
                    return new Enemy("ゴブリン", 15, 5, 10, Element.ICE, 10);
                case 2:
                    return new Enemy("オオカミ", 20, 6, 12, Element.THUNDER, 15);
                default:
                    return new Enemy("謎の敵", 12, 4, 8, Element.WIND, 8);
            }
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
                        case "キラービー":
                            imagePath = "/killer_bee.png";
                            break;
                        case "ゾンビ":
                            imagePath = "/zombie.png";
                            break;
                        case "ガルーダ":
                            imagePath = "/garuda.png";
                            break;
                        case "亡霊剣士":
                            imagePath = "/phantom_swordman.png";
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
                        view.setUserData(enemy);

                        view.setOnMouseClicked(event -> {
                            selectedTarget = (Enemy) view.getUserData();
                            updateTargetSelectionUI();
                            appendMessage(selectedTarget.getName() + " をターゲットに選択した！");
                        });

                        enemyImageDisplayBox.getChildren().add(view);
                    } catch (NullPointerException e) {
                        System.err.println("敵の画像ファイルが見つかりません: " + imagePath + ". エラー: " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("敵画像のロード中に予期せぬエラーが発生しました: " + e.getMessage());
                    }
                }
            }
            if (selectedTarget == null || !selectedTarget.isAlive()) {
                for (Enemy enemy : currentEnemies) {
                    if (enemy.isAlive()) {
                        selectedTarget = enemy;
                        break;
                    }
                }
            }
            updateTargetSelectionUI();
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

    private void handleContinue() {
        setActionButtonsDisabled(false);
        currentEnemies = null;
        updateEnemyStatsUI();
        updatePlayerStatsUI();
        appendMessage("冒険を続ける...");
        updateUIForState(GameState.EXPLORING);
    }

    private void playBGM(String bgmFileName) {
        if (bgmFileName != null && bgmFileName.equals(currentBgm) && bgmPlayer != null && bgmPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }

        if (bgmFileName == null || bgmFileName.isEmpty()) {
            currentBgm = "";
            return;
        }

        try {
            String musicFile = getClass().getResource("/" + bgmFileName).toExternalForm();
            Media media = new Media(musicFile);
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.play();
            currentBgm = bgmFileName;
        } catch (Exception e) {
            System.err.println("BGMの読み込みに失敗しました: " + bgmFileName + "エラー: " + e.getMessage());
            currentBgm = "";
        }
    }

    private void playSE(String seFileName) {
        String soundFile = getClass().getResource("/" + seFileName).toExternalForm();
        Media media = new Media(soundFile);
        sePlayer = new MediaPlayer(media);
        sePlayer.setCycleCount(1);
        sePlayer.play();
    }

    private void displayEffect(String effectImagePath, SpriteData spriteData, Enemy target, Runnable onFinished) {
        try {
            Image effectImage = new Image(getClass().getResourceAsStream(effectImagePath));
            effectImageView.setImage(effectImage);

            final int frameWidth = spriteData.getFrameWidth();
            final int frameHeight = spriteData.getFrameHeight();
            final int numFrames = spriteData.getNumFrames();
            final int columns = spriteData.getColumns();
            final Duration duration = Duration.millis(1000);

            final Animation animation = new SpriteAnimation(
                    effectImageView, duration, numFrames, columns, frameWidth, frameHeight
            );

            animation.setOnFinished(event -> {
                effectImageView.setVisible(false);
                if (onFinished != null) {
                    onFinished.run();
                }
            });

            ImageView targetView = null;
            if (target != null) {
                for (Node node : enemyImageDisplayBox.getChildren()) {
                    if (node.getUserData() == target) {
                        targetView = (ImageView) node;
                        break;
                    }
                }
            }
            if (targetView != null) {
                double hboxWidth = enemyImageDisplayBox.getWidth();
                double targetXInHBox = targetView.getLayoutX();
                double targetWidth = targetView.getBoundsInParent().getWidth();
                double newTranslateX = targetXInHBox - hboxWidth / 2 + targetWidth / 2;
                effectImageView.setTranslateX(newTranslateX);
                effectImageView.setTranslateY(0);
            } else {
                effectImageView.setTranslateX(0);
                effectImageView.setTranslateY(0);
            }

            effectImageView.setVisible(true);
            animation.play();
        } catch (Exception e) {
            System.err.println("エフェクト画像の読み込みに失敗しました。:" + e.getMessage());
            if (onFinished != null) {
                onFinished.run();
            }
        }
    }

    private void displayMultiTargetEffect(String effectImagePath, SpriteData spriteData, Enemy[] targets, Runnable onFinished) {
        mainContentPane.getChildren().removeAll(multiEffectViews);
        multiEffectViews.clear();

        Image effectImage;
        try {
            effectImage = new Image(getClass().getResourceAsStream(effectImagePath));
        } catch (Exception e) {
            System.err.println("エフェクトの読込に失敗しました: " + e.getMessage());
            if (onFinished != null) onFinished.run();
            return;
        }

        List<Animation> animations = new ArrayList<>();

        for (Enemy target : targets) {
            ImageView targetView = null;
            for (Node node : enemyImageDisplayBox.getChildren()) {
                if (node.getUserData() == target) {
                    targetView = (ImageView) node;
                    break;
                }
            }
            if (targetView == null) continue;

            ImageView newEffectView = new ImageView(effectImage);
            newEffectView.setFitWidth(100);
            newEffectView.setFitHeight(100);
            newEffectView.setPreserveRatio(true);

            double hboxWidth = enemyImageDisplayBox.getWidth();
            double targetXInHBox = targetView.getLayoutX();
            double targetWidth = targetView.getBoundsInParent().getWidth();
            double newTranslateX = targetXInHBox - hboxWidth / 2 + targetWidth / 2;
            newEffectView.setTranslateX(newTranslateX);
            newEffectView.setTranslateY(0);

            multiEffectViews.add(newEffectView);
            animations.add(new SpriteAnimation(
                    newEffectView,
                    Duration.millis(1000),
                    spriteData.getNumFrames(),
                    spriteData.getColumns(),
                    spriteData.getFrameWidth(),
                    spriteData.getFrameHeight()
            ));
        }

        if (animations.isEmpty()) {
            if (onFinished != null) onFinished.run();
            return;
        }

        mainContentPane.getChildren().addAll(multiEffectViews);

        javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition(animations.toArray(new Animation[0]));
        parallelTransition.setOnFinished(event -> {
            mainContentPane.getChildren().removeAll(multiEffectViews);
            multiEffectViews.clear();
            if (onFinished != null) {
                onFinished.run();
            }
        });
        parallelTransition.play();
    }

    private void updateTargetSelectionUI() {
        for (Node node : enemyImageDisplayBox.getChildren()) {
            if (node instanceof ImageView) {
                ImageView iv = (ImageView) node;
                if (iv.getUserData() == selectedTarget) {
                    iv.setStyle("-fx-effect: dropshadow(three-pass-box, aqua, 10, 0.7, 0, 0)");
                } else {
                    iv.setStyle(null);
                }
            }
        }
    }

    private void setActionButtonsDisabled(boolean disabled) {
        attackButton.setDisable(disabled);
        magicButton.setDisable(disabled);
        potionButton.setDisable(disabled);
        escapeButton.setDisable(disabled);
        multiAttackButton.setDisable(disabled);

        fireMagicButton.setDisable(disabled);
        iceMagicButton.setDisable(disabled);
        thunderMagicButton.setDisable(disabled);
        windMagicButton.setDisable(disabled);
    }

    public static void main(String[] args) {
        launch(args);
    }
}