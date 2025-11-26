package fr.uge.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.model.Armor;
import fr.uge.model.Clef;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Gold;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Weapon;

public class ItemRotation {
	
	private static List<Coordonate> rotate90Clockwise(List<Coordonate> coordonates) {
		if (coordonates.isEmpty()) {
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
			case Armor a -> Map.of(new Armor(a.name(), a.type(), a.rarity(),a.references(), a.cost(),a.defensePoint(),a.urlImage()), newCoordinates);
			case Weapon w -> Map.of(new Weapon(w.name(), w.type(), w.classe(), w.cost(), w.healthPoint(), w.rarity(), w.references()), newCoordinates);
			case Magic m -> Map.of(new Magic(m.name(), m.type(), m.rarity(), m.manaMax(), m.urlImage(), m.references()), newCoordinates);
			case Gold g -> Map.of(new Gold(g.number(), g.rarity(), g.urlImage(),g.references()), newCoordinates);
			case Consumable c -> Map.of(new Consumable(c.name(), c.references()), newCoordinates);
			case Clef cf -> Map.of(new Clef(cf.rarity(), cf.urlImage(),cf.references()), newCoordinates);
			default ->  Map.of(item, coordinates);
		};
	}
	
	public static Map<Item,List<Coordonate>> rotateItem(Item item, List<Coordonate> coordinates) {
		Objects.requireNonNull(item);
		
		//bloquer Malediction
		if(!canRotate(item)) {
			return Map.of(item, coordinates);
		}
		var newCoordinates = rotate90Clockwise(coordinates);
    return patternMatching(item, coordinates, newCoordinates);
		
	}
}
