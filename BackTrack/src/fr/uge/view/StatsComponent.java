package fr.uge.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;

import fr.uge.data.Hero;

public record StatsComponent(int x, int y, Hero hero) implements Component{
	public StatsComponent {
		Objects.requireNonNull(hero);
	}
	
	@Override
	public void draw(Graphics2D g) {
		// Cadre
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		
		var offsetX = 0; // décalage x 
		var offsetY = 0; // décalage Y
		// offsetX ou offsetY s'incrémentent , on va voir à l'implémentation
		g.drawString("⭐ Niveau     : " + hero.getLevel(), x + offsetX, y + offsetY);
		g.drawString("❤️ Vie 				: " + hero.getHealthPoint(), x + offsetX, y + offsetY);
		g.drawString("🔑 Clé        : " + hero.getKeys(), x + offsetX, y + offsetY);
		g.drawString("⚡ Energy	    : " + hero.getEnergy(), x + offsetX, y + offsetY);
		
	}
	
	 @Override
   public int[] getBounds() {
       return new int[]{x, y - 15, 500, 20};
   }
}
