package fr.uge.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Objects;

import fr.uge.model.Enemy;

public class BossView {
    
    private final int screenWidth;
    private final int screenHeight;
    
    // Couleurs

    private static final Color BOSS_RED = new Color(200, 50, 50);
    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color TEXT_WHITE = new Color(240, 240, 240);
    private static final Color TEXT_GRAY = new Color(180, 180, 180);
    
    // Animation
    private int animationFrame = 0;
    
    public BossView(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    // ==================== DESSIN PRINCIPAL ====================
    
    public void draw(Graphics2D g, Enemy boss, int floorNumber) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(boss);
        animationFrame++;
        // Fond sombre avec effet de pulsation
        drawBackground(g);
        // Titre d'avertissement
        drawWarning(g);
        // Présentation du Boss
        drawBossPresentation(g, boss, floorNumber);
        // Stats du Boss
        drawBossStats(g, boss);
        // Instructions
        drawInstructions(g);
    }
    
    private void drawBackground(Graphics2D g) {
        // Fond avec pulsation rouge
        var pulse = (int) (20 * Math.sin(animationFrame * 0.05));
        g.setColor(new Color(20 + pulse, 10, 10));
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        // Bordure rouge
        g.setColor(BOSS_RED);
        g.setStroke(new BasicStroke(8));
        g.drawRect(10, 10, screenWidth - 20, screenHeight - 20);
        
        // Coins décoratifs
        drawCornerDecoration(g, 20, 20);
        drawCornerDecoration(g, screenWidth - 60, 20);
        drawCornerDecoration(g, 20, screenHeight - 60);
        drawCornerDecoration(g, screenWidth - 60, screenHeight - 60);
    }
    
    private void drawCornerDecoration(Graphics2D g, int x, int y) {
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(3));
        g.drawLine(x, y, x + 40, y);
        g.drawLine(x, y, x, y + 40);
    }
    
    private void drawWarning(Graphics2D g) {
        // Effet de clignotement
        if ((animationFrame / 30) % 2 == 0) {
            g.setColor(BOSS_RED);
        } else {
            g.setColor(GOLD);
        }
        
        g.setFont(new Font("Arial", Font.BOLD, 48));
        var warning = "⚠️ ATTENTION ⚠️";
        var warningWidth = g.getFontMetrics().stringWidth(warning);
        g.drawString(warning, (screenWidth - warningWidth) / 2, 100);
    }
    
    private void drawBossStyle(Graphics2D g, int centerX,int centerY,int circleSize) {
    	 // Effet de pulsation
      var pulse = (int) (10 * Math.sin(animationFrame * 0.08));
      // Ombre
      g.setColor(new Color(0, 0, 0, 100));
      g.fillOval(centerX - circleSize/2 + 5, centerY - circleSize/2 + 5, circleSize + pulse, circleSize + pulse);      
      // Cercle principal
      g.setColor(new Color(60, 20, 20));
      g.fillOval(centerX - circleSize/2, centerY - circleSize/2,circleSize + pulse, circleSize + pulse);
      
      // Bordure dorée
      g.setColor(GOLD);
      g.setStroke(new BasicStroke(4));
      g.drawOval(centerX - circleSize/2, centerY - circleSize/2,circleSize + pulse, circleSize + pulse); 
                 
    }
    
    private void drawBossInformation(Graphics2D g, Enemy boss,int centerX,int centerY,int circleSize) {
      // Icône du boss (selon le type)
      String bossIcon = getBossIcon(boss);
      g.setFont(new Font("Arial", Font.PLAIN, 80));
      int iconWidth = g.getFontMetrics().stringWidth(bossIcon);
      g.drawString(bossIcon, centerX - iconWidth/2, centerY + 30);
      
      // Nom du boss
      g.setColor(GOLD);
      g.setFont(new Font("Arial", Font.BOLD, 36));
      String bossName = boss.getType().getName();
      int nameWidth = g.getFontMetrics().stringWidth(bossName);
      g.drawString(bossName, (screenWidth - nameWidth) / 2, centerY + circleSize/2 + 50);
    }
    
    
    private void drawBossPresentation(Graphics2D g, Enemy boss, int floorNumber) {
        var centerX = screenWidth / 2;
        var centerY = screenHeight / 2 - 50;
        // Cercle de fond pour le boss
        var circleSize = 200;
        
        drawBossStyle(g,centerX,centerY,circleSize); 
        drawBossInformation(g,boss, centerX, centerY, circleSize);
        
        // Titre de l'étage
        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 18));
        String floorTitle = "Boss de l'étage " + floorNumber;
        int floorWidth = g.getFontMetrics().stringWidth(floorTitle);
        g.drawString(floorTitle, (screenWidth - floorWidth) / 2, centerY + circleSize/2 + 80);
    }
    
    private String getBossIcon(Enemy boss) {
        return switch (boss.getType()) {
            case BOSS_GOLEM -> "🗿";
            case BOSS_DRAGON -> "🐉";
            case BOSS_DEMON -> "👹";
            default -> "👑";
        };
    }
    
    private void drawBossStats(Graphics2D g, Enemy boss) {
        var panelX = screenWidth / 2 - 150;
        var panelY = screenHeight - 200;
        var panelW = 300;
        var panelH = 100;
        // Fond du panel
        g.setColor(new Color(40, 20, 20, 200));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 10, 10);
        // Bordure
        g.setColor(BOSS_RED);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 10, 10);
        // Stats
        var textX = panelX + 20;
        var textY = panelY + 30; 
        g.setFont(new Font("Arial", Font.BOLD, 14));
        // PV
        g.setColor(new Color(255, 100, 100));
        g.drawString("❤️ PV : " + boss.getHealthPoint() + " / " + boss.getMaxHealthPoint(), textX, textY);
        // Dégâts
        textY += 25;
        g.setColor(new Color(255, 150, 100));
        g.drawString("⚔️ Dégâts : " + boss.getBaseDamage(), textX, textY);
        // Armure
        textY += 25;
        g.setColor(new Color(150, 150, 255));
        g.drawString("🛡️ Armure : " + boss.getBaseArmor(), textX, textY);
    }
    
    private void drawInstructions(Graphics2D g) {
        g.setColor(TEXT_WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String text = "Appuyez sur ESPACE pour commencer le combat !";
        int textWidth = g.getFontMetrics().stringWidth(text);
        // Effet de pulsation sur l'opacité
        int alpha = (int) (200 + 55 * Math.sin(animationFrame * 0.1));
        g.setColor(new Color(240, 240, 240, Math.min(255, alpha)));
        g.drawString(text, (screenWidth - textWidth) / 2, screenHeight - 50);
    }
    
    // ==================== GETTERS ====================
    
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}