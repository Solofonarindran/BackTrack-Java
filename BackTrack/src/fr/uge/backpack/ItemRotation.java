package fr.uge.backpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.item.Armor;
import fr.uge.item.Clef;
import fr.uge.item.Consumable;
import fr.uge.item.Gold;
import fr.uge.item.Item;
import fr.uge.item.Magic;
import fr.uge.item.Weapon;

public class ItemRotation {
	
	private static List<Coordonate> rotate90Clockwise(List<Coordonate> coordonates) {
		if (coordonates == null || coordonates.isEmpty()) {
			return new ArrayList<Coordonate>();
		}
		
		//Etape 1 Appliquer la rotation mathématique (x,y) -> (y, -x)
		var rotated = new ArrayList<Coordonate>();
		coordonates.forEach(c->rotated.add(new Coordonate(c.y(), -c.x())));
		
		//Etape 2 Normaliser pour avoir uniquement des coordonnées positives 
		//trouver le minimum ne X et Y
			var minX = rotated.stream().mapToInt(Coordonate::x).min().orElse(0);
			var minY = rotated.stream().mapToInt(Coordonate::y).min().orElse(0);
			var normalised = new ArrayList<Coordonate>();
			
			rotated.forEach(c->normalised.add(new Coordonate(c.x()-minX, c.y()- minY)));
			return normalised;
	}
	
	private static boolean canRotate(Item item ) {
		Objects.requireNonNull(item);
		return item.rotate();
	}
	
	private static Map<Item, List<Coordonate>> patternMatching(Item item, List<Coordonate> coordinates, List<Coordonate> newCoordinates) {
		Objects.requireNonNull(item);
		return switch (item) {
			case Armor a -> Map.of(new Armor(a.name(), a.type(), a.rarity(),a.damage(), a.protection(), a.moneyType(), a.cost(), a.references()), newCoordinates);
			case Weapon w -> Map.of(new Weapon(w.name(), w.type(), w.classe(), w.rarity(), w.damage(), w.protection(), w.moneyType(), w.cost(), w.references()), newCoordinates);
			case Magic m -> Map.of(new Magic(m.name(), m.type(), m.rarity(), m.damage(), m.protection(), m.moneyType(), m.cost(), m.references()), newCoordinates);
			case Gold g -> Map.of(new Gold(g.name(), g.rarity(), g.quantity(),g.references()), newCoordinates);
			case Consumable c -> Map.of(new Consumable(c.name(), c.rarity(), c.references()), newCoordinates);
			case Clef cf -> Map.of(new Clef(cf.name(), cf.rarity(),cf.references()), newCoordinates);
			default ->  Map.of(item, coordinates);
		};
	}
	
	public static Map<Item,List<Coordonate>> rotateItem(Item item, List<Coordonate> coordinates) {
		Objects.requireNonNull(item);
		
		if (coordinates == null) {
			throw new IllegalArgumentException("No coordinates provided for item: " + item.name());
		}
		
		//bloquer Malediction
		if(!canRotate(item)) {
			return Map.of(item, coordinates);
		}
		var newCoordinates = rotate90Clockwise(coordinates);
    return patternMatching(item, coordinates, newCoordinates);
		
	}

}
 