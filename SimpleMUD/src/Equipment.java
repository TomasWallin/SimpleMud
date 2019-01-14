
public abstract class Equipment extends Item {
	
	private int dmg_mod;
	private int level;
	
	public Equipment(String name, String description, int level, int dmg_mod) {
		super(name,description);
		this.dmg_mod = dmg_mod;
		this.level = level;
	}
	
	public int getDMG_mod() {
		return dmg_mod;
	}
	
	public int getLevel() {
		return level;
	}
	
}
