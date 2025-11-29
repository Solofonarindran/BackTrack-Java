package fr.uge.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import fr.uge.model.Coordonate;
import fr.uge.model.Item;

public class BackPack {
	
	private final HashMap<Item, List<Coordonate>> equipments ;
	
	// ce map contient les places (coordonnées) qui sont déverouillés (unlocked) et dispo
	private final HashMap<Coordonate,Map<String,Boolean>> coordonates;
	
	private static final String UNCLOCKED = "uncloked";
	private static final String DISPO = "dispo";
	
	private static final int MAX_WIDTH = 7;
	private static final int MAX_HEIGHT = 5;
	
	public BackPack() {
		equipments = new HashMap<Item, List<Coordonate>>();
		coordonates = new HashMap<Coordonate,Map<String,Boolean>>();
	}
	
	// Exemple de structure de données
	
	//Coordonate(1,0) => unclocked  :{ true (déverouillé)}
	//							      { false (vérouillé)}
	
	//					 dispo		: { true (sans object) }
	//								: { false 						 }
	
	

	// initialisation au milieu du sac
	public void initializeStartingGrid() {
		var intStreamWidth = IntStream.range(2, 5); // prendre le milieu de x
		var intStreamHeight = IntStream.range(1, 4); // prendre le milieu de y 
		
		intStreamWidth.forEach(x->{
			intStreamHeight.forEach(y->{
				var coord = new Coordonate(x, y);
				var state = new HashMap<String,Boolean>();
				state.put(UNCLOCKED, true);
				state.put(DISPO, true);
				
				coordonates.put(coord, state);
			});
		});
	}
	
	public void unclockedCoordonate(Coordonate coordonate) {
		Objects.requireNonNull(coordonate);
		coordonates.entrySet().stream()
													.filter(e->e.getKey().equals(coordonate))
													.findFirst()
													.ifPresent(e->e.getValue().put(UNCLOCKED, true));
	}
	
	// mis à jour de la disponibilité de coordonées
	// value true si dispo
	private void upgradeCoordonateDispo(Coordonate coordonate, boolean value) {
		Objects.requireNonNull(coordonate);
		coordonates.entrySet().stream()
													.filter(e->e.getKey().equals(coordonate))
													.findFirst()
													.ifPresent(e->e.getValue().put(DISPO, value));

	}
	
	// savoir si la place de coordonné peut contenir un équipement
	// check d'une seule coordonnée
	// l'ensemble des coordonées d'un item est checké dans le méthode addEquipement (var isAccepted)
	private boolean isAccepted(Coordonate coordonate) {
		Objects.requireNonNull(coordonate);
		return coordonates.entrySet().stream()
		 .filter(e->e.getKey().equals(coordonate) && e.getValue().get(UNCLOCKED))
									 .map(e->e.getValue().get(DISPO))
								   .findFirst()
									 .orElseGet(()->false);
	}
	
	
	// ajout d'un équipement 
	public void addEquipement(Item equipement,Coordonate coordonate) {
		// coordonate ( données ou coordonnées récuperées venant d'interface zen)
		Objects.requireNonNull(equipement);
		Objects.requireNonNull(coordonate);	
		var references = equipement.references();
		var coordonateAbsolute = Coordonate.toAbsolute(references, coordonate);
	
		var isAccepted = coordonateAbsolute.stream().map(c->isAccepted(c)).noneMatch(b->!b); // vérifie si aucune réponse est false

		if(isAccepted) {
			equipments.put(equipement, coordonateAbsolute);
			coordonateAbsolute.forEach(c->upgradeCoordonateDispo(c, true));
		}
										
	}
	
}
