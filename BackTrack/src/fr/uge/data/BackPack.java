package fr.uge.data;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.model.Coordonate;
import fr.uge.model.Item;

public class BackPack {
	private final HashMap<Item, List<Coordonate>> items ;
	
	// ce map contient les places (coordonnées) qui sont déverouillés (unlock) et dispo
	private final HashMap<Coordonate,Map<String,Boolean>> coordonates;
	
	public BackPack() {
		items = new HashMap<Item, List<Coordonate>>();
		coordonates = new HashMap<Coordonate,Map<String,Boolean>>();
	}
	
}
