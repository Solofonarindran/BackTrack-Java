package fr.uge.model;

import java.util.List;

public interface Item {
	List<Coordonate> references();
	default boolean rotate() {
		return true;
	}
}
