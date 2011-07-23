package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

class Bullet(var source : Entity, _x : Double, _y : Double, _xa : Double, _ya : Double) extends Entity(_x, _y) {
  import Entity._
  
  w = 1;
  h = 1;
  xa = _xa + (random.nextDouble() - random.nextDouble()) * 0.1;
  ya = _ya + (random.nextDouble() - random.nextDouble()) * 0.1;
  interactsWithWorld = true;
    
  var noHitTime = 10;
  private var time = 0;

  override def tick() {
    time += 1;
    tryMove(xa, ya);

    if (noHitTime > 0) {
      noHitTime-=1;
      return;
    }
    
    val entities = level.getEntities(x.toInt, y.toInt, 1, 1)
    for (e <- entities if e != source) {
      if (e.shot(this)) {
        remove();
      }
    }
  }

  override protected def hitWall(xa : Double, ya : Double) {
    for (i <- 0 until 3) {
      level.add(new Spark(x, y, 0, 0));
    }
    remove();
  }

  def render(g : Screen, camera : Camera) {
    // FIXME
//        if (tick % 2 == 0) {
//            g.setColor(Color.YELLOW);
//            int x1 = (int) (x + w / 2 - xa * 3);
//            int y1 = (int) (y + h / 2 - ya * 3);
//            int x2 = (int) (x + w / 2);
//            int y2 = (int) (y + h / 2);
//
//            g.drawLine(x1, y1, x2, y2);
//            g.setColor(Color.WHITE);
//
//            x1 = (int) (x + w / 2 - xa);
//            y1 = (int) (y + h / 2 - ya);
//            x2 = (int) (x + w / 2 + xa);
//            y2 = (int) (y + h / 2 + ya);
//
//            g.drawLine(x1, y1, x2, y2);
//        } else {
//            g.setColor(Color.YELLOW);
//            int x1 = (int) (x + w / 2 - xa);
//            int y1 = (int) (y + h / 2 - ya);
//            int x2 = (int) (x + w / 2 + xa);
//            int y2 = (int) (y + h / 2 + ya);
//
//            g.drawLine(x1, y1, x2, y2);
//        }

    g.draw(Art.shot, x.toInt, y.toInt);
    //        g.fillRect(xp, yp, w, h);
  }
}
