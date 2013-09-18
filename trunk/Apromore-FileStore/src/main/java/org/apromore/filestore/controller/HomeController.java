package org.apromore.filestore.controller;

import org.apromore.filestore.client.FileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Spring Controller to handle getting and displaying the user picked folder.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Controller
public class HomeController {

    @Autowired
    @Qualifier("fileStoreClient")
    private FileStoreService fileStoreService;


    /**
     * This is the entry for the home page.
     * Since we are only hitting this page at the start we have no data so auto pick the root to display.
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String home(final Model model) throws Exception {
        model.addAttribute("contents", fileStoreService.list("/"));
        return "index";
    }

}
