package fr.uge.data;

import java.util.Objects;

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
	    
  public static boolean isEmptyRoom(Room room) {
  	Objects.requireNonNull(room);
  	return switch(room) {
  		case EmptyRoom _-> true;
  		default -> false;
  	};
  }
  
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
