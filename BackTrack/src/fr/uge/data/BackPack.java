package fr.uge.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.model.Coordonate;
import fr.uge.model.Item;

public class BackPack {
	private final HashMap<Item, List<Coordonate>> equipments ;
	
	// ce map contient les places (coordonnées) qui sont déverouillés (unlock) et dispo
	
	private final HashMap<Coordonate,Map<String,Boolean>> coordonates;
	
	public BackPack() {
		equipments = new HashMap<Item, List<Coordonate>>();
		coordonates = new HashMap<Coordonate,Map<String,Boolean>>();
	}
	
	// Exemple de structure de données
	
	//Coordonate(1,0) => unclocked  :{ true (déverouillé)}
	//															 { false (vérouillé)}
	
	//									 dispo		: { true (sans object) }
	//														: { false 						 }
	
	

	private void upgradeCoordonateDispo(Coordonate coordonate, boolean value) {
		Objects.requireNonNull(coordonate);
		coordonates.entrySet().stream()
													.filter(e->e.getKey().equals(coordonate))
													.findFirst()
													.ifPresent(e->e.getValue().put("dispo", value));
													
	}
	
	private boolean isAccepted(Item equipement, Coordonate coordonate) {
		
		Objects.requireNonNull(equipement);
		Objects.requireNonNull(coordonate);
		return coordonates.entrySet().stream()
									 .filter(e->e.getKey().equals(coordonate) && e.getValue().get("unlocked"))
									 .map(e->e.getValue().get("dispo"))
								   .findFirst()
									 .orElseGet(()->false);
	}
	
	
	public void addEquipement(Item equipement,Coordonate coordonate) {
		// coordonate ( données ou coordonnées récuperées venant d'interface zen)
		Objects.requireNonNull(equipement);
		Objects.requireNonNull(coordonate);	
		var references = equipement.references();
		var coordonateAbsolute = Coordonate.toAbsolute(references, coordonate);
		// crée une copie pour manipuler 
		var coordonateAbsoluteCopy = new ArrayList<Coordonate>(coordonateAbsolute);
		var isAccepted = coordonateAbsoluteCopy.stream()
													.map(c->isAccepted(equipement, c))
													.noneMatch(b->!b); // vérifie si aucune réponse est false
		
		if(isAccepted) {
			equipments.put(equipement, coordonateAbsolute);
		}
		
													
	}
	
}
