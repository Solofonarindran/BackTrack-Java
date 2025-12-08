package fr.uge.data;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import fr.uge.model.Coordonate;
import fr.uge.model.FightItem;
import fr.uge.model.Gold;
import fr.uge.model.Item;
import fr.uge.model.ManaStone;
import fr.uge.model.Ressources;

public class BackPack {
	
	private final IdentityHashMap<Item, List<Coordonate>> equipments ;
	
	// ce map contient les places (coordonnées) qui sont déverouillés (unlocked) et dispo
	private final HashMap<Coordonate,Map<String,Boolean>> coordonates;
	
	private static final String UNLOCKED = "unlocked";
	private static final String DISPO = "dispo";
	
	private static final int MAX_WIDTH = 7;
	private static final int MAX_HEIGHT = 5;
	
	public BackPack() {
		equipments = new IdentityHashMap<Item, List<Coordonate>>();
		coordonates = new HashMap<Coordonate,Map<String,Boolean>>();
	}
	
	// Exemple de structure de données
	
	//Coordonate(1,0) => unclocked  :{ true (déverouillé)}
	//							      { false (vérouillé)}
	
	//					 dispo		: { true (sans object) }
	//								: { false 						 }
	
	

	// initialisation au milieu du sac
//	public void initializeStartingGrid() {
//		var intStreamWidth = IntStream.range(2, 5); // prendre le milieu de x
//		var intStreamHeight = IntStream.range(1, 4); // prendre le milieu de y 
//		
//		intStreamWidth.forEach(x->{
//			intStreamHeight.forEach(y->{
//				var coord = new Coordonate(x, y);
//				var state = new HashMap<String,Boolean>();
//				state.put(UNLOCKED, true);
//				state.put(DISPO, true);
//				
//				coordonates.put(coord, state);
//			});
//		});
//	}
	
	public void initializeStartingGrid() {
	    IntStream.range(2, 5).forEach(x -> {
	        IntStream.range(1, 4).forEach(y -> {
	            var coord = new Coordonate(x, y);
	            var state = new HashMap<String, Boolean>();
	            state.put(UNLOCKED, true);
	            state.put(DISPO, true);
	            coordonates.put(coord, state);
	        });
	    });
	}
	
	// cette méthode déverouille une case du sac.
	// déverouille signifie aussi de plus qu'il est dispo = true
	public void unlockedCoordonate(Coordonate coordonate) {
		Objects.requireNonNull(coordonate);
		if(coordonate.x() > MAX_WIDTH || coordonate.y() > MAX_HEIGHT) {
			throw new IllegalArgumentException("Débordement du coordonné");
		}
		var state = coordonates.computeIfAbsent(coordonate, b ->new HashMap<String, Boolean>());
		state.put(UNLOCKED, true);
		state.put(DISPO, true);
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
		 .filter(e->e.getKey().equals(coordonate) && e.getValue().get(UNLOCKED))
									 .map(e->e.getValue().get(DISPO))
									 .findFirst()
									 .orElseGet(()->false);
	}
	
	
	// ajout d'un équipement dans le sac
	public boolean addEquipement(Item equipement,Coordonate clickedCoord) {
		// clickedCoord ( données ou coordonnées récuperées venant d'interface zen)
		Objects.requireNonNull(equipement);
		Objects.requireNonNull(clickedCoord);	
		var references = equipement.references();
		
		// voir les coordonées réelles disponible dans le sac suivant les réferences et clickedCoord
		var coordonateAbsolute = Coordonate.toAbsolute(references, clickedCoord);
		// vérifie si aucune réponse est false
		var isAccepted = coordonateAbsolute.stream().allMatch(this::isAccepted); 

		if(!isAccepted) {
			return false;
		}
		equipments.put(equipement, coordonateAbsolute);
		coordonateAbsolute.forEach(c->upgradeCoordonateDispo(c, false)); // mettre les coordonnées indisponible
		return true;
	}
	
