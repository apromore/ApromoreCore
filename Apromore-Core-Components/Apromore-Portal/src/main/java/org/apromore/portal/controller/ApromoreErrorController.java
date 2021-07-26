package org.apromore.portal.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApromoreErrorController implements ErrorController {

  @RequestMapping("/error")
  public String getErrorPage(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (status != null) {
      Integer statusCode = Integer.valueOf(status.toString());

      if (statusCode == HttpStatus.NOT_FOUND.value()) {
        return "pages/404";
      } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
        return "pages/500";
      } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
        return "pages/401";
      }
    }
    return "pages/500";
  }

  @Override
  public String getErrorPath() {
    // TODO Auto-generated method stub
    return "/error";
  }

}
