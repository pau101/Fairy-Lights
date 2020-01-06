package me.paulf.fairylights.util.styledstring;

import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class StyledStringSelection implements Transferable {
    public static final DataFlavor FLAVOR = new DataFlavor(StyledString.class, "Styled String");

    private static final DataFlavor[] FLAVORS = {FLAVOR, DataFlavor.stringFlavor, DataFlavor.fragmentHtmlFlavor};

    private final FontRenderer font;

    private final StyledString value;

    public StyledStringSelection(final FontRenderer font, final StyledString value) {
        this.font = font;
        this.value = value;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return FLAVORS.clone();
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return ArrayUtils.contains(FLAVORS, flavor);
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(FLAVOR)) {
            return this.value;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
            return this.value == null ? "" : this.value.toUnstyledString();
        } else if (flavor.equals(DataFlavor.fragmentHtmlFlavor)) {
            return StyledString.toHTMLFragment(this.font, this.value);
        }
        throw new UnsupportedFlavorException(flavor);
    }
}
