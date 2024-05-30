import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGame extends JFrame {
    private static final int GRID_SIZE = 9; // 9x9 grid
    private static final int SUBGRID_SIZE = 3; // 3x3 subgrids
    private JTextField[][] cells;
    private Generator generator;
    private String currentDifficulty;

    public SudokuGame() {
        generator = new Generator();

        setTitle("Sudoku");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        showDifficultySelection();
    }

    private void showDifficultySelection() {
        String[] options = {"Easy", "Medium", "Hard", "Extreme", "Insane"};
        int response = JOptionPane.showOptionDialog(this, "Select difficulty level:", "Sudoku",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (response >= 0 && response < options.length) {
            startNewGame(options[response]);
        } else {
            dispose(); // Close application if no option selected
        }
    }

    private void startNewGame(String difficulty) {
        currentDifficulty = difficulty;
        getContentPane().removeAll();
        cells = new JTextField[GRID_SIZE][GRID_SIZE];
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Monospaced", Font.BOLD, 20));
                cells[row][col].setBorder(createCellBorder(row, col));
                panel.add(cells[row][col]);
            }
        }
        cp.add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnNewGame = new JButton("New Game");
        JButton btnCheck = new JButton("Check");
        JButton autobtn = new JButton("Auto-Complete");
        buttonPanel.add(btnNewGame);
        buttonPanel.add(btnCheck);
        buttonPanel.add(autobtn);

        cp.add(buttonPanel, BorderLayout.SOUTH);

        btnNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDifficultySelection();
            }
        });

        btnCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSolution();
            }
        });

        autobtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { autocomplete(); }
        });

        // Initialize the game when the application starts
        newGame(difficulty);
        revalidate();
        repaint();
    }

    private Border createCellBorder(int row, int col) {
        int top = (row % SUBGRID_SIZE == 0) ? 2 : 1;
        int left = (col % SUBGRID_SIZE == 0) ? 2 : 1;
        int bottom = ((row + 1) % SUBGRID_SIZE == 0) ? 2 : 1;
        int right = ((col + 1) % SUBGRID_SIZE == 0) ? 2 : 1;

        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
    }

    private void newGame(String difficulty) {
        int emptyCells;
        switch (difficulty) {
            case "Easy":
                emptyCells = 40;
                break;
            case "Medium":
                emptyCells = 50;
                break;
            case "Hard":
                emptyCells = 55;
                break;
            case "Extreme":
                emptyCells = 60;
                break;
            case "Insane":
                emptyCells = 65;
                break;
            default:
                emptyCells = 40;
                break;
        }

        Grid grid = generator.generate(emptyCells);
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int value = grid.getCell(row, col).getValue();
                if (value != 0) {
                    cells[row][col].setText(String.valueOf(value));
                    cells[row][col].setEditable(false);
                    cells[row][col].setBackground(Color.LIGHT_GRAY);
                } else {
                    cells[row][col].setText("");
                    cells[row][col].setEditable(true);
                    cells[row][col].setBackground(Color.WHITE);
                }
            }
        }
    }

    private void autocomplete() {
        int[][] board = getBoardFromGUI();
        if (solveSudoku(board)) {
            updateBoardInGUI(board);
        } else {
            JOptionPane.showMessageDialog(this, "The puzzle cannot be solved. Please check your inputs.");
        }
    }

    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) { // Empty cell
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num;

                            if (solveSudoku(board)) {
                                return true;
                            }

                            board[row][col] = 0; // Backtrack
                        }
                    }
                    return false; // No valid number found, backtrack
                }
            }
        }
        return true; // Puzzle solved
    }

    private boolean isSafe(int[][] board, int row, int col, int num) {
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        // Check 3x3 sub-grid
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[][] getBoardFromGUI() {
        int[][] board = new int[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                String text = cells[row][col].getText();
                if (text.isEmpty()) {
                    board[row][col] = 0; // Empty cell
                } else {
                    board[row][col] = Integer.parseInt(text);
                }
            }
        }
        return board;
    }

    private void updateBoardInGUI(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col].setText(board[row][col] == 0 ? "" : String.valueOf(board[row][col]));
            }
        }
    }

    private void checkSolution() {
        int[][] board = new int[GRID_SIZE][GRID_SIZE];
        try {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    String cellText = cells[row][col].getText();
                    if (!cellText.isEmpty()) {
                        board[row][col] = Integer.parseInt(cellText);
                    } else {
                        JOptionPane.showMessageDialog(this, "Incomplete solution", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            if (isValidSudoku(board)) {
                JOptionPane.showMessageDialog(this, "Correct solution!", "Success", JOptionPane.INFORMATION_MESSAGE);
                startNewGame(currentDifficulty);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect solution", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidSudoku(int[][] board) {
        for (int i = 0; i < GRID_SIZE; i++) {
            boolean[] rows = new boolean[GRID_SIZE];
            boolean[] cols = new boolean[GRID_SIZE];
            boolean[] cube = new boolean[GRID_SIZE];
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] != 0) {
                    if (rows[board[i][j] - 1]) return false;
                    rows[board[i][j] - 1] = true;
                }
                if (board[j][i] != 0) {
                    if (cols[board[j][i] - 1]) return false;
                    cols[board[j][i] - 1] = true;
                }
                int RowIndex = 3 * (i / 3) + j / 3;
                int ColIndex = 3 * (i % 3) + j % 3;
                if (board[RowIndex][ColIndex] != 0) {
                    if (cube[board[RowIndex][ColIndex] - 1]) return false;
                    cube[board[RowIndex][ColIndex] - 1] = true;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new SudokuGame();
    }
}
