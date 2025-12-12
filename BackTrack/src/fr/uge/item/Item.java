package fr.uge.model;

import java.util.List;

public interface Item {
	String name();
    Rarity rarity();
	List<Coordonate> references();
	default boolean rotate() {
		return true;
	}
}