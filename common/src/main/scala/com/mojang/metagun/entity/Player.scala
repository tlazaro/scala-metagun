package com.mojang.metagun.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mojang.metagun.Art;
import com.mojang.metagun.Input;
import com.mojang.metagun.Sound;
import com.mojang.metagun.Stats;
import com.mojang.metagun.level.Camera;
import com.mojang.metagun.level.Level;
import com.mojang.metagun.screen.Screen;

class Player(_x : Int, _y : Int) extends Entity(_x, _y) {
  import Entity._
  
  w = 8;
  h = 18;
  bounce = 0;

  var hatCount = 1;
  var damage = 0;
  var gunLevel = 0;
  var readSign = false;
    
  private var dir = 1;
  private var yAim = 0;
  private var frame = 0;
  private var noHurtTime = 0;
  private var shootTime = 0;

  override def tick() {
  }

  def render(g : Screen, camera : Camera) {
    //        g.setColor(Color.GREEN);
    var xp = x.toInt - (16 - w) / 2;
    var yp = y.toInt - 2;
    //      g.fillRect(xp, yp, w, h);

    var stepFrame = (frame / 4) % 4;

    val sheet = if (dir == 1) Art.player1 else Art.player2;        
    if (!onGround) {
      val yya = Math.round(-ya).toInt;
      stepFrame = 4;
      if (yya < -1) stepFrame = 5;
      yp += yya;
    }
    val idx = if (hatCount > 0) 0 else 1
    g.draw(sheet(3 + stepFrame)(idx), xp, yp);


    yp += (if (stepFrame == 3) 1 else 0);
    for (i <- 1 until hatCount) {
      g.draw(sheet(0)(1), xp, yp - i * 2);
    }

    if (gunLevel > 0) {
      if (!onGround) {
        var yya = Math.round(-ya).toInt
        if (yya < -1) yya = -1;
        if (yya > 1) yya = 1;
        yp += yya;
      }
      g.draw(sheet(1 + yAim)((gunLevel - 1) * 2), xp, yp);
    }
  }

  def tick(input : Input) {
    readSign = true; //onGround && input.buttons(Input.UP) && !input.oldButtons(Input.UP);
    if (noHurtTime > 0) noHurtTime-=1;
    val speed = 0.4;
    var aimAngle = -0.2;
    yAim = 0;
    if (input.buttons(Input.UP)) {
      aimAngle -= 0.8;
      yAim-=1;
    }
    if (input.buttons(Input.DOWN)) {
      aimAngle += 0.8;
      yAim+=1;
    }
    var walk = false;
    if (input.buttons(Input.LEFT)) {
      walk = true;
      xa -= speed;
      dir = -1;
    }
    if (input.buttons(Input.RIGHT)) {
      walk = true;
      xa += speed;
      dir = 1;
    }
    if (walk) frame+=1;
    else frame = 0;
    if (input.buttons(Input.JUMP) && !input.oldButtons(Input.JUMP) && onGround) {
      Sound.jump.play();
      ya -= 2 + Math.abs(xa) * 0.5;
    }

    tryMove(xa, ya);

    xa *= 0.7;
    if (ya < 0 && input.buttons(Input.JUMP)) {
      ya *= 0.992;
      ya += Level.GRAVITY * 0.5;
    } else {
      ya *= Level.FRICTION;
      ya += Level.GRAVITY;
    }

    var shooting = false;
    if (gunLevel > 0 && input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT)) shooting = true;
    if (gunLevel > 1 && input.buttons(Input.SHOOT) && (!input.oldButtons(Input.SHOOT) || shootTime>0)) {
      shootTime +=1
      shooting = shootTime % 3 == 0;
    } else {
      shootTime = 0;
    }
    if (shooting) {
      val pow = 3;
      Sound.launch.play();

      var xx = x + w / 2.0 - 2.5 + dir * 7
      val yy = y + (h / 2.0 - 2.5) + (this.yAim * 2)
      for (i <- 0 until 4) {
        val xAim = Math.cos(aimAngle + 0.2) * dir * pow;
        val yAim = Math.sin(aimAngle + 0.2) * pow;
        val xxa = xa + xAim * 0.2;
        val yya = ya + yAim * 0.2;
        level.add(new Spark(xx, yy + (-2 + i) * 0.5, xxa, yya));
      }
      val xAim = Math.cos(aimAngle) * dir * pow;
      val yAim = Math.sin(aimAngle) * pow;
      val xxa = xa + xAim;
      val yya = ya + yAim;
      if (gunLevel==2) {
        xa-=xAim*0.1;
        ya-=yAim*0.1;
      }
      xx = x + w / 2.0 - 2.5;
      Stats.instance.shots+=1;
      level.add(new Gunner(xx, yy, xxa, yya));
    }

    if (y < 5) level.transition(0, -1);
    if (y > 240 - w + 10 - 5) level.transition(0, 1);
    if (x < 0 + 5) level.transition(-1, 0);
    if (x > 320 - h + 10 - 5) level.transition(1, 0);
  }

  override def hitSpikes() {
    die();
  }

  def die() {
    if (removed) return; 
    if (hatCount > 0) {
      level.add(new Hat(x, y - hatCount * 2));
      hatCount-=1;
    }
    Sound.death.play();
    for (i <- 0 until 16) {
      level.add(new PlayerGore(x + random.nextDouble() * w, y + random.nextDouble() * h));
    }
    Stats.instance.deaths += 1;
    remove();
  }

  override def shot(bullet : Bullet) : Boolean = {
    Sound.pew.play();
    xa += bullet.xa * 0.5;
    ya += bullet.ya * 0.5;
    for (i <- 0 until 4) {
      val xd = (random.nextDouble() - random.nextDouble()) * 4 - bullet.xa * 3;
      val yd = (random.nextDouble() - random.nextDouble()) * 4 - bullet.ya * 3;
      level.add(new Gore(bullet.x, bullet.y, xa + xd, ya + yd));
    }
    if (noHurtTime != 0) return true;

    if (hatCount > 0) {
      while (hatCount>0) {
        val hat = new Hat(x, y);
        hat.ya-=hatCount*0.05;
        hat.xxa=(random.nextFloat()-random.nextFloat())*(hatCount-1)*0.5;
        hat.time+=hatCount*6; 
        level.add(hat);
        hat.tryMove(0, -hatCount*2);
        hatCount-=1;
      }
      noHurtTime = 20;
    } else {
      Sound.oof.play();
      damage+=1;
      noHurtTime = 20;
      if (damage == 4) {
        Sound.death.play();
        for (i <- 0 until 16) {
          level.add(new PlayerGore(x + random.nextDouble() * w, y + random.nextDouble() * h));
        }
        remove();
      } else {
        level.add(new PlayerGore(bullet.x, bullet.y));
      }
    }

    return true;
  }

  def readSign(sign : Sign) {
    if (sign.autoRead || readSign) {
      sign.autoRead = false;
      if (sign.id == 6) {
        sign.remove();
        gunLevel = 1;
        level.getGun(1);
      } else if (sign.id == 15) {
        sign.remove();
        gunLevel = 2;
        level.getGun(2);
      }
      level.readSign(sign);
    }
    sign.remove();
  }

  override def outOfBounds() {
  }

  override def explode(explosion : Explosion) {
    die();
  }

  override def collideMonster(e : Entity) {
    die();
  }

}
