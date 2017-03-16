var utils = {};

(function (utils) {
    utils.relativePrefix = function (path) {
        var parts = path.split('/');
        var prefix = '';
        var i;
        var count = parts.length - 3;
        for (i = 0; i < count; i++) {
            prefix += '../';
        }
        return prefix;
    };

    utils.findJag = function (path) {
        var file = new File(path);
        if (file.isExists()) {
            return path;
        }
        path = path.replace(/\/[^\/]*$/ig, '');
        if (!path) {
            return null;
        }
        return utils.findJag(path + '.jag');
    };

})(utils);