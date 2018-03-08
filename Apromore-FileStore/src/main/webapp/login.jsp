<%@ page contentType="text/html" pageEncoding="UTF-8" session="false" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Apromore - WebDav</title>

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
                <c:if test="${param['error'] == '3'}">
                    <div>
                        <div class="row">
                            <div class="span6 offset3">
                                <div class="alert alert-error">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <i class="icon-remove-circle"></i> Apromore Failed to process your request. Please try again.
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${param['success'] == '1'}">
                    <div>
                        <div class="row">
                            <div class="span6 offset3">
                                <div class="alert alert-success">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <i class="icon-remove-circle"></i> User Registration Accepted. Please Login.
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${param['success'] == '2'}">
                    <div>
                        <div class="row">
                            <div class="span6 offset3">
                                <div class="alert alert-success">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <i class="icon-remove-circle"></i> Password Reset. Please check your email account for a new password.
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <div class="row">
                    <div class="tab-content overflow form-dark">

                        <div class="tab-pane fade active in" id="login">
                            <div class="span6 offset3">
                                <h4 class="welcome">
                                    <small>
                                        <i class="icon-user"></i>
                                        Login
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
                                            <input class="span3" type="password" name="j_password" placeholder="password">
                                            <button type="submit" class="span2 btn btn-primary">SIGN IN</button>
                                        </div>
                                        <hr/>
                                        <label class="checkbox" for="<c:url value='/_spring_security_remember_me'/>">
                                            <input name="_spring_security_remember_me" class="checkbox" type="checkbox">
                                            Remember me
                                        </label>
                                        <a href="#forgot" class="btn" data-toggle="tab">Forgot Password?</a>
                                        <a href="#register" class="btn" data-toggle="tab">No account yet? Register now for free!</a>
                                    </fieldset>
                                </form>
                            </div>
                        </div>

                        <div class="tab-pane fade" id="forgot">
                            <div class="span6 offset3">
                                <h4 class="welcome">
                                    <small>
                                        Forgot your password
                                        <i class="icon-question"></i>
                                    </small>
                                </h4>
                                <form method="post" action="register/resetPassword" name="reset_form">
                                    <fieldset>
                                        <div class="input-append">
                                            <input class="span5" type="text" name="username" placeholder="Your Email Address"/>
                                        </div>
                                        <hr/>
                                        <button class="btn-block btn btn-primary" type="submit">Reset Password</button>
                                        <hr/>
                                        <p>Have you remembered? <a href="#login" class="btn" data-toggle="tab">Try to log in again.</a></p>
                                    </fieldset>
                                </form>
                            </div>
                        </div>

                        <div class="tab-pane fade" id="register">
                            <div class="offset3 span6">
                                <h4 class="welcome">
                                    <small>
                                        <i class="icon-user"></i>
                                        New User Registration
                                    </small>
                                </h4>
                                <form method="post" action="register/newUserRegister" name="newuser_form">
                                    <fieldset>
                                        <legend class="two"><span>Account information</span></legend>
                                        <div class="input-append">
                                            <input class="span4" type="text" name="firstname" placeholder="First Name"/>
                                            <span class="add-on">*</span>
                                        </div>
                                        <br/>
                                        <div class="input-append">
                                            <input class="span4" type="text" name="surname" placeholder="surname"/>
                                            <span class="add-on">*</span>
                                        </div>
                                        <br/>
                                        <div class="input-append">
                                            <input class="span4" type="text" name="username" placeholder="username"/>
                                            <span class="add-on">*</span>
                                        </div>
                                        <br/>
                                        <div class="input-append">
                                            <input class="span4" type="text" name="email" placeholder="email address"/>
                                            <span class="add-on">*</span>
                                        </div>
                                    </fieldset>
                                    <fieldset>
                                        <legend class="two"><span>Password</span></legend>
                                        <div class="input-group input-append">
                                            <input class="span4" type="password" name="password" placeholder="password"/>
                                            <span class="add-on">*</span>
                                        </div>
                                        <br/>
                                        <div class="input-group input-append">
                                            <input class="span4" type="password" name="confirmPassword" placeholder="confirm password"/>
                                            <span class="add-on">*</span>
                                        </div>
                                    </fieldset>
                                    <fieldset>
                                        <legend class="two"><span>Password Security</span></legend>
                                        <div class="input-append">
                                            <select name="securityQuestion" class="span4">
                                                <option value="What was your childhood nickname?">What was your childhood nickname?</option>
                                                <option value="In what city did you meet your spouse/significant other?">In what city did you meet your spouse/significant other?</option>
                                                <option value="What is the name of your favorite childhood friend?">What is the name of your favorite childhood friend?</option>
                                                <option value="What street did you live on in third grade?">What street did you live on in third grade?</option>
                                                <option value="What is the middle name of your oldest child?">What is the middle name of your oldest child?</option>
                                                <option value="What is your oldest sibling's middle name?">What is your oldest sibling's middle name?</option>
                                            </select>
                                        </div>
                                        <br/>
                                        <div class="input-append">
                                            <input class="span4" type="text" name="securityAnswer" placeholder="Answer for your security question"/>
                                        </div>
                                        <hr/>
                                        <button type="submit" class="btn-block btn btn-primary btn-larg">Register</button>
                                        <hr/>
                                        <p>Have you remembered? <a href="login.html" class="btn">Try to log in again.</a></p>
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
            <h5>&copy; 2009-2018, The Apromore Initiative.</h5>
            <p>Except where otherwise noted, content on this site is licensed under a <a href="http://creativecommons.org/licenses/by-nc-nd/3.0/">Creative Commons Licence</a></p>
            <p class="cc-logo"></p>
            <h:p>
                All models and other information stored in the repository available at the above nodes, are third-party materials that have been uploaded by other users, unless expressly indicated otherwise by the Hosting University (University of Melbourne, University of Tartu, Queensland University of Technologyor University of Camerino). The Hosting University makes the repository material available ‘as is’ and does not check the currency, accuracy, reliability, performance, completeness, suitability or workability of the repository material or whether repository materials uploaded by other users infringe anyone’s intellectual property rights. You use the repository material at your own risk.
            </h:p>
            <h:p>
                To the fullest extent permitted by law, the Hosting University excludes all liability whether in contract, tort (including negligence), statute or otherwise for any direct or indirect losses, damages, expenses, claims and liability that you may incur as a result or arising from any use of the repository material. To the extent legislation does not allow particular liability to be excluded, it is limited to the fullest extent permitted by such legislation.
            </h:p>
        </div>
    </footer>


    <!-- ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<c:url value='/resources/js/jquery.js'/>"></script>
    <script src="<c:url value='/resources/js/bootstrap.js'/>"></script>
    </body>
</html>
