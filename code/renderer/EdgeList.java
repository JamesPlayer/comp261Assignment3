package renderer;

import java.util.HashMap;
import java.util.Map;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {
	
	public int startY;
	public int endY;
	public Map<Integer, Float[]> rows = new HashMap<Integer, Float[]>();
	
	public EdgeList(int startY, int endY) {
		this.startY = startY;
		this.endY = endY;
	}

	public int getStartY() {
		return startY;
	}

	public int getEndY() {
		return endY;
	}

	public float getLeftX(int y) {
		return rows.get(y)[0];
	}

	public float getRightX(int y) {
		return rows.get(y)[1];
	}

	public float getLeftZ(int y) {
		return rows.get(y)[2];
	}

	public float getRightZ(int y) {
		return rows.get(y)[3];
	}
	
	public void addRow(int y, float xLeft, float xRight, float zLeft, float zRight) {
		rows.put(y, new Float[]{xLeft, xRight, zLeft, zRight});
	}
}

// code for comp261 assignments
