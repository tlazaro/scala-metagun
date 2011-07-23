package com.mojang.metagun

import com.badlogic.gdx.backends.jogl.JoglApplication

object Main {
  def main(args: Array[String]): Unit = {
    new JoglApplication(new Metagun(), "Metagun", 640, 480, false)
  }
}
