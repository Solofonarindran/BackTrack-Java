package fr.uge.model;

import java.util.List;

public record Malediction(List<Coordonate> size,List<Coordonate> references) implements Item{

	@Override
	public boolean rotate() {
		return false;
	}
}
