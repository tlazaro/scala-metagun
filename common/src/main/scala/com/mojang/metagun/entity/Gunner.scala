package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.Sound;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

object Gunner {
  val CHARGE_DURATION = 100; 
}

class Gunner(_x : Double, _y : Double, _xa : Double, _ya : Double) extends Entity(_x, _y) {
  import Entity._
  import Gunner._
  
  w = 6;
  h = 6;
  bounce = -0.1;
  xa = _xa + (random.nextDouble() - random.nextDouble()) * 0.5;
  ya = _ya + (random.nextDouble() - random.nextDouble()) * 0.5;
    
  var chargeTime = 0;
  private var sliding = 0;


  override def tick() {
    onGround = false;
    tryMove(xa, ya);

    if ((onGround || sliding != 0) && xa * xa < 0.01) {
      chargeTime += 1
      if (chargeTime >= CHARGE_DURATION) {
        chargeTime = 0;
        var xd = (level.player.x + level.player.w / 2) - (x + w / 2);
        var yd = (level.player.y + level.player.h / 2) - (y + h / 2);
        val dd = Math.sqrt(xd * xd + yd * yd);
        xd /= dd;
        yd /= dd;
        Sound.hit.play();
        level.add(new Bullet(this, x + 2, y + 2, xd, yd));
      }
    }
    xa *= Level.FRICTION;
    ya *= Level.FRICTION;
    ya += Level.GRAVITY;
  }

  override protected def hitWall(xa : Double, ya : Double) {
    sliding = 0;
    if (xa != 0) {
      if (xa > 0) {
        this.xa = 1;
        sliding = 1;
      }
      if (xa < 0) {
        this.xa = -1;
        sliding = -1;
      }
    }
    this.xa *= 0.4;
    this.ya *= 0.4;
  }


  def render(g : Screen, camera : Camera) {
    //        g.setColor(Color.red);
    val xp = x.toInt;
    val yp = y.toInt;
    //        g.fillRect(xp, yp, w, h);

    var xFrame = 0;
    var yFrame = 0;
    if (onGround) {
      var xd = (level.player.x + level.player.w / 2) - (x + w / 2);
      var yd = (level.player.y + level.player.h / 2) - (y + h / 2);
      val dd = Math.sqrt(xd * xd + yd * yd);
      xd /= dd;
      yd /= dd;
      xFrame = 3;
      yFrame = 2;
      val s = 0.3;
      if (xd > s) xFrame+=1;
      if (xd < -s) xFrame-=1;
      if (yd > s) yFrame+=1;
      if (yd < -s) yFrame-=1;
    } else if (sliding != 0) {
      var xd = (level.player.x + level.player.w / 2) - (x + w / 2);
      var yd = (level.player.y + level.player.h / 2) - (y + h / 2);
      val dd = Math.sqrt(xd * xd + yd * yd);
      xd /= dd;
      yd /= dd;
      xFrame = 0;
      yFrame = 2;
      if (sliding > 0) xFrame = 1;
      val s = 0.3;
      if (yd > s) yFrame+=1;
      if (yd < -s) yFrame-=1;
    } else {
      xFrame = (1 - Math.floor(ya * 0.1)).toInt
      if (xFrame < 0) xFrame = 0;
      if (xFrame > 2) xFrame = 2;
      yFrame = 0;
    }


    g.draw(Art.guys(xFrame)(yFrame), xp, yp);
        
    val entities = level.getEntities(x.toInt, y.toInt, 1, 1);
    for (e <- entities) {
      e.shove(this);
    }        
  }
    
  override def shot(bullet : Bullet) : Boolean = {
    die();
    return true;
  }
    
  override def hitSpikes() {
    die();
  }

  protected def die() {
    Sound.splat.play();
    level.add(new HeadGore(x+2, y));
    for (i <- 0 until 10) {
      val xd = (random.nextDouble()-random.nextDouble())*4;
      val yd = (random.nextDouble()-random.nextDouble())*4;
            
      level.add(new Gore(x+2+random.nextDouble(), y+random.nextDouble()*6, xa+xd, ya+yd));
    }
    remove();
  }
    
  override def shove(enemy : Gunner) {
    var xd = enemy.x-x;
    if (xd<0) {
      xd = -0.01;
    } else if (xd>0) {
      xd = 0.01;
    } else {
      if (random.nextBoolean()) {
        xd = -0.01;
      } else {
        xd = 0.01;
      }
    }
                  
    enemy.xa+=xd;
    xa-=xd;
  }

  override def explode(explosion : Explosion) {
    die();
  }
    
  override def collideMonster(e : Entity) {
    die();
  }
}
