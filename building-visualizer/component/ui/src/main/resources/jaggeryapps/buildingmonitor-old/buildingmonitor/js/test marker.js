/*
*
*
* marker modification
*
* */

/* Add dblclick event for svg */
function onAreaDblClick(e) {
    if (state.appMode === 'editing') {
        if (e.target.tagName === 'rect' || e.target.tagName === 'circle' || e.target.tagName === 'polygon') {
            state.selectedArea = e.target.parentNode.obj;
            //Displays the area attribute form
            // console.log(state.selectedArea._attributes.title);
            info.load(state.selectedArea, e.pageX, e.pageY);
        }
    }
}

domElements.container.addEventListener('dblclick', onAreaDblClick, false);

/**
 * The constructor of helpers points
 * Helper is small svg-rectangle with some actions
 *
 * @constructor
 * @param node {DOMElement} - a node for inserting helper
 * @param x {number} - x-coordinate of helper
 * @param y {number} - y-coordinate of helper
 * @param action {string} - an action by click of this helper (e.g. 'move')
 */
function Marking(node, x, y, action) {
    this._el = document.createElementNS(Area.SVG_NS, 'rect');
    this._el.classList.add(Marking.CLASS_NAME);
    this._el.setAttribute('height', Marking.SIZE);
    this._el.setAttribute('width', Marking.SIZE);
    this._el.setAttribute('x', x + Marking.OFFSET);
    this._el.setAttribute('y', y + Marking.OFFSET);

    node.appendChild(this._el);

    this._el.action = action; // TODO: move 'action' from dom el to data-attr
    this._el.classList.add(Marking.ACTIONS_TO_CURSORS[action]);
}

Marking.SIZE = 9;
Marking.OFFSET = -Math.ceil(Marking.SIZE / 2);
Marking.CLASS_NAME = 'device-marker';
//cursor graphics
Marking.ACTIONS_TO_CURSORS = {
    'move'            : 'move',
    'editLeft'        : 'e-resize',
    'editRight'       : 'w-resize',
    'editTop'         : 'n-resize',
    'editBottom'      : 's-resize',
    'editTopLeft'     : 'nw-resize',
    'editTopRight'    : 'ne-resize',
    'editBottomLeft'  : 'sw-resize',
    'editBottomRight' : 'se-resize',
    'movePoint'       : 'pointer'
};

/**
 * Set coordinates for this helper
 *
 * @param x {number} - x-coordinate
 * @param y {number} - y-coordinate
 * @returns {Helper}
 */
Marking.prototype.setCoords = function(x, y) {
    this._el.setAttribute('x', x + Marking.OFFSET);
    this._el.setAttribute('y', y + Marking.OFFSET);

    return this;
};

/**
 * Set id of this helper in list of parent's helpers
 *
 * @param id {number}
 * @returns {Helper}
 */
Marking.prototype.setId = function(id) {
    // TODO: move n-field from DOM-element to data-attribute
    this._el.n = id;

    return this;
};

/*
 *
 *
 * marker modification
 *
 * */
/**
 * The constructor for marker
 *
 * (x, y) -----
 * |          | height
 * ------------
 *     width
 *
 * @constructor
 * @param coords {Object} - object with parameters of new area (x, y, width, height)
 *                          if some parameter is undefined, it will set 0
 * @param attributes {Object} [attributes=undefined] - attributes for area (e.g. href, title)
 */
function Marker(coords, attributes) {
    Area.call(this, 'marker', coords, attributes);

    /**
     * @namespace
     * @property {number} x - Distance from the left edge of the image to the left side of the rectangle
     * @property {number} y - Distance from the top edge of the image to the top side of the rectangle
     * @property {number} width - Width of rectangle
     * @property {number} height - Height of rectangle
     */
    this._coords = {
        x : coords.x || 0,
        y : coords.y || 0,
        width : coords.width || 0,
        height : coords.height || 0
    };

    this._el = document.createElementNS(Area.SVG_NS, 'device');
    this._groupEl.appendChild(this._el);

    var x = coords.x - this._coords.width / 2,
        y = coords.y - this._coords.height / 2;

    this._helpers = {
        // center : new Helper(this._groupEl, x, y, 'move'),
        // top : new Helper(this._groupEl, x, y, 'editTop'),
        // bottom : new Helper(this._groupEl, x, y, 'editBottom'),
        // left : new Helper(this._groupEl, x, y, 'editLeft'),
        // right : new Helper(this._groupEl, x, y, 'editRight'),
        topLeft : new Helper(this._groupEl, x, y, 'move'),
        // topRight : new Helper(this._groupEl, x, y, 'editTopRight'),
        // bottomLeft : new Helper(this._groupEl, x, y, 'editBottomLeft'),
        // bottomRight : new Helper(this._groupEl, x, y, 'editBottomRight')
    };

    this.redraw();

}
utils.inherits(Marker, Area);

/**
 * Set attributes for svg-elements of area by new parameters
 *
 * -----top------
 * |            |
 * ---center_y---
 * |            |
 * ----bottom----
 *
 * @param coords {Object} - Object with coords of this area (x, y, width, height)
 * @returns {Rectangle} - this rectangle
 */
