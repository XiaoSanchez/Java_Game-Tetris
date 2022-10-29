package App;
import block.Cell;
import block.Tetromino;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class Tetris extends JPanel {
    private Tetromino currentOne = Tetromino.randomOne();
    private Tetromino nextOne = Tetromino.randomOne();
    private Cell[][] wall = new Cell[18][9];
    private static final int CELL_SIZE = 48;
    int[] scores_pool = {0, 1, 2, 5, 10};
    private int totalScore = 0;
    private int totalLine = 0;
    public static final int PLING = 0;
    public static final int STOP = 1;
    public static final int OVER = 2;
    private int game_state;
    String[] show_state = {" [P] to PAUSE", " [C] to CONTINUE", " [S] to START"};
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage background;
    static {
        try {
            I = ImageIO.read(new File("img/Block.png"));
            J = ImageIO.read(new File("img/Block.png"));
            L = ImageIO.read(new File("img/Block.png"));
            O = ImageIO.read(new File("img/Block.png"));
            S = ImageIO.read(new File("img/Block.png"));
            T = ImageIO.read(new File("img/Block.png"));
            Z = ImageIO.read(new File("img/Block.png"));
            background = ImageIO.read(new File("img/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        g.translate(22, 15);
        paintWall(g);
        paintCurrentOne(g);
        paintNextOne(g);
        paintSource(g);
        paintState(g);
    }
    public void start() {
        game_state = PLING;
        KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_DOWN:
                        sortDropActive();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveleftActive();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRightActive();
                        break;
                    case KeyEvent.VK_UP:
                        rotateRightActive();
                        break;
                    case KeyEvent.VK_SPACE:
                            hadnDropActive();
                        break;
                    case KeyEvent.VK_P:
                        if (game_state == PLING) {
                            game_state = STOP;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if (game_state == STOP) {
                            game_state = PLING;
                        }
                        break;
                    case KeyEvent.VK_S:
                        game_state = PLING;
                        wall = new Cell[18][9];
                        currentOne = Tetromino.randomOne();
                        nextOne = Tetromino.randomOne();
                        totalScore = 0;
                        totalLine = 0;
                        break;
                }
            }
        };
        this.addKeyListener(l);
        this.requestFocus();
        while (true) {
            if (game_state == PLING) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (camDrop()) {
                    currentOne.moveDrop();
                } else {
                    landToWall();
                    destroyLine();
                    if (isGameOver()) {
                        game_state = OVER;
                    } else {
                        currentOne = nextOne;
                        nextOne = Tetromino.randomOne();
                    }
                }
            }
            repaint();
        }
    }
    public void rotateRightActive() {
        currentOne.rotateRight();
        if (outOFBounds() || coincide()) {
            currentOne.rotateLeft();
        }
    }
    public void hadnDropActive() {
        while (true) {
            if (camDrop()) {
                currentOne.moveDrop();
            } else {
                break;
            }
        }
        landToWall();
        destroyLine();
        if (isGameOver()) {
            game_state = OVER;
        } else {
            currentOne = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }
    public void sortDropActive() {
        if (camDrop()) {
            currentOne.moveDrop();
        } else {
            landToWall();
            destroyLine();
            if (isGameOver()) {
                game_state = OVER;
            } else {
                currentOne = nextOne;
                nextOne = Tetromino.randomOne();
            }
        }
    }
    private void landToWall() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }
    public boolean camDrop() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (row == wall.length - 1) {
                return false;
            } else if (wall[row + 1][col] != null) {
                return false;
            }
        }
        return true;
    }
    public void destroyLine() {
        int line = 0;
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            if (isFullLine(row)) {
                line++;
                for (int i = row; i > 0; i--) {
                    System.arraycopy(wall[i - 1], 0, wall[i], 0, wall[0].length);
                }
                wall[0] = new Cell[9];
            }
        }
        totalScore += scores_pool[line];
        totalLine += line;
    }
    public boolean isFullLine(int row) {
        Cell[] cells = wall[row];
        for (Cell cell : cells) {
            if (cell == null) {
                return false;
            }
        }
        return true;
    }
    public boolean isGameOver() {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }
    private void paintState(Graphics g) {
        if (game_state == PLING) {
            g.drawString(show_state[PLING], 500, 660);
        } else if (game_state == STOP) {
            g.drawString(show_state[STOP], 500, 660);
        } else {
            g.drawString(show_state[OVER], 500, 660);
            g.setColor(Color.RED);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 60));
            g.drawString("GAME OVER!", 30, 400);
        }
    }
    private void paintSource(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        g.drawString("Score: " + totalScore, 500, 250);
        g.drawString("Lines: " + totalLine, 500, 430);
    }
    private void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE + 370;
            int y = cell.getRow() * CELL_SIZE + 25;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }
    private void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }
    private void paintWall(Graphics g) {
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[i].length; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];
                if (cell == null) {
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    g.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }
    public boolean outOFBounds() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if (row < 0 || row > wall.length - 1 || col < 0 || col > wall[0].length-1) {
                return true;
            }
        }
        return false;
    }
    public void moveleftActive() {
        currentOne.moveLeft();
        if (outOFBounds() || coincide()) {
            currentOne.moveRight();
        }
    }
    public void moveRightActive() {
        currentOne.moveRight();
        if (outOFBounds() || coincide()) {
            currentOne.moveLeft();
        }
    }
    public boolean coincide() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Tetris");
        Tetris panel = new Tetris();
        jFrame.add(panel);
        jFrame.setVisible(true);
        jFrame.setSize(800, 900);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.start();
    }
}
