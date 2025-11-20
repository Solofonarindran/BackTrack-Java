package fr.uge.data;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.model.Coordonate;
import fr.uge.model.Item;

public class BackPack {
	private final HashMap<Item, List<Coordonate>> equipements ;
	
	// ce map contient les places (coordonnées) qui sont déverouillés (unlock) et dispo
	
	private final HashMap<Coordonate,Map<String,Boolean>> coordonates;
	
	public BackPack() {
		equipements = new HashMap<Item, List<Coordonate>>();
		coordonates = new HashMap<Coordonate,Map<String,Boolean>>();
	}
	
	// Exemple de structure de données
	
	//Coordonate(1,0) => unclocked  :{ true (déverouillé)}
	//								 { false (vérouillé)}
	
	//					 dispo		: { true (sans object) }
	//								: { false 						 }
	private boolean isAccepted(Item equipement, Coordonate coordonate) {
		
		Objects.requireNonNull(equipement);
		Objects.requireNonNull(coordonate);
		return coordonates.entrySet().stream()
									 .filter(e->e.getKey().equals(coordonate) && e.getValue().get("unlocked"))
									 .map(e->e.getValue().get("dispo"))
								     .findFirst()
									 .orElseGet(()->false);
	}
	
	public void addEquipement(Item equipement) {
		Objects.requireNonNull(equipement);
	}
	
}
