// depends on common/js/index.js

(function() {
  Ap.login.decodeEmail = function(tries) {
    if (tries === 2) {
      return;
    }

    try {
      $('#wrapper').addClass('palette-' + (Math.floor(Math.random() * 3) + 1));
      let contact = $('.ap-contact-link'); // "6d6f632e65726f6d6f727061406f666e69"
      let coded = contact.attr('href');
      let href = [...Array(coded.length / 2).keys()].map(x =>
          String.fromCharCode(parseInt(coded.substring(2 * x, 2 * (x + 1)), 16)),
      ).reverse().join('');
      contact.attr('href', 'mailto:' + href);
      contact.text(href);
    } catch (e) {
      // pass
      tries++;
      setTimeout(function() {
        Ap.login.decodeEmail(tries);
      }, 500);
    }
  };

  let preferredCountries = ['au', 'ee', 'it', 'de', 'gb', 'us', 'ca'];
  let phone;
  let phoneInput;

  Ap.login.onSubmit = function() {
    // let number = phoneInput.getNumber();
    // phone.setValue(number);
    return true;
  };

  Ap.login.enhanceControls = function() {
    phone = document.querySelector('#ap-new-user-phone');
    phoneInput = window.intlTelInput(phone, {
      // allowDropdown: false,
      // autoHideDialCode: false,
      // autoPlaceholder: "off",
      dropdownContainer: document.body,
      formatOnDisplay: true,
      // geoIpLookup: function(callback) {
      //   $.get("http://ipinfo.io", function() {}, "jsonp").always(function(resp) {
      //     var countryCode = (resp && resp.country) ? resp.country : "";
      //     callback(countryCode);
      //   });
      // },
      // hiddenInput: "full_number",
      initialCountry: "",
      // localizedCountries: { 'de': 'Deutschland' },
      nationalMode: true,
      // onlyCountries: ['us', 'gb', 'ch', 'ca', 'do'],
      // placeholderNumberType: "MOBILE",
      // separateDialCode: true,
      preferredCountries,
      utilsScript: 'libs/intl-tel-input/js/utils.js',
    });
    window.phoneInput = phoneInput;
    $('#ap-new-user-country').countrySelect({
      defaultCountry: '',
      // responsiveDropdown: true,
      preferredCountries,
    });
  };

  zk.afterMount(function() {
    setTimeout(function() {
      Ap.login.enhanceControls();
      Ap.login.decodeEmail(0);
    }, 100);
  });

})();
