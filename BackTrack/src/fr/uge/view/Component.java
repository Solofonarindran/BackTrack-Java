package fr.uge.view;

import java.awt.Graphics2D;

public interface Component {
	
	void draw(Graphics2D g);
	
	default int[] getBounds() {
		return new int[] {0,0,0,0};
	};
	default boolean handleClick(int mouseX, int mouseY) {
		return false;
	}
}
