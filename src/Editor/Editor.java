package Editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

/**
 * @author mateusz Format: 1 linia: ilosc platform pozostale linie: x y w h R G
 *         B A
 */
class Map {
	public static class PlatformInfo {
		public float x, y, w, h;
		public Color col;
		public boolean selected;

		public PlatformInfo(float _x, float _y, float _w, float _h, Color _col) {
			x = _x;
			y = _y;
			w = _w;
			h = _h;
			col = _col;
		}

		@Override
		public String toString() {
			return x + " " + y + " " + w + " " + h + " " + col.getRed() + " "
					+ col.getGreen() + " " + col.getBlue() + " "
					+ col.getAlpha();
		}
	}

	private LinkedList<PlatformInfo> platforms = new LinkedList<PlatformInfo>();

	public LinkedList<PlatformInfo> getPlatforms() {
		return platforms;
	}

	/**
	 * 
	 */
	public boolean exportToFile(String _path) {
		try {
			PrintWriter out = new PrintWriter(_path);
			out.println(platforms.size());
			for (PlatformInfo platform : platforms) {
				out.println(platform.toString());
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	public void clear() {
		platforms.clear();
	}
}

class PlatformEditSkel {
	private Map.PlatformInfo platform;

	/**
	 * 
	 */
	public Map.PlatformInfo getPlatform() {
		return platform;
	}

	public void setPlatform(Map.PlatformInfo platform) {
		this.platform = platform;
	}

	boolean getDragg(float x, float y, int button) {
		if (x > platform.x + platform.w - 10 && x < platform.x + platform.w
				&& y > platform.y + platform.h - 10
				&& y < platform.y + platform.h) {
			return true;
		}
		return false;
	}

	/** 
	 * 
	 */
	public void drawPlatformSkel(Graphics2D g2) {
		if (platform != null) {
			g2.setColor(Color.WHITE);
			g2.fillRect((int) platform.x + (int) platform.w - 10,
					(int) platform.y + (int) platform.h - 10, 10, 10);
		}
	}
}

class MapRenderer extends JPanel {
	private PlatformEditSkel skel = new PlatformEditSkel();

	private BasicStroke fill_stroke = new BasicStroke(1);
	private BasicStroke border_stroke = new BasicStroke(2);

	private static final long serialVersionUID = 1L;
	private Map map = new Map();

	/**
	 * 
	 */
	public MapRenderer() {
		super.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				Map.PlatformInfo p = skel.getPlatform();
				if (p == null || resize) {
					return;
				}
				if (dragg_x != 0) {
					dragg_x = dragg_y = 0;
					skel.setPlatform(null);
				}
				p.x = (int) (p.x / 20) * 20;
				p.y = (int) (p.y / 20) * 20;
				p.w = (int) (p.w / 20) * 20;
				p.h = (int) (p.h / 20) * 20;
				MapRenderer.super.setCursor(Cursor.getDefaultCursor());
				MapRenderer.super.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!resize && skel.getPlatform() != null
						&& skel.getDragg(e.getX(), e.getY(), e.getButton())) {
					resize = true;
					MapRenderer.super.setCursor(Cursor
							.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				} else if (skel.getPlatform() != null
						&& getClick(e.getX(), e.getY(), e.getButton()) != null) {
					if (resize) {
						resize = false;
						return;
					}
					MapRenderer.super.setCursor(Cursor
							.getPredefinedCursor(Cursor.MOVE_CURSOR));
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
		super.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				Map.PlatformInfo p = skel.getPlatform();
				if (resize && p != null) {
					p.w = e.getX() - p.x + 5;
					p.h = e.getY() - p.y + 5;
					MapRenderer.super.repaint();
				} else if (p == null) {
					resize = false;
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				getDragg(e.getX(), e.getY(), e.getButton());
			}
		});
	}

	public Map getMap() {
		return map;
	}

	/**
	 * 
	 */
	float dragg_x = 0, dragg_y = 0;
	boolean resize = false;

	public Map.PlatformInfo getClick(float x, float y, int button) {
		boolean found = false;
		Map.PlatformInfo p = null;

		for (Map.PlatformInfo platform : map.getPlatforms()) {
			if (x > platform.x && x < platform.x + platform.w && y > platform.y
					&& y < platform.y + platform.h) {
				map.getPlatforms().remove(platform);
				map.getPlatforms().addLast(platform);
				skel.setPlatform(platform);
				p = platform;
				found = true;
				break;
			}
		}
		if (!found) {
			skel.setPlatform(null);
		}
		super.repaint();
		return p;
	}

	public void getDragg(float x, float y, int button) {
		Map.PlatformInfo p = skel.getPlatform();
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

	/**
	 * 
	 */
	private void drawPlatform(Map.PlatformInfo platform, Graphics2D g2) {
		g2.translate(platform.x, platform.y);
		g2.setColor(platform.col);
		g2.setStroke(border_stroke);
		g2.drawRect(0, 0, (int) platform.w, (int) platform.h);
		g2.setStroke(fill_stroke);
		if (platform.w > platform.h) {
			for (int i = 0; i < platform.w / 10; ++i) {
				g2.drawLine(i * 10, (int) platform.h, i * 10 + 10, 0);
			}
		} else {
			for (int i = 0; i < platform.h / 10; ++i) {
				g2.drawLine((int) platform.w, i * 10, 0, i * 10 + 10);
			}
		}
		g2.translate(-platform.x, -platform.y);
	}

	@Override
	public void paintComponent(Graphics g) {
		int w = super.getWidth(), h = super.getHeight();
		int cell_w = 100, cell_h = 100;

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);
		g.setColor(new Color(28, 28, 28));
		for (int i = 0; i < w / cell_w * 5 + 5; ++i) {
			for (int j = 0; j < h / cell_h * 5 + 5; ++j) {
				g.drawRect(-1 + i * cell_w / 5, -1 + j * cell_h / 5,
						cell_w / 5, cell_h / 5);
			}
		}
		g.setColor(Color.DARK_GRAY);
		for (int i = 0; i < w / cell_w + 1; ++i) {
			for (int j = 0; j < h / cell_h + 1; ++j) {
				g.drawRect(-1 + i * cell_w, -1 + j * cell_h, cell_w, cell_h);
			}
		}

		Graphics2D g2 = (Graphics2D) g;
		for (Map.PlatformInfo platform : map.getPlatforms()) {
			drawPlatform(platform, g2);
		}
		if (skel.getPlatform() != null) {
			Map.PlatformInfo p = skel.getPlatform();
			g2.setColor(new Color(98, 98, 98, 158));
			g2.fillRect((int) p.x, (int) p.y, (int) p.w, (int) p.h);
			drawPlatform(p, g2);
		}
		skel.drawPlatformSkel(g2);
		g2.dispose();
	}
}

public class Editor extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Editor() {
		super("Edytor map!");
		setSize(700, 500);
		create();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * 
	 */
	private MapRenderer map_renderer = new MapRenderer();
	private JScrollPane scroll = new JScrollPane(map_renderer);

	private void create() {
		map_renderer.setPreferredSize(new Dimension(1111, 1111));

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		add(new ToolBar(), BorderLayout.NORTH);
		add(new PropertyBar(), BorderLayout.WEST);

		setupMenu();

		map_renderer
				.getMap()
				.getPlatforms()
				.add(new Map.PlatformInfo(40, 40, 100, 50,
						new Color(255, 0, 98)));

		map_renderer
				.getMap()
				.getPlatforms()
				.add(new Map.PlatformInfo(70, 70, 200, 60, new Color(255, 255,
						98)));
	}

	/**
	 * 
	 */
	private JMenuBar menu = new JMenuBar();

	private JMenu file = new JMenu("Plik");
	private JMenuItem save = new JMenuItem("Zapisz");

	private JMenu objects = new JMenu("Obiekty");
	private JMenuItem add_platform = new JMenuItem("Dodaj platforme");

	private void setupMenu() {
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Editor.this.map_renderer.getMap().exportToFile("mapa.txt");
			}
		});
		file.add(save);
		menu.add(file);

