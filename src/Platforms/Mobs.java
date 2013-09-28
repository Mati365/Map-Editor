package Platforms;

public class Mobs {
	public enum Type {
		SCORE(0),
		HEALTH(1),
		GHOST(2),
		GUN(4),
		PLAYER(5),
		SPIKES(6),
		LADDER(7),
		LIANE(8),
		PORTAL_BEGIN(9),
		PORTAL_END(10);

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
		shapes[Type.LIANE.val] = new PlatformShape("Mobs/liana.txt");

		shapes[Type.PORTAL_BEGIN.val] = new PlatformShape("Mobs/portal_poczatek.txt");
		shapes[Type.PORTAL_END.val] = new PlatformShape("Mobs/portal_koniec.txt");
	}

	public static PlatformInfo	last_created	= null;

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
		/**
			 * 
			 */
			case LADDER:
				platform.fitToWidth(24);
				break;
			/**
				 * 
				 */
			case LIANE:
				platform.fitToWidth(16);
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

			/**
				 * 
				 */
			case PORTAL_BEGIN:
			case PORTAL_END:
				platform.fitToWidth(16);
				break;
		}
		platform.resize_lock = true;
		platform.mob_type = type;
		platform.flag = PlatformInfo.Flag.NONE.getFlag();
		if (type == Type.PORTAL_END) {
			if (last_created.mob_type == Type.PORTAL_BEGIN) {
				last_created.linked = platform;
				platform.linked = last_created;
			} else {
				return null;
			}
		}

		last_created = platform;

		return platform;
	}
}
