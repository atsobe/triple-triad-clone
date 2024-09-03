package tripleTriad;

import java.util.ArrayList;

import tripleTriad.Card.Color;

public class Deck {
	
	public int player;
	public Card.Color deckColor;
	
	private ArrayList<Card> playerCards = new ArrayList<>();
	
	public Deck() {
		this.player = 1;
		this.deckColor = Color.BLUE;
	}
	
	public Deck(int player) {
		this.player = player;
		
		if(player == 1) {
			this.deckColor = Color.BLUE;
		}
		else if(player == 2) {
			this.deckColor = Color.RED;
		}
	}
	
	public ArrayList<Card> getCards(){
		return this.playerCards;
	}
	
	public int getPlayer() {
		return this.player;
	}
	
	public Card.Color getColor(){
		return this.deckColor;
	}
	
	public void addCard(Card card) {
		Card copyCard = new Card(card);
		
		if(this.player == 1) {
			copyCard.setCardColor(Color.BLUE);
			copyCard.setStartPosition(90, 20 + (120 * this.playerCards.size()));
			this.playerCards.add(copyCard);
		}
		if(this.player == 2) {
			copyCard.setCardColor(Color.RED);
			copyCard.setStartPosition(1126, 20 + (120 * this.playerCards.size()));
			this.playerCards.add(copyCard);
		}
		
	}
	
	public void removeCard(Card card) {
		this.playerCards.remove(card);
	}
	
	public void resetDeck() {
		this.playerCards.removeAll(playerCards);
	}
	
	

}
