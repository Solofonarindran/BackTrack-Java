package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.data.BackPack;
import fr.uge.data.MerchantRoom;
import fr.uge.model.Armor;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Curse;
import fr.uge.model.Gold;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Weapon;

public class MerchantView {
    
    private final int screenWidth;
    private final int screenHeight;
    
    // Zones
    private final int shopZoneX, shopZoneY, shopZoneW, shopZoneH;
    private final int bagZoneX, bagZoneY, bagZoneW, bagZoneH;
    private final int infoZoneX, infoZoneY, infoZoneW, infoZoneH;
    
    private final int cellSize = 50;
    private int bagGridX, bagGridY;
    
    // Bounds pour détection clic
    private final Map<Item, int[]> shopItemBounds;
    private final Map<Item, int[]> bagItemBounds;
    
    // État
    private Item selectedShopItem;
    private Item selectedBagItem;
    private Item hoveredItem;
    private Coordonate previewPosition;
    
    // Mode
    public enum Mode { BUY, SELL }
   
    
    // Couleurs
    private static final Color BACKGROUND = new Color(30, 30, 40);
    private static final Color PANEL_BG = new Color(45, 45, 55);
    private static final Color PANEL_BORDER = new Color(80, 80, 100);
    private static final Color TEXT_WHITE = new Color(240, 240, 240);
    private static final Color TEXT_GRAY = new Color(180, 180, 180);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color VALID = new Color(80, 255, 80, 100);
    private static final Color INVALID = new Color(255, 80, 80, 100);
    private static final Color MERCHANT_PURPLE = new Color(180, 100, 220);
    
    public MerchantView(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.shopItemBounds = new HashMap<>();
        this.bagItemBounds = new HashMap<>();
        
        // Zone boutique (gauche)
        this.shopZoneX = 50;
        this.shopZoneY = 100;
        this.shopZoneW = 350;
        this.shopZoneH = screenHeight - 200;
        
        // Zone sac (centre)
        this.bagZoneX = 450;
        this.bagZoneY = 100;
        this.bagZoneW = 400;
        this.bagZoneH = screenHeight - 200;
        
        // Zone info (droite)
        this.infoZoneX = 900;
        this.infoZoneY = 100;
        this.infoZoneW = screenWidth - 950;
        this.infoZoneH = screenHeight - 200;
        
        this.bagGridX = bagZoneX + 20;
        this.bagGridY = bagZoneY + 50;
    }
    
    // ==================== DESSIN PRINCIPAL ====================
    
    public void draw(Graphics2D g, MerchantRoom merchant, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(merchant);
        Objects.requireNonNull(hero);
        shopItemBounds.clear();
        bagItemBounds.clear();
        
        // Fond
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Titre
        drawTitle(g, hero);
        
        // Zone boutique
        drawShopZone(g, merchant, hero);
        
        // Zone sac
        drawBagZone(g, hero);
        
        // Zone info
        drawInfoZone(g, hero);
        
        // Instructions
        drawInstructions(g);
    }
    
