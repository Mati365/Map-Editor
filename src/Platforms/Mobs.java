package Platforms;

public class Mobs {
	public enum Type {
		SCORE(0), HEALTH(1), GHOST(2), GUN(4), PLAYER(5), SPIKES(6), LADDER(7);

		public int	val;

		Type(int _val) {
			val = _val;
		}

		public static Type getFromIndex(int index) {
			for (Type type : Type.values()) {
				if (type.val == index) {
					return type;
				}
			}
			return null;
		}
	}

	static PlatformShape[]	shapes	= new PlatformShape[Type.values().length + 1];

	private static void loadTextures() {
		shapes[Type.SCORE.val] = new PlatformShape("Mobs/punkt.txt");
		shapes[Type.HEALTH.val] = new PlatformShape("Mobs/zycie.txt");
		shapes[Type.GHOST.val] = new PlatformShape("Mobs/wrog.txt");
		shapes[Type.GUN.val] = new PlatformShape("Mobs/bron.txt");
		shapes[Type.PLAYER.val] = new PlatformShape("Mobs/gracz.txt");
		//
		shapes[Type.SPIKES.val] = new PlatformShape("Mobs/kolce_gora.txt");
		shapes[Type.LADDER.val] = new PlatformShape("Mobs/drabina.txt");
	}

	public static PlatformInfo getMob(
			Type type,
			float x,
			float y,
			int level,
			int orientation) {
		if (shapes[0] == null) {
			loadTextures();
		}
		PlatformInfo platform = new PlatformInfo(x,
				y,
				0,
				0,
				null,
				PlatformInfo.Type.MOB.val,
				level);
		platform.setShape(shapes[type.val]);
		platform.orientation = orientation;

		switch (type) {
			case LADDER:
				platform.fitToWidth(24);
				break;
			/**
			 * 
			 */
			case SPIKES:
				platform.fitToWidth(24);
				break;
			/**
			 * 
			 */
			case SCORE:
				platform.fitToWidth(12);
				break;
			/**
				 * 
				 */
			case HEALTH:
				platform.fitToWidth(16);
				break;
			/**
				 * 
				 */
			case GHOST:
				platform.fitToWidth(23);
				break;
			/**
				 * 
				 */
			case GUN:
				platform.fitToWidth(16);
				break;
			/**
				 * 
				 */
			case PLAYER:
				platform.fitToWidth(23);
				break;
		}
		platform.resize_lock = true;
		platform.mob_type = type;
		platform.flag = PlatformInfo.Flag.NONE.getFlag();
		return platform;
	}
}
