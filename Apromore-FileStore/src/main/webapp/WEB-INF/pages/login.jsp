<%@ page contentType="text/html" pageEncoding="UTF-8" session="false" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Apromore - File Store</title>

    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="keywords" content="Apromore, File store, webDAV"/>
    <meta name="description" content="Apromore File Store Service"/>
    <meta name="author" content="Cameron James">

    <!-- Styles -->
    <link href="<c:url value='/resources/css/apromore.css'/>" type="text/css" rel="stylesheet">
    <link href="<c:url value='/resources/css/login.css'/>" type="text/css" rel="stylesheet">
    <link href="<c:url value='/resources/css/bootstrap.css'/>" type="text/css" rel="stylesheet">
    <link href="<c:url value='/resources/css/bootstrap-responsive.css'/>" type="text/css" rel="stylesheet">
    <link href="<c:url value='/resources/css/font-awesome.css'/>" type="text/css" rel="stylesheet">
</head>
<body class="signin signin-horizontal">

    <div class="page-container">
        <div id="header-container">
            <div id="header">
                <div class="navbar-inverse navbar-fixed-top">
                    <div class="navbar-inner">
                        <div class="container"> </div>
                    </div>
                </div>

                <div class="header-drawer" style="height:3px"> </div>
            </div>
        </div>

        <div id="main-content" class="main-content container">
            <div id="page-content" class="page-content">

                <c:if test="${param['error']=='1'}">
                    <div>
                        <div class="row">
                            <div class="span6 offset3">
                                <div class="alert alert-error">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <i class="icon-remove-circle"></i> Login or password was incorrect. Please try again.
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${param['error']=='2'}">
                    <div>
                        <div class="row">
                            <div class="span6 offset3">
                                <div class="alert alert-error">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <i class="icon-remove-circle"></i> The page or component you request is no longer available.
                                    This is normally caused by timeout, opening too many Web pages, or rebooting
                                    the server.
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <div class="row">
                    <div class="tab-content overflow form-dark">

                        <div class="tab-pane fade in active" id="login">
                            <div class="span6 offset3">
                                <h4 class="welcome">
                                    <small>
                                        <i class="icon-user"></i>
                                        Login in
                                    </small>
                                </h4>
                                <form method="post" action="<c:url value='/j_spring_security_check'/>" name="login_form">
                                    <fieldset>
                                        <div class="controls">
                                            <label>
                                                <input class="span5" type="text" name="j_username" placeholder="Your Login or Email">
                                            </label>
                                        </div>
                                        <div class="controls controls-row">
                                            <label>
                                                <input class="span3" type="password" name="j_password" placeholder="password">
                                            </label>
                                            <button type="submit" class="span2 btn btn-primary btn-large">SIGN IN</button>
                                        </div>
                                        <hr class="margin-xm"/>
                                        <label class="checkbox" for="<c:url value='/_spring_security_remember_me'/>">
                                            <input name="_spring_security_remember_me" class="checkbox" type="checkbox">
                                            Remember me
                                        </label>
                                    </fieldset>
                                </form>
                            </div>

                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <footer class="footer">
        <div class="web-description span12">
            <h5>&copy; 2009-2013, The Apromore Initiative.</h5>
            <p>Except where otherwise noted, content on this site is licensed under a <a href="http://creativecommons.org/licenses/by-nc-nd/3.0/">Creative Commons Licence</a></p>
            <p class="cc-logo"></p>
        </div>
    </footer>


    <!-- ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<c:url value='/resources/js/jquery.js'/>"></script>
    <script src="<c:url value='/resources/js/bootstrap.js'/>"></script>
</body>
</html>
