package com.mojang.metagun.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mojang.metagun.Art;
import com.mojang.metagun.Sound;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

class Hat(_x : Double, _y : Double, var xPos : Int = -1, yPos : Int = -1) extends Entity(_x, _y) {
  w = 6;
  h = 3;
  bounce = 0;
  ya = -1;
  var time = Math.Pi * 0.5;
    
  var noTakeTime = 30;
  var xxa = 0.0;

  override def tick() {
    tryMove(xa, ya);
    if (onGround) {
      time = 0;
    } else {
      time += 1;
    }

    xa = xxa+Math.sin(time * 0.05) * 0.2;
    xxa*=0.95;
    ya *= 0.95;
    ya += Level.GRAVITY * 0.1;

    if (noTakeTime > 0) noTakeTime -= 1;
    else {
      val entities = level.getEntities(x.toInt, y.toInt, w, h);
      for (e <- entities) {
        e match {
          case player : Player => {
              player.hatCount += 1;

              if (xPos >= 0 && yPos >= 0) {
                Art.level.setColor(0, 0, 0, 0);
                Art.level.drawPixel(xPos, yPos);
              }
              Sound.gethat.play();
              remove();
            }
          case _ =>
        }
      }
    }
  }

  def render(g : Screen, camera : Camera) {
    var dir = 1;
    val xp = x.toInt - (16 - w) / 2;
    val yp = y.toInt - 2;
    val sheet = if (dir == 1) Art.player1 else Art.player2;

    var xFrame = (xa * 10).toInt
    if (xFrame < -1) xFrame = -1;
    if (xFrame > +1) xFrame = +1;
    g.draw(sheet(1 + xFrame)(1), xp, yp);
  }

  override def shot(bullet : Bullet) : Boolean = {
    Sound.hit.play();
    xa += bullet.xa * 0.5;
    ya += bullet.ya * 0.5;

    return true;
  }
}
