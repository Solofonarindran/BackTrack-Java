package fr.uge.model;

import java.util.List;

public record Malediction(List<Coordonate> size) implements Item{

	@Override
	public boolean rotate() {
		return false;
	}
}
