package fr.uge.model;

public final class ExitRoom implements Room{
	private final int targetFloor;
	private boolean used;
	
	public ExitRoom () {
		this.targetFloor = -1;
		this.used = false;
	}
	
  public ExitRoom(int targetFloor) {
    this.targetFloor = targetFloor;
    this.used = false;
  }
  public ExitRoom(int targetFloor, boolean visited) {
    this.targetFloor = targetFloor;
    this.used =  visited;
  }
  
  @Override 
  public boolean isVisited() {
  	return used;
  }
	@Override
	public boolean isAccessible() {
		return false;
	}
  
	@Override
	public ExitRoom setVisited() {
		return new ExitRoom(this.targetFloor,true);
	}
  @Override
  public String getDescription() {
      if (used) {
          return "La porte de sortie a déjà été franchie.";
      }
      return "Une porte mystérieuse mène vers l'étage suivant. " +
             "⚠️ ATTENTION: Le passage est irréversible !";
  }
  
  @Override
  public String toString() {
      return used ? "░░" : "🚀";
  }
}
