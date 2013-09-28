package Editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import LineEditor.LineEditor;
import Platforms.Mobs;
import Platforms.PlatformInfo;
import Platforms.PlatformShape;
import Platforms.PlatformInfo.Flag;
import Platforms.PlatformShape.pPoint;

/**
 * Główny edytor!
 */
public class Editor extends JPanel {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public Editor() {
		create();
		setVisible(true);
	}

	/**
	 * 
	 */
	private MapRenderer		map_renderer		= new MapRenderer(this);
	private JScrollPane		scroll				= new JScrollPane(map_renderer);
	private PlatformToolbar	platform_property	= new PlatformToolbar();
	private ScriptToolBar	script_toolbar		= new ScriptToolBar();
	private JTabbedPane		property_tab		= new JTabbedPane();

	private void create() {
		map_renderer.setPreferredSize(new Dimension(19111, 19111));

		setLayout(new BorderLayout());

		property_tab.setPreferredSize(new Dimension(100, 150));
		property_tab.addTab("Platformy", new JScrollPane(platform_property));
		property_tab.addTab("Paralax", new JPanel());

		add(scroll, BorderLayout.CENTER);
		add(property_tab, BorderLayout.SOUTH);
		add(script_toolbar, BorderLayout.WEST);

		setupMenu();
	}

	public PlatformToolbar getPlatformProperty() {
		return platform_property;
	}

	/**
	 * 
	 */
	private JMenuBar					menu				= new JMenuBar();

	private JMenu						file				= new JMenu("Plik");
	private JMenuItem					save				= new JMenuItem("Zapisz");
	private JMenuItem					open				= new JMenuItem("Otwórz");

	private JMenu						objects				= new JMenu("Obiekty");
	private JMenuItem					add_platform		= new JMenuItem("Dodaj platforme");
	private JMenuItem					add_shape			= new JMenuItem("Dodaj kształt");

	private final JFileChooser			file_chooser		= new JFileChooser();
	private LinkedList<PlatformShape>	shapes_container	= new LinkedList<PlatformShape>();

	public static int					script_id_counter	= 0;

	/**
	 * Dodawanie platformy
	 */
	public void addShape(String path) {
		PlatformShape shape = new PlatformShape();
		try {
			shape.importFromFile(path);
			shapes_container.add(shape);
			platform_property.shapes_model.addElement(shape.getLabel());
		} catch (Exception e) {
			System.out.println("NIE DODANO PLATFORMY!");
		}
	}

