$(function() {
    setTimeout(function() {
        let targets = $('.p-1')
        let sources = $('.z-auxheader');
        for (i = 1; i < sources.length; i++) {
            try {
                let srcPos = $(sources[i]).offset()
                // console.log(srcPos, $(targets[i - 1]));
                $(targets[i - 1]).offset(srcPos);
            } catch (e) {
                // pass
            }
        }
    }, 1000);
});