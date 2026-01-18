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

public class DungeonView {
	private final int screenWidth;
  private final int screenHeight; 
  private final int cellSize;
  private final int gridStartX;
  private final int gridStartY;
  
  private final Map<Coordonate, int[]> roomBounds;
  private Coordonate hoveredRoom;
  
  public DungeonView(int screenWidth, int screenHeight) {
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.roomBounds = new HashMap<>();
    
    // Taille des cellules adaptée à l'écran
    int maxCellW = (screenWidth - 200) / Floor.WIDTH;
    int maxCellH = (screenHeight - 250) / Floor.HEIGHT;
    this.cellSize = Math.min(maxCellW, maxCellH);
    
    // Centrer la grille
    int gridWidth = cellSize * Floor.WIDTH;
    int gridHeight = cellSize * Floor.HEIGHT;
    this.gridStartX = (screenWidth - gridWidth) / 2;
    this.gridStartY = 120 + (screenHeight - 200 - gridHeight) / 2;
  }
  
  public void draw(Graphics2D g, Floor floor, Hero hero) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(floor);
    Objects.requireNonNull(hero);
    
    g.setColor(new Color(30, 30, 40));
    g.fillRect(0, 0, screenWidth, screenHeight);
    
    roomBounds.clear();
    // Fond
    g.setColor(new Color(20, 20, 30));
    g.fillRect(0, 0, screenWidth, screenHeight);
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
    g.setFont(new Font("Arial", Font.BOLD, 32));
    var title = "ÉTAGE " + floor.getFloorNumber();
    var titleWidth = g.getFontMetrics().stringWidth(title);
    g.drawString(title, (screenWidth - titleWidth) / 2, 60);
    
    g.setFont(new Font("Arial", Font.PLAIN, 16));
    g.setColor(new Color(180, 180, 180));
    var subtitle = "Cliquez sur une salle adjacente pour vous déplacer";
    var subWidth = g.getFontMetrics().stringWidth(subtitle);
    g.drawString(subtitle, (screenWidth - subWidth) / 2, 90);
  }
  
  private void drawConnections(Graphics2D g, Floor floor) {
    g.setColor(new Color(80, 80, 100));
    g.setStroke(new BasicStroke(3));
    
    for (var pos : floor.getAllRoomPositions()) {
        var x1 = gridStartX + pos.x() * cellSize + cellSize / 2;
        var y1 = gridStartY + pos.y() * cellSize + cellSize / 2;
        
        for (var connected : floor.getConnectedPositions(pos)) {
            if (connected.x() > pos.x() || connected.y() > pos.y()) {
                var x2 = gridStartX + connected.x() * cellSize + cellSize / 2;
                var y2 = gridStartY + connected.y() * cellSize + cellSize / 2;
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
            var pixelX = gridStartX + x * cellSize;
            var pixelY = gridStartY + y * cellSize;
            
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
    var iconX = x + cellSize / 4;
    var iconY = y + cellSize / 2 + cellSize / 6;
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
  
  private void stateHUD(Graphics2D g, Hero hero, int hudX, int hudY) {
    g.setFont(new Font("Arial", Font.BOLD, 14));
    g.setColor(Color.WHITE);
    g.drawString("HÉROS - Niveau " + hero.getLevel(), hudX + 10, hudY + 25);
    
    g.setFont(new Font("Arial", Font.PLAIN, 12));
    g.setColor(new Color(220, 80, 80));
    g.drawString("PV: " + hero.getHealthPoint() + "/" + hero.getMaxHealthPoint(), hudX + 10, hudY + 50);
    
    g.setColor(new Color(220, 200, 60));
    g.drawString("Énergie: " + hero.getEnergy() + "/" + hero.getMaxEnergy(), hudX + 10, hudY + 70);
    
    g.setColor(new Color(180, 180, 180));
    g.drawString("Clés: " + hero.getKeys(), hudX + 10, hudY + 90);
  }
  
  private void drawHUD(Graphics2D g, Hero hero, Floor floor) {
    var hudX = 20;
    var hudY = screenHeight - 120;
    var hudW = 250;
    var hudH = 100;
    // Fond
    g.setColor(new Color(40, 40, 50, 220));
    g.fillRoundRect(hudX, hudY, hudW, hudH, 10, 10);
    // Bordure
    g.setColor(new Color(100, 100, 120));
    g.setStroke(new BasicStroke(2));
    g.drawRoundRect(hudX, hudY, hudW, hudH, 10, 10);
    
    // Stats
    stateHUD(g,hero,hudX,hudY);
    
    // Étage
    g.setColor(Color.WHITE);
    g.drawString("Étage: " + floor.getFloorNumber(), hudX + 150, hudY + 50);
  }
 
  private void toolDescription(Graphics2D g, Room room, int tooltipX, int tooltipY) {
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
  }
  
  private void drawTooltip(Graphics2D g, Floor floor, Coordonate pos) {
    var room = floor.getRoom(pos);
    if (room == null) return;
    
    var bounds = roomBounds.get(pos);
    if (bounds == null) return;
    
    var tooltipX = bounds[0] + cellSize + 10;
    var tooltipY = bounds[1];
    
    if (tooltipX + 200 > screenWidth) {
        tooltipX = bounds[0] - 210;
    }
    
    var tooltipW = 200;
    var tooltipH = 70;
    
    // Fond
    g.setColor(new Color(30, 30, 40, 240));
    g.fillRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 10, 10);
    
    // Bordure
    g.setColor(getRoomColor(room));
    g.setStroke(new BasicStroke(2));
    g.drawRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 10, 10);
   
    toolDescription(g, room, tooltipX, tooltipY);
    
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
        case BossRoom b -> b.defeated() ? new Color(80, 80, 80) : new Color(255, 50, 50);
        case GateRoom g -> g.isUnlocked() ? new Color(60, 60, 80) : new Color(150, 100, 50);
        case SurpriseRoom s -> s.isRevealed() ? new Color(80, 80, 100) : new Color(200, 150, 255);
    };
  }
  
  private String getRoomIcon(Room room) {
    return switch (room) {
        case EmptyRoom e -> e.isVisited() ? "." : "";
        case EnemyRoom e -> e.isVisited() ? "V" : " 💀" + e;
        case TreasureRoom t -> t + "";
        case MerchantRoom m -> "" + m ;
        case HealerRoom h -> "🩺" + h;
        case ExitRoom e -> "🔚" + e;
        case BossRoom b -> b.isDefeated() ? "💀" : "👑";
        case GateRoom g -> g + "";
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

  public int getScreenWidth() { return screenWidth; }
  public int getScreenHeight() { return screenHeight; }
}
