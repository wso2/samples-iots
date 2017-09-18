/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var map;

function loadLeafletMap() {

    var deviceLocationID = "#device-location",
        locations = $(deviceLocationID).data("locations"),
        location_lat = $(deviceLocationID).data("lat"),
        location_long = $(deviceLocationID).data("long"),
        container = "device-location",
        zoomLevel = 13,
        tileSet = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        attribution = "&copy; <a href='https://openstreetmap.org/copyright'>OpenStreetMap</a> contributors";

    if (location_long && location_lat) {

        map = L.map(container).setView([location_lat, location_long], zoomLevel);
        L.tileLayer(tileSet, {attribution: attribution}).addTo(map);

        var m = L.marker([location_lat, location_long], {"opacity": 0.70}).addTo(map).bindPopup("Your device is here");
        m.on('mouseover', function (e) {
            this.openPopup();
        });
        m.on('mouseout', function (e) {
            this.closePopup();
        });
        $("#map-error").hide();
        $("#device-location").show();
        setTimeout(function(){ map.invalidateSize()}, 400);
    } else {
        $("#device-location").hide();
        $("#map-error").show();
    }
}

$(".media.tab-responsive [data-toggle=tab]").on("shown.bs.tab", function (e) {
    var activeTabPane = $(e.target).attr("href");
    var activeListGroupItem = $(".media .list-group-item.active");

    $(activeTabPane).removeClass("visible-xs-block");
    $(activeTabPane).siblings().not(".arrow-left").addClass("visible-xs-block");
    positionArrow(activeListGroupItem);
});

$(".media.tab-responsive .tab-content").on("shown.bs.collapse", function (e) {
    var thisParent = $(e.target).parent();
    var activeTabPaneCaret = thisParent.find('.caret-updown');
    var activeTabPaneCaretSiblings = thisParent.siblings().find('.caret-updown');

    activeTabPaneCaret.removeClass("fw-up").addClass("fw-down");
    activeTabPaneCaretSiblings.removeClass("fw-down").addClass("fw-up");
});


$('.media.tab-responsive a[data-toggle="collapse"]').on('click',function(){
    var clickedPanel = $(this).attr('href');

    if($(clickedPanel).hasClass('in')){
        $(clickedPanel).collapse('hide');
    }else{
        $(clickedPanel).collapse('show');
    }
});

$(document).ready(function () {
    $(".location_tab").on("click", function () {
        console.log('here');
        loadLeafletMap();
    });
});
