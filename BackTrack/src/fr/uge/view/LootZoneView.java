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
import fr.uge.data.LootZone;
import fr.uge.model.Armor;
import fr.uge.model.Consumable;
import fr.uge.model.Coordonate;
import fr.uge.model.Curse;
import fr.uge.model.Gold;
import fr.uge.model.Hero;
import fr.uge.model.Item;
import fr.uge.model.Magic;
import fr.uge.model.Weapon;

public class LootZoneView {
    
    private final int screenWidth;
    private final int screenHeight;
    
    // Zones
    private final int lootZoneX, lootZoneY, lootZoneW, lootZoneH;
    private final int bagZoneX, bagZoneY, bagZoneW, bagZoneH;
    private final int infoZoneX, infoZoneY, infoZoneW, infoZoneH;
    
    // Items bounds pour détection clic
    private final Map<Item, int[]> lootItemBounds;
    private final int cellSize = 50;
    
    // État
    private Item selectedItem;
    private Item hoveredItem;
    private Coordonate previewPosition;
    
    // Position de la grille du sac
    private int bagGridX;
    private int bagGridY;
    
    // Couleurs
    private static final Color BACKGROUND = new Color(30, 30, 40);
    private static final Color PANEL_BG = new Color(45, 45, 55);
    private static final Color PANEL_BORDER = new Color(80, 80, 100);
    private static final Color TEXT_WHITE = new Color(240, 240, 240);
    private static final Color TEXT_GRAY = new Color(180, 180, 180);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color VALID = new Color(80, 255, 80, 100);
    private static final Color INVALID = new Color(255, 80, 80, 100);
    
    public LootZoneView(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.lootItemBounds = new HashMap<>();
        
        // Zone de loot (gauche)
        this.lootZoneX = 50;
        this.lootZoneY = 100;
        this.lootZoneW = 300;
        this.lootZoneH = screenHeight - 200;
        
        // Zone du sac (centre)
        this.bagZoneX = 400;
        this.bagZoneY = 100;
        this.bagZoneW = 450;
        this.bagZoneH = screenHeight - 200;
        
        // Zone d'info (droite)
        this.infoZoneX = 900;
        this.infoZoneY = 100;
        this.infoZoneW = screenWidth - 950;
        this.infoZoneH = screenHeight - 200;
        
        // Position grille sac
        this.bagGridX = bagZoneX + 20;
        this.bagGridY = bagZoneY + 50;
    }
    
    // ==================== DESSIN PRINCIPAL ====================
    
    public void draw(Graphics2D g, LootZone lootZone, Hero hero) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(lootZone);
        Objects.requireNonNull(hero);
        lootItemBounds.clear();
        // Fond
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Titre
        drawTitle(g, lootZone);
        
        // Zone de loot
        drawLootZone(g, lootZone);
        
        // Zone du sac
        drawBagZone(g, hero);
        
        // Zone d'info
        drawInfoZone(g, hero);
        
