package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Objects;

import fr.uge.data.BackPack;
import fr.uge.model.Armor;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Gold;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Malediction;
import fr.uge.model.Weapon;

public class BagComponent {
	private final int marginLeft;
	private final int marginTop;
	
	private final int zoneWidth;
	private final int zoneHeight;
	
//	private final int gridStartX;
//	private final int girdStartY;
	private final int cellSize;
	private int currentEnergy;
	private final HashMap<Item,int[]> itemBounds;
	private int maxEnergy;
	
	private Item selectedItem;
	private Item hoveredItem;
	private final BackPack bag;
	
	//Couleurs 
	private static final Color CELL_LOCKED = new Color(40, 40, 50);
	private static final Color CELL_UNLOCKED = new Color(60,60, 80);
	private static final Color ITEM_WEAPON = new Color(200, 80, 80);
  private static final Color ITEM_ARMOR = new Color(80, 80, 200);
  private static final Color ITEM_CONSUMABLE = new Color(80, 200, 80);
  private static final Color ITEM_GOLD = new Color(255, 215, 0);
  private static final Color ITEM_MAGIC = new Color(200, 80, 200);
  private static final Color ITEM_CURSE = new Color(100, 0, 100);
  private static final Color HIGHLIGHT_USABLE = new Color(100, 255, 100, 100);
  private static final Color HIGHLIGHT_SELECTED = new Color(255, 255, 100, 150);
  private static final Color HIGHLIGHT_INSUFFICIENT = new Color(255, 100, 100, 100);
  
  
	public BagComponent (int x, int y,int sizeCell, int zoneWidth, int zoneHeight, BackPack bag) {
		Objects.requireNonNull(bag);
		this.marginLeft = x;
		this.marginTop = y;
		this.cellSize = sizeCell;
		this.zoneWidth = zoneWidth;
		this.zoneHeight = zoneHeight;
		this.bag = bag;
		this.itemBounds = new HashMap<Item, int[]>();
	}
	
	public static BagComponent create(int xResolution, int yResolution, BackPack bag) {
		var x =(int) (xResolution * 0.15);
		var y = (int) (yResolution * 0.20);
		var sizeCell =(int) (((xResolution + yResolution)/2)* 0.03);
		var zoneWidth = bag.getMaxWidth();
		var zoneHeight = bag.getMaxHeight();

		return new BagComponent(x, y, sizeCell, zoneWidth, zoneHeight,bag);
	}
	
  public void draw(Graphics2D g, Component component) {
    itemBounds.clear();
    
    // Grille du sac
    drawGrid(g,component);
    
    // Items
    drawItems(g);

    
    // Tooltip si item survolé
    if (hoveredItem != null) {
        drawTooltip(g, hoveredItem);
    }
  }
  
	public void updateEnergy(int currentEnergy) {
		this.currentEnergy = currentEnergy;
	}
	
