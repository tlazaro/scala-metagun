package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

class Explosion(var power : Int, var delay : Int, _x : Double, _y : Double, _xa : Double, _ya : Double) extends Entity(_x, _y) {
  import Entity._
  
  w = 1;
  h = 1;
  bounce = 0.2;
  
  xa = (_xa + (random.nextDouble() - random.nextDouble()) * 0.2);
  ya = (_ya + (random.nextDouble() - random.nextDouble()) * 0.2);
    
  private var life = 0
  
  private var color = random.nextInt(3);
  private var duration = random.nextInt(20) + 10;

  override def tick() {
    life += 1
    if (life >= duration) remove();
    interactsWithWorld = (life > 10);
    onGround = false;
    //        tryMove(xa, ya);
    x += xa;
    y += ya;

    level.isFree(this, x, y, w, h, 0, 0);
    xa *= 0.95;
    ya *= 0.95;
    ya -= Level.GRAVITY * 0.15;

    if (interactsWithWorld && life < duration*0.75) {
      val entities = level.getEntities(x.toInt, y.toInt, 1, 1);
      for (e <- entities) {
        e.explode(this);
      }
    }
  }

  override protected def hitWall(xa : Double, ya : Double) {
    this.xa *= 0.4;
    this.ya *= 0.4;
  }

  def render(g : Screen, camera : Camera) {
    val xp = x.toInt;
    val yp = y.toInt;
    g.draw(Art.guys((life - 1) * 8 / duration)(4 + color), xp - 3, yp - 3);
  }
}
