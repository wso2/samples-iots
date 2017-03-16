var path = path || {};

(function () {
    var relativePrefix = function () {
        var path = window.location.pathname;
        var parts = path.split('/');
        var prefix = '';
        var i;
        var count = parts.length - 1;
        for (i = 0; i < count; i++) {
            prefix += '../';
        }
        return prefix;
    };

    path.utils = {
        relativePrefix: relativePrefix
    };
}());