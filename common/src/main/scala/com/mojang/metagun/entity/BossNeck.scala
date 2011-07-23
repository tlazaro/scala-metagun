package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

class BossNeck(_x : Int, _y : Int, var child : BossPart) extends BossPart {
  x = _x
  y = _y
  import Entity._
  
  var slamTime = 0;
  var baseRot = Math.Pi * 1.25;
  var rot = 0.0
  var rota = 0.0;
  var time = 0;

  w = 12
  h = 12
  bounce = 0;

  override def tick() {
    if (dieIn > 0) {
      dieIn -= 1
      if (dieIn == 0) die();
    }
    time += 1;

    rot = Math.sin(time / 40.0) * Math.cos(time / 13.0) * 0.5;
    rota *= 0.9;
    rot *= 0.9;
    val rr = baseRot + rot;
    val xa = Math.sin(rr);
    val ya = Math.cos(rr);
    child.x = x + xa * 8;
    child.y = y + ya * 8;
    child.setRot(rr);

    val entities = level.getEntities(x + 4, y + 4, w - 8, h - 4);
    for (e <- entities) {
      e.collideMonster(this);
    }
  }

  override def setRot(rot : Double) {
    baseRot = rot;
  }

  def render(screen : Screen, camera : Camera) {
    val xp : Int = (x - 1).toInt
    val yp : Int =  (y - 1).toInt
    screen.draw(Art.gremlins(4)(1), xp, yp)
  }

  override def hitSpikes() {
    die();
  }

  private def die() {
    child.dieIn = 5;
    for (i <- 0 until 4) {
      level.add(new PlayerGore(x + random.nextDouble() * w, y + random.nextDouble() * h));
    }
    for (i <- 0 until 4) {
      val dir = i * Math.Pi * 2 / 8.0;
      val xa = Math.sin(dir);
      val ya = Math.cos(dir);
      val dist = ((i / 8) + 1);
      level.add(new Explosion(1, i * 3, x + w / 2 + xa * dist, y + h / 2 + ya * dist, xa, ya));
    }
    remove();
  }

  override def shot(bullet : Bullet) : Boolean = {
    return true;
  }

  override def explode(explosion : Explosion) {
    if (explosion.power > 0) {
      die();
    }
  }
}
