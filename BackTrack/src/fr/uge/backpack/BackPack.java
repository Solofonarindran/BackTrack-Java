package fr.uge.backpack;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import fr.uge.item.FightItem;
import fr.uge.item.Gold;
import fr.uge.item.Item;
import fr.uge.item.ManaStone;
import fr.uge.item.Ressources;

public class BackPack {
	
	private final IdentityHashMap<Item, List<Coordonate>> equipments ;
	
	private final List<List<Cell>> coordonates;
	
	private static int MAX_WIDTH = 7;
	private static int MAX_HEIGHT = 5;
	
	public BackPack() {
		equipments = new IdentityHashMap<Item, List<Coordonate>>();
		//coordonates = new HashMap<Coordonate,Map<String,Boolean>>();
		coordonates = new ArrayList<>();
		for(int x = 0; x < MAX_HEIGHT; x++) {
			List<Cell> row = new ArrayList<>();
			for(int y = 0; y < MAX_WIDTH; y++) {
				row.add(new Locked());
			}
			coordonates.add(row);
		}
	}
	
	// Exemple de structure de données
	
	//Coordonate(1,0) => unclocked  :{ true (déverouillé)}
	//							      { false (vérouillé)}
	
	//					 dispo		: { true (sans object) }
	//								: { false 						 }
	
	
	public void initializeStartingGrid() {
		// On prend le milieu de x et de y
	    IntStream.range(2, 5).forEach(x -> {
	        IntStream.range(1, 4).forEach(y -> {
	            coordonates.get(y).set(x, new Free()); 
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
		coordonates.get(coordonate.y()).set(coordonate.x(), new Free());
	}
	
	// mis à jour de la disponibilité de coordonées
	private void setCellToFree(Coordonate c) {
	    Objects.requireNonNull(c);
	    if (coordonates.get(c.y()).get(c.x()) instanceof Locked) {
	        throw new IllegalStateException("Impossible de libérer une case bloquée !");
	    }
	    coordonates.get(c.y()).set(c.x(), new Free());
	}

	private void setCellToItem(Coordonate c, Item item) {
	    Objects.requireNonNull(c);
	    Objects.requireNonNull(item);
	    coordonates.get(c.y()).set(c.x(), new ContainsItem(item));
	}

	// savoir si la place de coordonné peut contenir un équipement
	// check d'une seule coordonnée
	// l'ensemble des coordonées d'un item est checké dans le méthode addEquipement (var isAccepted)
	private boolean isAccepted(Coordonate c) {
		Objects.requireNonNull(c);
		return coordonates.get(c.y()).get(c.x()) instanceof Free;
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
		coordonateAbsolute.forEach(c -> setCellToItem(c, equipement)); // mettre les coordonnées indisponible
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
		coordAbsolutes.forEach(c->setCellToFree(c));
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
		
		// On récupère les coordonnées de l'item et on les set à free
		var oldCoordAbsolutes = equipments.get(equipment);
	    oldCoordAbsolutes.forEach(this::setCellToFree);
	    
	    // On récupère les nouvelles coordonnées possibles à jour
	    var references = equipment.references();
	    var newCoords = Coordonate.toAbsolute(references, newClickedCoord);
	    
	    boolean accepted = newCoords.stream().allMatch(this::isAccepted);

	    if (!accepted) {
	        // Si le placement voulu n'est pas possible -> on replace à l'endroit précédent
	        oldCoordAbsolutes.forEach(c -> setCellToItem(c, equipment));
	        return false;
	    }

	    // Mise à jour du sac
	    equipments.put(equipment, newCoords);
	    newCoords.forEach(c -> setCellToItem(c, equipment));
	    return true;
	}

	// Méthode qui effectue la rotation de l'objet et met à jour ses coordonnées
	public boolean rotateEquipment(Item item) {
		Objects.requireNonNull(item);
		var oldCoord = equipments.get(item);
		if (oldCoord == null) return false;
		
		// On utilise la méthode du pivot
		var pivot = oldCoord.get(0);
		
		// On remet les anciennes cellules à Free
		oldCoord.forEach(this::setCellToFree);
		
		var oldRelCoords = item.references();
		var rotatedEntry = ItemRotation.rotateItem(item, oldRelCoords);
		
		var newItem = rotatedEntry.keySet().iterator().next();
	    var newRelCoords = rotatedEntry.get(newItem);
	    var newAbsCoords = Coordonate.toAbsolute(newRelCoords, pivot);
	    boolean accepted = newAbsCoords.stream().allMatch(this::isAccepted);
	    if (!accepted) {
        // Restaurer ancien placement
	    	oldCoord.forEach(c -> setCellToItem(c, item));
	    	return false;
		}

	    equipments.remove(item);
	    equipments.put(newItem, newAbsCoords);
	    newAbsCoords.forEach(c -> setCellToItem(c, newItem));
	    return true;
	
	}
	
	/*
	 * Méthode qui remplace un item dans le backpack
	 * On récupère l'ancien item s'il existe
	 * On récuèore ses coordonnées
	 * On utilise la méthode du pivot pour recalculer ses nouvelles coordonées
	 * On met à jour ses anciennes coordonnées (à Free)
	 * On créé ses nouvelles coordonnées à partir du pivot
	 * On place l'item aux nouvelles coordonnées en fonction de leur validité
	 */
	public boolean replaceItem(Item newItem) {
	    Objects.requireNonNull(newItem);
	    // On trouve l'ancien item exact (même instance de classe + même nom)
	    var oldItemEntry = equipments.keySet().stream()
	    		/*
	    		 * A VOIR S'IL FAUT REECRIRE LE EQUALS + HASHCODE POUR CHAQUE CLASSE  
	    		 */
	            .filter(item -> item.getClass() == newItem.getClass() && item.name().equals(newItem.name()))
	            .findFirst();
	    
	    if (oldItemEntry.isEmpty()) return false;
	    
	    // On récupère l'item
	    Item oldItem = oldItemEntry.get();
	    
	    var oldCoords = equipments.get(oldItem);
	    // On utilise la méthode du pivot pour calculer les nouvelles coordonnées
        var pivot = oldCoords.get(0);
        oldCoords.forEach(this::setCellToFree);
	    var newCoords = Coordonate.toAbsolute(newItem.references(), pivot);

	    boolean accepted = newCoords.stream().allMatch(this::isAccepted);
	    // Si les coordonnées ne sont pas acceptées -> on restaure les anciennes cases
	    if (!accepted) {
	        oldCoords.forEach(c -> setCellToItem(c, oldItem));
	        return false;
	    }
	    equipments.remove(oldItem);
	    equipments.put(newItem, newCoords);

	    newCoords.forEach(c -> setCellToItem(c, newItem));

	    return true;
	}
	
	// Méthode qui vérifie qu'une case peut être débloquée pour étendre le backpack
	private boolean isExpandable(Coordonate c) {
	    Objects.requireNonNull(c);
	    
	    if (c.x() < 0 || c.x() >= MAX_WIDTH || c.y() < 0 || c.y() >= MAX_HEIGHT) {
	        return false;
	    }

	    // doit être Locked
	    if (!(coordonates.get(c.y()).get(c.x()) instanceof Locked)) {
	        return false;
	    }

	    // doit être collé à une case Free
	    return coordonates.get(c.y()).get(c.x() + 1) instanceof Free || 
	    	   coordonates.get(c.y()).get(c.x() - 1) instanceof Free || 
	    	   coordonates.get(c.y() + 1).get(c.x() + 1) instanceof Free || 
	    	   coordonates.get(c.y() - 1).get(c.x() + 1) instanceof Free;
	}
	
	// Méthode qui agrandit le backpack
	public void extendBackPack(int nbCases, List<Coordonate> clickedCoord) {
		Objects.requireNonNull(clickedCoord);
		int added = 0;
		for(var c: clickedCoord) {
			if(added >= nbCases) {
				break;
			}
			if(isExpandable(c)) {
				unlockedCoordonate(c);
				added++;
			}
		}
		if(added < nbCases) {
			IO.println("Toutes les cases n'ont pas pu être ajoutées.");
		}
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
