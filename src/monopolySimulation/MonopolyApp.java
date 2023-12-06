package monopolySimulation;

import java.io.File;
import java.io.PrintStream;

import edu.princeton.cs.algs4.RedBlackBST;

/**
 * Launches the application for several monopoly simulations in which visits to each property are tracked
 * over 1_000, 10_000, 100_000, and 1_000_000 turns (n) respectively. This application relies on underlying
 * data structures from algs4 to function, namely a RedBlackBST symbol table and a Stack. Simulations are
 * based on two possible strategies: A) players are immediately released from jail or B) players must try for 
 * doubles or be released after 3 attempts. Percentages are calculated for each of these visits. Results are 
 * printed to a "results.txt" file in the resources folder.
 * 
 * @author Colton Anderson + Noah Ewell + Phuon Tran + Ryley Pabst
 */
public class MonopolyApp {
	
	// Launches the application
	public static void main(String[] args) throws Exception {
		
		// Changes the printStream to a text file
		PrintStream console = System.out;
		PrintStream textfile = new PrintStream(new File("src/monopolySimulation/Resources/results.txt"));
		System.setOut(textfile);
		
		// Simulation runs 10 times for both strategies
		for (int i = 0; i < 10; i++) {
			char strategy = 'A';
			measureSimulations(i, strategy);
		}
		for (int i = 0; i < 10; i++) {
			char strategy = 'B';
			measureSimulations(i, strategy);
		}
		
		// Resets System.out to print back to the console.
		System.setOut(console);
		System.out.println("See results.txt for output");
	}

	/**
	 * Measures and prints the simulation when turns (AKA n) is 1_000, 10_000, 100_000, and 
	 * 1_000_000 based off either strategy 'A' or 'B'.
	 * 
	 * @param i				The number of times the strategy simulation has printed
	 * @param strategy		One of two rules regarding how jail works.
	 * @throws Exception	An invalid strategy is passed.
	 */
	private static void measureSimulations(int i, char strategy) throws Exception {
		int turns = 1000;
		
		System.out.println(" ".repeat(29) + "Strategy " + strategy + " Simulation #" + (i+1) + " of 10");
		System.out.println("-".repeat(89));
		
		RedBlackBST<Integer, Property> resultsST1 = runSimulation(strategy, turns);
		
		turns *= 10;
		RedBlackBST<Integer, Property> resultsST2 = runSimulation(strategy, turns);
		
		turns *= 10;
		RedBlackBST<Integer, Property> resultsST3 = runSimulation(strategy, turns);
		
		turns *= 10;
		RedBlackBST<Integer, Property> resultsST4 = runSimulation(strategy, turns);
		
		System.out.printf("%23s %14s | %14s | %14s | %14s",
						   "|", "n = 1,000", "n = 10,000", "n = 100,000", "n = 1,000,000");
		System.out.println();
		System.out.printf("%23s %6s | %5s | %6s | %5s | %6s | %5s | %6s | %5s",
							"|", "Count", "%", "Count", "%", "Count", "%", "Count", "%");
		System.out.println();
		
		for (Integer key = 0; key < resultsST1.size(); key++) {
			System.out.printf("%-21s | %6d | %-4.2f%% | %6d | %-4.2f%% | %6d | %-4.2f%% | %6d | %-4.2f%% \n",
					resultsST1.get(key).getName(), 
					resultsST1.get(key).getVisits(), (double) resultsST1.get(key).getVisits() / 1_000 * 100,
					resultsST2.get(key).getVisits(), (double) resultsST2.get(key).getVisits() / 10_000 * 100,
					resultsST3.get(key).getVisits(), (double) resultsST3.get(key).getVisits() / 100_000 * 100,
					resultsST4.get(key).getVisits(), (double) resultsST4.get(key).getVisits() / 1_000_000 * 100);
		}
		
		System.out.println("\n");
	}

	/**
	 * Runs a simulation based off one of two strategies:
	 * 	A) players are immediately released from jail.
	 * 	B) players must try for doubles or be released after 3 attempts.
	 * 
	 * @param strategy		the strategy being used
	 * @param turns			the number of turns to take
	 * @return 				a properties symbol table with the correct number of visits
	 * @throws Exception 	if an invalid strategy is entered
	 */
	private static RedBlackBST<Integer, Property> runSimulation(char strategy, int turns) throws Exception {
		
		// Initialize property components
		Property[] properties = PropertyTable.getProperties();
		RedBlackBST<Integer, Property> st = new RedBlackBST<>();			
		PropertyTable.fillSymbolTable(st, properties);
		
		// Initialize player-related components
		int currentPosition = 0;
		boolean jailCard1 = false;
		boolean jailCard2 = false;
		Card jCard1 = null;
		Card jCard2 = null;
		
		// Initialize Decks & Dice
		Deck chest = new Deck("Community Chest");
		Deck chance = new Deck("Chance");
		Dice die1 = new Dice();
		Dice die2 = new Dice();
		
		// Takes the specified number of turns.
		for (int i = 0; i < turns; i++) {
			
			// Roll dice and move
			int result1 = die1.roll();
			int result2 = die2.roll();
			int totalRoll = result1 + result2;
			currentPosition += totalRoll;
			
			// Checks and resets range if necessary
			if (currentPosition > 39) {
				currentPosition -= 40;
			}
			
			// Add visit
			st.get(currentPosition).addVisit();
			
			// Draw cards if landing on "Community Chest" or "Chance"
			if (st.get(currentPosition).getName().equals("Community Chest")) {
				currentPosition = drawCardFromChest(strategy, turns, currentPosition, 
													jailCard1, jailCard2, chest, 
													chance, die1, die2, jCard1, jCard2, st);
			}
			else if (st.get(currentPosition).getName().equals("Chance")) {
				currentPosition = drawCardFromChance(strategy, turns, currentPosition, 
													 jailCard1, jailCard2, jCard1, jCard2, chance,
													 chest, die1, die2, st);
			}
			else if (st.get(currentPosition).getName().equals("Go To Jail")) {
				triggerJail(strategy, turns, currentPosition, jailCard1, jailCard2, die1, die2, 
							chest, chance, jCard1, jCard2, st);
			}
		}
		return st;		
	}

