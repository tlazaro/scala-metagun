package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.Sound;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

object Gremlin {
  val MAX_TEMPERATURE = 80 * 5;
}

class Gremlin(var power : Int, _x : Int, _y : Int) extends Entity(_x, _y) {
  import Entity._
  import Gremlin._
  
  w = 30;
  h = 30;
  bounce = 0;
    
  private var temperature = 0;
  var jumpDelay = 0;

  override def tick() {
    if (temperature > 0) {
      temperature -= 1;
      if (random.nextInt(MAX_TEMPERATURE) <= temperature) {
        val xd = (random.nextDouble() - random.nextDouble()) * 0.2;
        val yd = (random.nextDouble() - random.nextDouble()) * 0.2;
        level.add(new Spark(x + random.nextDouble() * w, y + random.nextDouble() * h, xa * 0.2 + xd, ya * 0.2 + yd));
      }
    }
    tryMove(xa, ya);
    xa *= 0.4;
    ya *= Level.FRICTION;
    ya += Level.GRAVITY;

    if (onGround) {
      if (power==1 && jumpDelay <= 19) {
        if (jumpDelay % 2 == 0) {
          if (jumpDelay % 4 == 0) {
            Sound.hit.play();
          }
          val dir = jumpDelay / 32.0f * Math.Pi * 2+0.1;
          val xa = Math.cos(dir);
          val ya = -Math.sin(dir);
          level.add(new Bullet(this, x + 15, y + 10, xa, ya));
        }
      }
      jumpDelay += 1
      if (jumpDelay > 60) {
        ya = -2;
        jumpDelay = 0;
      }
    }

    val entities = level.getEntities((x+4).toInt, (y+4).toInt, w-8, h-4);
    for (e <- entities) {
      e.collideMonster(this);
    }
  }

  def render(g : Screen, camera : Camera) {
    val xp = x.toInt;
    val yp = y.toInt;
    if (onGround) {
      g.draw(Art.gremlins(0)(power), xp, yp);
    } else {
      val idx = if (ya > 0) 2 else 1
      g.draw(Art.gremlins(idx)(power), xp, yp);
    }
    // FIXME
//        g.setColor(Color.BLACK);
//        g.fillRect(xp + 5, yp - 8, 20, 3);
//        g.setColor(Color.RED);
//        g.fillRect(xp + 5, yp - 8, 20 - (20 * temperature / MAX_TEMPERATURE), 2);
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
    Sound.pew.play();
    for (i <- 0 until 4) {
      val xd = (random.nextDouble() - random.nextDouble()) * 4 - bullet.xa * 3;
      val yd = (random.nextDouble() - random.nextDouble()) * 4 - bullet.ya * 3;
      level.add(new Gore(bullet.x, bullet.y, xa + xd, ya + yd));
    }
    Sound.oof.play();
    temperature += 80;
    if (temperature >= MAX_TEMPERATURE) {
      die();
    } else {
      level.add(new PlayerGore(bullet.x, bullet.y));
    }

    return true;
  }

  override def explode(explosion : Explosion) {
    die();
  }
}
