/***
 * Project Castle
 * Created by Chris Kipruto For Castle co. and Techbay company.
 * Code that dynamically generates views for units.
 */
$(function(){
    
     var socket = io.connect();
    
     var castleId = {
        castle_id: 3
    };
    socket.emit("getCastlePosts",castleId);
    
    socket.on("getCastlePosts", function (data) {
        console.log("retriving castles");
        console.log(data);       
     }); 
});

