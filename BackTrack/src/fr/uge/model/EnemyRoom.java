package fr.uge.model;


import java.util.List;

import fr.uge.data.Enemy;

public record EnemyRoom(List<Enemy> enemies, boolean isVisited) implements Room{
	
	@Override
	public boolean isAccessible() {
		return true;
	}
	
	@Override
	public final String toString() {
	
		return "Salle d'Enemy";
	}
  @Override
  public String getDescription() {
      if (isVisited) {
          return "La salle est jonchée de débris de combat. Plus aucun danger.";
      }
      return "Des ennemis vous barrent le passage ! (" + enemies.size() + " ennemi(s))";
  }
	@Override
	public EnemyRoom setVisited() {
		return new EnemyRoom(List.<Enemy>of(), true);
	}
}
