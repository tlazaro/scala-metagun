package com.mojang.metagun.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Application.ApplicationType
import com.mojang.metagun.Art
import com.mojang.metagun.Input
import com.mojang.metagun.Stats
import com.mojang.metagun.Metagun
import com.mojang.metagun.level.Level
import com.mojang.metagun.level.Camera

object GameScreen {
  val MAX_HATS = 7
  val DEBUG_MODE = false
}

class GameScreen extends Screen {
  import GameScreen._
  
  var level = new Level(this, 32, 24, xLevel, yLevel, 0, 0)
  
  private var xLevel = if (DEBUG_MODE) 8 else 0
  private var yLevel = if (DEBUG_MODE) 4 else 0

  private val camera = new Camera(Metagun.GAME_WIDTH, Metagun.GAME_HEIGHT);

  var mayRespawn = false;
  private var gunLevel = if(DEBUG_MODE) 2 else 0
  private var hatCount = 1;
  
  Stats.reset();

  level.player.gunLevel = gunLevel;
  level.player.hatCount = hatCount;
  

  override def tick (input : Input) {
    Stats.instance.time += 1;
    if (!input.oldButtons(Input.ESCAPE) && input.buttons(Input.ESCAPE)) {
      setScreen(new PauseScreen(this));
      return;
    }
    if (!level.player.removed)
      level.player.tick(input);
    else if (mayRespawn) {
      if (input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT)) {
        respawnRoom();
        mayRespawn = false;
      }
    }
    level.tick();
    Stats.instance.hats = level.player.hatCount;
  }

  def transition (xa : Int, ya : Int) {
    Stats.instance.hats = level.player.hatCount;
    xLevel += xa;
    yLevel += ya;
    if (yLevel > 10) {
      setScreen(new WinScreen());
      return;
    }
    level.player.x -= xa * 300;
    level.player.y -= ya * 220;
    hatCount = level.player.hatCount;
    if (ya != 0) level.player.y -= 10;
    val newLevel = new Level(this, 32, 24, xLevel, yLevel, (level.player.x).toInt, (level.player.y + ya * 5).toInt);
    newLevel.player.remove();
    newLevel.player = level.player;
    newLevel.add(newLevel.player);
    setScreen(new LevelTransitionScreen(this, xLevel - xa, yLevel - ya, level, newLevel, xa, ya));
    this.level = newLevel;
    level.player.gunLevel = gunLevel;
    level.player.hatCount = hatCount;
    level.player.damage = 0;
  }

  def render () {
    spriteBatch.begin();
//              draw(Art.bg, -xLevel * 160, -yLevel * 120);
    draw(Art.bg, 0, 0);
    spriteBatch.end();
    level.render(this, camera);
                
    spriteBatch.begin();
    if (mayRespawn) {
      val msg = "PRESS X TO TRY AGAIN";
      drawString(msg, 160 - msg.length() * 3, 120 - 3);
    }
    
    if(Gdx.app.getType() == ApplicationType.Android) {
      draw(Art.buttons(0)(0), 0, 240-32);
      draw(Art.buttons(1)(0), 32, 240-32);
                        
      draw(Art.buttons(4)(0), 160-32, 240-32);
      draw(Art.buttons(5)(0), 160, 240-32);
                        
      draw(Art.buttons(2)(0), 320-64, 240-32);
      draw(Art.buttons(3)(0), 320-32, 240-32);
    }
    
    spriteBatch.end();
  }

  def readSign (id : Int) {
    setScreen(new SignReadScreen(this, id));
  }

  def respawnRoom () {
    val newLevel = new Level(this, 32, 24, xLevel, yLevel, level.xSpawn, level.ySpawn);
    this.level = newLevel;
    level.player.gunLevel = gunLevel;
    if (hatCount < 1) hatCount = 1;
    level.player.hatCount = hatCount;
    level.player.damage = 0;
  }

  def getGun (level : Int) {
    gunLevel = level;
  }
}

