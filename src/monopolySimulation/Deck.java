package monopolySimulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * Represents one of two decks in monopoly: a deck of community chest cards or
 * a deck of chance cards. Contains methods to manipulate cards inside of a deck
 * using the stack data structure from algs4.
 * 
 * @author Noah Ewell
 */
public class Deck {
	
	// Declare fields
	String deckType;
	Stack<Card> drawPile = new Stack<>();
	Stack<Card> discardPile = new Stack<>();
	
	/**
	 * Constructor checks the deckType to see if it's a community chest deck or a chance
	 * card deck. Cards are read in from different .csv files depending on that, then
	 * shuffled and put into the draw pile. The discard pile is initialized as an empty
	 * stack. Throws an exception if the deckType isn't "Chance" or "Community Chest".
	 * 
	 * @param deckType		Either "Chance" or "Community Chest"
	 * @param isDrawPile	Either is a draw pile (true) or a discard pile (false)
	 * @throws Exception	throws an exception if the deck type isn't valid.
	 */
	public Deck(String deckType) throws Exception {

		List<Card> cardList = new ArrayList<>();
		
		if (deckType.equals("Chance")) {
			cardList = getCards("src/monopolySimulation/Resources/chanceCards.csv");
		}
		else if (deckType.equals("Community Chest")) {
			cardList = getCards("src/monopolySimulation/Resources/communityChestCards.csv");
		}
		else {
			throw new Exception("The deck type must be 'Chance' or 'Community Chest'");
		}
		
		Collections.shuffle(cardList);
		
		drawPile = createDeck(cardList);
		
		this.deckType = deckType;
		
	}

	/**
	 * @return the drawPile
	 */
	public Stack<Card> getDrawPile() {
		return drawPile;
	}

	/**
	 * @return the discardPile
	 */
	public Stack<Card> getDiscardPile() {
		return discardPile;
	}
	
	/**
	 * @param drawPile the drawPile to set
	 */
	public void setDrawPile(Stack<Card> drawPile) {
		this.drawPile = drawPile;
	}

	/**
	 * @param discardPile the discardPile to set
	 */
	public void setDiscardPile(Stack<Card> discardPile) {
		this.discardPile = discardPile;
	}

	@Override
	public String toString() {
		return deckType + " Cards: \nDraw Pile: \n"+ drawPile + " \nDiscard Pile: \n" + discardPile;
	}

	/**
	 * Creates a deck as a stack from an array of cards.
	 */
	private Stack<Card> createDeck(List<Card> cardList) {
		Stack<Card> deck = new Stack<>();
		for (Card c : cardList) {
			deck.push(c);
		}
		return deck;
	}
	
	/**
	 * Draws the top card of the deck and adds it to the discard pile.
	 * Checks if the card is the "Get Out of Jail Free" card in which case it
	 * is not added to the discardpile.
	 * 
	 * @return the top card in the deck
	 * @throws Exception if the user tries shuffling with no cards
	 */
	public static Card drawCard(Stack<Card> drawPile, Stack<Card> discardPile) throws Exception {
	    if (drawPile.isEmpty()) {
	        if (discardPile.isEmpty()) {
	            throw new Exception("No cards left to draw");
	        }
	        drawPile = shuffleDeck(drawPile, discardPile);
	    }
	    Card crd = drawPile.pop();
	    if (!crd.getMovesToName().equals("Get Out of Jail Free")) {
	    	discardPile.push(crd);
	    }
	    return crd;
	}
	
	/**
	 * Adds the jailcard back to the discard pile once it has been used.
	 * 
	 * @param jailCard		The "Get Out of Jail Free" Card
	 * @param discardPile	The discard pile of either deck
	 */
	public static void putJailCardBack(Card jailCard, Stack<Card> discardPile) {
		discardPile.push(jailCard);
	}
	
	/**
	 * Shuffles a stack of cards. Throws an exception if an empty stack of cards
	 * is passed. Should only be called from drawCard.
	 * 
	 * @param discarded		A stack of unshuffled cards
	 * @return 				A deck of shuffled cards
	 * @throws Exception	If the user tries shuffling with no cards.
	 */
	private static Stack<Card> shuffleDeck(Stack<Card> drawPile, Stack<Card> discarded) throws Exception {
		if (discarded.isEmpty()) {
			throw new Exception("You need cards to shuffle!");
		}
		
		List<Card> cardList = new ArrayList<>();
		
		while (!discarded.isEmpty()) {
			cardList.add(discarded.pop());
		}
		
		Collections.shuffle(cardList);
		
		for (Card c : cardList) {
			drawPile.push(c);
		}
		
		return drawPile;
	}
	
	/**
	 * Reads in cards from a .csv
	 * 
	 * @param fileName			the filepath with the cards
	 * @return Ride[] rides		an array of cards.
	 */
	private static List<Card> getCards(String fileName) {
		List<Card> cardList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			reader.readLine();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				cardList.add(new Card(Integer.parseInt(tokens[0]), Boolean.parseBoolean(tokens[1]), 
						tokens[2], Integer.parseInt(tokens[3])));
			}
		} catch (IOException e) {
			System.out.println("A problem occured reading in the cards.");
			e.printStackTrace();
		}
		return cardList;
	}
	
	/*
	 * Test Driver
	 */
	public static void main(String[] args) throws Exception {
		StdOut.println("TESTING Deck.java");
		StdOut.println("-----------------");
		StdOut.println();
		
		Deck chance = new Deck("Chance");
		StdOut.println(chance.toString());
		StdOut.println();
		
		StdOut.println("After drawing 20 cards, triggering a Reshuffle:");
		StdOut.println("-----------------------------------------------");
		for (int i = 0; i < 20; i++) {
			Deck.drawCard(chance.drawPile, chance.discardPile);
		}
		StdOut.println(chance.toString());
		StdOut.println();
		
	}
	
}