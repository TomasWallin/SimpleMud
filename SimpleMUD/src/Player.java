import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Tomas Wallin
 * @version 1.0
 */
public class Player {
	
	private int we;
	private int ns;
	private int hp = 100;
	private int hp_max = 100;
	private int xp = 0;
	private boolean in_combat = false;
	private World world;
	private int MAPSIZE;
	private Helmet helmet;
	private Armor armor;
	private Weapon weapon;
	private ArrayList<Item> inventory = new ArrayList<Item>();
	
	public Player(int we, int ns, int mapsize) {
		this.we = we;
		this.ns = ns;
		MAPSIZE = mapsize;
		System.out.println("You're awake");
	}
	
	/**
	 * @param world: the world in which the player is placed.
	 */
	public void placeInWorld(World world) {
		this.world = world;
		this.world.placePlayer(we, ns);
	}
	
	
	/**
	 * Executes the player input/output loop.
	 * <p>
	 * The player must be placed in a world for the method to execute meaningfully.
	 * @throws IOException
	 */
	public void run() throws IOException {
		String in_buffer, arg;
		boolean loop_flag = true;
		String[] input;
		int status_flag;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		for(;loop_flag;) {
			if(hp <= 0) {
				System.out.println("You're dead");
				break;
			}
			in_buffer = br.readLine();
			input = null;
			arg = "";
			input =	in_buffer.split(" ");
			if(input.length >= 2) arg = input[1];
			if(input.length >= 3) arg = arg.concat(" "+input[2]);
			status_flag = 0;
			if(in_combat){
				switch(input[0]) {
				case "n":
				case "north":
				case "s":
				case "south":
				case "w":
				case "west":
				case "e":
				case "east":
					System.out.println("You can't do that while in combat");
					break;
				case "help":
					helpCombat();
					break;
				case "look":
					world.lookItems(we,ns);
					world.lookDoors(we,ns);
					break;
				case "map":
					world.drawMap(we,ns);
					int temp = Room.getTotal() - Room.getVisited();
					System.out.println("You have "+temp+" known rooms you have not entered yet");
					break;
				case "location":
					System.out.println("You're at "+we+"we "+ns+"ns");
					break;
				case "stats":
					stats();
					break;
				case "xp":
					System.out.println("You have gathered "+xp+"xp");
					break;
				case "hp":
					System.out.println("You currently have "+hp+" hp");
					break;
				case "take":
					takeItem(arg);
					break;
				case "drop":
					dropItem(arg);
					break;
				case "use":
					useItem(arg);
					break;
				case "equip":
					equip(arg);
					break;
				case "inventory":
					showInventory();
					break;
				case "investigate":
					investigateItem(arg);
					break;
				case "escape":
					escape(arg);
					break;
				case "fight":
					fight();
					if(world.isDead(we,ns)) in_combat = false;
					break;
				case "exit":
					loop_flag = false;
					System.out.println("Bye");
					break;
				default:
					System.out.println("That is not a valid command");
					break;
				}
			} else {
				switch(input[0]) {
				case "help":
					help();
					break;
				case "n":
				case "north":
					status_flag = world.enter(we,wrap(ns+1));
					if(status_flag != 0) {
						ns = wrap(ns+1);
					} else {
						System.out.println("You can't go there");
					}
					break;
				case "s":
				case "south":
					status_flag = world.enter(we,wrap(ns-1));
					if(status_flag != 0) {
						ns = wrap(ns-1);
					} else {
						System.out.println("You can't go there");
					}
					break;
				case "w":
				case "west":
					status_flag = world.enter(wrap(we-1),ns);
					if(status_flag != 0) {
						we = wrap(we-1);
					} else {
						System.out.println("You can't go there");
					}
					break;
				case "e":
				case "east":
					status_flag = world.enter(wrap(we+1),ns);
					if(status_flag != 0) {
						we = wrap(we+1);
					} else {
						System.out.println("You can't go there");
					}
					break;
				case "look":
					world.lookItems(we,ns);
					world.lookDoors(we,ns);
					break;
				case "map":
					world.drawMap(we,ns);
					int temp = Room.getTotal() - Room.getVisited();
					System.out.println("You have "+temp+" known rooms you have not entered yet");
					break;
				case "location":
					System.out.println("You're at "+we+"we "+ns+"ns");
					break;
				case "stats":
					stats();
					break;
				case "xp":
					System.out.println("You have gathered "+xp+"xp");
					break;
				case "hp":
					System.out.println("You currently have "+hp+" hp");
					break;
				case "take":
					takeItem(arg);
					break;
				case "drop":
					dropItem(arg);
					break;
				case "use":
					useItem(arg);
					break;
				case "equip":
					equip(arg);
					break;
				case "inventory":
					showInventory();
					break;
				case "investigate":
					investigateItem(arg);
					break;
				case "exit":
					loop_flag = false;
					System.out.println("Bye");
					break;
				default:
					System.out.println("That is not a valid command");
					break;
					}
			}
			hp_max = 100 + xp/2;
			if(status_flag == 2) in_combat = true; 
		}
	}
	
