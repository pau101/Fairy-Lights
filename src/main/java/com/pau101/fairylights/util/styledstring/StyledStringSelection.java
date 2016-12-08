package com.pau101.fairylights.util.styledstring;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.gui.FontRenderer;

public class StyledStringSelection implements Transferable {
	public static final DataFlavor FLAVOR = new DataFlavor(StyledString.class, "Styled String");

	private static final DataFlavor[] FLAVORS = { FLAVOR, DataFlavor.stringFlavor, DataFlavor.fragmentHtmlFlavor };

	private final FontRenderer font;

	private final StyledString value;

	public StyledStringSelection(FontRenderer font, StyledString value) {
		this.font = font;
		this.value = value;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return FLAVORS.clone();
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return ArrayUtils.contains(FLAVORS, flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.equals(FLAVOR)) {
			return value;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			return value == null ? "" : value.toUnstyledString();
		} else if (flavor.equals(DataFlavor.fragmentHtmlFlavor)) {
			return StyledString.toHTMLFragment(font, value);
		}
		throw new UnsupportedFlavorException(flavor);
	}
}
