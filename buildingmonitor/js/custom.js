/**
 * Created by lasantha on 3/6/17.
 */
google.load("visualization", "1");
var data = undefined;
var timeline = undefined;

// Set callback to run when API is loaded
google.setOnLoadCallback(drawVisualization);

function drawVisualization() {
    // Create and populate a data table.
    data = new google.visualization.DataTable();
    data.addColumn('datetime', 'start');
    data.addColumn('datetime', 'end');
    data.addColumn('string', 'content');

    // specify options
    var options = {
        "width":  "100%",
        "style": "box",
        "showNavigation":true
    };
    // Instantiate our timeline object.
    timeline = new links.Timeline(document.getElementById('timeline'), options);

    // Draw our timeline with the created data and options
    timeline.draw(data);
    timeline.setAutoScale(true);


    // set a custom range from -2 minute to +3 minutes current time
    var start = new Date((new Date()).getTime() - 2 * 60 * 1000);
    var end   = new Date((new Date()).getTime() );
    timeline.setVisibleChartRange(start, end);

}