	/**
	 * Prints a list of inventory contents to the console.
	 */
	private void showInventory() {
		Iterator<Item> iterator = inventory.iterator();
		if(iterator.hasNext()) {
			for(;iterator.hasNext();) {
				System.out.println(iterator.next().getName());
			} 
		} else {
			System.out.println("Your Inventory is empty");
		}
	}
	
	/**
	 * Tries to remove an item from the room you're currently in and place it in your inventory.
	 * @param item: is a String with the name of the item you wish to take.
	 */
	private void takeItem(String name) {
		if(world.itemExist(we,ns,name)) {
			if(!world.getItem(we,ns,name).getHeavy()) {
				Item temp = world.getItem(we,ns,name);
				System.out.println("You've picked up "+temp.getName());
				inventory.add(temp);
				world.deleteItem(we,ns,temp.getID());
			} else {
				System.out.println(name+" is too heavy");
			}
		} else {
			System.out.println("There is no "+name+" in this room");
		}
	}
	
	/**
	 * Removes an item from your inventory and places it in your current room.
	 * @param name: is a String with the name of the item you wish to drop.
	 */
	private void dropItem(String name) {
		if(inInventory(name)) {
			Item temp = getItemReference(name);
			world.dropItem(we,ns,temp);
			deleteItem(temp.getID());
			System.out.println("You've dropped "+name+" on the floor");
		} 
	}
	
	
	/**
	 * Uses an item from your inventory. Contains the code deciding what the items will do.
	 * @param name: is a String with the name of the item you wish to use.
	 */
	private void useItem(String name) {
			Item temp = getItemReference(name);
			if(temp == null) {
				if(world.itemExist(we,ns,name)) {
					if(!world.getItem(we, ns, name).getHeavy()){
						temp = world.getItem(we,ns,name);
						inventory.add(temp);
						world.deleteItem(we,ns,temp.getID());
					}
				}
			}
			if(temp != null) {
				if(name.equalsIgnoreCase("Red Potion")) {
					Potion potion = (Potion)temp;
					int temp_hp = hp;
					hp += potion.getHealth();
					if(hp > hp_max) hp = hp_max;
					System.out.println("You've restored "+(hp-temp_hp)+"hp");
					deleteItem(temp.getID());	
				}
				if(name.equalsIgnoreCase("Blue Potion")) {
					Potion potion = (Potion)temp;
					xp += potion.getXP();
					System.out.println("You've gained "+potion.getXP()+"xp");
					deleteItem(temp.getID());
				}
				if(name.equalsIgnoreCase("Meat")) {
					Meat meat = (Meat)temp;
					int temp_hp = hp;
					hp += meat.getHealth();
					if(hp > hp_max) hp = hp_max;
					System.out.println("You've restored "+(hp-temp_hp)+"hp");
					deleteItem(temp.getID());	
				}
				if(name.equalsIgnoreCase("Key")) {
					if(world.getChest(we,ns)) {
						Item[] loot = world.openChest(we,ns);
						System.out.println("You've opened a chest"); //make it so that loot is shown.
						deleteItem(temp.getID());
						world.lookLoot(we, ns, loot);
					} else {
						System.out.println("There is no chest in this room");
					}
				}
				if(temp instanceof Equipment) {
					System.out.println("Try equiping that instead");
				}
			} else {
				System.out.println("You don't have "+name+" in your inventory");
			}
	}
	