        // Instructions
        drawInstructions(g);
    }
    
    private void drawTitle(Graphics2D g, LootZone lootZone) {
        g.setColor(GOLD_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        
        String title = switch (lootZone.getType()) {
            case COMBAT_REWARD -> "BUTIN DE COMBAT";
            case TREASURE_CHEST -> "COFFRE AU TRÉSOR";
            case MERCHANT_SALE -> "MARCHAND";
        };
        
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (screenWidth - titleWidth) / 2, 60);
    }
    
    private void drawLootZone(Graphics2D g, LootZone lootZone) {
        // Panel
        drawPanel(g, lootZoneX, lootZoneY, lootZoneW, lootZoneH, "OBJETS DISPONIBLES");
        
        // Items
        var items = lootZone.getItems();
        var itemY = lootZoneY + 50;
        var itemX = lootZoneX + 20;
        var itemsPerRow = 4;
        var index = 0;
        for (var item : items) {
            var row = index / itemsPerRow;
            var col = index % itemsPerRow;
            
            var x = itemX + col * (cellSize + 10);
            var y = itemY + row * (cellSize + 30);
            
            drawLootItem(g, item, x, y);
            lootItemBounds.put(item, new int[]{x, y, cellSize, cellSize});
            
            index++;
        }
        
        if (items.isEmpty()) {
            g.setColor(TEXT_GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 14));
            g.drawString("Aucun objet", lootZoneX + 20, lootZoneY + 80);
        }
    }
    
    private void drawLootItem(Graphics2D g, Item item, int x, int y) {
        // Fond
        Color bgColor = getItemColor(item);
        g.setColor(bgColor);
        g.fillRoundRect(x, y, cellSize, cellSize, 8, 8);
        
        // Bordure
        if (item == selectedItem) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
        } else if (item == hoveredItem) {
            g.setColor(GOLD_COLOR);
            g.setStroke(new BasicStroke(2));
        } else {
            g.setColor(bgColor.brighter());
            g.setStroke(new BasicStroke(1));
        }
        g.drawRoundRect(x, y, cellSize, cellSize, 8, 8);
        
        // Icône
        String icon = getItemIcon(item);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        var iconWidth = g.getFontMetrics().stringWidth(icon);
        g.drawString(icon, x + (cellSize - iconWidth) / 2, y + cellSize / 2 + 7);
        
        // Nom (en dessous)
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        String name = getItemShortName(item);
        var nameWidth = g.getFontMetrics().stringWidth(name);
        g.drawString(name, x + (cellSize - nameWidth) / 2, y + cellSize + 15);
    }
    
    private void drawBagZone(Graphics2D g, Hero hero) {
        // Panel
        drawPanel(g, bagZoneX, bagZoneY, bagZoneW, bagZoneH, "VOTRE SAC");
        var backpack = hero.getBackpack();
        
        // Grille du sac
        for (var row = 0; row < backpack.getMaxHeight(); row++) {
            for (var col = 0; col < backpack.getMaxWidth(); col++) {
                var x = bagGridX + col * cellSize;
                var y = bagGridY + row * cellSize;
                
                var coord = new Coordonate(col, row);
                
                // Vérifier si la case est déverrouillée
                if (backpack.isUnlocked(coord)) {
                    // Case déverrouillée
                    g.setColor(new Color(60, 60, 70));
                    g.fillRect(x, y, cellSize - 2, cellSize - 2);
                    
                    g.setColor(new Color(80, 80, 90));
                    g.drawRect(x, y, cellSize - 2, cellSize - 2);
                } else {
                    // Case verrouillée
                    g.setColor(new Color(30, 30, 35));
                    g.fillRect(x, y, cellSize - 2, cellSize - 2);
                    
                    g.setColor(new Color(50, 50, 55));
                    g.drawRect(x, y, cellSize - 2, cellSize - 2);
                    
                    // Icône verrou
                    g.setColor(new Color(80, 80, 80));
                    g.setFont(new Font("Arial", Font.PLAIN, 12));
                    g.drawString("🔒", x + 15, y + 30);
                }
            }
        }
        
        // Items existants dans le sac
        var items = backpack.itemsWithCoordonate();
        for (var entry : items.entrySet()) {
            var item = entry.getKey();
            var coords = entry.getValue();  // List<Coordonate>
            
            drawBagItem(g, item, coords);
        }
        
        // Preview de l'item sélectionné
        if (selectedItem != null && previewPosition != null) {
            drawItemPreview(g, selectedItem, previewPosition, backpack);
        }
    }
    
    private void drawBagItem(Graphics2D g, Item item, List<Coordonate> absoluteCoords) {
        Color itemColor = getItemColor(item);
        
        // Dessiner chaque cellule de l'item
        for (var coord : absoluteCoords) {
            var x = bagGridX + coord.x() * cellSize;
            var y = bagGridY + coord.y() * cellSize;
            
            g.setColor(itemColor);
            g.fillRect(x + 2, y + 2, cellSize - 6, cellSize - 6);
            
            g.setColor(itemColor.brighter());
            g.setStroke(new BasicStroke(1));
            g.drawRect(x + 2, y + 2, cellSize - 6, cellSize - 6);
        }
        
        // Icône sur la première cellule
        if (!absoluteCoords.isEmpty()) {
            var firstCoord = absoluteCoords.get(0);
            var iconX = bagGridX + firstCoord.x() * cellSize;
            var iconY = bagGridY + firstCoord.y() * cellSize;
            
            String icon = getItemIcon(item);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(icon, iconX + 5, iconY + 20);
        }
    }
    
    private void drawItemPreview(Graphics2D g, Item item, Coordonate position, BackPack backpack) {
        // Calculer les coordonnées absolues pour la preview
        var references = item.references();
        var absoluteCoords = Coordonate.toAbsolute(references, position);
        // Vérifier si le placement est valide
        var canPlace = absoluteCoords.stream().allMatch(backpack::isAccepted);
        var previewColor = canPlace ? VALID : INVALID;
        // Dessiner la preview
        for (var coord : absoluteCoords) {
            var x = bagGridX + coord.x() * cellSize;
            var y = bagGridY + coord.y() * cellSize;
            g.setColor(previewColor);
            g.fillRect(x + 2, y + 2, cellSize - 6, cellSize - 6);   
            // Bordure
            g.setColor(canPlace ? Color.GREEN : Color.RED);
            g.setStroke(new BasicStroke(2));
            g.drawRect(x + 2, y + 2, cellSize - 6, cellSize - 6);
        }
    }
    
    private void displayItem(Graphics2D g, int textX, int textY, Item displayItem) {
      textY += 40;
      g.setColor(GOLD_COLOR);
      g.setFont(new Font("Arial", Font.BOLD, 14));
      g.drawString("OBJET", textX, textY);     
      textY += 25;
      g.setColor(TEXT_WHITE);
      g.setFont(new Font("Arial", Font.PLAIN, 12));
      g.drawString(getItemFullName(displayItem), textX, textY); 
      textY += 20;
      g.setColor(TEXT_GRAY);
      g.drawString(getItemDescription(displayItem), textX, textY);
      textY += 20;
      g.drawString("Taille : " + displayItem.references().size() + " case(s)", textX, textY);
    }
    
    private int statHero(Graphics2D g, int textX, int textY, Hero hero) {
    	Objects.requireNonNull(hero);
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
      g.drawString("Or : " + hero.getGold(), textX, textY);
      return textY;
    }
    private void drawInfoZone(Graphics2D g, Hero hero) {
        // Panel
        drawPanel(g, infoZoneX, infoZoneY, infoZoneW, infoZoneH, "INFORMATIONS");
        var textX = infoZoneX + 15;
        var textY = infoZoneY + 50;
        // Stats du héros
        textY = statHero(g, textX, textY, hero);
        // Info item sélectionné ou survolé
        var displayItem = selectedItem != null ? selectedItem : hoveredItem;
        if (displayItem != null) {
          displayItem(g, textX, textY, displayItem);
        }
    }
    
    private void drawInstructions(Graphics2D g) {
        int y = screenHeight - 50;
        
        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String instructions = "CLIC sur objet → sélectionner | CLIC sur sac → placer | ESPACE → continuer";
        int width = g.getFontMetrics().stringWidth(instructions);
        g.drawString(instructions, (screenWidth - width) / 2, y);
    }
    
    private void drawPanel(Graphics2D g, int x, int y, int w, int h, String title) {
        // Fond
        g.setColor(PANEL_BG);
        g.fillRoundRect(x, y, w, h, 10, 10);
        
        // Bordure
        g.setColor(PANEL_BORDER);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 10, 10);
        
        // Titre
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
    
    private String getItemShortName(Item item) {
        String name = getItemFullName(item);
        return name.length() > 8 ? name.substring(0, 7) + "." : name;
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
    
    // ==================== INTERACTION ====================
    
    public Item getLootItemAt(int mouseX, int mouseY) {
        for (var entry : lootItemBounds.entrySet()) {
            var bounds = entry.getValue();
            if (mouseX >= bounds[0] && mouseX <= bounds[0] + bounds[2] &&
                mouseY >= bounds[1] && mouseY <= bounds[1] + bounds[3]) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public Coordonate getBagPositionAt(int mouseX, int mouseY, BackPack backpack) {
        var col = (mouseX - bagGridX) / cellSize;
        var row = (mouseY - bagGridY) / cellSize;     
        if (col >= 0 && col < backpack.getMaxWidth() &&
            row >= 0 && row < backpack.getMaxHeight()) {
            return new Coordonate(col, row);
        }
        return null;
    }
    
    public void setSelectedItem(Item item) {
        this.selectedItem = item;
    }
    
    public void setHoveredItem(Item item) {
        this.hoveredItem = item;
    }
    
    public void setPreviewPosition(Coordonate pos) {
        this.previewPosition = pos;
    }
    
    public Item getSelectedItem() {
        return selectedItem;
    }
    
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}