	/**
	 * Draws a card from the community chest deck, determines if it requires player movement, and 
	 * carries out the appropriate movement.
	 * 
	 * @param strategy				the strategy being used this simulation
	 * @param turns					the number of turns
	 * @param currentPosition		the player's current position on the board
	 * @param jailCard1				whether the player has jailCard1
	 * @param jailCard2				whether the player has jailCard2
	 * @param jCard1				"Get Out of Jail Free" Card 1
	 * @param chest					the community chest deck of cards
	 * @param die1					the first dice
	 * @param die2					the second dice
	 * @return 						the position where the player should move
	 * @throws Exception			if an invalid strategy is used
	 */
	private static int drawCardFromChest(char strategy, int turns, int currentPosition, boolean jailCard1,
										 boolean jailCard2, Deck chest, Deck chance, Dice die1, Dice die2, 
										 Card jCard1, Card jCard2, RedBlackBST<Integer, Property> st) 
										 throws Exception {
		Card crd = Deck.drawCard(chest.drawPile, chest.discardPile);
		if (crd.moves()) { // if the card causes moving
			if (crd.getID() == 1) { // advance to Go
				currentPosition = 0;
				st.get(currentPosition).addVisit();
			}
			else if (crd.getID() == 6) { // go to jail
				triggerJail(strategy, turns, currentPosition, jailCard1, jailCard2, 
							die1, die2, chest, chance, jCard1, jCard2, st);
			}
			else if (crd.getID() == 5) { // keep 'Get Out of Jail' free card
				jailCard1 = true;
				jCard1 = crd;
			}
		}
		return currentPosition;
	}

	/**
	 * Draws a card from the chance deck, determines if it requires player movement, and carries out the
	 * appropriate movement.
	 * 
	 * @param strategy				the strategy being used this simulation
	 * @param turns					the number of turns
	 * @param currentPosition		the player's current position on the board
	 * @param jailCard1				whether the player has jailCard1
	 * @param jailCard2				whether the player has jailCard2
	 * @param jCard2				"Get Out of Jail Free" Card 2
	 * @param chance				the chance deck of cards
	 * @param die1					the first dice
	 * @param die2					the second dice
	 * @return 						the position where the player should move
	 * @throws Exception			if an invalid strategy is used
	 */
	private static int drawCardFromChance(char strategy, int turns, int currentPosition, boolean jailCard1,
										  boolean jailCard2, Card jCard1, Card jCard2, Deck chance, 
										  Deck chest, Dice die1, Dice die2, RedBlackBST<Integer, Property> st) 
									      throws Exception {
		Card crd = Deck.drawCard(chance.drawPile, chance.discardPile);
		if (crd.moves()) {
			if (crd.getID() == 1) { // go to boardwalk
				currentPosition = crd.getMovesToPosition();
				st.get(currentPosition).addVisit();
			}
			else if (crd.getID() == 2) { // Go
				currentPosition = crd.getMovesToPosition();
				st.get(currentPosition).addVisit();
			}
			else if (crd.getID() == 3) { // Illinois Ave
				currentPosition = crd.getMovesToPosition();
				st.get(currentPosition).addVisit();
			}
			else if (crd.getID() == 4) { // St. Charles Place
				currentPosition = crd.getMovesToPosition();
				st.get(currentPosition).addVisit();
			}
			else if (crd.getID() == 5) { // Nearest Railroad
				goToNearestRailroad(currentPosition, st);
			}
			else if (crd.getID() == 6) { // Nearest Railroad
				goToNearestRailroad(currentPosition, st);
			}
			else if (crd.getID() == 7) { // Nearest Utility
				goToNearestUtility(currentPosition, st);
			}
			else if (crd.getID() == 9) { // keep 'Get Out of Jail' free card
				jailCard2 = true;
				jCard2 = crd;
			}
			else if (crd.getID() == 10) { // Go back 3
				currentPosition -= 3;
				st.get(currentPosition).addVisit();
			}
			else if (crd.getID() == 11) { // Go to Jail
				triggerJail(strategy, turns, currentPosition, jailCard1, jailCard2, die1, 
							die2, chest, chance, jCard1, jCard2, st);
			}
			else if (crd.getID() == 14) { // Go to Reading Railroad
				currentPosition = crd.getMovesToPosition();
				st.get(currentPosition).addVisit();
			}
		}
		return currentPosition;
	}

