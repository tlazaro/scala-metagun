package com.mojang.metagun

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.ApplicationListener
import com.mojang.metagun.screen.TitleScreen
import com.mojang.metagun.screen.Screen

class Metagun extends ApplicationListener {
        

  private val input = new Input();
  private var screen : Screen = _
  
  private var running = false;
  private var started = false;
  private var accum = 0.f;
        
  def create() {
    Art.load();
		Sound.load();
		Gdx.input.setInputProcessor(input);		
		running = true;
		setScreen(new TitleScreen());
//		setScreen(new GameScreen());	           
  }

  def pause() {
    running = false;
  }

  def resume() {
    running = true;
  }

  def setScreen(newScreen : Screen) {
    if (screen != null)
      screen.removed();
    screen = newScreen;
    if (screen != null)
      screen.init(this);
  }

  def render() {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    accum += Gdx.graphics.getDeltaTime();
    while(accum > 1.0f / 60.0f) {                   
      screen.tick(input);                     
      input.tick();
      accum -= 1.0f / 60.0f;
    }
    screen.render();        
//              batch.begin();
//              font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 10, 30);
//              batch.end();
  }
        
  override def resize(width : Int, height : Int) {
    // TODO Auto-generated method stub

  }

  override def dispose() {
    // TODO Auto-generated method stub
  }
}

object Metagun {
  val GAME_WIDTH = 320;
  val GAME_HEIGHT = 240;
  val SCREEN_SCALE = 2;

  val serialVersionUID = 1L;
}
