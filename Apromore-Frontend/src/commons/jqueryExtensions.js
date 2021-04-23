
// 'Special scroll events for jQuery' by James Padolsey
// https://j11y.io/javascript/special-scroll-events-for-jquery/
export const registerWheelStartStopEvents = function(jQuery) {
    (function () {
        let special = jQuery.event.special,
            uid1 = 'D' + (+new Date()),
            uid2 = 'D' + (+new Date() + 1);

        special.wheelstart = {
            setup: function () {

                var timer,
                    handler = function (evt) {

                        var _self = this,
                            _args = arguments;

                        if (timer) {
                            clearTimeout(timer);
                        } else {
                            evt.type = 'wheelstart';
                            jQuery.event.dispatch.apply(_self, _args);
                        }

                        timer = setTimeout(function () {
                            timer = null;
                        }, special.wheelstop.latency);

                    };

                jQuery(this).bind('wheel', handler).data(uid1, handler);

            },
            teardown: function () {
                jQuery(this).unbind('wheel', jQuery(this).data(uid1));
            }
        };

        special.wheelstop = {
            latency: 500,
            setup: function () {

                var timer,
                    handler = function (evt) {

                        var _self = this,
                            _args = arguments;

                        if (timer) {
                            clearTimeout(timer);
                        }

                        timer = setTimeout(function () {

                            timer = null;
                            evt.type = 'wheelstop';
                            jQuery.event.dispatch.apply(_self, _args);

                        }, special.wheelstop.latency);

                    };

                jQuery(this).bind('wheel', handler).data(uid2, handler);

            },
            teardown: function () {
                jQuery(this).unbind('wheel', jQuery(this).data(uid2));
            }
        };

    })();
}