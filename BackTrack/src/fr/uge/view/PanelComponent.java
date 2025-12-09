package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class PanelComponent implements Component{
	private final int marginLeft, marginTop, width, height;
  private final Color bgColor;
  
  private final Color borderColor;
  private final String title;
  
  public PanelComponent(int x, int y, int width, int height, 
                       String title, Color bgColor, Color borderColor) {
      this.marginLeft = x;
      this.marginTop = y;
      this.width = width;
      this.height = height;
      this.title = title;
      this.bgColor = bgColor;
      this.borderColor = borderColor;
  }
  
  @Override
  public void draw(Graphics2D g, Component component) {
      // Fond
      g.setColor(bgColor);
      g.fillRoundRect(marginLeft, marginTop, width, height, 15, 15);
      
      // Bordure
      g.setColor(borderColor);
      g.setStroke(new BasicStroke(2));
      g.drawRoundRect(marginLeft, marginTop, width, height, 15, 15);
      
      // Titre
      if (title != null && !title.isEmpty()) {
          g.setFont(new Font("Arial", Font.BOLD, 14));
          g.drawString(title, marginLeft + 10, marginTop + 20);
      }
      
      //autre component fils
      component.draw(g, null);
  }
  
  @Override
  public int[] getBounds() {
      return new int[]{marginLeft, marginTop, width, height};
  }
}