	/**
	 * Prints an items in your inventories description.
	 * @param name: is a String with the name of the item you wish to describe.
	 */
	private void investigateItem(String name) {
		Item temp = getItemReference(name);
		if(temp != null) {
			System.out.println(temp.getDescription());
		} else if(world.itemExist(we,ns,name)) {
			System.out.println(world.getItem(we, ns, name).getDescription());
		} else if(world.existMonster(we,ns) && name.equalsIgnoreCase("monster")) {
			System.out.println(world.getMonster(we,ns).getDescription());
		} else {
			System.out.println("You don't have "+name);
		}
	}
	
	
	/**
	 * Checks if an item exist in your inventory.
	 * @param name: is a String with the name of the item you wish to search for.
	 * @return true if an item with name exists in your inventory, otherwise false.
	 */
	private boolean inInventory(String name) {
		Iterator<Item> iterator = inventory.iterator();
		boolean result_flag = false;
		for(;iterator.hasNext();) {
			if(iterator.next().getName().equalsIgnoreCase(name)) {
				result_flag = true;
				break;
			}
		}
		return result_flag;
	}
	
	
	/**
	 * Returns a reference to an item in your inventory.
	 * @param name: is a String with the name of the item you're trying to retrieve from your inventory.
	 * @return Item if it exists in your inventory, otherwise null.
	 */
	private Item getItemReference(String name) {
		Item item_reference = null;
		Item temp;
		Iterator<Item> iterator = inventory.iterator();
		for(;iterator.hasNext();) {
			temp = iterator.next();
			if(temp.getName().equalsIgnoreCase(name)) {
				item_reference = temp;
				break;
			}
		}
		return item_reference;
	}
	
