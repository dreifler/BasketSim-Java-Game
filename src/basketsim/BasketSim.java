package basketsim;

/**
 * Team Simulation CMSC 495
 */
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.text.*;

public class BasketSim extends JFrame implements Runnable {

    static final long serialVersionUID = 123L;

    JTextArea jta = new JTextArea();
    JComboBox<String> jcb;
    JTextField jtf;
    JFrame jf = new JFrame();
    JPanel jrun = new JPanel();
    JPanel jp = new JPanel();
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Player> availPlayers = new ArrayList<Player>();
    ArrayList<Team> teams = new ArrayList<Team>();
    ArrayList<Zone> zones = new ArrayList<Zone>();
    ArrayList<Scores> scores = new ArrayList<Scores>();
    JFileChooser jFileChooser1;
    JScrollPane jspTa;

    public BasketSim() throws FileNotFoundException {

        setTitle("Basketball Simulation");
        setVisible(true);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(jp, BorderLayout.NORTH);

        jrun.setLayout(new GridLayout(0, 4));
        jta.setFont(new Font("Monospaced", 0, 12));
        DefaultCaret caret = (DefaultCaret) jta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        jspTa = new JScrollPane(jta);
        jspTa.setViewportView(jta);
        jta.setEditable(false);

        add(jspTa, BorderLayout.CENTER);

        //Create and display buttons
        JButton jbread = new JButton("Read");
        JButton jbplay = new JButton("Play");
        JButton jbplyrs = new JButton("Players");
        JButton jbteams = new JButton("Teams");
        JButton jbstat = new JButton("Scores");

        jp.add(jbread);
        jp.add(jbplyrs);
        jp.add(jbteams);
        jp.add(jbstat);
        jp.add(jbplay);

        add(jp, BorderLayout.PAGE_START);

        validate();
        jbread.addActionListener(e -> readFile());
        jbplay.addActionListener(e -> playGame());
        jbplyrs.addActionListener(e -> playerMenu());
        jbteams.addActionListener(e -> teamMenu());
        jbstat.addActionListener(e -> displayScores());
    } // end no-parameter constructor

