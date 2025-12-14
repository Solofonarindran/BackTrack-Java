package fr.uge.data;

import java.util.Objects;

// Salle 
public final class GateRoom implements Room{
	private boolean unlocked;
	private boolean accessible;
	private final Room hiddenRoom;
	
	public GateRoom() {
		this.unlocked = false;
		this.accessible = false;
		this.hiddenRoom = TreasureRoom.create();
	}
	public GateRoom(Room hiddenRoom) {
		Objects.requireNonNull(hiddenRoom);
		this.unlocked = false;
		this.hiddenRoom = hiddenRoom;
	}
	
	public boolean isUnlocked() {
		return unlocked;
	}
	private void setAccessible(boolean value) {
		this.accessible = value;
	}
	public Room getHiddenRoom() {
		return hiddenRoom;
	}
	
  /**
   * Tente de déverrouiller la grille avec une clé
   * @return true si déverrouillé avec succès
   */
  public boolean unlock() {
      if (unlocked) {
          return false; // Déjà déverrouillée
      }
      unlocked = true;
      setAccessible(true);
      return true;
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
