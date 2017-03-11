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
    });
})


$(document).ready(function(){
    $("#historic-toggle").click(function(){
        $(".date-picker").slideToggle("slow");
    });
});


