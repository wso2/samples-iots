history = {
    initDashboardPageCharts: function() {
        /* ----------==========     Historical Temperature Chart initialization    ==========---------- */
        dataHistoricalTempChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [12, 17, 7, 17, 23, 18, 38, 10, 20, 30, 15]
            ]
        };

        optionsHistoricalTempChart = {
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

        var historicalTemp = new Chartist.Line('#historicalTemp', dataRealtimeTempChart, optionsRealtimeTempChart);
        md.startAnimationForLineChart(historicalTemp);


        /* ----------==========     Historical Humidity Chart initialization    ==========---------- */
        dataHistoricalHumidChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [12, 17, 7, 17, 23, 18, 38, 10, 20, 30, 15]
            ]
        };

        optionsHistoricalHumidChart = {
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

        var historicalHumid = new Chartist.Line('#historicalHumid', dataHistoricalHumidChart, optionsHistoricalHumidChart);
        md.startAnimationForLineChart(historicalHumid);


        /* ----------==========     Historical State Chart initialization    ==========---------- */
        dataHistoricalStateChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [0, 1, 1, 0, 1, 0, 0, 1, 0, 1]
            ]
        };

        optionsHistoricalStateChart = {
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

        var historicalState = new Chartist.Line('#historicalState', dataHistoricalStateChart, optionsHistoricalStateChart);
        md.startAnimationForLineChart(historicalState);


        /* ----------==========     Historical Occupancy Chart initialization    ==========---------- */
        dataHistoricalOccupancyChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [0, 1, 1, 0, 1, 1, 0, 1, 0, 0]
            ]
        };

        optionsHistoricalOccupancyChart = {
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

        var historicalOccupancy = new Chartist.Line('#historicalOccupancy', dataHistoricalOccupancyChart, optionsHistoricalOccupancyChart);
        md.startAnimationForLineChart(historicalOccupancy);

        /* ----------==========     Historical Metal Chart initialization    ==========---------- */
        dataHistoricalMetalChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [0, 1, 1, 0, 1, 1, 0, 1, 0, 0]
            ]
        };

        optionsHistoricalMetalChart = {
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

        var historicalMetal = new Chartist.Line('#historicalMetal', dataHistoricalMetalChart, optionsHistoricalMetalChart);
        md.startAnimationForLineChart(historicalMetal);
    }
};