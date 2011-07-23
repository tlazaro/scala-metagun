package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

class HeadGore(_x : Double, _y : Double) extends Entity(_x, _y) {
  import Entity._
  
  w = 2
  h = 2
  bounce = 0.2;
  xa = 0 + (random.nextDouble() - random.nextDouble()) * 0.5;
  ya = -1 + (random.nextDouble() - random.nextDouble()) * 0.5;
    
  private var life = random.nextInt(60) + 20;

  override def tick() {
    life -= 1
    if (life <= 0) remove();
    onGround = false;
    tryMove(xa, ya);

    xa *= Level.FRICTION;
    ya *= Level.FRICTION;
    ya += Level.GRAVITY*0.5;
    level.add(new Gore(x+random.nextDouble(), y+random.nextDouble()-1, xa, ya));
  }

  override protected def hitWall(xa : Double, ya : Double) {
    this.xa *= 0.8;
    this.ya *= 0.8;
  }

  def render(g : Screen, camera : Camera) {
    g.draw(Art.guys(6)(1), x.toInt, y.toInt);
  }
    
  override def hitSpikes() {
    for (i <- 0 until 4) {
      xa = (random.nextFloat()-random.nextFloat())*6;
      ya = (random.nextFloat()-random.nextFloat())*6;
      level.add(new Gore(x + random.nextDouble(), y + random.nextDouble() - 1, xa, ya));
    }
    remove();
  }
    
}