	private Color getItemColor(Item item) {
		return switch(item) {
			 case Weapon _ -> ITEM_WEAPON;
       case Armor _ -> ITEM_ARMOR;
       case Consumable _ -> ITEM_CONSUMABLE;
       case Gold _ -> ITEM_GOLD;
       case Magic _ -> ITEM_MAGIC;
       case Malediction _ -> ITEM_CURSE;
       default -> new Color(128, 128, 128);
		};
	}
	private void drawItems(Graphics2D g) {
		var items = bag.itemsWithCoordonate();
		
		items.entrySet().forEach(e-> {
			var item = e.getKey();
			var coords = e.getValue();
			
			// Calculer les bounds de l'item
			// Le but est de créer un rectangle pour capter le clic du souris
			var minX = coords.stream().mapToInt(Coordonate::x).min().orElse(0);
			var maxX = coords.stream().mapToInt(Coordonate::x).max().orElse(0);
			
			var minY = coords.stream().mapToInt(Coordonate::x).min().orElse(0);
			var maxY = coords.stream().mapToInt(Coordonate::y).max().orElse(0);
			
			var itemPixelX = marginLeft + minX * cellSize;
			var itemPixelY = marginTop +  minY * cellSize;
			
		
			var itemPixelW = (maxX - minX + 1) * cellSize; //surface occupée pour x 
			var itemPixelH = (maxY - minY + 1) *cellSize; 	//surface occupée pour y 
			
			//STocker les bounds pour la détection de clic
			itemBounds.put(item, new int[] {itemPixelX, itemPixelY,itemPixelW,itemPixelH});
			
			var itemColor = getItemColor(item);
			
			//Dessiner le fond de l'item
			g.setColor(itemColor);
			g.fillRoundRect(itemPixelX + 2, itemPixelY + 2, itemPixelW-4, itemPixelH-4, 8, 8);
			
			// Bordure 
			g.setColor(itemColor.brighter());
			g.setStroke(new BasicStroke(2));
			g.drawRoundRect(itemPixelX + 2, itemPixelY + 2, itemPixelW - 4, itemPixelH - 4, 8, 8);
			
			//Highlight selon l'état
			drawItemHighlight(g, item, itemPixelX, itemPixelY, itemPixelW, itemPixelH);
			
			//Icône et nom
			drawItemContent(g, item, itemPixelX, itemPixelY, itemPixelW, itemPixelH);
			
		
			
		});
	}
	
	private void drawTooltip(Graphics2D g, Item item) {
    var bounds = itemBounds.get(item);
    if (bounds == null) return;
    
    int tooltipX = bounds[0] + bounds[2] + 10;
    int tooltipY = bounds[1];
    
    if (tooltipX + 180 > marginLeft + zoneWidth) {
        tooltipX = bounds[0] - 190;
    }
    
    int tooltipW = 180;
    int tooltipH = 85;
    
    // Fond
    g.setColor(new Color(20, 20, 30, 245));
    g.fillRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 10, 10);
    
