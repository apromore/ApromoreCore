function apPdMinWindow2(el) {
  let winId = $(el.$n()).data('t');
  $('.ap-pd-floatlet-minimized[data-t=' + winId + ']').css('display', 'flex');
}

function apPdMaxWindow2(iconEl) {
  let minWin = $(iconEl).closest('.ap-pd-floatlet-minimized');
  let winId = minWin.data('t');
  console.log(
      'max',
      zk.Widget.$(jq('.ap-pd-floatlet[data-t=\'' + winId + '\']')),
  );
  zk.Widget.$(jq('.ap-pd-floatlet[data-t=\'' + winId + '\']')).setVisible(true);
  minWin.hide();
}