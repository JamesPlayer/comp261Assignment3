package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;

import renderer.Scene.Polygon;

public class Renderer extends GUI {
	
	protected Scene scene = null;
	
	protected Color ambientLight = new Color(128, 128, 128);
	
	protected Color bottomLeftLight = new Color(0, 0, 0);
	
	protected Color bottomRightLight = new Color(0, 0, 0);
	
	protected float rotationX = 0;
	
	protected float rotationY = 0;
	
	protected boolean scaled = false;
	
	
	
	@Override
	protected void onLoad(File file) {
		
		rotationX = 0;
		rotationY = 0;
		scaled = false;

		/*
		 * This method should parse the given file into a Scene object, which
		 * you store and use to render an image.
		 */
		try {
			scene = loadScene(file);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	protected Scene loadScene(File file) throws FileNotFoundException, IOException {
		BufferedReader data = null;
		String lightStr = null;
		String[] values = null;
		Vector3D light = null;
		String line = null;
		List<Polygon> polygons = new ArrayList<Polygon>();
		float[] polygonPoints = new float[9];
		int[] polygonColors = new int[3];
		
		data = new BufferedReader(new FileReader(file));
		lightStr = data.readLine();
		values = lightStr.split(" ");
		light = new Vector3D(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
		
		while ((line = data.readLine()) != null) {
			values = line.split(" ");
			
			int i;
			for (i=0; i<9; i++) {
				polygonPoints[i] = Float.parseFloat(values[i]);
			}
			
			for (i=0; i<3; i++) {
				polygonColors[i] = Integer.parseInt(values[i+9]);
			}
			
			polygons.add(new Polygon(polygonPoints, polygonColors));
		}
		
		return new Scene(polygons, light);
	}
	
	@Override
	protected void onKeyPress(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_LEFT
				|| Character.toUpperCase(ev.getKeyChar()) == 'A') {			
			rotationY = 0.1f; 
			rotationX = 0;
		}
		else if (ev.getKeyCode() == KeyEvent.VK_RIGHT
				|| Character.toUpperCase(ev.getKeyChar()) == 'D') {			
			rotationY = -0.1f;
			rotationX = 0;
		}
		else if (ev.getKeyCode() == KeyEvent.VK_UP
				|| Character.toUpperCase(ev.getKeyChar()) == 'W') {			
			rotationY = 0;
			rotationX = -0.1f;
		}
		else if (ev.getKeyCode() == KeyEvent.VK_DOWN
				|| Character.toUpperCase(ev.getKeyChar()) == 'S') {			
			rotationY = 0;
			rotationX = 0.1f;
		}
	}
	
	/**
	 * Triggered by moving a slider in the GUI
	 */
	protected void onLightChange(ChangeEvent e) {
		int[] ambientColor = getAmbientLight();
		int[] bottomLeftColor = getBottomLeftLight();
		int[] bottomRightColor = getBottomRightLight();
		
		ambientLight = new Color(ambientColor[0], ambientColor[1], ambientColor[2]);
		bottomLeftLight = new Color(bottomLeftColor[0], bottomLeftColor[1], bottomLeftColor[2]);
		bottomRightLight = new Color(bottomRightColor[0], bottomRightColor[1], bottomRightColor[2]);
		
		rotationY = 0;
		rotationX = 0;
	}

	@Override
	protected BufferedImage render() {
		
		if (scene == null) return null;

		/*
		 * This method should put together the pieces of your renderer, as
		 * described in the lecture. This will involve calling each of the
		 * static method stubs in the Pipeline class, which you also need to
		 * fill in.
		 */
		
		if (!scaled) {			
			scene = Pipeline.scaleScene(scene);
			scaled = true;
		}
		scene = Pipeline.rotateScene(scene, rotationX, rotationY);
		scene = Pipeline.translateScene(scene);
		
		
		Color[][] zbuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
		float[][] zdepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
		
		// Initialize all pixels to be ambient color
		// Initialize z-depth to be as large as possible
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				zbuffer[x][y] = ambientLight;
				zdepth[x][y] = Integer.MAX_VALUE;
			}
		}
	
		for (Polygon poly : scene.getPolygons()) {
			if (poly.isHidden || Pipeline.isHidden(poly)) {
				poly.isHidden = true;
				continue;
			}
			
			Color polyColor = Pipeline.getShading(poly, scene.getLight(), Color.WHITE, ambientLight, bottomLeftLight, bottomRightLight);
			EdgeList edgeList = Pipeline.computeEdgeList(poly);
			Pipeline.computeZBuffer(zbuffer, zdepth, edgeList, polyColor);
		}
		
		return convertBitmapToImage(zbuffer);
		
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}

// code for comp261 assignments
