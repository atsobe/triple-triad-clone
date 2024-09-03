package tripleTriad;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;


import javax.swing.JLabel;
import javax.swing.JPanel;

import tripleTriad.Card.CardRank;
import tripleTriad.Card.CardSide;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable {
	
	public static final int WIDTH = 1400;
	public static final int HEIGHT = 933;
	final int TARGET_FPS = 60;
	final int TARGET_UPS = 60;	// target logic ticks per second
	
	public int valueFPS;
	public boolean isCollision;
	public GridSlot containingSlot;
	
	Thread gameThread;
	Board board = new Board();
	
	public Mouse mouse;
	
	public ResetButton resetButton;
	
	// DECKS
	public Deck allCards = new Deck();	// all initialized cards
	public ArrayList<Card> cardsInPlay = new ArrayList<>();
	public Deck deckOne = new Deck(1);
	public Deck deckTwo = new Deck(2);
	Card prevCard;	// previous card picked up by player
	Card activeCard;	// the active card being used during current turn.
	
	public Card.Color currentColor;
	public Deck activeDeck;
	
	// GRIDS
	public ArrayList<GridSlot> gridSlots = new ArrayList<>();
	public ArrayList<GridSlot> activeSlots = new ArrayList<>();
	
	JLabel turnLabel;
	private int turns = 0;
	private String winner;
	private boolean gameOver = false;
	
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		
		// Adding mouse to game
		mouse = new Mouse(this);
		this.addMouseMotionListener(mouse);
		this.addMouseListener(mouse);
		
		// Adding reset button to game panel
		resetButton = new ResetButton(this);
		add(resetButton, BorderLayout.SOUTH);
		
		// Setting up playing grid
		this.gridSlots = board.gridSlots;
		this.activeSlots = board.activeSlots;
		
		// Setting up cards and each player's deck
		this.setCards();
		this.setDecks();
		this.setRandomPlayer();
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	private void update() {
		
		if(!gameOver) {
			// If game is NOT over
			if(mouse.pressed == true) {
				// If mouse is pressed
				if(activeCard == null) {
					// If the activeCard is null, check if you can pick up a card
					for(Card card : activeDeck.getCards()) {
						// If the card is in same position as mouse, pick up
						if( (mouse.x >= card.getX() && mouse.x < card.getX() + Card.CARD_WIDTH)
							&& (mouse.y >= card.getY() && mouse.y < card.getY() + Card.CARD_HEIGHT) ) {
							activeCard = card;
							prevCard = card;
							//activeCard.setInPlay(false);
							/* for(GridSlot gridSlot: gridSlots) {
								if(gridSlot.getCard() == activeCard) {
									gridSlot.setCard(null);
									gridSlot.isCardPlaced = false;
								} 
							} */
						}
					}
				}
				else {
					// If there is active card
					containingSlot = findContainingSlot(activeCard, gridSlots);
					// If player is holding a card, simulate move
					simulate();
				}
			}
			
			if(mouse.pressed == false) {
				//	If mouse is not pressed
				if(activeCard != null) {
					if(isCollision == true) {
						//	If collision is detected with the active card
						containingSlot = findContainingSlot(activeCard, gridSlots);
						if(containingSlot.isCardPlaced == false) {
							Card copyCard = new Card(activeCard);
							
							copyCard.moveCardTo(containingSlot);
							containingSlot.setCard(copyCard);
							cardsInPlay.add(copyCard);
							activeSlots.add(containingSlot);
							activeDeck.removeCard(activeCard);
							
							checkCardCapture(containingSlot);
							changePlayer();
						}
						else {
							activeCard.moveCardBack();
						}
	
					}
					else {
						activeCard.moveCardBack();
					}
					activeCard = null;
				}
			}
		}
		
		if(turns == 9) {
			this.gameOver = true;
			this.winner = findWinner();
		}
	}
	
	private void simulate() {
		this.isCollision = false;	//reset the collision detection to be false every time active card is simulated
		
		//If card is being held, update its position
		activeCard.setX(mouse.x - Card.CARD_WIDTH / 2);
		activeCard.setY(mouse.y - Card.CARD_HEIGHT / 2);
		activeCard.setBoundRect();	//set active card's bounding rectangle every time its  new position is set
		
		for(GridSlot gridSlot : gridSlots) {
			if(isCollision(activeCard, gridSlot) == true)	{
				this.isCollision = isCollision(activeCard, gridSlot);
				break;	// to stop iterating through arrayList, which would keep updating isCollision variable 
			}
		}
	}
	
	private void setCards() {
		
		Card chubbyChoco = new Card("Chubby Chocobo", 8, CardRank.Nine, CardRank.Four, CardRank.Four, CardRank.Eight, "chubby-chocobo", 90, 20);
		Card squall = new Card("Squall", 10, CardRank.Nine, CardRank.Four, CardRank.Ten, CardRank.Six, "squall", 90, 140);
		Card quistis = new Card("Quistis", 10, CardRank.Two, CardRank.Six, CardRank.Nine, CardRank.Ten, "quistis", 90, 260);
		Card diablos = new Card("Diablos", 9, CardRank.Three, CardRank.Ten, CardRank.Five, CardRank.Eight, "diablos", 90, 380);
		Card pupu = new Card("PuPu", 5, CardRank.One, CardRank.Ten, CardRank.Three, CardRank.Two, "pupu", 90, 500);
		
		this.allCards.addCard(chubbyChoco);
		this.allCards.addCard(squall);
		this.allCards.addCard(quistis);
		this.allCards.addCard(diablos);
		this.allCards.addCard(pupu);
	}
	
	private void setDecks() {
		//Setting up deck for player one
		for(Card card: allCards.getCards()) {
			deckOne.addCard(card);
		}
		
		//Setting up deck for player two
		for(Card card: allCards.getCards()) {
			deckTwo.addCard(card);
		}
	}
	
	private void setRandomPlayer() {
		int random = (int) (Math.random() * 100);
		
		if(random <= 49) {
			this.activeDeck = deckOne;
			this.currentColor = Card.Color.BLUE;
		}
		else {
			this.activeDeck = deckTwo;
			this.currentColor = Card.Color.RED;
		}
	}
	
	private void changePlayer() {
		isCollision = false;
		if(activeDeck == deckOne) {
			activeDeck = deckTwo;
			currentColor = Card.Color.RED;
		}
		else {
			activeDeck = deckOne;
			currentColor = Card.Color.BLUE;
		}
		turns++;
	}
	
	private String findWinner() {
		int blueCards = 0;
		String winner = "";
		for(Card cardInPlay: cardsInPlay) {
			if(cardInPlay.getCardColor() == Card.Color.BLUE) {
				blueCards++;
			}
		}
		
		for(Card card: deckOne.getCards()) {
			if(card.getCardColor() == Card.Color.BLUE) {
				blueCards++;
			}
		}
		
		for(Card card: deckTwo.getCards()) {
			if(card.getCardColor() == Card.Color.BLUE) {
				blueCards++;
			}
		}
		
		if(blueCards > 5) {
			winner = "Blue";	//Blue Wins
		}
		else if(blueCards  < 5) {
			winner = "Red";	//Red Wins
		}
		else if(blueCards == 5) {
			winner = "Draw";	//No winner
		}
		return winner;
	}
	
	public void resetGame() {
		for(GridSlot gridSlot: activeSlots) {
			gridSlot.isCardPlaced = false;
		}
		
		deckOne.resetDeck();
		deckTwo.resetDeck();
		cardsInPlay.removeAll(cardsInPlay);
		allCards.resetDeck();
		setCards();
		setDecks();
		gameOver = false;
		turns = 0;
		winner = null;
		//currentColor = Card.Color.BLUE;
		//activeDeck = deckOne;
		setRandomPlayer();
	}
	
	private boolean isCollision(Card card, GridSlot gridSlot) {
		return card.getBoundRect().intersects(gridSlot.getRect());
	}
	
	private GridSlot findContainingSlot(Card card, ArrayList<GridSlot> gridSlots) {
		double maxIntersectArea = 0;
		GridSlot containingSlot = null;
		
		for(GridSlot gridSlot : gridSlots) {
			if(isCollision(card, gridSlot)) {
				Rectangle intersection = card.getBoundRect().intersection(gridSlot.getRect());
				double intersectArea = intersection.getWidth() * intersection.getHeight();
				
				if (intersectArea > maxIntersectArea) {
	                maxIntersectArea = intersectArea;
	                containingSlot = gridSlot;
	            }
			}
		}
		return containingSlot;
	}
	
	private void checkCardCapture(GridSlot containingSlot) {
		if(containingSlot != null) {
			switch(containingSlot.getPosition()) {
			case 1:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 2 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(CardSide.LEFT)) {
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 4 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(CardSide.TOP)) {
							activeSlot.card.flipCardColor();
						}
					}
				}
				break;
			case 2:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 1 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.LEFT) > activeSlot.getCard().getCardRankValue(CardSide.RIGHT)) {
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 3 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(CardSide.LEFT)) {
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null 
							&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(CardSide.TOP)) {
								activeSlot.card.flipCardColor();
							}
						}
				}
				break;
			case 3:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 2 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.LEFT) > activeSlot.getCard().getCardRankValue(CardSide.RIGHT)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 6 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(CardSide.TOP)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
				}
				break;
			case 4:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 1 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.TOP) > activeSlot.getCard().getCardRankValue(CardSide.BOTTOM)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(CardSide.LEFT)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 7 && activeSlot.getCard() != null 
							&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(CardSide.TOP)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
				}
				break;
			case 5:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 2 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.TOP) > activeSlot.getCard().getCardRankValue(CardSide.BOTTOM)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 4 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.LEFT) > activeSlot.getCard().getCardRankValue(CardSide.RIGHT)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 6 && activeSlot.getCard() != null 
							&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(CardSide.LEFT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
					if(activeSlot.getPosition() == 8 && activeSlot.getCard() != null 
							&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(CardSide.TOP)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
				}
				break;
			case 6:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 3 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.TOP) > activeSlot.getCard().getCardRankValue(CardSide.BOTTOM)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.LEFT) > activeSlot.getCard().getCardRankValue(CardSide.RIGHT)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 9 && activeSlot.getCard() != null 
							&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(CardSide.TOP)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
				}
				break;
			case 7:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 4 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.TOP) > activeSlot.getCard().getCardRankValue(CardSide.BOTTOM)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 8 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(CardSide.LEFT)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
				}
				break;
			case 8:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 7 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.LEFT) > activeSlot.getCard().getCardRankValue(CardSide.RIGHT)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.TOP) > activeSlot.getCard().getCardRankValue(CardSide.BOTTOM)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 9 && activeSlot.getCard() != null 
							&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(CardSide.LEFT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
				}
				break;
			case 9:
				for(GridSlot activeSlot: activeSlots) {
					if(activeSlot.getPosition() == 6 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.TOP) > activeSlot.getCard().getCardRankValue(CardSide.BOTTOM)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
					if(activeSlot.getPosition() == 8 && activeSlot.getCard() != null 
						&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
						if(containingSlot.getCard().getCardRankValue(CardSide.LEFT) > activeSlot.getCard().getCardRankValue(CardSide.RIGHT)) {
							//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
							activeSlot.card.flipCardColor();
						}
					}
				}
				break;
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		// Drawing board
		board.draw(g2);
		
		// Drawing containing slot
		if(containingSlot != null && isCollision == true && mouse.pressed == true && prevCard.getCardColor() == currentColor) {
			g2.setColor(Color.white);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
			g2.fillRect(containingSlot.getX(), containingSlot.getY(), GridSlot.SLOT_X, GridSlot.SLOT_Y);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			g2.draw(containingSlot.getRect());
		}
		
		//	Drawing cards to screen
		for(Card cardOne: this.deckOne.getCards()) {
			cardOne.draw(g2);
		}
		
		for(Card cardTwo: this.deckTwo.getCards()) {
			cardTwo.draw(g2);
		}
		
		for(Card cardInPlay: this.cardsInPlay) {
			cardInPlay.draw(g2);
		}
		
		//Drawing mouse
		//mouse.draw(g);
		
		// Displaying active card's name
		if(activeCard != null) {
			Font cardInfoFont = new Font("Arial", Font.PLAIN, 30);
			g2.setFont(cardInfoFont);
			g2.setColor(Color.white);
			g2.drawString(activeCard.getCardName() + " is being held.", 200, 750);
		}
		
		// STATUS MESSAGES
		//g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font turnFont = new Font("Arial", Font.PLAIN, 30);
		g2.setFont(turnFont);
		g2.setColor(Color.white);
		
		// Displaying Turn Order
		if(!gameOver) {
			if(activeDeck.getPlayer() == 1) {
				g2.drawString("Blue's turn", 1200, 750);
			}
			else {
				g2.drawString("Red's turn", 1200, 750);
			}
		}
		
		// Displaying Winner when game is over
		if(gameOver) {
			Font gameOverFont = new Font("Arial", Font.PLAIN, 90);
			g2.setFont(gameOverFont);
			g2.setColor(Color.white);
			if(winner == "Draw") {
				g2.drawString(winner + "!", 500, 850);
			}
			else {
				g2.drawString(winner + " Wins!", 500, 850);
			}
			
		}
		
	}

	@Override
	public void run() {
		
		long lastLoopTime = System.nanoTime();
		final double TIME_F = 1000000000 / TARGET_FPS;
		final double TIME_U = 1000000000 / TARGET_UPS;
		double deltaU = 0, deltaF = 0;
		long currentTime;
		long currentLoopTime;
		int ticks = 0, frames = 0;
		long timer = 0;
		int drawCount = 0;
		
		while(gameThread != null) {
			currentLoopTime = System.nanoTime();
			deltaU += (currentLoopTime - lastLoopTime) / TIME_U;
			deltaF += (currentLoopTime - lastLoopTime) / TIME_F;
			timer += (currentLoopTime - lastLoopTime);
			lastLoopTime = currentLoopTime;
			
			if(deltaU >= 1) {
				update();
				ticks++;
				deltaU--;
			}
			
			if(deltaF >= 1) {
				repaint();
				frames++;
				deltaF--;
				drawCount++;
			}
			
			if(timer >= 1000000000) {
				valueFPS = drawCount;
				drawCount = 0;
				timer = 0;
			}
			
		}  
	}

}
