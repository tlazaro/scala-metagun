package com.mojang.metagun.level

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Matrix4;
import com.mojang.metagun.Art;
import com.mojang.metagun.Sound;
import com.mojang.metagun.entity.Boss;
import com.mojang.metagun.entity.BossNeck;
import com.mojang.metagun.entity.BossPart;
import com.mojang.metagun.entity.Entity;
import com.mojang.metagun.entity.Explosion;
import com.mojang.metagun.entity.Gremlin;
import com.mojang.metagun.entity.Gunner;
import com.mojang.metagun.entity.Hat;
import com.mojang.metagun.entity.Jabberwocky;
import com.mojang.metagun.entity.Player;
import com.mojang.metagun.entity.Sign;
import com.mojang.metagun.screen.GameScreen;
import com.mojang.metagun.screen.Screen;

import scala.collection.JavaConversions._

object Level {
  val FRICTION = 0.99;
  val GRAVITY = 0.10;
}

class Level(val screen : GameScreen, val width : Int, val height : Int, xo : Int, yo : Int, var xSpawn : Int, var ySpawn : Int) {
  import Level._
  
  val entities = new ArrayList[Entity]()
  
  var walls : Array[Byte] = new Array[Byte](width * height)
  var entityMap : Array[ArrayList[Entity]] = new Array[ArrayList[Entity]](width*height)
  
  private val random = new Random(1000)
  private var respawnTime = 0
  private var time = 0

  // Constructor
  for (y <- 0 until height) {
    for (x <- 0 until width) {
      entityMap(x + y * width) = new ArrayList[Entity]();

// int col = pixels(x + y * w) & 0xffffff;
      val col = (Art.level.getPixel(x + xo * 31, y + yo * 23) & 0xffffff00) >>> 8;                            
      var wall : Byte = 0;

      if (col == 0xffffff)
        wall = 1;
      else if (col == 0xFF00FF)
        wall = 2;
      else if (col == 0xffff00)
        wall = 3;
      else if (col == 0xff0000)
        wall = 4;
      else if (col == 0xB7B7B7)
        wall = 5;
      else if (col == 0xFF5050)
        wall = 6;
      else if (col == 0xFF5051)
        wall = 7;
      else if (col == 0x383838)
        wall = 8;
      else if (col == 0xA3FFFF)
        wall = 9;
      else if (col == 0x83FFFF) {
        var prev : BossPart = new Boss(x * 10 - 2, y * 10 - 2);
        var timeOffs = random.nextInt(60);
        prev.asInstanceOf[Boss].time = timeOffs;
        add(prev);
        for (i <- 0 to 10) {
          val b = new BossNeck(x * 10 - 1, y * 10 - 1, prev);
          b.time = i * 10 + timeOffs;
          prev = b;
          add(prev);
        }
      } else if (col == 0x80FFFF) {
        val g = new Gremlin(0, x * 10 - 10, y * 10 - 20);
        g.jumpDelay = random.nextInt(50);
        add(g);
      } else if (col == 0x81FFFF) {
        val g = new Gremlin(1, x * 10 - 10, y * 10 - 20);
        g.jumpDelay = random.nextInt(50);
        add(g);
      } else if (col == 0x82FFFF) {
        val g = new Jabberwocky(x * 10 - 10, y * 10 - 10);
        g.slamTime = random.nextInt(30);
        add(g);
      } else if (col == 0xFFADF8) {
        add(new Hat(x * 10 + 1, y * 10 + 5, xo * 31 + x, yo * 23 + y));
      } else if ((col & 0x00ffff) == 0x00ff00 && (col & 0xff0000) > 0) {
        add(new Sign(x * 10, y * 10, (col >> 16) & 0xff));
      } else if (col == 0x0000ff) {
        // if (xSpawn == 0 && ySpawn == 0) {
        this.xSpawn = x * 10 + 1;
        this.ySpawn = y * 10 - 8;
        // }
      } else if (col == 0x00FFFF) {
        val e = new Gunner(x * 10 + 2, y * 10 + 10 - 6, 0, 0);
        e.chargeTime = random.nextInt(Gunner.CHARGE_DURATION / 2);
        e.xa = 0
        e.ya = 0

        add(e);
      }
      walls(x + y * width) = wall;
    }                       
  }       
  var player = new Player(this.xSpawn, this.ySpawn);
  add(player);

