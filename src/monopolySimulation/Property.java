package monopolySimulation;

/**
 * Represents an instance of a property in monopoly, containing its location number, name,
 * and the number of times the space has been landed on.
 * 
 * @author Colton Anderson + Noah Ewell + Phuon Tran + Ryley Pabst
 */
public class Property {
	
	// Declare fields
	private String name;
	private Integer location;
	private int visits;
	
	// Constructor
	public Property(String name, Integer propertyID) {
		this.location = propertyID;
		this.name = name;
		this.visits = 0;
	}
	
	/**
	 * Increments number of visits to this property by 1.
	 */
	public void addVisit() {
		this.visits++;
	}
	
	/**
	 * @return the integer location of this property
	 */
	public Integer getLocation() {
		return location;
	}
	
	/**
	 * @return the name of this property
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the number of visits to this property
	 */
	public int getVisits() {
		return visits;
	}
	
	@Override
	public String toString() {
		return "Property: " + name + " - Location: " + location + " - Visits: " + visits;
	}
}
