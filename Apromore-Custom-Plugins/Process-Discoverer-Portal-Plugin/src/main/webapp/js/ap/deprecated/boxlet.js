function apPdMinWindow(iconEl) {
  let win = $(iconEl).closest('.ap-pd-boxlet');
  let winId = win.data('t');
  $('.ap-pd-boxlet-minimized[data-t=' + winId + ']').css('display', 'flex');
  win.hide();
}

function apPdMaxWindow(iconEl) {
  let minWin = $(iconEl).closest('.ap-pd-boxlet-minimized');
  let winId = minWin.data('t');
  $('.ap-pd-boxlet[data-t=' + winId + ']').show();
  minWin.hide();
}