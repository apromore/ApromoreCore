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

  Ap.login.onSubmit = function() {
    // let number = phoneInput.getNumber();
    // phone.setValue(number);
    return true;
  };

  Ap.login.enhanceControls = function() {
    let phoneInput;
    let phone = document.querySelector('#ap-new-user-phone');
    let country = $('#ap-new-user-country');
    let email = $('#ap-new-email')
    let reuseEmail = $('#ap-reuse-email');
    let username = $('#ap-new-username');

    phoneInput = window.intlTelInput(phone, {
      // allowDropdown: false,
      // autoHideDialCode: false,
      // autoPlaceholder: "off",
      dropdownContainer: document.body,
      formatOnDisplay: true,
      // geoIpLookup: function(callback) { callback(countryCode); },
      // hiddenInput: "full_number",
      initialCountry: "",
      // localizedCountries: { 'de': 'Deutschland' },
      nationalMode: true,
      // placeholderNumberType: "MOBILE",
      // separateDialCode: true,
      preferredCountries,
      utilsScript: 'libs/intl-tel-input/js/utils.js',
    });
    window.phoneInput = phoneInput;

    country.countrySelect({
      defaultCountry: '',
      preferredCountries,
      // responsiveDropdown: true,
    });

    country.on('change', function() {
      let selected = country.countrySelect("getSelectedCountryData");
      if (selected && selected.iso2) {
        phoneInput.setCountry(selected.iso2);
      }
    })

    email.keyup(function() {
      if (reuseEmail.prop('checked')) {
        username.val(email.val())
      }
    })
    reuseEmail.on('change', function() {
      if (reuseEmail.prop('checked')) {
        username.val(email.val())
      }
    })
  };

  zk.afterMount(function() {
    setTimeout(function() {
      Ap.login.enhanceControls();
      Ap.login.decodeEmail(0);
    }, 100);
  });

})();
