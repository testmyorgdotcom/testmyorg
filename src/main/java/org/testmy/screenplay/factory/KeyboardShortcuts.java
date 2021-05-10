package org.testmy.screenplay.factory;

import org.testmy.screenplay.act.interaction.keyboard.Ctrl;

import net.serenitybdd.screenplay.Interaction;

public class KeyboardShortcuts {
    public static Interaction save() {
        return Ctrl.plus("s");
    }
}