	// enlever l'item dans sac
	public boolean removeEquipment(Item equipment) {
		Objects.requireNonNull(equipment);
		if(!equipments.containsKey(equipment)) {
			return false;
		}
		
		//recuperer les coordonées occupées par item
		var coordAbsolutes = equipments.get(equipment);
		
		//Libérer les cases 
		coordAbsolutes.forEach(c->upgradeCoordonateDispo(c, true));
		equipments.remove(equipment);
		return true;
	}
	
	// déplacer un equipement ou item
	public boolean moveEquipment(Item equipment, Coordonate newClickedCoord) {
		Objects.requireNonNull(equipment);
		Objects.requireNonNull(newClickedCoord);
		
		if(!equipments.containsKey(equipment)) {
			return false;
		}
		
		var oldCoordAbsolutes = equipments.get(equipment);
		var accepted = addEquipement(equipment, newClickedCoord);
		
		if(accepted) {
		//les anciens coordonées sont disponibles
			oldCoordAbsolutes.forEach(c->upgradeCoordonateDispo(c, true));
		}
		return accepted;
	}
	
	
	public void rotateEquipment(Item item) {
		Objects.requireNonNull(item);
		var oldCoord = equipments.get(item);
		
		// Map<Item,List<Coordonate>>
		var rotatedItem = ItemRotation.rotateItem(item, oldCoord);
		//var success = false;
		rotatedItem.entrySet().forEach(e->{
			moveEquipment(e.getKey(), e.getValue().getFirst());
		});
	
	}
	
	public void replaceItem(Item newItem) {
	    Objects.requireNonNull(newItem);
	    // trouver l'ancien item exact (même instance de classe + même nom)
	    var oldItemEntry = equipments.keySet().stream()
	            .filter(item -> item.getClass() == newItem.getClass() && item.name().equals(newItem.name()))
	            .findFirst();

	    oldItemEntry.ifPresent(old -> {
	        var oldCoords = equipments.get(old);
	        removeEquipment(old); // libère les cases
	        // créer le nouvel item mais en réutilisant les mêmes coordonnées
	        equipments.put(newItem, oldCoords);
	        oldCoords.forEach(c -> upgradeCoordonateDispo(c, false));
	    });
	}


	public List<Item> getItem() {
		return equipments.keySet()
				.stream()
                .toList();
	}
	
	public Gold getGold() {
	    return equipments.keySet()
	    		.stream()
	            .filter(item -> item instanceof Gold)
	            .map(item -> (Gold) item)
	            .findFirst()
	            .orElseThrow(() -> new IllegalStateException("Pas d'or dans le sac"));
	}
	
	public ManaStone getManaStone() {
	    return equipments.keySet()
	    		.stream()
	            .filter(item -> item instanceof ManaStone)
	            .map(item -> (ManaStone) item)
	            .findFirst()
	            .orElse(null);
	}
	
	@Override
	public String toString() {
	    var items = getItem(); // récupère la liste des items
	    if (items.isEmpty()) {
	        return "Le sac est vide.";
	    }

	    StringBuilder sb = new StringBuilder("Contenu du sac :\n");
	    for (int i = 0; i < items.size(); i++) {
	        Item item = items.get(i);
	        sb.append(i + 1).append(". ").append(item.name()); // numérotation + nom
	        var coords = equipments.get(item);
	        // si c'est un FightItem, afficher stats
	        if (item instanceof FightItem fightItem) {
	            sb.append(" | Damage: ").append(fightItem.damage())
	              .append(" | Protection: ").append(fightItem.protection())
	              .append(" | Coût: ").append(fightItem.cost())
	              .append(" ").append(fightItem.moneyType());

	        }
	        
	        if (item instanceof Ressources money) {
	        	sb.append(" | Quantité : ").append(money.quantity());
	        }
	        
	        if (coords != null) {
	            sb.append(" | Occupe : ").append(coords);
	        }
	        
	        sb.append("\n");
	    }

	    return sb.toString();
	}

}
