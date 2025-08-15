import java.awt.*;
import javax.swing.*;

public class TicTacToe extends JFrame {

    // --- Constants for better readability and maintenance ---
    private static final int BOARD_SIZE = 3;
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 60);
    private static final Font STATUS_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Color WIN_COLOR = new Color(144, 238, 144); // Light Green
    // --- Model: Separates game state from the UI ---
    private char[][] board;
    private char currentPlayer;
    private boolean gameActive;
    private String player1Name;
    private String player2Name;

    // --- UI Components ---
    private final JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
    private JLabel statusLabel;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(400, 500); // Increased height for the reset button
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // --- Model Initialization ---
        board = new char[BOARD_SIZE][BOARD_SIZE];

        // --- UI Initialization ---
        JPanel startPanel = createStartPanel();
        JPanel gamePanel = createGamePanel();

        mainPanel.add(startPanel, "START");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);
    }

    private JPanel createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        JPanel boardPanel = createBoardPanel();
        gamePanel.add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        gamePanel.add(controlPanel, BorderLayout.SOUTH);
        return gamePanel;
    }

    private JPanel createStartPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Enter Player Names"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Player 1 (X):"), gbc);

        gbc.gridx = 1;
        JTextField player1Field = new JTextField("Player 1", 15);
        panel.add(player1Field, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Player 2 (O):"), gbc);

        gbc.gridx = 1;
        JTextField player2Field = new JTextField("Player 2", 15);
        panel.add(player2Field, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> {
            player1Name = player1Field.getText().trim();
            player2Name = player2Field.getText().trim();
            if (player1Name.isEmpty()) player1Name = "Player 1";
            if (player2Name.isEmpty()) player2Name = "Player 2";

            resetGame(); // Set up the board for the first time
            cardLayout.show(mainPanel, "GAME");
        });
        panel.add(startButton, gbc);

        return panel;
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFont(BUTTON_FONT);
                // Using final variables for use in lambda expression
                final int r = row;
                final int c = col;
                buttons[row][col].addActionListener(e -> onButtonClick(r, c));
                boardPanel.add(buttons[row][col]);
            }
        }
        return boardPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(STATUS_FONT);
        controlPanel.add(statusLabel, BorderLayout.NORTH);

        JButton resetButton = new JButton("New Game");
        resetButton.addActionListener(e -> resetGame());
        controlPanel.add(resetButton, BorderLayout.CENTER);

        return controlPanel;
    }

    private void onButtonClick(int row, int col) {
        // Check if the cell is already taken or if the game is over
        if (board[row][col] != '\0' || !gameActive) {
            return;
        }

        // 1. Update the model
        board[row][col] = currentPlayer;
        // 2. Update the view (UI) based on the model
        buttons[row][col].setText(String.valueOf(currentPlayer));

        // 3. Check for game end conditions by finding a winning line
        Point[] winningLine = getWinningLine();
        if (winningLine != null) {
            statusLabel.setText(getCurrentPlayerName() + " wins!");
            gameActive = false;
            highlightWin(winningLine);
            setButtonsEnabled(false);
        } else if (isBoardFull()) { // Check for a draw
            statusLabel.setText("It's a draw!");
            gameActive = false;
        } else {
            // 4. If game continues, switch player
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            statusLabel.setText(getCurrentPlayerName() + "'s turn");
        }
    }

    private String getCurrentPlayerName() {
        return (currentPlayer == 'X') ? player1Name : player2Name;
    }

    private void resetGame() {
        currentPlayer = 'X';
        gameActive = true;
        statusLabel.setText(player1Name + "'s turn");

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = '\0'; // Reset model
                buttons[row][col].setText("");   // Reset UI
                buttons[row][col].setBackground(null); // Reset background color
            }
        }
        setButtonsEnabled(true); // Re-enable all buttons
    }

    /**
     * Checks the game model for a win condition for the current player.
     */
    private Point[] getWinningLine() {
        // Check rows
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return new Point[]{new Point(i, 0), new Point(i, 1), new Point(i, 2)};
            }
        }
        // Check columns
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return new Point[]{new Point(0, i), new Point(1, i), new Point(2, i)};
            }
        }
        // Check diagonals
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return new Point[]{new Point(0, 0), new Point(1, 1), new Point(2, 2)};
        }
        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return new Point[]{new Point(0, 2), new Point(1, 1), new Point(2, 0)};
        }
        return null; // No winning line found
    }

    private boolean checkWin() {
        return getWinningLine() != null;
    }

    private void highlightWin(Point[] winningLine) {
        if (winningLine == null) return;
        for (Point p : winningLine) {
            buttons[p.x][p.y].setBackground(WIN_COLOR);
        }
    }

    /**
     * Checks the game model to see if all cells are filled.
     */
    private boolean isBoardFull() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private void setButtonsEnabled(boolean enabled) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col].setEnabled(enabled);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToe game = new TicTacToe();
            game.setVisible(true);
        });
    }
}
