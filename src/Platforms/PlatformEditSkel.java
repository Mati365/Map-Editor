package Platforms;

import java.awt.Color;
import java.awt.Graphics2D;

public class PlatformEditSkel {
	private PlatformInfo	platform;

	/**
	 * 
	 */
	public PlatformInfo getPlatform() {
		return platform;
	}

	public void setPlatform(PlatformInfo platform) {
		this.platform = platform;
	}

	public boolean getDragg(float x, float y, int button) {
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
					(int) platform.y + (int) platform.h - 10,
					10,
					10);
			g2.setColor(Color.CYAN);
			g2.drawRect((int) platform.x - 1,
					(int) platform.y - 1,
					(int) platform.w + 2,
					(int) platform.h + 2);
		}
	}
}
