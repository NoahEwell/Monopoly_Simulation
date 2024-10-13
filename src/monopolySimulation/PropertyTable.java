package monopolySimulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

/**
 * Reads in properties from a .csv file and stores them in a RedBlackBST symbol table
 * using data structures from algs4.
 * 
 * @author Noah Ewell
 */
public class PropertyTable {
	
	/**
	 * Reads in the games from the csv file and
	 * returns them as an array of games.
	 * 
	 * @return an array of properties from the file
	 */
	static Property[] getProperties() {
		String fileName = "src/monopolySimulation/Resources/properties.csv";
		List<Property> list = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
			reader.readLine();
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				list.add(new Property(tokens[0], Integer.parseInt(tokens[1])));
			}
		} catch (IOException e) {
			System.err.println("Cannot read properties.csv");
			e.printStackTrace();
		}
		return list.toArray(new Property[list.size()]);
	}
	
	/**
	 * Stores properties in the symbol table according to their location on the board.
	 * 
	 * @param st 			symbol table
	 * @param properties 	an array of properties
	 */
	static void fillSymbolTable(RedBlackBST<Integer, Property> st, Property[] properties) {
		for (Property p : properties) {
			int id = p.getLocation();
			st.put(id, p);
		}
	}
	
	/**
	 * Utility print function to print properties.
	 * 
	 * @param properties	an array of properties
	 */
	static void printProperties(Property[] properties) {
		for (Property p: properties) {
			System.out.println(p);
		}
		System.out.println();
	}
	
	/*
	 * Test Driver
	 */
	public static void main(String[] args) {
		
		Property[] properties = getProperties();
		
		StdOut.println("PROPERTIES");
		StdOut.println("----------");
		printProperties(properties);

		RedBlackBST<Integer, Property> st = new RedBlackBST<>();			
		fillSymbolTable(st, properties);
		
		for (Integer key : st.keys()) {
			StdOut.println(st.get(key).getName() + " located at " + st.get(key).getLocation() 
					+ " with " + st.get(key).getVisits() + " visits");
		}
	}
}
