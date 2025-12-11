package fr.uge.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Coordonate(int x, int y) {
//	public Coordonate {
//		if( x < 0 || y < 0 ) {
//			throw new IllegalArgumentException();
//		}
//	}
	
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
	
  /**
   * Calcule la position voisine dans une direction
   */
  public Coordonate move(Direction direction) {
      Objects.requireNonNull(direction);
      return new Coordonate(x + direction.getDx(), y + direction.getDy());
  }
  
  /**
   * Vérifie si la position est dans les limites
   */
  public boolean isInBounds(int width, int height) {
      return x >= 0 && x < width && y >= 0 && y < height;
  }
  
  /**
   * Calcule la distance de Manhattan vers une autre position
   */
  public int manhattanDistance(Coordonate other) {
      Objects.requireNonNull(other);
      return Math.abs(x - other.x) + Math.abs(y - other.y);
  }
  
  /**
   * Vérifie si deux positions sont adjacentes
   */
  public boolean isAdjacentTo(Coordonate other) {
      Objects.requireNonNull(other);
      return manhattanDistance(other) == 1;
  }
  
  @Override
  public String toString() {
      return "(" + x + ", " + y + ")";
  }
	
}
