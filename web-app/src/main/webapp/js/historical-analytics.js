analyticsHistory = {
    historicalTempLabel: ['0s'],
    historicalTempSeries: [0],
    historicalHumidLabel: ['0s'],
    historicalHumidSeries: [0],
    historicalStateLabel: ['0s'],
    historicalStateSeries: [0],
    historicalOccupancyLabel: ['0s'],
    historicalOccupancySeries: [0],
    historicalMetalLabel: ['0s'],
    historicalMetalSeries: [0],
    historicalTemp: {},
    historicalHumid: {},
    historicalState: {},
    historicalOccupancy: {},
    historicalMetal: {},
    initDashboardPageCharts: function () {
        /* ----------==========     Historical Temperature Chart initialization    ==========---------- */
        dataHistoricalTempChart = {
            labels: analyticsHistory.historicalTempLabel,
            series: [
                analyticsHistory.historicalTempSeries
            ]
        };

        optionsHistoricalTempChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                                                            tension: 0
                                                        }),
            showArea: true,
            low: 0,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better
                      // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        analyticsHistory.historicalTemp =
            new Chartist.Line('#historicalTemp', dataHistoricalTempChart, optionsHistoricalTempChart);
        md.startAnimationForLineChart(analyticsHistory.historicalTemp);

        /* ----------==========     Historical Humidity Chart initialization    ==========---------- */
        dataHistoricalHumidChart = {
            labels: analyticsHistory.historicalHumidLabel,
            series: [
                analyticsHistory.historicalHumidSeries
            ]
        };

        optionsHistoricalHumidChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                                                            tension: 0
                                                        }),
            showArea: true,
            low: 0,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better
                      // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        analyticsHistory.historicalHumid =
            new Chartist.Line('#historicalHumid', dataHistoricalHumidChart, optionsHistoricalHumidChart);
        md.startAnimationForLineChart(analyticsHistory.historicalHumid);


        /* ----------==========     Historical State Chart initialization    ==========---------- */
        dataHistoricalStateChart = {
            labels: analyticsHistory.historicalStateLabel,
            series: [
                analyticsHistory.historicalStateSeries
            ]
        };

        optionsHistoricalStateChart = {
            lineSmooth: Chartist.Interpolation.step(),
            showArea: true,
            low: 0,
            high: 2, // creative tim: we recommend you to set the high sa the biggest value + something for a better
                     // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        analyticsHistory.historicalState =
            new Chartist.Line('#historicalState', dataHistoricalStateChart, optionsHistoricalStateChart);
        md.startAnimationForLineChart(analyticsHistory.historicalState);


        /* ----------==========     Historical Occupancy Chart initialization    ==========---------- */
        dataHistoricalOccupancyChart = {
            labels: analyticsHistory.historicalOccupancyLabel,
            series: [
                analyticsHistory.historicalOccupancySeries
            ]
        };

        optionsHistoricalOccupancyChart = {
            lineSmooth: Chartist.Interpolation.step(),
            showArea: true,
            low: 0,
            high: 2, // creative tim: we recommend you to set the high sa the biggest value + something for a better
                     // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        analyticsHistory.historicalOccupancy =
            new Chartist.Line('#historicalOccupancy', dataHistoricalOccupancyChart, optionsHistoricalOccupancyChart);
        md.startAnimationForLineChart(analyticsHistory.historicalOccupancy);

        /* ----------==========     Historical Metal Chart initialization    ==========---------- */
        dataHistoricalMetalChart = {
            labels: analyticsHistory.historicalMetalLabel,
            series: [
                analyticsHistory.historicalMetalSeries
            ]
        };

        optionsHistoricalMetalChart = {
            lineSmooth: Chartist.Interpolation.step(),
            showArea: true,
            low: 0,
            high: 2, // creative tim: we recommend you to set the high sa the biggest value + something for a better
                     // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        analyticsHistory.historicalMetal =
            new Chartist.Line('#historicalMetal', dataHistoricalMetalChart, optionsHistoricalMetalChart);
        md.startAnimationForLineChart(analyticsHistory.historicalMetal);
    },

    timeDifference: function (current, previous) {
        var msPerMinute = 60 * 1000;
        var msPerHour = msPerMinute * 60;
        var msPerDay = msPerHour * 24;
        var msPerMonth = msPerDay * 30;
        var msPerYear = msPerDay * 365;

        var elapsed = current - previous;

        if (elapsed < msPerMinute) {
            return Math.round(elapsed / 1000) + ' seconds ago';
        } else if (elapsed < msPerHour) {
            return Math.round(elapsed / msPerMinute) + ' minutes ago';
        } else if (elapsed < msPerDay) {
            return Math.round(elapsed / msPerHour) + ' hours ago';
        } else if (elapsed < msPerMonth) {
            return 'approximately ' + Math.round(elapsed / msPerDay) + ' days ago';
        } else if (elapsed < msPerYear) {
            return 'approximately ' + Math.round(elapsed / msPerMonth) + ' months ago';
        } else {
            return 'approximately ' + Math.round(elapsed / msPerYear) + ' years ago';
        }
    },

    redrawGraphs: function (events) {
        var sumTemp = 0;
        var sumHumid = 0;
        var occupantCount = 0;
        var openCount = 0;
        var metalPresenceCount = 0;
        if (events.count > 0) {
            var currentTime = new Date();
            analyticsHistory.historicalTempLabel.length = 0;
            analyticsHistory.historicalTempSeries.length = 0;
            analyticsHistory.historicalHumidLabel.length = 0;
            analyticsHistory.historicalHumidSeries.length = 0;
            analyticsHistory.historicalStateLabel.length = 0;
            analyticsHistory.historicalStateSeries.length = 0;
            analyticsHistory.historicalOccupancyLabel.length = 0;
            analyticsHistory.historicalOccupancySeries.length = 0;
            analyticsHistory.historicalMetalLabel.length = 0;
            analyticsHistory.historicalMetalSeries.length = 0;
            for (var i = 0; i < events.records.length; i++) {
                var record = events.records[i];
                var sinceText = analyticsHistory.timeDifference(currentTime, new Date(record.timestamp));
                var isOpen = record.values.open;
                var isOccupant = record.values.occupancy;
                var isMetalPresent = record.values.metal;
                var temperature = record.values.temperature;
                var humidity = record.values.humidity;

                if (isOpen) {
                    openCount++;
                }

                if (isMetalPresent) {
                    metalPresenceCount++;
                }

                if (isOccupant) {
                    occupantCount++;
                }

                if (temperature) {
                    sumTemp += temperature;
                }

                if (humidity) {
                    sumHumid += humidity;
                }

                if (i === events.records.length - 1) {
                    var avgHumid = sumHumid / events.records.length;
                    var avgTemp = sumTemp / events.records.length;
                    $("#historicalStateAlert").html("<span class=\"text-success\"><i class=\"fa fa-bolt\"></i> " + openCount + " </span>times open.");
                    $("#historicalTempAlert").html("<span class=\"text-success\"><i class=\"fa fa-bolt\"></i> " + avgTemp.toFixed(2) + " </span>average Temperature.");
                    $("#historicalHumidAlert").html("<span class=\"text-success\"><i class=\"fa fa-bolt\"></i> " + avgHumid.toFixed(2) + " </span> average Humidity.");
                    $("#historicalOccupancyAlert").html("<span class=\"text-success\"><i class=\"fa fa-group\"></i> " + occupantCount + " </span> people occupied locker.");
                    $("#historicalMetalAlert").html("<span class=\"text-success\"><i class=\"fa fa-cubes\"></i> " + metalPresenceCount + " </span> metal presence detected");
                }

                analyticsHistory.historicalTempLabel.push(sinceText);
                analyticsHistory.historicalTempSeries.push(temperature);

                analyticsHistory.historicalHumidLabel.push(sinceText);
                analyticsHistory.historicalHumidSeries.push(humidity);

                analyticsHistory.historicalStateLabel.push(sinceText);
                analyticsHistory.historicalStateSeries.push((isOpen) ? 1 : 0);

                analyticsHistory.historicalOccupancyLabel.push(sinceText);
                analyticsHistory.historicalOccupancySeries.push((isOccupant) ? 1 : 0);

                analyticsHistory.historicalMetalLabel.push(sinceText);
                analyticsHistory.historicalMetalSeries.push((isMetalPresent) ? 1 : 0);
            }
        } else {
            analyticsHistory.historicalTempLabel = ['0s'];
            analyticsHistory.historicalTempSeries = [0];
            analyticsHistory.historicalHumidLabel = ['0s'];
            analyticsHistory.historicalHumidSeries = [0];
            analyticsHistory.historicalStateLabel = ['0s'];
            analyticsHistory.historicalStateSeries = [0];
            analyticsHistory.historicalOccupancyLabel = ['0s'];
            analyticsHistory.historicalOccupancySeries = [0];
            analyticsHistory.historicalMetalLabel = ['0s'];
            analyticsHistory.historicalMetalSeries = [0];
        }
        analyticsHistory.historicalTemp.update();
        analyticsHistory.historicalHumid.update();
        analyticsHistory.historicalState.update();
        analyticsHistory.historicalOccupancy.update();
        analyticsHistory.historicalMetal.update();
    }
};