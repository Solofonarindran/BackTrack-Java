package fr.uge.item;

import java.util.ArrayList;
import java.util.List;

public class FightItemsFactory {

	private static final List<FightItem> FIGHT_ITEMS = initFightItems();
		
	  // Méthode qui initialise le catalogue des objets de combat disponibles 
	public static List<FightItem> initFightItems() {
		List<FightItem> list = new ArrayList<>();
		for(var item: ItemFactory.getCatalog()) {
			if (item instanceof FightItem fightItem) {
				list.add(fightItem);
			}
		}
	    return List.copyOf(list);    
	}
	  
	public static List<FightItem> getfightItems() {
	    return FIGHT_ITEMS;
     }
}