		// /////////////

		add_platform.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Editor.this.map_renderer
						.getMap()
						.getPlatforms()
						.add(new Map.PlatformInfo(20, 20, 100, 100, new Color(
								255, 255, 255, 255)));
				Editor.this.map_renderer.repaint();
			}
		});
		objects.add(add_platform);
		menu.add(objects);

		setJMenuBar(menu);
	}
	
	/**
	 * 
	 *
	 */
	class PropertyBar extends JPanel {
		
		public PropertyBar() {
			setPreferredSize(new Dimension(150, 100));
			create();
		}
		
		/**
		 * 
		 */
		private void create() {
			
		}
	}

	/**
	 *
	 */
	class ToolBar extends JToolBar {

		public ToolBar() {
			create();
		}

		/**
		 * 
		 */
		private JButton clear = new JButton(new ImageIcon("new.png"));
		private JButton save = new JButton(new ImageIcon("save.png"));
		private JButton open = new JButton(new ImageIcon("open.png"));

		private void create() {
			connectActions();

			super.add(clear);
			super.add(save);
			super.add(open);
		}

		private void connectActions() {
			clear.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					map_renderer.getMap().clear();
					map_renderer.repaint();
				}
			});

			save.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Editor.this.map_renderer.getMap().exportToFile("mapa.txt");
					JOptionPane.showMessageDialog(Editor.this,
							"Zapisywanie zakończone sukcesem!");
				}
			});
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Editor();
	}
}
