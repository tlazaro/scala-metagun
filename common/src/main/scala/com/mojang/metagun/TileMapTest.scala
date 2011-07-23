package com.mojang.metagun

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas
import com.badlogic.gdx.graphics.g2d.tiled.SimpleTileAtlas
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import scala.collection.JavaConversions._

class TiledMapTest extends ApplicationListener {

	val automove = false;

	lazy val spriteBatch  = new SpriteBatch();
	lazy val font = new BitmapFont();

	var cam : OrthographicCamera = _
	var camController : OrthoCamController = _
	val camDirection = new Vector3(1, 1, 0);
	val maxCamPosition = new Vector2(0, 0);

	var map : TiledMap = _
	var atlas : TileAtlas = _
	lazy val tileMapRenderer = new TileMapRenderer(map, atlas, 32, 32, 8, 8);

	val startTime = System.nanoTime();
	val tmp = new Vector3();
  
  def create () {
		font.setColor(Color.RED);

		val path = "res/";
//		val mapname = "level0";
		val mapname = "desert";

		val mapHandle = Gdx.files.internal(path + mapname + ".tmx");
		val baseDir = Gdx.files.internal(path);

		var startTime = System.currentTimeMillis();
		map = TiledLoader.createMap(mapHandle);
		var endTime = System.currentTimeMillis();
		System.out.println("Loaded map in " + (endTime - startTime) + "mS");

		atlas = new SimpleTileAtlas(map, baseDir)
//		atlas = new TileAtlas(map, baseDir)

		startTime = System.currentTimeMillis();
    tileMapRenderer
		endTime = System.currentTimeMillis();
		System.out.println("Created cache in " + (endTime - startTime) + "mS");

		for (group <- map.objectGroups) {
			for (obj <- group.objects) {
				// TODO: Draw sprites where objects occur
				System.out.println("Object " + obj.name + " x,y = " + obj.x + "," + obj.y + " width,height = " + obj.width + "," + obj.height);
			}
		}

		val aspectRatio = Gdx.graphics.getWidth().toFloat / Gdx.graphics.getHeight().toFloat;
		cam = new OrthographicCamera(100f * aspectRatio, 100f);
		
		cam.position.set(tileMapRenderer.getMapWidthUnits()/2, tileMapRenderer.getMapHeightUnits() / 2, 0);
		camController = new OrthoCamController(cam);
		Gdx.input.setInputProcessor(camController);

		maxCamPosition.set(tileMapRenderer.getMapWidthUnits(), tileMapRenderer.getMapHeightUnits());
	}

	def render () {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		if (automove) {
			updateCameraPosition();
		}
		
		cam.zoom = 0.9f;
		cam.update();
		tileMapRenderer.render(cam);

		spriteBatch.begin();
		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, 20);
		font.draw(spriteBatch, "InitialCol, LastCol: " + tileMapRenderer.getInitialCol() + "," + tileMapRenderer.getLastCol(), 20, 40);
		font.draw(spriteBatch, "InitialRow, LastRow: " + tileMapRenderer.getInitialRow() + "," + tileMapRenderer.getLastRow(), 20, 60);
		
		tmp.set(0, 0, 0);
		cam.unproject(tmp);
		font.draw(spriteBatch, "Location: " + tmp.x + "," + tmp.y, 20, 80);
		spriteBatch.end();
	}

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

	def resume( ) { };
	def resize(width : Int, height : Int) { };
	def pause( ) { };
	def dispose( ) { };
}

class OrthoCamController(val camera : OrthographicCamera) extends InputAdapter {
	val curr = new Vector3();
	val last = new Vector3(-1, -1, -1);	
	val delta = new Vector3();
	
	override def touchDragged (x : Int, y : Int, pointer : Int) : Boolean = {
		camera.unproject(curr.set(x, y,0));
		if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
			camera.unproject(delta.set(last.x, last.y, 0));			
			delta.sub(curr);
			camera.position.add(delta.x, delta.y, 0);
		}
		last.set(x, y, 0);
		return false;
	}
	
	override def touchUp(x : Int, y : Int, pointer : Int, button : Int) : Boolean = {
		last.set(-1, -1, -1);
		return false;
	}
}
