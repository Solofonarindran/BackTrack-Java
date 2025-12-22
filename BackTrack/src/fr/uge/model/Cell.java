package fr.uge.model;

import java.util.Objects;

public class Cell{
	private final int id;
	private Coordonate coordonate;
	private boolean visible;
	private boolean free;
	
	
	// Nouveau cellule toujours libre
	public Cell(int id, boolean visible, Coordonate coordonate) {
		this.id = id;
		this.coordonate = Objects.requireNonNull(coordonate);
		this.visible = visible;
		this.free = true;
	}
	
	//rend visible 
	public void setVisible() {
		visible = true;
	}
	
	// Mis à jour que cellule occupée
	public void setBusy() {
		free = false;
	}

	
	// Mis à jour d'une cellule être libre
	public void setFree() {
		free = true;
	}
	
	public void updateValueFree(boolean value) {
		free = value;
	}
	
	
  // Getter 
	
	public Coordonate coordonate() {
		return coordonate;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isFree() {
		return free;
	}
	public int id() {
		return id;
	}
	
	@Override
	public String toString() {
		
		return "Cell " + visible + " --- Coordonate -- " + coordonate;
	}
}
