package fr.uge.data;

import java.util.Objects;

import fr.uge.model.Enemy;
import fr.uge.model.Room;

/**
 * Salle contenant le boss de l'étage.
 * Doit être vaincue pour accéder à la sortie.
 */

public record BossRoom(Enemy boss, boolean defeated) implements Room{
	public BossRoom {
		Objects.requireNonNull(boss);
	}
	
	public BossRoom(Enemy boss) {
		this(boss,false);
	}
	
	@Override
	public boolean isAccessible() {
		return true;
	}
	
	@Override 
	
	public BossRoom setVisited() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getDescription() {
		return " " ;
	}
	// si le boss est mort , il est visité
	@Override
	public boolean isVisited() {
		return defeated;
	}
	/**
   * Crée une salle de boss pour un étage donné
//   */
//  public static BossRoom createForFloor(int floorNumber) {
//      var boss = Enemy.createBossForFloor(floorNumber);
//      return new BossRoom(boss);
//  }
//	
}
