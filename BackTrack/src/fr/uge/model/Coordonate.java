package fr.uge.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Coordonate(int x, int y) {
	public Coordonate {
		if( x < 0 || y < 0 ) {
			throw new IllegalArgumentException();
		}
	}
	
	//cette méthode retourne la coordonné relative à la référence et la coordonné c venant d'interface zen
	private static Coordonate relativeTo(Coordonate reference, Coordonate c) {
		Objects.requireNonNull(reference);
		Objects.requireNonNull(c);
		return new Coordonate(reference.x() + c.x(), reference.y() + c.y());
	}
	
//Méthode toAbsolute retourne une liste de Coordonnées absolute 
	// placée dans le sac à dos
	// Les coordonées qu'un objet s'occupe dans le sac
	public static List<Coordonate> toAbsolute(List<Coordonate> references, Coordonate c){
		Objects.requireNonNull(c);
		var referencesCopy = new ArrayList<Coordonate>(references);
		var absolutes = new ArrayList<Coordonate>();
		
		referencesCopy.forEach(r->absolutes.add(relativeTo(r, c)));
		return absolutes;
	}
	
}
