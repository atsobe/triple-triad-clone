package tripleTriad;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Board {
	
	final int MAX_COL = 0;
	final int MAX_ROW = 0;
	public static final int SQUARE_SIZE = 100;
	public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;
	public static final int BOARD_WIDTH = 1400;
	public static final int BOARD_HEIGHT = 700;
	public static final int SLOT_X = 226, SLOT_Y = 232;
	public static final int X_OFFSET = 360, Y_OFFSET = 2;
	
	public BufferedImage image;
	public int x, y;
	public int col, row, preCol, preRow;
	
	public ArrayList<GridSlot> gridSlots = new ArrayList<>();
	public ArrayList<GridSlot> activeSlots = new ArrayList<>();
	
	
	public Board() {
		//preCol = col;
		//preRow = row;
		//image = this.getImage("/board/Board Tile");
		image = this.getImage("/board/triple-triad-board");
		setBoardGrid();
	}
	
	public BufferedImage getImage(String imagePath) {
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
		} catch(IOException e){
			e.printStackTrace();
		}
		return image;
	}
	
	public int getX(int col) {
		return col * SLOT_X + X_OFFSET;
	}
	
	public int getY(int row) {
		return row * SLOT_Y + Y_OFFSET;
	}
	
	public void setBoardGrid() {
		for(row = 0; row < 3; row++) {
			for(col = 0; col < 3; col++) {
				gridSlots.add(new GridSlot(col, row));
			}
		}	
	}
	
	public ArrayList<GridSlot> getGridSlots(){
		return this.gridSlots;
	}
	
	
	
	public void draw(Graphics2D g2) {
		/*
		for(col = 0; col < 3; col++) {
			for(row = 0; row < 3; row++) {
				x = getX(col);
				y = getY(row);
				g2.drawImage(image, x, y, SQUARE_SIZE, SQUARE_SIZE, null);
			}
		}
		*/
		
		g2.drawImage(image, x, y, BOARD_WIDTH, BOARD_HEIGHT, null);
		

	}

}
