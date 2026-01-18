package fr.uge.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import fr.uge.model.Armor;
import fr.uge.model.ArmorType;
import fr.uge.model.Cell;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Curse;
import fr.uge.model.Gold;
import fr.uge.model.Item;
import fr.uge.model.Malediction;
import fr.uge.model.Rarity;
import fr.uge.model.Weapon;
import fr.uge.model.WeaponClass;
import fr.uge.model.WeaponType;

public class BackPack {

	private static final int MAX_WIDTH = 7;
	private static final int MAX_HEIGHT = 5;
	private final HashMap<Item, List<Coordonate>> equipments ;
	
	private Cell [] [] grids;
	
	
	public BackPack() {
		equipments = new HashMap<Item, List<Coordonate>>();
		grids = new Cell[MAX_HEIGHT][MAX_WIDTH];
	}
		
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
  	
  public void initializeStartingGrid() {
		var i = 0;
		for(var y = 0; y < MAX_HEIGHT; y++) {
			for(var x = 0; x < MAX_WIDTH; x++) {
			
				grids[y][x] =  ((x >=2) && (x <=4) && (y >= 1) && (y <=3)) ? new Cell(i, true, new Coordonate(x,y)) : new Cell(i, false,new Coordonate(x,y)); 
				i ++;
			}
		}
		
	}
	
	public void init() {
		initializeStartingGrid();
		initialItems();
	}
	
	public int getMaxHeight() {
		return MAX_HEIGHT;
	}
	
	public int getMaxWidth() {
		return MAX_WIDTH;
	}
	
	
	// cette méthode déverouille une case du sac.
	// déverouille signifie aussi de plus qu'il est dispo = true

//==================== EXTENSION DU SAC ====================

	/**
	* Déverrouille N nouvelles cases adjacentes aux cases existantes
	* @param count Nombre de cases à déverrouiller
	* @return Nombre de cases effectivement déverrouillées
	*/
	public int unlockNewSlots(int count) {
	   int unlocked = 0;
	   
	   for (int i = 0; i < count; i++) {
	       var nextSlot = findNextSlotToUnlock();
	       if (nextSlot != null) {
	           unlockedCoordonate(nextSlot);
	           unlocked++;
	       }
	   }
	   
	   return unlocked;
	}

	/**
	* Trouve la prochaine case à déverrouiller (adjacente à une case déjà déverrouillée)
	*/
	private Coordonate findNextSlotToUnlock() {
	   // Parcourir toutes les cases
	   for (int y = 0; y < MAX_HEIGHT; y++) {
	       for (int x = 0; x < MAX_WIDTH; x++) {
	           var coord = new Coordonate(x, y);
	           
	           // Si la case est déjà déverrouillée, passer
	           if (isUnlocked(coord)) {
	               continue;
	           }
	           
	           // Vérifier si elle est adjacente à une case déverrouillée
	           if (isAdjacentToUnlocked(coord)) {
	               return coord;
	           }
	       }
	   }
	   
	   return null;  // Aucune case disponible
	}

	/**
	* Vérifie si une coordonnée est adjacente à une case déverrouillée
	*/
	private boolean isAdjacentToUnlocked(Coordonate coord) {
	   int x = coord.x();
	   int y = coord.y();
	   
	   // Vérifier les 4 directions
	   if (x > 0 && isUnlocked(new Coordonate(x - 1, y))) return true;
	   if (x < MAX_WIDTH - 1 && isUnlocked(new Coordonate(x + 1, y))) return true;
	   if (y > 0 && isUnlocked(new Coordonate(x, y - 1))) return true;
	   if (y < MAX_HEIGHT - 1 && isUnlocked(new Coordonate(x, y + 1))) return true;
	   
	   return false;
	}

	/**
	* @return Nombre total de cases déverrouillées
	*/
	public int getUnlockedSlotCount() {
	   int count = 0;
	   for (int y = 0; y < MAX_HEIGHT; y++) {
	       for (int x = 0; x < MAX_WIDTH; x++) {
	           if (isUnlocked(new Coordonate(x, y))) {
	               count++;
	           }
	       }
	   }
	   return count;
	}
	
	/**
	* @return Nombre de cases encore verrouillées
	*/
		public int getLockedSlotCount() {
		   return (MAX_WIDTH * MAX_HEIGHT) - getUnlockedSlotCount();
		}
	
