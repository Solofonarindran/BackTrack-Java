package fr.uge.data;


public final class ExitRoom implements Room{
	private final int nextFloor;
	private boolean used;
	
	public ExitRoom () {
		this.nextFloor = 1;
		this.used = false;
	}
	public int getNextFloor() {
    return nextFloor;
 }
  public ExitRoom(int targetFloor) {
    this.nextFloor = targetFloor;
    this.used = false;
  }
  public ExitRoom(int targetFloor, boolean visited) {
    this.nextFloor = targetFloor;
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
		return new ExitRoom(this.nextFloor,true);
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
      return used ? "🚪" : "🚀";
  }
}
