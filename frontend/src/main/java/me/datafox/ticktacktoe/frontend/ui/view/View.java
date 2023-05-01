package me.datafox.ticktacktoe.frontend.ui.view;

import me.datafox.ticktacktoe.frontend.ui.element.BaseTable;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;

/**
 * @author datafox
 */
public abstract class View extends BaseTable {
    protected final TitleScreen parent;

    protected View(TitleScreen parent) {
        this.parent = parent;
    }
}
