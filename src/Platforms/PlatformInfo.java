package Platforms;

import java.awt.Color;
import java.util.LinkedList;

public class PlatformInfo {
	public float			x, y, w, h;

	public Color			col;
	public boolean			selected;

	public PlatformShape	shape;

	public float			to_x			= -1, to_y = -1;
	public float			move_velocity	= 2;
	public boolean			move_loop		= false;

	public int				flag			= Flag.NONE.flag;
	public int				level			= 0;

	public boolean[]		border			= new boolean[4];
	public boolean			resize_lock		= false;

	public Mobs.Type		mob_type		= null;

	public enum Type {
		DIAGONAL(0), SIMPLE(1), FILLED(2), NONE(3), MOB(4);

		public int	val;

		Type(int _type) {
			val = _type;
		}
	};

	public Type	type	= Type.SIMPLE;

	public enum Flag {
		NONE(1),
		STATIC(1 << 1),
		HIDDEN(1 << 2),
		NOT_RENDERABLE(1 << 3),
		SHAPE(1 << 4);

		private int	flag;

		private Flag(int _flag) {
			flag = _flag;
		}

		/**
		 * @return
		 */
		public int getFlag() {
			return flag;
		}

		public static String[] getValues() {
			String[] array = new String[Flag.values().length];
			int index = 0;
			for (Flag f : Flag.values()) {
				array[index++] = f.toString();
			}
			return array;
		}

		public static Flag getValueOf(int _flag) {
			for (Flag f : Flag.values()) {
				if (f.flag == _flag) {
					return f;
				}
			}
			return NONE;
		}
	}

	public PlatformInfo(float _x, float _y, float _w, float _h, Color _col,
			int _flag, int _level) {
		x = _x;
		y = _y;
		w = _w;
		h = _h;
		col = _col;
		flag = _flag;
		level = _level;
		for (int i = 0; i < 4; ++i) {
			border[i] = true;
		}
	}

	/**
	 * @return
	 */
	public boolean isSet(Flag _flag) {
		return (flag & _flag.getFlag()) == _flag.getFlag();
	}

	/**
	 * 
	 */
	public void setColor(Color _col) {
		col = _col;
		//
		if (shape != null && !shape.getPoints().isEmpty()) {
			shape.getPoints().get(0).col = _col;
		}
	}

	/**
	 * 
	 * @param _shape
	 */
	public void setShape(PlatformShape _shape) {
		if (_shape != null) {
			shape = _shape.clone();
			if (w == 0 || h == 0) {
				w = shape.getWidth();
				h = shape.getHeight();
			} else {
				shape.fitTo(w, h);
			}
		} else {
			shape = null;
		}
	}

	/**
	 * 
	 */
	public void fitToWidth(float _w) {
		float prop = _w / w;
		w *= prop;
		h *= prop;
		//
		if (shape != null)
			shape.fitTo(w, h);
	}

	/**
	 * 
	 */
	public void setSize(float _w, float _h) {
		if (resize_lock)
			return;
		w = _w;
		h = _h;
		//
		if (shape != null)
			shape.fitTo(w, h);
	}

	/**
	 * 
	 */
	public static PlatformInfo getFromString(
			String str,
			LinkedList<PlatformShape> shapes) {
		String[] split = str.split(" ");

		float x = Double.valueOf(split[11]).floatValue(), y = Double.valueOf(split[12])
				.floatValue(), w = Double.valueOf(split[13]).floatValue(), h = Double.valueOf(split[14])
				.floatValue(), r = Double.valueOf(split[15]).floatValue(), g = Double.valueOf(split[16])
				.floatValue(), b = Double.valueOf(split[17]).floatValue(), a = Double.valueOf(split[18])
				.floatValue();
		PlatformInfo p = new PlatformInfo(x,
				y,
				w,
				h,
				new Color((int) r, (int) g, (int) b, (int) a),
				Integer.valueOf(split[10]),
				Integer.valueOf(split[19]));
		p.move_loop = split[5].equals("1") ? true : false;

		float to_x = Double.valueOf(split[6]).floatValue();
		float to_y = Double.valueOf(split[7]).floatValue();

		p.to_x = (to_x == -1 ? -1 : x + w + to_x);
		p.to_y = (to_y == -1 ? -1 : y + h + to_y);
		p.move_velocity = Double.valueOf(split[8]).floatValue();
		// p.move_velocity = Double.valueOf(split[9]).floatValue();
		p.type = Type.values()[Integer.valueOf(split[4])];

		// !!!!!!!!!!!!!!!
		for (int i = 0; i < 4; ++i) {
			p.border[i] = split[i].equals("1") ? true : false;
		}
		// !!!!!!!!!!!!!!!

		if (Integer.valueOf(split[20]) == 1) {
			String label = split[21];
			for (PlatformShape s : shapes) {
				if (s.getLabel().equals(label)) {
					p.setShape(s);
					break;
				}
			}
		}
		return p;
	}

	/**
	 * Opis:
	 * [obramowanie] [typ zamalowania] [pętla ruchu 0:1] [to_x] [to_y] [speed_x]
	 * [speed_y]
	 * [flag] [4 kolory]
	 * [level] [ksztalt 0:1] [nazwa ksztaltu]
	 */
	@Override
	public String toString() {
		float distance_x = (to_x != -1 ? to_x - x - w : -1);
		float distance_y = (to_y != -1 ? to_y - y - h : -1);
		
		String _move = (move_loop ? "1" : "0") + " " + distance_x + " "
				+ distance_y + " " + (distance_x > 0 ? "2" : "0")
				+ " " + (distance_y > 0 ? "2" : "0");

		String str = (border[0] ? 1 : 0) + " " + (border[1] ? 1 : 0) + " "
				+ (border[2] ? 1 : 0) + " " + (border[3] ? 1 : 0) + " "
				+ type.val + " " + _move + " " + flag + " " + x + " " + y + " "
				+ w + " " + h + " " + col.getRed() + " " + col.getGreen() + " "
				+ col.getBlue() + " " + col.getAlpha() + " " + level + " "
				+ (shape == null ? 0 : 1) + " ";
		if (shape != null)
			str += shape.getLabel() + " ";
		else
			str += "null ";
		return str;
	}
}
