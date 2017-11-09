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
            low: 0,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeTemp = new Chartist.Line('#realtimeTemp', dataRealtimeTempChart, optionsRealtimeTempChart);
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
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeHumid = new Chartist.Line('#realtimeHumid', dataRealtimeHumidChart, optionsRealtimeHumidChart);
        md.startAnimationForLineChart(realtimeHumid);

        /* ----------==========     Realtime State Chart initialization    ==========---------- */
        var realtimeStateLabelRef = [new Date()];
        var realtimeStateLabel = ['0s'];
        var realtimeStateSeries = [0];

        realtimeAnalytics.createLiFo(realtimeStateLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeStateLabel, 10);
        realtimeAnalytics.createLiFo(realtimeStateSeries, 10);

        dataRealtimeStateChart = {
            labels: realtimeStateLabel,
            series: [
                realtimeStateSeries
            ]
        };

        optionsRealtimeStateChart = {
            lineSmooth: Chartist.Interpolation.step(),
            showArea: true,
            low: 0,
            high: 2, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeState = new Chartist.Line('#realtimeState', dataRealtimeStateChart, optionsRealtimeStateChart);
        md.startAnimationForLineChart(realtimeState);

        /* ----------==========     Realtime Occupancy Chart initialization    ==========---------- */

        var realtimeOccupancyLabelRef = [new Date()];
        var realtimeOccupancyLabel = ['0s'];
        var realtimeOccupancySeries = [0];

        realtimeAnalytics.createLiFo(realtimeOccupancyLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeOccupancyLabel, 10);
        realtimeAnalytics.createLiFo(realtimeOccupancySeries, 10);

        dataRealtimeOccupancyChart = {
            labels: realtimeOccupancyLabel,
            series: [
                realtimeOccupancySeries
            ]
        };

        optionsRealtimeOccupancyChart = {
            lineSmooth: Chartist.Interpolation.step(),
            showArea: true,
            low: 0,
            high: 2, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeOccupancy = new Chartist.Line('#realtimeOccupancy', dataRealtimeOccupancyChart, optionsRealtimeOccupancyChart);
        md.startAnimationForLineChart(realtimeOccupancy);

        /* ----------==========     Realtime Metal Chart initialization    ==========---------- */

        var realtimeMetalLabelRef = [new Date()];
        var realtimeMetalLabel = ['0s'];
        var realtimeMetalSeries = [0];

        realtimeAnalytics.createLiFo(realtimeMetalLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeMetalLabel, 10);
        realtimeAnalytics.createLiFo(realtimeMetalSeries, 10);

        dataRealtimeMetalChart = {
            labels: realtimeMetalLabel,
            series: [
                realtimeMetalSeries
            ]
        };

        optionsRealtimeMetalChart = {
            lineSmooth: Chartist.Interpolation.step(),
            showArea: true,
            low: 0,
            high: 2, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeMetal = new Chartist.Line('#realtimeMetal', dataRealtimeMetalChart, optionsRealtimeMetalChart);
        md.startAnimationForLineChart(realtimeMetal);

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
                ws = new MozWebSocket(target);
            } else {
                console.log('WebSocket is not supported by this browser.');
            }
            if (ws) {
                ws.onmessage = function (event) {
                    var data = event.data;
                    console.log(data);
                    var dataPoint = JSON.parse(data);

                    var open = dataPoint.open;
                    var occupancy = dataPoint.occupancy;
                    var temperature = dataPoint.temperature;
                    var metal = dataPoint.metal;
                    var humidity = dataPoint.humidity;

                    var currentTime = new Date();
                    var sinceText = timeDifference(currentTime, new Date(dataPoint.timeStamp), false) + " ago";
                    updateStatusCards(sinceText, open, occupancy, metal);

                    var lastUpdatedTime = realtimeTempLabelRef[realtimeTempLabelRef.length - 1];
                    var lastUpdatedText = "<i class=\"material-icons\">access_time</i> updated "+timeDifference(currentTime, lastUpdatedTime)+" ago";

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

                    realtimeStateLabel.push('0s');
                    realtimeStateLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeStateLabel, realtimeStateLabelRef);
                    realtimeStateSeries.push((open)? 1 : 0);
                    $("#realtimeStateLastUpdated").html(lastUpdatedText);

                    realtimeOccupancyLabel.push('0s');
                    realtimeOccupancyLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeOccupancyLabel, realtimeOccupancyLabelRef);
                    realtimeOccupancySeries.push((occupancy)? 1 : 0);
                    $("#realtimeOccupancyLastUpdated").html(lastUpdatedText);

                    realtimeMetalLabel.push('0s');
                    realtimeMetalLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeMetalLabel, realtimeMetalLabelRef);
                    realtimeMetalSeries.push((metal)? 1 : 0);
                    $("#realtimeMetalLastUpdated").html(lastUpdatedText);

                    updateGraphs();
                };
            }
        }
        
        function updateGraphs(){
            realtimeTemp.update();
            realtimeHumid.update();
            realtimeState.update();
            realtimeOccupancy.update();
            realtimeMetal.update();
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
                ws = null;
            }
        }
    },

    createLiFo : function(arr, length){
        arr.push = function (){
            if (this.length >= length) {
                this.shift();
            }
            return Array.prototype.push.apply(this,arguments);
        };
    },

    calcTimeDiff: function (arr, arrRef) {
        var now = new Date();
        for (var i = 0; i < arr.length; i++) {
            arr[i] = timeDifference(now, arrRef[i], true);
        }
    }
};