	public void unlockedCoordonate(Coordonate coordonate) {
		Objects.requireNonNull(coordonate);
		var x = coordonate.x();
		var y = coordonate.y();
		if( x > MAX_WIDTH ||  y > MAX_HEIGHT) {
			throw new IllegalArgumentException("Débordement du coordonné");
		}
		var cell = grids[y][x];
		cell.setVisible();
		grids[y][x] = cell;
	}
	
	private void updateCoordonateDispo(Coordonate coordonate, boolean value) {
		Objects.requireNonNull(coordonate);
		var x = coordonate.x();
		var y = coordonate.y();
		if( x > MAX_WIDTH ||  y > MAX_HEIGHT) {
			throw new IllegalArgumentException("Débordement du coordonné");
		}
		var cell = grids[y][x];
		cell.updateValueFree(value);
		grids[y][x] = cell;
	}
	
	
	 public boolean isAccepted(Coordonate coordonate) {
		 Objects.requireNonNull(coordonate);
		 var x = coordonate.x();
		 var y = coordonate.y();
		 
		 var cell = grids[y][x];
		 
		 if(Objects.isNull(cell)) {
			 return false;
		 }
		 return cell.isVisible() && cell.isFree();
		
	 }
	
	 private boolean isCurse(Item item) {
		 return switch(item) {
			 case Curse _ -> true;
			 default -> false;
		 };
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
		var isAccepted = coordonateAbsolute.stream().allMatch(c->isAccepted(c)); 
		
		if(!isAccepted && !isCurse(equipement)) {
			return false;
		}
		equipments.put(equipement, coordonateAbsolute);
		coordonateAbsolute.forEach(c->updateCoordonateDispo(c, false)); // mettre les coordonnées indisponible
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
		coordAbsolutes.forEach(c->updateCoordonateDispo(c, true));
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
			oldCoordAbsolutes.forEach(c->updateCoordonateDispo(c, true));
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
//  public boolean isUnlocked(Coordonate coordonate) {
//      Objects.requireNonNull(coordonate);
//      return coordonates.get(coordonate).get(UNLOCKED);   
//  }
	
	public boolean isUnlocked(Coordonate coordonate) {
		Objects.requireNonNull(coordonate);
		var x = coordonate.x();
		var y = coordonate.y();
		return grids[y][x].isVisible();
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
  
  public Map<Item, List<Coordonate>> itemsWithCoordonate() {
  	return Map.copyOf(equipments) ;
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
    absoluteCoords.forEach(c->updateCoordonateDispo(c, false));
		return destroyedItems;
	}
	
	public Set<Coordonate> getUnlockedCoordinates() {
		return Arrays.stream(grids)
								 .flatMap(g->Arrays.stream(g))
								 .filter(Cell::isVisible)
								 .map(Cell::coordonate)
								 .collect(Collectors.toSet());
	}
	
//==================== MALÉDICTION ====================

//Vérifier si on peut placer une malédiction à cette position
public boolean canPlaceCurse(Curse curse, Coordonate position) {
   for (var ref : curse.references()) {
       int x = position.x() + ref.x();
       int y = position.y() + ref.y();
       
       // Vérifier les limites du sac
       if (x < 0 || x >= MAX_WIDTH || y < 0 || y >= MAX_HEIGHT) {
           return false;
       }
   }
   return true;
}

//Supprimer tous les items aux positions données
public void removeItemsAt(List<Coordonate> references, Coordonate basePosition) {
   var itemsToRemove = new java.util.HashSet<Item>();
   
   for (var ref : references) {
       int x = basePosition.x() + ref.x();
       int y = basePosition.y() + ref.y();
       
       // Trouver l'item à cette position
       var item = getItemAt(new Coordonate(x, y));
       if (item != null) {
           itemsToRemove.add(item);
       }
   }
   
   // Supprimer tous les items trouvés
   for (var item : itemsToRemove) {
       removeEquipment(item);
   }
}

//Placer une malédiction (force la position, écrase les items)
public void placeCurse(Curse curse, Coordonate position) {
   addEquipement(curse, position);
}


	// =============== GETTERS POUR VIEW PROF ====================
	
	//Map des items avec ses coordonnées
	public Map<Item, List<Coordonate>> getItems() {
		return Collections.unmodifiableMap(equipments);
	}
	
	
// total des cases dispo
	
	public int getTotalSlots() {
		return Arrays.stream(grids)
								 .flatMap(a->Arrays.stream(a))
								 .filter(c->c.isVisible())
								 .mapToInt(_->1)
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
	
	public Cell[][] grids() {
		return grids;
	}
}
