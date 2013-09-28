package Editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import Platforms.Map;
import Platforms.Mobs.Type;
import Platforms.Mobs;
import Platforms.PlatformEditSkel;
import Platforms.PlatformInfo;
import Platforms.PlatformInfo.Flag;
import Platforms.PlatformShape.pPoint;

class MapRenderer extends JPanel {
	private PlatformEditSkel		skel				= new PlatformEditSkel();

	private BasicStroke				fill_stroke			= new BasicStroke(1);
	private BasicStroke				border_stroke		= new BasicStroke(2);

	private static final long		serialVersionUID	= 1L;
	private Map						map					= new Map();

	private Editor					editor;
	private LinkedList<MapRenderer>	paralax_maps		= new LinkedList<MapRenderer>();

	public static int				childs				= 0;
	private int						child				= childs++;
	private int						mouse_x				= 0, mouse_y = 0;

	/**
	 * 
	 */
	public MapRenderer(Editor editor) {
		this.editor = editor;

		super.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				PlatformInfo p = skel.getPlatform();
				if (p == null || resize) {
					return;
				}
				if (dragg_x != 0) {
					dragg_x = dragg_y = 0;
					skel.setPlatform(null);
					path_dragging = false;
				}
				p.x = (int) (p.x / 10) * 10;
				p.y = (int) (p.y / 10) * 10;
				p.setSize((int) (p.w / 10) * 10, (int) (p.h / 10) * 10);

				MapRenderer.super.setCursor(Cursor.getDefaultCursor());
				MapRenderer.super.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!resize && skel.getPlatform() != null
						&& skel.getDragg(e.getX(), e.getY(), e.getButton())) {
					if (skel.getPlatform().resize_lock) {
						return;
					}
					resize = true;
					MapRenderer.super.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				} else if (skel.getPlatform() != null) {
					if (resize) {
						resize = false;
						return;
					}
					MapRenderer.super.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				getClick(e.getX(), e.getY(), e.getButton());
			}
		});
		/**
		 * Zmiana rozmiaru platformy!
		 */
		super.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				mouse_x = e.getX();
				mouse_y = e.getY();
				//
				PlatformInfo p = skel.getPlatform();
				if (resize && p != null) {
					p.w = e.getX() - p.x + 5;
					p.h = e.getY() - p.y + 5;
					if (p.w < 10) {
						p.w = 10;
					}
					if (p.h < 10) {
						p.h = 10;
					}
					MapRenderer.super.repaint();
				} else if (p == null) {
					resize = false;
				}
				if (path_dragging) {
					MapRenderer.this.repaint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				getDragg(e.getX(), e.getY(), e.getButton());
			}
		});
	}

	public Editor getEditor() {
		return editor;
	}

	public Map getMap() {
		return map;
	}

	/**
	 * 
	 */
	float	dragg_x			= 0, dragg_y = 0;
	boolean	resize			= false;
	boolean	path_dragging	= false;

	public boolean enablePathDragging() {
		if (path_dragging)
			path_dragging = false;
		else {
			if (skel.getPlatform().to_x != -1) {
				skel.getPlatform().to_x = -1;
				skel.getPlatform().to_y = -1;
				path_dragging = false;
			} else {
				path_dragging = !(skel.getPlatform() == null);
			}
		}
		//
		return path_dragging;
	}

	public PlatformInfo getClick(float x, float y, int button) {
		boolean found = false;
		PlatformInfo p = null;

		if (path_dragging) {
			path_dragging = false;
			if (skel.getPlatform() != null) {
				skel.getPlatform().to_x = (int) ((float) x / 10.f) * 10;
				skel.getPlatform().to_y = (int) ((float) y / 10.f) * 10;
			}
		}
		for (PlatformInfo platform : map.getPlatforms()) {
			if (x > platform.x && x < platform.x + platform.w && y > platform.y
					&& y < platform.y + platform.h) {
				if (platform.mob_type != Mobs.Type.PORTAL_BEGIN
						&& platform.mob_type != Mobs.Type.PORTAL_END) {
					map.getPlatforms().remove(platform);
					map.getPlatforms().addLast(platform);
				}
				skel.setPlatform(platform);
				editor.getPlatformProperty().copyFrom(platform);
				//
				p = platform;
				found = true;
				break;
			}
		}
		if (!found) {
			skel.setPlatform(null);
			path_dragging = false;
		}
		super.repaint();
		return p;
	}

	public void getDragg(float x, float y, int button) {
		PlatformInfo p = skel.getPlatform();
		if (p == null) {
			dragg_x = dragg_y = 0;
			return;
		}
		if (dragg_x == 0) {
			dragg_x = x - p.x;
			dragg_y = y - p.y;
		}
		p.x = x - dragg_x;
		p.y = y - dragg_y;
		super.repaint();
	}

	public PlatformEditSkel getSkel() {
		return skel;
	}

	private float	paralax	= 0;

	public void setParalax(float _paralax) {
		paralax = _paralax;
	}

	public float getParalax() {
		return paralax;
	}

	/**
	 * 
	 */
	private void drawPlatform(PlatformInfo platform, Graphics2D g2) {
		if (platform.x > getWidth() || platform.y > getHeight()
				|| platform.x + platform.w < 0 || platform.y + platform.y < 0) {
			return;
		}
		g2.translate(platform.x, platform.y);
		/**
		 * Rysowanie platformy!
		 */
		if (platform.shape == null) {
			g2.setStroke(border_stroke);
			/**
			 * Obramowanie!
			 * 0 - góra
			 * 1 - prawo
			 * 2 - dół
			 * 3 - lewo
			 */
			g2.setColor(Color.YELLOW);
			g2.drawRect(0, 0, (int) platform.w, (int) platform.h);
			if (platform.isSet(Flag.SCRIPT)) {
				g2.setColor(Color.white);
				g2.drawString("Skrypt", platform.w / 2 - 20, platform.h / 2 + 5);
			}
			g2.setColor(platform.col);
			if (platform.border[0]) {
				g2.drawLine(0, 0, (int) platform.w, 0);
			}
			if (platform.border[1]) {
				g2.drawLine((int) platform.w,
						0,
						(int) platform.w,
						(int) platform.h);
			}
			if (platform.border[2]) {
				g2.drawLine(0,
						(int) platform.h,
						(int) platform.w,
						(int) platform.h);
			}
			if (platform.border[3]) {
				g2.drawLine(0, 0, 0, (int) platform.h);
			}

			g2.setStroke(fill_stroke);
			if (platform.isSet(Flag.STATIC)) {
				switch (platform.type) {
				/**
					 * 
					 */
					case DIAGONAL:
						if (platform.w > platform.h) {
							for (int i = 0; i < platform.w / 10; ++i) {
								g2.drawLine(i * 10,
										(int) platform.h,
										i * 10 + 10,
										0);
							}
						} else {
							for (int i = 0; i < platform.h / 10; ++i) {
								g2.drawLine((int) platform.w,
										i * 10,
										0,
										i * 10 + 10);
							}
						}
						break;
					/**
						 * 
						 */
					case METAL:
						if (platform.w > platform.h) {
							for (int i = 0; i < platform.w / 8; ++i) {
								if (i % 2 == 0) {
									g2.drawLine(i * 8,
											(int) platform.h,
											i * 8 + 8,
											0);
								} else {
									g2.drawLine(i * 8,
											(int) platform.h,
											i * 8 - 8,
											0);
								}
							}
						} else {
							for (int i = 0; i < platform.h / 10; ++i) {
								g2.drawLine((int) platform.w,
										i * 10,
										0,
										i * 10 + 10);
							}
						}
						break;

					/**
						 * 
						 */
					case SIMPLE:
					case ICY:
						if (platform.w > platform.h) {
							for (int i = 0; i < platform.w / 10; ++i) {
								g2.drawLine(i * 10, (int) platform.h, i * 10, 0);
							}
						} else {
							for (int i = 0; i < platform.h / 10; ++i) {
								g2.drawLine((int) platform.w, i * 10, 0, i * 10);
							}
						}
						break;
					/**
						 * 
						 */
					case FILLED:
						g2.fillRect(0, 0, (int) platform.w, (int) platform.h);
						break;
					/**
						 * 
						 */
					default:
						break;
				}
				/**
				 * Pokrywa śniegowa
				 */
				if (platform.type == PlatformInfo.Type.ICY) {
					g2.setColor(Color.WHITE);
					g2.setStroke(new BasicStroke(3));
					g2.drawLine(0, -3, (int) platform.w, -3);
				}
			}
		} else {
			g2.setColor(Color.green);
			g2.setStroke(new BasicStroke(1));
			g2.drawRect(-(int) border_stroke.getLineWidth(),
					-(int) border_stroke.getLineWidth(),
					(int) platform.w + (int) border_stroke.getLineWidth() * 2,
					(int) platform.h + (int) border_stroke.getLineWidth() * 2);
			// -----------
			LinkedList<pPoint> points = platform.shape.getPoints();
			// ----------
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.white);
			for (int i = 0; i < points.size(); ++i) {
				if (i != 0 && !points.get(i).begin) {
					g2.drawLine(points.get(i - 1).x,
							points.get(i - 1).y,
							points.get(i).x,
							points.get(i).y);
				}
				if (points.get(i).type == pPoint.Type.COLOR) {
					Color col = points.get(i).col;
					g2.setColor(col);
					continue;
				}
			}
			g2.setStroke(new BasicStroke(1));
		}
		g2.setColor(Color.green);
		g2.drawString(String.valueOf(platform.level), 10, 18);
		g2.translate(-platform.x, -platform.y);
		if (platform.to_x != -1) {
			g2.drawLine((int) platform.x + (int) platform.w,
					(int) platform.y + (int) platform.h,
					(int) platform.to_x,
					(int) platform.to_y);
			if (platform.move_loop) {
				g2.setColor(Color.blue);
				//
				g2.drawString("LOOP",
						platform.x + platform.w - 40,
						platform.y + 20);
			}
		}
		g2.setColor(Color.red);
		//
		if (platform.orientation != 0) {
			g2.setStroke(new BasicStroke(3));
			switch (platform.orientation) {
			// prawo
				case 1:
					g2.drawLine((int) platform.x + (int) platform.w,
							(int) platform.y,
							(int) platform.x + (int) platform.w,
							(int) platform.y + (int) platform.h);
					break;
				// lewo
				case 2:
					g2.drawLine((int) platform.x,
							(int) platform.y,
							(int) platform.x,
							(int) platform.y + (int) platform.h);
					break;
				// gora
				case 3:
					g2.drawLine((int) platform.x,
							(int) platform.y,
							(int) platform.x + (int) platform.w,
							(int) platform.y);
					break;
				// dol
				case 4:
					g2.drawLine((int) platform.x, (int) platform.y
							+ (int) platform.h, (int) platform.x
							+ (int) platform.w, (int) platform.y
							+ (int) platform.h);
					break;
			}
		}
		if (platform.mob_type == Type.PORTAL_END) {
			g2.setColor(Color.red);
			g2.setStroke(new BasicStroke(1));
			g2.drawLine((int) platform.x,
					(int) platform.y,
					(int) platform.linked.x,
					(int) platform.linked.y);
		}
		g2.setColor(Color.white);
		g2.drawString("ID:" + platform.script_id,
				platform.x + platform.w - 50,
				platform.y + 20);
		// g2.dispose();
	}

	@Override
	public void paintComponent(Graphics g) {
		int w = super.getVisibleRect().width;
		int h = super.getVisibleRect().height;
		int x = super.getVisibleRect().x;
		int y = super.getVisibleRect().y;
		//
		if (child == 0) {
			g.setColor(Color.GRAY);
			g.fillRect(x, y, w, h);
		}
		for (MapRenderer map : paralax_maps) {
			map.paintComponent(g);
		}
		//
		Graphics2D g2 = (Graphics2D) g;
		for (PlatformInfo platform : map.getPlatforms()) {
			drawPlatform(platform, g2);
		}
		if (skel.getPlatform() != null) {
			PlatformInfo p = skel.getPlatform();
			g2.setColor(new Color(98, 98, 98, 158));
			g2.fillRect((int) p.x, (int) p.y, (int) p.w, (int) p.h);
			drawPlatform(p, g2);
			if (path_dragging) {
				g2.setColor(Color.green);
				g2.drawLine((int) p.x + (int) p.w,
						(int) p.y + (int) p.h,
						mouse_x,
						mouse_y);
			}
		}
		skel.drawPlatformSkel(g2);
		g2.dispose();
	}
}