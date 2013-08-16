package org.apromore.portal.dialogController;

public class BaseDetailController extends BaseController {

    private static final long serialVersionUID = 602641424691365807L;

    private final MainController mainController;

    public BaseDetailController(MainController mainController) {
        super();
        this.mainController = mainController;
//        setHflex("true");
//        setVflex("true");
    }

    public MainController getMainController() {
        return mainController;
    }

}
