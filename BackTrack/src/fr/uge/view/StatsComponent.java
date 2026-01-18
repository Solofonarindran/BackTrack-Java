package fr.uge.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;

import fr.uge.model.Actor;
import fr.uge.model.Enemy;
import fr.uge.model.Hero;

public record StatsComponent(int marginLeft, int marginTop, Actor actor) implements Component{
	public StatsComponent {
		Objects.requireNonNull(actor);
	}
	
	private void drawLevelIcon(Graphics2D g, int x, int y) {
		g.setColor(Color.YELLOW); 
		g.setFont(new Font("Arial", Font.BOLD, 16)); g.drawString("⭐  ", x , y); 
	}
	
	private void drawHeartIcon(Graphics2D g, int x, int y) {
		g.setColor(Color.RED); 
		g.setFont(new Font("Arial", Font.BOLD, 16)); g.drawString("❤️  ", x , y); 
	}
	private void drawKeyIcon(Graphics2D g, int x, int y) {
		g.setColor(Color.BLACK); 
		g.setFont(new Font("Arial", Font.BOLD, 16)); g.drawString("🗝️  ", x , y); 
	}
	private void drawEnergyIcon(Graphics2D g, int x, int y) {
		g.setColor(Color.YELLOW); 
		g.setFont(new Font("Arial", Font.BOLD, 16)); g.drawString("⚡  ", x , y); 
	}
	
	
	@Override
	public void draw(Graphics2D g, Component component) {
		
		switch(actor) {
			case Hero hero -> {
				var offsetX = 15; // décalage x 
				var offsetY = 20; // décalage Y
				var i = 20;
				// offsetX ou offsetY s'incrémentent , on va voir à l'implémentation
				drawLevelIcon(g, marginLeft + offsetX, marginTop + offsetY);offsetX+=15;
				g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 14)); g.drawString("Niveau     : " + hero.getLevel(), marginLeft +offsetX, marginTop + offsetY); offsetY += i;
				
				offsetX = 15; // retour à la position x initial
				drawHeartIcon(g, marginLeft + offsetX, marginTop + offsetY);offsetX+=15;
				g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 14)); g.drawString("Vie           : " + hero.getHealthPoint(), marginLeft + offsetX, marginTop + offsetY);offsetY += i;
				
				offsetX = 15; // retour à la position x initial
				drawKeyIcon(g, marginLeft + offsetX, marginTop + offsetY);offsetX+=15;
				g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 14)); g.drawString("Clé           : " + hero.getKeys(), marginLeft + offsetX, marginTop + offsetY);offsetY += i;
				
				offsetX = 15; // retour à la position x initial
				drawEnergyIcon(g, marginLeft + offsetX, marginTop + offsetY);offsetX+=15;
				g.setColor(Color.WHITE) ; g.setFont(new Font("Arial", Font.BOLD, 14)); g.drawString("Energy     : " + hero.getEnergy(), marginLeft + offsetX, marginTop + offsetY);	offsetY += i;
				
				offsetX = 15; // retour à la position x initial
				drawEnergyIcon(g, marginLeft + offsetX, marginTop + offsetY);offsetX+=15;
				g.setColor(Color.WHITE) ; g.setFont(new Font("Arial", Font.BOLD, 14)); g.drawString("Protection     : " + hero.getProtection(), marginLeft + offsetX, marginTop + offsetY);	
			}
			
			case Enemy enemy -> {
				
				var offsetY = 20;
				var i = 20;
				g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 12)); 
				g.drawString("Nom : " + enemy.getType().getName(), marginLeft, marginTop + offsetY );	offsetY += i;
				
				g.setColor(Color.WHITE);
				g.setFont(new Font("Arial", Font.BOLD, 12));
				g.drawString(" Protection " + enemy.getArmor(), marginLeft, marginTop + offsetY);offsetY += i;
				
				
				g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 12)); 
				g.drawString("Prochain action     : " + enemy.getNextAction(), marginLeft, marginTop + offsetY);	offsetY += i;
				
			}
		}
		
	}
	
	 @Override
   public int[] getBounds() {
       return new int[]{marginLeft, marginTop - 15, 500, 20};
   }
}
