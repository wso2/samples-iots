function toggleDiv(divId) {
    var x = document.getElementById(divId);
    if (x.style.display === "none") {
        x.style.display = "block";
    } else {
        x.style.display = "none";
    }
}

function redirect(ele) {
    var act = $(".col-md-4").hasClass("resize");
    if (!act) {
        $(".his").toggleClass("setHistorical");
    }

    var act1 = $(".card").hasClass("temp");
    if (act1) {
        $(".real").toggleClass("resize");
    }
    $('#' + ele.id).toggleClass('modal');
    $('div.card-chart').toggleClass('maxHeight');
    $('div.card').toggleClass('padzero');
    $('.ct-chart').toggleClass('fillcontent');
    $('.ct-chart').toggleClass('setheight');

    var histab=$("#historicalTab").hasClass('active');
    if(histab)
    analyticsHistory.updateGraphs();

}

//hide the date range initially
$("#dateR").hide();
$('#daterangebar').hide();

$("#historicalTab").click(function () {//show date range on click on historical tab
    $("#dateR").show();
    $("#daterangebar").show();
});

$("#realtimeTab").click(function () {//hide tab on click on realtime tab
    $("#dateR").hide();
    $("#daterangebar").hide();
});

//SideBar toggle script
$("#menu-toggle").click(function (e) {
    e.preventDefault();
    // $(".ct-chart").toggleClass('ct-golden-section');
    $("#wrapper").toggleClass("toggled");
    $(".real").toggleClass("resize");
    $(".real").toggleClass("temp");
    toggleDiv("statusCards");
    $('#icon').toggleClass('fa fa-angle-double-left fa fa-angle-double-right');
    setTimeout(analyticsHistory.updateGraphs, 250);
});

//to show a loading bar when charts are loading
$('#curtain').hide();

function chartsLoading(){
    $('#daterangebar').hide();
    $('#curtain').show();

}
function chartsLoaded() {
    $('#curtain').hide();
    $('#daterangebar').show();

}