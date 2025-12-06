package fr.uge.view;

public record BackPackView(int xOrigin, int yOrigin, int cellSize, ImageLoader loader ) {
	public static BackPackView create(int margin, ImageLoader loader) {
    return new BackPackView(margin, margin, 50, loader);
	}
	
	// Conversion souris - > grille 
	public int cellXFromMouse(float mouseX) {
		return (int) ((mouseX - xOrigin) / cellSize);
	}
	
	 public int cellYFromMouse(float mouseY) {
     return (int)((mouseY - yOrigin) / cellSize);
	 }
}
