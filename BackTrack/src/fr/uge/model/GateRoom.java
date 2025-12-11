package fr.uge.model;

import java.util.Objects;

// Salle 
public final class GateRoom implements Room{
	private boolean unlocked;
	private final Room hiddenRoom;
	
	public GateRoom(Room hiddenRoom) {
		Objects.requireNonNull(hiddenRoom);
		this.unlocked = false;
		this.hiddenRoom = hiddenRoom;
	}
	
	public GateRoom(boolean visited,Room hiddenRoom) {
		Objects.requireNonNull(hiddenRoom);
		this.unlocked = visited;
		this.hiddenRoom = hiddenRoom;
	}
	
	@Override
	public boolean isVisited() {
		return unlocked;
	}
	
  @Override
  public boolean isAccessible() {
      return unlocked;
  }
  
  @Override 
  public GateRoom setVisited() {
  	return new GateRoom(true,this.hiddenRoom);
  }
  @Override
  public String getDescription() {
      if (!unlocked) {
          return "Une grille massive bloque le passage. Il vous faut une clé pour l'ouvrir.";
      }
      return "La grille est ouverte. " + hiddenRoom.getDescription();
  }
  
  @Override
  public String toString() {
      return unlocked ? hiddenRoom + "" : "🔒";
  }
}
