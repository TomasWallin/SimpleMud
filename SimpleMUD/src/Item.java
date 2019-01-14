
public abstract class Item {

		private String name;
		private String description;
		private static int item_num;
		private int id;
		private boolean heavy;
		
		public Item(String name, String description) {
			this.name = name;
			this.description = description;
			item_num++;
			id = item_num;
		}
		
		public Item(String name, String description, boolean heavy) {
			this.name = name;
			this.description = description;
			item_num++;
			id = item_num;
			this.heavy = heavy;
		}
	
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public int getID() {
			return id;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public void setHeavy(boolean heavy) {
			this.heavy = heavy;
		}
		
		public boolean getHeavy() {
			return heavy;
		}
	}