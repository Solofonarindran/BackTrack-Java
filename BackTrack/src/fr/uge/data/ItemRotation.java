package fr.uge.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.model.Coordonate;
import fr.uge.model.Item;

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
	
	private boolean canRotate(Item item ) {
		Objects.requireNonNull(item);
		return item.rotate();
	}
}
