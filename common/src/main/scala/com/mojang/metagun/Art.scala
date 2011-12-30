package com.mojang.metagun

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

object Art {
  lazy val bg = load("res/background.png", 320, 240)
  lazy val level = new Pixmap(Gdx.files.internal("res/levels.png"))
  lazy val titleScreen = load("res/titlescreen.png", 320, 740)
  lazy val guys = split("res/guys.png", 6, 6)
  lazy val player1 = split("res/player.png", 16, 32)
  lazy val player2 = split("res/player.png", 16, 32, true)
  lazy val walls = split("res/walls.png", 10, 10)
  lazy val gremlins = split("res/gremlins.png", 30, 30)
  lazy val buttons = split("res/buttons.png", 32, 32)
  lazy val shot = new TextureRegion(guys(0)(0).getTexture(), 3, 27, 2, 2)
  lazy val winScreen1 = load("res/winscreen1.png", 320, 240)
  lazy val winScreen2 = load("res/winscreen2.png", 320, 240)
  
  def load () {
    bg; level; titleScreen; guys; player1; player2; walls; gremlins; buttons; shot; winScreen1; winScreen2
  }
        
  private def split (name : String, width : Int, height : Int) : Array[Array[TextureRegion]] = {
    split(name, width, height, false)
  }

  private def split (name : String, width : Int, height : Int, flipX : Boolean) : Array[Array[TextureRegion]] = {
    val texture = new Texture(Gdx.files.internal(name))
    val xSlices = texture.getWidth() / width
    val ySlices = texture.getHeight() / height
    val res = Array.ofDim[TextureRegion](xSlices, ySlices)
    for (x <- 0 until xSlices) {
      for (y <- 0 until ySlices) {
        res(x)(y) = new TextureRegion(texture, x * width, y * height, width, height)
        res(x)(y).flip(flipX, true)
      }
    }
    res
  }

  def load (name : String, width : Int, height : Int) : TextureRegion = {
    val texture = new Texture(Gdx.files.internal(name))
    val region = new TextureRegion(texture, 0, 0, width, height)
    region.flip(false, true)
    return region
  }

}
