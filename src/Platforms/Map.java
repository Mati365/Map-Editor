package Platforms;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;

import Platforms.PlatformInfo.Flag;
import Editor.Editor;

public class Map {
	private LinkedList<PlatformInfo>	platforms	= new LinkedList<PlatformInfo>();

	public LinkedList<PlatformInfo> getPlatforms() {
		return platforms;
	}

	/**
	 * 
	 */
	public boolean exportToFile(String _path, LinkedList<PlatformShape> shapes) {
		try {
			// Eksport pozycji gracza
			boolean player_exported = false;
			PrintWriter out = new PrintWriter(_path);

			// temperature weather
			out.println("0 1");
			for (PlatformInfo s : platforms) {
				if (s.mob_type == Mobs.Type.PLAYER) {
					out.println((int) s.x + " " + (int) s.y + " " + s.w
							+ "gracz_mikolaj.txt");
					player_exported = true;
					break;
				}
			}

			if (!player_exported) {
				out.println(0 + " " + 0 + " " + 23 + " gracz_robot.txt ");
			}
			// Wyliczanie mobów
			LinkedList<PlatformInfo> mobs = new LinkedList<PlatformInfo>();
			for (PlatformInfo s : platforms) {
				if (s.mob_type != null && s.mob_type != Mobs.Type.PLAYER) {
					mobs.add(s);
				}
			}
			// Wyliczanie skryptów
			LinkedList<PlatformInfo> scripts = new LinkedList<PlatformInfo>();
			for (PlatformInfo s : platforms) {
				if (s.isSet(Flag.SCRIPT)) {
					scripts.add(s);
				}
			}
			// Eksportowanie kształtów
			out.println(shapes.size());
			for (PlatformShape s : shapes) {
				out.println(s.getLabel());
			}
			// Eksportowanie platform
			out.println(platforms.size() - mobs.size() - scripts.size()
					- (player_exported ? 1 : 0));
			for (PlatformInfo platform : platforms) {
				if (platform.mob_type == null && !platform.isSet(Flag.SCRIPT)) {
					out.println(platform.toString());
				}
			}
			// Eksportowanie mobów
			out.println(mobs.size());
			for (PlatformInfo mob : mobs) {
				if (mob.mob_type != Mobs.Type.PLAYER) {
					out.println(mob.mob_type.val + " " + mob.x + " " + mob.y
							+ " " + mob.orientation + " " + mob.script_id + " "
							+ mob.flag + " ");
				}
			}
			// Eksportowanie skryptów
			out.println(scripts.size());
			for (PlatformInfo script : scripts) {
				out.println(script.x + " " + script.y + " " + script.w + " "
						+ script.h + " "
						+ script.script.replaceAll("[\n\r]", " "));
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean
			importFromFile(String _path, LinkedList<PlatformShape> shapes) {
		try {
			FileReader fr = new FileReader(new File(_path));
			BufferedReader br = new BufferedReader(fr);

			// Wczytywanie parametrów mapy
			br.readLine();

			// Importowanie pozycji gracza
			String[] tok = br.readLine().split(" ");
			platforms.add(Mobs.getMob(Mobs.Type.PLAYER,
					Integer.valueOf(tok[0]),
					Integer.valueOf(tok[1]),
					1,
					0));

			// Importowanie kształtów
			int len = Integer.valueOf(br.readLine());
			for (int i = 0; i < len; i++) {
				PlatformShape shape = new PlatformShape();
				shape.importFromFile(br.readLine());
				shapes.add(shape);
			}
			// Importowanie platform
			len = Integer.valueOf(br.readLine());
			for (int i = 0; i < len; i++) {
				platforms.add(PlatformInfo.getFromString(br.readLine(), shapes));
			}
			// Importowanie mobów
			len = Integer.valueOf(br.readLine());
			for (int i = 0; i < len; i++) {
				tok = br.readLine().split(" ");
				PlatformInfo mob = Mobs.getMob(Mobs.Type.getFromIndex(Integer.valueOf(tok[0])),
						Double.valueOf(tok[1]).intValue(),
						Double.valueOf(tok[2]).intValue(),
						1,
						Integer.valueOf(tok[3]));
				mob.script_id = Integer.valueOf(Integer.valueOf(tok[4]));
				if (mob.script_id == -1) {
					mob.script_id = Editor.script_id_counter++;
				} else {
					Editor.script_id_counter = mob.script_id + 1;
				}
				mob.flag = Integer.valueOf(tok[5]);
				platforms.add(mob);
			}
			// Importowanie skryptów
			len = Integer.valueOf(br.readLine());
			for (int i = 0; i < len; i++) {
				tok = br.readLine().split(" ");
				PlatformInfo info = new PlatformInfo(Float.valueOf(tok[0]),
						Float.valueOf(tok[1]),
						Float.valueOf(tok[2]),
						Float.valueOf(tok[3]),
						Color.white,
						Flag.SCRIPT.getFlag(),
						0);
				String script = "";
				for (int j = 4; j < tok.length; j++) {
					script += tok[j] + " ";
				}
				info.script = script;
				platforms.add(info);
			}
			br.close();
		} catch (Exception e) {
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
