package fr.uge.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.forax.zen.ApplicationContext;

import fr.uge.data.Enemy;
import fr.uge.data.Hero;


public class Index {
	private final int xResolution;
	private final int yResolution;
	
	public Index (int xResolution, int yResolution) {
		this.xResolution = xResolution;
		this.yResolution = yResolution;
	
	}
	
	// création de la résolution
	public static Index page() {
		var screen = Toolkit.getDefaultToolkit().getScreenSize();
		return new Index(screen.width,screen.height);
	}
	
	//Chargement d'image du fond de la page
	private static BufferedImage profil(String path) throws IOException { 
		BufferedImage background = ImageIO.read(new File(path));
		return background;
	}
		

	public void body(ApplicationContext context, Graphics2D g, Hero hero, Enemy enemy) {
		try {
	
			// fond de la page 
			g.drawImage(profil("images/murLampe.jpg") , 0, 0, xResolution, yResolution, null); // marginX = 0, marginY = 0
			 
			//Grand title
			new TitleComponent(xResolution, yResolution, "⚔️ BACKPACK HERO ⚔️", Color.WHITE).draw(g,null);
			
			// Panneau statistique
			new PanelComponent(20, 20,150, 120, "Stat", Color.gray, Color.WHITE).draw(g, new StatsComponent(40, 40, hero));
			
		  
			//Hero
		  ActorPortrayalComponent.create(profil("images/hero.jpeg"),xResolution, yResolution, hero).draw(g, null);
		  
		  // Enemy
		  ActorPortrayalComponent.create(profil("images/enemies.png"),xResolution, yResolution, enemy).draw(g, null);
			
		} catch (IOException e) {
			System.err.println("Fichier inaccessible");
			e.printStackTrace();
		}
	}
	

}
