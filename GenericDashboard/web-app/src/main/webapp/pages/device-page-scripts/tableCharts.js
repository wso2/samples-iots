var devices = [];
var rows = [];
var deviceCount;
var ParameterOne = [];
var ParameterTwo = [];
var ParameterThree = [];
historicalParameterOneLabel = ['0s']
historicalParameterOneSeries = [0]
historicalParameterTwoLabel = ['0s']
historicalParameterTwoSeries = [0]
historicalParameterThreeLabel = ['0s']
historicalParameterThreeSeries = [0]


//function for the charts
function initDashboardPageCharts(deviceId) {
    ParameterOne[deviceId] = {};
    ParameterTwo[deviceId] = {};
    ParameterThree[deviceId] = {};

    //use this to get different variables for different devices
    this["historicalParameterOneLabel" + deviceId] = ['0s']
    this["historicalParameterOneSeries" + deviceId] = [0]
    this["historicalParameterTwoLabel" + deviceId] = ['0s']
    this["historicalParameterTwoSeries" + deviceId] = [0]
    this["historicalParameterThreeLabel" + deviceId] = ['0s']
    this["historicalParameterThreeSeries" + deviceId] = [0]

    /* ----------==========      parameter1 Chart initialization    ==========---------- */
    dataHistoricalParameterOneChart = {
        labels: this["historicalParameterOneLabel" + deviceId],
        series: [
            this["historicalParameterOneSeries" + deviceId]
        ]
    };

    optionsHistoricalParameterOneChart = {
        lineSmooth: Chartist.Interpolation.cardinal({
            tension: 0
        }),
        // look
        chartPadding: {
            top: 20,
            right: 0,
            bottom: 0,
            left: 10
        }
    };

    ParameterOne[deviceId] =
        new Chartist.Line('#HistoricalParameterOneChart' + deviceId, dataHistoricalParameterOneChart, optionsHistoricalParameterOneChart);
    md.startAnimationForLineChart(ParameterOne[deviceId]);

    /* ----------==========      parameter2 Chart initialization    ==========---------- */
    dataHistoricalParameterTwoChart = {
        labels: this["historicalParameterTwoLabel" + deviceId],
        series: [
            this["historicalParameterTwoSeries" + deviceId]
        ]
    };

    optionsHistoricalParameterTwoChart = {
        lineSmooth: Chartist.Interpolation.cardinal({
            tension: 0
        }),
        // look
        chartPadding: {
            top: 20,
            right: 0,
            bottom: 0,
            left: 10
        }
    };

    ParameterTwo[deviceId] =
        new Chartist.Line('#HistoricalparameterTwoChart' + deviceId, dataHistoricalParameterTwoChart, optionsHistoricalParameterTwoChart);
    md.startAnimationForLineChart(ParameterTwo[deviceId]);

    /* ----------==========      Parameter3 Chart initialization    ==========---------- */
    dataHistoricalparameterThreeChart = {
        labels: this["historicalParameterThreeLabel" + deviceId],
        series: [
            this["historicalParameterThreeSeries" + deviceId]
        ]
    };

    optionsHistoricalparameterThreeChart = {
        lineSmooth: Chartist.Interpolation.cardinal({
            tension: 0
        }),
        // look
        chartPadding: {
            top: 20,
            right: 0,
            bottom: 0,
            left: 10
        }
    };

    ParameterThree[deviceId] =
        new Chartist.Line('#HistoricalparameterThreeChart' + deviceId, dataHistoricalparameterThreeChart, optionsHistoricalparameterThreeChart);
    md.startAnimationForLineChart(ParameterThree[deviceId]);


}

function timeDifference(current, previous) {
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
        return Math.round(elapsed / msPerDay) + ' days ago';
    } else if (elapsed < msPerYear) {
        return Math.round(elapsed / msPerMonth) + ' months ago';
    } else {
        return Math.round(elapsed / msPerYear) + ' years ago';
    }
}

function redrawGraphs(events, deviceId) {

    var sumParameter1 = 0;
    var sumParameter2 = 0;
    var sumParameter3 = 0;


    if (events.count > 0) {

        var currentTime = new Date();
        this["historicalParameterOneLabel" + deviceId].length = 0;
        this["historicalParameterOneSeries" + deviceId].length = 0;
        this["historicalParameterTwoLabel" + deviceId].length = 0;
        this["historicalParameterTwoSeries" + deviceId].length = 0;
        this["historicalParameterThreeLabel" + deviceId].length = 0;
        this["historicalParameterThreeSeries" + deviceId].length = 0;

        for (var i = events.records.length - 1; i >= 0; i--) {

            var record = events.records[i];

            var sinceText = timeDifference(currentTime, new Date(record.timestamp));
            var dataPoint = record.values;
            var parameter1 = dataPoint[typepParameter1];
            var parameter2 = dataPoint[typeParameter2];
            var parameter3 = dataPoint[typeParameter3];

            if (parameter1)
                sumParameter1 += parameter1;

            if (parameter2)
                sumParameter2 += parameter2;

            if (parameter3)
                sumParameter3 += parameter3;

            this["historicalParameterOneLabel" + deviceId].push(sinceText);
            this["historicalParameterOneSeries" + deviceId].push(parameter1);

            this["historicalParameterTwoLabel" + deviceId].push(sinceText);
            this["historicalParameterTwoSeries" + deviceId].push(parameter2);

            this["historicalParameterThreeLabel" + deviceId].push(sinceText);
            this["historicalParameterThreeSeries" + deviceId].push(parameter3);

            ParameterOne[deviceId].update();
            ParameterTwo[deviceId].update();
            ParameterThree[deviceId].update();


        }
    } else {
        //if there is no records in this period display no records

        ParameterOne[deviceId].update();
        ParameterTwo[deviceId].update();
        ParameterThree[deviceId].update();


    }
}


