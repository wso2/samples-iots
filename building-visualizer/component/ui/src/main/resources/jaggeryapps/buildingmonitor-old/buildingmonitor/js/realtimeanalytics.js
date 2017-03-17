/**
 * Created by lasantha on 3/7/17.
 */

// ws://localhost:9763/outputwebsocket/{publisher_name}

var publisher_name = "";

function connect(){
    var socket;
    var host = "ws://localhost:9763/outputwebsocket/"+publisher_name;

    try{
        var socket = new WebSocket(host);

        socket.onopen = function(){
            console.log('Socket Status: '+socket.readyState+' (open)');
        };

        socket.onmessage = function(msg){
            console.log('Received: '+msg.data);
        };

        socket.onclose = function(){
            console.log('Socket Status: '+socket.readyState+' (Closed)');
        };

    } catch(exception){
        console.log('Error'+exception);
    }



}//End connect

$(document).ready(function() {

    if(!("WebSocket" in window)){
        console.log('WebSocket is not supported by this browser.');
    }else{
        //The user has WebSockets

        connect();

    }//End else

});