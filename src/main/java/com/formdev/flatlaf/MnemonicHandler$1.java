package com.formdev.flatlaf;

import com.formdev.flatlaf.MnemonicHandler;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class MnemonicHandler$1
extends WindowAdapter {
    MnemonicHandler$1() {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        altPressedEventCount = 0;
        selectMenuOnAltReleased = false;
        EventQueue.invokeLater(() -> MnemonicHandler.showMnemonics(false, null));
    }
}
