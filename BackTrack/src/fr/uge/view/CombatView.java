package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import fr.uge.data.Combat;
import fr.uge.model.Hero;



public class CombatView {
    
    // Dimensions de l'écran
    private final int screenWidth;
    private final int screenHeight;
    
    // Vue du sac à dos (cliquable)
    private BagComponent backPackView;
        
    // Couleurs

    private static final Color BORDER = new Color(100, 100, 120);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color TEXT_GRAY = new Color(180, 180, 180);
    private static final Color HEALTH_RED = new Color(220, 60, 60);
    private static final Color HEALTH_GREEN = new Color(60, 180, 60);
    private static final Color ENERGY_YELLOW = new Color(220, 200, 60);
    
    // Fonts
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    
    // ==================== CONSTRUCTEUR ====================
    
    public CombatView(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.backPackView = null;
        
    }
    
    public static CombatView create() {
      var screen = Toolkit.getDefaultToolkit().getScreenSize();
      return new CombatView(screen.width, screen.height);
  }
  
  	//Chargement d'image du fond de la page
  	private static BufferedImage profil(String path) throws IOException { 
  		BufferedImage background = ImageIO.read(new File(path));
  		return background;
  	}
    
    // ==================== DESSIN PRINCIPAL ====================
    
    /**
     * Dessine tout l'écran de combat
     * @throws IOException 
     */
    public void draw(Graphics2D g, Combat combat, Hero hero) throws IOException {
        Objects.requireNonNull(g);
        Objects.requireNonNull(combat);
        Objects.requireNonNull(hero);
        g.setColor(new Color(30, 30, 40));
        g.fillRect(0, 0, screenWidth, screenHeight);
        drawBackground(g);// Ok
        // 3. Zone héros (gauche)
        drawHeroZone(g, hero); //ok   
        // 4. Zone ennemis (centre)
        drawEnemyZone(g, combat); //ok    
        // 5. État du tour (droite)
        drawTurnState(g, combat);   
        // 6. Journal (bas gauche)   // ok
        drawLog(g, combat); 
        // 7. Sac à dos (bas droite)
        drawBackPack(g, hero, combat);
        // 8. Écran de fin (si combat terminé)
        if (combat.isOver()) {
            drawEndScreen(g, combat);
        }
    }


 // fond de la page 
    private void drawBackground(Graphics2D g) throws IOException {
    	g.drawImage(profil("images/murLampe.jpg") , 0, 0, screenWidth, screenHeight, null); // marginX = 0 , marginY = 0
    }
 			
    // -------------------- ZONE HÉROS --------------------
    
    private void drawHeroZone(Graphics2D g, Hero hero) throws IOException {
       
    	var heroProfil= ActorPortrayalComponent.create(profil("images/hero.jpeg"),screenWidth, screenHeight,1, hero);
      heroProfil.draw(g, null);
      heroProfil.drawStat(g);
     
    }
    
    // -------------------- ZONE ENNEMIS --------------------
    
    private void drawEnemyZone(Graphics2D g, Combat combat) throws IOException {
     
        var enemies = combat.getEnemies();
        if (enemies.isEmpty()) {
            return;
        }
        for (int i = 0; i < enemies.size(); i++) {
            var enemy = enemies.get(i);
            var enemyProfil = ActorPortrayalComponent.create(profil("images/enemies.png"),screenWidth, screenHeight, (i+1), enemy);
            enemyProfil.draw(g, null);
            enemyProfil.drawStat(g);
        }
    }
       
    
    // -------------------- ÉTAT DU TOUR --------------------
    
    private Color stateColor(Combat combat) {
    	var stateColor = switch (combat.getState()) {
        case HERO_TURN -> HEALTH_GREEN;
        case ENEMY_TURN -> HEALTH_RED;
        case VICTORY -> new Color(255, 215, 0);
        case DEFEAT -> new Color(100, 100, 100);
        default -> TEXT_GRAY;
    	};
    	return stateColor;
    }
    
    private String stateText(Combat combat) {
      var stateText = switch (combat.getState()) {
        case HERO_TURN -> "VOTRE TOUR";
        case ENEMY_TURN -> "TOUR ENNEMI";
        case VICTORY -> "VICTOIRE!";
        case DEFEAT -> "DEFAITE";
        default -> "...";
      };
      return stateText;
    }
    
