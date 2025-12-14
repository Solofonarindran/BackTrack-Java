package fr.uge.controller;


import fr.uge.data.Dungeon;
import fr.uge.data.Floor;
import fr.uge.model.Direction;
import fr.uge.model.GateRoom;


/**
 * Test du système de donjon et navigation.
 */
public class DungeonTest {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DU SYSTÈME DE DONJON ===\n");
        
        // Test 1: Création d'un étage
        testFloorCreation();
        
        // Test 2: Navigation
        testNavigation();
        
        // Test 3: Donjon complet
        testDungeon();
        
        // Test 4: Grille avec clé
        testGateRoom();
    }
    
    private static void testFloorCreation() {
        System.out.println("--- Test 1: Création d'un étage ---");
        var floor = new Floor(1);
        System.out.println(floor);
        System.out.println();
        System.out.println("Salles générées: " + floor.getAllRoomPositions().size());
        System.out.println();
    }
    
    private static void testNavigation() {
        System.out.println("--- Test 2: Navigation ---");
        var floor = new Floor(1);
        
        System.out.println("Position initiale: " + floor.getPlayerPosition());
        System.out.println("Salle actuelle: " + floor.getCurrentRoom().getDescription());
        System.out.println();
        
        // Afficher les directions disponibles
        var directions = floor.getAvailableDirections();
        System.out.println("Directions disponibles: " + directions);
        
        // Tenter de se déplacer
        for (var dir : directions) {
            System.out.println("\nTentative de déplacement vers " + dir + "...");
            if (floor.movePlayer(dir)) {
                System.out.println("Succès ! Nouvelle position: " + floor.getPlayerPosition());
                System.out.println("Salle: " + floor.getCurrentRoom().getDescription());
            } else {
                System.out.println("Échec du déplacement.");
            }
            break; // Un seul déplacement pour le test
        }
        
        System.out.println();
        System.out.println(floor.toDetailedString());
    }
    
    private static void testDungeon() {
        System.out.println("--- Test 3: Donjon complet ---");
        var dungeon = new Dungeon(5);
        
        System.out.println(dungeon);
        System.out.println();
        
        // Simuler la progression
        System.out.println("Passage à l'étage suivant...");
        if (dungeon.goToNextFloor()) {
            System.out.println("Nouvel étage atteint !");
            System.out.println(dungeon);
        }
        System.out.println();
    }
    
    private static void testGateRoom() {
        System.out.println("--- Test 4: Grille avec clé ---");
        var gate = new GateRoom();
        
        System.out.println("État initial:");
        System.out.println("  Accessible: " + gate.isAccessible());
        System.out.println("  Description: " + gate.getDescription());
        System.out.println("  Affichage: " + gate);
        
        System.out.println("\nDéverrouillage...");
        gate.unlock();
        
        System.out.println("Après déverrouillage:");
        System.out.println("  Accessible: " + gate.isAccessible());
        System.out.println("  Description: " + gate.getDescription());
        System.out.println("  Affichage: " + gate);
        System.out.println("  Salle cachée: " + gate.getHiddenRoom());
        System.out.println();
    }
}