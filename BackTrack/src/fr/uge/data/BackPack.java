package fr.uge.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.uge.model.Coordonate;
import fr.uge.model.Item;

public class BackPack {
	private final ArrayList<Item> items ;
	
	// ce map contient les places (coordonnées) qui sont déverouillés (unlock) et dispo
	private final HashMap<Coordonate,Map<String,Boolean>> coordonates;
	
	public BackPack() {
		items = new ArrayList<Item>();
		coordonates = new HashMap<Coordonate,Map<String,Boolean>>();
	}
	
}
