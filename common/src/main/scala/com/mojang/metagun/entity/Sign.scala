package com.mojang.metagun.entity;

import com.mojang.metagun.Art;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

class Sign(_x : Int, _y : Int, var id : Int) extends Entity(_x, _y) {
  w = 6
  h = 6
  xa = 0
  ya = 0
    
  var autoRead = id == 1;
  if (id==6) autoRead = true;
  if (id==15) autoRead = true;

  override def tick() {
    if (id==6 && level.player.gunLevel>=1) remove();
    if (id==15 && level.player.gunLevel>=2) remove();
    
    val entities = level.getEntities(x.toInt, y.toInt, 6, 6);
    for (e <- entities) {
      e match {
        case player : Player => player.readSign(this)
        case _ =>
      }
    }
  }

  def render(g : Screen, camera : Camera) {
    if (id==6 && level.player.gunLevel>=1) return;
    if (id==15 && level.player.gunLevel>=2) return;
    if (id==6) {
      g.draw(Art.walls(5)(0), x.toInt, y.toInt);
    } else if (id==15) {
      g.draw(Art.walls(6)(0), x.toInt, y.toInt);
    } else {
      g.draw(Art.walls(4)(0), x.toInt, y.toInt);
    }
  }
}
