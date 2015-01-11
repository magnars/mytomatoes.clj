if (!MT.debug) {
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-16879106-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

  var pageTracker;
  jQuery(document).ajaxSuccess(function (event, xhr, options) {
    if (window._gat) {
      if (!pageTracker) {
        pageTracker = _gat._getTracker('UA-16879106-1');
      }
      pageTracker._trackPageview(options.url);
    }
  });

  window.onerror = function(message, file, line) {
    var sFormattedMessage = '[' + file + ' (' + line + ')] ' + message;
    _gaq.push(['_trackEvent', 'Exceptions', 'Application', sFormattedMessage, null, true]);
  };
}
