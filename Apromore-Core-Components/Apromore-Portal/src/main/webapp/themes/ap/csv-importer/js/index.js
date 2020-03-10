function openPop(i) {
    let all = $('.relativePos > div')
    $(all[i]).show()
    $(all[i]).css('visibility', 'visible')
}

function adjustPos(i) {
    try {
        let targets = $('.p-1');
        let sources = $('.z-auxheader');

        let srcPos = $(sources[i + 1]).offset()
        // console.log(srcPos, $(targets[i]));
        $(targets[i]).offset(srcPos);
    } catch(e) {
        // pass
    }

}