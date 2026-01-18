package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;

import fr.uge.model.Hero;

public class HealerView {
    
    private final int screenWidth;
    private final int screenHeight;
    
    // Boutons
    private int[] fullHealBounds;
    private int[] halfHealBounds;
    private int[] leaveBounds;
    
    // État
    private int hoveredButton = -1;  // 0 = full, 1 = half, 2 = leave
    
    // Couleurs
    private static final Color BACKGROUND = new Color(30, 30, 40);
    private static final Color PANEL_BG = new Color(45, 45, 55);
    private static final Color HEALER_GREEN = new Color(100, 220, 100);
    private static final Color TEXT_WHITE = new Color(240, 240, 240);
    private static final Color TEXT_GRAY = new Color(180, 180, 180);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);
    private static final Color HEALTH_RED = new Color(220, 80, 80);
    
    public HealerView(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    // ==================== DESSIN PRINCIPAL ====================
    
    public void draw(Graphics2D g, Hero hero, int fullHealCost, int halfHealCost) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(hero);
        
        // Fond
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Titre
        drawTitle(g);
        
        // Panel central
        drawHealerPanel(g, hero, fullHealCost, halfHealCost);
        
        // Instructions
        drawInstructions(g);
    }
    
    private void drawTitle(Graphics2D g) {
        g.setColor(HEALER_GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        
        String title = "GUÉRISSEUR";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (screenWidth - titleWidth) / 2, 80);
        
        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 16));
        String subtitle = "\"Laissez-moi soigner vos blessures...\"";
        int subWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (screenWidth - subWidth) / 2, 115);
    }
    
    private void fondPanel(Graphics2D g, int panelX, int panelY, int panelW, int panelH) {
    	 // Fond du panel
      g.setColor(PANEL_BG);
      g.fillRoundRect(panelX, panelY, panelW, panelH, 15, 15);
      
      g.setColor(HEALER_GREEN.darker());
      g.setStroke(new BasicStroke(3));
      g.drawRoundRect(panelX, panelY, panelW, panelH, 15, 15);
    }
    
    private void textFullShape(Graphics2D g, int panelX,int panelY, int panelW, int textY) {
    	g.setColor(HEALER_GREEN);
      g.setFont(new Font("Arial", Font.ITALIC, 14));
      String msg = "Vous êtes en pleine forme !";
      int msgW = g.getFontMetrics().stringWidth(msg);
      g.drawString(msg, panelX + (panelW - msgW) / 2, textY);
    }
    
    private void careText (Graphics2D g, int textX, int textY) {
    	 g.setColor(TEXT_WHITE);
       g.setFont(new Font("Arial", Font.BOLD, 18));
       g.drawString("Votre état :", textX, textY);
    }
    
    private void orInformation(Graphics2D g, Hero hero, int textX, int textY) {
    	 // Or disponible
      g.setColor(GOLD_COLOR);
      g.setFont(new Font("Arial", Font.BOLD, 16));
      g.drawString("💰 Or : " + hero.getGold(), textX, textY);
    }
    
    private void drawHealerPanel(Graphics2D g, Hero hero, int fullHealCost, int halfHealCost) {
        var panelW = 500;
        var panelH = 400;
        var panelX = (screenWidth - panelW) / 2;
        var panelY = 150;
        fondPanel(g,panelX,panelY,panelW,panelH);
        // Stats du héros
        var textX = panelX + 30;
        var textY = panelY + 50;
        careText(g,textX,textY);
        textY += 35;
        // Barre de vie
        drawHealthBar(g, hero, textX, textY, panelW - 60);
        textY += 50;
        orInformation(g, hero, textX, textY);
        textY += 50;
        
        // Options de soin
        g.setColor(TEXT_WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Services disponibles :", textX, textY);
        
        textY += 30;
        
        // Bouton Soin complet
        var btnW = panelW - 60;
        var btnH = 50;
        
        var missingHp = hero.getMaxHealthPoint() - hero.getHealthPoint();
        var canAffordFull = hero.getGold() >= fullHealCost;
        var needsHeal = missingHp > 0;
        
        fullHealBounds = new int[]{textX, textY, btnW, btnH};
        drawHealButton(g, textX, textY, btnW, btnH, "Soin complet (+" + missingHp + " PV)", fullHealCost,  canAffordFull && needsHeal,hoveredButton == 0);
                  
        textY += btnH + 15;
        
        // Bouton Demi-soin
        int halfHeal = Math.min(hero.getMaxHealthPoint() / 2, missingHp);
        boolean canAffordHalf = hero.getGold() >= halfHealCost;
        
        halfHealBounds = new int[]{textX, textY, btnW, btnH};
        drawHealButton(g, textX, textY, btnW, btnH, "Petit soin (+" + halfHeal + " PV)", halfHealCost, canAffordHalf && needsHeal,hoveredButton == 1);
        textY += btnH + 15;
        // Bouton Partir
        leaveBounds = new int[]{textX, textY, btnW, btnH};
        drawLeaveButton(g, textX, textY, btnW, btnH, hoveredButton == 2);
        
        // Message si pleine vie
        if (!needsHeal) {
            textY += btnH + 20;
            textFullShape(g,panelX,panelY, panelW, textY);
        }
    }
    
    private void drawHealthBar(Graphics2D g, Hero hero, int x, int y, int width) {
        int barHeight = 30;
        
        // Fond
        g.setColor(new Color(60, 60, 70));
        g.fillRoundRect(x, y, width, barHeight, 8, 8);
        
        // Barre de vie
        double ratio = (double) hero.getHealthPoint() / hero.getMaxHealthPoint();
        int healthWidth = (int) (width * ratio);
        
        Color healthColor;
        if (ratio > 0.6) {
            healthColor = HEALER_GREEN;
        } else if (ratio > 0.3) {
            healthColor = new Color(220, 180, 50);
        } else {
            healthColor = HEALTH_RED;
        }
        
        g.setColor(healthColor);
        g.fillRoundRect(x, y, healthWidth, barHeight, 8, 8);
        
        // Bordure
        g.setColor(new Color(100, 100, 110));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, width, barHeight, 8, 8);
        
        // Texte
        g.setColor(TEXT_WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String hpText = hero.getHealthPoint() + " / " + hero.getMaxHealthPoint() + " PV";
        int textWidth = g.getFontMetrics().stringWidth(hpText);
        g.drawString(hpText, x + (width - textWidth) / 2, y + 21);
    }
    
    private void drawHealButton(Graphics2D g, int x, int y, int w, int h, String text, int cost, boolean enabled, boolean hovered) {                     
        // Fond
        Color bgColor;
        if (!enabled) {
            bgColor = new Color(50, 50, 55);
        } else if (hovered) {
            bgColor = new Color(60, 100, 60);
        } else {
            bgColor = new Color(50, 80, 50);
        }
        
        g.setColor(bgColor);
        g.fillRoundRect(x, y, w, h, 8, 8);
        
        // Bordure
        g.setColor(enabled ? HEALER_GREEN : new Color(80, 80, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 8, 8);
        
        // Texte
        g.setColor(enabled ? TEXT_WHITE : TEXT_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(text, x + 15, y + 30);
        
        // Prix
        g.setColor(enabled ? GOLD_COLOR : new Color(120, 100, 50));
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String priceText = cost + " Or";
        int priceWidth = g.getFontMetrics().stringWidth(priceText);
        g.drawString(priceText, x + w - priceWidth - 15, y + 30);
    }
    
    private void drawLeaveButton(Graphics2D g, int x, int y, int w, int h, boolean hovered) {
        // Fond
        Color bgColor = hovered ? new Color(70, 70, 80) : new Color(55, 55, 65);
        g.setColor(bgColor);
        g.fillRoundRect(x, y, w, h, 8, 8);
        
        // Bordure
        g.setColor(new Color(120, 120, 130));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 8, 8);
        
        // Texte
        g.setColor(TEXT_WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String text = "Partir";
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, x + (w - textWidth) / 2, y + 30);
    }
    
    private void drawInstructions(Graphics2D g) {
        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        
        String instructions = "CLIC pour choisir | ESPACE pour partir";
        int width = g.getFontMetrics().stringWidth(instructions);
        g.drawString(instructions, (screenWidth - width) / 2, screenHeight - 50);
    }
    
    // ==================== INTERACTION ====================
    
    public int getButtonAt(int mouseX, int mouseY) {
        if (isInBounds(mouseX, mouseY, fullHealBounds)) return 0;
        if (isInBounds(mouseX, mouseY, halfHealBounds)) return 1;
        if (isInBounds(mouseX, mouseY, leaveBounds)) return 2;
        return -1;
    }
    
    private boolean isInBounds(int x, int y, int[] bounds) {
        if (bounds == null) return false;
        return x >= bounds[0] && x <= bounds[0] + bounds[2] &&
               y >= bounds[1] && y <= bounds[1] + bounds[3];
    }
    
    public void setHoveredButton(int button) {
        this.hoveredButton = button;
    }
    
    public int[] getFullHealBounds() { return fullHealBounds; }
    public int[] getHalfHealBounds() { return halfHealBounds; }
    public int[] getLeaveBounds() { return leaveBounds; }
}