Marker.prototype.setSVGCoords = function(coords) {
    this._el.setAttribute('x', coords.x);
    this._el.setAttribute('y', coords.y);

    var top = coords.y,
        center_y = coords.y + coords.height / 2,
        bottom = coords.y + coords.height,
        left = coords.x,
        center_x = coords.x + coords.width / 2,
        right = coords.x + coords.width;

    // this._helpers.center.setCoords(center_x, center_y);
    // this._helpers.top.setCoords(center_x, top);
    // this._helpers.bottom.setCoords(center_x, bottom);
    // this._helpers.left.setCoords(left, center_y);
    // this._helpers.right.setCoords(right, center_y);
    this._helpers.topLeft.setCoords(left, top);
    // this._helpers.topRight.setCoords(right, top);
    // this._helpers.bottomLeft.setCoords(left, bottom);
    // this._helpers.bottomRight.setCoords(right, bottom);

    return this;
};

/**
 * Set coords for this area
 *
 * @param coords {coords}
 * @returns {Rectangle} - this rectangle
 */
Marker.prototype.setCoords = function(coords) {
    this._coords.x = coords.x;
    this._coords.y = coords.y;
    this._coords.width = coords.width;
    this._coords.height = coords.height;
    return this;
};

/**
 * Calculates new coordinates in process of drawing
 *
 * @param x {number} - x-coordinate of cursor
 * @param y {number} - y-coordinate of cursor
 * @param isSquare {boolean}
 * @returns {Object} - calculated coords of this area
 */
Marker.prototype.dynamicDraw = function(x, y, isSquare) {
    var newCoords = {
        x : this._coords.x,
        y : this._coords.y,
        width : x - this._coords.x,
        height: y - this._coords.y
    };

    if (isSquare) {
        newCoords = Marker.getSquareCoords(newCoords);
    }

    newCoords = Marker.getNormalizedCoords(newCoords);

    this.redraw(newCoords);

    return newCoords;
};

/**
 * Handler for drawing process (by mousemove)
 * It includes only redrawing area by new coords
 * (this coords doesn't save as own area coords)
 *
 * @params e {MouseEvent} - mousemove event
 */
Marker.prototype.onProcessDrawing = function(e) {
    var coords = utils.getRightCoords(e.pageX, e.pageY);
    this.dynamicDraw(coords.x, coords.y, e.shiftKey);
};

/**
 * Handler for drawing stoping (by second click on drawing canvas)
 * It includes redrawing area by new coords
 * and saving this coords as own area coords
 *
 * @params e {MouseEvent} - click event
 */
Marker.prototype.onStopDrawing = function(e) {
    var coords = utils.getRightCoords(e.pageX, e.pageY);

    this.setCoords(this.dynamicDraw(coords.x, coords.y, e.shiftKey)).deselect();

    app.removeAllEvents()
        .setIsDraw(false)
        .resetNewArea();
};

/**
 * Changes area parameters by editing type and offsets
 *
 * @param {string} editingType - A type of editing (e.g. 'move')
 * @returns {Object} - Object with changed parameters of area
 */
Marker.prototype.edit = function(editingType, dx, dy) {
    var tempParams = Object.create(this._coords);

    switch (editingType) {
        case 'move':
            tempParams.x += dx;
            tempParams.y += dy;
            break;
        //
        // case 'editLeft':
        //     tempParams.x += dx;
        //     tempParams.width -= dx;
        //     break;
        //
        // case 'editRight':
        //     tempParams.width += dx;
        //     break;
        //
        // case 'editTop':
        //     tempParams.y += dy;
        //     tempParams.height -= dy;
        //     break;
        //
        // case 'editBottom':
        //     tempParams.height += dy;
        //     break;

        // case 'editTopLeft':
        //     tempParams.x += dx;
        //     tempParams.y += dy;
        //     tempParams.width -= dx;
        //     tempParams.height -= dy;
        //     break;

        // case 'editTopRight':
        //     tempParams.y += dy;
        //     tempParams.width += dx;
        //     tempParams.height -= dy;
        //     break;
        //
        // case 'editBottomLeft':
        //     tempParams.x += dx;
        //     tempParams.width -= dx;
        //     tempParams.height += dy;
        //     break;
        //
        //     tempParams.height += dy;
        //     break;
        // case 'editBottomRight':
        //     tempParams.width += dx;
    }

    return tempParams;
};

/**
 * Calculates new coordinates in process of editing
 *
 * @param coords {Object} - area coords
 * @param saveProportions {boolean}
 * @returns {Object} - new coordinates of area
 */
Marker.prototype.dynamicEdit = function(coords, saveProportions) {
    coords = Marker.getNormalizedCoords(coords);

    if (saveProportions) {
        coords = Marker.getSavedProportionsCoords(coords);
    }

    this.redraw(coords);

    return coords;
};

/**
 * Handler for editing process (by mousemove)
 * It includes only redrawing area by new coords
 * (this coords doesn't save as own area coords)
 *
 * @params e {MouseEvent} - mousemove event
 */
