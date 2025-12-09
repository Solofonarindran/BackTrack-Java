package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import fr.uge.view.Component;

public class ButtonComponent implements Component{
	 private final int x, y, width, height;
   private final String text;
   private final Color normalColor;
   private final Color hoverColor;
   private final Runnable onClick;
   
   private boolean hovered = false;
   
   public ButtonComponent(int x, int y, int width, int height, 
                         String text, Color color, Runnable onClick) {
       this.x = x;
       this.y = y;
       this.width = width;
       this.height = height;
       this.text = text;
       this.normalColor = color;
       this.hoverColor = color.brighter();
       this.onClick = onClick;
   }
   
   public void setHovered(boolean hovered) {
     this.hovered = hovered;
 }
	
	public void draw(Graphics2D g, Component component) {
    // Fond
    g.setColor(hovered ? hoverColor : normalColor);
    g.fillRoundRect(x, y, width, height, 10, 10);
    
    // Bordure
    g.setColor(Color.WHITE);
    g.setStroke(new BasicStroke(2));
    g.drawRoundRect(x, y, width, height, 10, 10);
    
    // Texte
    g.setFont(new Font("Arial", Font.BOLD, 14));
    FontMetrics fm = g.getFontMetrics();
    int textX = x + (width - fm.stringWidth(text)) / 2;
    int textY = y + (height + fm.getAscent()) / 2 - 2;
    g.drawString(text, textX, textY);
	}
	 @Override
   public int[] getBounds() {
       return new int[]{x, y, width, height};
   }
	 
	 @Override
   public boolean handleClick(int mouseX, int mouseY) {
       if (mouseX >= x && mouseX <= x + width &&
           mouseY >= y && mouseY <= y + height) {
           onClick.run();
           return true;
       }
       return false;
   }
	
}
