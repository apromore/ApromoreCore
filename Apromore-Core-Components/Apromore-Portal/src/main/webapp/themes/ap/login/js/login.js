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

  Ap.login.encodeEmail = function(email) {
    return [...Array(email.length).keys()].map(x => email.charCodeAt(x).toString(16)).reverse().join('');
  };

  let preferredCountries = ['au', 'ee', 'it', 'de', 'gb', 'us', 'ca'];

  Ap.login.onSubmit = function(e) {
    // let number = phoneInput.getNumber();
    // phone.setValue(number);
    let agree = $('#ap-agree');
    let pass = false

    function mainCheck() {
      try {
        pass = validateInput();
      } catch(e) {
        pass = false;
      }
    }

    if (isTCEnabled()) {
      checkComply();
      if (agree.prop('checked')) {
        mainCheck();
      } else {
        $('.ap-force-comply').show();
        pass = false;
      }
    } else {
      mainCheck();
    }

    let subscribe = $('.z-div #ap-subscribe');
    if (!subscribe.is(':visible')) {
      subscribe.prop('checked', false);
    }
    if (!pass) {
      e.preventDefault()
      return false
    }
    return true
  };

  let prefix = '#register'
  let controls = ['firstname', 'surname', 'email', 'username', 'password', 'confirmPassword'];

  function getControl(name) {
    return $(`${prefix} input[name=${name}]`);
  }

  function isTCEnabled() {
    return zk.Widget.$("$agree").isVisible();
  }

  function markControl(control, valid) {
    if (valid) {
      control.removeClass('ap-invalid-input');
      control.prev('.ap-invalid-input-hint').hide();
    } else {
      control.addClass('ap-invalid-input');
      control.prev('.ap-invalid-input-hint').show();
    }
  }

  function addBlankValidators () {
    // test for blanks
    controls.forEach((name) => {
      control = getControl(name);
      let hint = $('<div class="ap-invalid-input-hint"><i class="z-icon-times"></i></div>')
      hint.insertBefore(control);
      hint.hide();
      control.on('change', function (e) {
        let control = $(e.target)
        let val = control.val()
        markControl(control, /([^\s])/.test(val));
      })
    })
  }

  function checkComply (forceHide) {
    if (!isTCEnabled()) {
      return
    }
    let agree = $('#ap-agree');
    let force = $('.ap-force-comply')
    if (agree.prop('checked') || forceHide) {
      force.hide();
      force.css('visibility', 'hidden');
    } else {
      force.show();
      force.css('visibility', 'visible');
    }
  }

  function validateInput () {
    let control, val;
    let pass = true;

    // test for blanks
    controls.forEach((name) => {
      control = getControl(name);
      val = control.val();
      if (!/([^\s])/.test(val)) {
        markControl(control, false);
        pass = false
      }
    })
    // test for email
    control = getControl('email');
    val = control.val();
    let re = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    if (!re.test(val))  {
      markControl(control, false);
      control.attr('placeholder', 'Enter valid email address');
      control.val('');
      pass = false
    }
    // test for password match
    control = getControl('password');
    let control2 = getControl('confirmPassword');
    if (control.val() !== control2.val())  {
      markControl(control, false);
      markControl(control2, false);
      Ap.login.showMessage("The two password entries do not match");
      pass = false
    }
    return pass;
  }

  Ap.login.checkComply = checkComply;

  Ap.login.showMessage = function (content) {
    $('#ap-login-message .content').html(content);
    let msg = $('#ap-login-message');
    msg.show();
    msg.css('visibility', 'visible');
  }

  Ap.login.closeMessage = function () {
    let msg = $('#ap-login-message')
    msg.hide();
    msg.css('visibility', 'hidden');
  }

  Ap.login.enhanceControls = function() {
    let phoneInput;
    let phone = document.querySelector('#ap-new-user-phone');
    let country = $('#ap-new-user-country');
    let email = $('#ap-new-email')
    let reuseEmail = $('#ap-reuse-email');
    let username = $('#ap-new-username');
    let agree = $('#ap-agree');
    let pass = getControl('password');
    let pass2 = getControl('confirmPassword');

    pass.on('change', function() {
      Ap.login.closeMessage()
    })
    pass2.on('change', function() {
      Ap.login.closeMessage()
    })

    if (isTCEnabled() && agree) {
      agree.change((e) => {
        checkComply();
      })
    }

    addBlankValidators();

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