Marker.prototype.onProcessEditing = function(e) {
    return this.dynamicEdit(
        this.edit(
            app.getEditType(),
            e.pageX - this.editingStartPoint.x,
            e.pageY - this.editingStartPoint.y
        ),
        e.shiftKey
    );
};

/**
 * Handler for editing stoping (by mouseup)
 * It includes redrawing area by new coords
 * and saving this coords as own area coords
 *
 * @params e {MouseEvent} - mouseup event
 */
Marker.prototype.onStopEditing = function(e) {
    this.setCoords(this.onProcessEditing(e));
    app.removeAllEvents();
};

/**
 * Returns string-representation of this rectangle
 *
 * @returns {string}
 */
Marker.prototype.toString = function() {
    return 'Marker {x: '+ this._coords.x +
        ', y: ' + this._coords.y;
}

/**
 * Returns html-string of area html element with params of this rectangle
 *
 * @returns {string}
 */
Marker.prototype.toHTMLMapElementString = function() {
    var x2 = this._coords.x + this._coords.width,
        y2 = this._coords.y + this._coords.height;

    return '<area shape="rect" coords="' // TODO: use template engine
        + this._coords.x + ', '
        + this._coords.y + ', '
        + x2 + ', '
        + y2
        + '"'
        + (this._attributes.href ? ' href="' + this._attributes.href + '"' : '')
        + (this._attributes.alt ? ' alt="' + this._attributes.alt + '"' : '')
        + (this._attributes.title ? ' title="' + this._attributes.title + '"' : '')
        + ' />';
};

/**
 * Returns coords for area attributes form
 *
 * @returns {Object} - object width coordinates of point
 */
Marker.prototype.getCoordsForDisplayingInfo = function() {
    return {
        x : this._coords.x,
        y : this._coords.y
    };
};

/**
 * Returns true if coords is valid for rectangles and false otherwise
 *
 * @static
 * @param coords {Object} - object with coords for new rectangle
 * @return {boolean}
 */
Marker.testCoords = function(coords) {
    return coords.x && coords.y;
};

/**
 * Returns true if html coords array is valid for rectangles and false otherwise
 *
 * @static
 * @param coords {Array} - coords for new rectangle as array
 * @return {boolean}
 */
Marker.testHTMLCoords = function(coords) {
    return coords.length === 4;
};

/**
 * Return rectangle coords object from html array
 *
 * @param htmlCoordsArray {Array}
 * @returns {Object}
 */
Marker.getCoordsFromHTMLArray = function(htmlCoordsArray) {
    if (!Marker.testHTMLCoords(htmlCoordsArray)) {
        throw new Error('This html-coordinates is not valid for rectangle');
    }

    return {
        x : htmlCoordsArray[0],
        y : htmlCoordsArray[1]
        // width : htmlCoordsArray[2] - htmlCoordsArray[0],
        // height : htmlCoordsArray[3] - htmlCoordsArray[1]
    };
};

/**
 * Fixes coords if width or/and height are negative
 *
 * @static
 * @param coords {Object} - Coordinates of this area
 * @returns {Object} - Normalized coordinates of area
 */
Marker.getNormalizedCoords = function(coords) {
    if (coords.width < 0) {
        coords.x += coords.width;
        coords.width = Math.abs(coords.width);
    }

    if (coords.height < 0) {
        coords.y += coords.height;
        coords.height = Math.abs(coords.height);
    }

    return coords;
};

/**
 * Returns coords with equivivalent width and height
 *
 * @static
 * @param coords {Object} - Coordinates of this area
 * @returns {Object} - Coordinates of area with equivivalent width and height
 */
Marker.getSquareCoords = function(coords) {
    var width = Math.abs(coords.width),
        height = Math.abs(coords.height);

    if (width > height) {
        coords.width = coords.width > 0 ? height : -height;
    } else {
        coords.height = coords.height > 0 ? width : -width;
    }

    return coords;
};

/**
 * Returns coords with saved proportions of original area
 *
 * @static
 * @param coords {Object} - Coordinates of this area
 * @param originalCoords {Object} - Coordinates of the original area
 * @returns {Object} - Coordinates of area with saved proportions of original area
 */
Marker.getSavedProportionsCoords = function(coords, originalCoords) {
    var originalProportions = coords.width / coords.height,
        currentProportions = originalCoords.width / originalCoords.height;

    if (currentProportions > originalProportions) {
        coords.width = Math.round(coords.height * originalProportions);
    } else {
        coords.height = Math.round(coords.width / originalProportions);
    }

    return coords;
};

/**
 * Creates new rectangle and adds drawing handlers for DOM-elements
 *
 * @static
 * @param firstPointCoords {Object}
 * @returns {Rectangle}
 */
Marker.createAndStartDrawing = function(firstPointCoords) {
    var newArea = new Marker({
        x : firstPointCoords.x,
        y : firstPointCoords.y,
    });

    app.addEvent(app.domElements.container, 'mousedown', newArea.onProcessDrawing.bind(newArea))
        .addEvent(app.domElements.container, 'mouseup', newArea.onStopDrawing.bind(newArea));

    return newArea;
};