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
        /* ----------==========     Realtime Chart One initialization    ==========---------- */
        var realtimeChartOneLabelRef = [new Date()];
        var realtimeChartOneLabel = ['0s'];
        var realtimeChartOneSeries = [0];

         realtimeAnalytics.createLiFo(realtimeChartOneLabelRef, 10);
         realtimeAnalytics.createLiFo(realtimeChartOneLabel, 10);
         realtimeAnalytics.createLiFo(realtimeChartOneSeries, 10);

        dataRealtimeChartOneChart = {
            labels: realtimeChartOneLabel,
            series: [
                realtimeChartOneSeries
            ]
        };

        optionsRealtimeChartOneChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            responsive:true,
            maintainAspectRatio : false,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 0
            },



        };

        var realtimeChartOne = new Chartist.Line('#RealTimeChartOne', dataRealtimeChartOneChart, optionsRealtimeChartOneChart);
        md.startAnimationForLineChart(realtimeChartOne);

        /* ----------==========     Realtime Chart Two initialization    ==========---------- */
        var realtimeChartTwoLabelRef = [new Date()];
        var realtimeChartTwoLabel = ['0s'];
        var realtimeChartTwoSeries = [0];

        realtimeAnalytics.createLiFo(realtimeChartTwoLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeChartTwoLabel, 10);
        realtimeAnalytics.createLiFo(realtimeChartTwoSeries, 10);

        dataRealtimeChartTwoChart = {
            labels: realtimeChartTwoLabel,
            series: [
                realtimeChartTwoSeries
            ]
        };

        optionsRealtimeChartTwoChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0.2
            }),
            showArea: false,
            maintainAspectRatio : false,
            responsive:false,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartTwo = new Chartist.Line('#RealTimeChartTwo', dataRealtimeChartTwoChart, optionsRealtimeChartTwoChart);
        md.startAnimationForLineChart(realtimeChartTwo);

        /* ----------==========     Realtime Chart Three initialization    ==========---------- */
        var realtimeChartThreeLabelRef = [new Date()];
        var realtimeChartThreeLabel = ['0s'];
        var realtimeChartThreeSeries = [0];

        realtimeAnalytics.createLiFo(realtimeChartThreeLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeChartThreeLabel, 10);
        realtimeAnalytics.createLiFo(realtimeChartThreeSeries, 10);

        dataRealtimeChartThreeChart = {
            labels: realtimeChartThreeLabel,
            series: [
                realtimeChartThreeSeries
            ]
        };

        optionsRealtimeChartThreeChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartThree = new Chartist.Line('#RealTimeChartThree', dataRealtimeChartThreeChart, optionsRealtimeChartThreeChart);
        md.startAnimationForLineChart(realtimeChartThree);

        /* ----------==========     Realtime Chart Four initialization    ==========---------- */
        var realtimeChartFourLabelRef = [new Date()];
        var realtimeChartFourLabel = ['0s'];
        var realtimeChartFourSeries = [0];

        realtimeAnalytics.createLiFo(realtimeChartFourLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeChartFourLabel, 10);
        realtimeAnalytics.createLiFo(realtimeChartFourSeries, 10);

        dataRealtimeChartFourChart = {
            labels: realtimeChartFourLabel,
            series: [
                realtimeChartFourSeries
            ]
        };

        optionsRealtimeChartFourChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartFour = new Chartist.Line('#RealTimeChartFour', dataRealtimeChartFourChart, optionsRealtimeChartFourChart);
        md.startAnimationForLineChart(realtimeChartFour);

        /* ----------==========     Realtime Chart Five initialization    ==========---------- */
        var realtimewChartFiveLabelRef = [new Date()];
        var realtimewChartFiveLabel = ['0s'];
        var realtimewChartFiveSeries = [0];

        realtimeAnalytics.createLiFo(realtimewChartFiveLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimewChartFiveLabel, 10);
        realtimeAnalytics.createLiFo(realtimewChartFiveSeries, 10);

        dataRealtimewChartFiveChart = {
            labels: realtimewChartFiveLabel,
            series: [
                realtimewChartFiveSeries
            ]
        };

        optionsRealtimewChartFiveChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartFive = new Chartist.Line('#RealTimeChartFive', dataRealtimewChartFiveChart, optionsRealtimewChartFiveChart);
        md.startAnimationForLineChart(realtimeChartFive);

        /* ----------==========     Realtime Chart Six initialization    ==========---------- */
        var realtimeChartSixLabelRef = [new Date()];
        var realtimeChartSixLabel = ['0s'];
        var realtimeChartSixSeries = [0];

        realtimeAnalytics.createLiFo(realtimeChartSixLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeChartSixLabel, 10);
        realtimeAnalytics.createLiFo(realtimeChartSixSeries, 10);

        dataRealtimeChartSixChart = {
            labels: realtimeChartSixLabel,
            series: [
                realtimeChartSixSeries
            ]
        };

        optionsRealtimeChartSixChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartSix = new Chartist.Line('#RealTimeChartSix', dataRealtimeChartSixChart, optionsRealtimeChartSixChart);
        md.startAnimationForLineChart(realtimeChartSix);

        /* ----------==========     Realtime Chart Seven initialization    ==========---------- */
        var realtimeChartSevenLabelRef = [new Date()];
        var realtimeChartSevenLabel = ['0s'];
        var realtimeChartSevenSeries = [0];

        realtimeAnalytics.createLiFo(realtimeChartSevenLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeChartSevenLabel, 10);
        realtimeAnalytics.createLiFo(realtimeChartSevenSeries, 10);

        dataRealtimeChartSevenChart = {
            labels: realtimeChartSevenLabel,
            series: [
                realtimeChartSevenSeries
            ]
        };

        optionsRealtimeChartSevenChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartSeven= new Chartist.Line('#RealTimeChartSeven', dataRealtimeChartSevenChart, optionsRealtimeChartSevenChart);
        md.startAnimationForLineChart(realtimeChartSeven);

        /* ----------==========     Realtime Chart Eight initialization    ==========---------- */
        var realtimeChartEightLabelRef = [new Date()];
        var realtimeChartEightLabel = ['0s'];
        var realtimeChartEightSeries = [0];

        realtimeAnalytics.createLiFo(realtimeChartEightLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeChartEightLabel, 10);
        realtimeAnalytics.createLiFo(realtimeChartEightSeries, 10);

        dataRealtimeChartEightChart = {
            labels: realtimeChartEightLabel,
            series: [
                realtimeChartEightSeries
            ]
        };

        optionsRealtimeChartEightChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartEight= new Chartist.Line('#RealTimeChartEight', dataRealtimeChartEightChart, optionsRealtimeChartEightChart);
        md.startAnimationForLineChart(realtimeChartEight);

        /* ----------==========     Realtime Chart nine initialization    ==========---------- */
        var realtimeChartNineLabelRef = [new Date()];
        var realtimeChartNineLabel = ['0s'];
        var realtimeChartNineSeries = [0];

        realtimeAnalytics.createLiFo(realtimeChartNineLabelRef, 10);
        realtimeAnalytics.createLiFo(realtimeChartNineLabel, 10);
        realtimeAnalytics.createLiFo(realtimeChartNineSeries, 10);

        dataRealtimeChartNineChart = {
            labels: realtimeChartNineLabel,
            series: [
                realtimeChartNineSeries
            ]
        };

        optionsRealtimeChartNineChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            chartPadding: {
                top: 20,
                right: 0,
                bottom: 0,
                left: 10
            }
        };

        var realtimeChartNine= new Chartist.Line('#RealTimeChartNine', dataRealtimeChartNineChart, optionsRealtimeChartNineChart);
        md.startAnimationForLineChart(realtimeChartNine);





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
                    var varOne=dataPoint[typeParameter1];
                    var varTwo=dataPoint[typeParameter2];
                    var varThree=dataPoint[typeParameter3];
                    var varFour=dataPoint[typeParameter4];
                    var varFive=dataPoint[typeParameter5];
                    var varSix=dataPoint[typeParameter6];
                    var varSeven=dataPoint[typeParameter7];
                    var varEight=dataPoint[typeParameter8];
                    var varnine=dataPoint[typeParameter9];


                    var currentTime = new Date();
                    var sinceText = timeDifference(currentTime, new Date(dataPoint.timeStamp), false) + " ago";
                    updateStatusCards(sinceText,varOne, varTwo, varThree,varFour);

                    var lastUpdatedTime = realtimeChartOneLabelRef[realtimeChartOneLabelRef.length - 1];
                    var lastUpdatedText = "<i class=\"material-icons\">access_time</i> updated "+timeDifference(currentTime, lastUpdatedTime)+" ago";

                    realtimeChartOneLabel.push('0s');
                    realtimeChartOneLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartOneLabel, realtimeChartOneLabelRef);
                    realtimeChartOneSeries.push(varOne);
                    $("#realtimeChartOneLastUpdated").html(lastUpdatedText);

                    realtimeChartTwoLabel.push('0s');
                    realtimeChartTwoLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartTwoLabel, realtimeChartTwoLabelRef);
                    realtimeChartTwoSeries.push(varTwo);
                    $("#realtimeChartTwoLastUpdated").html(lastUpdatedText);

                    realtimeChartThreeLabel.push('0s');
                    realtimeChartThreeLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartThreeLabel, realtimeChartThreeLabelRef);
                    realtimeChartThreeSeries.push(varThree);
                    $("#realtimeChartThreeLastUpdated").html(lastUpdatedText);

                    realtimeChartFourLabel.push('0s');
                    realtimeChartFourLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartFourLabel, realtimeChartFourLabelRef);
                    realtimeChartFourSeries.push(varFour);
                    $("#realtimeChartFourLastUpdated").html(lastUpdatedText);

                    realtimewChartFiveLabel.push('0s');
                    realtimewChartFiveLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimewChartFiveLabel, realtimewChartFiveLabelRef);
                    realtimewChartFiveSeries.push(varFive);
                    $("#realtimewChartFiveLastUpdated").html(lastUpdatedText);

                    realtimeChartSixLabel.push('0s');
                    realtimeChartSixLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartSixLabel, realtimeChartSixLabelRef);
                    realtimeChartSixSeries.push(varSix);
                    $("#realtimeChartSixLastUpdated").html(lastUpdatedText);

                    realtimeChartSevenLabel.push('0s');
                    realtimeChartSevenLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartSevenLabel, realtimeChartSevenLabelRef);
                    realtimeChartSevenSeries.push(varSeven);
                    $("#realtimeChartSevenLastUpdated").html(lastUpdatedText);

                    realtimeChartEightLabel.push('0s');
                    realtimeChartEightLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartEightLabel, realtimeChartEightLabelRef);
                    realtimeChartEightSeries.push(varEight);
                    $("#realtimeChartEightLastUpdated").html(lastUpdatedText);

                    realtimeChartNineLabel.push('0s');
                    realtimeChartNineLabelRef.push(currentTime);
                    realtimeAnalytics.calcTimeDiff(realtimeChartNineLabel, realtimeChartNineLabelRef);
                    realtimeChartNineSeries.push(varnine);
                    $("#realtimeChartNineLastUpdated").html(lastUpdatedText);

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
            realtimeChartOne.update();
            realtimeChartTwo.update();
            realtimeChartThree.update();
            realtimeChartFour.update();
            realtimeChartFive.update();
            realtimeChartSix.update();
            realtimeChartSeven.update();
            realtimeChartEight.update();
            realtimeChartNine.update();
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