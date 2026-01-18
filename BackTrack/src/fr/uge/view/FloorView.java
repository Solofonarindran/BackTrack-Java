package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fr.uge.data.BossRoom;
import fr.uge.data.EmptyRoom;
import fr.uge.data.EnemyRoom;
import fr.uge.data.ExitRoom;
import fr.uge.data.Floor;
import fr.uge.data.GateRoom;
import fr.uge.data.HealerRoom;
import fr.uge.data.MerchantRoom;
import fr.uge.data.Room;
import fr.uge.data.SurpriseRoom;
import fr.uge.data.TreasureRoom;
import fr.uge.model.Coordonate;
import fr.uge.model.Hero;

public class FloorView {
	private int width;
	private int height;
  private  int cellSize;
  private  int gridStartX;
  private  int gridStartY;

  
  private final Map<Coordonate, int[]> roomBounds;
  private Coordonate hoveredRoom;
  
  public FloorView(int width, int height, int startX, int startY, int cellSize) {
    this.width = width;
    this.height = height;
  	this.gridStartX = startX;
    this.gridStartY = startY;

    this.roomBounds = new HashMap<>();
   
  }
  
  public static FloorView create(int screenWidth, int screenHeight, int pourcent) {
  	// pourcent % de taille d'écran
  	
  	var sizeWidth = screenWidth * pourcent / 100;
  	var sizeHeight = screenHeight * pourcent / 100;
  	
  	var startX = screenWidth - sizeWidth;
  	var startY = 20;
  	
  	if (pourcent > 30) {
  		startY = screenHeight - sizeHeight;
  	}
  	
  	
  	var maxCellW = sizeWidth / Floor.WIDTH;
  	var maxCellH = sizeHeight / Floor.HEIGHT;
  	var cellSize = Math.min(maxCellW, maxCellH);
  	return new FloorView(sizeWidth, sizeHeight, startX, startY, cellSize);
  }
  
  public void draw(Graphics2D g, Floor floor, Hero hero) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(floor);
    Objects.requireNonNull(hero);

    roomBounds.clear();
    
    // Fond
    g.setColor(new Color(20, 20, 30));
    g.fillRect(0, 0, width, height);
    
    // Titre
    drawTitle(g, floor);
    
    // Connexions
    drawConnections(g, floor);
    
    // Salles
    drawRooms(g, floor);
    
    // Joueur
    drawPlayer(g, floor);
    
    // HUD
    drawHUD(g, hero, floor);
    
