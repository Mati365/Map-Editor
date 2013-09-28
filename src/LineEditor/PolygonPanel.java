package LineEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Platforms.PlatformShape.pPoint;
import Platforms.PlatformShape.pPoint.Type;

public class PolygonPanel extends JPanel implements MouseListener,
		MouseMotionListener {
	/**
	 * Konstruktor poligonu!
	 */
	public PolygonPanel() {
		super.addMouseListener(this);
		super.addMouseMotionListener(this);
	}

	/**
	 * Tło
	 */
	private BufferedImage	background_image;

	public void loadBackgroundImage(File file) {
		try {
			background_image = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int		spaces				= 10;
	private pPoint	active_pPoint		= new pPoint(-1, -1, true);
	private Color	line_color			= Color.WHITE;
	private Color	background_color	= Color.GRAY;

	public Color getBackgroundColor() {
		return background_color;
	}

	public void setBackgroundColor(Color background_color) {
		this.background_color = background_color;
		//
		repaint();
	}

	public Color getLineColor() {
		return line_color;
	}

	public void setLineColor(Color line_color) {
		this.line_color = line_color;
		if (!pPoints.isEmpty()) {
			pPoint p = new pPoint(line_color);
			p.x = pPoints.getLast().x;
			p.y = pPoints.getLast().y;
			this.pPoints.add(p);
		}
	}

	/**
	 * 
	 */
	@Override
	public void paintComponent(Graphics g) {
		// Pr0fesionalne rysowanie!
		Graphics2D g2 = (Graphics2D) g;
		//
		g.setColor(background_color);
		g.fillRect(0, 0, getWidth(), getHeight());

		Color col = new Color(58, 58, 58);
		g.setColor(col);
		for (int i = 0; i < getWidth() / spaces + 1; ++i) {
			for (int j = 0; j < getHeight() / spaces + 1; ++j) {
				if (i == active_pPoint.x && j == active_pPoint.y) {
					g.setColor(Color.RED);
					g.fillRect(i * spaces - 1, j * spaces - 1, 2, 2);
					g.setColor(col);
				} else {
					g.fillRect(i * spaces, j * spaces, 1, 1);
				}
			}
		}
		if (background_image != null) {
			g.drawImage(background_image,
					0,
					0,
					getVisibleRect().width,
					getVisibleRect().height,
					null);
		}
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.white);
		for (int i = 0; i < pPoints.size(); ++i) {
			if (i != 0 && !pPoints.get(i).begin) {
				g.drawLine(pPoints.get(i - 1).x * spaces, pPoints.get(i - 1).y
						* spaces, pPoints.get(i).x * spaces, pPoints.get(i).y
						* spaces);
			}
			if (pPoints.get(i).type == Type.COLOR) {
				g2.setColor(pPoints.get(i).col);
				continue;
			}
		}
		if (!pPoints.isEmpty() && active_pPoint.x != -1) {
			g.drawLine(pPoints.getLast().x * spaces, pPoints.getLast().y
					* spaces, active_pPoint.x * spaces, active_pPoint.y
					* spaces);
		}
		g2.setStroke(new BasicStroke(1));
		g2.dispose();
	}

	/**
	 * 
	 */
	boolean	thrown	= false;

	void throwPen() {
		active_pPoint.x = active_pPoint.y = -1;
		thrown = true;
	}

	/**
	 * 
	 */
	private LinkedList<pPoint>	pPoints	= new LinkedList<pPoint>();

	public LinkedList<pPoint> getpPoints() {
		return pPoints;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			int index_x = e.getX() / spaces, index_y = e.getY() / spaces;
			if (thrown) {
				thrown = false;
				pPoints.add(new pPoint(index_x, index_y, true));
			}
			if (pPoints.isEmpty()) {
				pPoint p = new pPoint(line_color);
				p.x = index_x;
				p.y = index_y;
				pPoints.add(p);
			}
			pPoints.add(new pPoint(index_x, index_y, false));
		} else if (e.getButton() == 3) {
			throwPen();
		}
		super.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!thrown) {
			int index_x = e.getX() / spaces, index_y = e.getY() / spaces;
			active_pPoint.x = index_x;
			active_pPoint.y = index_y;
		}

		super.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}
}
