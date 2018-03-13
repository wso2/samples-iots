<div id="mapid" style="width: 100%; height:50%"></div>
<script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
        integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
        crossorigin=""></script>
<script>
    //intialising and setting the map
    var mymap = L.map('mapid').setView([7.9, 80.56274], 7);
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 7,
        id: 'mapbox.streets',
        accessToken: 'pk.eyJ1IjoibGFzaGFuIiwiYSI6ImNqYmc3dGVybTFlZ3UyeXF3cG8yNGxsdzMifQ.n3QEq0-g5tVFmsQxn3JZ-A',
        maxWidth: 200,
        maxHeight: 200
    }).addTo(mymap);

    var marker = L.marker([<%=lat.getString("value")%>, <%=lon.getString("value")%>]).addTo(mymap);
    mymap.fitBounds([[<%=lat.getString("value")%>, <%=lon.getString("value")%>]]);
    marker.bindPopup("<b><%=device.getString("name")%></b>").openPopup();
</script>