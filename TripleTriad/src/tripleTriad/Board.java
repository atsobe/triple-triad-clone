package tripleTriad;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	private int turns = 0;
	private String winner;
	private boolean gameOver = false;

	public ArrayList<GridSlot> gridSlots = new ArrayList<>();
	public ArrayList<GridSlot> activeSlots = new ArrayList<>();
	public GridSlot containingSlot;

	// DECKS
	public Deck allCards = new Deck();	// all initialized cards
	public ArrayList<Card> cardsInPlay = new ArrayList<>();
	public Deck deckOne = new Deck(1);
	public Deck deckTwo = new Deck(2);
	Card prevCard;	// previous card picked up by player
	Card activeCard;	// the active card being used during current turn.
	public Card.Color currentColor;
	public Deck activeDeck;


	public Board() {
		//preCol = col;
		//preRow = row;
		//image = this.getImage("/board/Board Tile");
		image = this.getImage("/board/triple-triad-board");
		setBoardGrid();

		// Setting up cards and each player's deck
		this.setCards();
		this.setDecks();
		this.setRandomPlayer();
	}


	public Board(Board board){
		this.image = board.getImage("/board/triple-triad-board");
		setBoardGrid();

		this.turns = board.turns;
		this.gameOver = board.gameOver;

		for(GridSlot slot: board.getGridSlots()){
			this.gridSlots.add(new GridSlot(slot));
		}
		for(GridSlot slot: board.activeSlots){
			this.activeSlots.add(new GridSlot(slot));
		}
		for(Card card: board.cardsInPlay){
			this.cardsInPlay.add(new Card(card));
		}

		// DECKS
		this.allCards = new Deck(board.allCards);
		deckOne = new Deck(board.deckOne);
		deckTwo = new Deck(board.deckTwo);
		prevCard = new Card(board.prevCard);
		activeCard = new Card(board.activeCard);
		currentColor = board.currentColor;
		activeDeck = new Deck(board.activeDeck);
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
	
	
	
	public void drawBoardBase(Graphics2D g2) {
		g2.drawImage(image, x, y, BOARD_WIDTH, BOARD_HEIGHT, null);
	}

	public void drawBoardCards(Graphics2D g2){
		for(Card cardOne: this.deckOne.getCards()) {
			cardOne.draw(g2);
		}

		for(Card cardTwo: this.deckTwo.getCards()) {
			cardTwo.draw(g2);
		}

		for(Card cardInPlay: this.cardsInPlay) {
			cardInPlay.draw(g2);
		}
	}

	public void playCard(Card playedCard, GridSlot slot) {

		Card copyCard = new Card(playedCard);
		copyCard.moveCardTo(slot);
		slot.setCard(copyCard);
		this.cardsInPlay.add(copyCard);
		this.activeSlots.add(slot);
		this.activeDeck.removeCard(playedCard);

		checkCardCapture(slot);
		changePlayer();
	}

	private void setCards() {

		Card chubbyChoco = new Card("Chubby Chocobo", 8, Card.CardRank.Nine, Card.CardRank.Four, Card.CardRank.Four, Card.CardRank.Eight, "chubby-chocobo", 90, 20);
		Card squall = new Card("Squall", 10, Card.CardRank.Nine, Card.CardRank.Four, Card.CardRank.Ten, Card.CardRank.Six, "squall", 90, 140);
		Card quistis = new Card("Quistis", 10, Card.CardRank.Two, Card.CardRank.Six, Card.CardRank.Nine, Card.CardRank.Ten, "quistis", 90, 260);
		Card diablos = new Card("Diablos", 9, Card.CardRank.Three, Card.CardRank.Ten, Card.CardRank.Five, Card.CardRank.Eight, "diablos", 90, 380);
		Card pupu = new Card("PuPu", 5, Card.CardRank.One, Card.CardRank.Ten, Card.CardRank.Three, Card.CardRank.Two, "pupu", 90, 500);

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

	public boolean isGameOver(){
		if(turns == 9){
			gameOver = true;
		}
		return gameOver;
	}

	public String findWinner() {
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
		else {
			winner = "Draw";	//No winner
		}
		return winner;
	}

	public void resetGame() {
		for(GridSlot gridSlot: activeSlots) {
			gridSlot.isCardPlaced = false;
		}
		activeSlots.clear();
		deckOne.resetDeck();
		deckTwo.resetDeck();
		cardsInPlay.clear();
		allCards.resetDeck();
		setCards();
		setDecks();
		gameOver = false;
		turns = 0;
		winner = null;
		setRandomPlayer();
	}


	private void checkCardCapture(GridSlot containingSlot) {
		if(containingSlot != null) {
			switch(containingSlot.getPosition()) {
				case 1:
					for(GridSlot activeSlot: activeSlots) {
						if(activeSlot.getPosition() == 2 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(Card.CardSide.LEFT)) {
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 4 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(Card.CardSide.TOP)) {
								activeSlot.card.flipCardColor();
							}
						}
					}
					break;
				case 2:
					for(GridSlot activeSlot: activeSlots) {
						if(activeSlot.getPosition() == 1 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.LEFT) > activeSlot.getCard().getCardRankValue(Card.CardSide.RIGHT)) {
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 3 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(Card.CardSide.LEFT)) {
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(Card.CardSide.TOP)) {
								activeSlot.card.flipCardColor();
							}
						}
					}
					break;
				case 3:
					for(GridSlot activeSlot: activeSlots) {
						if(activeSlot.getPosition() == 2 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.LEFT) > activeSlot.getCard().getCardRankValue(Card.CardSide.RIGHT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 6 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(Card.CardSide.TOP)) {
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
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.TOP) > activeSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(Card.CardSide.LEFT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 7 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(Card.CardSide.TOP)) {
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
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.TOP) > activeSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 4 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.LEFT) > activeSlot.getCard().getCardRankValue(Card.CardSide.RIGHT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 6 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(Card.CardSide.LEFT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 8 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(Card.CardSide.TOP)) {
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
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.TOP) > activeSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.LEFT) > activeSlot.getCard().getCardRankValue(Card.CardSide.RIGHT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 9 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM) > activeSlot.getCard().getCardRankValue(Card.CardSide.TOP)) {
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
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.TOP) > activeSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 8 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(Card.CardSide.LEFT)) {
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
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.LEFT) > activeSlot.getCard().getCardRankValue(Card.CardSide.RIGHT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 5 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.TOP) > activeSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 9 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.RIGHT) > activeSlot.getCard().getCardRankValue(Card.CardSide.LEFT)) {
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
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.TOP) > activeSlot.getCard().getCardRankValue(Card.CardSide.BOTTOM)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
						if(activeSlot.getPosition() == 8 && activeSlot.getCard() != null
								&& activeSlot.getCard().getCardColor() != containingSlot.getCard().getCardColor()) {
							if(containingSlot.getCard().getCardRankValue(Card.CardSide.LEFT) > activeSlot.getCard().getCardRankValue(Card.CardSide.RIGHT)) {
								//activeSlot.card.flipCardColor(activeSlot.getCard().getCardColor());
								activeSlot.card.flipCardColor();
							}
						}
					}
					break;
			}
		}
	}

	//	Get all possible moves for active player
	public List<Move> getMoves(){
		List<Move> possibleMoves = new ArrayList<>();
		List<Integer> activeGridPos = new ArrayList<>();
		for(GridSlot slot: activeSlots){
			activeGridPos.add(slot.getPosition());
		}

		List<GridSlot> possibleSlots = new ArrayList<>();
		for (GridSlot slot: gridSlots){
			possibleSlots.add(new GridSlot(slot));
		}
		possibleSlots.removeIf(slot -> activeGridPos.contains(slot.getPosition()));

		for(Card card: activeDeck.getCards()){
			for(GridSlot slot: possibleSlots){
				possibleMoves.add(new Move(slot, card));
			}
		}
		return possibleMoves;
	}

	//	Return AI cards - Player cards
	public int getEnemyScore(){
		int blueCards = 0;
        List<Card> currentCards = new ArrayList<>(cardsInPlay);
		currentCards.addAll(deckOne.getCards());
		currentCards.addAll(deckTwo.getCards());

		for(Card card: currentCards){
			if(card.getCardColor() == Card.Color.BLUE) {
				blueCards++;
			}
		}
		int redCards = 10 - blueCards;

		return redCards - blueCards;
	}
}