    // Tooltip
    if (hoveredRoom != null) {
        drawTooltip(g, floor, hoveredRoom);
    }
  }
  
  private void drawTitle(Graphics2D g, Floor floor) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20));
    String title = "ÉTAGE " + floor.getFloorNumber();
    int titleWidth = g.getFontMetrics().stringWidth(title);
    g.drawString(title, (width - titleWidth) / 2, (width * 40 / 100));
    
    g.setFont(new Font("Arial", Font.PLAIN, 16));
    g.setColor(new Color(180, 180, 180));
    String subtitle = "Cliquez sur une salle adjacente pour vous déplacer";
    int subWidth = g.getFontMetrics().stringWidth(subtitle);
    g.drawString(subtitle, (width - subWidth) / 2, (width * 50 / 100));
  }
  
  private void drawConnections(Graphics2D g, Floor floor) {
    g.setColor(new Color(80, 80, 100));
    g.setStroke(new BasicStroke(3));
    
    for (var pos : floor.getAllRoomPositions()) {
        int x1 = gridStartX + pos.x() * cellSize + cellSize / 2;
        int y1 = gridStartY + pos.y() * cellSize + cellSize / 2;
        
        for (var connected : floor.getConnectedPositions(pos)) {
            if (connected.x() > pos.x() || connected.y() > pos.y()) {
                int x2 = gridStartX + connected.x() * cellSize + cellSize / 2;
                int y2 = gridStartY + connected.y() * cellSize + cellSize / 2;
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
  }
  
  private void drawRooms(Graphics2D g, Floor floor) {
    for (int y = 0; y < Floor.HEIGHT; y++) {
        for (int x = 0; x < Floor.WIDTH; x++) {
            var pos = new Coordonate(x, y);
            var room = floor.getRoom(pos);
            
            int pixelX = gridStartX + x * cellSize;
            int pixelY = gridStartY + y * cellSize;
            
            if (room == null) {
                g.setColor(new Color(40, 40, 50));
                g.fillRect(pixelX, pixelY, cellSize, cellSize);
            } else {
                drawRoom(g, room, pixelX, pixelY, pos, floor);
                roomBounds.put(pos, new int[]{pixelX, pixelY, cellSize, cellSize});
            }
        }
    }
  }
  
  private void drawRoom(Graphics2D g, Room room, int x, int y, Coordonate pos, Floor floor) {
    Color roomColor = getRoomColor(room);
    
    // Fond
    g.setColor(roomColor);
    g.fillRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 8, 8);
    
    // Bordure
    g.setColor(roomColor.brighter());
    g.setStroke(new BasicStroke(2));
    g.drawRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 8, 8);
    
    // Highlight si adjacent au joueur
    if (floor.areConnected(floor.getPlayerPosition(), pos) && 
        !pos.equals(floor.getPlayerPosition())) {
        g.setColor(new Color(255, 255, 255, 40));
        g.fillRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 8, 8);
    }
    
    // Highlight si survolé
    if (pos.equals(hoveredRoom)) {
        g.setColor(new Color(255, 255, 255, 80));
        g.fillRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 8, 8);
    }
    
    // Icône
    String icon = getRoomIcon(room);
    g.setFont(new Font("Arial", Font.PLAIN, cellSize / 2));
    g.setColor(Color.WHITE);
    int iconX = x + cellSize / 4;
    int iconY = y + cellSize / 2 + cellSize / 6;
    g.drawString(icon, iconX, iconY);
  }
  
  private void drawPlayer(Graphics2D g, Floor floor) {
    var pos = floor.getPlayerPosition();
    var x = gridStartX + pos.x() * cellSize;
    var y = gridStartY + pos.y() * cellSize;
    
    // Cercle joueur
    g.setColor(new Color(100, 200, 255));
    var playerSize = cellSize / 2;
    var playerX = x + (cellSize - playerSize) / 2;
    var playerY = y + (cellSize - playerSize) / 2;
    g.fillOval(playerX, playerY, playerSize, playerSize);
    
    // Bordure
    g.setColor(Color.WHITE);
    g.setStroke(new BasicStroke(3));
    g.drawOval(playerX, playerY, playerSize, playerSize);
  }
  private void drawHUD(Graphics2D g, Hero hero, Floor floor) {
 
    var hudW = 250;
    var hudH = 100;
    
    var hudX = gridStartX ;
    var hudY =gridStartY + height ;
    
    // Fond
    g.setColor(new Color(40, 40, 50, 220));
    g.fillRoundRect(hudX, hudY, hudW, hudH, 10, 10);
    
    // Bordure
    g.setColor(new Color(100, 100, 120));
    g.setStroke(new BasicStroke(2));
    g.drawRoundRect(hudX, hudY, hudW, hudH, 10, 10);
    
    // Stats
    g.setFont(new Font("Arial", Font.BOLD, 14));
    g.setColor(Color.WHITE);
    g.drawString("HEROS - Niveau " + hero.getLevel(), hudX + 10, hudY + 25);
   
    // Étage
    g.setColor(new Color(180, 180, 180));
    g.drawString("Etage: " + floor.getFloorNumber(), hudX + 10, hudY + 90);
    
  }

  private void drawTooltip(Graphics2D g, Floor floor, Coordonate pos) {
    var room = floor.getRoom(pos);
    if (room == null) return;
    var bounds = roomBounds.get(pos);
    if (bounds == null) return;
    var tooltipX = bounds[0] + cellSize + 10;
    var tooltipY = bounds[1];
    var tooltipW = 200;
    var tooltipH = 70;
    // Fond
    g.setColor(new Color(30, 30, 40, 240));
    g.fillRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 10, 10);
    
    // Bordure
    g.setColor(getRoomColor(room));
    g.setStroke(new BasicStroke(2));
    g.drawRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 10, 10);
    
    // Nom
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 12));
    g.drawString(getRoomName(room), tooltipX + 10, tooltipY + 20);
    
    // Description
    g.setFont(new Font("Arial", Font.PLAIN, 10));
    g.setColor(new Color(180, 180, 180));
    String desc = room.getDescription();
    if (desc.length() > 35) {
        desc = desc.substring(0, 32) + "...";
    }
    g.drawString(desc, tooltipX + 10, tooltipY + 40);
    
    // Indication accessible
    if (floor.areConnected(floor.getPlayerPosition(), pos) &&
        !pos.equals(floor.getPlayerPosition())) {
        g.setColor(new Color(100, 255, 100));
        g.drawString("Cliquez pour entrer", tooltipX + 10, tooltipY + 60);
    }
	}
  
  private Color getRoomColor(Room room) {
    return switch (room) {
        case EmptyRoom e -> e.isVisited() ? new Color(80, 80, 100) : new Color(60, 60, 80);
        case EnemyRoom e -> e.isVisited() ? new Color(80, 80, 100) : new Color(220, 80, 80);
        case TreasureRoom t -> t.isLooted() ? new Color(80, 80, 100) : new Color(255, 215, 0);
        case MerchantRoom _ -> new Color(180, 100, 220);
        case HealerRoom _ -> new Color(100, 220, 100);
        case ExitRoom _ -> new Color(100, 255, 200);
        case BossRoom b -> b.defeated() ? new Color(80, 80, 100) : new Color(255, 50, 50);
        case GateRoom g -> g.isUnlocked() ? new Color(60, 60, 80) : new Color(150, 100, 50);
        case SurpriseRoom s -> s.isRevealed() ? new Color(80, 80, 100) : new Color(200, 150, 255);
    };
  }
  
  private String getRoomIcon(Room room) {
    return switch (room) {
        case EmptyRoom e -> e.isVisited() ? "." : "?";
        case EnemyRoom e -> e.isVisited() ? "V" : "E";
        case TreasureRoom t -> t.isLooted() ? "o" : "T";
        case MerchantRoom _ -> "M";
        case HealerRoom _ -> "+";
        case ExitRoom _ -> "S";
        case BossRoom b -> b.defeated() ? "X" : "B";
        case GateRoom g -> g.isUnlocked() ? "." : "L";
        case SurpriseRoom s -> s.isRevealed() ? "." : "?";
    };
  }
  
  private String getRoomName(Room room) {
    return switch (room) {
        case EmptyRoom _ -> "Couloir";
        case EnemyRoom e -> e.isVisited() ? "Salle sécurisée" : "Ennemis !";
        case TreasureRoom t -> t.isLooted() ? "Coffre vide" : "Trésor";
        case MerchantRoom _ -> "Marchand";
        case HealerRoom _ -> "Guérisseur";
        case ExitRoom _ -> "Sortie";
        case BossRoom b -> b.defeated() ? "Boss vaincu" : "BOSS";
        case GateRoom g -> g.isUnlocked() ? "Grille ouverte" : "Grille fermée";
        case SurpriseRoom s -> s.isRevealed() ? "Salle vide" : "Mystère";
    };
  }
  
  // ==================== INTERACTION ====================
  
  public Coordonate getRoomAt(int mouseX, int mouseY) {
      for (var entry : roomBounds.entrySet()) {
          var bounds = entry.getValue();
          if (mouseX >= bounds[0] && mouseX <= bounds[0] + bounds[2] &&
              mouseY >= bounds[1] && mouseY <= bounds[1] + bounds[3]) {
              return entry.getKey();
          }
      }
      return null;
  }
  
  public void setHoveredRoom(Coordonate pos) {
    this.hoveredRoom = pos;
  }

  public int getScreenWidth() { return width; }
  public int getScreenHeight() { return height; }
}
