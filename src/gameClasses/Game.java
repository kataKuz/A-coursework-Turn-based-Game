package gameClasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Главный класс игры, реализует графический интерфейс и игровую логику.
 * Реализует интерфейс GameI и сериализуем для сохранения/загрузки игры.
 */
public class Game extends JFrame implements GameI, Serializable {
    /**
     * Логгер для записи сообщений в лог-файл.
     */
    private static final Logger logger = LogManager.getLogger(Game.class);
    /**Игроки*/
    private Player player1, player2;
    /**Игровая карта*/
    private GameMap gameMap;
    /**Текущий день игры*/
    private int gameDay;
    /**Размер карты*/
    int mapSize;
    /**Окно меню*/
    private JFrame menuFrame;
    /**Кнопки карты*/
    private JButton[][] mapButtons;
    /**Изображения для клеток*/
    private Map<String, BufferedImage> tileImages = new HashMap<>();
    /**Флаг, указывающий на выбранную клетку*/
    private boolean isTileSelected = false;
    /**Кнопки действий*/
    private JButton[] actionButtons;
    /**Координаты выбранной клетки х*/
    private int selectedTileX;
    /**Координаты выбранной клетки у*/
    private int selectedTileY;
    /**Панель игрока 1*/
    private JPanel player1Panel;
    /**Панель игрока 2*/
    private JPanel player2Panel;
    /**Панель карты*/
    private JPanel mapPanel;
    /**Массив характеристик игрока 1*/
    private JLabel[] player1Labels;
    /**Массив характеристик игрока 2*/
    private JLabel[] player2Labels;
    /**Консольное окно-панель*/
    private JTextArea consoleArea;

    /**
     * Конструктор класса Game.
     * Инициализирует параметры игры, создает карту и игроков.
     * @param mapSize Размер игровой карты.
     */
    public Game(int mapSize) {
        this.mapSize = mapSize;
        gameDay = 0;
        gameMap = new GameMap(mapSize);
        player1 = new Player(gameMap.getWidth() - 1, gameMap.getHeight() - 1, 20, 10, 15, gameMap);
        player2 = new Player(0, 0, 20, 10, 15, gameMap);
        logger.info("Game initialized with map size: " + mapSize);
    }

    /**
     * Отображает главное меню игры.
     * Создает окно меню с кнопками "Начать новую игру", "Загрузить игру" и "Выход".
     * Обрабатывает события нажатия кнопок, запускает новую игру, загрузку игры или завершает работу приложения.
     */
    public void menu(){
        logger.debug("Entering game menu.");
        try {
            menuFrame = new JFrame();
            menuFrame.setTitle("Rice Game - Меню");
            menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menuFrame.setSize(300, 200);
            menuFrame.setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton startNewGame = new JButton("Начать новую игру");
            JButton loadGame = new JButton("Загрузить игру");
            JButton exitGame = new JButton("Выход");

            buttonPanel.add(Box.createVerticalGlue());
            buttonPanel.add(Box.createVerticalStrut(20));
            buttonPanel.add(startNewGame);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(loadGame);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(exitGame);
            buttonPanel.add(Box.createVerticalStrut(20));
            buttonPanel.add(Box.createVerticalGlue());

            startNewGame.addActionListener(e -> {
                menuFrame.dispose();
                interfaceBuilder(10);
            });
            loadGame.addActionListener(e -> {
                try {
                    loadGame("file");
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                menuFrame.dispose();
                interfaceBuilder(10);
            });
            exitGame.addActionListener(e -> System.exit(0));

            mainPanel.add(buttonPanel);

            menuFrame.add(mainPanel, BorderLayout.CENTER);

            menuFrame.setVisible(true);
            logger.info("Game logic executed successfully.");
        } catch (Exception e) {
            logger.error("Error in game logic:", e);
        }
    }

    /**
     * Создает и отображает игровой интерфейс.
     * Инициализирует игровое окно и его элементы: карту, панели игроков, панель действий и панель событий.
     * Добавляет обработчик закрытия окна для сохранения игры.
     * @param mapSize Размер игровой карты.
     */
    public void interfaceBuilder(int mapSize) {
        logger.debug("Entering game interface.");
        try {
            JFrame gameFrame = new JFrame();
            gameFrame.setTitle("Rice Game - Игра");
            gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            gameFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int result = JOptionPane.showConfirmDialog(gameFrame,
                            "Сохранить игру перед выходом?", "Сохранение", JOptionPane.YES_NO_CANCEL_OPTION);

                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            try {
                                saveGame("file");
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            System.exit(0);
                            break;
                        case JOptionPane.NO_OPTION:
                            System.exit(0);
                            break;
                        case JOptionPane.CANCEL_OPTION:
                            break;
                    }
                }
            });
            gameFrame.setSize(725, 680);
            gameFrame.setLayout(new GridBagLayout());
            gameFrame.setLocationRelativeTo(null);
            logger.debug("Game frame created.");

