package com.mojang.metagun.screen;

import com.mojang.metagun.Art;
import com.mojang.metagun.Input;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;

object LevelTransitionScreen {
	val TRANSITION_DURATION = 20
}

class LevelTransitionScreen(var parent : Screen,
                            var xLevel : Int,
                            var yLevel : Int,
                            var level1 : Level,
                            var level2 : Level,
                            var xa : Int,
                            var ya : Int) extends Screen {
  
  import LevelTransitionScreen._
  
	private var time  : Int = 0

	override def tick (input : Input) {
		time+=1;
		if (time == TRANSITION_DURATION) {
			setScreen(parent);
		}
	}
	
	val c  = new Camera(320, 240);	
	def render () {		
		val pow = time / TRANSITION_DURATION.toDouble;
		
		spriteBatch.getTransformMatrix().idt();
		spriteBatch.begin();		
//		draw(Art.bg, -xLevel * 160 - (int)(xa * 160 * pow), -yLevel * 120 - (int)(ya * 120 * pow));
		draw(Art.bg, 0, 0);
		spriteBatch.end();
				
		c.x = (-xa * 320 * pow).toInt
		c.y = (-ya * 240 * pow).toInt				
		level1.render(this, c);				
		
		c.x = (xa * 320 * (1-pow)).toInt
		c.y = (ya * 240 * (1-pow)).toInt
		level2.render(this, c);		
	}
}
