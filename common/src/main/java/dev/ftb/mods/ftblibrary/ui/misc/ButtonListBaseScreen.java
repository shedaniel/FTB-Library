package dev.ftb.mods.ftblibrary.ui.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetLayout;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

/**
 * @author LatvianModder
 */
public abstract class ButtonListBaseScreen extends BaseScreen {
	private final Panel panelButtons;
	private final PanelScrollBar scrollBar;
	private Component title = TextComponent.EMPTY;
	private final TextBox searchBox;
	private boolean hasSearchBox;
	private int borderH, borderV, borderW;

	public ButtonListBaseScreen() {
		panelButtons = new Panel(this) {
			@Override
			public void add(Widget widget) {
				if (!hasSearchBox || searchBox.getText().isEmpty() || getFilterText(widget).contains(searchBox.getText().toLowerCase())) {
					super.add(widget);
				}
			}

			@Override
			public void addWidgets() {
				addButtons(this);
			}

			@Override
			public void alignWidgets() {
				setY(hasSearchBox ? 23 : 9);
				int prevWidth = width;

				if (widgets.isEmpty()) {
					setWidth(100);
				} else {
					setWidth(100);

					for (Widget w : widgets) {
						setWidth(Math.max(width, w.width));
					}
				}

				if (width > ButtonListBaseScreen.this.width - 40) {
					width = ButtonListBaseScreen.this.width - 40;
				}

				if (hasSearchBox) {
					setWidth(Math.max(width, prevWidth));
				}

				for (Widget w : widgets) {
					w.setX(borderH);
					w.setWidth(width - borderH * 2);
				}

				setHeight(140);

				scrollBar.setPosAndSize(posX + width + 6, posY - 1, 16, height + 2);
				scrollBar.setMaxValue(align(new WidgetLayout.Vertical(borderV, borderW, borderV)));

				getGui().setWidth(scrollBar.posX + scrollBar.width + 8);
				getGui().setHeight(height + 18 + (hasSearchBox ? 14 : 0));

				if (hasSearchBox) {
					searchBox.setPosAndSize(8, 6, getGui().width - 16, 12);
				}
			}

			@Override
			public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
				theme.drawPanelBackground(matrixStack, x, y, w, h);
			}
		};

		panelButtons.setPosAndSize(9, 9, 0, 146);

		scrollBar = new PanelScrollBar(this, panelButtons);
		scrollBar.setCanAlwaysScroll(true);
		scrollBar.setScrollStep(20);

		searchBox = new TextBox(this) {
			@Override
			public void onTextChanged() {
				panelButtons.refreshWidgets();
			}
		};

		searchBox.ghostText = I18n.get("gui.search_box");
		hasSearchBox = false;
	}

	public void setHasSearchBox(boolean v) {
		if (hasSearchBox != v) {
			hasSearchBox = v;
			refreshWidgets();
		}
	}

	public String getFilterText(Widget widget) {
		return widget.getTitle().getString().toLowerCase();
	}

	@Override
	public void addWidgets() {
		add(panelButtons);
		add(scrollBar);

		if (hasSearchBox) {
			add(searchBox);
		}
	}

	@Override
	public void alignWidgets() {
		panelButtons.alignWidgets();
	}

	public abstract void addButtons(Panel panel);

	public void setTitle(Component txt) {
		title = txt;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	public void setBorder(int h, int v, int w) {
		borderH = h;
		borderV = v;
		borderW = w;
	}

	@Override
	public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		super.drawBackground(matrixStack, theme, x, y, w, h);

		Component title = getTitle();

		if (title != TextComponent.EMPTY) {
			theme.drawString(matrixStack, title, x + (width - theme.getStringWidth(title)) / 2, y - theme.getFontHeight() - 2, Theme.SHADOW);
		}
	}

	public void focus() {
		searchBox.setFocused(true);
	}
}