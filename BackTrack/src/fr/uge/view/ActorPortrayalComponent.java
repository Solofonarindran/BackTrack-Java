package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.uge.model.Actor;
import fr.uge.model.Enemy;
import fr.uge.model.Hero;

public class ActorPortrayalComponent implements Component{
	private final int screenWidth;
	private final int screenHeight;
	private final int marginLeft;
	private final int marginTop;
	private final int size;
	private final int rang ; // rang si on a plusieurs énemies
  private final Actor actor;
	private final BufferedImage uriImage;
		
	public ActorPortrayalComponent(BufferedImage uriImage,int screenWidth, int screenHeight, int x, int y, int size,int rang, Actor actor) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.marginLeft= x;
		this.marginTop= y;
		this.size = size;
		this.actor = actor;
		this.uriImage = uriImage;
		this.rang = rang;
	}
	
	// Position de x par rapport à l' Acteur => Enemy ou Hero
	private static int marginLeft(Actor actor, int xResolution) {
		return switch(actor) {
			case Hero _ ->(int) (xResolution *  0.02); // MarginLeft 10 % de la résolution x
			case Enemy _ -> (int) (xResolution * 0.7);// MarginLeft 70 % de la résolution x
		};
	}
	
	//méthode pour créer un portrait qui est dépend de la taille d'écran
	public static ActorPortrayalComponent create(BufferedImage uriImage,int xResolution, int yResolution,int rang, Actor actor) {
		var x = marginLeft(actor, xResolution);
		var y = (int) (yResolution * 0.75); // MarginTop 75 % de la résolution y
		var size = (int) (yResolution * 0.15); // 20 % de la taille d'écran
		return new ActorPortrayalComponent(uriImage, xResolution, yResolution,x, y, size,rang,actor);
	}
	
	@Override
	public void draw(Graphics2D g,Component component) {
		//Cadre 
		
		var x = marginLeft + (size * rang) + 10;
		var y = marginTop;
		g.setColor(new Color(80,80,100));
		g.fillRoundRect(x,y,size,size,10,10);
		
		g.setColor(new Color(150,150,170));
		g.setStroke(new BasicStroke(3));
		g.drawRoundRect(x,y,size ,size,10,10);
		g.drawImage(uriImage, x, y,size ,size, null);
		drawHealthBar(g);
		drawEnergyBar(g);
	}
	
	public void drawHealthBar(Graphics2D g) {
		// barWidth , barHeight => Taille du fond du bar de vie
		// 1 er progress bar (fond)
		var barWidth = size; 
		var barHeight = 8;
		//position sur l'affichage
		var barX = marginLeft + (size * rang) + 10;
		var barY = marginTop - 15;
		 // Fond
    g.setColor(new Color(50, 50, 50));
    g.fillRect(barX, barY, barWidth, barHeight);
    // Vie
    var healthPointRatio = (double) actor.getHealthPoint() / actor.getMaxHealthPoint(); //POurcentage de vie pour le progressBar
    var fillWidth = (int) (barWidth * healthPointRatio) ; // cette variable affiche dynamiquement le barre de vie du hero
    Color healthPointColor = healthPointRatio > 0.5 ? new Color(50, 200, 50) : healthPointRatio > 0.25 ? new Color(255, 200, 0) : Color.RED;
    g.setColor(healthPointColor);
    g.fillRect(barX, barY, fillWidth, barHeight);
    // Bordure
    g.setColor(Color.WHITE);
    g.setStroke(new BasicStroke(3));
    g.drawRoundRect(barX, barY, barWidth, barHeight,10,10); 
 // Texte
    g.setFont(new Font("Arial", Font.PLAIN, 10));
    String hpText ="Vie " +  actor.getHealthPoint() + "/" + actor.getMaxHealthPoint();
    g.drawString(hpText, barX - 50, barY);
  	
	}
	
	public void drawEnergyBar(Graphics2D g) {
		switch(actor) {
			case Hero hero -> {
			// barWidth , barHeight => Taille du fond du bar de ENergy
				// 1 er progress bar (fond)
				var barWidth = size; 
				var barHeight = 8;
				//position sur l'affichage
				var barX = marginLeft + (size * rang) + 10;
				var barY = marginTop + size + 15;
				 // Fond
		    g.setColor(new Color(50, 50, 50));
		    g.fillRect(barX, barY, barWidth, barHeight);
		    // Vie
		    var energyPointRatio = (double) hero.getEnergy() / hero.getMaxEnergy(); //POurcentage de vie pour le progressBar
		    var fillWidth = (int) (barWidth * energyPointRatio) ; // cette variable affiche dynamiquement le barre de vie du hero
		    Color energyPointColor = new Color(220, 200, 60);
		    g.setColor(energyPointColor);
		    g.fillRect(barX, barY, fillWidth, barHeight);   
		    // Bordure
		    g.setColor(Color.WHITE);
		    g.setStroke(new BasicStroke(3));
		    g.drawRoundRect(barX, barY, barWidth, barHeight,10,10);
		    
		 // Texte
		    g.setFont(new Font("Arial", Font.PLAIN, 10));
		    String hpText = "Energie: " + hero.getEnergy()  + "/" + hero.getMaxEnergy();
		    g.drawString(hpText, barX - 80, barY + barHeight);
			}
			
			case Enemy _ -> {} // pas d'énergie
		}
		
	}
	
	public void drawStat(Graphics2D g) {
		switch(actor) {
			case Hero hero -> {
				new PanelComponent(20, 20,180, 150, "Stat", Color.gray, Color.WHITE).draw(g, new StatsComponent(40, 40, hero));
			}
			
			case Enemy enemy -> {
				var x = (int) (screenWidth * 0.8);
					var w = (int)(screenWidth* 0.2);
				var y = 30 + (int)(screenHeight * 0.44) + 30;
				// 						 hauteur de log						+ 30;
				new PanelComponent(x, y, w, 90, "Stat", Color.gray, Color.WHITE).draw(g, new StatsComponent(x + 20, y + 20, enemy));
			}
		}
	}
	
  @Override
  public int[] getBounds() {
      return new int[]{marginLeft, marginTop, size, size + 15};
  }
}
