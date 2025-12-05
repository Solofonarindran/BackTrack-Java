package fr.uge.view;

import java.awt.Color;
import java.util.Objects;

import fr.uge.data.BackPack;

// Classe utilitaire pour dessiner la grille du sac
public class GridRenderer {
	private static final double CELL_SIZE = 0.6;
	private static final Color GRID_COLOR = new Color(100, 100, 120);
	private static final Color UNLOCKED_COLOR = new Color(60, 60, 80);
	private static final Color LOCKED_COLOR = new Color(30, 30, 40);
	private static final Color OCCPIED_COLOR = new Color(40, 40, 60);
	
	public static void drawGrid(BackPack backPack, double offsetX, double offsetY) {
		Objects.requireNonNull(backPack);
		
	}
}
