package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

class Gore(_x : Double, _y : Double, _xa : Double, _ya : Double) extends Entity(_x, _y) {
  import Entity._
  
  w = 1;
  h = 1;
  bounce = 0.2;
  xa = (_xa + (random.nextDouble() - random.nextDouble()) * 0.2);
  ya = (_ya + (random.nextDouble() - random.nextDouble()) * 0.2);
  
  private var life = random.nextInt(20) + 10;

  override def tick() {
    life -= 1
    if (life <= 0) remove();
    onGround = false;
    tryMove(xa, ya);

    xa *= 0.999;
    ya *= 0.999;
    ya += Level.GRAVITY*0.15;
  }

  override protected def hitWall(xa : Double, ya : Double) {
    this.xa *= 0.4;
    this.ya *= 0.4;
  }

  def render(g : Screen, camera : Camera) {
    g.draw(Art.guys(7)(1), x.toInt, y.toInt);
  }
}