	private void setupMenu() {
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int val = file_chooser.showSaveDialog(Editor.this);
				if (val != JFileChooser.APPROVE_OPTION) {
					return;
				}
				Editor.this.map_renderer.getMap()
						.exportToFile(file_chooser.getSelectedFile()
								.getAbsolutePath(),
								shapes_container);
			}
		});
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int val = file_chooser.showOpenDialog(Editor.this);
				if (val != JFileChooser.APPROVE_OPTION) {
					return;
				}
				Editor.this.map_renderer.getMap().clear();
				Editor.this.shapes_container.clear();
				//
				Editor.this.map_renderer.getMap()
						.importFromFile(file_chooser.getSelectedFile()
								.getAbsolutePath(),
								shapes_container);
				Editor.this.repaint();
				//
				platform_property.shapes_model.clear();
				for (PlatformShape s : shapes_container) {
					platform_property.shapes_model.addElement(s.getLabel());
				}
			}
		});
		file.add(save);
		file.add(open);
		menu.add(file);

		add_shape.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (map_renderer.getSkel() == null) {
					return;
				}
				int val = file_chooser.showOpenDialog(Editor.this);
				if (val != JFileChooser.APPROVE_OPTION) {
					return;
				}
				addShape(file_chooser.getSelectedFile().getAbsolutePath());
			}
		});

		add_platform.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Editor.this.map_renderer.getMap()
						.getPlatforms()
						.add(platform_property.getPlatformInstance());
				Editor.this.map_renderer.repaint();
			}
		});

		final Mobs.Type[] mobs = Mobs.Type.values();
		for (int i = 0; i < mobs.length; ++i) {
			JMenuItem item = new JMenuItem("Dodaj " + mobs[i].toString());
			item.addActionListener(new AddMobListener());
			objects.add(item);
		}
		objects.add(add_shape);
		objects.add(add_platform);

		menu.add(objects);
		super.add(menu, BorderLayout.NORTH);
	}

	class AddMobListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Component[] components = objects.getMenuComponents();
			for (int i = 0; i < components.length; ++i) {
				if (i < Mobs.Type.values().length
						&& e.getSource().equals(components[i])) {
					PlatformInfo mob = Mobs.getMob(Mobs.Type.values()[i],
							map_renderer.getVisibleRect().x
									+ map_renderer.getVisibleRect().width / 2,
							map_renderer.getVisibleRect().y
									+ map_renderer.getVisibleRect().height / 2,
							1,
							0);
					mob.script_id = script_id_counter++;

					map_renderer.getMap().getPlatforms().add(mob);
					map_renderer.repaint();
					break;
				}
			}
		}

	}

	/**
	 * 
	 *
	 */
	class ScriptToolBar extends JPanel {
		public JTextArea	script	= new JTextArea();
		public JTextField	pos		= new JTextField(),
				bounds = new JTextField(), move_pos = new JTextField();

		public ScriptToolBar() {
			setPreferredSize(new Dimension(180, 300));
			create();
		}

		private void create() {
			setLayout(new BorderLayout());
			add(new JScrollPane(script), BorderLayout.CENTER);

			JButton set = new JButton("SET");
			add(set, BorderLayout.NORTH);

			JPanel panel = new JPanel(new GridLayout(3, 1));
			panel.add(pos);
			panel.add(bounds);
			panel.add(move_pos);
			add(panel, BorderLayout.SOUTH);

			set.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					PlatformInfo info = Editor.this.map_renderer.getSkel()
							.getPlatform();
					if (info != null) {
						info.script = script.getText();
						info.flag |= Flag.SCRIPT.getFlag();
						Editor.this.repaint();
					}
				}
			});
		}

		public void refreshScript() {
			PlatformInfo info = Editor.this.map_renderer.getSkel()
					.getPlatform();
			if (info != null) {
				script.setText(info.script);
				pos.setText("X:" + info.x + " Y:" + info.y);
				bounds.setText("W:" + info.w + " H:" + info.h);
				move_pos.setText("To X:" + info.to_x + " To Y:" + info.to_y);
			} else {
				script.setText("");
			}
		}
	}

	class PlatformToolbar extends JPanel {
		public PlatformToolbar() {
			setPreferredSize(new Dimension(1700, 70));
			create();
		}

		/**
		 * 
		 */
		private JButton						update_flags	= new JButton("Flagi");
		private JButton						remove			= new JButton("Usuń");
		private JButton						remove_shape	= new JButton("Usuń kształt");
		private Option<JButton>				platform_color	= new Option<JButton>("Kolor platformy:",
																	new JButton());
		private Option<JSlider>				transparency	= new Option<JSlider>("Alpha mapy:",
																	new JSlider(0,
																			255));

		private DefaultListModel<String>	shapes_model	= new DefaultListModel<String>();
		private DefaultListModel<String>	flags_model		= new DefaultListModel<String>();
		private DefaultListModel<String>	types_model		= new DefaultListModel<String>();
		private DefaultListModel<String>	border_model	= new DefaultListModel<String>();

		private JList<String>				types			= new JList<String>(types_model);
		private JList<String>				flags			= new JList<String>(flags_model);
		private JList<String>				shapes			= new JList<String>(shapes_model);
		private JList<String>				borders			= new JList<String>(border_model);

		private Option<JComboBox<String>>	levels			= new Option<JComboBox<String>>("Warstwa:",
																	new JComboBox<String>(new String[] {
																			"0",
																			"1",
																			"2" }));
		private JButton						mobile			= new JButton("Ruchoma");
		private JCheckBox					move_loop		= new JCheckBox("Pętla ruchu");

		private JComboBox<String>			orientation		= new JComboBox<String>(new String[] {
																	"NONE",
																	"RIGHT",
																	"LEFT",
																	"UP",
																	"DOWN" });

		public PlatformInfo getPlatformInstance() {
			PlatformInfo p = new PlatformInfo(map_renderer.getVisibleRect().x
					+ map_renderer.getVisibleRect().width / 2 - 50,
					map_renderer.getVisibleRect().y
							+ map_renderer.getVisibleRect().height / 2 - 30,
					200,
					60,
					platform_property.getColorChangeButton().getBackground(),
					Flag.NONE.getFlag(),
					Integer.valueOf(levels.getRightComponent()
							.getSelectedItem()
							.toString()));
			p.script_id = script_id_counter++;
			return p;
		}

		/**
		 * WAŻNE! KOPIOWANIE PARAMETRÓW DO KONTROLEK!!
		 * 
		 * @param info
		 */
		public void copyFrom(PlatformInfo info) {
			if (info.mob_type != null) {
				return;
			}
			orientation.setSelectedIndex(info.orientation);
			//
			script_toolbar.refreshScript();
			//
			platform_color.getRightComponent()
					.setBackground(new Color(info.col.getRGB()));
			//
			Flag[] val = Flag.values();
			LinkedList<Integer> selected = new LinkedList<Integer>();
			for (int i = 0; i < val.length; ++i) {
				if (info.isSet(val[i])
						|| (val[i] == Flag.SHAPE && info.shape != null)) {
					selected.add(i);
				}
			}
			if (selected.size() != 0) {
				int[] array = new int[selected.size()];
				for (int i = 0; i < selected.size(); ++i) {
					array[i] = selected.get(i);
				}
				flags.setSelectedIndices(array);
			} else {
				flags.clearSelection();
			}
			//
			LinkedList<Integer> border = new LinkedList<Integer>();
			borders.clearSelection();
			for (int i = 0; i < 4; ++i) {
				if (info.border[i]) {
					border.add(i);
				}
			}
			int[] border_selected = new int[border.size()];
			for (int i = 0; i < border.size(); ++i) {
				border_selected[i] = border.get(i).intValue();
			}
			borders.setSelectedIndices(border_selected);
			//
			types.setSelectedIndex(info.type.val);
			//
			shapes.clearSelection();
			for (int i = 0; i < shapes_container.size(); ++i) {
				if (info.shape != null
						&& shapes_container.get(i)
								.equals(info.shape.getSource())) {
					shapes.setSelectedIndex(i);
					break;
				}
			}
		}

		private JColorChooser	color_chooser	= new JColorChooser();
		private Dialog			color_dialog	= JColorChooser.createDialog(this,
														"Dialog Title",
														false,
														color_chooser,
														null,
														null);

		private void create() {
			platform_color.getRightComponent().setBackground(Color.white);
			platform_color.setBounds(5, 5, 210, 30);
			platform_color.getRightComponent()
					.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							color_dialog.setVisible(true);

							JButton btn = platform_color.getRightComponent();
							btn.setBackground(color_chooser.getColor());

							PlatformInfo selected = Editor.this.map_renderer.getSkel()
									.getPlatform();
							if (selected != null) {
								selected.setColor(new Color(btn.getBackground()
										.getRGB()));
							}
							Editor.this.repaint();
						}
					});

			for (String el : Flag.getValues()) {
				flags_model.addElement(el);
			}

			update_flags.setBounds(345, 55, 80, 30);
			update_flags.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PlatformInfo selected = Editor.this.map_renderer.getSkel()
							.getPlatform();
					if (selected != null) {
						if (flags.getSelectedValuesList().size() == 0) {
							return;
						}
						ArrayList<String> list = (ArrayList<String>) flags.getSelectedValuesList();
						if (list == null) {
							return;
						}
						boolean reset_shape = true;
						//
						selected.flag = 0x01;
						for (String flag : list) {
							Flag _flag = Flag.valueOf(flag);
							if (_flag != Flag.SHAPE) {
								selected.flag |= _flag.getFlag();
								if (_flag == Flag.SCRIPT) {
									selected.script = JOptionPane.showInputDialog("Wpisz skrypt:");
								}
							} else {
								reset_shape = false;
								selected.setShape(shapes.getSelectedIndex() == -1 ? null
										: shapes_container.get(shapes.getSelectedIndex()));
							}
						}
						if (reset_shape && selected.mob_type == null) {
							selected.setShape(null);
						}
						Editor.this.repaint();
					}
				}
			});

			JScrollPane scroll_flags = new JScrollPane(flags);
			scroll_flags.setBounds(225, 5, 110, 80);

			remove.setBounds(345, 5, 80, 40);
			remove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					map_renderer.getMap()
							.getPlatforms()
							.remove(Editor.this.map_renderer.getSkel()
									.getPlatform());
					map_renderer.getSkel().setPlatform(null);
					map_renderer.path_dragging = false;
					Editor.this.repaint();
				}
			});

			levels.setBounds(595, 5, 150, 30);
			levels.getRightComponent().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PlatformInfo selected = Editor.this.map_renderer.getSkel()
							.getPlatform();
					if (selected != null) {
						selected.level = Integer.valueOf(levels.getRightComponent()
								.getSelectedItem()
								.toString());
						Editor.this.repaint();
					}
				}
			});

			mobile.setBounds(760, 5, 105, 30);
			mobile.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					map_renderer.enablePathDragging();
					map_renderer.repaint();
				}
			});

			int index = 0;
			types.setBounds(870, 5, 105, 80);
			types.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent arg0) {
					PlatformInfo platform = map_renderer.getSkel()
							.getPlatform();
					if (platform != null) {
						platform.type = PlatformInfo.Type.valueOf(types.getSelectedValue());
					}
					map_renderer.repaint();
				}
			});
			for (PlatformInfo.Type t : PlatformInfo.Type.values()) {
				types_model.add(index++, t.toString());
			}

			borders.setBounds(980, 5, 105, 70);
			border_model.add(0, "Góra");
			border_model.add(1, "Prawo");
			border_model.add(2, "Dół");
			border_model.add(3, "Lewo");

			JButton set_border = new JButton("Set");
			set_border.setBounds(980, 75, 65, 30);
			set_border.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					PlatformInfo platform = map_renderer.getSkel()
							.getPlatform();
					if (platform == null) {
						return;
					}
					for (int i = 0; i < 4; ++i) {
						platform.border[i] = false;
					}
					for (int el : borders.getSelectedIndices()) {
						platform.border[el] = true;
					}
					map_renderer.repaint();
				}
			});

			move_loop.setBounds(760, 45, 100, 30);
			move_loop.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					PlatformInfo p = map_renderer.getSkel().getPlatform();
					if (p == null) {
						JOptionPane.showMessageDialog(null,
								"Brak zaznaczonego obiektu!");
					} else {
						p.move_loop = move_loop.isSelected();
					}
					map_renderer.repaint();
				}
			});

			remove_shape.setBounds(595, 45, 150, 35);
			remove_shape.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int index = shapes.getSelectedIndex();
					if (index == -1) {
						return;
					}
					//
					shapes_model.remove(index);
					shapes_container.remove(index);
				}
			});

			transparency.setBounds(5, 45, 210, 30);
			transparency.getRightComponent()
					.addChangeListener(new ChangeListener() {

						@Override
						public void stateChanged(ChangeEvent arg0) {
							for (PlatformInfo info : map_renderer.getMap()
									.getPlatforms()) {
								if (info.col == null) {
									continue;
								}
								info.col = new Color(info.col.getRed(),
										info.col.getGreen(),
										info.col.getBlue(),
										transparency.getRightComponent()
												.getValue());
							}
							Editor.this.map_renderer.repaint();
						}
					});

			JScrollPane scroll_shapes = new JScrollPane(shapes);
			scroll_shapes.setBorder(BorderFactory.createTitledBorder("Kształty"));
			scroll_shapes.setBounds(435, 5, 150, 80);

			JButton move_right = new JButton("->");
			move_right.setBounds(1150, 5, 55, 35);
			move_right.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (PlatformInfo info : map_renderer.getMap()
							.getPlatforms()) {
						info.x += 50;
						if (info.to_x > 0) {
							info.to_x += 50;
						}
					}
					Editor.this.map_renderer.repaint();
				}
			});
			JButton move_down = new JButton("_");
			move_down.setBounds(1150, 45, 55, 35);
			move_down.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (PlatformInfo info : map_renderer.getMap()
							.getPlatforms()) {
						info.y += 50;
						if (info.to_y > 0) {
							info.to_y += 50;
						}
					}
					Editor.this.map_renderer.repaint();
				}
			});
			JButton move_left = new JButton("<-");
			move_left.setBounds(1090, 5, 55, 35);
			move_left.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (PlatformInfo info : map_renderer.getMap()
							.getPlatforms()) {
						info.x -= 50;
						if (info.to_x > 0) {
							info.to_x -= 50;
						}
					}
					Editor.this.map_renderer.repaint();
				}
			});
			JButton move_up = new JButton("^");
			move_up.setBounds(1090, 45, 55, 35);
			move_up.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (PlatformInfo info : map_renderer.getMap()
							.getPlatforms()) {
						info.y -= 50;
						if (info.to_y > 0) {
							info.to_y -= 50;
						}
					}
					Editor.this.map_renderer.repaint();
				}
			});

			orientation.setBounds(1230, 5, 120, 35);

			JButton set_orientation = new JButton("Ustaw orien.");
			set_orientation.setBounds(1230, 45, 120, 35);
			set_orientation.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					PlatformInfo p = map_renderer.getSkel().getPlatform();
					if (p == null) {
						JOptionPane.showMessageDialog(null,
								"Brak zaznaczonego obiektu!");
					} else {
						p.orientation = orientation.getSelectedIndex();
					}
					map_renderer.repaint();
				}
			});

			JScrollPane scroll_types = new JScrollPane(types);
			scroll_types.setBounds(types.getX(),
					types.getY(),
					types.getWidth(),
					types.getHeight());

			setLayout(null);
			add(platform_color);
			add(remove);
			add(transparency);
			add(levels);
			add(scroll_shapes);
			add(scroll_flags);
			add(remove_shape);
			add(update_flags);
			add(mobile);
			add(move_loop);
			add(scroll_types);
			add(borders);
			add(set_border);
			add(move_right);
			add(move_left);
			add(move_up);
			add(move_down);
			add(orientation);
			add(set_orientation);
		}

		public JButton getColorChangeButton() {
			return platform_color.getRightComponent();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame window = new JFrame("Game editor!");
		window.setSize(700, 900);
		window.setLayout(new BorderLayout());

		Editor editor = new Editor();
		editor.setPreferredSize(new Dimension(100, 450));

		LineEditor line_editor = new LineEditor(editor);
		line_editor.setPreferredSize(new Dimension(100, 450));

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				editor,
				line_editor);
		split.setDividerLocation(550);
		window.add(split, BorderLayout.CENTER);

		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
