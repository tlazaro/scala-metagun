package com.mojang.metagun.screen;

import com.mojang.metagun.Art;
import com.mojang.metagun.Input;
import com.mojang.metagun.Stats;

class WinScreen extends Screen {
  private var time = 0;
    
  def render() {
    spriteBatch.begin();
    val w = 240 * 8; //Art.bg.getHeight();
    draw(Art.bg, 0, -(time*2%w));
    draw(Art.bg, 0, -(time*2%w)+w);

    var offs0 = 500-time*10;
    if (offs0 < 0) offs0=0;
    var offs1 = 1200-time*16;
    if (offs1 < 0) offs1=0;
    var yOffs = 600-time*5;
    if (yOffs < -120) yOffs = -120;
    if (yOffs > 0) yOffs = 0;
    draw(Art.winScreen1, offs0, yOffs+30);
    draw(Art.winScreen2, -offs1, yOffs*2/3+30);
        
    val tt = time-(60*2+30);
    val yo = 130;
    val xo = 120-8*3;
    if (tt>=0) {
      drawString("       TIME: "+Stats.instance.getTimeString(), xo, yo+0*6);
      drawString("     DEATHS: "+Stats.instance.deaths, xo, yo+1*6);
      drawString("    FEDORAS: "+Stats.instance.hats+"/"+7, xo, yo+2*6);
      drawString("SHOTS FIRED: "+Stats.instance.shots, xo, yo+3*6);
      drawString("FINAL SCORE: "+timeScale(Stats.instance.getFinalScore(), tt-30*5), xo, yo+5*6);

      drawString(timeHideScale(Stats.instance.getSpeedScore(), tt-30*1), xo+20*6, yo+0*6);
      drawString(timeHideScale(Stats.instance.getDeathScore(), tt-30*2), xo+20*6, yo+1*6);
      drawString(timeHideScale(Stats.instance.getHatScore(), tt-30*3), xo+20*6, yo+2*6);
      drawString(timeHideScale(Stats.instance.getShotScore(), tt-30*4), xo+20*6, yo+3*6);
    }
        
    if (time > 60*7 && (time / 30 % 2 == 0)) {
      val msg = "PRESS X TO RESET THE GAME"; 
      drawString(msg, 160-msg.length()*3, yo+10*6);
    }
    spriteBatch.end();
  }
    
  private def timeHideScale(_val : Int, time : Int) : String = {
    if (time<10) return "";
    "+"+_val*clamp(time,0,60)/60;
  }
  
  private def clamp(value : Int, min : Int, max : Int) : Int = {
    if (value <= min) min
    else if (value >= max) max
    else value
  }

  private def timeScale(_val : Int, time : Int) : String = {
    "" + _val * clamp(time,0,60) / 60;
  }
    
  override def tick(input : Input) {
    time+=1;
    if (time>60*7) {
      if (input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT)) {
        setScreen(new TitleScreen());
      }
    }
  }
}
