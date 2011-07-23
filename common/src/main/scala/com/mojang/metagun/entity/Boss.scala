package com.mojang.metagun.entity;

import com.mojang.metagun.Art
import com.mojang.metagun.Sound
import com.mojang.metagun.level.Camera
import com.mojang.metagun.screen.Screen

import scala.collection.JavaConversions._

object Boss {
  val MAX_TEMPERATURE = 80 * 5;
}

class Boss(_x : Int, _y : Int) extends BossPart {
  import Entity._
  
  x = _x
  y = _y
  w = 14;
  h = 14;
  bounce = 0.0;
    
  var slamTime = 0;
  var time = 0;
    
  private var temperature = 0;
  private var xo = 0.0
  private var yo = 0.0

  override def tick() {
    if (dieIn > 0) {
      dieIn -= 1
      if (dieIn == 0) die();
    }
    xa = x - xo;
    ya = y - yo;
    time += 1;
    if (time % 60 == 0) {
      for (i <- 0 until 16) {
        val xxa = Math.sin(i * Math.Pi * 2 / 16);
        val yya = Math.cos(i * Math.Pi * 2 / 16);
        level.add(new Gunner(x + xxa * 4, y + yya * 4, xa * 0.2 + xxa, ya * 0.2 + yya - 1));
      }
    } else if (time % 60 > 20 && time % 60 < 40 && time % 4 == 0) {
      var xd = (level.player.x + level.player.w / 2) - (x + w / 2);
      var yd = (level.player.y + level.player.h / 2) - (y + h / 2);
      val dd = Math.sqrt(xd * xd + yd * yd);
      xd /= dd;
      yd /= dd;
      Sound.hit.play();
      level.add(new Bullet(this, x + 2, y + 2, xd, yd));
    }
    xo = x;
    yo = y;

    val entities = level.getEntities(x + 4, y + 4, w - 8, h - 4);
    for (e <- entities) {
      e.collideMonster(this);
    }
  }

  def render(screen : Screen, camera : Camera) {
    screen.draw(Art.gremlins(3)(1), (x - 2).toInt, (y - 2).toInt);
    //FIXME
//        g.setColor(Color.BLACK);
//        yp += 2;
//        xp -= 7;
//        g.fillRect(xp + 5, yp - 8, 20, 3);
//        g.setColor(Color.RED);
//        g.fillRect(xp + 5, yp - 8, 20 - (20 * temperature / MAX_TEMPERATURE), 2);
  }

  override def hitSpikes() {
  }

  private def die() {
    Sound.death.play();
    for (i <- 0 until 32) {
      level.add(new PlayerGore(x + random.nextDouble() * w, y + random.nextDouble() * h));
    }
    Sound.boom.play();
    for (i <- 0 until 32) {
      val dir = i * Math.Pi * 2 / 8.0;
      val xa = Math.sin(dir);
      val ya = Math.cos(dir);
      val dist = ((i / 8) + 1);
      level.add(new Explosion(1, i * 3, x + w / 2 + xa * dist, y + h / 2 + ya * dist, xa, ya));
    }
    remove();
  }

  override def shot(bullet : Bullet) : Boolean = {
    true;
  }

  override def explode(explosion : Explosion) {
    if (explosion.power > 0) {
      die();
    }
  }

}
