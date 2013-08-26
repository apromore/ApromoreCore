package org.apromore.portal.dialogController;

import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class ShortMessageController extends Window {

    private Window shortmessage;
    private Label message;
    private Image image;

    public ShortMessageController(Window win) {
        this.shortmessage = win;
        this.message = (Label) this.shortmessage.getFellow("message");
        this.image = (Image) this.shortmessage.getFellow("image");
    }

    public void displayMessage(String mes) {
        this.message.setValue(mes);
        this.image.setVisible(true);
    }

    public void eraseMessage() {
        this.message.setValue("no messages");
        this.image.setVisible(false);
    }
}
