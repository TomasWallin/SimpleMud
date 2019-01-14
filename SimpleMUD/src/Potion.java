
public class Potion extends Item {

	private int health;
	private int xp;
	
	public Potion(String name, String description, int value) {
		super(name, description);
		this.health = value;
		this.xp = value;
		
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getXP() {
		return xp;
	}
}
