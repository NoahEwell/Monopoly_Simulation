package monopolySimulation;

import java.util.Random;

/**
 * A die that will roll a random number in range [1, <code>diceSides</code>]
 * 
 * @author Colton Anderson + Noah Ewell + Phuon Tran + Ryley Pabst
 */
public class Dice {
	
	// Declare Fields
	private int diceSides;
	
	// Default Constructor
	public Dice () {
		this.diceSides = 6;
	}
	
	/**
	 * Rolls the die and returns a random number in range [1, <code>diceSides</code>]
	 * 
	 * @return A random integer in range [1, <code>diceSides</code>]
	 */
	public int roll() {
		Random rand = new Random();
		return rand.nextInt(diceSides) + 1;
	}
	
	/*
	 * Test Driver
	 */
	public static void main(String[] args) {
		Dice newDie = new Dice();
		System.out.println("Rolling Dice: " + newDie.roll());
	}
}
