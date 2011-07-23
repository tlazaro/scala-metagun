package com.mojang.metagun.entity

import com.mojang.metagun.level.Level
import com.mojang.metagun.level.Camera
import com.mojang.metagun.screen.Screen
import java.util.Random;


object Entity {
  private[entity] val random = new Random();
}

abstract class Entity protected(var x : Double = 0, var y : Double = 0) {
  protected var onGround = false;

  var xa : Double = _
  var ya : Double = _
    
  protected var bounce = 0.05;
  var w = 10
  var h = 10

  protected var level : Level = _
    
  var removed = false
  var xSlot = 0
  var ySlot = 0

  var interactsWithWorld = false;

  def init(level : Level) {
    this.level = level;
  }

  def tryMove(_xa : Double, _ya : Double) {
    var xa = _xa
    var ya = _ya
    onGround = false;
    if (level.isFree(this, x + xa, y, w, h, xa, 0)) {
      x += xa;
    } else {
      hitWall(xa, 0);
      if (xa < 0) {
        val xx = x / 10;
        xa = -(xx - xx.toInt) * 10;
      } else {
        val xx = (x + w) / 10;
        xa = 10 - (xx - xx.toInt) * 10;
      }
      if (level.isFree(this, x + xa, y, w, h, xa, 0)) {
        x += xa;
      }
      this.xa *= -bounce;
    }
    if (level.isFree(this, x, y + ya, w, h, 0, ya)) {
      y += ya;
    } else {
      if (ya > 0) onGround = true;
      hitWall(0, ya);
      if (ya < 0) {
        val yy = y / 10;
        ya = -(yy - yy.toInt) * 10;
      } else {
        val yy = (y + h) / 10;
        ya = 10 - (yy - yy.toInt) * 10;
      }
      if (level.isFree(this, x, y + ya, w, h, 0, ya)) {
        y += ya;
      }
      this.ya *= -bounce;
    }
  }

  protected def hitWall(xa : Double, ya : Double) {
  }

  def remove() {
    removed = true;
  }

  def tick() {
  }

  def render(screen : Screen, camera : Camera)

  def shot(bullet : Bullet) : Boolean = {
    false;
  }

  def hitSpikes() {
  }

  def shove(enemy : Gunner) {
  }

  def outOfBounds() {
    if (y < 0) return;
    remove();
  }

  def explode(explosion : Explosion) {
  }

  def collideMonster(e : Entity) {
  }
}