            mapPanel = new JPanel(new GridLayout(mapSize, mapSize));
            mapButtons = new JButton[mapSize][mapSize];
            loadTileImages();

            for (int i = 0; i < mapSize; i++) {
                for (int j = 0; j < mapSize; j++) {
                    mapButtons[i][j] = createTileButton(i, j, player1);
                    mapPanel.add(mapButtons[i][j]);
                }
            }
            mapPanel.setBorder(BorderFactory.createTitledBorder("Карта"));
            logger.debug("Map panel created and populated with buttons.");

            player1Panel = new JPanel();
            player2Panel = new JPanel();

            player1Panel.setBorder(BorderFactory.createTitledBorder("Игрок 1"));
            player1Panel.setLayout(new BoxLayout(player1Panel, BoxLayout.Y_AXIS));

            player2Panel.setBorder(BorderFactory.createTitledBorder("Игрок 2"));
            player2Panel.setLayout(new BoxLayout(player2Panel, BoxLayout.Y_AXIS));

            player1Labels = new JLabel[5];
            player2Labels = new JLabel[5];

            player1Labels = addPlayerCharacteristics(player1Panel, player1);
            player2Labels = addPlayerCharacteristics(player2Panel, player2);

            logger.debug("Player panels created.");
            SwingUtilities.invokeLater(() -> updatePlayerCharacteristics());

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(mapPanel, BorderLayout.EAST); // карта в центре
            JPanel playerPanels = new JPanel(new GridLayout(1, 2));
            playerPanels.add(player1Panel);
            playerPanels.add(player2Panel);
            topPanel.add(playerPanels, BorderLayout.WEST);
            logger.debug("Top panel constructed.");

            JPanel actionPanel = new JPanel();
            actionPanel.setBorder(BorderFactory.createTitledBorder("Действия"));

            JPanel eventPanel = new JPanel();
            actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            eventPanel.setBorder(BorderFactory.createTitledBorder("События"));
            eventPanel.setLayout(new BorderLayout());

            consoleArea = new JTextArea(2, 30);
            consoleArea.setEditable(false);
            consoleArea.setLineWrap(true);
            consoleArea.setWrapStyleWord(true);
            consoleArea.setBackground(Color.WHITE);
            consoleArea.setForeground(Color.BLUE);

