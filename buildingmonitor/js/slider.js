/**
 * Created by yesin on 3/8/17.
 */

$('#temp-slider').slider({
    formatter: function(value) {
        return 'Current value: ' + value;
    }
});

// date range pcker
$(document).ready( function () {
    $('input[name="daterange"]').daterangepicker({
        opens:"center",
        drops:"up"
    },function(start, end, label) {
        var fromDate = new Date(start).valueOf();
        var endDate = new Date(end).valueOf();
        custom.functions.getProviderData(fromDate, endDate);

    });
});


$(document).ready(function(){
    $("#historic-toggle").click(function(){
        heatMapManagement.functions.enableDisableHistoricalData();
        $(".date-picker").slideToggle("slow");
    });
});


