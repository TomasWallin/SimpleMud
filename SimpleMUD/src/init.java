import java.io.IOException;

public class init {

	private static final int MAPSIZE = 64;
	private static final int ITEM_SPAWN_RATE = 18;
	private static final int CHEST_SPAWN_RATE = 13;
	private static final int MONSTER_SPAWN_RATE = 16;
	
	public static void main(String[] args) throws IOException
	{
		
		//Init objects
		World world = new World(MAPSIZE,MAPSIZE,ITEM_SPAWN_RATE, CHEST_SPAWN_RATE, MONSTER_SPAWN_RATE);
		Player player = new Player(MAPSIZE/2,MAPSIZE/2, MAPSIZE);
		player.placeInWorld(world);
		player.run();

	}

}
