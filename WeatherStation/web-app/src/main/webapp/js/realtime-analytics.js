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
            responsive:false,
            maintainAspectRatio : false,
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

        /* ----------==========     Realtime wind direction Chart initialization    ==========---------- */
        var realtimeWindDirLabelRef = [new Date()];
        var realtimeWindDirLabel = ['0s'];
        var realtimeWindDirSeries = [0];

        realtimeAnalytics.createLiFo(realtimeWindDirLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeWindDirLabel, 10);
        realtimeAnalytics.createLiFo(realtimeWindDirSeries, 10);

        dataRealtimeWindDirChart = {
            labels: realtimeWindDirLabel,
            series: [
                realtimeWindDirSeries
            ]
        };

        optionsRealtimeWindDirChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 360, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeWindDir = new Chartist.Line('#RealTimeWindDirChart', dataRealtimeWindDirChart, optionsRealtimeWindDirChart);
        md.startAnimationForLineChart(realtimeWindDir);
        // dataRealtimeWindDirChart={series: [-340]};
        // optionsRealtimeWindDirChart={
        //     donut: true,
        //     donutWidth: 60,
        //     startAngle: 0,
        //     total: 360,
        //     showLabel: true
        // };
        // new Chartist.Pie('#RealTimeWindDirChart',dataRealtimeWindDirChart,optionsRealtimeWindDirChart );

        /* ----------==========     Realtime dew point forecast Chart initialization    ==========---------- */
        var realtimedewptfLabelRef = [new Date()];
        var realtimedewptfLabel = ['0s'];
        var realtimedewptfSeries = [0];

        realtimeAnalytics.createLiFo(realtimedewptfLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimedewptfLabel, 10);
        realtimeAnalytics.createLiFo(realtimedewptfSeries, 10);

        dataRealtimedewptfChart = {
            labels: realtimedewptfLabel,
            series: [
                realtimedewptfSeries
            ]
        };

        optionsRealtimedewptfChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: -50,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimedewptf = new Chartist.Line('#RealTimeDewPointChart', dataRealtimedewptfChart, optionsRealtimedewptfChart);
        md.startAnimationForLineChart(realtimedewptf);

        /* ----------==========     Realtime wind chill Chart initialization    ==========---------- */
        var realtimewindchillfLabelRef = [new Date()];
        var realtimewindchillfLabel = ['0s'];
        var realtimewindchillfSeries = [0];

        realtimeAnalytics.createLiFo(realtimewindchillfLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimewindchillfLabel, 10);
        realtimeAnalytics.createLiFo(realtimewindchillfSeries, 10);

        dataRealtimewindchillfChart = {
            labels: realtimewindchillfLabel,
            series: [
                realtimewindchillfSeries
            ]
        };

        optionsRealtimewindchillfChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: -50,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimewindchillf = new Chartist.Line('#RealTimeWindChillChart', dataRealtimewindchillfChart, optionsRealtimewindchillfChart);
        md.startAnimationForLineChart(realtimewindchillf);

        /* ----------==========     Realtime wind speed Chart initialization    ==========---------- */
        var realtimewindspeedmphLabelRef = [new Date()];
        var realtimewindspeedmphLabel = ['0s'];
        var realtimewindspeedmphSeries = [0];

        realtimeAnalytics.createLiFo(realtimewindspeedmphLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimewindspeedmphLabel, 10);
        realtimeAnalytics.createLiFo(realtimewindspeedmphSeries, 10);

        dataRealtimewindspeedmphChart = {
            labels: realtimewindspeedmphLabel,
            series: [
                realtimewindspeedmphSeries
            ]
        };

        optionsRealtimewindspeedmphChart = {
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

        var realtimewindspeedmph = new Chartist.Line('#RealTimeWindSpeedChart', dataRealtimewindspeedmphChart, optionsRealtimewindspeedmphChart);
        md.startAnimationForLineChart(realtimewindspeedmph);

        /* ----------==========     Realtime wind gust Chart initialization    ==========---------- */
        var realtimewindgustmphLabelRef = [new Date()];
        var realtimewindgustmphLabel = ['0s'];
        var realtimewindgustmphSeries = [0];

        realtimeAnalytics.createLiFo(realtimewindgustmphLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimewindgustmphLabel, 10);
        realtimeAnalytics.createLiFo(realtimewindgustmphSeries, 10);

        dataRealtimewindgustmphChart = {
            labels: realtimewindgustmphLabel,
            series: [
                realtimewindgustmphSeries
            ]
        };

        optionsRealtimewindgustmphChart = {
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

        var realtimewindgustmph= new Chartist.Line('#RealTimeWindGustChart', dataRealtimewindgustmphChart, optionsRealtimewindgustmphChart);
        md.startAnimationForLineChart(realtimewindgustmph);

        /* ----------==========     Realtime raining Chart initialization    ==========---------- */
        var realtimerainingLabelRef = [new Date()];
        var realtimerainingLabel = ['0s'];
        var realtimerainingSeries = [0];

        realtimeAnalytics.createLiFo(realtimerainingLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimerainingLabel, 10);
        realtimeAnalytics.createLiFo(realtimerainingSeries, 10);

        dataRealtimerainingChart = {
            labels: realtimerainingLabel,
            series: [
                realtimerainingSeries
            ]
        };

        optionsRealtimerainingChart = {
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

        var realtimeraining= new Chartist.Line('#RealTimeRainingChart', dataRealtimerainingChart, optionsRealtimerainingChart);
        md.startAnimationForLineChart(realtimeraining);

        /* ----------==========     Realtime daily raining Chart initialization    ==========---------- */
        var realtimedailyrainingLabelRef = [new Date()];
        var realtimedailyrainingLabel = ['0s'];
        var realtimedailyrainingSeries = [0];

        realtimeAnalytics.createLiFo(realtimedailyrainingLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimedailyrainingLabel, 10);
        realtimeAnalytics.createLiFo(realtimedailyrainingSeries, 10);

        dataRealtimedailyrainingChart = {
            labels: realtimedailyrainingLabel,
            series: [
                realtimedailyrainingSeries
            ]
        };

        optionsRealtimedailyrainingChart = {
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

        var realtimedailyraining= new Chartist.Line('#RealTimeDailyRainingChart', dataRealtimedailyrainingChart, optionsRealtimedailyrainingChart);
        md.startAnimationForLineChart(realtimedailyraining);

        /* ----------==========     Realtime weekly raining Chart initialization    ==========---------- */
        var realtimeweeklyrainingLabelRef = [new Date()];
        var realtimeweeklyrainingLabel = ['0s'];
        var realtimeweeklyrainingSeries = [0];

        realtimeAnalytics.createLiFo(realtimeweeklyrainingLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeweeklyrainingLabel, 10);
        realtimeAnalytics.createLiFo(realtimeweeklyrainingSeries, 10);

        dataRealtimeweeklyrainingChart = {
            labels: realtimeweeklyrainingLabel,
            series: [
                realtimeweeklyrainingSeries
            ]
        };

        optionsRealtimeweeklyrainingChart = {
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

        var realtimeweeklyraining= new Chartist.Line('#RealTimeWeeklyRainingChart', dataRealtimeweeklyrainingChart, optionsRealtimeweeklyrainingChart);
        md.startAnimationForLineChart(realtimeweeklyraining);

        /* ----------==========     Realtime monthly raining Chart initialization    ==========---------- */
        var realtimemonthlyrainingLabelRef = [new Date()];
        var realtimemonthlyrainingLabel = ['0s'];
        var realtimemonthlyrainingSeries = [0];

        realtimeAnalytics.createLiFo(realtimemonthlyrainingLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimemonthlyrainingLabel, 10);
        realtimeAnalytics.createLiFo(realtimemonthlyrainingSeries, 10);

        dataRealtimemonthlyrainingChart = {
            labels: realtimemonthlyrainingLabel,
            series: [
                realtimemonthlyrainingSeries
            ]
        };

        optionsRealtimemonthlyrainingChart = {
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

        var realtimemonthlyraining= new Chartist.Line('#RealTimeMonthlyRainingChart', dataRealtimemonthlyrainingChart, optionsRealtimemonthlyrainingChart);
        md.startAnimationForLineChart(realtimemonthlyraining);


        /* ----------==========     Realtime yearly raining Chart initialization    ==========---------- */
        var realtimeyearlyrainingLabelRef = [new Date()];
        var realtimeyearlyrainingLabel = ['0s'];
        var realtimeyearlyrainingSeries = [0];

        realtimeAnalytics.createLiFo(realtimeyearlyrainingLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeyearlyrainingLabel, 10);
        realtimeAnalytics.createLiFo(realtimeyearlyrainingSeries, 10);

        dataRealtimeyearlyrainingChart = {
            labels: realtimeyearlyrainingLabel,
            series: [
                realtimeyearlyrainingSeries
            ]
        };

        optionsRealtimeyearlyrainingChart = {
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

        var realtimeyearlyraining= new Chartist.Line('#RealTimeYearlyRainingChart', dataRealtimeyearlyrainingChart, optionsRealtimeyearlyrainingChart);
        md.startAnimationForLineChart(realtimeyearlyraining);

        /* ----------==========     Realtime solar radiation Chart initialization    ==========---------- */
        var realtimesolarradiationLabelRef = [new Date()];
        var realtimesolarradiationLabel = ['0s'];
        var realtimesolarradiationSeries = [0];

        realtimeAnalytics.createLiFo(realtimesolarradiationLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimesolarradiationLabel, 10);
        realtimeAnalytics.createLiFo(realtimesolarradiationSeries, 10);

        dataRealtimesolarradiationChart = {
            labels: realtimesolarradiationLabel,
            series: [
                realtimesolarradiationSeries
            ]
        };

        optionsRealtimesolarradiationChart = {
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

        var realtimesolarradiation= new Chartist.Line('#RealTimeSolarRadiationChart', dataRealtimesolarradiationChart, optionsRealtimesolarradiationChart);
        md.startAnimationForLineChart(realtimesolarradiation);

        /* ----------==========     Realtime UV Chart initialization    ==========---------- */
        var realtimeuvLabelRef = [new Date()];
        var realtimeuvLabel = ['0s'];
        var realtimeuvSeries = [0];

        realtimeAnalytics.createLiFo(realtimeuvLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeuvLabel, 10);
        realtimeAnalytics.createLiFo(realtimeuvSeries, 10);

        dataRealtimeuvChart = {
            labels: realtimeuvLabel,
            series: [
                realtimeuvSeries
            ]
        };

        optionsRealtimeuvChart = {
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

        var realtimeuv= new Chartist.Line('#RealTimeUltraVioletChart', dataRealtimeuvChart, optionsRealtimeuvChart);
        md.startAnimationForLineChart(realtimeuv);

        /* ----------==========     Realtime Indoor Temperature Chart initialization    ==========---------- */
        var realtimeindoorTempLabelRef = [new Date()];
        var realtimeindoorTempLabel = ['0s'];
        var realtimeindoorTempSeries = [0];

        realtimeAnalytics.createLiFo(realtimeindoorTempLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeindoorTempLabel, 10);
        realtimeAnalytics.createLiFo(realtimeindoorTempSeries, 10);

        dataRealtimeindoorTempChart = {
            labels: realtimeindoorTempLabel,
            series: [
                realtimeindoorTempSeries
            ]
        };

        optionsRealtimeindoorTempChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: -50,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        var realtimeindoorTemp = new Chartist.Line('#RealTimeIndoorTempChart', dataRealtimeindoorTempChart, optionsRealtimeindoorTempChart);
        md.startAnimationForLineChart(realtimeindoorTemp);

        /* ----------==========     Realtime Indoor Humidity Chart initialization    ==========---------- */
        var realtimeindoorHumidLabelRef = [new Date()];
        var realtimeindoorHumidLabel = ['0s'];
        var realtimeindoorHumidSeries = [0];

        realtimeAnalytics.createLiFo(realtimeindoorHumidLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeindoorHumidLabel, 10);
        realtimeAnalytics.createLiFo(realtimeindoorHumidSeries, 10);

        dataRealtimeindoorHumidChart = {
            labels: realtimeindoorHumidLabel,
            series: [
                realtimeindoorHumidSeries
            ]
        };

        optionsRealtimeindoorHumidChart = {
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

        var realtimeindoorHumid = new Chartist.Line('#RealTimeIndoorHumidityChart', dataRealtimeindoorHumidChart, optionsRealtimeindoorHumidChart);
        md.startAnimationForLineChart(realtimeindoorHumid);

        /* ----------==========     Realtime baromin Chart initialization    ==========---------- */
        var realtimebarominLabelRef = [new Date()];
        var realtimebarominLabel = ['0s'];
        var realtimebarominSeries = [0];

        realtimeAnalytics.createLiFo(realtimebarominLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimebarominLabel, 10);
        realtimeAnalytics.createLiFo(realtimebarominSeries, 10);

        dataRealtimebarominChart = {
            labels: realtimebarominLabel,
            series: [
                realtimebarominSeries
            ]
        };

        optionsRealtimebarominChart = {
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

        var realtimebaromin = new Chartist.Line('#RealTimeBarominChart', dataRealtimebarominChart, optionsRealtimebarominChart);
        md.startAnimationForLineChart(realtimebaromin);



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
                    console.log('data');
                    var data = event.data;
                    var dataPoint = JSON.parse(data).event.payloadData;
                    var temperature = dataPoint.tempf;
                    temperature = ((temperature - 32) * 5) / 9;
                    var humidity = dataPoint.humidity;
                    var windDir=dataPoint.winddir;
                    var dewptf=dataPoint.dewptf;
                    dewptf = ((dewptf - 32) * 5) / 9;
                    var windchillf=dataPoint.windchillf;
                    windchillf = ((windchillf - 32) * 5) / 9;
                    var windspeedmph=dataPoint.windspeedmph;
                    var windgustmph=dataPoint.windgustmph;
                    var rainin=dataPoint.rainin;
                    var dailyrainin=dataPoint.dailyrainin;
                    var weeklyrainin=dataPoint.weeklyrainin;
                    var monthlyrainin=dataPoint.monthlyrainin;
                    var yearlyrainin=dataPoint.yearlyrainin;
                    var solarradiation=dataPoint.solarradiation;
                    var UV=dataPoint.UV;
                    var indoortempf=dataPoint.indoortempf;
                    indoortempf = ((indoortempf - 32) * 5) / 9;
                    var indoorhumidity=dataPoint.indoorhumidity;
                    var baromin=dataPoint.baromin;

                    var currentTime = new Date();
                    var sinceText = timeDifference(currentTime, new Date(dataPoint.timeStamp), false) + " ago";
                    updateStatusCards(sinceText,temperature, humidity, windDir,windspeedmph);

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

                    realtimeWindDirLabel.push('0s');
                    realtimeWindDirLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeWindDirLabel, realtimeWindDirLabelRef);
                    realtimeWindDirSeries.push(windDir);
                    $("#realtimeWindDirLastUpdated").html(lastUpdatedText);

                    realtimedewptfLabel.push('0s');
                    realtimedewptfLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimedewptfLabel, realtimedewptfLabelRef);
                    realtimedewptfSeries.push(dewptf);
                    $("#realtimedewptfLastUpdated").html(lastUpdatedText);

                    realtimewindchillfLabel.push('0s');
                    realtimewindchillfLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimewindchillfLabel, realtimewindchillfLabelRef);
                    realtimewindchillfSeries.push(windchillf);
                    $("#realtimewindchillfLastUpdated").html(lastUpdatedText);

                    realtimewindspeedmphLabel.push('0s');
                    realtimewindspeedmphLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimewindspeedmphLabel, realtimewindspeedmphLabelRef);
                    realtimewindspeedmphSeries.push(windspeedmph);
                    $("#realtimewindspeedmphLastUpdated").html(lastUpdatedText);

                    realtimewindgustmphLabel.push('0s');
                    realtimewindgustmphLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimewindgustmphLabel, realtimewindgustmphLabelRef);
                    realtimewindgustmphSeries.push(windgustmph);
                    $("#realtimewindgustmphLastUpdated").html(lastUpdatedText);

                    realtimerainingLabel.push('0s');
                    realtimerainingLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimerainingLabel, realtimerainingLabelRef);
                    realtimerainingSeries.push(rainin);
                    $("#realtimerainingLastUpdated").html(lastUpdatedText);

                    realtimedailyrainingLabel.push('0s');
                    realtimedailyrainingLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimedailyrainingLabel, realtimedailyrainingLabelRef);
                    realtimedailyrainingSeries.push(dailyrainin);
                    $("#realtimedailyrainingLastUpdated").html(lastUpdatedText);

                    realtimeweeklyrainingLabel.push('0s');
                    realtimeweeklyrainingLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeweeklyrainingLabel, realtimeweeklyrainingLabelRef);
                    realtimeweeklyrainingSeries.push(weeklyrainin);
                    $("#realtimeweeklyrainingLastUpdated").html(lastUpdatedText);

                    realtimemonthlyrainingLabel.push('0s');
                    realtimemonthlyrainingLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimemonthlyrainingLabel, realtimemonthlyrainingLabelRef);
                    realtimemonthlyrainingSeries.push(monthlyrainin);
                    $("#realtimemonthlyrainingLastUpdated").html(lastUpdatedText);

                    realtimeyearlyrainingLabel.push('0s');
                    realtimeyearlyrainingLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeyearlyrainingLabel, realtimeyearlyrainingLabelRef);
                    realtimeyearlyrainingSeries.push(yearlyrainin);
                    $("#realtimeyearlyrainingLastUpdated").html(lastUpdatedText);

                    realtimesolarradiationLabel.push('0s');
                    realtimesolarradiationLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimesolarradiationLabel, realtimesolarradiationLabelRef);
                    realtimesolarradiationSeries.push(solarradiation);
                    $("#realtimesolarradiationLastUpdated").html(lastUpdatedText);

                    realtimeuvLabel.push('0s');
                    realtimeuvLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeuvLabel, realtimeuvLabelRef);
                    realtimeuvSeries.push(UV);
                    $("#realtimeuvLastUpdated").html(lastUpdatedText);

                    realtimeindoorTempLabel.push('0s');
                    realtimeindoorTempLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeindoorTempLabel, realtimeindoorTempLabelRef);
                    realtimeindoorTempSeries.push(indoortempf);
                    $("#realtimeindoortempLastUpdated").html(lastUpdatedText);

                    realtimeindoorHumidLabel.push('0s');
                    realtimeindoorHumidLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeindoorHumidLabel, realtimeindoorHumidLabelRef);
                    realtimeindoorHumidSeries.push(indoorhumidity);
                    $("#realtimeindoorhumidLastUpdated").html(lastUpdatedText);

                    realtimebarominLabel.push('0s');
                    realtimebarominLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimebarominLabel, realtimebarominLabelRef);
                    realtimebarominSeries.push(baromin);
                    $("#realtimebarominLastUpdated").html(lastUpdatedText);

                    realtimewindgustmphLabel.push('0s');
                    realtimewindgustmphLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimewindgustmphLabel, realtimewindgustmphLabelRef);
                    realtimewindgustmphSeries.push(windgustmph);
                    $("#realtimewindgustLastUpdated").html(lastUpdatedText);
                    updateGraphs();
                };

            }

            //refresh graphs on click on the chart
            $('.card').click(function() {
                updateGraphs();

            });

            $("#menu-toggle").click(function () {
                setTimeout(updateGraphs, 200);
            });

        }

        function updateGraphs(){
            realtimeTemp.update();
            realtimeHumid.update();
            realtimeWindDir.update();
            realtimedewptf.update();
            realtimewindchillf.update();
            realtimewindspeedmph.update();
            realtimewindgustmph.update();
            realtimeraining.update();
            realtimedailyraining.update();
            realtimeweeklyraining.update();
            realtimemonthlyraining.update();
            realtimeyearlyraining.update();
            realtimesolarradiation.update();
            realtimeuv.update();
            realtimeindoorTemp.update();
            realtimeindoorHumid.update();
            realtimebaromin.update();

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
    },

};