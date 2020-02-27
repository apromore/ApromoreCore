// depends on common/js/index.js

Ap.login.decodeEmail = function(tries) {
  if (tries === 2) {
    return;
  }

  try {
    $("#wrapper").addClass("palette-" + (Math.floor(Math.random() * 3) + 1));
    let contact = $(".ap-contact-link"); // "6d6f632e65726f6d6f727061406f666e69"
    let coded = contact.attr("href");
    let href = [...Array(coded.length / 2).keys()]
      .map(x =>
        String.fromCharCode(parseInt(coded.substring(2 * x, 2 * (x + 1)), 16))
      )
      .reverse()
      .join("");
    contact.attr("href", "mailto:" + href);
    contact.text(href);
  } catch (e) {
    // pass
    tries++;
    setTimeout(function() {
      Ap.login.decodeEmail(tries);
    }, 500);
  }
};

zk.afterMount(function() {
  setTimeout(function() {
    Ap.login.decodeEmail(0);
  }, 100);
});
