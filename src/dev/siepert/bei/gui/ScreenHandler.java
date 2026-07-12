package dev.siepert.bei.gui;

public class ScreenHandler {
	public final int x, y, w, h;
	public final String[] categoryUIDs;

	public ScreenHandler(int x, int y, int w, int h, String[] categoryUIDs) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.categoryUIDs = categoryUIDs;
	}
}
