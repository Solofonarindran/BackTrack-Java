package fr.uge.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import fr.uge.data.BackPack;
import fr.uge.data.Hero;
import fr.uge.model.Item;

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
	 
   // Rendu privé
   private void draw(Graphics2D g, BackPack backpack, Hero hero, Item selectedItem) {
       // Fond
       g.setColor(new Color(20, 20, 30));
       g.fillRect(0, 0, 1000, 700);
       
       // Titre
       g.setColor(Color.WHITE);
       g.setFont(new Font("Arial", Font.BOLD, 28));
       g.drawString("⚔️ BACKPACK HERO ⚔️", 250, 40);
       
       // Stats joueur
       drawPlayerStats(g, hero);
       
       // Grille
       drawGrid(g, backpack);
       
       // Items
       drawItems(g, backpack, selectedItem);
       
       // Info sac
       drawBackpackInfo(g, backpack);
       
       // Instructions
       drawInstructions(g);
   }
}
