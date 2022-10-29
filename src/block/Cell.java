package block;
import java.awt.image.BufferedImage;
import java.util.Objects;
public class Cell {
    private int row;
    private int col;
    private BufferedImage image;
    public Cell() {
    }
    public Cell(int row, int col, BufferedImage image) {
        this.row = row;
        this.col = col;
        this.image = image;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public BufferedImage getImage() {
        return image;
    }
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", col=" + col +
                ", image=" + image +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cell)) {
            return false;
        }
        Cell cell = (Cell) o;
        return getRow() == cell.getRow() &&
                getCol() == cell.getCol() &&
                Objects.equals(getImage(), cell.getImage());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol(), getImage());
    }
    public void left(){
        col--;
    }
    public void right(){
        col++;
    }
    public void down(){
        row++;
    }
}
