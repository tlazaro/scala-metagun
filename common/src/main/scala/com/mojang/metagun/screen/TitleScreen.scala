package com.mojang.metagun.screen;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.mojang.metagun.Art;
import com.mojang.metagun.Input;
import com.mojang.metagun.Sound;

class TitleScreen extends Screen {
  private var time = 0;    
    
  def render() {
    var yOffs = 480 - time * 2;
    if (yOffs < 0) yOffs = 0;
    spriteBatch.begin();
    draw(Art.bg, 0, 0);
    draw(Art.titleScreen, 0, -yOffs);        
    if (time > 240) {
      val msg = if(Gdx.app.getType() == ApplicationType.Android)
        "TOUCH TO START";
      else
        "PRESS X TO START";
      drawString(msg, 160 - msg.length() * 3, 140 - 3 - (Math.abs(Math.sin(time * 0.1) * 10)).toInt);

    }
    if (time >=0) {
      val msg = "COPYRIGHT MOJANG 2010";
      drawString(msg, 2, 240-6-2);
    }
    spriteBatch.end();
  }

  override def tick(input : Input) {
    time+=1;
    if (time > 240) {
      if (input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT) || Gdx.input.isTouched()) {
        Sound.startgame.play();
        setScreen(new GameScreen());
        input.releaseAllKeys();
      }
    }
    if (time > 60*10) {
      setScreen(new ExpositionScreen());
    }
  }
}
