package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



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
 
 private static List<Integer> calculateMargin(AlignItem alignItem, int xResolution, int yResolution) {
	 
	 return switch(alignItem) {
		 case CENTER -> List.of((int)(xResolution * 0.4),(int)(yResolution * 0.4));
		 case TOPLEFT -> List.of((int)(xResolution * 0.2),(int)(yResolution * 0.2));
		 case TOPCENTER -> List.of((int)(xResolution * 0.4),(int)(yResolution * 0.2));
		 case TOPRIGHT -> List.of((int)(xResolution * 0.9),(int)(yResolution * 0.05));
		 case BOTTOMLEFT -> List.of((int)(xResolution * 0.2),(int)(yResolution * 0.8));
		 case BOTTOMCENTER -> List.of((int)(xResolution * 0.4),(int)(yResolution * 0.8));
		 case BOTTOMRIGHT -> List.of((int)(xResolution * 0.8),(int)(yResolution * 0.8));
		 default -> throw new IllegalArgumentException("Position non reconnu");
	 };

 }
 public static ButtonComponent create(AlignItem aligneItem, int xResolution, int yResolution, String text, Color color,Runnable onClick) {
	 Objects.requireNonNull(aligneItem);
	 Objects.requireNonNull(text);
	 Objects.requireNonNull(color);
	 if( xResolution < 0 || yResolution < 0) {
		 throw new IllegalArgumentException();
	 }
	 var x = calculateMargin(aligneItem, xResolution, yResolution).getFirst();
	 var y = calculateMargin(aligneItem, xResolution, yResolution).getLast();
	 var width = (int) (xResolution * 0.05);
	 var height = (int) (yResolution * 0.03);
	 return new ButtonComponent(x, y, width, height, text, color, onClick);
 }
 public void setHovered(boolean hovered) {
     this.hovered = hovered;
 }
	
	public void draw(Graphics2D g, Component component) {
    // Fond
    g.setColor(hovered ? hoverColor : normalColor);
    g.fillRoundRect(x, y, width, height, 10, 10);
    
    // Bordure
    g.setColor(Color.GRAY);
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
