package renderer;

import java.awt.Color;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		return getNormal(poly).z > 0;
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
		int r, g, b;
		float normalizedRed, normalizedGreen, normalizedBlue;
		Vector3D unitNormal = getNormal(poly).unitVector();
		float cosTheta = unitNormal.cosTheta(lightDirection);
		float multiplier = 1 / (float) 255;
		
		// Ignore lightColor if lightDirection is coming from the back
		if (cosTheta < 0) {
			lightColor = new Color(0, 0, 0);
		}
		
		normalizedRed = ((multiplier*ambientLight.getRed() + multiplier*lightColor.getRed() * cosTheta) * multiplier*poly.getReflectance().getRed());
		r = (int) (normalizedRed * 255);
		
		normalizedGreen = ((multiplier*ambientLight.getGreen() + multiplier*lightColor.getGreen() * cosTheta) * multiplier*poly.getReflectance().getGreen());
		g = (int) (normalizedGreen * 255);
		
		normalizedBlue = ((multiplier*ambientLight.getBlue() + multiplier*lightColor.getBlue() * cosTheta) * multiplier*poly.getReflectance().getBlue());
		b = (int) (normalizedBlue * 255);
		
		return new Color(r, g, b);
	}
	
	public static Vector3D getNormal(Polygon poly) {
		Vector3D edge1 = poly.getVertices()[1].minus(poly.getVertices()[0]);
		Vector3D edge2 = poly.getVertices()[2].minus(poly.getVertices()[1]);
		return edge1.crossProduct(edge2);
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		// TODO fill this in.
		return null;
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene translateScene(Scene scene) {
		// TODO fill this in.
		return null;
	}

	/**
	 * This should scale the scene.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene) {
		// TODO fill this in.
		return null;
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		
		int ymin = (int) (Math.min(poly.vertices[0].y, Math.min(poly.vertices[1].y, poly.vertices[2].y)));
		int ymax = (int) (Math.max(poly.vertices[0].y, Math.max(poly.vertices[1].y, poly.vertices[2].y)));
		EdgeList edgeList = new EdgeList(ymin, ymax);
		
		Vector3D[][] edges = new Vector3D[][]{
			new Vector3D[]{poly.vertices[0],poly.vertices[1]},
			new Vector3D[]{poly.vertices[1],poly.vertices[2]},
			new Vector3D[]{poly.vertices[2],poly.vertices[0]}
		};
		
		setXCoords(edgeList, edges);
		setZCoords(edgeList, edges);
		
		return edgeList;
	}
	
	protected static void setXCoords(EdgeList edgeList, Vector3D[][] edges) {
		
		float x, slope;
		int y;
		
		for (Vector3D[] edge : edges) {
			slope = (edge[1].x - edge[0].x) / (float) (edge[1].y - edge[0].y);
			x = edge[0].x;
			y = (int) (edge[0].y);
			
			// Going down
			if (edge[0].y < edge[1].y) {
				while (y <= Math.round(edge[1].y)) {
					edgeList.rows.get(y).leftX = x;
					x = x + slope;
					y++;
				}
			} else {
				while (y >= Math.round(edge[1].y)) {
					edgeList.rows.get(y).rightX = x;
					x = x - slope;
					y--;
				}
			}
		}	
	}
	
	protected static void setZCoords(EdgeList edgeList, Vector3D[][] edges) {
		
		float z, slope;
		int y;
		
		for (Vector3D[] edge : edges) {
			slope = (edge[1].z - edge[0].z) / (float) (edge[1].y - edge[0].y);
			z = edge[0].z;
			y = (int) (edge[0].y);
			
			// Going down
			if (edge[0].y < edge[1].y) {
				while (y <= Math.round(edge[1].y)) {
					edgeList.rows.get(y).leftZ = z;
					z = z + slope;
					y++;
				}
			} else {
				while (y >= Math.round(edge[1].y)) {
					edgeList.rows.get(y).rightZ = z;
					z = z - slope;
					y--;
				}
			}
		}	
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
		// TODO fill this in.
	}
}

// code for comp261 assignments
