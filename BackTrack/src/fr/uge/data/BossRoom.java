package fr.uge.data;

import fr.uge.model.Enemy;

/**
 * Salle contenant le boss de l'étage.
 * Doit être vaincue pour accéder à la sortie.
*/

import fr.uge.model.EnemyType;

public record BossRoom(
    Enemy boss,
    boolean isDefeated
) implements Room {
    
    // Factory pour créer un boss selon l'étage
    public static BossRoom create(int floorNumber) {
        var bossType = getBossForFloor(floorNumber);
        var boss = new Enemy(bossType);
        return new BossRoom(boss, false);
    }
    
    private static EnemyType getBossForFloor(int floorNumber) {
        return switch (floorNumber) {
            case 1 -> EnemyType.BOSS_GOLEM;
            case 2 -> EnemyType.BOSS_DRAGON;
            case 3 -> EnemyType.BOSS_DEMON;
            default -> EnemyType.BOSS_DEMON;  // Répéter pour étages supérieurs
        };
    }
    
    // Marquer comme vaincu
    public BossRoom markDefeated() {
        return new BossRoom(boss, true);
    }
    
    @Override
    public String getDescription() {
        return isDefeated ? "Salle du boss (vaincu)" : "⚠️ BOSS : " + boss.getType().getName();
    }
    
    @Override
    public boolean isAccessible() {
        return true;
    }
    
    @Override
    public boolean isVisited() {
    // TODO Auto-generated method stub
    	return isDefeated;
    }
  	
  	@Override 
  	
  	public BossRoom setVisited() {
  		// TODO Auto-generated method stub
  		return null;
  	}
  	
    @Override
    public final String toString() {
    	return isDefeated ? "💀" : "👑";	
    }
    
    public boolean defeated() {
    	return isDefeated;
    }
}

