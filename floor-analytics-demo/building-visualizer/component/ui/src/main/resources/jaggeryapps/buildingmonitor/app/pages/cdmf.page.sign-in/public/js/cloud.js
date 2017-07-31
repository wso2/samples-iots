function CloudManager(container, options) {
    if (!('Tau' in Math)) {
        Math.Tau = Math.PI * 2;
    }
    if (typeof Object.extend !== 'function') {
        Object.extend = function (d, s) {
            for (var k in s) {
                if (s.hasOwnProperty(k)) {
                    var v = s[k];
                    if (d.hasOwnProperty(k) && typeof d[k] === "object" && typeof v === "object") {
                        Object.extend(d[k], v);
                    } else {
                        d[k] = v;
                    }
                }
            }
            return d;
        };
    }

    function apply_styles(elms, styles) {
        !Array.isArray(elms) && (elms = [elms]);
        for (var i = 0; i < elms.length; i++) {
            for (var key in styles) {
                if (styles.hasOwnProperty(key)) {
                    elms[i].style[key] = styles[key];
                }
            }
        }
    }

    var canvas = document.createElement('canvas'),
        context = canvas.getContext('2d'),
        height = canvas.height = container.offsetHeight,
        width = canvas.width = container.offsetWidth;
    var Clouds = [];
    var defaults = {
        count: 60,
        opacity: 0.15,
        radius: 30,
        jitter: 0.5,
        xRange: [width * 1.5, width * 0.25],
        yRange: [height / 7.5, height / 7.5],
        color: {
            r: 255,
            g: 255,
            b: 255
        },
        cluster: 30
    };
    options = Object.extend(defaults, options);
    for (var i = 0; i < options.count; i++) {
        var cloud = {
            x: Math.random() * options.xRange[0] - options.xRange[1],
            y: Math.random() * options.yRange[0] + options.yRange[1],
            radius: Math.random() * (options.radius * options.jitter) + (options.radius * options.jitter),
            angle: Math.random() * Math.Tau,
            opacity: Math.random() * options.opacity + options.opacity / 2,
            cluster: Math.random() * (options.cluster * options.jitter) + (options.cluster * options.jitter)
        };
        cloud.seeds = [];
        for (var j = 0; j < cloud.cluster; j++) {
            cloud.seeds.push(Math.random());
        }
        var icanvas = document.createElement('canvas'),
            icontext = icanvas.getContext('2d');
        icanvas.height = icanvas.width = cloud.radius * 2;
        var grad = icontext.createRadialGradient(cloud.radius, cloud.radius, 0, cloud.radius, cloud.radius, cloud.radius);
        grad.addColorStop(0, 'rgba(' + options.color.r + ', ' + options.color.g + ', ' + options.color.b + ', ' + cloud.opacity + ')');
        grad.addColorStop(1, 'rgba(' + options.color.r + ', ' + options.color.g + ', ' + options.color.b + ', 0)');
        icontext.fillStyle = grad;
        icontext.beginPath();
        icontext.arc(cloud.radius, cloud.radius, cloud.radius, 0, Math.Tau, true);
        icontext.fill();
        icontext.closePath();
        cloud.img = icanvas;
        Clouds.push(cloud);
    }

    function render() {
        requestAnimationFrame(render);
        context.clearRect(0, 0, width, height);
        for (var i = 0, l = Clouds.length; i < l; i++) {
            var cloud = Clouds[i];
            for (var j = 0; j < cloud.cluster; j++) {
                var x = cloud.x - cloud.radius + cloud.seeds[j] * cloud.radius * Math.cos(cloud.angle + j) * Math.PI;
                var y = cloud.y - cloud.radius + cloud.seeds[j] * cloud.radius * Math.sin(cloud.angle + j) * Math.PI / 2;
                context.drawImage(cloud.img, x, y);
            }
        }
    }

    function update() {
        for (var i = 0, l = Clouds.length; i < l; i++) {
            Clouds[i].x -= 0.119;
            if (Clouds[i].x < Clouds[i].radius * -4) {
                Clouds[i].x = options.xRange[1] + Clouds[i].radius * 2;
            }

        }
        setTimeout(update, 1000 / 60);
    }
    apply_styles(canvas, {
        'position':'absolute',
        'top': '-80px',
        'left': '0px',
        'bottom': '0px',
        'right': '0px'
    });
    container.insertBefore(canvas, container.firstChild);
    update();
    render();

}
setTimeout( function() {
    CloudManager(document.body, {jitter: 0.88});
}, 100)