type = ['', 'info', 'success', 'warning', 'danger'];


demo = {
    initPickColor: function() {
        $('.pick-class-label').click(function() {
            var new_class = $(this).attr('new-class');
            var old_class = $('#display-buttons').attr('data-class');
            var display_div = $('#display-buttons');
            if (display_div.length) {
                var display_buttons = display_div.find('.btn');
                display_buttons.removeClass(old_class);
                display_buttons.addClass(new_class);
                display_div.attr('data-class', new_class);
            }
        });
    },


    initDashboardPageCharts: function() {



        /* ----------==========     Realtime Temperature Chart initialization    ==========---------- */
        dataRealtimeTempChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [12, 17, 7, 17, 23, 18, 38, 10, 20, 30, 15]
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
            },
        }

        var realtimeTemp = new Chartist.Line('#realtimeTemp', dataRealtimeTempChart, optionsRealtimeTempChart);
        md.startAnimationForLineChart(realtimeTemp);




        /* ----------==========     Realtime Humidity Chart initialization    ==========---------- */
        dataRealtimeHumidChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [12, 17, 7, 17, 23, 18, 38, 10, 20, 30, 15]
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
            },
        }

        var realtimeHumid = new Chartist.Line('#realtimeHumid', dataRealtimeHumidChart, optionsRealtimeHumidChart);
        md.startAnimationForLineChart(realtimeHumid);



        /* ----------==========     Realtime State Chart initialization    ==========---------- */
        dataRealtimeStateChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [0, 1, 1, 0, 1, 0, 0, 1, 0, 1]
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
            },
        }

        var realtimeState = new Chartist.Line('#realtimeState', dataRealtimeStateChart, optionsRealtimeStateChart);
        md.startAnimationForLineChart(realtimeState);



        /* ----------==========     Realtime Occupancy Chart initialization    ==========---------- */
        dataRealtimeOccupancyChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [0, 1, 1, 0, 1, 1, 0, 1, 0, 0]
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
            },
        }

        var realtimeOccupancy = new Chartist.Line('#realtimeOccupancy', dataRealtimeOccupancyChart, optionsRealtimeOccupancyChart);
        md.startAnimationForLineChart(realtimeOccupancy);



        /* ----------==========     Realtime Metal Chart initialization    ==========---------- */
        dataRealtimeMetalChart = {
            labels: ['0s', '1s', '2s', '3s', '4s', '5s', '6s','7s', '8s', '9s', '10s'],
            series: [
                [0, 1, 1, 0, 1, 1, 0, 1, 0, 0]
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
            },
        }

        var realtimeMetal = new Chartist.Line('#realtimeMetal', dataRealtimeMetalChart, optionsRealtimeMetalChart);
        md.startAnimationForLineChart(realtimeMetal);


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
            },
        }

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
            },
        }

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
            },
        }

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
            },
        }

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
            },
        }

        var historicalMetal = new Chartist.Line('#historicalMetal', dataHistoricalMetalChart, optionsHistoricalMetalChart);
        md.startAnimationForLineChart(historicalMetal);

    },





    initGoogleMaps: function() {
        var myLatlng = new google.maps.LatLng(40.748817, -73.985428);
        var mapOptions = {
            zoom: 13,
            center: myLatlng,
            scrollwheel: false, //we disable de scroll over the map, it is a really annoing when you scroll through page
            styles: [{
                "featureType": "water",
                "stylers": [{
                    "saturation": 43
                }, {
                    "lightness": -11
                }, {
                    "hue": "#0088ff"
                }]
            }, {
                "featureType": "road",
                "elementType": "geometry.fill",
                "stylers": [{
                    "hue": "#ff0000"
                }, {
                    "saturation": -100
                }, {
                    "lightness": 99
                }]
            }, {
                "featureType": "road",
                "elementType": "geometry.stroke",
                "stylers": [{
                    "color": "#808080"
                }, {
                    "lightness": 54
                }]
            }, {
                "featureType": "landscape.man_made",
                "elementType": "geometry.fill",
                "stylers": [{
                    "color": "#ece2d9"
                }]
            }, {
                "featureType": "poi.park",
                "elementType": "geometry.fill",
                "stylers": [{
                    "color": "#ccdca1"
                }]
            }, {
                "featureType": "road",
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#767676"
                }]
            }, {
                "featureType": "road",
                "elementType": "labels.text.stroke",
                "stylers": [{
                    "color": "#ffffff"
                }]
            }, {
                "featureType": "poi",
                "stylers": [{
                    "visibility": "off"
                }]
            }, {
                "featureType": "landscape.natural",
                "elementType": "geometry.fill",
                "stylers": [{
                    "visibility": "on"
                }, {
                    "color": "#b8cb93"
                }]
            }, {
                "featureType": "poi.park",
                "stylers": [{
                    "visibility": "on"
                }]
            }, {
                "featureType": "poi.sports_complex",
                "stylers": [{
                    "visibility": "on"
                }]
            }, {
                "featureType": "poi.medical",
                "stylers": [{
                    "visibility": "on"
                }]
            }, {
                "featureType": "poi.business",
                "stylers": [{
                    "visibility": "simplified"
                }]
            }]

        }
        var map = new google.maps.Map(document.getElementById("map"), mapOptions);

        var marker = new google.maps.Marker({
            position: myLatlng,
            title: "Hello World!"
        });

        // To add the marker to the map, call setMap();
        marker.setMap(map);
    },

    showNotification: function(from, align) {
        color = Math.floor((Math.random() * 4) + 1);

        $.notify({
            icon: "notifications",
            message: "Welcome to <b>Material Dashboard</b> - a beautiful freebie for every web developer."

        }, {
            type: type[color],
            timer: 4000,
            placement: {
                from: from,
                align: align
            }
        });
    }



}