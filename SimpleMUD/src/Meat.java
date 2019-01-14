
public class Meat extends Item {
	
	private int hp;
	
	public Meat(String name, String description, int hp) {
		super(name, description);
		this.hp = hp;
	}
	
	public int getHealth() {
		return hp;
	}
}
