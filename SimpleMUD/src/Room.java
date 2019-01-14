import java.util.*;

public class Room {
	private boolean exists;
	private boolean visit;
	private char[] neighbours;
	private static int total_rooms;
	private static int visited_rooms;
	private ArrayList<Item> Items = new ArrayList<Item>();
	private Monster monster = null;
	private Item latest;
	
	static {
		total_rooms = 0;
		visited_rooms = 0;
	}
	

	public Room(boolean exists) {
		visit = false;
		this.exists = exists;
		if(exists) total_rooms++;
	}
	
	public void setVisit() {
		visit = true;
		visited_rooms++;
		
	}
	
	public static int getVisited() {
		return visited_rooms;
	}
	
	public static int getTotal() {
		return total_rooms;
	}
	
	public void setNeighbours(char[] in_neighbours) {
		neighbours = in_neighbours;
	}
	
	public boolean getVisit() {
		return visit;
	}
	
	public boolean getExists() {
		return exists;
	}
	
	public void setExists() {
		exists = true;
	    total_rooms++;
	}
	
	public void setMonster(int hp, int level) {
		this.monster = new Monster(hp, level);
	}
	
	public void removeMonster() {
		monster = null;
	}
	
	public void dealDMG(int dmg) {
		monster.dealDMG(dmg);
	}
	
	public int getDMG() {
		return monster.getDMG();
	}
	
	public int getLevel() {
		return monster.getLVL();
	}
	
	public boolean existMonster() {
		if(monster != null) return true;
		else return false;
	}
	
	public boolean isDead() {
		if(monster != null) {
			if(monster.isDead()) return true;
			else return false;
		} else return true;
	}
	
	public Monster getMonster() {
		return monster;
	}
	
	public boolean encounter() {
		if(monster != null) {
			System.out.println("You've encountered a level "+monster.getLVL()+" monster!");
			return true;
		} else return false;
	}
	
	public void lookItems() {
		//Check for objects to describe;
		String temp;
		Iterator<Item> iterator = Items.iterator();
		if(iterator.hasNext()) {
			System.out.print("In the room there is a ");
			for(int i = 0;iterator.hasNext();i++) {
				if(0 < i) System.out.print(" and a ");
				temp = iterator.next().getName();
				System.out.print(temp);
			}
			System.out.println("");
		}
		
	}
	
	public void lookLoot(Item[] loot) {
		System.out.print("It drops a ");
		for(int i = 0; i < loot.length; i++) {
			System.out.print(loot[i].getName());
			if(i == loot.length - 2) {
				System.out.print(" and a ");
			} else if(i == loot.length - 1) {
				System.out.print("\n");
			} else System.out.print(", ");
		}
		
	}
	
	public void lookDoors() {
		System.out.print("There is a door to your ");
		for(int i = 0; i < neighbours.length; i++) {
			switch(neighbours[i]) {
			case 'n':
				System.out.print("north");
				if(i < neighbours.length-2) System.out.print(", ");
				if(i == neighbours.length-2) System.out.print(" and ");
				break;
			case 's':
				System.out.print("south");
				if(i < neighbours.length-2) System.out.print(", ");
				if(i == neighbours.length-2) System.out.print(" and ");
				break;
			case 'w':
				System.out.print("west");
				if(i < neighbours.length-2) System.out.print(", ");
				if(i == neighbours.length-2) System.out.print(" and ");
				break;
			case 'e':
				System.out.print("east");
				if(i < neighbours.length-2) System.out.print(", ");
				if(i == neighbours.length-2) System.out.print(" and ");
				break;
				default: 
					break;
			}
		}
		if(neighbours.length == 1) System.out.println(". This door is a dead end");
		else System.out.println("");
	}
	
	public void addItem(Item item) {
		Items.add(item);
		latest = item;
	}
	
	public Item returnLatest() {
		return latest;
	}
	
	public boolean hasItem(String name) {
		boolean ret_bool = false;
		Iterator<Item> iterator = Items.iterator();
			for(;iterator.hasNext();) {
				if(iterator.next().getName().equalsIgnoreCase(name)) {
					ret_bool = true;
					break;
				}
				if(!iterator.hasNext()) break;
			}
		return ret_bool;
	}
	
	//Returns 0 if there is no item otherwise the item ID.
	public int getID(String name) {
		int id = 0;
		String temp;
		Item obj;
		Iterator<Item> iterator = Items.iterator();
			for(;iterator.hasNext();) {
				obj = iterator.next();
				temp = obj.getName();
				if(temp.equalsIgnoreCase(name)) {
					id = obj.getID();
					break;
				}
			}
		return id;
	}
	
	public Item returnItem(String name) {
		Iterator<Item> iterator = Items.iterator();
		Item obj;
		Item ret_obj = null;
		String temp;
			for(;iterator.hasNext();) {
				obj = iterator.next();
				temp = obj.getName();
				if(temp.equalsIgnoreCase(name)) {
					ret_obj = obj;
					break;
				}
			}
		return ret_obj;
	}
	
	public Item returnItem(int id) {
		Iterator<Item> iterator = Items.iterator();
		Item obj;
		Item ret_obj = null;
		int temp;
			for(;iterator.hasNext();) {
				obj = iterator.next();
				temp = obj.getID();
				if(temp == id) {
					ret_obj = obj;
					break;
				}
			}
		return ret_obj;
	}
	
	public void dropItem(Item item) {
		Items.add(item);
	}
	
	public void deleteItem(int id) {
		Iterator<Item> iterator = Items.iterator();
		int temp;
		Item obj;
			for(;iterator.hasNext();) {
				obj = iterator.next();
				temp = obj.getID();
				if(temp == id) {
					iterator.remove();
					break;
				}
			}
	}
	
	public boolean notEmpty() {
		return !Items.isEmpty();
	}
	
	
}
