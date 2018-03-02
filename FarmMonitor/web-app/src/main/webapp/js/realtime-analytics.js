/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

realtimeAnalytics = {
    initDashboardPageCharts: function (wsEndpoint) {
        /* ----------==========     Realtime Temperature Chart initialization    ==========---------- */
        var realtimeTempLabelRef = [new Date()];
        var realtimeTempLabel = ['0s'];
        var realtimeTempSeries = [0];

        realtimeAnalytics.createLiFo(realtimeTempLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeTempLabel, 10);
        realtimeAnalytics.createLiFo(realtimeTempSeries, 10);

        dataRealtimeTempChart = {
            labels: realtimeTempLabel,
            series: [
                realtimeTempSeries
            ]
        };

        optionsRealtimeTempChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            responsive: false,
            maintainAspectRatio: false,
            low: -50,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            },


        };

        var realtimeTemp = new Chartist.Line('#RealTimeTempChart', dataRealtimeTempChart, optionsRealtimeTempChart);
        md.startAnimationForLineChart(realtimeTemp);

        /* ----------==========     Realtime Humidity Chart initialization    ==========---------- */
        var realtimeHumidLabelRef = [new Date()];
        var realtimeHumidLabel = ['0s'];
        var realtimeHumidSeries = [0];

        realtimeAnalytics.createLiFo(realtimeHumidLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeHumidLabel, 10);
        realtimeAnalytics.createLiFo(realtimeHumidSeries, 10);

        dataRealtimeHumidChart = {
            labels: realtimeHumidLabel,
            series: [
                realtimeHumidSeries
            ]
        };

        optionsRealtimeHumidChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeHumid = new Chartist.Line('#RealTimeHumidityChart', dataRealtimeHumidChart, optionsRealtimeHumidChart);
        md.startAnimationForLineChart(realtimeHumid);

        /* ----------==========     Realtime Engine temperature forecast Chart initialization    ==========---------- */
        var realtimeengineTempLabelRef = [new Date()];
        var realtimeengineTempLabel = ['0s'];
        var realtimeengineTempSeries = [0];

        realtimeAnalytics.createLiFo(realtimeengineTempLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeengineTempLabel, 10);
        realtimeAnalytics.createLiFo(realtimeengineTempSeries, 10);

        dataRealtimeengineTempChart = {
            labels: realtimeengineTempLabel,
            series: [
                realtimeengineTempSeries
            ]
        };

        optionsRealtimeengineTempChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: -10,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeengineTemp = new Chartist.Line('#RealTimeEngineTempChart', dataRealtimeengineTempChart, optionsRealtimeengineTempChart);
        md.startAnimationForLineChart(realtimeengineTemp);

        /* ----------==========     Realtime tractor speed Chart initialization    ==========---------- */
        var realtimetractorspeedmphLabelRef = [new Date()];
        var realtimetractorspeedmphLabel = ['0s'];
        var realtimetractorspeedmphSeries = [0];

        realtimeAnalytics.createLiFo(realtimetractorspeedmphLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimetractorspeedmphLabel, 10);
        realtimeAnalytics.createLiFo(realtimetractorspeedmphSeries, 10);

        dataRealtimetractorspeedmphChart = {
            labels: realtimetractorspeedmphLabel,
            series: [
                realtimetractorspeedmphSeries
            ]
        };

        optionsRealtimetractorspeedmphChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimetractorspeedmph = new Chartist.Line('#RealTimeTractorSpeedChart', dataRealtimetractorspeedmphChart, optionsRealtimetractorspeedmphChart);
        md.startAnimationForLineChart(realtimetractorspeedmph);


        /* ----------==========     Realtime soil moisture Chart initialization    ==========---------- */
        var realtimesoilmoistureLabelRef = [new Date()];
        var realtimesoilmoistureLabel = ['0s'];
        var realtimesoilmoistureSeries = [0];

        realtimeAnalytics.createLiFo(realtimesoilmoistureLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimesoilmoistureLabel, 10);
        realtimeAnalytics.createLiFo(realtimesoilmoistureSeries, 10);

        dataRealtimesoilmoistureChart = {
            labels: realtimesoilmoistureLabel,
            series: [
                realtimesoilmoistureSeries
            ]
        };

        optionsRealtimesoilmoistureChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimesoilmoisture = new Chartist.Line('#RealTimesoilmoistureChart', dataRealtimesoilmoistureChart, optionsRealtimesoilmoistureChart);
        md.startAnimationForLineChart(realtimesoilmoisture);

        /* ----------==========     Realtime illumination Chart initialization    ==========---------- */
        var realtimeilluminationLabelRef = [new Date()];
        var realtimeilluminationLabel = ['0s'];
        var realtimeilluminationSeries = [0];

        realtimeAnalytics.createLiFo(realtimeilluminationLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeilluminationLabel, 10);
        realtimeAnalytics.createLiFo(realtimeilluminationSeries, 10);

        dataRealtimeilluminationChart = {
            labels: realtimeilluminationLabel,
            series: [
                realtimeilluminationSeries
            ]
        };

        optionsRealtimeilluminationChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeillumination = new Chartist.Line('#RealTimeIlluminationChart', dataRealtimeilluminationChart, optionsRealtimeilluminationChart);
        md.startAnimationForLineChart(realtimeillumination);

        /* ----------==========     Realtime fuel usage Chart initialization    ==========---------- */
        var realtimefuelusageLabelRef = [new Date()];
        var realtimefuelusageLabel = ['0s'];
        var realtimefuelusageSeries = [0];

        realtimeAnalytics.createLiFo(realtimefuelusageLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimefuelusageLabel, 10);
        realtimeAnalytics.createLiFo(realtimefuelusageSeries, 10);

        dataRealtimefuelusageChart = {
            labels: realtimefuelusageLabel,
            series: [
                realtimefuelusageSeries
            ]
        };

        optionsRealtimefuelusageChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimefuelusage = new Chartist.Line('#RealTimeFuelUsageChart', dataRealtimefuelusageChart, optionsRealtimefuelusageChart);
        md.startAnimationForLineChart(realtimefuelusage);

        /* ----------==========     Realtime fuel usage Chart initialization    ==========---------- */
        var realtimeloadweightLabelRef = [new Date()];
        var realtimeloadweightLabel = ['0s'];
        var realtimeloadweightSeries = [0];

        realtimeAnalytics.createLiFo(realtimeloadweightLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeloadweightLabel, 10);
        realtimeAnalytics.createLiFo(realtimeloadweightSeries, 10);

        dataRealtimeloadweightChart = {
            labels: realtimeloadweightLabel,
            series: [
                realtimeloadweightSeries
            ]
        };

        optionsRealtimeloadweightChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeloadweight = new Chartist.Line('#RealTimetractorLoadWeightChart', dataRealtimeloadweightChart, optionsRealtimeloadweightChart);
        md.startAnimationForLineChart(realtimeloadweight);


        if (wsEndpoint) {
            connect(wsEndpoint);
        } else {
            updateGraphs();
        }
        var ws;

        // close websocket when page is about to be unloaded
        // fixes broken pipe issue
        window.onbeforeunload = function () {
            disconnect();
        };

        //websocket connection
        function connect(target) {
            if ('WebSocket' in window) {
                ws = new WebSocket(target);
            } else if ('MozWebSocket' in window) {
                console.log('realtime mozsocket');
                ws = new MozWebSocket(target);
            } else {
                console.log('WebSocket is not supported by this browser.');
            }
            if (ws) {

                ws.onmessage = function (event) {
                    var data = event.data;
                    var dataPoint = JSON.parse(data).event.payloadData;
                    var temperature = dataPoint.temperature;
                    var humidity = dataPoint.humidity;
                    var dewpoint = dataPoint.EngineTemp;
                    var tractorSpeed = dataPoint.tractorSpeed;
                    var tractorLoadWeight = dataPoint.loadWeight;
                    var soilMoisture = dataPoint.soilMoisture;
                    var Illumination = dataPoint.illumination;
                    var fuelUsage = dataPoint.fuelUsage;
                    var engineIdlingstatus = dataPoint.engineidle;
                    var alert = dataPoint.raining;

                    var currentTime = new Date();
                    var sinceText = timeDifference(currentTime, new Date(dataPoint.timeStamp), false) + " ago";
                    updateStatusCards(sinceText, alert, fuelUsage, engineIdlingstatus, tractorLoadWeight);

                    var lastUpdatedTime = realtimeTempLabelRef[realtimeTempLabelRef.length - 1];
                    var lastUpdatedText = "<i class=\"material-icons\">access_time</i> updated " + timeDifference(currentTime, lastUpdatedTime) + " ago";

                    realtimeTempLabel.push('0s');
                    realtimeTempLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeTempLabel, realtimeTempLabelRef);
                    realtimeTempSeries.push(temperature);
                    $("#realtimeTempLastUpdated").html(lastUpdatedText);

                    realtimeHumidLabel.push('0s');
                    realtimeHumidLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeHumidLabel, realtimeHumidLabelRef);
                    realtimeHumidSeries.push(humidity);
                    $("#realtimeHumidLastUpdated").html(lastUpdatedText);

                    realtimeengineTempLabel.push('0s');
                    realtimeengineTempLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeengineTempLabel, realtimeengineTempLabelRef);
                    realtimeengineTempSeries.push(dewpoint);
                    $("#realtimeengineTempLastUpdated").html(lastUpdatedText);

                    realtimetractorspeedmphLabel.push('0s');
                    realtimetractorspeedmphLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimetractorspeedmphLabel, realtimetractorspeedmphLabelRef);
                    realtimetractorspeedmphSeries.push(tractorSpeed);
                    $("#realtimetractorspeedmphLastUpdated").html(lastUpdatedText);

                    realtimesoilmoistureLabel.push('0s');
                    realtimesoilmoistureLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimesoilmoistureLabel, realtimesoilmoistureLabelRef);
                    realtimesoilmoistureSeries.push(soilMoisture);
                    $("#realtimesoilmoistureLastUpdated").html(lastUpdatedText);

                    realtimeilluminationLabel.push('0s');
                    realtimeilluminationLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeilluminationLabel, realtimeilluminationLabelRef);
                    realtimeilluminationSeries.push(Illumination);
                    $("#realtimeIlluminationLastUpdated").html(lastUpdatedText);


                    realtimeloadweightLabel.push('0s');
                    realtimeloadweightLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeloadweightLabel, realtimeloadweightLabelRef);
                    realtimeloadweightSeries.push(tractorLoadWeight);
                    $("#realtimeTractorLoadWeightlastUpdated").html(lastUpdatedText);

                    realtimefuelusageLabel.push('0s');
                    realtimefuelusageLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimefuelusageLabel, realtimefuelusageLabelRef);
                    realtimefuelusageSeries.push(fuelUsage);
                    $("#realtimeFuelUsageLastUpdated").html(lastUpdatedText);

                    updateGraphs();
                };

            }

            //refresh graphs on click on the chart
            $('.card').click(function () {
                updateGraphs();

            });

            $("#menu-toggle").click(function () {
                setTimeout(updateGraphs, 200);
            });

        }

        function updateGraphs() {
            realtimeTemp.update();
            realtimeHumid.update();
            realtimeengineTemp.update();
            realtimetractorspeedmph.update();
            realtimefuelusage.update();
            realtimeloadweight.update();
            realtimesoilmoisture.update();
            realtimeillumination.update();
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
                ws = null;
            }
        }
    },

    createLiFo: function (arr, length) {
        arr.push = function () {
            if (this.length >= length) {
                this.shift();
            }
            return Array.prototype.push.apply(this, arguments);
        };
    },

    calcTimeDiff: function (arr, arrRef) {
        var now = new Date();
        for (var i = 0; i < arr.length; i++) {
            arr[i] = timeDifference(now, arrRef[i], true);
        }
    },

};