package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.Sound;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

object Jabberwocky {
  val MAX_TEMPERATURE = 80 * 5;
}

class Jabberwocky(_x : Int, _y : Int) extends Entity(_x, _y) {
  import Jabberwocky._
  import Entity._
  
  w = 30;
  h = 20;
  bounce = 0;
    
  private var temperature = 0;
  var slamTime = 0;

  override def tick() {
    slamTime += 1;
    if (temperature > 0) {
      temperature -= 1;
      if (random.nextInt(MAX_TEMPERATURE) <= temperature) {
        val xd = (random.nextDouble() - random.nextDouble()) * 0.2;
        val yd = (random.nextDouble() - random.nextDouble()) * 0.2;
        level.add(new Spark(x + random.nextDouble() * w, y + random.nextDouble() * h, xa * 0.2 + xd, ya * 0.2 + yd));
      }
    }
    tryMove(xa, ya);
    xa *= Level.FRICTION;
    ya *= Level.FRICTION;
    ya += Level.GRAVITY;

    val entities = level.getEntities((x + 4)toInt, (y + 4).toInt, w - 8, h - 4);
    for (e <- entities) {
      e match {
        case gunner : Gunner => {
            temperature += 10;
            if (temperature >= MAX_TEMPERATURE) {
              die();
            }
          }
        case _ => 
      }
      e.collideMonster(this);
    }
  }

  def render(g : Screen, camera : Camera) {
    val xp = x.toInt;
    val yp = (y-10).toInt;
    val idx = if(slamTime / 10 % 5 == 2) 1 else 0
    g.draw(Art.gremlins(3 + idx)(0), xp, yp)
    // FIXME
//        g.setColor(Color.BLACK);
//        yp+=10;
//        g.fillRect(xp + 5, yp - 8, 20, 3);
//        g.setColor(Color.RED);
//        g.fillRect(xp + 5, yp - 8, (20 * temperature / MAX_TEMPERATURE), 2);
  }

  override def hitSpikes() {
    die();
  }

  private def die() {
    Sound.death.play();
    for (i <- 0 until 16) {
      level.add(new PlayerGore(x + random.nextDouble() * w, y + random.nextDouble() * h));
    }
    Sound.boom.play();
    for (i <- 0 until 32) {
      val dir = i * Math.Pi * 2 / 8.0;
      val xa = Math.sin(dir);
      val ya = Math.cos(dir);
      val dist = ((i / 8) + 1);
      level.add(new Explosion(0, i * 3, x + w / 2 + xa * dist, y + h / 2 + ya * dist, xa, ya));
    }
    remove();
  }

  override def shot(bullet : Bullet) : Boolean = {
    return true;
  }

  override def explode(explosion : Explosion) {
    die();
  }
}
