package fr.uge.item;

import java.util.List;

import fr.uge.backpack.Coordonate;

public interface Item {
	String name();
    Rarity rarity();
	List<Coordonate> references();
	default boolean rotate() {
		return true;
	}
}