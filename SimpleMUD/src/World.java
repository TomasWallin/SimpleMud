import java.util.*;
import java.lang.reflect.Array;


public class World {
	
	private Room[][] map;
	private int MAPSIZE;
	private int ITEM_SPAWN_RATE;
	private int CHEST_SPAWN_RATE;
	private int MONSTER_SPAWN_RATE;
	
	public World(int dim_we, int dim_ns, int spawn_rate, int chest_rate, int monster_rate) 
	{
		map = new Room[dim_we][dim_ns];
		MAPSIZE = dim_we;
		ITEM_SPAWN_RATE = spawn_rate;
		CHEST_SPAWN_RATE = chest_rate;
		MONSTER_SPAWN_RATE = monster_rate;
	}
	
	public void placePlayer(int we, int ns) 
	{
			map[we][ns] = new Room(true);
			map[we][ns].setVisit();
			generateNeighbours(we, ns, true);
			char[] ch = getNeighbourList(we, ns);
			map[we][ns].setNeighbours(ch);
			map[we][ns].lookItems();
			map[we][ns].lookDoors();
		
	}
	
	public int enter(int we, int ns) 
	{
		int status = 0;
		char[] ch;
		if(map[we][ns] != null) 
		{
			if(map[we][ns].getExists())
			{
				if(!(map[we][ns].getVisit())) 
				{
					map[we][ns].setVisit();
					generateNeighbours(we,ns);
					ch = getNeighbourList(we,ns);
					map[we][ns].setNeighbours(ch);
					spawnItems(we,ns,false);
					spawnChest(we,ns);
					spawnMonster(we,ns);
				}
			System.out.print("You enter a room. ");
				if(!map[we][ns].encounter()) 
				{
					map[we][ns].lookItems();
					map[we][ns].lookDoors();
					status = 1;
				} 
				else 
				{
					status = 2;
				}
			} 
		}
		return status;
	}
	
	private char[] getNeighbourList(int we, int ns) 
	{
		String ch ="";
		if(map[wrap(we)][wrap(ns+1)].getExists()) ch += "n";
		if(map[wrap(we)][wrap(ns-1)].getExists()) ch += "s";
		if(map[wrap(we-1)][wrap(ns)].getExists()) ch += "w";
		if(map[wrap(we+1)][wrap(ns)].getExists()) ch += "e";
		
		return ch.toCharArray();
	}
	
	//Generates neighbors to a room. Makes several calls to roomPossible.
	private void generateNeighbours(int we, int ns) 
	{
		int create = 0;
		char ch;
		ArrayList<Character> candidate = new ArrayList<Character>();
		
		if(map[wrap(we)][wrap(ns+1)] == null) 
		{
			map[wrap(we)][wrap(ns+1)] = new Room(false);
			candidate.add('n');
		}
		if(map[wrap(we)][wrap(ns-1)] == null) 
		{
			map[wrap(we)][wrap(ns-1)] = new Room(false);
			candidate.add('s');
		}
		if(map[wrap(we-1)][wrap(ns)] == null) 
		{
			map[wrap(we-1)][wrap(ns)] = new Room(false);
			candidate.add('w');
		}
		if(map[wrap(we+1)][wrap(ns)] == null) 
		{
			map[wrap(we+1)][wrap(ns)] = new Room(false);
			candidate.add('e');
		}
		int vis_room = Room.getVisited();
		int tot_room = Room.getTotal();
		double random = Math.random();
		if((double)((double)(tot_room - vis_room)/(double)(tot_room/2)) < random) 
		{
			random = Math.random();
			if(((double)(tot_room - vis_room)/(double)(tot_room/1.5)) < random)
			{
				create = 3;
			}
			else
			{
				create = 2;
				}
		} 
		else
		{
			random = Math.random();
			if(((double)(tot_room - vis_room)/(double)(tot_room)) < random)
			{
				create = 1;
			}
			else 
			{
				create = 0;
			}
		}
		
		if(Room.getVisited() == Room.getTotal()) 
		{
			create = 3;
		}
		if(create > candidate.size()) 
		{
			create = candidate.size();
		}
		Collections.shuffle(candidate);
		for(int i = candidate.size(); i > 0; i--, create--) 
		{
			ch = candidate.get(i-1);
			candidate.remove(i-1);
			switch(ch) 
			{
			case 'n':
				if (create <= 0);
				else map[wrap(we)][wrap(ns+1)].setExists();
				break;
			case 's':
				if (create <= 0);
				else map[wrap(we)][wrap(ns-1)].setExists();
				break;
			case 'w':
				if (create <= 0);
				else map[wrap(we-1)][wrap(ns)].setExists();
				break;
			case 'e':
				if (create <= 0);
				else map[wrap(we+1)][wrap(ns)].setExists();
				break;
				default:
					break;
			}
		}
	}
	
