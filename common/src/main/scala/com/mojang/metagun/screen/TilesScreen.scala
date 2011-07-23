/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mojang.metagun.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

class TilesScreen extends Screen {
  val path = "res/";
  val mapname = "level0";
  
  val blockWidth = 10;
  val blockHeight = 10;
  
  val mapHandle = Gdx.files.internal(path + mapname + ".tmx");
  val baseDir = Gdx.files.internal(path);

  lazy val map = TiledLoader.createMap(mapHandle);
  
  lazy val atlas = new TileAtlas(map, baseDir);
  lazy val tileMapRenderer = new TileMapRenderer(map, atlas, blockWidth, blockHeight, 5.0f, 5.0f);
  
  
  lazy val aspectRatio = Gdx.graphics.getWidth().toFloat / Gdx.graphics.getHeight().toFloat
	lazy val	cam = new OrthographicCamera(100f * aspectRatio, 100f);
  cam.position.set(tileMapRenderer.getMapWidthUnits()/2, tileMapRenderer.getMapHeightUnits() / 2, 0);
  
  var startTime = System.nanoTime();
	lazy val tmp = new Vector3();
  
  def render () {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
    tileMapRenderer.render();
    
    spriteBatch.begin();
		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		font.draw(spriteBatch, "InitialCol, LastCol: " + tileMapRenderer.getInitialCol() + "," + tileMapRenderer.getLastCol(), 20,
              40);
		font.draw(spriteBatch, "InitialRow, LastRow: " + tileMapRenderer.getInitialRow() + "," + tileMapRenderer.getLastRow(), 20,
              60);
		
		tmp.set(0, 0, 0);
		cam.unproject(tmp);
		font.draw(spriteBatch, "Location: " + tmp.x + "," + tmp.y, 20, 80);
		spriteBatch.end();
  }
  
  val font = new BitmapFont();
  font.setColor(Color.RED);
  
  val camDirection = new Vector3(1, 1, 0);
	val maxCamPosition = new Vector2(0, 0);
  
  private def updateCameraPosition () {
		cam.position.add(camDirection.tmp().mul(Gdx.graphics.getDeltaTime()).mul(5*tileMapRenderer.getUnitsPerTileX()));

		if (cam.position.x < 0) {
			cam.position.x = 0;
			camDirection.x = 1;
		}
		if (cam.position.x > maxCamPosition.x) {
			cam.position.x = maxCamPosition.x;
			camDirection.x = -1;
		}
		if (cam.position.y < 0) {
			cam.position.y = 0;
			camDirection.y = 1;
		}
		if (cam.position.y > maxCamPosition.y) {
			cam.position.y = maxCamPosition.y;
			camDirection.y = -1;
		}
	}
}
