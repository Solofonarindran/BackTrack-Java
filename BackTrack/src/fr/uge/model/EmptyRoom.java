package fr.uge.model;

public record EmptyRoom(boolean isVisited) implements Room{

	@Override	
	public boolean isAccessible() {
		return true;
	};
	    
	public Room setVisited() {
		return new EmptyRoom(true);
	}

	@Override
  public String getDescription() {
      return "Un couloir sombre et silencieux.";
  }
	
	@Override
	public String toString(){
		return isVisited ? "░░" : "··";
	}
}
