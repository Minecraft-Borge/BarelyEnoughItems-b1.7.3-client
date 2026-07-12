package dev.siepert.bei.gui;

import dev.siepert.bei.BarelyEnoughItems;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiContainer;

public class GuiWrapperList extends GuiContainer {
	private final GuiContainer screen;

	public GuiWrapperList(GuiContainer screen) {
		super(screen.inventorySlots);
		this.screen = screen;

		try {
			this.xSize = BarelyEnoughItems.xSizeField.getInt(screen);
			this.ySize = BarelyEnoughItems.ySizeField.getInt(screen);
		} catch (Exception e) {
			throw new RuntimeException("Field problems", e);
		}
	}

	@Override
	public void initGui() {
		this.screen.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		this.screen.drawScreen(mouseX, mouseY, partialTick);
	}

	@Override
	public void handleInput() {
		this.screen.handleInput();
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		this.screen.setWorldAndResolution(mc, width, height);
	}

	@Override
	public void drawBackground(int type) {
		this.screen.drawBackground(type);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return this.screen.doesGuiPauseGame();
	}

	@Override
	public void updateScreen() {
		this.screen.updateScreen();
	}

	@Override
	public void onGuiClosed() {
		this.screen.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick) {
		// This is not called
	}
}
