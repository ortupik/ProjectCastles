/***
 * Project Castle
 * Created by Chris Kipruto For Castle co. and Techbay company.
 * Code that dynamically generates views for timetable.
 */
$(function(){
    
     var socket = io.connect();
     /***
      * Suppposed to emit user info
      */
     var user_id = {
        user_id: getCookie("user_id")
    };
    
    var codTimeUpdate =  getCookie("cod_time_update");
   // $("#cod_time_update").text("L.Updated "+codTimeUpdate);
  
    socket.emit("viewTimetable",user_id);
    
    socket.on("viewTimetable", function (data) {
        console.log("retriving timetable");
        console.log(data);
        displayTable(data);
     
     });
      var details = {
        currentTime:8,
        day:"MON"
    };
    socket.emit("getClassRooms",details);
    
    socket.on("getClassRooms", function (data) {
        console.log("retriving classes");
        console.log(data);     
     });
    
      
   });
   
function getCookie(c_name){
 var i,x,y,ARRcookies=document.cookie.split(";");
    for (i=0;i<ARRcookies.length;i++){
      x = ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
      y  = ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
      x=x.replace(/^\s+|\s+$/g,"");
      if (x==c_name){
        return unescape(y);
        }
      }
}
function displayTable(viewObj){
    var tableBody = $("#view_body");
    
 if(viewObj[0].length < 1){
         createEmptyTables('MON',tableBody);
     }else{
         createTables('MON',tableBody,viewObj[0]);
     }
      if(viewObj[1].length < 1){
         createEmptyTables('TUE',tableBody);
     }else{
         createTables('TUE',tableBody,viewObj[1]);
     }
      if(viewObj[2].length < 1){
         createEmptyTables('WED',tableBody);
     }else{
         createTables('WED',tableBody,viewObj[2]);
     }
      if(viewObj[3].length < 1){
         createEmptyTables('THUR',tableBody);
     }else{
         createTables('THUR',tableBody,viewObj[3]);
     }
      if(viewObj[4].length < 1){
         createEmptyTables('FRI',tableBody);
     }else{
        createTables('FRI',tableBody,viewObj[4]);
     }
     
     
        $(".unit_row").on("click",function(){ 
           alert($(this).prop("id"));
           $(".unit_row").removeClass("active_nav");
            $(".unit_row").removeClass("selected");
            $(this).addClass("selected");
            $(this).addClass("active_nav");
            
              $(".ICS2311").addClass("selected");

           
     });

 } 

function createTables(day,tableBody,dayArray){
   var dayRow = document.createElement("tr");
      
   $(tableBody).append($(dayRow));
   
    var tableHeader = document.createElement("th");
    $(tableHeader).append("<br>"+day);
    
   
    $(dayRow).append($(tableHeader));
    
    var initTime = 7;
    var size = dayArray.length;

         var sheduledTimeUpdate = dayArray[0]['created_at'];
         $("#scheduler_time_update").text("Last Updated: "+sheduledTimeUpdate);
   
    for( var i = 0; i < 12; i++ ){          
        
          var tableDef = document.createElement("td");
          $(tableDef).addClass("container");
           $(dayRow).attr("id",makeid());
          var id = $(dayRow).attr("id");
         // alert(id);
         dragula([document.getElementById(id)],[document.getElementById(id)]);
         
        
         
            for(var j = 0; j < size; j++){
                
                if(dayArray[j]['s_time'] == initTime){
                 
                  $(tableDef).attr("colspan",dayArray[j]['hours']);
                   $(tableDef).addClass("event");
                  
                   i+=dayArray[j]['hours']-1;
                   initTime+=dayArray[j]['hours']-1;
                   
                   /* * Note to Self
                    * Guess what - you can use [] or . to access json atttribute !
                    */
                  var trimmedUnit = (dayArray[j]['unit_code']).replace(" ", "");
                   $(tableDef).addClass(trimmedUnit);
                   
                  $(tableDef).append(dayArray[j]['unit_code']);
                     $(tableDef).append("<br>");
                    $(tableDef).append(dayArray[j]['username']);
                     $(tableDef).append("<br>");
                    $(tableDef).append(dayArray[j]['room']);
                    $(tableDef).append("<br>");
                   
                    
                     if(j == 0){
                        $("#li_class_group").text(dayArray[j].class_group);
                        $("#li_dept").text(dayArray[j].dept);
                    }
                   
               }else{
                 
               }
               
            }
                 
            initTime++;
 
        $(dayRow).append($(tableDef));     
        
    }

}
function createEmptyTables(day,tableBody){
   var dayRow = document.createElement("tr");
   $(tableBody).append($(dayRow));
   
    var tableHeader = document.createElement("th");
    $(tableHeader).append("<br>"+day);
   
    $(dayRow).append($(tableHeader));
    
   
    for( var i = 0; i < 12; i++ ){          
        
          var tableDef = document.createElement("td");
          $(tableDef).addClass("container");
              
            $(tableDef).attr("colspan",1);
            $(tableDef).append("");
               $(tableDef).append("<br>");
              $(tableDef).append("");
               $(tableDef).append("<br>");
              $(tableDef).append("");
              $(tableDef).append("<br>");

           $(dayRow).append($(tableDef));     
        
    }

   
}
function makeid()
{
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for( var i=0; i < 5; i++ )
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}

