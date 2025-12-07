package fr.uge.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class BackgroundComponent implements Component{
	private final BufferedImage image;
  private final Color color;
  private final int width, height;
  
  public BackgroundComponent(BufferedImage image, int width, int height) {
  	this.image = Objects.requireNonNull(image);
  	this.color = null;
  	this.width = width;
  	this.height = height;
  }
  
  public BackgroundComponent(Color color, int width, int height) {
  	this.image = null;
  	this.color = Objects.requireNonNull(color);
  	this.width = width;
  	this.height = height;
  }
  
  @Override
  public void draw(Graphics2D g) {
  	if(image != null) {
  		g.drawImage(image,0,0, width, height, null);
  	}else if(color != null) {
  		g.setColor(color);
  		g.fillRect(0, 0, width, height);
  	}
  }
}
