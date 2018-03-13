
var alerts = [];

function historyGraphRefresh() {
    analyticsHistory.initDashboardPageCharts();
}

function realtimeGraphRefresh(wsEndpoint) {
    realtimeAnalytics.initDashboardPageCharts(wsEndpoint);
}

function timeDifference(current, previous, isshort) {
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


function displayAlerts(wsEndpoint) {
    connect(wsEndpoint);
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
                var alert = JSON.parse(data).event.payloadData;
                alerts.unshift(alert);
                if (alerts.length > 5) {
                    alerts = alerts.slice(0, -1);
                }
                var realtimeAlerts = $('#realtime_alerts');
                realtimeAlerts.find('tbody').empty();
                for (var i = 0; i < alerts.length; i++) {
                    var row = '<tr ' + (alerts[i].level === 'Warn' ? 'style="background-color: #faffd7">' : '>') +
                        '<td>' + new Date().toLocaleString() + '</td>' +
                        '<td>' + alerts[i].message + '</td>' +
                        '</tr>';
                    realtimeAlerts.find('tbody').append(row);
                }
            }
        }
    }

    function disconnect() {
        if (ws != null) {
            ws.close();
            ws = null;
        }
    }
}

//update the card details
function updateStatusCards(sincetext, varOne, varTwo, varThree, varFour) {

    //temperature status
    $("#card1").html(precise_round(varOne,3) + units1);

    //humidity status
    $("#card2").html(varTwo + units2);

    //wind status
    $("#card3").html(varThree + units3);

    //wind speed
    $("#card4").html(precise_round(varFour, 3) + units4);

}


function precise_round(num, decimals) {
    var t = Math.pow(10, decimals);
    return (Math.round((num * t) + (decimals > 0 ? 1 : 0) * (Math.sign(num) * (10 / Math.pow(100, decimals)))) / t).toFixed(decimals);
}


function upgradeFirmware() {
    var url = $("#firmwareUrl").val();
    console.log('firmware url: ' + url);


}

function uploadConfiguration() {
    var executionPlan = $("#executionPlan").val();
    console.log('exec plan : ' + executionPlan);


}