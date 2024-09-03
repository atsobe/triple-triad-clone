package tripleTriad;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;

import javax.imageio.ImageIO;

public class Card {
	private EnumMap<CardSide, CardRank> sideRanks = new EnumMap<>(CardSide.class);
	public Color cardColor;
	private final String cardName;
	//public static final int CARD_WIDTH = 163;
	//public static final int CARD_HEIGHT = 210;
	public static final int CARD_WIDTH = 181;	//181
	public static final int CARD_HEIGHT = 186;	//186
	public BufferedImage cardImage;
	private final int cardLevel;
	private int cardX , cardY;
	private int preCardX, preCardY;
	private Rectangle boundRect;
	private int boundX, boundY;
	private final int boundWidth = CARD_WIDTH - 18, boundHeight = CARD_HEIGHT - 18;
	public String fileName;
	public boolean inPlay;
	public int slotPosition;
	
	
	public Card(String cardName, int cardLevel, CardRank left, CardRank right, CardRank top, CardRank bottom, String fileName, int x, int y) {
		 this.preCardX = x;
		 this.preCardY = y;
		 this.cardName = cardName;
		 this.cardLevel = cardLevel;
		 this.inPlay = false;
		 this.fileName = fileName;
		 setX(x);
		 setY(y);
		 setBoundRect();
		 setCardColor(Color.BLUE);
		 setCardImage(fileName);
		 
		 sideRanks.put(CardSide.LEFT, left);
		 sideRanks.put(CardSide.RIGHT, right);
		 sideRanks.put(CardSide.TOP, top);
		 sideRanks.put(CardSide.BOTTOM, bottom);
	}
	
	public Card(Card card) {
		 this.preCardX = card.getX();
		 this.preCardY = card.getY();
		 this.cardName = card.getCardName();
		 this.cardLevel = card.getCardLevel();
		 this.inPlay = false;
		 this.fileName = card.fileName;
		 this.slotPosition = 0;
		 
		 setX(card.getX());
		 setY(card.getY());
		 setBoundRect();
		 setCardColor(card.getCardColor());
		 setCardImage(this.fileName);
		 
		 sideRanks.put(CardSide.LEFT, card.getCardRank(CardSide.LEFT));
		 sideRanks.put(CardSide.RIGHT, card.getCardRank(CardSide.RIGHT));
		 sideRanks.put(CardSide.TOP, card.getCardRank(CardSide.TOP));
		 sideRanks.put(CardSide.BOTTOM, card.getCardRank(CardSide.BOTTOM));
	}
	
	enum Color {
		RED, BLUE;
	}
	
	enum CardSide {
		LEFT, RIGHT, TOP, BOTTOM;
	}
		
	enum CardRank {
		One(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), Ten(10);
		private int value;
		private CardRank(int value) {
			this.value = value;
		}
		public int getRankValue() {
			return value;
		}
	}
	
	public void setX(int x) {
		this.cardX = x;
		this.boundX = cardX + (CARD_WIDTH - boundWidth) / 2;
	}
	
	public void setY(int y) {
		this.cardY = y;
		this.boundY = cardY + (CARD_HEIGHT - boundHeight) / 2;
	}
	
	public int getX() {
		return this.cardX;
	}
	
	public int getY() {
		return this.cardY;
	}
	
	public void setStartPosition(int x, int y) {
		this.preCardX = x;
		setX(x);
		
		this.preCardY = y;
		setY(y);
	}
	
	public void setSlotPosition(int position) {
		this.slotPosition = position;
	}
	
	public void moveCardTo(GridSlot gridSlot) {
		this.setX(gridSlot.getX() + (GridSlot.SLOT_X - Card.CARD_WIDTH) / 2);
		this.setY(gridSlot.getY() + (GridSlot.SLOT_Y - Card.CARD_HEIGHT) / 2);
		this.setBoundRect();
	}
	
	public void moveCardBack() {
		this.setX(this.preCardX);
		this.setY(this.preCardY);
		this.setBoundRect();
	}
	
	public void setBoundRect() {
		//boundWidth = CARD_WIDTH - 10;
		//boundHeight = CARD_HEIGHT - 12;
		
		//boundX = this.cardX + (CARD_WIDTH - boundWidth) / 2;
		//boundY = this.cardY + (CARD_HEIGHT - boundHeight) / 2;
		
		this.boundRect = new Rectangle(this.boundX, this.boundY, boundWidth, boundHeight);
	}
	
	public void setInPlay(boolean inPlay) {
		this.inPlay = inPlay;
	}
	
	public Rectangle getBoundRect() {
		return this.boundRect;
	}

	public CardRank getCardRank(CardSide cardSide) {
		return this.sideRanks.get(cardSide);
	}
	
	public int getCardRankValue(CardSide cardSide) {
		return this.getCardRank(cardSide).getRankValue();
	}
	
	public String getCardName() {
		return this.cardName;
	}
	
	public int getCardLevel() {
		return this.cardLevel;
	}
	
	public void setCardColor(Color color) {
		this.cardColor = color;
		setCardImage(this.fileName);
	}
	
	public Color getCardColor() {
		return this.cardColor;
	}
	
	public void flipCardColor(Color color) {
		if(color == Color.BLUE) {
			setCardColor(Color.RED);
		}
		else if(color == Color.RED) {
			setCardColor(Color.BLUE);
		}
	}
	
	public void flipCardColor() {
		if(this.cardColor == Color.BLUE) {
			setCardColor(Color.RED);
		}
		else if(this.cardColor == Color.RED) {
			setCardColor(Color.BLUE);
		}
	}
	
	public void setCardImage(String fileName) {
		if(this.getCardColor() == Color.BLUE) {
			 this.cardImage = this.getImage("/cards/" + fileName + "-blue");
		 }
		 if(this.getCardColor() == Color.RED) {
			 this.cardImage = this.getImage("/cards/" + fileName + "-red");
		 }
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
	
	public void draw(Graphics2D g2) {
		g2.drawImage(this.cardImage, cardX, cardY, CARD_WIDTH, CARD_HEIGHT, null);
		
	}
	
	@Override
	public String toString() {
		String rank;
		String side;
		String cardString = "NAME: " + this.getCardName() + "\n CARD LEVEL: " + this.getCardLevel() + "\n ";
		
		for (EnumMap.Entry<CardSide, CardRank> entry : sideRanks.entrySet()) {
            side = "" + entry.getKey();
            if(entry.getValue().getRankValue() == 10) {
            	rank = "A";
            }
            else {
            	rank = "" + entry.getValue().getRankValue();
            }
            cardString += side + ": " + rank + "\n ";
        }
		return cardString;
	}
	
	
}
