package fr.uge.model;

import java.util.Objects;

/**
 * Position d'une salle dans l'étage (grille 5x11).
 */
public record Position(int x, int y) {
    
    public Position {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordonnées négatives non autorisées");
        }
    }
    
    /**
     * Calcule la position voisine dans une direction
     */
    public Position move(Direction direction) {
        Objects.requireNonNull(direction);
        return new Position(x + direction.getDx(), y + direction.getDy());
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
    public int manhattanDistance(Position other) {
        Objects.requireNonNull(other);
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
    
    /**
     * Vérifie si deux positions sont adjacentes
     */
    public boolean isAdjacentTo(Position other) {
        Objects.requireNonNull(other);
        return manhattanDistance(other) == 1;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}