package org.apromore.portal.dialogController;

import org.apromore.model.UserType;
import org.apromore.portal.common.UserSessionManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;

public class LoginController extends BaseController {

    private Window signinWindow;
    private Button okWindowButton;
    private Button btnFacebook;
    private Button btnTwitter;
    private Button cancelWindowButton;
    private Textbox username;
    private Textbox passwd;

    public void onCreate() throws InterruptedException {
        try {
            if (UserSessionManager.getCurrentUser() != null) {
                Executions.sendRedirect("/index.zul");
            }

            if (Executions.getCurrent() != null) {
                HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();

                if (request != null && request.getParameter("code") != null && !request.getParameter("code").isEmpty()) {
                    try {
                        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory("323718794350124", "1e5eaff8335e554f3a04fdd99e5e2fa0");
                        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
                        AccessGrant accessGrant = oauthOperations.exchangeForAccess(request.getParameter("code"), "http://localhost:9000/Apromore-portal/login.zul", null);
                        //AccessGrant accessGrant = new AccessGrant(request.getParameter("code"));
                        Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);
                        Facebook facebook = connection.getApi();
                        FacebookProfile profile = facebook.userOperations().getUserProfile();

                        UserType user = new UserType();
                        user.setFirstName(profile.getFirstName());
                        user.setLastName(profile.getLastName());
                        user.setUsername(profile.getEmail());
                        user.setEmail(profile.getEmail());

                        UserType existingUser = getService().readUser(profile.getEmail());
                        if (existingUser == null) {
                            existingUser = getService().writeUser(user);
                        }
                        user.setId(existingUser.getId());

                        UserSessionManager.setCurrentUser(user);
                        Executions.sendRedirect("/index.zul");
                    } catch (Exception e) {
                        String message = null;
                        if (e.getMessage() == null) {
                            message = "Please contact Apromore's administrator";
                        } else {
                            message = e.getMessage();
                        }
                        e.printStackTrace();
                        Messagebox.show("Error occurred initialising facebook connection (" + message + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }

                if (request != null && request.getParameter("oauth_token") != null && !request.getParameter("oauth_token").isEmpty()) {
                    try {
                        TwitterConnectionFactory serviceProvider = new TwitterConnectionFactory("AQERyCD74siO1LuvY7VA", "HbH8QewEnXitJJWi2WlERrRhh8mwb3M3iteWt7lRs");
                        OAuth1Operations oauthOperations = serviceProvider.getOAuthOperations();

                        String token = request.getParameter("oauth_token").toString();
                        String verifier = request.getParameter("oauth_verifier").toString();

                        OAuthToken previousToken = (OAuthToken) Executions.getCurrent().getSession().getAttribute("twitter-token");

                        if (previousToken.getValue().equals(token)) {
                            AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(previousToken, verifier);

                            OAuthToken accessToken = serviceProvider.getOAuthOperations().exchangeForAccessToken(authorizedRequestToken, null);
                            Connection<Twitter> connection = serviceProvider.createConnection(accessToken);
                            Twitter twitter = connection.getApi();
                            TwitterProfile profile = twitter.userOperations().getUserProfile();
                            UserType user = new UserType();
                            String fullName = profile.getName();
                            String[] names = fullName.split(" ");
                            if (names.length > 0) {
                                user.setFirstName(names[0]);
                            } else {
                                user.setFirstName("");
                            }
                            if (names.length > 1) {
                                user.setLastName(names[1]);
                            } else {
                                user.setLastName("");
                            }
                            user.setUsername(profile.getScreenName());
                            user.setEmail(profile.getScreenName());

                            UserType existingUser = getService().readUser(profile.getScreenName());
                            if (existingUser == null) {
                                existingUser = getService().writeUser(user);
                            }
                            user.setId(existingUser.getId());

                            UserSessionManager.setCurrentUser(user);
                            Executions.sendRedirect("/index.zul");
                        }
                    } catch (Exception e) {
                        String message = null;
                        if (e.getMessage() == null) {
                            message = "Please contact Apromore's administrator";
                        } else {
                            message = e.getMessage();
                        }
                        e.printStackTrace();
                        Messagebox.show("Error occurred initialising twitter connection (" + message + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }
            }

            this.signinWindow = (Window) this.getFellow("signinWindow");
            this.username = (Textbox) this.getFellow("username");
            this.passwd = (Textbox) this.getFellow("passwd");
            this.okWindowButton = (Button) this.getFellow("okWindowButton");
            this.btnFacebook = (Button) this.getFellow("btnFacebook");
            this.btnTwitter = (Button) this.getFellow("btnTwitter");
            this.cancelWindowButton = (Button) this.signinWindow.getFellow("cancelWindowButton");

            okWindowButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    signin();
                }
            });
            signinWindow.addEventListener("onOK", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    signin();
                }
            });
            cancelWindowButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            btnFacebook.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    facebook();
                }
            });
            btnTwitter.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    twitter();
                }
            });
        } catch (Exception e) {
            String message = null;
            if (e.getMessage() == null) {
                message = "Please contact Apromore's administrator";
            } else {
                message = e.getMessage();
            }
            e.printStackTrace();
            Messagebox.show("Repository not available (" + message + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Perform sign out of the current user
     */
    private void cancel() {
        this.username.setValue("");
        this.passwd.setValue("");
    }

    private void signin() throws InterruptedException {
        try {
            String username = this.username.getValue();
            String password = this.passwd.getValue();//hashPassword(this.passwd.getValue());

            password = hashPassword(password);
            UserType user = getService().login(username, password);
            if (user == null) {
                Messagebox.show("Invalid username/password", "Attention", Messagebox.OK, Messagebox.ERROR);
            } else {
                UserSessionManager.setCurrentUser(user);
                Executions.sendRedirect("/index.zul");
            }
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void facebook() {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory("323718794350124", "1e5eaff8335e554f3a04fdd99e5e2fa0");
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.add("scope", "email");
        params.setRedirectUri("http://localhost:8080/Apromore-portal/login.zul");
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
        Executions.sendRedirect(authorizeUrl);

    }

    private void twitter() {
        TwitterServiceProvider serviceProvider = new TwitterServiceProvider("AQERyCD74siO1LuvY7VA", "HbH8QewEnXitJJWi2WlERrRhh8mwb3M3iteWt7lRs");
        OAuth1Operations oauthOperations = serviceProvider.getOAuthOperations();
        OAuthToken requestToken = oauthOperations.fetchRequestToken("http://127.0.0.1:8080/Apromore-portal/login.zul", null);
        Executions.getCurrent().getSession().setAttribute("twitter-token", requestToken);
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);
        Executions.sendRedirect(authorizeUrl);
    }

    public String hashPassword(String password) {
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
        } catch (NoSuchAlgorithmException nsae) {
            // ignore
        }
        return hashword;
    }
}