	private void generateNeighbours(int we, int ns, boolean first) 
	{
		if(first) 
		{
			map[we][ns+1] = new Room(true);
			map[we][ns-1] = new Room(true);
			map[we-1][ns] = new Room(true);
			map[we+1][ns] = new Room(true);
		}
	}
	
	//Draws a map in the terminal of visited and not visited rooms. Will crash when hits corners of the array.
	public void drawMap(int we, int ns) 
	{
		int w_limit, n_limit, e_limit, s_limit;
		w_limit = n_limit = e_limit = s_limit = MAPSIZE/2; //ugly solution. Cleaner way to get start position?
			for(int i = 0; i < MAPSIZE; i++) 
			{
				for(int j = 0; j < MAPSIZE; j++) 
				{
					if(map[j][i] != null) 
					{
						if(j < w_limit) w_limit = j;
						if(j > e_limit) e_limit = j;
						if(i > n_limit) n_limit = i;
						if(i < s_limit) s_limit = i;
					}
				}
			}
		w_limit = wrap(w_limit-2);
		e_limit = wrap(e_limit+3);
		n_limit = wrap(n_limit+2);
		s_limit = wrap(s_limit-3);
		for(int i = n_limit; i > s_limit; i--) 
		{
			for(int j = w_limit; j < e_limit; j++) 
			{
				if(map[j][i] == null) System.out.print("   ");
				else if(j == we && i == ns) System.out.print("[P]");
				else if(map[j][i].existMonster()) System.out.print("[M]");
				else if(map[j][i].hasItem("Chest")) System.out.print("[C]");
				else if(map[j][i].notEmpty()) System.out.print("[L]");
				else if(map[j][i].getVisit()) System.out.print("[_]");
				else if(!(map[j][i].getVisit()) && map[j][i].getExists()) System.out.print("[?]");
				else if(!(map[j][i].getExists())) System.out.print("   ");
			}
			System.out.println("");
		}
	}
	
	private void spawnChest(int we, int ns) 
	{
		int random = (int)(Math.random()*100);
		if(random < CHEST_SPAWN_RATE) 
		{
			map[we][ns].addItem(new Chest("Chest", "A solid wooden chest with a lock",true));
		}
	}
	
	private void spawnMonster(int we, int ns) 
	{
		int random = (int)(Math.random()*100);
		if(random < MONSTER_SPAWN_RATE) 
		{
			int lvl = level(we,ns);
			map[we][ns].setMonster(20+(lvl*30), lvl);
		}
	}
	

	public Item[] spawnEquipment(int we, int ns) 
	{
		int random = (int)(Math.random()*3);
		int lvl = level(we,ns);
		if(random == 0) 
		{
			map[we][ns].addItem(new Armor("Armor","A suit of metal armor to protect your body",lvl,lvl*3));
		}
		if(random == 1) 
		{
			map[we][ns].addItem(new Helmet("Helmet","A metal helmet to protect your head",lvl,lvl*3));
		}
		if(random == 2) 
		{
			map[we][ns].addItem(new Weapon("Sword","A rusty sword to strike your foes",lvl,lvl*3));
		}
		Item[] spawn = {map[we][ns].returnLatest()};
		return spawn;
	}
	
	public Item[] spawnItems(int we, int ns, boolean forced) 
	{
		int random;
		ArrayList<Item> spawn = new ArrayList<Item>();
		boolean loop_flag, key_flag, rpotion_flag, bpotion_flag, meat_flag;
		loop_flag = key_flag = rpotion_flag = bpotion_flag = meat_flag = true;
		for(;loop_flag;) 
		{
			random = (int)(Math.random()*100);
			if(ITEM_SPAWN_RATE > random || forced) 
			{
				forced = false;
				random = (int)(Math.random()*100);
				if(0 <= random && random < 10 && key_flag) 
				{
					map[we][ns].addItem(new Key("Key","An easy way to open chests"));
					spawn.add(map[we][ns].returnLatest());
					key_flag = false;
				}
				if(10 <= random && random < 50 && rpotion_flag) 
				{
					map[we][ns].addItem(new Potion("Red Potion","Use to rejuvernate health", 40));
					spawn.add(map[we][ns].returnLatest());
					rpotion_flag = false;
				}
				if(50 <= random && random < 75 && bpotion_flag) 
				{
					map[we][ns].addItem(new Potion("Blue Potion","Use to gain xp", 25));
					spawn.add(map[we][ns].returnLatest());
					bpotion_flag = false;
				}
				if(75 <= random && random < 100 && meat_flag) 
				{
					map[we][ns].addItem(new Meat("Meat", "A juicy piece of meat, should you eat it yourself or use it for some other goal?", 15));
					spawn.add(map[we][ns].returnLatest());
					meat_flag = false;
				}
			}
			else
			{
				loop_flag = false;
			}
				
		}
		Item[] ret = new Item[spawn.size()];
		Iterator<Item> iterator = spawn.iterator();
		for(int i = 0; i < ret.length; i++) 
		{
			ret[i] = iterator.next();
		}
		return ret;
	}
	
