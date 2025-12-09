package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.uge.data.Actor;
import fr.uge.data.Enemy;
import fr.uge.data.Hero;

public class ActorPortrayalComponent implements Component{
	private final int marginLeft;
	private final int marginTop;
	private final int size;
  private final Actor actor;
	private final BufferedImage uriImage;
		
	public ActorPortrayalComponent(BufferedImage uriImage,int x, int y, int size, Actor actor) {
		this.marginLeft= x;
		this.marginTop= y;
		this.size = size;
		this.actor = actor;
		this.uriImage = uriImage;
	}
	
	// Position de x par rapport à l' Acteur => Enemy ou Hero
	private static int marginLeft(Actor actor, int xResolution) {
		return switch(actor) {
			case Hero _ ->(int) (xResolution *  0.1); // MarginLeft 10 % de la résolution x
			case Enemy _ -> (int) (xResolution * 0.7);// MarginLeft 70 % de la résolution x
		};
	}
	
	//méthode pour créer un portrait qui est dépend de la taille d'écran
	public static ActorPortrayalComponent create(BufferedImage uriImage,int xResolution, int yResolution,Actor actor) {
		var x = marginLeft(actor, xResolution);
		var y = (int) (yResolution * 0.75); // MarginTop 75 % de la résolution y
		var size = (int) (yResolution * 0.15); // 20 % de la taille d'écran
		return new ActorPortrayalComponent(uriImage,x, y, size,actor);
	}
	
	@Override
	public void draw(Graphics2D g, Component component) {
		//Cadre 
		g.setColor(new Color(80,80,100));
		g.fillRoundRect(marginLeft,marginTop,size,size,10,10);
		
		g.setColor(new Color(150,150,170));
		g.setStroke(new BasicStroke(3));
		g.drawRoundRect(marginLeft,marginTop,size ,size,10,10);
		g.drawImage(uriImage, marginLeft, marginTop,size ,size, null);
		drawHealthBar(g);
	}
	
	public void drawHealthBar(Graphics2D g) {
		
		// barWidth , barHeight => Taille du fond du bar de vie
		// 1 er progress bar (fond)
		var barWidth = size; 
		var barHeight = 8;
		
		//position sur l'affichage
		var barX = marginLeft;
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
    String hpText = actor.getHealthPoint() + "/" + actor.getMaxHealthPoint();
    g.drawString(hpText, barX + 2, barY - barHeight);
  	
	}
	
  @Override
  public int[] getBounds() {
      return new int[]{marginLeft, marginTop, size, size + 15};
  }
}