    private void drawTitle(Graphics2D g, Hero hero) {
        g.setColor(MERCHANT_PURPLE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        
        String title = "MARCHAND";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (screenWidth - titleWidth) / 2, 50);
        
        // Or du héros
        g.setColor(GOLD_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String goldText = "💰 " + hero.getGold() + " Or";
        g.drawString(goldText, (screenWidth - g.getFontMetrics().stringWidth(goldText)) / 2, 80);
    }
   
    private void bagGridZone(Graphics2D g, int col, int row, BackPack backpack) {
    	var x = bagGridX + col * cellSize;
      var y = bagGridY + row * cellSize;
      
      var coord = new Coordonate(col, row);
      
      if (backpack.isUnlocked(coord)) {
          g.setColor(new Color(60, 60, 70));
          g.fillRect(x, y, cellSize - 2, cellSize - 2);
          g.setColor(new Color(80, 80, 90));
          g.drawRect(x, y, cellSize - 2, cellSize - 2);
      } else {
          g.setColor(new Color(30, 30, 35));
          g.fillRect(x, y, cellSize - 2, cellSize - 2);
      }
    }
    
    private void drawBagZone(Graphics2D g, Hero hero) {
        drawPanel(g, bagZoneX, bagZoneY, bagZoneW, bagZoneH, "VOTRE SAC - VENDRE");
        var backpack = hero.getBackpack();
        // Grille
        for (int row = 0; row < backpack.getMaxHeight(); row++) {
            for (int col = 0; col < backpack.getMaxWidth(); col++) {
               bagGridZone(g,col,row,backpack);
            }
        }
        
        // Items
        var items = backpack.itemsWithCoordonate();
        for (var entry : items.entrySet()) {
            var item = entry.getKey();
            var coords = entry.getValue();
            
            drawBagItem(g, item, coords);
            boundsClickItem(coords, item);
        }
        
        // Preview si achat
        if (selectedShopItem != null && previewPosition != null) {
            drawItemPreview(g, selectedShopItem, previewPosition, backpack);
        }
    }
    
    private void boundsClickItem(List<Coordonate> coords, Item item) {
   // Calculer bounds pour clic
      var minX = coords.stream().mapToInt(Coordonate::x).min().orElse(0);
      var maxX = coords.stream().mapToInt(Coordonate::x).max().orElse(0);
      var minY = coords.stream().mapToInt(Coordonate::y).min().orElse(0);
      var maxY = coords.stream().mapToInt(Coordonate::y).max().orElse(0);
      
      var pixelX = bagGridX + minX * cellSize;
      var pixelY = bagGridY + minY * cellSize;
      var pixelW = (maxX - minX + 1) * cellSize;
      var pixelH = (maxY - minY + 1) * cellSize;
      
      bagItemBounds.put(item, new int[]{pixelX, pixelY, pixelW, pixelH});
    }
    
    private void bagGrid(Graphics2D g, Coordonate coord, Color itemColor) {
      int x = bagGridX + coord.x() * cellSize;
      int y = bagGridY + coord.y() * cellSize;
      
      g.setColor(itemColor);
      g.fillRect(x + 2, y + 2, cellSize - 6, cellSize - 6);
      
      g.setColor(itemColor.brighter());
      g.setStroke(new BasicStroke(1));
      g.drawRect(x + 2, y + 2, cellSize - 6, cellSize - 6);
    }
    
    private void bagGridIcone(Graphics2D g, List<Coordonate> coords, Item item) {
      var first = coords.get(0);
      int iconX = bagGridX + first.x() * cellSize;
      int iconY = bagGridY + first.y() * cellSize;
      
      g.setColor(Color.WHITE);
      g.setFont(new Font("Arial", Font.BOLD, 14));
      g.drawString(getItemIcon(item), iconX + 5, iconY + 20);
    }
    
    private void priceSelectedItem(Graphics2D g, List<Coordonate> coords, Item item) {
      var first = coords.get(0);
      int priceX = bagGridX + first.x() * cellSize;
      int priceY = bagGridY + first.y() * cellSize + cellSize - 5;
      
      g.setColor(GOLD_COLOR);
      g.setFont(new Font("Arial", Font.BOLD, 10));
      g.drawString(getSellPrice(item) + "g", priceX + 3, priceY);
    }
    
    private void drawBagItem(Graphics2D g, Item item, List<Coordonate> coords) {
        Color itemColor = getItemColor(item);
        // Highlight si sélectionné pour vente
        if (item == selectedBagItem) {
            itemColor = itemColor.brighter();
        }
        for (var coord : coords) {
          bagGrid(g, coord, itemColor);
        }
        // Icône
        if (!coords.isEmpty()) {
        	bagGridIcone(g, coords,item);
        }
        // Prix de vente si sélectionné
        if (item == selectedBagItem && !coords.isEmpty()) {
          priceSelectedItem(g, coords, item);
        }
    }
    
    private void drawItemPreview(Graphics2D g, Item item, Coordonate position, BackPack backpack) {
        var references = item.references();
        var absoluteCoords = Coordonate.toAbsolute(references, position);
        
        boolean canPlace = absoluteCoords.stream().allMatch(backpack::isAccepted);
        Color previewColor = canPlace ? VALID : INVALID;
        
        for (var coord : absoluteCoords) {
            int x = bagGridX + coord.x() * cellSize;
            int y = bagGridY + coord.y() * cellSize;
            
            g.setColor(previewColor);
            g.fillRect(x + 2, y + 2, cellSize - 6, cellSize - 6);
            
            g.setColor(canPlace ? Color.GREEN : Color.RED);
            g.setStroke(new BasicStroke(2));
            g.drawRect(x + 2, y + 2, cellSize - 6, cellSize - 6);
        }
    }
    
    private int statHeroInfoZone(Graphics2D g, int textX, int textY, Hero hero) {
   // Stats héros
      g.setColor(TEXT_WHITE);
      g.setFont(new Font("Arial", Font.BOLD, 14));
      g.drawString("HÉROS", textX, textY);
      
      g.setFont(new Font("Arial", Font.PLAIN, 12));
      g.setColor(TEXT_GRAY);
      textY += 25;
      g.drawString("Niveau : " + hero.getLevel(), textX, textY);
      textY += 20;
      g.drawString("PV : " + hero.getHealthPoint() + "/" + hero.getMaxHealthPoint(), textX, textY);
      textY += 20;
      g.setColor(GOLD_COLOR);
      g.drawString("Or : " + hero.getGold(), textX, textY);
      return textY;
    }
    
    private void drawInfoZone(Graphics2D g, Hero hero) {
        drawPanel(g, infoZoneX, infoZoneY, infoZoneW, infoZoneH, "INFORMATIONS");
        var textX = infoZoneX + 15;
        var textY = infoZoneY + 50;
        // Stats héros
        textY = statHeroInfoZone(g, textX, textY, hero);
        
        // Item sélectionné
        var displayItem = selectedShopItem != null ? selectedShopItem : 
                          selectedBagItem != null ? selectedBagItem : hoveredItem;
        
        if (displayItem != null) {
            textY += 40;
            g.setColor(MERCHANT_PURPLE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            
            if (selectedShopItem != null) {
                g.drawString("ACHETER", textX, textY);
                textY += 25;
                g.setColor(GOLD_COLOR);
                g.drawString("Prix : " + getItemPrice(displayItem) + " Or", textX, textY);
            } else if (selectedBagItem != null) {
                g.drawString("VENDRE", textX, textY);
                textY += 25;
                g.setColor(GOLD_COLOR);
                g.drawString("Prix : " + getSellPrice(displayItem) + " Or", textX, textY);
            }
            
            textY += 25;
            g.setColor(TEXT_WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(getItemFullName(displayItem), textX, textY);
            
            textY += 20;
            g.setColor(TEXT_GRAY);
            g.drawString(getItemDescription(displayItem), textX, textY);
        }
    }
    
    private void drawInstructions(Graphics2D g) {
        int y = screenHeight - 50;
        
        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String instructions;
        if (selectedShopItem != null) {
            instructions = "CLIC sur sac pour placer | ESCAPE pour annuler | ESPACE pour quitter";
        } else if (selectedBagItem != null) {
            instructions = "Appui A pour vendre | ESCAPE pour annuler | ESPACE pour quitter";
        } else {
            instructions = "CLIC boutique → acheter | CLIC sac → vendre | ESPACE pour quitter";
        }
        
        int width = g.getFontMetrics().stringWidth(instructions);
        g.drawString(instructions, (screenWidth - width) / 2, y);
    }
    
    private void drawPanel(Graphics2D g, int x, int y, int w, int h, String title) {
        g.setColor(PANEL_BG);
        g.fillRoundRect(x, y, w, h, 10, 10);
        
        g.setColor(PANEL_BORDER);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 10, 10);
        
        g.setColor(TEXT_WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(title, x + 15, y + 25);
    }
    
    // ==================== UTILITAIRES ====================
    
    private Color getItemColor(Item item) {
        return switch (item) {
            case Weapon _ -> new Color(200, 80, 80);
            case Armor _ -> new Color(80, 80, 200);
            case Consumable _ -> new Color(80, 200, 80);
            case Magic _ -> new Color(200, 80, 200);
            case Gold _ -> new Color(255, 200, 50);
            case Curse _ -> new Color(100, 50, 100);
            default -> new Color(150, 150, 150);
        };
    }
    
    private String getItemIcon(Item item) {
        return switch (item) {
            case Weapon _ -> "⚔";
            case Armor _ -> "🛡";
            case Consumable _ -> "+";
            case Magic _ -> "✨";
            case Gold _ -> "$";
            case Curse _ -> "💀";
            default -> "?";
        };
    }
    
    private String getItemFullName(Item item) {
        return switch (item) {
            case Weapon w -> w.name();
            case Armor a -> a.name();
            case Consumable c -> c.name();
            case Magic m -> m.name();
            case Gold g -> g.number() + " Or";
            case Curse c -> c.name();
            default -> "Item";
        };
    }
    
    private String getItemDescription(Item item) {
        return switch (item) {
            case Weapon w -> "Dégâts : " + w.healthPoint();
            case Armor a -> "Défense : " + a.defensePoint();
            case Consumable _ -> "Soin : +10 PV";
            case Magic _ -> "Dégâts magiques : 15";
            case Gold g -> "Valeur : " + g.number();
            case Curse c -> c.description();
            default -> "";
        };
    }
    
    public int getItemPrice(Item item) {
        return switch (item) {
            case Weapon w -> 20 + w.healthPoint() * 3;
            case Armor a -> 15 + a.defensePoint() * 3;
            case Consumable _ -> 10;
            case Magic _ -> 30;
            default -> 10;
        };
    }
    
    public int getSellPrice(Item item) {
        // Vente à 50% du prix d'achat
        return getItemPrice(item) / 2;
    }
    
    // ==================== INTERACTION ====================
    
    public Item getShopItemAt(int mouseX, int mouseY) {
        for (var entry : shopItemBounds.entrySet()) {
            var bounds = entry.getValue();
            if (mouseX >= bounds[0] && mouseX <= bounds[0] + bounds[2] &&
                mouseY >= bounds[1] && mouseY <= bounds[1] + bounds[3]) {
                return entry.getKey();
            }
        }
        return null;
    }
    private void drawShopZone(Graphics2D g, MerchantRoom merchant, Hero hero) {
      // Panel
      drawPanel(g, shopZoneX, shopZoneY, shopZoneW, shopZoneH, "BOUTIQUE - ACHETER");
      var items = merchant.getItemsForSale();
      var itemX = shopZoneX + 15;
      var startY = shopZoneY + 50;
      var itemHeight = 65;
      var itemWidth = shopZoneW - 30;
      // IMPORTANT: Clear les bounds avant de redessiner
      shopItemBounds.clear();
      var index = 0;
      for (var item : items) {
          var y = startY + index * itemHeight;
          drawShopItem(g, item, itemX, y, itemWidth, itemHeight - 5, hero);
          // Stocker les bounds avec les dimensions EXACTES
          shopItemBounds.put(item, new int[]{itemX, y, itemWidth, itemHeight - 5});
          index++;
      }
      if (items.isEmpty()) {
          g.setColor(TEXT_GRAY);
          g.setFont(new Font("Arial", Font.ITALIC, 14));
          g.drawString("Rupture de stock !", shopZoneX + 20, shopZoneY + 100);
      }
  }

  private void priceShopItem(Graphics2D g, boolean canAfford, int price, int x, int width, int y) {
  	 g.setColor(canAfford ? GOLD_COLOR : new Color(150, 80, 80));
     g.setFont(new Font("Arial", Font.BOLD, 14));
     String priceText = price + " Or";
     int priceWidth = g.getFontMetrics().stringWidth(priceText);
     g.drawString(priceText, x + width - priceWidth - 15, y + 35);
  }
  private void infoShop(Graphics2D g, Item item, int x, int y) {
 // Icône
    g.setColor(getItemColor(item));
    g.setFont(new Font("Arial", Font.BOLD, 24));
    g.drawString(getItemIcon(item), x + 15, y + 38);
    
    // Nom
    g.setColor(TEXT_WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 14));
    g.drawString(getItemFullName(item), x + 55, y + 22);
    
    // Description
    g.setColor(TEXT_GRAY);
    g.setFont(new Font("Arial", Font.PLAIN, 11));
    g.drawString(getItemDescription(item), x + 55, y + 40);
  }
  private void borderShopItem(Graphics2D g, Item item, boolean canAfford) {
  	 if (item == selectedShopItem) {
       g.setColor(Color.WHITE);
       g.setStroke(new BasicStroke(3));
	   } else if (canAfford) {
	       g.setColor(new Color(80, 200, 80));  // Vert
	       g.setStroke(new BasicStroke(2));
	   } else {
	       g.setColor(new Color(200, 80, 80));  // Rouge
	       g.setStroke(new BasicStroke(2));
	   }
  }
  private Color backgroundShopItem(Item item) {
    Color bgColor = new Color(50, 50, 60);
    if (item == selectedShopItem) {
        bgColor = new Color(70, 70, 90);
    } else if (item == hoveredItem) {
        bgColor = new Color(60, 60, 75);
    }
    return bgColor;
  }
  private void drawShopItem(Graphics2D g, Item item, int x, int y, int width, int height, Hero hero) {
      int price = getItemPrice(item);
      var canAfford = hero.getGold() >= price;
      // Fond
	    var bgColor = backgroundShopItem(item);
	      g.setColor(bgColor);
	      g.fillRoundRect(x, y, width, height, 8, 8);
	      // Bordure
	    borderShopItem(g, item, canAfford);
	    g.drawRoundRect(x, y, width, height, 8, 8);
	    infoShop(g, item, x, y);
	    // Prix
	     priceShopItem(g, canAfford, price, x, width, y);
  }
  
    public Item getBagItemAt(int mouseX, int mouseY) {
        for (var entry : bagItemBounds.entrySet()) {
            var bounds = entry.getValue();
            if (mouseX >= bounds[0] && mouseX <= bounds[0] + bounds[2] &&
                mouseY >= bounds[1] && mouseY <= bounds[1] + bounds[3]) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public Coordonate getBagPositionAt(int mouseX, int mouseY, BackPack backpack) {
        int col = (mouseX - bagGridX) / cellSize;
        int row = (mouseY - bagGridY) / cellSize;
        
        if (col >= 0 && col < backpack.getMaxWidth() &&
            row >= 0 && row < backpack.getMaxHeight()) {
            return new Coordonate(col, row);
        }
        return null;
    }
    
    public void setSelectedShopItem(Item item) {
        this.selectedShopItem = item;
        this.selectedBagItem = null;
    }
    
    public void setSelectedBagItem(Item item) {
        this.selectedBagItem = item;
        this.selectedShopItem = null;
    }
    
    public void setHoveredItem(Item item) {
        this.hoveredItem = item;
    }
    
    public void setPreviewPosition(Coordonate pos) {
        this.previewPosition = pos;
    }
    
    public void clearSelection() {
        this.selectedShopItem = null;
        this.selectedBagItem = null;
        this.previewPosition = null;
    }
    
    public Item getSelectedShopItem() { return selectedShopItem; }
    public Item getSelectedBagItem() { return selectedBagItem; }
}