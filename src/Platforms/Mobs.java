package Platforms;

public class Mobs {
	public enum Type {
		SCORE(0), HEALTH(1), GHOST(2), GUN(4), GREEN_GUN(5), PLAYER(6);

		public int	val;

		Type(int _val) {
			val = _val;
		}

		public static Type getFromIndex(int index) {
			for (Type type : Type.values()) {
				if(type.val == index) {
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
		shapes[Type.GREEN_GUN.val] = new PlatformShape("Mobs/bron.txt");
		shapes[Type.PLAYER.val] = new PlatformShape("Mobs/obiekt.txt");
	}

	public static PlatformInfo getMob(Type type, float x, float y, int level) {
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
		switch (type) {
			case SCORE:
				platform.fitToWidth(26);
				break;
			/**
				 * 
				 */
			case HEALTH:
				platform.fitToWidth(32);
				break;
			/**
				 * 
				 */
			case GHOST:
				platform.fitToWidth(90);
				break;
			/**
				 * 
				 */
			case GUN:
			case GREEN_GUN:
				platform.fitToWidth(32);
				break;
			/**
				 * 
				 */
			case PLAYER:
				platform.fitToWidth(36);
				break;
		}
		platform.resize_lock = true;
		platform.mob_type = type;
		return platform;
	}
}
