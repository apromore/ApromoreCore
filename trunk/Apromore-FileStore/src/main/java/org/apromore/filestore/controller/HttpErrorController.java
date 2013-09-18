package org.apromore.filestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Spring MVC Controller to handle HTTP Error code.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Controller
public class HttpErrorController {

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public void handle404() {
    }

    @RequestMapping(value = "/500", method = RequestMethod.GET)
    public void handle500() {
    }

}
