package fr.uge.controller;

import java.awt.Color;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;

import fr.uge.data.BackPack;
import fr.uge.data.Hero;
import fr.uge.view.Index;

public class Run {
	public static void run(ApplicationContext context, Hero hero) {
		
		context.renderFrame(graphics -> Index.page().body(context, graphics, hero));
	}
	
	public static void main(String[] args) {
	// Initialisation
	  var HEALTH_POINT = 10;
	  var MAX_HEALTH_POINT = 30;
	  var level = 1;
	  var experience = 100;
	  var maxEnergy = 3;
	  var energy = 3;
	  var manaPoint = 5;
	  var protection = 10;
	  var key = 1;
	  
	  var backpack = new BackPack();
    var hero = new Hero(HEALTH_POINT,MAX_HEALTH_POINT, level, experience,maxEnergy, energy,manaPoint,protection, backpack,key);
		Application.run(Color.BLACK, context -> {
				  run(context,hero);
		});
	}
}
