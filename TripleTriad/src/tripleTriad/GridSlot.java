package tripleTriad;

import java.awt.Rectangle;

public class GridSlot {
	
	public int col, row;
	public int x, y;
	private int gridPosition;
	private Rectangle gridBlock;
	public Card card;
	public boolean isCardPlaced;
	public static final int SLOT_X = 226, SLOT_Y = 232;
	public static final int X_OFFSET = 360, Y_OFFSET = 2;
	
	public GridSlot(int col, int row) {
		this.col = col;
		this.row = row;
		setX(col);
		setY(row);
		setPosition();
		this.gridBlock = new Rectangle(getX(), getY(), SLOT_X, SLOT_Y);
		isCardPlaced = false;
	}
	
	public GridSlot(GridSlot gridSlot) {
		//this.col = gridSlot.col;
		//this.row = gridSlot.row;
		this(gridSlot.col, gridSlot.row);
		setX(this.col);
		setY(this.row);
		setPosition();
		this.gridBlock = new Rectangle(this.getX(), this.getY(), SLOT_X, SLOT_Y);
		this.isCardPlaced = gridSlot.isCardPlaced;
	}
	
	
	public void setX(int col) {
		this.x = col * SLOT_X + X_OFFSET;
	}
	
	public void setY(int row) {
		this.y = row * SLOT_Y + Y_OFFSET;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setCard(Card card) {
		this.card = card;
		isCardPlaced = true;
	}
	
	public Card getCard() {
		return this.card;
	}
	
	public Rectangle getRect() {
		return this.gridBlock;
	}
	
	public void setPosition() {
		switch(this.row) {
		case 0:
			this.gridPosition = 1 + this.col;
			break;
		case 1:
			this.gridPosition = 4 + this.col;
			break;
		case 2:
			this.gridPosition = 7 + this.col;
			break;
		}
	}
	public int getPosition() {
		return this.gridPosition;
	}
	

}
