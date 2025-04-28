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
	private String winner;
	
	
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
	}
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	private void update() {
		if(!board.isGameOver()) {
			// If game is NOT over
			if(mouse.pressed) {
				// If mouse is pressed
				if(activeCard == null) {
					// If the activeCard is null, check if you can pick up a card
					for(Card card : board.activeDeck.getCards()) {
						// If the card is in same position as mouse, pick up
						if( (mouse.x >= card.getX() && mouse.x < card.getX() + Card.CARD_WIDTH)
							&& (mouse.y >= card.getY() && mouse.y < card.getY() + Card.CARD_HEIGHT) ) {
							activeCard = card;
							prevCard = card;
						}
					}
				}
				else {
					// If there is active card
					containingSlot = findContainingSlot(activeCard, board.gridSlots);
					// If player is holding a card, simulate move
					simulate();
				}
			}
			if(!mouse.pressed) {
				//	If mouse is not pressed
				if(activeCard != null) {
					if(isCollision) {
						//	If collision is detected with the active card
						containingSlot = findContainingSlot(activeCard, board.gridSlots);
						if(!containingSlot.isCardPlaced) {
							board.playCard(activeCard, containingSlot);
						}
						else {
							activeCard.moveCardBack();
						}
					}
					else {
						activeCard.moveCardBack();
					}
					activeCard = null;
					containingSlot = null;
					isCollision = false;
				}
			}
		}
		else {
			this.winner = board.findWinner();
		}
	}
	
	private void simulate() {
		this.isCollision = false;	//reset the collision detection to be false every time active card is simulated
		
		//If card is being held, update its position
		activeCard.setX(mouse.x - Card.CARD_WIDTH / 2);
		activeCard.setY(mouse.y - Card.CARD_HEIGHT / 2);
		activeCard.setBoundRect();	//set active card's bounding rectangle every time its  new position is set
		
		for(GridSlot gridSlot : board.gridSlots) {
			if(isCollision(activeCard, gridSlot))	{
				this.isCollision = isCollision(activeCard, gridSlot);
				break;	// to stop iterating through arrayList, which would keep updating isCollision variable 
			}
		}
	}

	
	private boolean isCollision(Card card, GridSlot gridSlot) {
		return card.getBoundRect().intersects(gridSlot.getRect());
	}

	
	private GridSlot findContainingSlot(Card card, ArrayList<GridSlot> gridSlots) {
		double maxIntersectArea = 0;
		GridSlot outputSlot = null;
		
		for(GridSlot gridSlot : gridSlots) {
			if(isCollision(card, gridSlot)) {
				Rectangle intersection = card.getBoundRect().intersection(gridSlot.getRect());
				double intersectArea = intersection.getWidth() * intersection.getHeight();
				
				if (intersectArea > maxIntersectArea) {
	                maxIntersectArea = intersectArea;
					outputSlot = gridSlot;
	            }
			}
		}
		return outputSlot;
	}

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		// Drawing board
		board.drawBoardBase(g2);
		
		// Drawing containing slot
		if(containingSlot != null && this.isCollision && mouse.pressed && prevCard.getCardColor() == board.currentColor) {
			g2.setColor(Color.white);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
			g2.fillRect(containingSlot.getX(), containingSlot.getY(), GridSlot.SLOT_X, GridSlot.SLOT_Y);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			g2.draw(containingSlot.getRect());
		}
		
		//	Drawing cards to screen
		board.drawBoardCards(g2);
		
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
		if(!board.isGameOver()) {
			if(board.activeDeck.getPlayer() == 1) {
				g2.drawString("Blue's turn", 1200, 750);
			}
			else {
				g2.drawString("Red's turn", 1200, 750);
			}
		}
		
		// Displaying Winner when game is over
		if(board.isGameOver()) {
			Font gameOverFont = new Font("Arial", Font.PLAIN, 90);
			g2.setFont(gameOverFont);
			g2.setColor(Color.white);
			if(winner!=null && winner.equals("Draw")) {
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