    // Bordure
    g.setColor(getItemColor(item));
    g.setStroke(new BasicStroke(2));
    g.drawRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 10, 10);
    
    // Contenu
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 13));
    g.drawString(getItemIcon(item) + " " + getItemName(item), tooltipX + 10, tooltipY + 22);
    
    g.setFont(new Font("Arial", Font.PLAIN, 11));
    g.setColor(new Color(200, 200, 200));
    
    String stats = getItemStats(item);
    g.drawString(stats, tooltipX + 10, tooltipY + 42);
    
    int cost = getEnergyCost(item);
    String costStr = "Coût énergie: " + cost;
    Color costColor = (cost <= currentEnergy) ? new Color(100, 255, 100) : new Color(255, 100, 100);
    g.setColor(costColor);
    g.drawString(costStr, tooltipX + 10, tooltipY + 60);
    
    if (!isUsableInCombat(item)) {
        g.setColor(new Color(255, 150, 150));
        g.drawString("Non utilisable en combat", tooltipX + 10, tooltipY + 78);
    }
	}
	private void drawItemContent(Graphics2D g, Item item, int x, int y, int w, int h) {
		//Icône
		var icon = getItemIcon(item);
		var fontSize = Math.min(w, h) / 2;
		g.setFont(new Font("Arial",Font.PLAIN, fontSize));
		g.setColor(Color.WHITE);
		
		var iconX = x + w / 2 - fontSize / 2;
		var iconY = y + h / 2 + fontSize / 3;
	  g.drawString(icon, iconX, iconY);
    
    // Nom (si assez grand)
    if (w >= cellSize * 2 || h >= cellSize * 2) {
        String name = getItemName(item);
        g.setFont(new Font("Arial", Font.BOLD, 9));
        
        if (g.getFontMetrics().stringWidth(name) > w - 10) {
            name = name.substring(0, Math.min(8, name.length())) + "...";
        }
        
        g.drawString(name, x + 5, y + h - 5);
    }
		
	}
	private void drawItemHighlight(Graphics2D g, Item item, int x, int y,int w, int h) {
		var energyCost = getEnergyCost(item);
    if (item == selectedItem) {
      g.setColor(HIGHLIGHT_SELECTED);
      g.fillRoundRect(x, y, w, h, 8, 8);
    } else if (item == hoveredItem) {
      if (energyCost <= currentEnergy && isUsableInCombat(item)) {
          g.setColor(HIGHLIGHT_USABLE);
      } else {
          g.setColor(HIGHLIGHT_INSUFFICIENT);
      }
      g.fillRoundRect(x, y, w, h, 8, 8);
    }
	}
	
	private boolean isUsableInCombat(Item item ) {
		 return switch (item) {
       case Weapon _, Armor _, Magic _, Consumable _ -> true;
       case Gold _, Malediction _ -> false;
       default -> false;
   };
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
	
	private int getEnergyCost(Item item) {
		Objects.requireNonNull(item);
		 return switch (item) {
       case Weapon w -> w.cost();
       case Armor a -> a.cost();
       case Magic _ -> 2;
       case Consumable _ -> 1;
       default -> 0;
   };
	}
	
  private String getItemIcon(Item item) {
    return switch (item) {
        case Weapon _ -> "E";  // Épée
        case Armor _ -> "B";   // Bouclier
        case Consumable _ -> "P"; // Potion
        case Gold _ -> "G";    // Gold
        case Magic _ -> "M";   // Magie
        case Malediction _ -> "X"; // Malédiction
        default -> "?";
    };
  }

  private String getItemName(Item item) {
    return switch (item) {
        case Weapon w -> w.name();
        case Armor a -> a.name();
        case Consumable c -> c.name();
        case Gold g -> g.number() + " Or";
    case Magic m -> m.name();
    case Malediction _ -> "Malédiction";
    default -> "Item";
    };
  }

  private String getItemStats(Item item) {
    return switch (item) {
        case Weapon w -> "Dégâts: " + w.healthPoint();
        case Armor a -> "Défense: " + a.defensePoint();
        case Consumable _ -> "Soin: 10 PV";
        case Gold g -> "Valeur: " + g.number();
        case Magic m -> "Mana requis: " + m.manaMax();
        case Malediction _ -> "Occupe de la place";
        default -> "";
    };
  }
	/**
	* Trouve l'item à une position donnée (appelé par le controller)
	*/
  public Item getItemAt(int mouseX, int mouseY) {
   for (var entry : itemBounds.entrySet()) {
       var bounds = entry.getValue();
       int x = bounds[0], y = bounds[1], w = bounds[2], h = bounds[3];
       
       if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
           return entry.getKey();
       }
   }
       return null;
	}

/**
* Vérifie si un point est dans la zone du sac
*/
  public boolean containsPoint(int x, int y) {
   return x >= marginLeft && x <= marginLeft + zoneWidth &&
          y >= marginTop && y <= marginTop + zoneHeight;
  }

/**
 * Convertit une position écran en coordonnée de grille du sac
 */
  public Coordonate screenToGridCoord(int screenX, int screenY) {
    if (!containsPoint(screenX, screenY)) {return null;}
    
    int relX = screenX - marginLeft;
    int relY = screenY - marginTop;
    
    if (relX < 0 || relY < 0) {return null;}
    
    int gridX = relX / cellSize;
    int gridY = relY / cellSize;
    
    if (gridX >= bag.getMaxWidth() || gridY >= bag.getMaxHeight()) {
        return null;
    }
	  return new Coordonate(gridX,gridY);
  }
  
  public void setHoveredItem(Item item) {
    this.hoveredItem = item;
}

public void setSelectedItem(Item item) {
    this.selectedItem = item;
}

public boolean canUseItem(Item item) {
    if (item == null || !isUsableInCombat(item)) {
        return false;
    }
    return getEnergyCost(item) <= currentEnergy;
}

public int getGridStartX() { return marginLeft; }
public int getGridStartY() { return marginTop; }
public int getCellSize() { return cellSize; }


public int[] getBounds() {
    return new int[]{marginLeft, marginTop, zoneWidth, zoneHeight};
}

}