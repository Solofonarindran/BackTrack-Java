package fr.uge.model;

/**
 * Interface représentant une salle du donjon.
 * Chaque type de salle implémente cette interface.
 */
public sealed interface Room permits EmptyRoom, TreasureRoom, EnemyRoom, 
                                      MerchantRoom, HealerRoom, ExitRoom, 
                                       GateRoom {
    

    /**
     * @return true si le joueur peut traverser cette salle
     */
    boolean isAccessible();
    
    /**
     * @return true si la salle a été visitée
     */
    boolean isVisited();
    
    /**
     * Marque la salle comme visitée
     */
    Room setVisited();
    
    String getDescription();
}