	public Item[] spawnKey(int we, int ns) 
	{
		map[we][ns].addItem(new Key("Key","An easy way to open chests"));
		Item[] spawn = {map[we][ns].returnLatest()};
		return spawn;
	}
	
	public Item[] openChest(int we, int ns) 
	{
		Item[] loot = concatenate(
					spawnEquipment(we,ns),
					spawnItems(we,ns, true)
					);
		map[we][ns].deleteItem(map[we][ns].getID("Chest"));
		return loot;
	}
	
	public boolean getChest(int we, int ns) 
	{
		return map[we][ns].hasItem("Chest");
	}

	
	public Item getItem(int we, int ns, String name) 
	{
		return map[we][ns].returnItem(name);
	}
	
	public Item getItem(int we, int ns, int id) 
	{
		return map[we][ns].returnItem(id);
	}
	
	public boolean itemExist(int we, int ns, String name) 
	{
		return map[we][ns].hasItem(name);
	}
	
	public int getID(int we, int ns, String name) 
	{
		return map[we][ns].getID(name);
	}
	
	public void deleteItem(int we, int ns, int id) 
	{
		map[we][ns].deleteItem(id);
	}
	
	public void lookItems(int we, int ns) 
	{
		if(map[we][ns] != null) map[we][ns].lookItems();
	}
	
	public void lookLoot(int we, int ns, Item[] loot) 
	{
		if(map[we][ns] != null) map[we][ns].lookLoot(loot);
	}
	
	public void lookDoors(int we, int ns) 
	{
		if(map[we][ns] != null) map[we][ns].lookDoors();
	}
	
	public void dropItem(int we, int ns, Item item) 
	{
		map[we][ns].dropItem(item);
	}
	
	public boolean isDead(int we, int ns) 
	{
		if(map[we][ns].isDead()) return true;
		else return false;
	}
	
	public void setDead(int we, int ns) 
	{
		map[we][ns].removeMonster();
	}
	
	public Monster getMonster(int we, int ns) 
	{
		return map[we][ns].getMonster();
	}
	
	public void dealDMG(int we, int ns, int dmg) 
	{
		map[we][ns].dealDMG(dmg);
	}
	
	public int getDMG(int we, int ns) 
	{
		return map[we][ns].getDMG();
	}
	
	public int getLevel(int we, int ns) 
	{
		return map[we][ns].getLevel();
	}
	
	public boolean existMonster(int we, int ns) 
	{
		return map[we][ns].existMonster();
	}
	
	private int level(int we, int ns) 
	{
		int rad_inc = 5;
		int radius = rad_inc;
		int i = 1;
		for(;; i++) 
		{
			if((MAPSIZE/2)-radius < we && we < (MAPSIZE/2)+radius && (MAPSIZE/2)-radius < ns && ns < (MAPSIZE/2)+radius)
			{
				break;
			}
			if(2 < rad_inc) 
			{
				radius += rad_inc;
				rad_inc--;
			} 
			else
			{
				radius += rad_inc;
			}
		}
		return i;
	}
	
	//Wraps back the coordinates if a call is made outside the map array.
	private int wrap(int coordinate) 
	{
		if(coordinate >= MAPSIZE) coordinate -= MAPSIZE;
		if(coordinate < 0) coordinate += MAPSIZE;
		return coordinate;
	}
	
	//Borrowed this code. 
	private <T> T[] concatenate(T[] arr1, T[] arr2) 
	{
	@SuppressWarnings("unchecked")
	T[] arr3 = (T[]) Array.newInstance(arr1.getClass().getComponentType(), arr1.length + arr2.length);
	System.arraycopy(arr1, 0, arr3, 0, arr1.length);
	System.arraycopy(arr2, 0, arr3, arr1.length, arr2.length);
	return arr3;
	}
	
}
