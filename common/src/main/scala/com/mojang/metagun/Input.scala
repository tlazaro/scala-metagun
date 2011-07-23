package com.mojang.metagun

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

object Input {
  val UP = 0;
  val DOWN = 1;
  val LEFT = 2;
  val RIGHT = 3;

  val JUMP = 4;
  val SHOOT = 5;

  val ESCAPE = 6;
  
}

class Input extends InputProcessor {
  import Input._
    
  val buttons = new Array[Boolean](64);
  val oldButtons = new Array[Boolean](64);

  def set(key : Int, down : Boolean) {
    var button = -1;

    if (key == Keys.DPAD_UP) button = UP;
    if (key == Keys.DPAD_LEFT) button = LEFT;
    if (key == Keys.DPAD_DOWN) button = DOWN;
    if (key == Keys.DPAD_RIGHT) button = RIGHT;

    if (key == Keys.Y) button = JUMP;
    if (key == Keys.Z) button = JUMP;
    if (key == Keys.X) button = SHOOT;
    if (key == Keys.C) button = JUMP;
    if (key == Keys.A) button = JUMP;
    if (key == Keys.S) button = SHOOT;
    if (key == Keys.D) button = JUMP;

    if (key == Keys.ESCAPE) button = ESCAPE;

    if (button >= 0) {
      buttons(button) = down;
    }
  }

  def tick() {
    buttons.copyToArray(oldButtons)
         
    if(Gdx.app.getType() == ApplicationType.Android) {      
      var left = false;
      var right = false;
      var z = false;
      var s = false;
                 
      for(i <- 0 until 2) {
        val x = ((Gdx.input.getX(i) / Gdx.graphics.getWidth().toFloat) * 320).toInt;
        if(Gdx.input.isTouched(i)) {
          if(x < 32) {
            set(Keys.DPAD_LEFT, true);
            left |= true;
          }
          if(x > 32 && x < 90) {
            set(Keys.DPAD_RIGHT, true);
            right |= true;
          }
          if(x > 320-64 && x < 320-32) {
            set(Keys.Z, true);
            z |= true;                              
          }
          if(x > 320-32 && x < 320) {
            set(Keys.X, true);
            s |= true;
          }
        }
      }       
         
      if(left==false) set(Keys.DPAD_LEFT, false);
      if(right==false) set(Keys.DPAD_RIGHT, false);
      if(z==false) set(Keys.Z, false);
      if(s==false) set(Keys.X, false);
    }                                                        
  }


  def releaseAllKeys() {
    for (i <- 0 until buttons.length) {
      buttons(i) = false;
    }
  }

  override def keyDown (keycode : Int) : Boolean = {
    set(keycode, true);
    false
  }

  override def keyUp (keycode : Int) : Boolean = {
    set(keycode, false);
    false
  }

  override def keyTyped (character : Char) = false
  
  override def touchDown (x : Int, y : Int, pointer : Int, button : Int) = false
  override def touchUp (x : Int, y : Int, pointer : Int, button : Int) : Boolean = {

    if(x > 160 - 32 && x < 160) 
      set(Keys.DPAD_UP, !buttons(UP))
    if(x > 160 && x < 160 + 32) 
      set(Keys.DPAD_DOWN, !buttons(DOWN))
    
    false
  }

  override def touchDragged (x : Int, y : Int, pointer : Int) : Boolean = {
    false;
  }

  override def touchMoved (x : Int, y : Int) : Boolean = false

  override def scrolled (amount : Int) : Boolean = false
}

