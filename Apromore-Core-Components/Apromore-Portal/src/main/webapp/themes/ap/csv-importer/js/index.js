function openPop(i) {
    let all = $('.relativePos > div')
    $(all[i]).show()
    $(all[i]).css('visibility', 'visible')
}

function adjustPos(i) {
    try {
        let targets = $('.p-1');
        let sources = $('.z-auxheader');
        let ref = $('.relativePos').offset();

        let { left, top } = $(sources[i + 1]).offset()
        left -= ref.left;
        left += 8;
        // top -= ref.top;
        $(targets[i]).css({left});
    } catch(e) {
        // pass
    }
}
