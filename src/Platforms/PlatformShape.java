package Platforms;

import java.awt.Color;
import java.io.File;
import java.util.LinkedList;

import LineEditor.LineEditor;

public class PlatformShape implements Cloneable {
	public static class pPoint implements Cloneable {
		public int		x, y;
		public boolean	begin;
		public Color	col;

		public enum Type {
			COLOR, POINT
		}

		public Type	type;

		public pPoint(int x, int y, boolean begin) {
			this.x = x;
			this.y = y;
			this.type = Type.POINT;
			this.begin = begin;
		}

		public pPoint(Color col) {
			this.col = col;
			this.x = this.y = -1;
			this.type = Type.COLOR;
			this.begin = false;
		}

		@Override
		public pPoint clone() {
			pPoint p = new pPoint(x, y, begin);
			p.type = type;
			p.col = col;
			//
			return p;
		}
	}

	private int					w, h;
	private LinkedList<pPoint>	points	= new LinkedList<pPoint>();
	private PlatformShape		source	= null;
	private String				label	= null;
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
	public PlatformShape() {
	}

	public PlatformShape(String path) {
		try {
			importFromFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public PlatformShape getSource() {
		return source;
	}

	public LinkedList<pPoint> getPoints() {
		return points;
	}

	@Override
	public PlatformShape clone() {
		PlatformShape shape = new PlatformShape();
		shape.w = w;
		shape.h = h;
		shape.source = this;
		for (pPoint p : points) {
			shape.points.add(p.clone());
		}
		shape.label = label;
		//
		return shape;
	}

	public void fitTo(float _w, float _h) {
		float _prop_w = _w / w;
		float _prop_h = _h / h;
		//
		for (pPoint p : points) {
			p.x *= _prop_w;
			p.y *= _prop_h;
		}
		//
		w = (int) _w;
		h = (int) _h;
	}

	/**
	 * Importowanie!
	 */
	public void importFromFile(String path) throws Exception {
		LineEditor.importShapeFromFile(points, path);
		/**
		 * Wymiary!
		 */
		w = h = 0;
		for (pPoint p : points) {
			if (p.x > w) {
				w = p.x;
			}
			if (p.y > h) {
				h = p.y;
			}
		}
		/**
		 * 
		 */
		label = path.substring(path.lastIndexOf("/") + 1);
	}
}