	/**
	 * Deletes an item from your inventory.
	 * @param id: is the unique id of the item you wish to delete.
	 */
	public void deleteItem(int id) {
		Iterator<Item> iterator = inventory.iterator();
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
	
	private void equip(String arg) {
		Item temp_item = getItemReference(arg);
		if(temp_item != null) {
			if(temp_item instanceof Equipment) {
					Equipment temp_eq = (Equipment) temp_item;
				if(temp_eq instanceof Helmet) {
					if(helmet != null) {
						if(helmet.getLevel() <= temp_eq.getLevel()) {
							helmet = (Helmet) temp_eq;
							System.out.println("You've equipped a new helmet");
						} else {
							System.out.println("Current equipment is higher level");
						}
					} else {
						helmet = (Helmet) temp_eq;
						System.out.println("You've equipped a new helmet");
					}
					
				}
				if(temp_eq instanceof Armor) {
					if(armor != null) {
						if(armor.getLevel() <= temp_eq.getLevel()) {
							armor = (Armor) temp_eq;
							System.out.println("You've equipped a new armor");
						} else {
						System.out.println("Current equipment is higher level");
						}
					} else {
						armor = (Armor) temp_eq;
						System.out.println("You've equipped a new armor");
					}
					
				} if(temp_eq instanceof Weapon) {
					if(weapon != null) {
						if(weapon.getLevel() <= temp_eq.getLevel()) {
							weapon = (Weapon) temp_eq;
							System.out.println("You've equipped a new weapon");
						} else {
							System.out.println("Current equipment is higher level");
						}
					} else {
						weapon = (Weapon) temp_eq;
						System.out.println("You've equipped a new weapon");
					}
					
				}
				deleteItem(getItemReference(arg).getID());
			} else {
				System.out.print(arg+" is of the wrong type");
			}
		} else {
			System.out.print("You don't have "+arg+" in your inventory");
		}
		
	}
	
	
	private void fight() {
		int damage = (int)(12*(1.0+xp/150.0)*(Math.random()+1));
		if(weapon != null) damage += weapon.getDMG_mod(); {
			try {
				TimeUnit.MILLISECONDS.sleep(300);
				world.dealDMG(we, ns, damage);
				System.out.print("You strike the monster");
				if(weapon != null) {
					System.out.print(" with your sword");
				}		
				System.out.println(" and deal "+damage+" damage");
				TimeUnit.MILLISECONDS.sleep(700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(world.isDead(we,ns)) {
			int inc_xp = (10 * world.getLevel(we,ns));
			xp += inc_xp;
			Item[] loot = concatenate(
					world.spawnItems(we, ns, true),
					world.spawnKey(we, ns)
					);
			System.out.println("You've killed the monster!");
			System.out.println("+"+inc_xp+"xp");
			world.lookLoot(we, ns,loot);
			world.setDead(we,ns);
		} else {
			damage = world.getDMG(we, ns);
			if(helmet != null) damage -= helmet.getDMG_mod();
			if(armor != null) damage -= armor.getDMG_mod();
			System.out.println("The monster strikes back and deals you "+damage);
			hp -= damage;
		}
	}
	
	//delete meat when you leave the room.
	private void escape(String arg) {
		int status_flag;
		if(world.itemExist(we, ns, "Meat")) {
			System.out.println("You've distracted the monster with meat");
			switch(arg) {
			case "n":
			case "north":
				status_flag = world.enter(we,wrap(ns+1));
				if(status_flag != 0) {
					world.deleteItem(we, ns, world.getID(we,ns,"Meat"));
					ns = wrap(ns+1);
					System.out.println("You've escaped!");
					in_combat = false;
				} else {
					System.out.println("You can't go north!");
				}
				break;
			case "s":
			case "south":
				status_flag = world.enter(we,wrap(ns-1));
				if(status_flag != 0) {
					world.deleteItem(we, ns, world.getID(we,ns,"Meat"));
					ns = wrap(ns-1);
					System.out.println("You've escaped!");
					in_combat = false;
				} else {
					System.out.println("You can't go south!");
				}
				break;
			case "w":
			case "west":
				status_flag = world.enter(wrap(we-1),ns);
				if(status_flag != 0) {
					world.deleteItem(we, ns, world.getID(we,ns,"Meat"));
					we = wrap(we-1);
					System.out.println("You've escaped!");
					in_combat = false;
				} else {
					System.out.println("You can't go west!");
				}
				break;
			case "e":
			case "east":
				status_flag = world.enter(wrap(we+1),ns);
				if(status_flag != 0) {
					world.deleteItem(we, ns, world.getID(we,ns,"Meat"));
					we = wrap(we+1);
					System.out.println("You've escaped!");
					in_combat = false;
				} else {
					System.out.println("You can't go east!");
				}
				break;
				default:
					System.out.println("In what direction do you wan't to escape?");
					break;
			}
		} 
		else {
			int dmg;
			dmg = world.getDMG(we, ns);
			System.out.println("The monster prevents your escape and you take "+dmg+" damage!");
			hp -= dmg;
			System.out.println("Maybe you can distract the monster?");
		}
	}
	
	/**
	 * Prints possible commands,
	 */
	private void help() {
		System.out.println(""
				+ "Here is a description of the commands used to navigate this game\n"
				+ "Navigate the world by using the n (north), s (south), w (west), e (east).\n"
				+ "\n"
				+ "look          -    gives a description of the room you are currently in\n"
				+ "\n"
				+ "map           -    prints a map of rooms previously visited\n"
				+ "                   [_] is an empty room\n"
				+ "                   [?] is a room you've yet to enter\n"
				+ "                    P  denotes the player\n"
				+ "                    M  means monster\n"
				+ "                    C  marks a chest\n"
				+ "                    L  means loot has been left on the ground\n"
				+ "\n"
				+ "location      -    gives the current coordinates of the player on the map grid\n"
				+ "\n"
				+ "stats         -    shows stats of player\n"
				+ "\n"
				+ "xp            -    gives the amount of accumulated xp\n"
				+ "\n"
				+ "hp            -    gives your current hp\n"
				+ "\n"
				+ "take <item>   -    picks an item from the room and puts it in your inventory\n"
				+ "                   note that some items may be to heavy to lift\n"
				+ "\n"
				+ "drop <item>   -    drops an item on the floor it it's in your inventory\n"
				+ "\n"
				+ "use <item>    -    uses an item in your inventory or in the room\n"
				+ "\n"
				+ "equip <item>  -    equip an item from your inventory to boost your stats\n"	
				+ "\n"
				+ "investigate <item> investigate an item in your inventory or in the same room\n"
				+ "\n"
				+ "inventory     -    lists the items currently in your inventory \n"
				+ "\n"
				+ "exit          -    immediately ends the playing session\n"
				);
	}

	private void helpCombat() {
		System.out.println(""
				+ "Here is a description of the commands used to navigate this game\n"
				+ "You cannot just leave a room while in combat.\n"
				+ "\n"
				+ "fight         -    Fight the monster\n"
				+ "\n"
				+ "escape <direction> Try and escape from the room\n"
				+ "\n"
				+ "look          -    gives a description of the room you are currently in\n"
				+ "\n"
				+ "map           -    prints a map of rooms previously visited\n"
				+ "                   [_] is an empty room\n"
				+ "                   [?] is a room you've yet to enter\n"
				+ "                    P  denotes the player\n"
				+ "                    M  means monster\n"
				+ "                    C  marks a chest\n"
				+ "                    L  means loot has been left on the ground\n"
				+ "\n"
				+ "location      -    gives the current coordinates of the player on the map grid\n"
				+ "\n"
				+ "stats         -    shows stats of player\n"
				+ "\n"
				+ "xp            -    gives the amount of accumulated xp\n"
				+ "\n"
				+ "hp            -    gives your current hp\n"
				+ "\n"
				+ "take <item>   -    picks an item from the room and puts it in your inventory\n"
				+ "                   note that some items may be to heavy to lift\n"
				+ "\n"
				+ "drop <item>   -    drops an item on the floor it it's in your inventory\n"
				+ "\n"
				+ "use <item>    -    uses an item in your inventory or in the room\n"
				+ "\n"
				+ "equip <item>  -    equip an item from your inventory to boost your stats\n"	
				+ "\n"
				+ "investigate <item> investigate an item in your inventory or in the same room\n"
				+ "\n"
				+ "inventory     -    lists the items currently in your inventory \n"
				+ "\n"
				+ "exit          -    immediately ends the playing session\n"
				);
	}
	
	private void stats() {
		System.out.println(""
				+"HP:     "+hp+"\n"
				+"Max HP: "+hp_max+"\n"
				+"XP:     "+xp+"\n"
				+"Helmet: "+buildEquipmentStats(helmet)+"\n"
				+"Armor:  "+buildEquipmentStats(armor)+"\n"
				+"Weapon: "+buildEquipmentStats(weapon)+"\n"
				+"Rooms explored: "+Room.getVisited()
				);
	}
	
	//Simplify this later when you can see if it works
	private String buildEquipmentStats(Equipment eq) {
		String result = "";
		if(eq != null) {
			result = result.concat(eq.getName());
			result = result.concat(" level:");
			result = result.concat(Integer.toString(eq.getLevel()));
			result = result.concat("     bonus:");
			result = result.concat(Integer.toString(eq.getDMG_mod()));
			
		} else {
			result = result.concat("none");
		}
		return result;
	}
	
	/**
	 * Wraps the coordinates if it is bigger or smaller than MAPSIZE
	 * @param coordinate: the coordinate call which you're 
	 * @return coordinate wrapped if coordinate is bigger than MAPSIZE or smaller than 0.
	 */
	private int wrap(int coordinate) {
		if(coordinate >= MAPSIZE) coordinate -= MAPSIZE;
		if(coordinate < 0) coordinate += MAPSIZE;
		return coordinate;
	}
	
	//Borrowed this code. 
	private <T> T[] concatenate(T[] arr1, T[] arr2) {
		@SuppressWarnings("unchecked")
		T[] arr3 = (T[]) Array.newInstance(arr1.getClass().getComponentType(), arr1.length + arr2.length);
		System.arraycopy(arr1, 0, arr3, 0, arr1.length);
		System.arraycopy(arr2, 0, arr3, arr1.length, arr2.length);
		return arr3;
	}
		
}