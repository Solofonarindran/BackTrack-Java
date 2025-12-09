package fr.uge.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class TitleComponent implements Component{
	 private final float marginLeft, marginTop;
	 
   private final String title;
   private final Color color;
   
   public TitleComponent(int xResolution, int yResolution, String title, Color color) {
       this.marginLeft = (float)( xResolution * 0.25); // 25 % margin left 
       this.marginTop = (float) (yResolution * 0.1); // 5 % margin right
       this.title = title;
       this.color = color;
   }
   
   @Override
   public void draw(Graphics2D g, Component component) {
       g.setColor(color);
       g.setFont(new Font("Arial", Font.BOLD, 50));
          
       // Ombre
       g.setColor(Color.darkGray);
       g.drawString(title, marginLeft + 3, marginTop + 3);
       
       // Texte
       g.setColor(color);
       g.drawString(title, marginLeft, marginTop);
   }
   
//   @Override
//   public int[] getBounds() {
//       return new int[]{x, y - 30, 400, 40};
//   }
}
