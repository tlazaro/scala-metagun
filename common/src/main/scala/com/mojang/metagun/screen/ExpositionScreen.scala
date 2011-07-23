package com.mojang.metagun.screen

import com.mojang.metagun.Art
import com.mojang.metagun.Input

import com.badlogic.gdx.Gdx;

class ExpositionScreen extends Screen {
  private var time = 0;

  //    "1234567890123456789012345678901234567890"
  private lazy val lines = scala.io.Source.fromInputStream(Gdx.files.internal("res/exposition.txt").read()).getLines.toList;

  def render() {
    val w = -Art.bg.getRegionHeight();
    spriteBatch.begin();
    draw(Art.bg, 0, -(time / 8 % w));
    draw(Art.bg, 0, -(time / 8 % w) + w);        

    val yo = time / 4;
    for (y <- 0 to (240 / 6)) {
      val yl = yo / 6 - 240 / 6+y
      if (yl >= 0 && yl < lines.size) {
        drawString(lines(yl), (320 - 40 * 6)/2, y * 6 - yo % 6);
      }
    }
    spriteBatch.end();
  }

  override def tick(input : Input) {
    time += 1
    if (time / 4 > lines.size * 6 + 250) {
      setScreen(new TitleScreen());
    }
    if (input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT) || Gdx.input.isTouched()) {
      setScreen(new TitleScreen());
    }
    if (input.buttons(Input.ESCAPE) && !input.oldButtons(Input.ESCAPE)) {
      setScreen(new TitleScreen());
    }
  }
}

