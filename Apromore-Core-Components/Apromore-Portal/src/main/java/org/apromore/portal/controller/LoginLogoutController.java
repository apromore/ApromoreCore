package org.apromore.portal.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class LoginLogoutController {


  @GetMapping("/")
  public RedirectView login(HttpServletRequest request) throws ServletException {
    return new RedirectView("/zkau/web/index.zul");

  }

  @GetMapping("/logout")
  public RedirectView logout(HttpServletRequest request) throws ServletException {
    request.logout();
    return new RedirectView("/zkau/web/index.zul");
  }

}
