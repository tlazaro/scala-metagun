package com.mojang.metagun.screen

import com.mojang.metagun.Art
import com.mojang.metagun.Input
import com.mojang.metagun.Metagun
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

abstract class Screen {
  private var metagun : Metagun = _
        
  var spriteBatch : SpriteBatch = _

  def removed () {
    spriteBatch.dispose();
  }

  def init (metagun : Metagun) {
    this.metagun = metagun;
    val projection = new Matrix4();
    projection.setToOrtho(0, 320, 240, 0, -1, 1);

    spriteBatch = new SpriteBatch(100);
    spriteBatch.setProjectionMatrix(projection);
  }

  protected def setScreen (screen : Screen) {
    metagun.setScreen(screen);
  }

  val chars = List("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", ".,!?:;\"'+-=/\\< ");

  def draw (region : TextureRegion, x : Int, y : Int) {
    var width = region.getRegionWidth();
    if(width < 0) width = -width;
    spriteBatch.draw(region, x, y, width, -region.getRegionHeight());
  }

  def drawString (_string : String, x : Int, y : Int) {
    val string = _string.toUpperCase();
    for (i <- 0 until string.length()) {
      val ch = string.charAt(i);
      for (ys <- 0 until chars.length) {
        val xs = chars(ys).indexOf(ch);
        if (xs >= 0) {
          draw(Art.guys(xs)(ys + 9), x + i * 6, y);
        }
      }
    }
  }

  def render()

  def tick (input : Input) {
  }
}

object Screen {
  protected val random = new Random();
}
