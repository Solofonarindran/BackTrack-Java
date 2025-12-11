package fr.uge.model;

import java.util.ArrayList;
import java.util.List;

public record TreasureRoom(List<Item> treasures, boolean isVisited) implements Room{
	
	public static TreasureRoom create() {
		
		// items au hasard 
		var items = new ArrayList<Item>();
		return new TreasureRoom(items,false);
	}
	
	@Override
	public boolean isAccessible() {
		return true;
	}
  @Override
  public String getDescription() {
      if (isVisited) {
          return "Un coffre vide. Le trésor a déjà été récupéré.";
      }
      return "Un coffre au trésor scintille dans l'obscurité !";
  }
	
	@Override
	public TreasureRoom setVisited() {
		return new TreasureRoom(List.of(),true);
	}
}
