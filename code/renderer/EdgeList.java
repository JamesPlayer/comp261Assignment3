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
	public Map<Integer, EdgeListRow> rows = new HashMap<Integer, EdgeListRow>();
	
	public EdgeList(int startY, int endY) {
		this.startY = startY;
		this.endY = endY;
		
		// Initilialise rows with empty EdgeListRows
		for (int y = startY; y <= endY; y++) {
			rows.put(y, new EdgeListRow());
		}
	}

	public int getStartY() {
		return startY;
	}

	public int getEndY() {
		return endY;
	}

	public float getLeftX(int y) {
		return rows.get(y).leftX;
	}

	public float getRightX(int y) {
		return rows.get(y).rightX;
	}

	public float getLeftZ(int y) {
		return rows.get(y).leftZ;
	}

	public float getRightZ(int y) {
		return rows.get(y).rightZ;
	}
	
}

// code for comp261 assignments
