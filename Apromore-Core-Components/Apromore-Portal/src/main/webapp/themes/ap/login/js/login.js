$(function () {
    setTimeout(function() {
        $('#wrapper').addClass('palette-' + (Math.floor(Math.random() * 3) + 1))
    }, 200)
})