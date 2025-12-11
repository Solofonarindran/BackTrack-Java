package fr.uge.model;

public final class EmptyRoom implements Room{
  
	private final boolean isAccessible;
	private final boolean isVisited;
	
	public EmptyRoom () {
		this.isAccessible = true;
		this.isVisited = false;
	}
	public EmptyRoom (boolean isAccessible, boolean isVisited) {
		this.isAccessible = isAccessible;
		this.isVisited = isVisited;
	}
	
	@Override
	public boolean isVisited() {
		return isVisited;
	}
	
	@Override	
	public boolean isAccessible() {
		return true;
	};
	    
	public Room setVisited() {
		return new EmptyRoom(this.isAccessible, true);
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
