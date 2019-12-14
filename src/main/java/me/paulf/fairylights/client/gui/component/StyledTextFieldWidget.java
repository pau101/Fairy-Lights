package me.paulf.fairylights.client.gui.component;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GlStateManager;
import me.paulf.fairylights.client.gui.EditLetteredConnectionScreen;
import me.paulf.fairylights.util.styledstring.Style;
import me.paulf.fairylights.util.styledstring.StyledString;
import me.paulf.fairylights.util.styledstring.StyledStringBuilder;
import me.paulf.fairylights.util.styledstring.StyledStringSelection;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class StyledTextFieldWidget extends Widget implements IRenderable, IGuiEventListener {
	private static final DataFlavor RTF_FLAVOR = new DataFlavor("text/rtf", "Rich Text Format"); 

	private static final Predicate<String> ALWAYS_TRUE = str -> true;

	private static final Function<Character, Character> IDENTITY_CHARACTER_TRANSFORMER = c -> c;

	private final FontRenderer font;

	private final int multiClickInterval = getMultiClickInterval();

	private final ColorButton colorBtn;

	private final ToggleButton boldBtn;

	private final ToggleButton italicBtn;

	private final ToggleButton underlineBtn;

	private final ToggleButton strikethroughBtn;

	private StyledString value;

	private int maxLength = 32;

	private int tick;

	private boolean hasBackground = true;

	private boolean isBlurable = true;

	private boolean isFocused;

	private boolean isWritable = true;

	private boolean isVisible = true;

	private int lineScrollOffset;

	private int caret;

	private int selectionEnd;

	private int writableTextColor = 0xE0E0E0;

	private int readonlyTextColor = 0x707070;

	private boolean isDraggingSelection;

	private boolean hasDraggedSelecton;

	private boolean isPressed;

	private long lastClickTime;

	private int multiClicks;

	private Function<Character, Character> charInputTransformer = IDENTITY_CHARACTER_TRANSFORMER;

	private Predicate<String> validator = ALWAYS_TRUE;

	private List<ChangeListener> changeListeners = new ArrayList<>(); 

	private Style currentStyle;

	public StyledTextFieldWidget(
		FontRenderer font,
		ColorButton colorBtn,
		ToggleButton boldBtn,
		ToggleButton italicBtn,
		ToggleButton underlineBtn,
		ToggleButton strikethroughBtn,
		int x, int y, int width, int height,
		String msg
	) {
		super(x, y, width, height, msg);
		this.font = font;
		this.colorBtn = colorBtn;
		this.boldBtn = boldBtn;
		this.italicBtn = italicBtn;
		this.underlineBtn = underlineBtn;
		this.strikethroughBtn = strikethroughBtn;
		setValue0(new StyledString());
		setStyle(new Style());
	}

	public void setIsBlurable(boolean isBlurable) {
		this.isBlurable = isBlurable;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void updateStyling(TextFormatting styling, boolean state) {
		int start = caret;
		int end = selectionEnd;
		if (start == end) {
			withStyling(styling, state);
		} else {
			if (end < start) {
				int t = start;
				start = end;
				end = t;
			}
			setValue0(value.addStyling(start, end, styling, state));
			updateSelectionControls();
		}
	}

	public void setColor(TextFormatting color) {
		setStyle(currentStyle.withColor(color));
	}

	public void withStyling(TextFormatting styling, boolean state) {
		setStyle(currentStyle.withStyling(styling, state));
	}

	public void setStyle(Style style) {
		currentStyle = style;
		colorBtn.setDisplayColor(currentStyle.getColor());
		boldBtn.setValue(style.isBold());
		italicBtn.setValue(style.isItalic());
		underlineBtn.setValue(style.isUnderline());
		strikethroughBtn.setValue(style.isStrikethrough());
	}

	public TextFormatting getColor() {
		return currentStyle.getColor();
	}

	public void setValue(String value) {
		setValue(new StyledString(value, currentStyle));
	}

	public void setValue(StyledString value) {
		if (validator.test(value.toUnstyledString())) {
			if (value.length() > maxLength) {
				setValue0(value.substring(0, maxLength));
			} else {
				setValue0(value);
			}
		}
	}

	private void setValue0(StyledString value) {
		this.value = value;
		for (ChangeListener listener : changeListeners) {
			listener.onChange(value);
		}
	}

	public String getUnstyledValue() {
		return value.toUnstyledString();
	}

	public StyledString getValue() {
		return value;
	}

	public String getUnstyledSelectedText() {
		return getSelectedText().toUnstyledString();
	}

	public StyledString getSelectedText() {
		int start = caret, end = selectionEnd;
		if (end < start) {
			int t = start;
			start = end;
			end = t;
		}
		return value.substring(start, end);
	}

	public void setCaretStart() {
		setCaret(0);
	}

	public void setCaretEnd() {
		setCaret(value.length());
	}

	public void setCaret(int pos) {
		setCaret(pos, true);
	}

	public void setCaret(int pos, boolean changeColor) {
		caret = MathHelper.clamp(pos, 0, value.length());
		setSelectionPos(caret);
		if (changeColor) {
			setCurrentStyleByIndex(caret);
		}
		tick = 0;
	}

	private void setCurrentStyleByIndex(int index) {
		if (value.length() > 0) {
			setStyle(value.styleAt(index <= 0 ? 0 : index - 1));
		}
	}

	public int getCaret() {
		return caret;
	}

	public int getSelectionEnd() {
		return selectionEnd;
	}

	public Style getStyle() {
		return currentStyle;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setHasBackground(boolean hasBackground) {
		this.hasBackground = hasBackground;
	}

	public void setTextColor(int writableTextColor) {
		this.writableTextColor = writableTextColor & 0xFFFFFF;
	}

	public void setReadonlyTextColor(int readonlyTextColor) {
		this.readonlyTextColor = readonlyTextColor & 0xFFFFFF;
	}

	@Override
	public void setFocused(boolean isFocused) {
		if (isFocused) {
			if (!this.isFocused) {
				tick = 0;
			}
		} else if (selectionEnd != caret) {
			setSelectionPos(caret);
		}
		this.isFocused = isFocused;
	}

	@Override
	public boolean isFocused() {
		return isFocused;
	}

	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}

	public void setCharInputTransformer(Function<Character, Character> charTransformer) {
		this.charInputTransformer = charTransformer;
	}

	public void setValidator(Predicate<String> validator) {
		this.validator = validator;
	}

	public int getInnerWidth() {
		return hasBackground ? width - 8 : width;
	}

	public void setSelectionPos(int pos) {
		int len = value.length();
		selectionEnd = MathHelper.clamp(pos, 0, len);
		if (lineScrollOffset > len) {
			lineScrollOffset = len;
		}
		int w = value.substring(lineScrollOffset).trimToWidth(font, getInnerWidth(), true).length();
		if (selectionEnd > w + lineScrollOffset) {
			lineScrollOffset = selectionEnd - w;
		} else if (selectionEnd <= lineScrollOffset) {
			lineScrollOffset = selectionEnd;
		}
		lineScrollOffset = MathHelper.clamp(lineScrollOffset, 0, len);
		if (caret != selectionEnd) {
			updateSelectionControls();
		}
	}

	private void updateSelectionControls() {
		StyledString selected = getSelectedText();
		TextFormatting color = null;
		boolean consistantColor = true;
		boolean bold = true, italic = true, underline = true, strikethrough = true;
		for (int i = 0; i < selected.length(); i++) {
			Style s = selected.styleAt(i);
			if (color != null && color != s.getColor()) {
				color = null;
				consistantColor = false;
			}
			if (consistantColor) {
				color = s.getColor();
			}
			if (!s.isBold()) {
				bold = false;
			}
			if (!s.isItalic()) {
				italic = false;
			}
			if (!s.isUnderline()) {
				underline = false;
			}
			if (!s.isStrikethrough()) {
				strikethrough = false;
			}
		}
		setStyle(new Style(color == null ? currentStyle.getColor() : color, bold, strikethrough, underline, italic, false));
		if (!consistantColor) {
			colorBtn.removeDisplayColor();
		}
	}

	public void registerChangeListener(ChangeListener listener) {
		if (!changeListeners.contains(listener)) {
			changeListeners.add(listener);
		}
		listener.onChange(value);
	}

	public boolean removeChangeListener(ChangeListener listener) {
		return changeListeners.remove(listener);
	}

	public void update(int mouseX, int mouseY) {
		if (isPressed && caret != selectionEnd) {
			int lower, upper;
			if (hasBackground) {
				lower = x + 15;
				upper = x + width - 16;
			} else {
				lower = x + 11;
				upper = x + width - 12;
			}
			boolean scrolled = false;
			if (mouseX < lower) {
				if (lineScrollOffset > 0) {
					int rate = (2 - (mouseX - x) / 5) * 2 + 2;
					lineScrollOffset -= rate;
					if (lineScrollOffset < 0) {
						lineScrollOffset = 0;
					}
					scrolled = true;
				}
			} else if (mouseX > upper) {
				int max = value.length() - value.trimToWidth(font, getInnerWidth(), true).length();
				if (lineScrollOffset < max) {
					int rate = (2 + (mouseX - x - width + 1) / 5) * 2 + 2;
					lineScrollOffset += rate;
					if (lineScrollOffset > max) {
						lineScrollOffset = max;
					}
					scrolled = true;
				}
			}
			if (scrolled && !hasDraggedSelecton) {
				int relativeX = mouseX - x;
				if (hasBackground) {
					relativeX -= 2;
				}
				setSelectionPos(getIndexInTextByX(relativeX));
			}
		}
		tick++;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!isFocused) {
			return false;
		}
		if (Screen.isSelectAll(keyCode)) {
			setCaretEnd();
			setSelectionPos(0);
		} else if (Screen.isCopy(keyCode)) {
			setClipboardString(getSelectedText());
		} else if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_V) {
			if (isWritable) {
				StyledString str = getClipboardString();
				if (Screen.hasShiftDown()) {
					writeText(str.toUnstyledString());
				} else {
					writeText(str);
				}
			}
		} else if (Screen.isCut(keyCode)) {
			setClipboardString(getSelectedText());
			if (isWritable) {
				writeText("");
			}
		} else if (EditLetteredConnectionScreen.isControlOp(keyCode, GLFW.GLFW_KEY_BACKSLASH)) {
			if (caret == selectionEnd) {
				resetCurrentFormatting();
			} else {
				resetSelectedFormatting();
			}
		} else {
			switch (keyCode) {
				case GLFW.GLFW_KEY_BACKSPACE:
					if (Screen.hasControlDown()) {
						if (isWritable) {
							deleteWords(-1);
						}
					} else if (isWritable) {
						deleteFromCursor(-1);
					}
					break;
				case GLFW.GLFW_KEY_HOME:
					if (Screen.hasShiftDown()) {
						setSelectionPos(0);
					} else {
						setCaretStart();
					}
					break;
				case GLFW.GLFW_KEY_LEFT:
					if (Screen.hasShiftDown()) {
						if (Screen.hasControlDown()) {
							setSelectionPos(skipWords(-1, getSelectionEnd()));
						} else {
							setSelectionPos(getSelectionEnd() - 1);
						}
					} else if (Screen.hasControlDown()) {
						setCaret(skipWords(-1));
					} else {
						if (getSelectedText().isEmpty()) {
							moveCursorBy(-1);	
						} else {
							setCaret(Math.min(caret, selectionEnd));
						}
					}
					break;
				case GLFW.GLFW_KEY_RIGHT:
					if (Screen.hasShiftDown()) {
						if (Screen.hasControlDown()) {
							setSelectionPos(skipWords(1, getSelectionEnd()));
						} else {
							setSelectionPos(getSelectionEnd() + 1);
						}
					} else if (Screen.hasControlDown()) {
						setCaret(skipWords(1));
					} else {
						if (getSelectedText().isEmpty()) {
							moveCursorBy(1);	
						} else {
							setCaret(Math.max(caret, selectionEnd));
						}
					}
					break;
				case GLFW.GLFW_KEY_END:
					if (Screen.hasShiftDown()) {
						setSelectionPos(value.length());
					} else {
						setCaretEnd();
					}
					break;
				case GLFW.GLFW_KEY_DELETE:
					if (Screen.hasControlDown()) {
						if (isWritable) {
							deleteWords(1);
						}
					} else if (isWritable) {
						deleteFromCursor(1);
					}
					break;
				default:
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean charTyped(final char typedChar, final int keyCode) {
		if (!isFocused) {
			return false;
		}
		char writeChar = charInputTransformer.apply(typedChar);
		if (SharedConstants.isAllowedCharacter(writeChar)) {
			if (isWritable) {
				writeText(Character.toString(writeChar));
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
		if (isBlurable) {
			setFocused(hovered);
		} else if (!hovered) {
			setCaret(Math.min(caret, selectionEnd));
		}
		if (isFocused && hovered && button == 0) {
			int relativeX = MathHelper.floor(mouseX - x);
			if (hasBackground) {
				relativeX -= 2;
			}
			int idx = getIndexInTextByX(relativeX);
			long now = System.currentTimeMillis();
			if (now - lastClickTime <= multiClickInterval) {
				multiClicks++;
				if (multiClicks > 3) {
					multiClicks = 1;
				}
			} else {
				multiClicks = 1;
			}
			lastClickTime = now;
			if (Screen.hasShiftDown()) {
				int end = selectionEnd;
				setCaret(idx);
				setSelectionPos(end);
			} else {
				clickIndex(idx);	
			}
			isPressed = true;
			return true;
		}
		return false;
	}

	private void clickIndex(int pos) {
		switch (multiClicks) {
			case 1: {
				int start = caret, end = selectionEnd;
				if (end < start) {
					int t = start;
					start = end;
					end = t;
				}
				isDraggingSelection = caret != selectionEnd && pos >= start && pos < end;
				if (!isDraggingSelection) {
					setCaret(pos);
					hasDraggedSelecton = false;
				}
				break;
			}
			case 2: {
				if (pos < value.length() && value.charAt(pos) == ' ') {
					int low = pos - 1, high = pos, max = value.length() - 1;
					while (low >= 0) {
						if (value.charAt(low) != ' ') {
							low++;
							break;
						}
						low--;
					}
					while (true) {
						if (high < max) {
							if (value.charAt(++high) != ' ') {
								break;
							}	
						} else {
							high++;
							break;
						}
					}
					setCaret(high);
					setSelectionPos(low);
				} else {
					int low = value.lastIndexOf(' ', pos);
					int high = value.indexOf(' ', pos);
					setCaret(high == -1 ? value.length() : high);
					setSelectionPos(low + 1);
				}
				break;
			}
			case 3:
				setCaretEnd();
				setSelectionPos(0);
				break;
			default:
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			isPressed = false;
			if (isDraggingSelection) {
				boolean hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
				if (hovered) {
					int relativeX = MathHelper.floor(mouseX - x);
					if (hasBackground) {
						relativeX -= 2;
					}
					int pos = getIndexInTextByX(relativeX);
					// Is pos outside of selection
					if ((pos - caret) * (pos - selectionEnd) > 0) {
						StyledString selection = getSelectedText();
						writeText("");
						// If pos after selection
						if ((pos - caret) - (selectionEnd - pos) > 0) {
							pos -= selection.length();
						}
						setCaret(pos);
						writeText(selection);
						setCaret(pos);
						setSelectionPos(pos + selection.length());
					} else {
						setCaret(pos);
					}
				}
				isDraggingSelection = false;
				hasDraggedSelecton = false;
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		if (isFocused && isPressed && button == 0) {
			int relativeX = MathHelper.floor(mouseX - x);
			if (hasBackground) {
				relativeX -= 2;
			}
			if (isDraggingSelection) {
				hasDraggedSelecton = true;
			} else {
				setSelectionPos(getIndexInTextByX(relativeX));
			}
			return true;
		}
		return false;
	}

	public void writeText(String input) {
		if (!colorBtn.hasDisplayColor()) {
			setCurrentStyleByIndex(Math.min(selectionEnd, caret));
		}
		writeText(new StyledString(input, currentStyle), false);
	}

	public void writeText(StyledString input) {
		writeText(input, true);
	}

	public void writeText(StyledString input, boolean changeColor) {
		StyledStringBuilder val = new StyledStringBuilder();
		StyledString finput = filterAllowedCharacters(input);
		int startIdx = caret, endIdx = selectionEnd;
		if (endIdx < startIdx) {
			int t = startIdx;
			startIdx = endIdx;
			endIdx = t;
		}
		int available = maxLength - value.length() - (startIdx - endIdx);
		if (value.length() > 0) {
			val.append(value.substring(0, startIdx));
		}
		int length;
		if (available < finput.length()) {
			val.append(finput.substring(0, available));
			length = available;
		} else {
			val.append(finput);
			length = finput.length();
		}
		if (value.length() > 0 && endIdx < value.length()) {
			val.append(value.substring(endIdx));
		}
		StyledString v = val.toStyledString();
		if (validator.test(v.toUnstyledString())) {
			setValue0(v);
			moveCursorBy(startIdx - selectionEnd + length, changeColor);
		}
	}

	public static boolean isAllowedCharacter(char character) {
		return character >= ' ' && character != '\u007F';
	}

	private static StyledString filterAllowedCharacters(StyledString str) {
		StyledStringBuilder bldr = new StyledStringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char chr = str.charAt(i);
			if (isAllowedCharacter(chr)) {
				bldr.append(chr, str.rawStyleAt(i));
			}
		}
		return bldr.toStyledString();
	}

	public void deleteWords(int num) {
		deleteFromCursor(skipWords(num) - caret);
	}

	public void deleteFromCursor(int num) {
		if (value.length() > 0) {
			if (selectionEnd != caret) {
				writeText("");
			} else {
				boolean reverse = num < 0;
				int endIdx = reverse ? caret + num : caret;
				int startIdx = reverse ? caret : caret + num;
				StyledStringBuilder val = new StyledStringBuilder();
				Style style = null;
				if (endIdx > 0) {
					if (endIdx < value.length()) {
						style = value.styleAt(endIdx);
					}
					val.append(value.substring(0, endIdx));
				}
				if (startIdx < value.length()) {
					if (startIdx > 0 && style == null) {
						style = value.styleAt(startIdx - 1);
					}
					val.append(value.substring(startIdx));
				}
				StyledString v = val.toStyledString();
				if (validator.test(v.toUnstyledString())) {
					setValue0(v);
					if (reverse) {
						moveCursorBy(num, false);
					}
					if (style == null) {
						style = currentStyle;
					}
					setStyle(style);
				}
			}
		}
	}

	public int skipWords(int n) {
		return skipWords(n, n < 0 ? Math.min(selectionEnd, caret) : Math.max(selectionEnd, caret));
	}

	public int skipWords(int n, int pos) {
		int idx = pos;
		boolean reverse = n < 0;
		for (int word = 0, len = value.length(), count = Math.abs(n); word < count; word++) {
			if (reverse) {
				while (idx > 0 && value.charAt(idx - 1) == ' ') {
					idx--;
				}
				while (idx > 0 && value.charAt(idx - 1) != ' ') {
					idx--;
				}
				if (idx == 0) {
					break;
				}
			} else {
				while (idx < len && value.charAt(idx) == ' ') {
					idx++;
				}
				while (idx < len && value.charAt(idx) != ' ') {
					idx++;
				}
				if (idx == len) {
					break;
				}
			}
		}
		return idx;
	}

	public void moveCursorBy(int num) {
		setCaret(selectionEnd + num, true);
	}

	public void moveCursorBy(int num, boolean changeColor) {
		setCaret(selectionEnd + num, changeColor);
	}

	private int getIndexInTextByX(int x) {
		StyledString s = value.substring(lineScrollOffset).trimToWidth(font, getInnerWidth());
		return s.trimToWidth(font, x).length() + lineScrollOffset;
	}

	private void resetCurrentFormatting() {
		setStyle(new Style(StyledString.DEFAULT_COLOR));
		tick = 0;
	}

	private void resetSelectedFormatting() {
		int start = caret, end = selectionEnd;
		if (end < start) {
			int t = start;
			start = end;
			end = t;
		}
		setValue0(value.withStyling(start, end, StyledString.DEFAULT_COLOR));
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (!isVisible) {
			return;
		}
		if (hasBackground) {
			fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xAAA0A0A0);
			fill(x, y, x + width, y + height, 0xFF000000);
		}
		int textColor = isWritable ? writableTextColor : readonlyTextColor;
		int visibleCaret = caret - lineScrollOffset;
		int visibleSelectionEnd = selectionEnd - lineScrollOffset;
		StyledString visibleText = value.substring(lineScrollOffset).trimToWidth(font, getInnerWidth());
		boolean isCaretVisible = visibleCaret >= 0 && visibleCaret <= visibleText.length();
		boolean drawSelection = visibleSelectionEnd != visibleCaret;
		boolean drawCaret = !drawSelection && isFocused && tick / 6 % 2 == 0 && isCaretVisible;
		int offsetX = hasBackground ? x + 4 : x;
		int offsetY = hasBackground ? y + (height - 8) / 2 : y;
		int textX = offsetX;
		if (visibleSelectionEnd > visibleText.length()) {
			visibleSelectionEnd = visibleText.length();
		}
		if (visibleText.length() > 0) {
			String beforeCaret = (isCaretVisible ? visibleText.substring(0, visibleCaret) : visibleText).toString();
			textX = font.drawStringWithShadow(beforeCaret, offsetX, offsetY, textColor);
		}
		int caretX;
		if (isCaretVisible) {
			caretX = --textX;
		} else {
			caretX = visibleCaret > 0 ? offsetX + width - 6 : offsetX;
		}
		if (visibleText.length() > 0 && isCaretVisible && visibleCaret < visibleText.length()) {
			textX = font.drawStringWithShadow(visibleText.substring(visibleCaret).toString(), textX, offsetY, textColor);
		}
		if (drawCaret) {
			int rgb = StyledString.getColor(currentStyle.getColor());
			if (currentStyle.isItalic()) {
		        Tessellator tes = Tessellator.getInstance();
				BufferBuilder buf = tes.getBuffer();
		        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		        buf.pos(caretX + 2, offsetY - 2, 0).endVertex();
		        buf.pos(caretX + 1, offsetY - 2, 0).endVertex();
		        buf.pos(caretX - 1, offsetY + 1 + font.FONT_HEIGHT, 0).endVertex();
		        buf.pos(caretX, offsetY + 1 + font.FONT_HEIGHT, 0).endVertex();
		        GlStateManager.disableTexture();
		        GlStateManager.color3f((rgb >> 16 & 0xFF) / 255F, (rgb >> 8 & 0xFF) / 255F, (rgb & 0xFF) / 255F);
		        tes.draw();
		        GlStateManager.enableTexture();
			} else {
				fill(caretX, offsetY - 2, caretX + 1, offsetY + 1 + font.FONT_HEIGHT, 0xFF000000 | rgb);
			}
		}
		if (drawSelection) {
			int selectionX = offsetX + (visibleSelectionEnd < 0 ? 0 : visibleText.substring(0, visibleSelectionEnd).getWidth(font));
			int start = caretX, end = selectionX;
			if (end < start) {
				int t = start;
				start = end;
				end = t;
			}
			drawSelectionHighlight(start - 1, offsetY - 2, end, offsetY + 1 + font.FONT_HEIGHT);
		}
		if (hasDraggedSelecton) {
			if (isHovered) {
				int relativeX = mouseX - x;
				if (hasBackground) {
					relativeX -= 2;
				}
				int pos = getIndexInTextByX(relativeX) - lineScrollOffset;
				if (pos >= 0 && pos <= visibleText.length()) {
					int x = visibleText.substring(0, pos).getWidth(font);
					int rgb = StyledString.getColor(currentStyle.getColor());
					fill(offsetX + x, offsetY - 2, offsetX + x + 1, offsetY + 1 + font.FONT_HEIGHT, 0xFF000000 | rgb);
				}
			}
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			font.drawStringWithShadow(getSelectedText().toString(), mouseX + 5, mouseY + 5, textColor | 0xBF000000);
			GlStateManager.disableBlend();
		}
	}

	private void drawSelectionHighlight(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int t = startX;
			startX = endX;
			endX = t;
		}
		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}
		if (endX > x + width) {
			endX = x + width;
		}
		if (startX > x + width) {
			startX = x + width;
		}
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();
		GlStateManager.color3f(1, 1, 1);
		GlStateManager.disableTexture();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buf.pos(startX, endY, 0).endVertex();
		buf.pos(endX, endY, 0).endVertex();
		buf.pos(endX, endY + 1, 0).endVertex();
		buf.pos(startX, endY + 1, 0).endVertex();
		buf.pos(startX, startY - 1, 0).endVertex();
		buf.pos(endX, startY - 1, 0).endVertex();
		buf.pos(endX, startY, 0).endVertex();
		buf.pos(startX, startY, 0).endVertex();
		buf.pos(startX, endY, 0).endVertex();
		buf.pos(startX - 1, endY, 0).endVertex();
		buf.pos(startX - 1, startY, 0).endVertex();
		buf.pos(startX, startY, 0).endVertex();
		buf.pos(endX + 1, endY, 0).endVertex();
		buf.pos(endX, endY, 0).endVertex();
		buf.pos(endX, startY, 0).endVertex();
		buf.pos(endX + 1, startY, 0).endVertex();
		tes.draw();
		GlStateManager.enableTexture();
	}

	public StyledString getClipboardString() {
		try {
			Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			/*/
			Set<String> mimetypes = new HashSet<>();
			for (DataFlavor f : transferable.getTransferDataFlavors()) {
				mimetypes.add(f.getMimeType().substring(0, f.getMimeType().indexOf(';')));
			}
			System.out.printf("%s\n", mimetypes);//*/
			if (transferable != null) {
				if (transferable.isDataFlavorSupported(StyledStringSelection.FLAVOR)) {
					return (StyledString) transferable.getTransferData(StyledStringSelection.FLAVOR);	
				}
				if (transferable.isDataFlavorSupported(DataFlavor.fragmentHtmlFlavor)) {
					String fragment = (String) transferable.getTransferData(DataFlavor.fragmentHtmlFlavor);
					StyledString str = StyledString.fromHTMLFragment(font, fragment);
					if (str != null) {
						return str;
					}
				}
				if (transferable.isDataFlavorSupported(RTF_FLAVOR)) {
					try (InputStream in = (InputStream) transferable.getTransferData(RTF_FLAVOR)) {
						StyledString str = StyledString.fromRTF(in);
						if (str != null) {
							return str;
						}
					}
				}
				if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					String str = (String) transferable.getTransferData(DataFlavor.stringFlavor);
					if (str.indexOf('\u00a7') == -1) {
						return new StyledString(str, currentStyle);
					} else {
						return StyledString.valueOf(str);
					}
				}
			}
		} catch (Exception e) {}
		return new StyledString();
	}

	private void setClipboardString(StyledString value) {
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StyledStringSelection(font, value), null);
		} catch (Exception e) {}
	}

	private static int getMultiClickInterval() {
		return MoreObjects.firstNonNull((Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval"), 500);
	}

	@FunctionalInterface
	public interface ChangeListener {
		void onChange(StyledString value);
	}
}
