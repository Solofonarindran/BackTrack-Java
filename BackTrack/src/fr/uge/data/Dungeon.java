package fr.uge.data;
import java.util.ArrayList;
import java.util.List;


/**
 * Représente le donjon complet avec tous ses étages.
 */
public class Dungeon {
    
    private final List<Floor> floors;
    private int currentFloorIndex;
    private final int totalFloors;
    
    public Dungeon(int totalFloors) {
        if (totalFloors <= 0) {
            throw new IllegalArgumentException("Le donjon doit avoir au moins un étage");
        }
        this.totalFloors = totalFloors;
        this.floors = new ArrayList<>();
        this.currentFloorIndex = 0;
        
        // Générer le premier étage
        floors.add(new Floor(1));
    }
    
    /**
     * @return l'étage actuel
     */
    public Floor getCurrentFloor() {
        return floors.get(currentFloorIndex);
    }
    
    /**
     * @return le numéro de l'étage actuel (1-indexed)
     */
    public int getCurrentFloorNumber() {
        return currentFloorIndex + 1;
    }
    
    /**
     * Passe à l'étage suivant (irréversible)
     * @return true si le passage a réussi
     */
    public boolean goToNextFloor() {
        if (currentFloorIndex >= totalFloors - 1) {
            return false; // Dernier étage atteint
        }
        
        currentFloorIndex++;
        
        // Générer le nouvel étage si nécessaire
        if (currentFloorIndex >= floors.size()) {
            floors.add(new Floor(currentFloorIndex + 1));
        }
        
        return true;
    }
    
    /**
     * @return true si le joueur est au dernier étage
     */
    public boolean isLastFloor() {
        return currentFloorIndex >= totalFloors - 1;
    }
    
    /**
     * @return le nombre total d'étages
     */
    public int getTotalFloors() {
        return totalFloors;
    }
    
    /**
     * @return la progression (pourcentage)
     */
    public int getProgressPercent() {
        return (currentFloorIndex + 1) * 100 / totalFloors;
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║           DONJON                     ║\n");
        sb.append("╠══════════════════════════════════════╣\n");
        sb.append("║ Étage: ").append(getCurrentFloorNumber())
          .append("/").append(totalFloors)
          .append(" (").append(getProgressPercent()).append("%)");
        
        // Padding pour alignement
        int padding = 38 - 10 - String.valueOf(getCurrentFloorNumber()).length() 
                        - String.valueOf(totalFloors).length()
                        - String.valueOf(getProgressPercent()).length();
        sb.append(" ".repeat(Math.max(0, padding))).append("║\n");
        
        sb.append("╚══════════════════════════════════════╝\n\n");
        sb.append(getCurrentFloor().toString());
        
        return sb.toString();
    }
}
