package fr.uge.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import fr.uge.data.BackPack;

public class BagComponent {
	private final int marginLeft;
	private final int marginTop;
	private final int cellSize;
	
	private final BackPack bag;
	
	public BagComponent (int x, int y,int sizeCell, BackPack bag) {
		Objects.requireNonNull(bag);
		this.marginLeft = x;
		this.marginTop = y;
		this.cellSize = sizeCell;
		this.bag = bag;
	}
	
	public static BagComponent create(int xResolution, int yResolution, BackPack bag) {
		var x =(int) (xResolution * 0.15);
		var y = (int) (yResolution * 0.20);
		var sizeCell =(int) (((xResolution + yResolution)/2)* 0.03);
		return new BagComponent(x, y, sizeCell, bag);
	}
	
	public void drawGrid(Graphics2D g, Component component) {
		var cell = bag.getUnlockedCoordinates();
		cell.forEach(c -> {
		
			 var x = marginLeft + c.x() * cellSize;
			 var y = marginTop + c.y() * cellSize;
			 
			 g.setColor(new Color(60, 60, 80));
       g.fill(new Rectangle2D.Float(x, y, cellSize, cellSize));
      
       
       g.setColor(new Color(100, 100, 120));
       g.draw(new Rectangle2D.Float(x, y, cellSize, cellSize));
		});
		
	}
}