  def add (e : Entity) {
    entities.add(e);
    e.init(this);

    e.xSlot = ((e.x + e.w / 2.0) / 10).toInt
    e.ySlot = ((e.y + e.h / 2.0) / 10).toInt
    if (e.xSlot >= 0 && e.ySlot >= 0 && e.xSlot < width && e.ySlot < height) {
      entityMap(e.xSlot + e.ySlot * width).add(e);
    }
  }

  def tick () {
    time += 1
    
    if (player.removed) {
      respawnTime+=1;
      if (respawnTime == 20) {
        screen.mayRespawn = true;
      }
    }
    
    var i = 0
    while (i < entities.size()) {
      val e = entities.get(i);
      var xSlotOld = e.xSlot;
      var ySlotOld = e.ySlot;
      if (!e.removed) e.tick();
      e.xSlot = ((e.x + e.w / 2.0) / 10).toInt
      e.ySlot = ((e.y + e.h / 2.0) / 10).toInt
      if (e.removed) {
        if (xSlotOld >= 0 && ySlotOld >= 0 && xSlotOld < width && ySlotOld < height) {
          entityMap(xSlotOld + ySlotOld * width).remove(e);
        }
        entities.remove(i);
        i-=1
      } else {
        if (e.xSlot != xSlotOld || e.ySlot != ySlotOld) {
          if (xSlotOld >= 0 && ySlotOld >= 0 && xSlotOld < width && ySlotOld < height) {
            entityMap(xSlotOld + ySlotOld * width).remove(e);
          }
          if (e.xSlot >= 0 && e.ySlot >= 0 && e.xSlot < width && e.ySlot < height) {
            entityMap(e.xSlot + e.ySlot * width).add(e);
          } else {
            e.outOfBounds();
          }

        }
      }
      
      i += 1
    }
  }

