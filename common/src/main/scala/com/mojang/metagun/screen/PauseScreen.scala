package com.mojang.metagun.screen

import com.mojang.metagun.Art
import com.mojang.metagun.Input

class PauseScreen(private[this] val parent : GameScreen) extends Screen {
  private var selected : Int = 0;

  private val options = Array("BACK TO GAME", "AUTOMORTIS", "QUIT TO TITLE")

  def render() {
    parent.render();
        
    spriteBatch.begin();
    var xs = 0;
    var ys = options.length;
    for (y <- 0 until options.length) {
      val s = options(y).length();
      if (s > xs) xs = s;
    }
    xs += 1;
    val xp = 40;
    val yp = 40;
    var x = -1
    while (x < xs + 1) {
      var y = -1
      while (y < ys + 1) {
        var xf = 1;
        var yf = 12;
        if (x < 0) xf-=1;
        if (y < 0) yf-=1;
        if (x >= xs) xf+=1;
        if (y >= ys) yf+=1;
        draw(Art.guys(xf)(yf), xp + x * 6, yp + y * 6);
                
        y += 1
      }
      x += 1
    }
    for (y <- 0 until options.length) {
      if (y == selected) {
        drawString("+", xp, yp + y * 6);
      }
      drawString(options(y), xp + 6, yp + y * 6);
    }
    spriteBatch.end();
  }

  override def tick(input : Input) {
    if (!input.oldButtons(Input.ESCAPE) && input.buttons(Input.ESCAPE)) {
      setScreen(parent);
      return;
    }
    if (input.buttons(Input.UP) && !input.oldButtons(Input.UP)) {
      selected-=1;
      if (selected < 0) selected += options.length;
    }
    if (input.buttons(Input.DOWN) && !input.oldButtons(Input.DOWN)) {
      selected+=1;
      if (selected >= options.length) selected -= options.length;
    }
    if (input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT)) {
      if (selected==0) {
        setScreen(parent);
      } else if (selected==1) {
        parent.level.player.die();
        setScreen(parent);
      } else if (selected==2) {
        setScreen(new TitleScreen());
      } else if (selected==3) {
        setScreen(new WinScreen());
      }
    }
    //        if (delay>0) delay--;
    //        if (delay==0 && input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT)) {
    //            setScreen(parent);
    //        }
  }
}
