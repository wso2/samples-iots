/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var heatMapManagement = heatMapManagement || {};

(function(){
    var heatmapInstance;
    var currentHeatMap;
    var heatMapData = [];
    var rangeSlider;
    var isSliderChanged = false;
    var currentSliderValue = 60;
    var historicalData;
    var isHistoricalView = false;
    var timouts = [];
    var heatMapConfig = {
        container: document.getElementById('image'),
        radius: 200,
        maxOpacity: .5,
        minOpacity: 0,
        blur: .75
    };

    /**
     * To get the heat map.
     * @returns instance of heatmap.
     */
    var getHeatMap = function () {
        return heatmapInstance;
    };

    /**
     * To create the heat map.
     */
    var createHeatMap = function () {
        if (!heatmapInstance) {
            heatmapInstance = h337.create(heatMapConfig);
        }
        if (!currentHeatMap) {
            var config = {
                container: document.getElementById('heat-map-hidden'),
                radius: 200,
                maxOpacity: .5,
                minOpacity: 0,
                blur: .75
            };
            currentHeatMap = window.h337.create(config);
        }
    };

    /**
     * To update the heat map on changing the slider.
     */
    var updateHeatMapOnSlideChange = function () {
        isSliderChanged = true;
        var max = rangeSlider.bootstrapSlider("getAttribute", 'max');
        var min = rangeSlider.bootstrapSlider("getAttribute", 'min');
        currentSliderValue = rangeSlider.bootstrapSlider("getValue");

        var data = {data: []};
        heatmapInstance.setData(data);
        heatmapInstance = h337.create(heatMapConfig);

        if (!isHistoricalView) {
            if (currentSliderValue == 10) {
                heatmapInstance.setData(data);
                heatmapInstance = h337.create(heatMapConfig);
                heatmapInstance.setData(currentHeatMap.getData());
            } else {
                heatmapInstance.setData(heatMapData[currentSliderValue - 1]);
            }
        } else {
            if (historicalData) {
                data = {data: []};
                heatmapInstance.setData(data);
                heatmapInstance = h337.create(heatMapConfig);
                var length = historicalData.length * ((currentSliderValue-min) / (max-min));
                for (var i = 0; i < length; i++) {
                    var dataPoint = {
                        x: historicalData[i].xCoordinate,
                        y: historicalData[i].yCoordinate,
                        value: historicalData[i].temperature
                    };
                    heatmapInstance.addData(dataPoint);
                }
            } else {

            }
        }
    };

    /**
     * To handle the real-time data.
     * @param dataValues
     */
    var handleRealTimeData = function(dataValues) {
        if (heatmapInstance != null && dataValues.temperature > 0) {
            var dataPoint = {
                x: dataValues.location.coordinates[0],
                y: dataValues.location.coordinates[1],
                value: dataValues.temperature
            };
            currentHeatMap.addData(dataPoint);

            if (!isSliderChanged) {
                heatmapInstance.addData(dataPoint);
            } else if (!isHistoricalView && currentSliderValue == 10) {
                heatmapInstance.addData(dataPoint);
            }
            if (heatMapData.length == 10) {
                heatMapData.shift();
            }
            heatMapData.push(currentHeatMap.getData());
        }
    };


    /**
     * To initialize the slider.
     */
    var initializeSlider = function() {
        rangeSlider = $("#ex1Slider").bootstrapSlider();
        rangeSlider.bootstrapSlider("setAttribute", "min", 1);
        rangeSlider.bootstrapSlider("setAttribute", "max", 10);
        rangeSlider.bootstrapSlider("setValue", 10);

        $('#ex1Slider').on("slide", function () {
            updateHeatMapOnSlideChange();

        }).on("change", function () {
            updateHeatMapOnSlideChange();
        });
    };

    /**
     * To update the heatMap with historical data.
     */
    var updateHistoricalData = function(batchData) {
        historicalData = batchData;
        heatmapInstance.setData({data:[]});
        heatmapInstance = h337.create(heatMapConfig);

        for (var data in historicalData) {
            var dataPoint = {
                x: historicalData[data].xCoordinate,
                y: historicalData[data].yCoordinate,
                value: historicalData[data].temperature
            };
            heatmapInstance.addData(dataPoint);
        }
    };

    /**
     * To enable or disable historical data.
     */
    var enableDisableHistoricalData = function() {
        isHistoricalView = !isHistoricalView;
    };

    /**
     * To make animation out of the heat-map for the real-time data.
     */
    $( "#play" ).click(function(e) {
        e.preventDefault();

        if ($("#play").hasClass('play')) {
            timouts = [];
            $(this).addClass('pause').removeClass('play');
            $("#pau").removeClass("hidden");
            $("#pla").addClass("hidden");
            var currentSliderValue = rangeSlider.bootstrapSlider("getValue");

            if (currentSliderValue == 10) {
                rangeSlider.bootstrapSlider("setValue", 0);
                currentSliderValue = 0;
                updateHeatMapOnSlideChange();
            }

            for (var i = 0, len = rangeSlider.bootstrapSlider("getAttribute", "max"); currentSliderValue <= len; currentSliderValue++, i++) {
                 timouts.push(setTimeout(function(y) {
                    rangeSlider.bootstrapSlider("setValue", y);
                     updateHeatMapOnSlideChange();

                    if (y == 10) {
                        $("#pla").removeClass("hidden");
                        $("#pau").addClass("hidden");
                        $("#play").addClass('play').removeClass('pause');
                    }
                }, i * 500, currentSliderValue));
            }
        } else {
            $("#pla").removeClass("hidden");
            $("#pau").addClass("hidden");
            if (timouts) {
                for (var i = 0;i < timouts.length; i++) {
                    clearTimeout(timouts[i]);
                }
            }
            $(this).addClass('play').removeClass('pause');

        }

    });

    heatMapManagement.functions = {
        getHeatMap : getHeatMap,
        createHeatMap : createHeatMap,
        handleRealTimeData : handleRealTimeData,
        initializeSlider : initializeSlider,
        enableDisableHistoricalData : enableDisableHistoricalData,
        updateHistoricalData : updateHistoricalData
    }

})();
