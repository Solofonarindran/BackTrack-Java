package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.github.forax.zen.ApplicationContext;

import fr.uge.data.BackPack;
import fr.uge.data.Hero;
import fr.uge.model.Armor;
import fr.uge.model.Clef;
import fr.uge.model.Consumable;
import fr.uge.model.Gold;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Malediction;
import fr.uge.model.Weapon;

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
   
  
   private void drawPlayerStats(Graphics2D g, Hero player) {
     g.setColor(Color.WHITE);
     g.setFont(new Font("Arial", Font.PLAIN, 14));
     int y = yOrigin - 30;
     g.drawString("❤️ " + player.getHealthPoint() + "/" + player.getMaxHealthPoint(), xOrigin, y);
     g.drawString("⚡ " + player.getEnergy() + "/" + player.getMaxEnergy(), xOrigin + 100, y);
     
     g.drawString("🔑 " + player.getKeys(), xOrigin + 400, y);
     g.drawString("⭐ Niv." + player.getLevel(), xOrigin + 480, y);
 }
 
 private void drawGrid(Graphics2D g, BackPack backpack) {
     for (var coord : backpack.getUnlockedCoordinates()) {
         float x = xOrigin + coord.x() * cellSize;
         float y = yOrigin + coord.y() * cellSize;
         
         g.setColor(new Color(60, 60, 80));
         g.fill(new Rectangle2D.Float(x, y, cellSize, cellSize));
         
         g.setColor(new Color(100, 100, 120));
         g.draw(new Rectangle2D.Float(x, y, cellSize, cellSize));
     }
 }
 
 private void drawItems(Graphics2D g, BackPack backpack, Item selectedItem) {
     for (var entry : backpack.getItems().entrySet()) {
         Item item = entry.getKey();
         boolean isSelected = (item == selectedItem);
         
         Color color = getItemColor(item);
         if (isSelected) {
             color = color.brighter();
         }
         
         // Dessiner chaque case
         for (var coord : entry.getValue()) {
             float x = xOrigin + coord.x() * cellSize;
             float y = yOrigin + coord.y() * cellSize;
             
             g.setColor(color);
             g.fill(new Rectangle2D.Float(x + 2, y + 2, cellSize - 4, cellSize - 4));
             
             g.setColor(Color.BLACK);
             g.setStroke(new BasicStroke(2));
             g.draw(new Rectangle2D.Float(x + 2, y + 2, cellSize - 4, cellSize - 4));
         }
         
         // Label sur première case
         if (!entry.getValue().isEmpty()) {
             var firstCoord = entry.getValue().get(0);
             float x = xOrigin + firstCoord.x() * cellSize;
             float y = yOrigin + firstCoord.y() * cellSize;
             
             g.setColor(Color.WHITE);
             g.setFont(new Font("Arial", Font.BOLD, 20));
             String label = getItemLabel(item);
             g.drawString(label, x + 12, y + 32);
         }
     }
 }
 
 private void drawBackpackInfo(Graphics2D g, BackPack backpack) {
     g.setColor(new Color(200, 200, 200));
     g.setFont(new Font("Arial", Font.PLAIN, 12));
     int infoY = yOrigin + backpack.getMaxHeight() * cellSize + 20;
     g.drawString("📦 Cases: " + backpack.getOccupiedSlots() + "/" + 
                  backpack.getTotalSlots() + "  |  " + 
                  backpack.getItemCount() + " items", 
                  xOrigin, infoY);
 }
 
 private void drawInstructions(Graphics2D g) {
     g.setColor(new Color(180, 180, 200));
     g.setFont(new Font("Arial", Font.PLAIN, 11));
     int ix = 550, iy = 150;
     g.drawString("═══════ CONTRÔLES ═══════", ix, iy);
     g.drawString("🖱️  Clic : Sélectionner", ix, iy + 25);
     g.drawString("🔄 R : Tourner (90°)", ix, iy + 45);
     g.drawString("🗑️  Suppr : Supprimer", ix, iy + 65);
     g.drawString("❌ Échap : Désélectionner", ix, iy + 85);
     g.drawString("🚪 Q : Quitter", ix, iy + 105);
 }
 
 // Méthode publique statique (pattern prof)
 public static void draw(ApplicationContext context, BackPack backpack, 
                        Hero hero, Item selectedItem, BackPackView view) {
     context.renderFrame(graphics -> view.draw(graphics, backpack, hero, selectedItem));
 }
 
 private Color getItemColor(Item item) {
     return switch (item) {
         case Weapon _ -> new Color(200, 50, 50);
         case Armor _ -> new Color(100, 100, 200);
         case Magic _ -> new Color(150, 50, 200);
         case Consumable _ -> new Color(50, 200, 50);
         case Gold _ -> new Color(255, 215, 0);
         case Clef _ -> new Color(200, 200, 50);
         case Malediction _ -> new Color(100, 0, 100);
         default -> Color.GRAY;
     };
 }
 
 private String getItemLabel(Item item) {
     return switch (item) {
         case Weapon _ -> "⚔️";
         case Armor _ -> "🛡️";
         case Magic _ -> "✨";
         case Consumable _ -> "⚗️";
         case Gold _ -> "💰";
         case Clef _ -> "🔑";
         case Malediction _ -> "💀";
         default -> "?";
     };
 }
}