  private val hits = new ArrayList[Entity]();
  def getEntities (xc : Double, yc : Double, w : Double, h : Double) = {
    hits.clear();
    var r = 20;
    var x0 = ((xc - r) / 10).toInt
    var y0 = ((yc - r) / 10).toInt
    var x1 = ((xc + w + r) / 10).toInt
    var y1 = ((yc + h + r) / 10).toInt
    
    var x = x0
    while (x <= x1) {
      var y = y0
      while (y <= y1) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
          val es = entityMap(x + y * width)
          for (e <- es) {
            val xx0 = e.x
            val yy0 = e.y
            val xx1 = e.x + e.w
            val yy1 = e.y + e.h
            if (!(xx0 > xc + w || yy0 > yc + h || xx1 < xc || yy1 < yc)) {
              hits.add(e)
            }
          }
        }
        y += 1
      }
      x += 1
    }
    
    hits
  }

  var matrix = new Matrix4();

  def render (screen : Screen, camera : Camera) {
    matrix.idt();
    matrix.setToTranslation(camera.x, camera.y, 0)
    screen.spriteBatch.setTransformMatrix(matrix)
    screen.spriteBatch.begin();
    //g.translate(-camera.x, -camera.y);

    var xo = 0;
    var yo = 0;
    
    var x = xo
    while (x <= xo + (camera.width / 10)) {
      var y = yo
      while (y <= yo + (camera.height / 10)) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
          var ximg = 0;
          var yimg = 0;
          var w : Byte = walls(x + y * width);
          if (w == 0) yimg = 1;
          if (w == 1) ximg = 0;
          if (w == 2) ximg = 2;
          if (w == 3) ximg = 1;
          if (w == 9) ximg = 7;
          if (w == 8) {
            ximg = 4;
            yimg = 1;
          }
          if (w == 5) {
            ximg = 1;
            yimg = 1;
          }
          if (w == 6) {
            ximg = (time / 4 + x * 2) & 3;
            yimg = 2;
          }
          if (w == 7) {
            ximg = (-time / 4 + x * 2) & 3;
            yimg = 3;
          }
          if (w == 4) {
            if (walls(x + (y - 1) * width) == 1) {
              yimg += 1;
            }
            ximg = 3;
          }

          if(w != 0) {
            screen.draw(Art.walls(ximg)(yimg), x * 10, y * 10);
          }
        }
        y += 1
      }
      x += 1
    }
    
    var i = entities.size -1
    while (i >= 0) {
      entities.get(i).render(screen, camera)
      i -= 1
    }
                
    screen.spriteBatch.end();
  }

  def isFree (ee : Entity, xc : Double, yc : Double, w : Int, h : Int, xa : Double, ya : Double) : Boolean = {
    if (ee.interactsWithWorld) {
      return isBulletFree(ee, xc, yc, w, h);
    }
    val e = 0.1;
    var x0 = (xc / 10).toInt
    var y0 = (yc / 10).toInt
    var x1 = ((xc + w - e) / 10).toInt
    var y1 = ((yc + h - e) / 10).toInt
    var ok = true;
    
    var x = x0
    while (x <= x1) {
      var y = y0
      while (y <= y1) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
          var ww = walls(x + y * width);
          if (ww != 0) ok = false;
          if (ww == 8) ok = true;
          if (ww == 4 && ya != 0) ee.hitSpikes();
          if (ww == 6) {
            ee.xa += 0.12;
          }
          if (ww == 7) {
            ee.xa -= 0.12;
          }
        }
        
        y += 1
      }
      x += 1
    }

    return ok;
  }

  def isBulletFree (bullet : Entity, xc : Double, yc : Double, w : Int, h : Int) : Boolean = {
    val e = 0.1;
    var x0 = (xc / 10).toInt
    var y0 = (yc / 10).toInt
    var x1 = ((xc + w - e) / 10).toInt
    var y1 = ((yc + h - e) / 10).toInt
    var ok = true;
    
    var x = x0
    while (x <= x1) {
      var y = y0
      while (y <= y1) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
          val ww = walls(x + y * width);
          if (ww != 0) ok = false;
          if (ww == 5) ok = true;
          if (ww == 2) {
            var xPush = 0;
            var yPush = 0;

            if (Math.abs(bullet.xa) > Math.abs(bullet.ya)) {
              if (bullet.xa < 0) xPush = -1;
              if (bullet.xa > 0) xPush = 1;
            } else {
              if (bullet.ya < 0) yPush = -1;
              if (bullet.ya > 0) yPush = 1;
            }
            val r = 0.5;
            if (walls((x + xPush) + (y + yPush) * width) == 0
                && getEntities((x + xPush) * 10 + r, (y + yPush) * 10 + r, 10 - r * 2, 10 - r * 2).size() == 0) {
              walls(x + y * width) = 0;
              walls((x + xPush) + (y + yPush) * width) = 2;
            }
            bullet.remove();
          }
          if (ww == 3) {
            Sound.boom.play();
            for (i <- 0 until 16) {
              val dir = i * Math.Pi * 2 / 8.0;
              val xa = Math.sin(dir);
              val ya = Math.cos(dir);
              val dist = (i / 8) + 1;
              add(new Explosion(1, i * 3, x * 10 + 5 + xa * dist, y * 10 + 5 + ya * dist, xa, ya));
            }
            bullet.remove();
            walls(x + y * width) = 0;
          }
          if (ww == 9) {
            if ((bullet.isInstanceOf[Explosion]) && (bullet.asInstanceOf[Explosion]).power > 0) {
              Sound.boom.play();
              for (i <- 0 until 16) {
                val dir = i * Math.Pi * 2 / 8.0;
                val xa = Math.sin(dir);
                val ya = Math.cos(dir);
                val dist = (i / 8) + 1;
                add(new Explosion(1, i * 3, x * 10 + 5 + xa * dist, y * 10 + 5 + ya * dist, xa, ya));
              }
              bullet.remove();
              walls(x + y * width) = 0;
            }
          }
        }
        y += 1
      }
      x += 1
    }

    return ok;
  }

  def readSign (sign : Sign) {
    screen.readSign(sign.id - 1);
  }

  def transition (x : Int, y : Int) {
    screen.transition(x, y);
  }

  def getGun (level : Int) {
    screen.getGun(level);
  }
}

