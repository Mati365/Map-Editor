package Platforms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Types;
import java.util.LinkedList;

import Platforms.PlatformInfo.Type;

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
			for (PlatformInfo s : platforms) {
				if (s.mob_type == Mobs.Type.PLAYER) {
					out.println((int) s.x + " " + (int) s.y + " 42 ");
					player_exported = true;
					break;
				}
			}
			if (!player_exported) {
				out.println(0 + " " + 0);
			}
			//
			LinkedList<PlatformInfo> mobs = new LinkedList<PlatformInfo>();
			for (PlatformInfo s : platforms) {
				if (s.mob_type != null && s.mob_type != Mobs.Type.PLAYER) {
					mobs.add(s);
				}
			}
			// Eksportowanie kształtów
			out.println(shapes.size());
			for (PlatformShape s : shapes) {
				out.println(s.getLabel());
			}
			// Eksportowanie platform
			out.println(platforms.size() - mobs.size()
					- (player_exported ? 1 : 0));
			for (PlatformInfo platform : platforms) {
				if (platform.mob_type == null) {
					out.println(platform.toString());
				}
			}
			// Eksportowanie mobów
			out.println(mobs.size());
			for (PlatformInfo mob : mobs) {
				if (mob.mob_type != Mobs.Type.PLAYER) {
					out.println(mob.mob_type.val + " " + mob.x + " " + mob.y
							+ " null ");
				}
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

			// Importowanie pozycji gracza
			String[] tok = br.readLine().split(" ");
			platforms.add(Mobs.getMob(Mobs.Type.PLAYER,
					Integer.valueOf(tok[0]),
					Integer.valueOf(tok[1]),
					1));

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
				platforms.add(Mobs.getMob(Mobs.Type.getFromIndex(Integer.valueOf(tok[0])),
						Double.valueOf(tok[1]).intValue(),
						Double.valueOf(tok[2]).intValue(),
						1));
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
