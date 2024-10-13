package monopolySimulation;

/**
 * Represents an instance of either a community chest card or a chance card in the game
 * monopoly. This class only contains functionality to determine what the card is
 * <code>ID</code>, whether the card causes moving <code>moves</code>, the name of where 
 * the card moves to <code>movesToName</code>, and the position of where it moves <code>
 * movesToPosition</code>.
 * 
 * @author Noah Ewell
 */
public class Card {

	// Declare fields
	private int ID;
	private boolean moves;
	private String movesToName;
	private int movesToPosition;
	
	// Default Constructor
	public Card (int ID, boolean moves, String movesToName, int movesToPosition) {
		this.ID = ID;
		this.moves = moves;
		this.movesToName = movesToName;
		this.movesToPosition = movesToPosition;
	}

	/**
	 * Get the card's ID.
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Get whether it causes moving.
	 */
	public boolean moves() {
		return moves;
	}

	/**
	 * Get the name of where it moves.
	 */
	public String getMovesToName() {
		return movesToName;
	}

	/**
	 * Get the position of where it moves.
	 */
	public int getMovesToPosition() {
		return movesToPosition;
	}
	
	@Override
	public String toString() {
		return String.format("Card ID: %-4d | Moves: %-5b | Moves to Name: %-18s | Moves to Space: %-2d\n", 
				ID, moves, movesToName, movesToPosition);
	}
}
