package fr.uge.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.uge.model.Armor;
import fr.uge.model.ArmorType;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Gold;
import fr.uge.model.Item;
import fr.uge.model.Malediction;
import fr.uge.model.Rarity;
import fr.uge.model.Weapon;
import fr.uge.model.WeaponClass;
import fr.uge.model.WeaponType;

public class BackPack {
	
	private final HashMap<Item, List<Coordonate>> equipments ;
	
	// ce map contient les places (coordonnées) qui sont déverouillés (unlocked) et dispo
	private final HashMap<Coordonate,Map<String,Boolean>> coordonates;
	
	private static final String UNLOCKED = "unloked";
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
	
	
  public void initialItems() {
  	 // Épée verticale (3 cases)
    var sword = new Weapon("Épée en bois",WeaponType.SWORDS,WeaponClass.MELLER,1,6,Rarity.COMMON,
    		List.of(new Coordonate(0, 0),new Coordonate(0, 1),new Coordonate(0, 2)));
    
    // Bouclier 2×2
    var shield = new Armor("Bouclier en bois",ArmorType.SHIELD,Rarity.COMMON,
        List.of(new Coordonate(0, 0),new Coordonate(1, 0),new Coordonate(0, 1),new Coordonate(1, 1)),1,7,"images");
    
    // Potion 1×1
    var potion = new Consumable("Potion de santé",List.of(new Coordonate(0, 0)));
    
    // Or 1×1
    var gold = new Gold(50,Rarity.COMMON,"images",List.of(new Coordonate(0, 0)));
    
    // Placement dans le sac (zone 3×3 : colonnes 2-4, lignes 1-3)
    addEquipement(sword, new Coordonate(2, 1));
    addEquipement(shield, new Coordonate(3, 1));
    addEquipement(potion, new Coordonate(2, 3));
    addEquipement(gold, new Coordonate(4, 3));
  }
  
	// initialisation au milieu du sac
	public void initializeStartingGrid() {
		
		for(var x = 2; x <= 4; x++) {
			for(var y = 1; y <= 3; y++) {
				var coord = new Coordonate(x, y);
				var state = new HashMap<String,Boolean>();
				state.put(UNLOCKED, true);
				state.put(DISPO, true);
				
				coordonates.put(coord, state);
			}
		}
	}
	
	public void init() {
		initialItems();
		initializeStartingGrid();
	}
	
	public int getMaxHeight() {
		return MAX_HEIGHT;
	}
	
	public int getMaxWidth() {
		return MAX_WIDTH;
	}

//	public void unlockedCoordonate(Coordonate coordonate) {
//		Objects.requireNonNull(coordonate);
//		coordonates.entrySet().stream()
//													.filter(e->e.getKey().equals(coordonate))
//													.findFirst()
//													.ifPresent(e->e.getValue().put(UNLOCKED, true));
//	}
	
	
	// cette méthode déverouille une case du sac.
	// déverouille signifie aussi de plus qu'il est dispo = true
	public void unlockedCoordonate(Coordonate coordonate) {
		Objects.requireNonNull(coordonate);
		if(coordonate.x() > MAX_WIDTH || coordonate.y() > MAX_HEIGHT) {
			throw new IllegalArgumentException("Débordement du coordonné");
		}
		var state = coordonates.computeIfAbsent(coordonate, _ ->new HashMap<String, Boolean>());
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
	/**
   * Vérifie si une case peut accueillir un item (déverrouillée ET disponible)
   * @param coordonate La coordonnée à vérifier
   * @return true si la case est libre
   */
	public boolean isAccepted(Coordonate coordonate) {
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
	
	/**
	 * 
	 * @param equipment 
	 * @param newClickedCoord
	 * @return
	 */
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
	
	
	public boolean rotateEquipment(Item item) {
		Objects.requireNonNull(item);
		var oldCoord = equipments.get(item);
		
		// Map<Item,List<Coordonate>>
		var rotatedItem = ItemRotation.rotateItem(item, oldCoord);
		//var success = false;
		rotatedItem.entrySet().forEach(e->{
			moveEquipment(e.getKey(), e.getValue().getFirst());
		});
		if(oldCoord.equals(rotatedItem.get(item))) {
			return true;
		}
		return false;
	}
	
	/* retourne les coordonnées occupées par un item*/
	public List<Coordonate> getItemCoordinates(Item item) {
			Objects.requireNonNull(item);
			return equipments.getOrDefault(item, new ArrayList<>());
	}
	
  /**
   * Vérifie si une case est déverrouillée (existe dans le sac)
   */
  public boolean isUnlocked(Coordonate coordonate) {
      Objects.requireNonNull(coordonate);
      return coordonates.containsKey(coordonate) && 
             coordonates.get(coordonate).get(UNLOCKED);
  }
	
  //Item by coordinate
  //Même si une partie d'item est concernée. 
  public Item getItemAt(Coordonate c) {
  	return equipments.entrySet().stream()
  											.filter(e->e.getValue().contains(c))
  											//.map(e->e.getKey())
  											.map(Map.Entry::getKey)
  											.findFirst()
  											.orElseGet(()->null);

  }
  
  public boolean contains(Item item) {
  	Objects.requireNonNull(item);
  	return equipments.containsKey(item);
  }
  
  public List<Item> getAllItems() {
  	return equipments.entrySet().stream()
  															.map(Map.Entry::getKey)
  															.toList();
  }
  //retourne la liste des items débarassés
	public List<Item> forceMalediction(Malediction malediction,Coordonate clickedCoordinate) {
		Objects.requireNonNull(malediction);
		Objects.requireNonNull(clickedCoordinate);
		
		// La liste des items en conflit
		var destroyedItems = new ArrayList<Item>();
		//calculer les cases que la malediction occupera
		var references = malediction.references();
		var absoluteCoords = Coordonate.toAbsolute(references, clickedCoordinate);
		
	// Vérifier que toutes les cases sont déverrouillées
    boolean allUnlocked = absoluteCoords.stream()
        									.allMatch(this::isUnlocked);
    
    if(!allUnlocked) {
    	return destroyedItems;
    }
    absoluteCoords.forEach(c-> {
    	var itemAt = getItemAt(c);
    	if(!Objects.isNull(itemAt) ) {
    		destroyedItems.add(itemAt);
    	}
    });
    
    //Détruire les items en conflit
    destroyedItems.forEach(this::removeEquipment);
    
    equipments.put(malediction, absoluteCoords);
    absoluteCoords.forEach(c->upgradeCoordonateDispo(c, false));
		return destroyedItems;
	}
	
	public Set<Coordonate> getUnlockedCoordinates() {
		return coordonates.entrySet().stream()
																 .filter(e->e.getValue().get(UNLOCKED))
																 //.map(e->e.getKey())
																 .map(Map.Entry::getKey)
																 .collect(Collectors.toSet());
	}
	
	// =============== GETTERS POUR VIEW PROF ====================
	
	//Map des items avec ses coordonnées
	public Map<Item, List<Coordonate>> getItems() {
		return Collections.unmodifiableMap(equipments);
	}
	
	
	// total des déverouillés
	public int getTotalSlots() {
		return coordonates.values().stream()
															  .filter(state ->state.get(UNLOCKED))
															  .mapToInt(_ -> 1)
															  .sum();
	}
	
	
	//return le total des cases occupées
	public int getOccupiedSlots() {
		return equipments.values().stream()
															.mapToInt(List::size)
															.sum();
	}
	
	public int getItemCount() {
		return equipments.size();
	}
}
