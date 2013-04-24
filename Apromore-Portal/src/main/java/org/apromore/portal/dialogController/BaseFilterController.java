package org.apromore.portal.dialogController;

public class BaseFilterController extends BaseController {

    private static final long serialVersionUID = -7879730927994569217L;

    private final MainController mainController;

    public BaseFilterController(MainController mainController) {
        super();
        this.mainController = mainController;
        setHflex("true");
        setVflex("true");
    }

    public MainController getMainController() {
        return mainController;
    }
}
