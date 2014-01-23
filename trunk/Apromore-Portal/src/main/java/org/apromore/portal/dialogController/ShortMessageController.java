package org.apromore.portal.dialogController;

import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class ShortMessageController extends Window {

    private Label message;
    private Image image;

    public ShortMessageController(Window win) {
        this.message = (Label) win.getFellow("message");
        this.image = (Image) win.getFellow("image");
    }

    public void displayMessage(String mes) {
        this.message.setValue(mes);
        this.image.setVisible(false);
    }

    public void eraseMessage() {
        this.message.setValue("no messages");
        this.image.setVisible(false);
    }
}