            JScrollPane scrollPane = new JScrollPane(consoleArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            eventPanel.add(scrollPane);

            createActionButtons(actionPanel);
            JButton rulesButton = new JButton("Правила");
            rulesButton.addActionListener(e -> showRules());
            actionPanel.add(rulesButton);

            logger.debug("Action and event panels created.");

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

            bottomPanel.add(actionPanel);
            bottomPanel.add(Box.createVerticalStrut(3));
            bottomPanel.add(eventPanel);

            gameFrame.getContentPane().setLayout(new BorderLayout());
            gameFrame.getContentPane().add(topPanel, BorderLayout.NORTH);
            gameFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

            gameFrame.setVisible(true);
            showRules();
            logger.info("Game interface built successfully.");
        } catch (Exception e) {
            logger.error("Error in game logic:", e);
        }
    }

    /**
     * Создает кнопку для клетки на игровой карте.
     * Устанавливает иконку в зависимости от состояния клетки и добавляет обработчик событий.
     * @param i Координата X клетки.
     * @param j Координата Y клетки.
     * @param player Игрок, для которого отображается состояние клетки.
     * @return Кнопка, представляющая клетку на карте.
     */
    public JButton createTileButton(int i, int j, Player player) {
        logger.debug("Creating tile button at coordinates ("+ i + ", " + j + "), player: " + player);

        JButton button = new JButton();
        button.setPreferredSize(new Dimension(50, 50));
        String state = gameMap.getStateString(i, j, player);

        try {
            button.setIcon(new ImageIcon(tileImages.get(state)));
            logger.debug("Tile button icon set to: " + state);
        } catch (NullPointerException e) {
            logger.error("Error setting icon for tile button at (" + i + ", " + j + ") state " + state);
            button.setText("Error loading image");
        }

        button.addActionListener(e -> handleTileClick(i, j, state));

        if (state =="EMPTY") {
            JLabel unitLabel = new JLabel(String.valueOf(gameMap.getTile(i, j).getRequiredUnits()));
            unitLabel.setHorizontalAlignment(SwingConstants.CENTER);
            unitLabel.setVerticalAlignment(SwingConstants.BOTTOM);
            unitLabel.setForeground(Color.BLACK);
            button.add(unitLabel, BorderLayout.SOUTH);
            logger.debug("Added unit requirement label to EMPTY tile button at (" + i + ", "+ j + "). Required units: " + gameMap.getTile(i, j).getRequiredUnits());
        }

        return button;
    }


    /**
     * Загружает изображения для клеток игровой карты из ресурсов приложения.
     * Изображения хранятся в папке "/images/" и имеют соответствующие названия.
     * Обрабатывает исключения IOException при ошибке загрузки изображений.
     */
    public void loadTileImages() {
        logger.info("Loading tile images...");
            try {
                tileImages.put("EMPTY", ImageIO.read(getClass().getResource("/images/EMPTY.png")));
                tileImages.put("RICE1", ImageIO.read(getClass().getResource("/images/RICE1.png")));
                tileImages.put("RICE2", ImageIO.read(getClass().getResource("/images/RICE2.png")));
                tileImages.put("HOUSEWATER1", ImageIO.read(getClass().getResource("/images/HOUSEWATER1.png")));
                tileImages.put("HOUSEWATER2", ImageIO.read(getClass().getResource("/images/HOUSEWATER2.png")));
                tileImages.put("HOUSE1", ImageIO.read(getClass().getResource("/images/HOUSED1.png")));
                tileImages.put("HOUSE2", ImageIO.read(getClass().getResource("/images/HOUSED2.png")));
                tileImages.put("RICEWATER1", ImageIO.read(getClass().getResource("/images/RICEWATERED1.png")));
                tileImages.put("RICEWATER2", ImageIO.read(getClass().getResource("/images/RICEWATERED2.png")));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Ошибка загрузки изображений");
            }
        logger.info("Tile image loading completed.");
    }


    /**
     * Добавляет характеристики игрока на указанную панель.
     * Создает массив JLabel с характеристиками игрока (территории, вода, рис, крестьяне, дома)
     * и добавляет их на панель.
     * @param panel Панель, на которую добавляются характеристики.
     * @param player Игрок, чьи характеристики отображаются.
     * @return Массив JLabel с характеристиками игрока.
     */
    public JLabel[] addPlayerCharacteristics(JPanel panel, Player player) {
        logger.debug("Adding player characteristics for player: " + player);

        JLabel[] labels = new JLabel[5];
        labels[0] = new JLabel("Территории: " + player.controlledTiles());
        labels[1] = new JLabel("Вода: " + player.getWater());
        labels[2] = new JLabel("Рис: " + player.getRice());
        labels[3] = new JLabel("Крестьяне: " + player.getUnits());
        labels[4] = new JLabel("Дома: " + player.getHouses());

        for (JLabel label : labels) {
            panel.add(label);
            logger.debug("Added characteristic label to panel");
        }

        return labels;
    }

    /**
     * Обрабатывает событие клика на клетку игровой карты.
     * Устанавливает флаг isTileSelected, запоминает координаты выбранной клетки и активирует кнопки действий.
     * @param i Координата X клетки.
     * @param j Координата Y клетки.
     * @param state Состояние клетки.
     */
    public void handleTileClick(int i, int j, String state) {
        logger.info("Tile clicked at coordinates (" + i + ", " + j + "state" + state);

        isTileSelected = true;
        selectedTileX = i;
        selectedTileY = j;

        for (JButton button : actionButtons) {
            button.setEnabled(true);
        }
        logger.debug("Enabled " + actionButtons.length + "action buttons.");
    }

    /**
     * Обрабатывает нажатие кнопки действия.
     * Выполняет действие в зависимости от нажатой кнопки (сбор воды, захват территории, полив риса, строительство дома).
     * Обновляет характеристики игроков и карту после выполнения действия.
     * @param e Событие нажатия кнопки.
     */
    public void handleActionClick(ActionEvent e) {
        if (isTileSelected) {
            logger.info("Action button clicked");

            isTileSelected = false;
            for (JButton button : actionButtons) {
                button.setEnabled(false);
            }
            logger.debug("Disabled action buttons.");

            int x = selectedTileX;
            int y = selectedTileY;

            if (e.getSource() == actionButtons[0]) {
                player1.collectWater(15);
                logger.info("Player 1 collected water.");
                printToConsole("Вы набрали 15 единиц воды");
                playerTurn(player2);
                endOfDay();
            } else if (e.getSource() == actionButtons[1]) {
                String result = player1.claimTerritory(x, y, gameMap);
                logger.info("Player 1 claimed territory at (" + x + ", " + y + "), Result: " + result);
                printToConsole(result);
                playerTurn(player2);
                endOfDay();
            } else if (e.getSource() == actionButtons[2]) {
                String result = player1.waterRice(x, y, gameMap);
                logger.info("Player 1 watered rice at (" + x + ", " + y + "), Result: {}" + result);
                printToConsole(result);
                playerTurn(player2);
                endOfDay();
            } else if (e.getSource() == actionButtons[3]) {
                String result = player1.buildHouse(x, y, gameMap);
                logger.info("Player 1 built house at (" + x + ", " + y + "), Result: " + result);
                printToConsole(result);
                playerTurn(player2);
                endOfDay();
            }

            SwingUtilities.invokeLater(() -> {
                updatePlayerCharacteristics();
                updateMap();
                logger.debug("Player characteristics and map updated.");
            });
        }

        SwingUtilities.invokeLater(() -> {
            updatePlayerCharacteristics();
            updateMap();
        });
    }

    /**
     * Создает кнопки действий игрока и добавляет их на указанную панель.
     * Кнопки изначально отключены и активируются при выборе клетки.
     * @param panel Панель, на которую добавляются кнопки действий.
     */
    public void createActionButtons(JPanel panel) {
        logger.info("Creating action buttons...");

        JButton claimTerritoryButton = new JButton("Освоить территорию");
        JButton buildHouseButton = new JButton("Построить дом");
        JButton waterRiceButton = new JButton("Полить рис");
        JButton collectWaterButton = new JButton("Набрать воду");

        collectWaterButton.addActionListener(e -> handleActionClick(e));
        claimTerritoryButton.addActionListener(e -> handleActionClick(e));
        waterRiceButton.addActionListener(e -> handleActionClick(e));
        buildHouseButton.addActionListener(e -> handleActionClick(e));

        claimTerritoryButton.setEnabled(false);
        buildHouseButton.setEnabled(false);
        waterRiceButton.setEnabled(false);
        collectWaterButton.setEnabled(false);

        actionButtons = new JButton[4];
        actionButtons[0] = collectWaterButton;
        actionButtons[1] = claimTerritoryButton;
        actionButtons[2] = waterRiceButton;
        actionButtons[3] = buildHouseButton;

        panel.add(collectWaterButton);
        panel.add(claimTerritoryButton);
        panel.add(waterRiceButton);
        panel.add(buildHouseButton);

        logger.info("Action buttons created and added to panel. Initially disabled.");
        logger.debug("Action buttons: Collect Water, Claim Territory, Water Rice, Build House");
    }

    /**
     * Обновляет отображение игровой карты в соответствии с текущим состоянием клетки.
     * Перерисовывает все клетки карты, обновляя иконки.
     * Обрабатывает возможные ошибки при обновлении клетки.
     */
    public void updateMap() {
        logger.info("Updating game map...");

        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                String state = gameMap.getStateString(i, j, player1);
                BufferedImage img = tileImages.get(state);

                try {
                    if (img != null) {
                        ImageIcon icon = new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                        mapButtons[i][j].setIcon(icon);
                        logger.debug("Updated tile icon at (" + i + ", " + j + ") to state: " + state);
                    } else {
                        logger.warn("Image not found for state '" + state + "' at (" + i + ", " + j + ").");
                    }

                    if (state.equals("EMPTY")) {
                        JLabel unitLabel = new JLabel(String.valueOf(gameMap.getTile(i, j).getRequiredUnits()));
                        unitLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        unitLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                        unitLabel.setForeground(Color.BLACK);
                        mapButtons[i][j].add(unitLabel, BorderLayout.SOUTH);
                        logger.debug("Added unit label to EMPTY tile at (" + i + ", " + j + ") Required units: " + gameMap.getTile(i, j).getRequiredUnits());
                    }
                } catch (Exception e) {
                    logger.error("Error updating tile at (" + i + ", " + j + ")");
                }
            }
        }

        mapPanel.revalidate();
        mapPanel.repaint();
        logger.info("Game map updated successfully.");
    }

    /**
     * Обновляет отображение характеристик игроков на панели.
     * Перерисовывает значения территорий, воды, риса, крестьян и домов для обоих игроков.
     */
    public void updatePlayerCharacteristics() {
        logger.info("Updating player characteristics...");

        player1Labels[0].setText("Территории: " + player1.controlledTiles());
        player1Labels[1].setText("Вода: " + player1.getWater());
        player1Labels[2].setText("Рис: " + player1.getRice());
        player1Labels[3].setText("Крестьяне: " + player1.getUnits());
        player1Labels[4].setText("Дома: " + player1.getHouses());
        logger.debug("Player 1 characteristics updated: Territories=" + player1.controlledTiles() + ", Water=" + player1.getWater() + ", Rice=" + player1.getRice() + ", Units=" + player1.getUnits() + ", Houses=" + player1.getHouses());

        player2Labels[0].setText("Территории: " + player2.controlledTiles());
        player2Labels[1].setText("Вода: " + player2.getWater());
        player2Labels[2].setText("Рис: " + player2.getRice());
        player2Labels[3].setText("Крестьяне: " + player2.getUnits());
        player2Labels[4].setText("Дома: " + player2.getHouses());
        logger.debug("Player 2 characteristics updated: Territories=" + player2.controlledTiles() + ", Water=" + player2.getWater() + ", Rice=" + player2.getRice() + ", Units=" + player2.getUnits() + ", Houses=" + player2.getHouses());

        player1Panel.revalidate();
        player1Panel.repaint();
        player2Panel.revalidate();
        player2Panel.repaint();

        logger.info("Player characteristics updated successfully.");
    }

    /**
     * Выводит сообщение в консольное окно игры - окно событий.
     * Использует SwingUtilities.invokeLater для безопасного обновления UI из другого потока.
     * @param message Сообщение для вывода.
     */
    public void printToConsole(String message) {
        SwingUtilities.invokeLater(() -> {
            consoleArea.append(message + "\n");
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        });
    }

    /**
     * Отображает диалоговое окно с правилами игры.
     * Создает диалоговое окно, содержащее текст с правилами игры, и делает его видимым.
     */
    public void showRules() {
        JDialog rulesDialog = new JDialog(this, "Правила игры", true);
        rulesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextArea rulesText = new JTextArea(
                "Цель игры - захватить 50% или более игрового поля быстрее соперника ИИ.\n" +
                "\nВаши клетки белые, и начинаете вы в правом нижнем углу. Чтобы захватить клетку поля, требуется определённое количество крестьян, указанное в клетке. Стольких людей вы потеряете в погоне за территориями.\n"+
                        "\nВ каждой захваченной клетке посажен рис. Он растёт на одну единицу в день. Если его полить, он будет расти на две единицы в день. В конце дня вы получаете рис с каждой клетки.\n" +
                        "\nВ день каждый крестьянин съедает по два риса. Они могут и не есть рис, если его нет. Однако нет риса - нет и новых крестьян.\n" +
                        "\nНовые крестьяне появляются из домов. Чтобы такой построить, вам потребуется 25 единиц риса, 10 единиц воды и 1 крестьянин. В день из каждого дома появляется один крестьянин.\n"+
                        "\nВы также можете получить больше воды. За раз наливается 15 единиц воды.\n"+
                        "\nВ день можно сделать только одно действие: налить воды, освоить территорию, полить рис или построить дом. Выберите клетку для действия и выберите действие. Результат появиться в окне События. Там же будет показан результат действий вашего противника.\n"+
                        "\nПосле нажатия на закрытие игрового окна, вам будет предложено сохранить игру. Загрузить сохранённую игру можно будет из меню при повторном открытии окна. По окончанию игры вы также сможете посмотреть графики изменения ваших ресурсов и противника\n"+
                        "\nУдачи!"
        );
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(rulesText);
        rulesDialog.add(scrollPane);

        rulesDialog.setSize(400, 300);
        rulesDialog.setLocationRelativeTo(this);
        rulesDialog.setVisible(true);
    }

    /**
     * Отображает диалоговое окно с результатом игры.
     * Показывает сообщение о победе или поражении и кнопки "Закрыть игру" и "Показать графики".
     * @param playerWon Флаг, указывающий на победу игрока (true) или поражение (false).
     */
    public void showEndGameWindow(boolean playerWon) {
        logger.info("Showing end game window. Player won: " + playerWon);

        JDialog endGameDialog = new JDialog(this, "Результат игры", true);
        endGameDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel resultLabel = new JLabel(playerWon ? "Поздравляем! Вы победили!" : "Вы проиграли!");
        resultLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton closeButton = new JButton("Закрыть игру");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("End game window closed. Exiting application.");
                endGameDialog.dispose();
                System.exit(0);
            }
        });

        JButton graphButton = new JButton("Показать графики");
        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Show graphs button clicked.");
                showGraphs();
            }
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        buttonPanel.add(graphButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(resultLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        endGameDialog.add(mainPanel);
        endGameDialog.pack();
        endGameDialog.setLocationRelativeTo(this);
        endGameDialog.setVisible(true);
    }

    /**
     * Выполняет ход игрока (или AI).
     * Если это ход AI, то AI принимает решение о действии (захват территории, другие действия или сбор воды) в зависимости от ресурсов и ситуации на карте.
     * Результат хода выводится в консольное окно событий.
     * @param player Игрок, чей ход выполняется.
     */
    public void playerTurn(Player player) {
        if (player == player2) {
            logger.info("AI player's turn started.");

            if ((player2.getUnits() >= player2.controlledTiles()) || (player2.getRice() == 0 && player2.getHouses() != 0)) {
                String result = player2.aiPlayerClaimTerritory(player2, gameMap.getWidth() * gameMap.getHeight() / 2, gameMap);
                logger.info("AI player claimed territory. Result: " + result);
                printToConsole(result);
            } else {
                if ((player2.getWater() >= 15)) {
                    String result = player2.aiPlayerOtherOptions(player2, gameMap);
                    logger.info("AI player performed other action. Result: " + result);
                    printToConsole(result);
                } else {
                    player2.collectWater(15);
                    logger.info("AI player collected water (no other actions possible).");
                    printToConsole("ИИ набрал воду, больше ему делать нечего :/");
                }
            }
            logger.info("AI player's turn ended.");
        }
    }

    /**
     * Завершает игровой день.
     * Увеличивает количество риса на карте, собирается рис, крестьяне потребляют его, дома производят новых крестьян.
     * Проверяет условие окончания игры и отображает окно с результатом, если игра закончена.
     */
    public void endOfDay() {
        logger.info("End of day "+ gameDay + " started.");

        gameMap.growRice(player1);
        gameMap.growRice(player2);
        logger.debug("Rice grown for both players.");

        player1.collectRice(gameMap);
        player2.collectRice(gameMap);
        logger.debug("Rice collected by both players.");

        player1.eatRice(player1.getUnits());
        player2.eatRice(player2.getUnits());
        logger.debug("Rice consumed by both players.");

        int newUnitsPlayer1 = player1.getHouses();
        int newUnitsPlayer2 = player2.getHouses();
        if (player1.getRice() != 0) {
            newUnitsPlayer1 = player1.getHouses();
            player1.setUnits(player1.getUnits() + newUnitsPlayer1);
        }
        if (player2.getRice() != 0) {
            newUnitsPlayer2 = player2.getHouses();
            player2.setUnits(player2.getUnits() + newUnitsPlayer2);
        }
        logger.debug("New units added: Player 1=" + newUnitsPlayer1 + ", Player 2=" + newUnitsPlayer2);


        player1.saveResources();
        player2.saveResources();
        logger.debug("Player resources saved.");

        gameDay++;

        boolean player1Won = false;
        if (isGameOver()) {
            if (player1.getUnits() == 0 || player2.getUnits() == 0) {
                player1Won = player1.getUnits() > player2.getUnits();
            } else {
                player1Won = player1.controlledTiles() > player2.controlledTiles();
            }
            logger.info("Game over! Player 1 won: " + player1Won);
            showEndGameWindow(player1Won);
        }

        logger.info("End of day " + gameDay + " completed.");
    }

    /**
     * Проверяет, завершена ли игра.
     * Игра заканчивается, если у одного из игроков закончились крестьяне и дома, или если один из игроков захватил 50% и более клеток.
     * @return True, если игра завершена, false - в противном случае.
     */
    public boolean isGameOver() {
        if ((player1.getUnits() == 0 && player1.getHouses() == 0) || (player2.getUnits() == 0 && player2.getHouses() == 0)) {
            return true;
        } else {
            int totalTiles = gameMap.getWidth() * gameMap.getHeight();
            return player1.controlledTiles() >= totalTiles / 2 || player2.controlledTiles() >= totalTiles / 2;
        }
    }

    /**
     * Сохраняет текущее состояние игры в файл.
     * Сериализует объекты Player, GameMap и gameDay в файл.
     * @param filename Имя файла для сохранения.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    public void saveGame(String filename) throws IOException {
        logger.info("Saving game to file: " + filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(player1);
            oos.writeObject(player2);
            oos.writeObject(gameMap);
            oos.writeInt(gameDay);
            logger.info("Game saved successfully to file: " + filename);
        } catch (IOException e) {
            logger.error("Error saving game to file " + filename + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Загружает состояние игры из файла.
     * Десериализует объекты Player, GameMap и gameDay из файла.
     * @param filename Имя файла для загрузки.
     * @throws IOException Если возникает ошибка ввода-вывода.
     * @throws ClassNotFoundException Если класс объекта не найден.
     */
    public void loadGame(String filename) throws IOException, ClassNotFoundException {
        logger.info("Loading game from file: " + filename);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            player1 = (Player) ois.readObject();
            player2 = (Player) ois.readObject();
            gameMap = (GameMap) ois.readObject();
            gameDay = ois.readInt();
            logger.info("Game loaded successfully from file: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error loading game from file " + filename + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Отображает диалоговое окно с графиками изменения ресурсов игроков за время игры.
     * Использует библиотеку JFreeChart для построения графиков.
     */
    public void showGraphs() {
        logger.info("Generating and showing resource graphs...");

        Map<String, double[]> player1Resources = processPlayerResources(player1);
        Map<String, double[]> player2Resources = processPlayerResources(player2);

        XYSeriesCollection dataset = new XYSeriesCollection();

        addResourcesToDataset(dataset, player1Resources, "Игрок 1");
        addResourcesToDataset(dataset, player2Resources, "ИИ");

        logger.debug("Dataset created successfully.");

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Изменение ресурсов",
                "День",
                "Количество ресурсов",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        plot.setRenderer(renderer);
        xAxis.setAutoRange(true);
        yAxis.setAutoRange(true);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1000, 600));

        JDialog graphDialog = new JDialog(this, "Графики ресурсов", true);
        graphDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        graphDialog.add(chartPanel);
        graphDialog.pack();
        graphDialog.setLocationRelativeTo(null);
        graphDialog.setVisible(true);

        logger.info("Resource graphs shown successfully.");
    }

    /**
     * Обрабатывает данные о ресурсах игрока и формирует словарь для построения графиков.
     * Преобразует список ресурсов игрока в словарь, где ключи - названия ресурсов, а значения - массивы значений ресурсов по дням.
     * @param player Игрок, данные о ресурсах которого обрабатываются.
     * @return Словарь данных о ресурсах игрока.
     */
    public Map<String, double[]> processPlayerResources(Player player) {
        logger.debug("Processing resources for player: " + player);

        Map<String, double[]> resourcesData = new HashMap<>();
        List<Map<String, Double>> playerResources = player.getResources();
        int numDays = gameDay;
        String[] resourceNames = {"вода", "рис", "крестьяне", "дома"};

        for (String resourceName : resourceNames) {
            resourcesData.put(resourceName, new double[numDays]);
        }

        for (int day = 0; day < numDays; day++) {
            if (day < playerResources.size()) {
                Map<String, Double> dayResources = playerResources.get(day);
                for (String resourceName : resourceNames) {
                    Double amount = dayResources.get(resourceName);
                    if (amount != null) {
                        resourcesData.get(resourceName)[day] = amount;
                    } else {
                        logger.warn("Resource '" + resourceName + "' not found for player " + player + " on day " + day);
                    }
                }
            } else {
                logger.debug("No resource data found for player " + player + " on day " + day);
            }
        }
        logger.debug("Resource processing completed for player: " + player + ".  Returning " + numDays + " data points.");
        return resourcesData;
    }

    /**
     * Добавляет данные о ресурсах игрока в набор данных для построения графика.
     * Создает отдельный ряд данных для каждого ресурса игрока.
     * @param dataset Набор данных для построения графика.
     * @param resources Словарь данных о ресурсах игрока.
     * @param playerName Имя игрока.
     */
    public void addResourcesToDataset(XYSeriesCollection dataset, Map<String, double[]> resources, String playerName) {
        logger.debug("Adding resources to dataset for player: " + playerName);

        for (Map.Entry<String, double[]> entry : resources.entrySet()) {
            String resourceName = entry.getKey();
            double[] resourceValues = entry.getValue();
            XYSeries series = new XYSeries(playerName + ": " + resourceName);
            for (int i = 0; i < resourceValues.length; i++) {
                series.add(i + 1, resourceValues[i]);
            }
            dataset.addSeries(series);
            logger.debug("Added series '" + playerName + ": " + resourceName + "' to dataset.");
        }

        logger.debug("Added " + resources.size() + " series to dataset for player " + playerName);
    }
}