/***
 * Project Castle
 * Created by Chris Kipruto For Castle co. and Techbay company.
 * Code that dynamically generates views for units.
 */
$(function(){
    
     var socket = io.connect();
    
     var user_id = {
        user_id: getCookie("user_id")
    };
    socket.emit("getUnits",user_id);
    
    socket.on("getUnits", function (data) {
        console.log("retriving units");
        console.log(data);
        displayUnitsTable(data);
     });

     
   });

function displayUnitsTable(unitsobj){
    
    var tableBody = $("#unitsBody");
    var tableBody_n = $("#notificationsBody");


      var size = unitsobj.length;
    //  size = 1;
        
        for(var j = 0; j < size; j++){
            
             i = j+1;
             
             //for units
            var tableRow = document.createElement("tr");
            var trimmedUnit = (unitsobj[j].unit_code).replace(" ", "");
            $(tableRow).attr("id",trimmedUnit);
            
            $(tableRow).addClass("unit_row"); 
            if(j == 0){
               // $(tableRow).addClass("alert alert-warning");
                //$(tableRow).addClass("selected");  
               // $(tableRow).addClass("active_nav"); 
            }else{
               // $(tableRow).addClass("alert alert-warning");  
            }
            //$(tableRow).append('<td class="mdl-badge " data-badge="3/5">'+i+'</td>');
             $(tableRow).append('<td >'+i+'</td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric ">'+unitsobj[j].unit_code+'</td>');
            $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].name+'</td>');
           // $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].fname+'</td>');
           // $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].lname+'</td>');
            

            //for notifications
             var tableRow_n = document.createElement("tr");
            $(tableRow_n).append('<td>'+i+'</td>');
           $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].unit_code+'</td>');
            //$(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].fname+'</td>');
          //  $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].lname+'</td>');
            $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">2 days, 5:42 hrs to go</td>');
            $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric"><i class="mdl-color-text--Green-500 material-icons" role="presentation">alarm</i><input type="checkbox" id="switch1" class="mdl-switch__input"></td>');
          
           $(tableBody).append(tableRow);
           $(tableBody_n).append(tableRow_n);
            
            if(j == 0){
                $("#li_u_class_group").text(unitsobj[i].class_group);
                $("#li_u_dept").text(unitsobj[i].dept);
                
                 $("#li_class_group").text(unitsobj[i].class_group);
                $("#li_dept").text(unitsobj[i].dept);
            }
        } 
     
}


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