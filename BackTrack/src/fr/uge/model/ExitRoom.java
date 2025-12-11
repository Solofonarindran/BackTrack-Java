package fr.uge.model;

public record ExitRoom(boolean isVisited) implements Room{

	
	@Override
	public boolean isAccessible() {
		return false;
	}
  
	@Override
	public ExitRoom setVisited() {
		return new ExitRoom(true);
	}
  @Override
  public String getDescription() {
      if (isVisited) {
          return "La porte de sortie a déjà été franchie.";
      }
      return "Une porte mystérieuse mène vers l'étage suivant. " +
             "⚠️ ATTENTION: Le passage est irréversible !";
  }
  
  @Override
  public String toString() {
      return isVisited ? "░░" : "🚀";
  }
}
