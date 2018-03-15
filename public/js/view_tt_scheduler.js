/***
 * Project Castle
 * Created by Chris Kipruto For Castle co. and Techbay company.
 * Code that dynamically generates views for timetable.
 */
$(function(){
    
     var socket = io.connect();
     var doc = new jsPDF();
    var specialElementHandlers = {
        '#editor': function (element, renderer) {
            return true;
        }
    };

     /***
      * Suppposed to emit user info
      */
     var user_id = {
        user_id: getCookie("user_id")
    };
    
    var status = getCookie("status");
    if(status != "Approved Timetable"){
         $("#status_tt").text("DRAFT TIMETABLE");
         $("#downloadTT").hide();
         $("#sendDraft").show();
    }else{
         $("#status_tt").text("APPROVED TIMETABLE");
         $("#downloadTT").show();
         $("#sendDraft").hide();
    }
   
    
    var codTimeUpdate =  getCookie("cod_time_update");
    $("#cod_time_update").text("L.Updated "+codTimeUpdate);
    
    var group = $.trim(getCookie("class_group"));
    var data = {
        class_group:group,
        dept:"CS"
    }
    
     $("#li_class_group").text(group);
     $("#li_dept").text("CS");
     
    socket.emit("viewGroupTimetable",data);
    
    socket.on("viewGroupTimetable", function (data) {
        console.log("retriving timetable");
        console.log(data);
        displayTable(data);
     
     });
    
     $("#sendDraft").on("click",function(){
         var data = {
             group:group,
             dept:"CS",
             status:"Draft Submitted"
         }
          socket.emit("updateTimetableStatus",data);
     });
    $("#downloadTT").on("click",function(){
        html2canvas($('#timetable-tab'), {
         onrendered: function(canvas) { 
             var img = canvas.toDataURL("image/png"),
                uri = img.replace(/^data:image\/[^;]/, 'data:application/octet-stream');
               var doc = new jsPDF('p', 'mm');
               doc.addImage(img, 'png', 2, 2,200,105);
               doc.save('CS_'+group+'.pdf');
         }
       }); 
    });

     socket.on("updateTimetableStatus", function (data) {
        if(data.success == 1){
           alert("Successfully updated !");
              location.href= 'scheduler_select'; 
        }
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