	/**
	 * Triggers taking a player to jail depending on what strategy is being used:
	 * 	A) players are immediately released from jail.
	 * 	B) players must try for doubles or be released after 3 attempts.
	 * 
	 * @param strategy				the strategy being used this simulation
	 * @param turns					the number of turns
	 * @param currentPosition		the player's current position on the board
	 * @param jailCard1				whether the player has jailCard1
	 * @param jailCard2				whether the player has jailCard2
	 * @param die1					the first dice
	 * @param die2					the second dice
	 * @throws Exception			if an invalid strategy is used
	 */
	private static void triggerJail(char strategy, int turns, int currentPosition, boolean jailCard1, 
									boolean jailCard2, Dice die1, Dice die2, Deck chest, Deck chance,
									Card jCard1, Card jCard2, RedBlackBST<Integer, Property> st) 
									throws Exception {
		if (strategy == 'A') {
			goToJailA(currentPosition, jailCard1, jailCard2, chest, chance, jCard1, jCard2, st);
		}
		else if (strategy == 'B') {
			goToJailB(currentPosition, jailCard1, jailCard2, jCard1, jCard2, turns, die1, die2, chest, chance, st);
		}
		else {
			throw new Exception("Please use a valid strategy");
		}
	}

	/**
	 * Puts the player in jail based off strategy B where they try for doubles and use
	 * a "Get Out of Jail Free Card" if they have it.
	 * 
	 * @param currentPosition	the player's current position
	 * @param jailCard1			the jail card associated with the <code>chest</code> deck
	 * @param jailCard2			the jail card associated with the <code>chance</code> deck
	 */
	private static void goToJailB(int currentPosition, boolean jailCard1, boolean jailCard2, 
								  Card jCard1, Card jCard2, int turns, Dice die1, Dice die2,
								  Deck chest, Deck chance, RedBlackBST<Integer, Property> st) {
		currentPosition = 10;
		st.get(currentPosition).addVisit();
		if (jailCard1) {
			jailCard1 = !jailCard1;
			Deck.putJailCardBack(jCard1, chest.discardPile);
		}
		else if (jailCard2) {
			jailCard2 = !jailCard2;
			Deck.putJailCardBack(jCard2, chance.discardPile);
		}
		else {
			int turnsAtMax = turns + 3;
			while (turns < turnsAtMax) {
				if (die1.roll() == die2.roll()) {
					break;
				}
				else {
					turns++;
				}
			}
		}
	}
	
	/**
	 * Puts the player in jail based off strategy A where they are immediately released and use
	 * a "Get Out of Jail Free Card" if they have it.
	 * 
	 * @param currentPosition	the player's current position
	 * @param jailCard1			the jail card associated with the <code>chest</code> deck
	 * @param jailCard2			the jail card associated with the <code>chance</code> deck
	 */
	private static void goToJailA(int currentPosition, boolean jailCard1, boolean jailCard2,
								  Deck chest, Deck chance, Card jCard1, Card jCard2,
								  RedBlackBST<Integer, Property> st) {
		currentPosition = 10;
		st.get(currentPosition).addVisit();
		if (jailCard1) {
			jailCard1 = !jailCard1;
			Deck.putJailCardBack(jCard1, chest.discardPile);
		}
		else if (jailCard2) {
			jailCard2 = !jailCard2;
			Deck.putJailCardBack(jCard2, chance.discardPile);
		}
	}
	
	/**
	 * Sets the player's current position to the nearest utility.
	 * 
	 * @param currentPosition	the player's current position
	 */
	private static void goToNearestUtility(int currentPosition, RedBlackBST<Integer, Property> st) {
		if (currentPosition > 12 && currentPosition < 28) {
			currentPosition = 28;
			st.get(currentPosition).addVisit();
		}
		if (currentPosition > 28 || currentPosition < 12) {
			currentPosition = 12;
			st.get(currentPosition).addVisit();
		}
	}

	/**
	 * Sets the player's current position to the nearest railroad.
	 * 
	 * @param currentPosition	the player's current position
	 */
	private static void goToNearestRailroad(int currentPosition, RedBlackBST<Integer, Property> st) {
		if (currentPosition > 35 || currentPosition < 5) {
			currentPosition = 5;
			st.get(currentPosition).addVisit();
		}
		else if (currentPosition > 5 && currentPosition < 15) {
			currentPosition = 15;
			st.get(currentPosition).addVisit();
		}
		else if (currentPosition > 15 && currentPosition < 25) {
			currentPosition = 25;
			st.get(currentPosition).addVisit();
		}
		else if (currentPosition > 25 && currentPosition < 35) {
			currentPosition = 35;
			st.get(currentPosition).addVisit();
		}
	}
	
}
