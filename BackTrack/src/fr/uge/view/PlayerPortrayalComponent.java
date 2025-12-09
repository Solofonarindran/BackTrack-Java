package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.uge.data.Actor;

public class PlayerPortrayalComponent implements Component{
	private final int marginLeft;
	private final int marginTop;
	private final int size;
	private final Actor actor;
	private final BufferedImage uriImage;
	
	// Taille d'image d' ACTOR
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	
	public PlayerPortrayalComponent(int x, int y, int size, Actor actor, BufferedImage uriImage) {
		this.marginLeft= x;
		this.marginTop= y;
		this.size = size;
		this.actor = actor;
		this.uriImage = uriImage;
	}
	@Override
	public void draw(Graphics2D g, Component component) {
		//Cadre 
		g.setColor(new Color(80,80,100));
		g.fillRoundRect(marginLeft,marginTop,size,size,10,10);
		
		g.setColor(new Color(150,150,170));
		g.setStroke(new BasicStroke(3));
		g.drawRoundRect(marginLeft,marginTop,size,size,10,10);
		g.drawImage(uriImage, marginLeft, marginTop,WIDTH ,HEIGHT, null);
		
	}
	
	public void drawHealthBar(Graphics2D g) {
		var barWidth = size;
		var barHeight = 8;
		//position sur l'affichage
		var barX = marginLeft;
		var barY = marginTop;
		 // Fond
    g.setColor(new Color(50, 50, 50));
    g.fillRect(barX, barY, barWidth, barHeight);
    // Vie
    var healthPointRatio = (double) actor.getHealthPoint() / actor.getMaxHealthPoint();
    var fillWidth = (int) barWidth * healthPointRatio ;
    Color healthPointColor = healthPointRatio > 0.5 ? new Color(50, 200, 50) : healthPointRatio > 0.25 ? new Color(255, 200, 0) : new Color(200, 50, 50);
    g.setColor(healthPointColor);
    g.fillRect(barX, barY, barWidth, barHeight); 
    // Bordure
    g.setColor(Color.WHITE);
    g.drawRect(barX, barY, barWidth, barHeight);
 // Texte
    g.setFont(new Font("Arial", Font.PLAIN, 10));
    String hpText = actor.getHealthPoint() + "/" + actor.getMaxHealthPoint();
    g.drawString(hpText, barX + 2, barY + barHeight - 1);
  	
	}
	
  @Override
  public int[] getBounds() {
      return new int[]{marginLeft, marginTop, size, size + 15};
  }
}
