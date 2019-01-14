
public class Monster {

	int hp;
	int level;
	final int BASE_DMG = 10;
	
	public Monster(int hp, int level) {
		this.hp = hp;
		this.level = level;
	}
	
	public int getDMG() {
		//Base dmg x dmg modifier * 0-2
		int dmg = (int)(BASE_DMG * (1.0 + (level * 0.5) * (Math.random() + 1)));
		return dmg;
	}
	
	
	public void dealDMG(int dmg) {
		hp -= dmg;
	}
	
	public int getLVL() {
		return level;
	}
	
	public String getDescription() {
		return "A furry level "+level+" monster with "+hp+"hp left";
	}

	
	public boolean isDead() {
		if(hp <= 0) {
			return true;
		} else return false;
	}
}