    private void drawTurnState(Graphics2D g, Combat combat) {
        var x = (int)(screenWidth * 0.4);
        var y = (int)(screenHeight * 0.8);
        var w = (int)(screenWidth * 0.15);
        var h = (int)(screenHeight * 0.1);
        // Couleur selon l'état
        g.setColor(stateColor(combat));
        g.fillRoundRect(x, y, w, h, 10, 10);
        
        g.setColor(Color.BLACK);
        g.setFont(HEADER_FONT);
        var stateText = stateText(combat);    
        int textWidth = g.getFontMetrics().stringWidth(stateText);
        g.drawString(stateText, x + (w - textWidth) / 2, y + 30);
        
        // Instructions
        if (combat.getState() == Combat.CombatState.HERO_TURN) {
            g.setFont(SMALL_FONT);
            g.drawString("ESPACE = fin tour", x + 10, y + 50);
            g.drawString("Clic = utiliser item", x + 10, y + 70);
        }
    }
    
    // -------------------- JOURNAL --------------------
    
    private void drawLog(Graphics2D g, Combat combat) {
        var x = (int) (screenWidth * 0.8);
        var y = 30;
        var w = (int)(screenWidth* 0.2);
        var h = (int)(screenHeight * 0.44);
        drawPanel(g, x, y, w, h, "JOURNAL DE COMBAT");
        var logs = combat.getRecentLogs(12);
        g.setFont(SMALL_FONT);
        var logY = y + 45;
         
        for (var log : logs) {
            if (log.isImportant()) {
                g.setColor(ENERGY_YELLOW);
            } else {
                g.setColor(TEXT_GRAY);
            }
            
            String text = log.message();
            if (text.length() > 60) text = text.substring(0, 57) + "...";
            g.drawString(text, x + 15, logY);
            logY += 18;
        }
    }
    
    // -------------------- SAC À DOS --------------------
    
    private void drawBackPack(Graphics2D g, Hero hero, Combat combat) {
      // Créer le BagComponent si nécessaire
      if (backPackView == null) {
          backPackView = BagComponent.create(screenWidth, screenHeight, hero.getBackpack());
      } else {
          backPackView.setBackPack(hero.getBackpack());
      }
      backPackView.updateEnergy(hero.getEnergy(), hero.getMaxEnergy());
      backPackView.draw(g, null);
  }
    // -------------------- ÉCRAN DE FIN --------------------
    
    private void boxStyle(Graphics2D g,Color boxColor, Combat combat, int boxX,int boxY, int boxW, int boxH) {
    	 g.setColor(boxColor);
       g.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
       
       g.setColor(combat.isVictory() ? HEALTH_GREEN : HEALTH_RED);
       g.setStroke(new BasicStroke(3));
       g.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);
       
       // Texte
       g.setFont(TITLE_FONT);
       g.setColor(TEXT_WHITE);
       String text = combat.isVictory() ? "VICTOIRE !" : "DEFAITE...";
       int textW = g.getFontMetrics().stringWidth(text);
       g.drawString(text, boxX + (boxW - textW) / 2, boxY + 60);
    }
    
    private void drawEndScreen(Graphics2D g, Combat combat) {
        // Overlay semi-transparent
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, screenWidth, screenHeight);
        // Boîte centrale
        var boxW = 400;
        var boxH = 200;
        var boxX = (screenWidth - boxW) / 2;
        var boxY = (screenHeight - boxH) / 2;
        Color boxColor = combat.isVictory() ? new Color(40, 80, 40) : new Color(80, 40, 40);
        boxStyle(g, boxColor, combat, boxX, boxY, boxW, boxH);
        // Stats
        g.setFont(NORMAL_FONT);
        g.drawString("XP gagne: " + combat.getTotalExperience(), boxX + 50, boxY + 100);
        g.drawString("Items: " + combat.getLootedItems().size(), boxX + 50, boxY + 125);
        
        // Instruction
        g.setFont(SMALL_FONT);
        g.setColor(TEXT_GRAY);
        g.drawString("Appuyez sur une touche pour continuer...", boxX + 80, boxY + 170);
    }
    
    // ==================== UTILITAIRES ====================
    
    private void drawPanel(Graphics2D g, int x, int y, int w, int h, String title) {
        // Fond
        g.setColor(new Color(40, 40, 50, 200));
        g.fillRoundRect(x, y, w, h, 10, 10);
        // Bordure
        g.setColor(BORDER);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 10, 10);
        // Titre
        if (title != null) {
            g.setFont(HEADER_FONT);
            g.setColor(TEXT_WHITE);
            g.drawString(title, x + 15, y + 25);
        }
    }
   
    
    // ==================== GETTERS ====================
    
    public BagComponent getBackPackView() {
        return backPackView;
    }
    
    public int getWidth() {
        return screenWidth;
    }
    
    public int getHeight() {
        return screenHeight;
    }
}