    public void readFile() {
        JFileChooser fileChooser = new JFileChooser(".");
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            jta.append("\nYou read from the file: " + fileChooser.getSelectedFile().getName());
            try {
                int teamCounter = 0, playerCounter = 0;
                Scanner scan = new Scanner(fileChooser.getSelectedFile()).skip("//");
                while (scan.hasNext()) {
                    String inline = scan.nextLine().trim();
                    if (inline.length() == 0) {
                        continue;
                    }
                    Scanner line = new Scanner(inline).useDelimiter("\\s*:\\s*"); // compress white space also, else nextInt fails
                    switch (inline.charAt(0)) {
                        case 't':
                        case 'T':
                            addTeam(line);
                            teamCounter++;
                            break;
                        case 'p':
                        case 'P':
                            addPlayer(line);
                            playerCounter++;
                            break;
                    } // end switch
                } // end while reading data file
                jta.append("\nImported " + teamCounter + " Teams and " + playerCounter + " Players.\n");
            } // end try
            catch (FileNotFoundException e) {
                jta.append("\nFile not found.\n");
            } // end catch
        }
        // open and read file

    } // end readFile

    public void addTeam(Scanner line) {
        Team team = new Team(line);
        teams.add(team);
    } // end method addParty

    public void addPlayer(Scanner line) {
        Player player = new Player(line);
        players.add(player);
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getName().equals(player.getTeamName())) {
                teams.get(i).addPlayer(player);
                break;
            }
        }
    } // end method addToParty

    public void playGame() {
        run();
    }

    @Override
    public void run() {
        int endPoints = 100;
        int partialTeamCount = 0;
        ArrayList<Team> teamList = new ArrayList<>();
        for (Team t : teams) {
            if (t.getPG() != null && t.getSG() != null && t.getSF() != null && t.getPF() != null && t.getC() != null) {
                teamList.add(t);
            } else {
                partialTeamCount++;
            }
        }

        if (teamList.size() < 2) {
            if (partialTeamCount++ >= 2) {
                jta.append("\nNot enough teams with full rosters to play");
            } else {
                jta.append("\nNot enough teams to play");
            }
            return;
        }

        JComboBox<Team> homeList = new JComboBox<>();
        JComboBox<Team> awayList = new JComboBox<>();
        for (Team t : teamList) {
            homeList.addItem(t);
            awayList.addItem(t);
        }

        JLabel labelHome = new JLabel("Home Team");
        JLabel labelAway = new JLabel("Away Team");
        JLabel teamMessage = new JLabel("Select teams to play");

        JButton startGameButtom = new JButton("Start Game");
        JButton exitButton = new JButton("Exit");

        GridLayout inputLayout = new GridLayout(0, 2);
        JPanel teamData = new JPanel(inputLayout);
        teamData.add(labelHome);
        teamData.add(homeList);
        teamData.add(labelAway);
        teamData.add(awayList);

        JPanel teamButtons = new JPanel();
        teamButtons.add(startGameButtom);
        teamButtons.add(exitButton);

        JPanel remainingPoints = new JPanel();
        remainingPoints.add(teamMessage);

        JFrame frameEditTeam = new JFrame();

        frameEditTeam.add(teamData, BorderLayout.PAGE_START);
        frameEditTeam.add(remainingPoints, BorderLayout.CENTER);
        frameEditTeam.add(teamButtons, BorderLayout.PAGE_END);

        frameEditTeam.setSize(350, 180);
        frameEditTeam.setVisible(true);
        frameEditTeam.setLocationRelativeTo(jrun);
        frameEditTeam.setTitle("Choose Teams");

        startGameButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent l) {
                if (homeList.getSelectedIndex() == awayList.getSelectedIndex()) {
                    teamMessage.setText("Please select two different teams");
                } else {
                    frameEditTeam.dispatchEvent(new WindowEvent(frameEditTeam, WindowEvent.WINDOW_CLOSING));
                    Game play = new Game(teamList.get(homeList.getSelectedIndex()), teamList.get(awayList.getSelectedIndex()));
                    String dec;
                    int delay = 500;
                    Player target;
                    boolean success;
                    jta.setText("");
                    jtaUpdate("\n------------------------------------------------------------\n\nNew game: " + play.homeTeam + " vs " + play.awayTeam + "\n");

                    while (play.homeTeam.points < endPoints && play.awayTeam.points < endPoints) {
                        dec = play.decision();
                        switch (dec) {
                            case "dribble":
                                success = play.dribble();
                                if (success == true) {
                                    jtaUpdate(play.withBall + " dribbles to the " + play.withBall.inZone + "\n");
                                    play.playerMovement();
                                } else {
                                    jtaUpdate(play.withBall.defBy + " dribbles in the " + play.withBall.defBy.inZone + "\n");
                                    jtaUpdate(play.withBall + " steals the ball from " + play.withBall.defBy + ". TURNOVER\n\n");
                                    if (!play.withBall.pos.equals("PG")) {
                                        jtaUpdate(play.withBall + " passes the ball to " + play.withBall.onTeam.PG + "\n");
                                        play.withBall = play.withBall.defBy.onTeam.PG;
                                    }
                                }
                                try {
                                    Thread.sleep(delay);
                                } catch (Exception e) {
                                }
                                break;
                            case "pass":
                                target = play.passChoice();
                                jtaUpdate(play.withBall + " passes the ball to " + target + "\n");
                                success = play.pass(target);

                                if (success == true) {
                                    play.playerMovement();
                                } else {
                                    jtaUpdate(play.withBall + " picks off the pass. TURNOVER\n\n");
                                    if (!play.withBall.pos.equals("PG")) {
                                        jtaUpdate(play.withBall + " passes the ball to " + play.withBall.onTeam.PG + "\n");
                                        play.withBall = play.withBall.defBy.onTeam.PG;
                                    }
                                }
                                try {
                                    Thread.sleep(delay);
                                } catch (Exception e) {
                                }
                                break;
                            case "shoot":
                                String offense = play.withBall.onTeam.name;
                                jtaUpdate(play.withBall + " shoots.\n");

                                success = play.shoot();
                                if (success == true) {
                                    if (play.withBall.inZone.num == 1 || play.withBall.inZone.num == 2) {
                                        jtaUpdate(play.withBall + " scores 2 points.\n\n");
                                    } else {
                                        jtaUpdate(play.withBall + " scores 3 points.\n\n");
                                    }
                                    play.withBall = play.withBall.defBy.onTeam.PG;
                                    jtaUpdate("\n");
                                    jtaUpdate(play.homeTeam + ": " + play.homeTeam.points + "\n");
                                    jtaUpdate(play.awayTeam + ": " + play.awayTeam.points + "\n");
                                    jtaUpdate("\n");
                                    play.resetZones();
                                    try {
                                        Thread.sleep(delay);
                                    } catch (Exception e) {
                                    }
                                } else {
                                    if (play.withBall.inZone.num == 1 || play.withBall.inZone.num == 2) {
                                        jtaUpdate(play.withBall + " misses shot.\n");
                                    } else {
                                        jtaUpdate(play.withBall + " misses 3 point shot.\n");
                                    }
                                    play.withBall = play.Rebound();
                                    jtaUpdate("Rebounded by " + play.withBall + "\n\n");
                                    if (offense.equals(play.withBall.onTeam.name)) {
                                        play.playerMovement();
                                    } else if (!play.withBall.pos.equals("PG")) {
                                        jtaUpdate(play.withBall + " passes the ball to " + play.withBall.onTeam.PG + "\n");
                                        play.withBall = play.withBall.onTeam.PG;
                                        play.resetZones();
                                    }
                                    try {
                                        Thread.sleep(delay);
                                    } catch (Exception e) {
                                    }
                                }
                                break;
                        }
                    }//end loop

                    //print out final statistics to gameplay screen
                    printStats(play);

                    Scores stat = new Scores(play.homeTeam, play.awayTeam, play.homeTeam.points, play.awayTeam.points);
                    scores.add(stat);
                }
            }
        }
        );

        exitButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        frameEditTeam.dispatchEvent(new WindowEvent(frameEditTeam, WindowEvent.WINDOW_CLOSING));
                    }
                }
        );
    }

    public void jtaUpdate(String s) {
        //jta.append(s);
        jta.insert(s, 0);
        jta.update(jta.getGraphics());
    }

    public void printStats(Game play) {
        jta.append(String.format("\n\n%45s --------------------------\n", "-------------------------- Final Score"));
        jta.append(String.format("\n%43s: %2d", play.homeTeam, play.homeTeam.points));
        jta.append(String.format("\n%43s: %2d", play.awayTeam, play.awayTeam.points));
        jta.append(String.format("\n\n%43s -----------------\n", "----------------- Box Score"));
        jta.append(String.format("\n%47s", play.homeTeam));
        jta.append(String.format("\n%25s | %3s | %3s | %3s | %3s | %3s | %3s | %3s", "Name", "POS", "REB", "FGM", "FGA", "3PM", "3PA", "PTS"));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.homeTeam.PG, play.homeTeam.PG.pos, play.homeTeam.PG.rebounds, play.homeTeam.PG.shotsMade, play.homeTeam.PG.shots, play.homeTeam.PG.threesMade, play.homeTeam.PG.threes, play.homeTeam.PG.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.homeTeam.SG, play.homeTeam.SG.pos, play.homeTeam.SG.rebounds, play.homeTeam.SG.shotsMade, play.homeTeam.SG.shots, play.homeTeam.SG.threesMade, play.homeTeam.SG.threes, play.homeTeam.SG.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.homeTeam.SF, play.homeTeam.SF.pos, play.homeTeam.SF.rebounds, play.homeTeam.SF.shotsMade, play.homeTeam.SF.shots, play.homeTeam.SF.threesMade, play.homeTeam.SF.threes, play.homeTeam.SF.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.homeTeam.PF, play.homeTeam.PF.pos, play.homeTeam.PF.rebounds, play.homeTeam.PF.shotsMade, play.homeTeam.PF.shots, play.homeTeam.PF.threesMade, play.homeTeam.PF.threes, play.homeTeam.PF.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.homeTeam.C, play.homeTeam.C.pos, play.homeTeam.C.rebounds, play.homeTeam.C.shotsMade, play.homeTeam.C.shots, play.homeTeam.C.threesMade, play.homeTeam.C.threes, play.homeTeam.C.points));

        jta.append(String.format("\n%47s", play.awayTeam));
        jta.append(String.format("\n%25s | %3s | %3s | %3s | %3s | %3s | %3s | %3s", "Name", "POS", "REB", "FGM", "FGA", "3PM", "3PA", "PTS"));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.awayTeam.PG, play.awayTeam.PG.pos, play.awayTeam.PG.rebounds, play.awayTeam.PG.shotsMade, play.awayTeam.PG.shots, play.awayTeam.PG.threesMade, play.awayTeam.PG.threes, play.awayTeam.PG.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.awayTeam.SG, play.awayTeam.SG.pos, play.awayTeam.SG.rebounds, play.awayTeam.SG.shotsMade, play.awayTeam.SG.shots, play.awayTeam.SG.threesMade, play.awayTeam.SG.threes, play.awayTeam.SG.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.awayTeam.SF, play.awayTeam.SF.pos, play.awayTeam.SF.rebounds, play.awayTeam.SF.shotsMade, play.awayTeam.SF.shots, play.awayTeam.SF.threesMade, play.awayTeam.SF.threes, play.awayTeam.SF.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d", play.awayTeam.PF, play.awayTeam.PF.pos, play.awayTeam.PF.rebounds, play.awayTeam.PF.shotsMade, play.awayTeam.PF.shots, play.awayTeam.PF.threesMade, play.awayTeam.PF.threes, play.awayTeam.PF.points));
        jta.append(String.format("\n%25s | %3s | %3d | %3d | %3d | %3d | %3d | %3d\n", play.awayTeam.C, play.awayTeam.C.pos, play.awayTeam.C.rebounds, play.awayTeam.C.shotsMade, play.awayTeam.C.shots, play.awayTeam.C.threesMade, play.awayTeam.C.threes, play.awayTeam.C.points));
    }

    public void playerMenu() {
        Object[] options = {"See All", "Create Player", "Delete Player", "Edit Player", "Cancel"};
        int pointsMin = 400, pointsMax = 500;

        int n = JOptionPane.showOptionDialog(jrun, "", "Player Options", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[4]);

        if (n == 0) {
            if (players.isEmpty()) {
                jta.append("\nNo players to display");
            } else {
                if (teams.size() > 0) {
                    for (int i = 0; i < teams.size(); i++) {
                        jta.append("\n" + teams.get(i) + teams.get(i).getTeamMembers());
                    }
                }
                if (availPlayers.size() > 0) {
                    jta.append("\nFree Agents");
                    for (int i = 0; i < availPlayers.size(); i++) {
                        jta.append("\n" + availPlayers.get(i).getName() + "," + availPlayers.get(i).getPosition());
                    }
                } else {
                    jta.append("\nNo Free Agents\n");
                }
            }
        } else if (n == 1) {

            JLabel labelFirstName = new JLabel("First Name");
            JLabel labelLastName = new JLabel("Last Name");
            JLabel labelHeight = new JLabel("Height (in) (60-99)");
            JLabel labelStr = new JLabel("Strength (1-99)");
            JLabel labelQuick = new JLabel("Quickness (1-99)");
            JLabel labelShoot = new JLabel("Shooting (1-99)");
            JLabel labelDef = new JLabel("Defense (1-99)");
            JLabel labelPass = new JLabel("Passing (1-99)");
            JLabel labelDribble = new JLabel("Dribble (1-99)");
            JLabel labelPointsRemaining = new JLabel("Points Remaing: " + String.valueOf(pointsMax));

            NumberFormat twoDigits = NumberFormat.getInstance();
            twoDigits.setMaximumIntegerDigits(2);
            twoDigits.setParseIntegerOnly(true);
            JTextField inputFirstName = new JTextField();
            JTextField inputLastName = new JTextField();
            JTextField inputHeight = new JFormattedTextField(twoDigits);
            inputHeight.setText("68");

            JTextField inputStr = new JFormattedTextField(twoDigits);
            inputStr.setText("1");
            JTextField inputQuick = new JFormattedTextField(twoDigits);
            inputQuick.setText("1");
            JTextField inputShoot = new JFormattedTextField(twoDigits);
            inputShoot.setText("1");
            JTextField inputDef = new JFormattedTextField(twoDigits);
            inputDef.setText("1");
            JTextField inputPass = new JFormattedTextField(twoDigits);
            inputPass.setText("1");
            JTextField inputDribble = new JFormattedTextField(twoDigits);
            inputDribble.setText("1");

            JButton createPlayerButton = new JButton("Create Player");
            JButton calculateButton = new JButton("Calculate Points");
            JButton exitButton = new JButton("Exit");

            GridLayout inputLayout = new GridLayout(0, 2);
            JPanel playerData = new JPanel(inputLayout);
            playerData.add(labelFirstName);
            playerData.add(inputFirstName);
            playerData.add(labelLastName);
            playerData.add(inputLastName);
            playerData.add(labelHeight);
            playerData.add(inputHeight);
            playerData.add(labelStr);
            playerData.add(inputStr);
            playerData.add(labelQuick);
            playerData.add(inputQuick);
            playerData.add(labelShoot);
            playerData.add(inputShoot);
            playerData.add(labelDef);
            playerData.add(inputDef);
            playerData.add(labelPass);
            playerData.add(inputPass);
            playerData.add(labelDribble);
            playerData.add(inputDribble);

            JPanel playerButtons = new JPanel();
            playerButtons.add(createPlayerButton);
            playerButtons.add(calculateButton);
            playerButtons.add(exitButton);

            JPanel remainingPoints = new JPanel();
            remainingPoints.add(labelPointsRemaining);

            JFrame frameAddPlayer = new JFrame();
            frameAddPlayer.add(playerData, BorderLayout.PAGE_START);
            frameAddPlayer.add(remainingPoints, BorderLayout.CENTER);
            frameAddPlayer.add(playerButtons, BorderLayout.PAGE_END);

            frameAddPlayer.setSize(350, 310);
            frameAddPlayer.setVisible(true);
            frameAddPlayer.setLocationRelativeTo(jrun);
            frameAddPlayer.setTitle("Create Player");

            inputFirstName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    inputFirstName.setText(inputFirstName.getText().trim().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
                }
            });

            inputLastName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    inputLastName.setText(inputLastName.getText().trim().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
                }
            });

            inputHeight.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputHeight.getText());
                    } catch (NumberFormatException f) {
                        inputHeight.setText("0");
                    }
                    if (Integer.parseInt(inputHeight.getText()) < 60) {
                        inputHeight.setText("60");
                    }
                }
            });
            inputStr.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputStr.getText());
                    } catch (NumberFormatException f) {
                        inputStr.setText("0");
                    }
                    if (Integer.parseInt(inputStr.getText()) < 1) {
                        inputStr.setText("1");
                    }
                }
            });
            inputQuick.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputQuick.getText());
                    } catch (NumberFormatException f) {
                        inputQuick.setText("0");
                    }
                    if (Integer.parseInt(inputQuick.getText()) < 1) {
                        inputQuick.setText("1");
                    }
                }
            });
            inputShoot.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputShoot.getText());
                    } catch (NumberFormatException f) {
                        inputShoot.setText("0");
                    }
                    if (Integer.parseInt(inputShoot.getText()) < 1) {
                        inputShoot.setText("1");
                    }
                }
            });
            inputDef.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputDef.getText());
                    } catch (NumberFormatException f) {
                        inputDef.setText("0");
                    }
                    if (Integer.parseInt(inputDef.getText()) < 1) {
                        inputDef.setText("1");
                    }
                }
            });
            inputPass.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputPass.getText());
                    } catch (NumberFormatException f) {
                        inputPass.setText("0");
                    }
                    if (Integer.parseInt(inputPass.getText()) < 1) {
                        inputPass.setText("1");
                    }
                }
            });
            inputDribble.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputDribble.getText());
                    } catch (NumberFormatException f) {
                        inputDribble.setText("0");
                    }
                    if (Integer.parseInt(inputDribble.getText()) < 1) {
                        inputDribble.setText("1");
                    }
                }
            });

            createPlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (inputFirstName.getText().isEmpty()) {
                        labelPointsRemaining.setText("Please enter a first name");
                        return;
                    } else if (inputLastName.getText().isEmpty()) {
                        labelPointsRemaining.setText("Please enter a last name");
                        return;
                    }
                    for (Player p : players) {
                        if (p.getFirstName().toUpperCase().equals(inputFirstName.getText().toUpperCase()) && p.getLastName().toUpperCase().equals(inputLastName.getText().toUpperCase())) {
                            labelPointsRemaining.setText("There is already a player with that name");
                            return;
                        }
                    }

                    int pointsUsed = Integer.parseInt(inputHeight.getText()) + Integer.parseInt(inputStr.getText()) + Integer.parseInt(inputQuick.getText()) + Integer.parseInt(inputShoot.getText()) + Integer.parseInt(inputDef.getText()) + Integer.parseInt(inputPass.getText()) + Integer.parseInt(inputDribble.getText());
                    if (pointsUsed > pointsMax) {
                        labelPointsRemaining.setText("Exceeded point limit! Points Used: " + String.valueOf(pointsUsed) + "/" + pointsMax);
                    } else if (pointsUsed < pointsMin) {
                        labelPointsRemaining.setText("Player needs at least " + pointsMin + " points. Points Used: " + String.valueOf(pointsUsed) + "/" + pointsMax);
                    } else {
                        int hgt = Integer.parseInt(inputHeight.getText());
                        int str = Integer.parseInt(inputStr.getText());
                        int qck = Integer.parseInt(inputQuick.getText());
                        int sht = Integer.parseInt(inputShoot.getText());
                        int def = Integer.parseInt(inputDef.getText());
                        int pas = Integer.parseInt(inputPass.getText());
                        int dri = Integer.parseInt(inputDribble.getText());

                        Player player = new Player(inputFirstName.getText(), inputLastName.getText(), hgt, str, qck, sht, def, pas, dri, (int) (Math.random() * 60 + 20));
                        jta.append("\nCreated Player: " + player);
                        players.add(player);
                        availPlayers.add(player);
                        labelPointsRemaining.setText("Created player: " + player.getName());
                    }
                }
            }
            );

            calculateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int pointsUsed = Integer.parseInt(inputHeight.getText()) + Integer.parseInt(inputStr.getText()) + Integer.parseInt(inputQuick.getText()) + Integer.parseInt(inputShoot.getText()) + Integer.parseInt(inputDef.getText()) + Integer.parseInt(inputPass.getText()) + Integer.parseInt(inputDribble.getText());
                    labelPointsRemaining.setText("Points Used: " + String.valueOf(pointsUsed) + "/" + pointsMax);
                }
            }
            );

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frameAddPlayer.dispatchEvent(new WindowEvent(frameAddPlayer, WindowEvent.WINDOW_CLOSING));
                }
            }
            );

        } else if (n == 2) {
            if (players.isEmpty()) {
                jta.append("\nNo players to delete");
                return;
            }
            JComboBox<String> playerList = new JComboBox<>();
            for (Player p : players) {
                playerList.addItem(p.getName());
            }
            JLabel labelName = new JLabel("Choose Player to Delete");

            JButton deletePlayerButton = new JButton("Delete");
            JButton exitButton = new JButton("Exit");

            GridLayout inputLayout = new GridLayout(0, 2);
            JPanel deletePlayer = new JPanel(inputLayout);
            deletePlayer.add(labelName);
            deletePlayer.add(playerList);

            JPanel playerButtons = new JPanel();
            playerButtons.add(deletePlayerButton);
            playerButtons.add(exitButton);

            JFrame frameAddPlayer = new JFrame();
            frameAddPlayer.add(deletePlayer, BorderLayout.PAGE_START);
            frameAddPlayer.add(playerButtons, BorderLayout.CENTER);

            frameAddPlayer.setSize(350, 130);
            frameAddPlayer.setVisible(true);
            frameAddPlayer.setLocationRelativeTo(jrun);
            frameAddPlayer.setTitle("Delete Player");

            deletePlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (Player p : players) {
                        if (p.name.equals(playerList.getSelectedItem())) {
                            jta.append("\nDeleted Player: " + p);
                            if (p.getTeam() == null) {
                                availPlayers.remove(p);
                            } else {
                                switch (p.getPosition()) {
                                    case "C":
                                        p.getTeam().removeC(p);
                                        break;
                                    case "PF":
                                        p.getTeam().removePF(p);
                                        break;
                                    case "SF":
                                        p.getTeam().removeSF(p);
                                        break;
                                    case "SG":
                                        p.getTeam().removeSG(p);
                                        break;
                                    case "PG":
                                        p.getTeam().removePG(p);
                                        break;
                                }
                            }
                            players.remove(p);
                            playerList.removeAllItems();
                            for (Player p2 : players) {
                                playerList.addItem(p2.getName());
                            }
                            return;
                        }
                    }
                }
            }
            );

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frameAddPlayer.dispatchEvent(new WindowEvent(frameAddPlayer, WindowEvent.WINDOW_CLOSING));
                }
            }
            );
        } else if (n == 3) {
            if (players.isEmpty()) {
                jta.append("\nNo players to edit");
                return;
            }
            JComboBox<String> playerList = new JComboBox<>();
            for (Player p : players) {
                playerList.addItem(p.getName());
            }

            JLabel labelName = new JLabel("Name");
            JLabel labelHeight = new JLabel("Height (in) (60-99)");
            JLabel labelStr = new JLabel("Strength (1-99)");
            JLabel labelQuick = new JLabel("Quickness (1-99)");
            JLabel labelShoot = new JLabel("Shooting (1-99)");
            JLabel labelDef = new JLabel("Defense (1-99)");
            JLabel labelPass = new JLabel("Passing (1-99)");
            JLabel labelDribble = new JLabel("Dribble (1-99)");
            JLabel labelPointsRemaining = new JLabel("Points Remaining: " + String.valueOf(pointsMax));

            NumberFormat twoDigits = NumberFormat.getInstance();
            twoDigits.setMaximumIntegerDigits(2);
            twoDigits.setParseIntegerOnly(true);
            JTextField inputHeight = new JFormattedTextField(twoDigits);
            JTextField inputStr = new JFormattedTextField(twoDigits);
            JTextField inputQuick = new JFormattedTextField(twoDigits);
            JTextField inputShoot = new JFormattedTextField(twoDigits);
            JTextField inputDef = new JFormattedTextField(twoDigits);
            JTextField inputPass = new JFormattedTextField(twoDigits);
            JTextField inputDribble = new JFormattedTextField(twoDigits);
            inputHeight.setText(Integer.toString(players.get(0).getHeight()));
            inputStr.setText(Integer.toString(players.get(0).getStrength()));
            inputQuick.setText(Integer.toString(players.get(0).getQuickness()));
            inputShoot.setText(Integer.toString(players.get(0).getShooting()));
            inputDef.setText(Integer.toString(players.get(0).getDefense()));
            inputPass.setText(Integer.toString(players.get(0).getPassing()));
            inputDribble.setText(Integer.toString(players.get(0).getDribble()));

            JButton editPlayerButton = new JButton("Edit Player");
            JButton calculateButton = new JButton("Calculate Points");
            JButton exitButton = new JButton("Exit");

            GridLayout inputLayout = new GridLayout(0, 2);
            JPanel playerData = new JPanel(inputLayout);
            playerData.add(labelName);
            playerData.add(playerList);
            playerData.add(labelHeight);
            playerData.add(inputHeight);
            playerData.add(labelStr);
            playerData.add(inputStr);
            playerData.add(labelQuick);
            playerData.add(inputQuick);
            playerData.add(labelShoot);
            playerData.add(inputShoot);
            playerData.add(labelDef);
            playerData.add(inputDef);
            playerData.add(labelPass);
            playerData.add(inputPass);
            playerData.add(labelDribble);
            playerData.add(inputDribble);

            JPanel playerButtons = new JPanel();
            playerButtons.add(editPlayerButton);
            playerButtons.add(calculateButton);
            playerButtons.add(exitButton);

            JPanel remainingPoints = new JPanel();
            remainingPoints.add(labelPointsRemaining);

            JFrame frameAddPlayer = new JFrame();
            frameAddPlayer.add(playerData, BorderLayout.PAGE_START);
            frameAddPlayer.add(remainingPoints, BorderLayout.CENTER);
            frameAddPlayer.add(playerButtons, BorderLayout.PAGE_END);

            frameAddPlayer.setSize(350, 335);
            frameAddPlayer.setVisible(true);
            frameAddPlayer.setLocationRelativeTo(jrun);
            frameAddPlayer.setTitle("Edit Player");

            playerList.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    for (Player p : players) {
                        if (p.name.equals(playerList.getSelectedItem())) {
                            inputHeight.setText(Integer.toString(p.getHeight()));
                            inputStr.setText(Integer.toString(p.getStrength()));
                            inputQuick.setText(Integer.toString(p.getQuickness()));
                            inputShoot.setText(Integer.toString(p.getShooting()));
                            inputDef.setText(Integer.toString(p.getDefense()));
                            inputPass.setText(Integer.toString(p.getPassing()));
                            inputDribble.setText(Integer.toString(p.getDribble()));
                            break;
                        }
                    }
                }
            });

            inputHeight.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputHeight.getText());
                    } catch (NumberFormatException f) {
                        inputHeight.setText("0");
                    }
                    if (Integer.parseInt(inputHeight.getText()) < 60) {
                        inputHeight.setText("60");
                    }
                }
            });
            inputStr.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputStr.getText());
                    } catch (NumberFormatException f) {
                        inputStr.setText("0");
                    }
                    if (Integer.parseInt(inputStr.getText()) < 1) {
                        inputStr.setText("1");
                    }
                }
            });
            inputQuick.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputQuick.getText());
                    } catch (NumberFormatException f) {
                        inputQuick.setText("0");
                    }
                    if (Integer.parseInt(inputQuick.getText()) < 1) {
                        inputQuick.setText("1");
                    }
                }
            });
            inputShoot.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputShoot.getText());
                    } catch (NumberFormatException f) {
                        inputShoot.setText("0");
                    }
                    if (Integer.parseInt(inputShoot.getText()) < 1) {
                        inputShoot.setText("1");
                    }
                }
            });
            inputDef.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputDef.getText());
                    } catch (NumberFormatException f) {
                        inputDef.setText("0");
                    }
                    if (Integer.parseInt(inputDef.getText()) < 1) {
                        inputDef.setText("1");
                    }
                }
            });
            inputPass.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputPass.getText());
                    } catch (NumberFormatException f) {
                        inputPass.setText("0");
                    }
                    if (Integer.parseInt(inputPass.getText()) < 1) {
                        inputPass.setText("1");
                    }
                }
            });
            inputDribble.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        Integer.parseInt(inputDribble.getText());
                    } catch (NumberFormatException f) {
                        inputDribble.setText("0");
                    }
                    if (Integer.parseInt(inputDribble.getText()) < 1) {
                        inputDribble.setText("1");
                    }
                }
            });

            editPlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int pointsUsed = Integer.parseInt(inputHeight.getText()) + Integer.parseInt(inputStr.getText()) + Integer.parseInt(inputQuick.getText()) + Integer.parseInt(inputShoot.getText()) + Integer.parseInt(inputDef.getText()) + Integer.parseInt(inputPass.getText()) + Integer.parseInt(inputDribble.getText());
                    if (pointsUsed > pointsMax) {
                        labelPointsRemaining.setText("Exceeded point limit! Points Used: " + String.valueOf(pointsUsed) + "/" + pointsMax);
                    } else if (pointsUsed < pointsMin) {
                        labelPointsRemaining.setText("Player needs at least " + pointsMin + " points. Points Used: " + String.valueOf(pointsUsed) + "/" + pointsMax);
                    } else {
                        for (Player p : players) {
                            if (p.name.equals(playerList.getSelectedItem())) {
                                p.setHeight(Integer.parseInt(inputHeight.getText()));
                                p.setStrength(Integer.parseInt(inputStr.getText()));
                                p.setQuickness(Integer.parseInt(inputQuick.getText()));
                                p.setShooting(Integer.parseInt(inputShoot.getText()));
                                p.setDefense(Integer.parseInt(inputDef.getText()));
                                p.setPassing(Integer.parseInt(inputPass.getText()));
                                p.setDribble(Integer.parseInt(inputDribble.getText()));
                                labelPointsRemaining.setText("Edited Player: " + p);
                                jta.append("\nEdited Player: " + p);
                                break;
                            }
                        }
                    }
                }
            }
            );

            calculateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int pointsUsed = Integer.parseInt(inputHeight.getText()) + Integer.parseInt(inputStr.getText()) + Integer.parseInt(inputQuick.getText()) + Integer.parseInt(inputShoot.getText()) + Integer.parseInt(inputDef.getText()) + Integer.parseInt(inputPass.getText()) + Integer.parseInt(inputDribble.getText());
                    labelPointsRemaining.setText("Points Used: " + String.valueOf(pointsUsed) + "/" + pointsMax);
                }
            }
            );

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frameAddPlayer.dispatchEvent(new WindowEvent(frameAddPlayer, WindowEvent.WINDOW_CLOSING));
                }
            }
            );

        }
    }

    public void teamMenu() {

        Object[] options = {"See All", "Create Team", "Delete Team", "Edit Team", "Cancel"};

        int n = JOptionPane.showOptionDialog(jf, "", "Team Options", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[4]);

        if (n == 0) { //See All
            if (teams.isEmpty()) {
                jta.append("\nNo teams to display");
            } else {
                jta.append("\nList of Teams: ");
                for (int i = 0; i < teams.size(); i++) {
                    jta.append("\n" + teams.get(i).toString());
                }
            }
        } else if (n == 1) { //Create Team
            ArrayList<String> freeAgents = new ArrayList<>();
            freeAgents.add(null);
            for (Player p : availPlayers) {
                freeAgents.add(p.getName());
            }

            JComboBox<String> PGList = new JComboBox<>();
            JComboBox<String> SGList = new JComboBox<>();
            JComboBox<String> SFList = new JComboBox<>();
            JComboBox<String> PFList = new JComboBox<>();
            JComboBox<String> CList = new JComboBox<>();

            for (String s : freeAgents) {
                PGList.addItem(s);
            }
            for (String s : freeAgents) {
                SGList.addItem(s);
            }
            for (String s : freeAgents) {
                SFList.addItem(s);
            }
            for (String s : freeAgents) {
                PFList.addItem(s);
            }
            for (String s : freeAgents) {
                CList.addItem(s);
            }

            JTextField inputLocation = new JFormattedTextField();
            JTextField inputName = new JFormattedTextField();

            JLabel labelLocation = new JLabel("Location");
            JLabel labelName = new JLabel("Name");
            JLabel labelPG = new JLabel("Point Guard");
            JLabel labelSG = new JLabel("Shooting Guard");
            JLabel labelSF = new JLabel("Small Forward");
            JLabel labelPF = new JLabel("Power Forward");
            JLabel labelC = new JLabel("Center");
            JLabel teamMessage = new JLabel("Create a new team");

            JButton createTeamButton = new JButton("Create Team");
            JButton exitButton = new JButton("Exit");

            GridLayout inputLayout = new GridLayout(0, 2);
            JPanel teamData = new JPanel(inputLayout);
            teamData.add(labelLocation);
            teamData.add(inputLocation);
            teamData.add(labelName);
            teamData.add(inputName);
            teamData.add(labelPG);
            teamData.add(PGList);
            teamData.add(labelSG);
            teamData.add(SGList);
            teamData.add(labelSF);
            teamData.add(SFList);
            teamData.add(labelPF);
            teamData.add(PFList);
            teamData.add(labelC);
            teamData.add(CList);

            JPanel teamButtons = new JPanel();
            teamButtons.add(createTeamButton);
            teamButtons.add(exitButton);

            JPanel teamMessagePanel = new JPanel();
            teamMessagePanel.add(teamMessage);

            JFrame frameCreateTeam = new JFrame();

            frameCreateTeam.add(teamData, BorderLayout.PAGE_START);
            frameCreateTeam.add(teamMessagePanel, BorderLayout.CENTER);
            frameCreateTeam.add(teamButtons, BorderLayout.PAGE_END);

            frameCreateTeam.setSize(350, 305);
            frameCreateTeam.setVisible(true);
            frameCreateTeam.setLocationRelativeTo(jrun);
            frameCreateTeam.setTitle("Create Team");

            inputLocation.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    inputLocation.setText(inputLocation.getText().trim().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
                }
            });

            inputName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    inputName.setText(inputName.getText().trim().replaceAll(" +", " ").replaceAll("[^a-zA-Z ]", ""));
                }
            });

            createTeamButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (inputLocation.getText().isEmpty()) {
                        teamMessage.setText("Please enter a team location");
                        return;
                    }
                    if (inputName.getText().isEmpty()) {
                        teamMessage.setText("Please enter a team name");
                        return;
                    } else {
                        for (Team t : teams) {
                            if (inputName.getText().toUpperCase().equals(t.getName().toUpperCase())) {
                                teamMessage.setText("There is already a team with that name");
                                return;
                            }
                        }
                    }

                    if (PGList.getSelectedItem() != null) {
                        if (PGList.getSelectedItem() == SGList.getSelectedItem() || PGList.getSelectedItem() == SFList.getSelectedItem() || PGList.getSelectedItem() == PFList.getSelectedItem() || PGList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }
                    if (SGList.getSelectedItem() != null) {
                        if (SGList.getSelectedItem() == SFList.getSelectedItem() || SGList.getSelectedItem() == PFList.getSelectedItem() || SGList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }
                    if (SFList.getSelectedItem() != null) {
                        if (SFList.getSelectedItem() == PFList.getSelectedItem() || SFList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }
                    if (PFList.getSelectedItem() != null) {
                        if (PFList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }

                    Team team = new Team(inputLocation.getText(), inputName.getText());

                    if (PGList.getSelectedItem() != null) {
                        for (Player p : availPlayers) {
                            if (p.getName().equals(PGList.getSelectedItem())) {
                                team.addPG(p);
                                availPlayers.remove(p);
                                break;
                            }
                        }
                    }
                    if (SGList.getSelectedItem() != null) {
                        for (Player p : availPlayers) {
                            if (p.getName().equals(SGList.getSelectedItem())) {
                                team.addSG(p);
                                availPlayers.remove(p);
                                break;
                            }
                        }
                    }
                    if (SFList.getSelectedItem() != null) {
                        for (Player p : availPlayers) {
                            if (p.getName().equals(SFList.getSelectedItem())) {
                                team.addSF(p);
                                availPlayers.remove(p);
                                break;
                            }
                        }
                    }
                    if (PFList.getSelectedItem() != null) {
                        for (Player p : availPlayers) {
                            if (p.getName().equals(PFList.getSelectedItem())) {
                                team.addPF(p);
                                availPlayers.remove(p);
                                break;
                            }
                        }
                    }
                    if (CList.getSelectedItem() != null) {
                        for (Player p : availPlayers) {
                            if (p.getName().equals(CList.getSelectedItem())) {
                                team.addC(p);
                                availPlayers.remove(p);
                                break;
                            }
                        }
                    }
                    teams.add(team);
                    teamMessage.setText("Created Team: " + team);
                    jta.append("\nCreated Team: " + team);
                    frameCreateTeam.dispatchEvent(new WindowEvent(frameCreateTeam, WindowEvent.WINDOW_CLOSING));
                }
            }
            );

            exitButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frameCreateTeam.dispatchEvent(new WindowEvent(frameCreateTeam, WindowEvent.WINDOW_CLOSING));
                        }
                    }
            );
        } else if (n == 2) { //Delete Team
            if (teams.isEmpty()) {
                jta.append("\nNo teams to delete");
                return;
            }
            JComboBox<String> teamList = new JComboBox<>();
            for (Team t : teams) {
                teamList.addItem(t.getName());
            }
            JLabel labelName = new JLabel("Choose Team to Delete");

            JButton deleteTeamButton = new JButton("Delete Team");
            JButton exitButton = new JButton("Exit");

            GridLayout inputLayout = new GridLayout(0, 2);
            JPanel deleteTeam = new JPanel(inputLayout);
            deleteTeam.add(labelName);
            deleteTeam.add(teamList);

            JPanel teamButtons = new JPanel();
            teamButtons.add(deleteTeamButton);
            teamButtons.add(exitButton);

            JFrame frameDeleteTeam = new JFrame();
            frameDeleteTeam.add(deleteTeam, BorderLayout.PAGE_START);
            frameDeleteTeam.add(teamButtons, BorderLayout.CENTER);

            frameDeleteTeam.setSize(350, 130);
            frameDeleteTeam.setVisible(true);
            frameDeleteTeam.setLocationRelativeTo(jrun);
            frameDeleteTeam.setTitle("Delete Team");

            deleteTeamButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (Team t : teams) {
                        if (t.name.equals(teamList.getSelectedItem())) {
                            jta.append("\nDeleted Team: " + t);
                            if (t.getC() != null) {
                                availPlayers.add(t.getC());
                                t.removeC(t.getC());
                            }
                            if (t.getPF() != null) {
                                availPlayers.add(t.getPF());
                                t.removePF(t.getPF());
                            }
                            if (t.getPG() != null) {
                                availPlayers.add(t.getPG());
                                t.removePG(t.getPG());
                            }
                            if (t.getSF() != null) {
                                availPlayers.add(t.getSF());
                                t.removeSF(t.getSF());
                            }
                            if (t.getSG() != null) {
                                availPlayers.add(t.getSG());
                                t.removeSG(t.getSG());
                            }
                            teams.remove(t);
                            teamList.removeAllItems();
                            for (Team t2 : teams) {
                                teamList.addItem(t2.getName());
                            }
                            return;
                        }
                    }
                }
            }
            );

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frameDeleteTeam.dispatchEvent(new WindowEvent(frameDeleteTeam, WindowEvent.WINDOW_CLOSING));
                }
            }
            );
        } else if (n == 3) { //Edit Team
            if (teams.isEmpty()) {
                jta.append("\nNo teams to edit");
                return;
            }

            JComboBox<String> teamList = new JComboBox<>();
            teamList.addItem("--- Select Team ---");
            for (Team t : teams) {
                teamList.addItem(t.getName());
            }

            JComboBox<String> PGList = new JComboBox<>();
            JComboBox<String> SGList = new JComboBox<>();
            JComboBox<String> SFList = new JComboBox<>();
            JComboBox<String> PFList = new JComboBox<>();
            JComboBox<String> CList = new JComboBox<>();

            JLabel labelName = new JLabel("Name");
            JLabel labelPG = new JLabel("Point Guard");
            JLabel labelSG = new JLabel("Shooting Guard");
            JLabel labelSF = new JLabel("Small Forward");
            JLabel labelPF = new JLabel("Power Forward");
            JLabel labelC = new JLabel("Center");
            JLabel teamMessage = new JLabel("Select a team to edit");

            JButton editTeamButton = new JButton("Edit Team");
            JButton exitButton = new JButton("Exit");

            GridLayout inputLayout = new GridLayout(0, 2);
            JPanel teamData = new JPanel(inputLayout);
            teamData.add(labelName);
            teamData.add(teamList);
            teamData.add(labelPG);
            teamData.add(PGList);
            teamData.add(labelSG);
            teamData.add(SGList);
            teamData.add(labelSF);
            teamData.add(SFList);
            teamData.add(labelPF);
            teamData.add(PFList);
            teamData.add(labelC);
            teamData.add(CList);

            JPanel teamButtons = new JPanel();
            teamButtons.add(editTeamButton);
            teamButtons.add(exitButton);

            JPanel teamMessagePanel = new JPanel();
            teamMessagePanel.add(teamMessage);

            JFrame frameEditTeam = new JFrame();

            frameEditTeam.add(teamData, BorderLayout.PAGE_START);
            frameEditTeam.add(teamMessagePanel, BorderLayout.CENTER);
            frameEditTeam.add(teamButtons, BorderLayout.PAGE_END);

            frameEditTeam.setSize(350, 280);
            frameEditTeam.setVisible(true);
            frameEditTeam.setLocationRelativeTo(jrun);
            frameEditTeam.setTitle("Edit Team");

            teamList.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    ArrayList<String> freeAgents = new ArrayList<>();
                    for (Player p : availPlayers) {
                        freeAgents.add(p.getName());
                    }
                    freeAgents.add(null);

                    if (teamList.getSelectedIndex() == 0) {
                        teamMessage.setText("Select a team to edit");
                        PGList.removeAllItems();
                        PGList.addItem(null);
                        SGList.removeAllItems();
                        SGList.addItem(null);
                        SFList.removeAllItems();
                        SFList.addItem(null);
                        PFList.removeAllItems();
                        PFList.addItem(null);
                        CList.removeAllItems();
                        CList.addItem(null);

                    } else {
                        for (Team t : teams) {
                            teamMessage.setText("Edit " + teamList.getSelectedItem() + " Team Members");
                            String PG = null, SG = null, SF = null, PF = null, C = null;
                            if (t.name.equals(teamList.getSelectedItem())) {
                                if (t.getPG() != null) {
                                    PG = t.getPG().getName();
                                }
                                if (t.getSG() != null) {
                                    SG = t.getSG().getName();
                                }
                                if (t.getSF() != null) {
                                    SF = t.getSF().getName();
                                }
                                if (t.getPF() != null) {
                                    PF = t.getPF().getName();
                                }
                                if (t.getC() != null) {
                                    C = t.getC().getName();
                                }

                                PGList.removeAllItems();
                                if (PG != null) {
                                    PGList.addItem(PG);
                                }
                                if (SG != null) {
                                    PGList.addItem(SG);
                                }
                                if (SF != null) {
                                    PGList.addItem(SF);
                                }
                                if (PF != null) {
                                    PGList.addItem(PF);
                                }
                                if (C != null) {
                                    PGList.addItem(C);
                                }
                                for (String s : freeAgents) {
                                    PGList.addItem(s);
                                }
                                if (PG != null) {
                                    PGList.setSelectedItem(PG);
                                } else {
                                    PGList.setSelectedIndex(PGList.getItemCount() - 1);
                                }

                                SGList.removeAllItems();
                                if (PG != null) {
                                    SGList.addItem(PG);
                                }
                                if (SG != null) {
                                    SGList.addItem(SG);
                                }
                                if (SF != null) {
                                    SGList.addItem(SF);
                                }
                                if (PF != null) {
                                    SGList.addItem(PF);
                                }
                                if (C != null) {
                                    SGList.addItem(C);
                                }
                                for (String s : freeAgents) {
                                    SGList.addItem(s);
                                }
                                if (SG != null) {
                                    SGList.setSelectedItem(SG);
                                } else {
                                    SGList.setSelectedIndex(SGList.getItemCount() - 1);
                                }

                                SFList.removeAllItems();
                                if (PG != null) {
                                    SFList.addItem(PG);
                                }
                                if (SG != null) {
                                    SFList.addItem(SG);
                                }
                                if (SF != null) {
                                    SFList.addItem(SF);
                                }
                                if (PF != null) {
                                    SFList.addItem(PF);
                                }
                                if (C != null) {
                                    SFList.addItem(C);
                                }
                                for (String s : freeAgents) {
                                    SFList.addItem(s);
                                }
                                if (SF != null) {
                                    SFList.setSelectedItem(SF);
                                } else {
                                    SFList.setSelectedIndex(SFList.getItemCount() - 1);
                                }

                                PFList.removeAllItems();
                                if (PG != null) {
                                    PFList.addItem(PG);
                                }
                                if (SG != null) {
                                    PFList.addItem(SG);
                                }
                                if (SF != null) {
                                    PFList.addItem(SF);
                                }
                                if (PF != null) {
                                    PFList.addItem(PF);
                                }
                                if (C != null) {
                                    PFList.addItem(C);
                                }
                                for (String s : freeAgents) {
                                    PFList.addItem(s);
                                }
                                if (PF != null) {
                                    PFList.setSelectedItem(PF);
                                } else {
                                    PFList.setSelectedIndex(PFList.getItemCount() - 1);
                                }

                                CList.removeAllItems();
                                if (PG != null) {
                                    CList.addItem(PG);
                                }
                                if (SG != null) {
                                    CList.addItem(SG);
                                }
                                if (SF != null) {
                                    CList.addItem(SF);
                                }
                                if (PF != null) {
                                    CList.addItem(PF);
                                }
                                if (C != null) {
                                    CList.addItem(C);
                                }
                                for (String s : freeAgents) {
                                    CList.addItem(s);
                                }
                                if (C != null) {
                                    CList.setSelectedItem(C);
                                } else {
                                    CList.setSelectedIndex(CList.getItemCount() - 1);
                                }
                                break;
                            }
                        }
                    }
                }
            });

            editTeamButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (teamList.getSelectedIndex() == 0) {
                        teamMessage.setText("Please select a team to edit");
                        return;
                    }

                    if (PGList.getSelectedItem() != null) {
                        if (PGList.getSelectedItem() == SGList.getSelectedItem() || PGList.getSelectedItem() == SFList.getSelectedItem() || PGList.getSelectedItem() == PFList.getSelectedItem() || PGList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }
                    if (SGList.getSelectedItem() != null) {
                        if (SGList.getSelectedItem() == SFList.getSelectedItem() || SGList.getSelectedItem() == PFList.getSelectedItem() || SGList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }
                    if (SFList.getSelectedItem() != null) {
                        if (SFList.getSelectedItem() == PFList.getSelectedItem() || SFList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }
                    if (PFList.getSelectedItem() != null) {
                        if (PFList.getSelectedItem() == CList.getSelectedItem()) {
                            teamMessage.setText("Please select a unique player for each position");
                            return;
                        }
                    }

                    for (Team t : teams) {
                        if (t.name.equals(teamList.getSelectedItem())) {
                            teamMessage.setText("Edited Team: " + t);
                            jta.append("\nEdited Team: " + t);

                            if (t.getPG() != null) {
                                availPlayers.add(t.getPG());
                            }
                            if (t.getSG() != null) {
                                availPlayers.add(t.getSG());
                            }
                            if (t.getSF() != null) {
                                availPlayers.add(t.getSF());
                            }
                            if (t.getPF() != null) {
                                availPlayers.add(t.getPF());
                            }
                            if (t.getC() != null) {
                                availPlayers.add(t.getC());
                            }

                            if (PGList.getSelectedItem() == null && t.getPG() != null) {
                                t.removePG(t.getPG());
                            } else {
                                for (Player p : availPlayers) {
                                    if (p.getName().equals(PGList.getSelectedItem())) {
                                        t.addPG(p);
                                        availPlayers.remove(p);
                                        break;
                                    }
                                }
                            }
                            if (SGList.getSelectedItem() == null && t.getSG() != null) {
                                t.removeSG(t.getSG());
                            } else {
                                for (Player p : availPlayers) {
                                    if (p.getName().equals(SGList.getSelectedItem())) {
                                        t.addSG(p);
                                        availPlayers.remove(p);
                                        break;
                                    }
                                }
                            }
                            if (SFList.getSelectedItem() == null && t.getSF() != null) {
                                t.removeSF(t.getSF());
                            } else {
                                for (Player p : availPlayers) {
                                    if (p.getName().equals(SFList.getSelectedItem())) {
                                        t.addSF(p);
                                        availPlayers.remove(p);
                                        break;
                                    }
                                }
                            }
                            if (PFList.getSelectedItem() == null && t.getPF() != null) {
                                t.removePF(t.getPF());
                            } else {
                                for (Player p : availPlayers) {
                                    if (p.getName().equals(PFList.getSelectedItem())) {
                                        t.addPF(p);
                                        availPlayers.remove(p);
                                        break;
                                    }
                                }
                            }
                            if (CList.getSelectedItem() == null && t.getC() != null) {
                                t.removeC(t.getC());
                            } else {
                                for (Player p : availPlayers) {
                                    if (p.getName().equals(CList.getSelectedItem())) {
                                        t.addC(p);
                                        availPlayers.remove(p);
                                        break;
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
            }
            );

            exitButton.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frameEditTeam.dispatchEvent(new WindowEvent(frameEditTeam, WindowEvent.WINDOW_CLOSING));
                        }
                    }
            );
        }
    }

    public void displayScores() {
        if (scores.isEmpty()) {
            jta.append("\nThere are previous games to display.\n");
        } else {
            jta.append(String.format("\n\n%56s\n", "---------- Game Scores ----------"));
            for (Scores s : scores) {
                jta.append("\n" + s);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BasketSim sim = new BasketSim();
    }
}//End class BasketSim



