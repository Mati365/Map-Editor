package LineEditor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import Editor.Editor;
import Editor.Option;
import Platforms.PlatformInfo;
import Platforms.PlatformShape.pPoint;
import Platforms.PlatformShape.pPoint.Type;

public class LineEditor extends JPanel {
	static interface OpenFileCallBack {
		public void open(File file);
	}

	static class FileChoosePanel extends JPanel {
		/**
		 * 
		 * @param callback
		 */
		OpenFileCallBack	callback;

		public FileChoosePanel(OpenFileCallBack callback) {
			this.callback = callback;

			super.setLayout(new BorderLayout());
			setup();
		}

		/**
		 * 
		 */
		private JTextField			path		= new JTextField();
		private JButton				enter_path	= new JButton("...");

		private static JFileChooser	chooser		= new JFileChooser();

		private void setup() {
			super.add(path, BorderLayout.CENTER);
			super.add(enter_path, BorderLayout.EAST);

			/**
			 * Akcje!
			 */
			enter_path.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					chooser.showOpenDialog(FileChoosePanel.this);
					path.setText(chooser.getSelectedFile().getAbsolutePath());
					callback.open(chooser.getSelectedFile());
				}
			});
		}

		/**
		 * 
		 */

		public static File getFile() {
			return chooser.getSelectedFile();
		}
	}

	/**
	 * Panel właściwości!
	 */
	class ToolPanel extends JPanel {
		public ToolPanel() {
			super.setPreferredSize(new Dimension(160, 100));
			super.setBorder(BorderFactory.createTitledBorder("Właściwości"));
			super.setLayout(null);
			setup();
		}

		/**
		 * Tworzenie!
		 */
		private Option<JButton>			line_color			= new Option<JButton>("Kolor linii: ",
																	new JButton());
		private Option<JButton>			background_color	= new Option<JButton>("Kolor tła: ",
																	new JButton());
		private Option<FileChoosePanel>	background_image	= new Option<FileChoosePanel>("Tło: ",
																	new FileChoosePanel(new OpenFileCallBack() {

																		@Override
																		public
																				void
																				open(
																						File file) {
																			polygon.loadBackgroundImage(file);
																			polygon.repaint();
																		}
																	}));
		private JColorChooser			color_chooser		= new JColorChooser(Color.BLACK);
		private JDialog					dialog				= JColorChooser.createDialog(null,
																	"Kolor linii",
																	true,
																	color_chooser,
																	new ActionListener() {

																		@Override
																		public
																				void
																				actionPerformed(
																						ActionEvent e) {
																			line_color.getRightComponent()
																					.setBackground(color_chooser.getColor());
																			polygon.setLineColor(color_chooser.getColor());
																			polygon.repaint();
																		}
																	},
																	null);

		private JButton					undo				= new JButton("Undo");
		private JButton					clear				= new JButton("Clear");
		private JButton					export				= new JButton("Eksportuj");

		/**
		 * 
		 */
		private void setup() {
			line_color.getRightComponent()
					.setBackground(polygon.getLineColor());
			line_color.setBounds(5, 145, 150, 25);
			super.add(line_color);

			background_color.getRightComponent()
					.setBackground(polygon.getBackgroundColor());
			background_color.setBounds(5, 185, 140, 25);
			super.add(background_color);

			undo.setBounds(5, 55, 75, 35);
			super.add(undo);

			clear.setBounds(80, 55, 75, 35);
			super.add(clear);

			background_image.setBounds(5, 25, 150, 25);
			super.add(background_image);

			export.setBounds(5, 95, 150, 35);
			super.add(export);

			/**
			 * Akcje!
			 */
			export.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (polygon.getpPoints().isEmpty() || editor == null) {
						JOptionPane.showMessageDialog(null,
								"Błąd eksportu!");
						return;
					}
					String path = JOptionPane.showInputDialog(null,
							"Nazwa kształtu:");
					exportShapeToFile(polygon.getpPoints(), path);
					editor.addShape(path);
				}
			});

			line_color.getRightComponent()
					.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							dialog.setVisible(true);
						}
					});

			background_color.getRightComponent()
					.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							polygon.setBackgroundColor(JColorChooser.showDialog(null,
									"Kolor tła",
									polygon.getBackgroundColor()));
							background_color.getRightComponent()
									.setBackground(polygon.getBackgroundColor());
						}
					});

			undo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (polygon.getpPoints().size() > 1) {
						polygon.getpPoints().pollLast();
					}
					polygon.repaint();
				}
			});

			clear.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					while (polygon.getpPoints().size() != 1) {
						polygon.getpPoints().pollLast();
					}
					polygon.repaint();
				}
			});
		}
	}

	/**
	 * KONSTRUKTOR!!!!!!!!!!!!!!!!!!!!!!!
	 * Uruchamianie całego okna wraz z panelami
	 */
	private Editor	editor;

	public LineEditor(Editor editor) {
		this.editor = editor;

		setup();
	}

	/**
	 * Ustawianie menu!
	 */
	private JMenuBar	menubar	= new JMenuBar();
	private JMenu		file	= new JMenu("Plik");
	private JMenuItem	save	= new JMenuItem("Zapisz jako");
	private JMenuItem	open	= new JMenuItem("Otwórz");

	public static void
			exportShapeToFile(LinkedList<pPoint> polygon, String path) {
		try {
			pPoint min = new pPoint(-1, -1, false);
			for (pPoint p : polygon) {
				if (min.x == -1 || (p.x < min.x)) {
					min.x = p.x;
				}
				if (min.y == -1 || (p.y < min.y)) {
					min.y = p.y;
				}
			}

			PrintWriter out = new PrintWriter(path);
			out.println(polygon.size());
			for (pPoint p : polygon) {
				switch (p.type) {
					case POINT:
						if (p.begin) {
							out.println("S");
						} else {
							out.println("P " + (p.x - min.x) * 10 + " "
									+ (p.y - min.y) * 10);
						}
						break;

					case COLOR:
						out.println("C " + p.col.getRed() + " "
								+ p.col.getGreen() + " " + p.col.getBlue()
								+ " " + p.col.getAlpha());
						break;
				}

			}
			out.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public static void importShapeFromFile(
			LinkedList<pPoint> pPoints,
			String file) {
		/**
		 * Importowanie!
		 */
		pPoints.clear();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line = br.readLine();
			int size = Integer.valueOf(line);
			for (int i = 0; i < size; ++i) {
				line = br.readLine();
				String[] _arg = line.split(" ");
				switch (_arg[0]) {
					case "C":
						pPoints.add(new pPoint(new Color(Integer.valueOf(_arg[1]),
								Integer.valueOf(_arg[2]),
								Integer.valueOf(_arg[3]),
								Integer.valueOf(_arg[4]))));
						break;
					/**
					 * Odstęp
					 */
					case "S":
						pPoints.add(new pPoint(pPoints.getLast().x,
								pPoints.getLast().y,
								true));
						break;
					/**
					 * Platforma
					 */
					case "P":
						pPoints.add(new pPoint(Integer.valueOf(_arg[1]),
								Integer.valueOf(_arg[2]),
								pPoints.isEmpty()));
						pPoint p = pPoints.get(i);
						p.x /= 10;
						p.y /= 10;
						//p.x += 100;
						//p.y += 100;
						if (i > 0 && pPoints.get(i - 1).begin) {
							pPoints.get(i - 1).x = p.x;
							pPoints.get(i - 1).y = p.y;
						}
						break;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupMenu() {
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				exportShapeToFile(polygon.getpPoints(), "obiekt.txt");
			}
		});

		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				importShapeFromFile(polygon.getpPoints(), "obiekt.txt");
			}
		});

		file.add(save);
		file.add(open);

		menubar.add(file);
		add(menubar, BorderLayout.NORTH);
	}

	/**
	 * Dodawanie paneli i menu!
	 */
	private PolygonPanel	polygon	= new PolygonPanel();
	private JScrollPane		scroll	= new JScrollPane(polygon,
											JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
											JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	private ToolPanel		tools	= new ToolPanel();

	private void setup() {
		setLayout(new BorderLayout());
		setupMenu();
		/**
		 * DODAWANIE ELEMNTÓW!!
		 */
		JTabbedPane frame_tabs = new JTabbedPane();
		frame_tabs.addTab("1", scroll);

		add(frame_tabs, BorderLayout.CENTER);
		add(tools, BorderLayout.EAST);

		scroll.getHorizontalScrollBar()
				.addAdjustmentListener(new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						JScrollBar bar = scroll.getHorizontalScrollBar();
						if (polygon.getWidth() != 0
								&& e.getValue() >= polygon.getWidth() / 2) {
							polygon.setPreferredSize(new Dimension(polygon.getWidth() + 5,
									polygon.getHeight()));
							bar.setValue(bar.getValue() - 10);
						}
					}
				});
		scroll.getVerticalScrollBar()
				.addAdjustmentListener(new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						JScrollBar bar = scroll.getVerticalScrollBar();
						if (polygon.getHeight() != 0
								&& e.getValue() >= polygon.getHeight() / 2) {
							polygon.setPreferredSize(new Dimension(polygon.getWidth(),
									polygon.getHeight() + 5));
							bar.setValue(bar.getValue() - 10);
						}
					}
				});
		polygon.setPreferredSize(new Dimension(1000, 1000));
	}
}
