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
    
      var group = $.trim(getCookie("class_group"));
        var data = {
            class_group:group,
            dept:"CS"
        }
    socket.emit("getGroupUnits",data);
    
    socket.on("getGroupUnits", function (data) {
        console.log("retriving units");
        console.log(data);
        displayUnitsTable(data);
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
               // $(tableRow).addClass("selected");  
                //$(tableRow).addClass("active_nav"); 
            }else{
               // $(tableRow).addClass("alert alert-warning");  
            }
            //$(tableRow).append('<td class="mdl-badge " data-badge="3/5">'+i+'</td>');
             $(tableRow).append('<td >'+i+'</td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric ">'+unitsobj[j].unit_code+'</td>');
            $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].fname+" "+unitsobj[j].lname+'</td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].hours+'</td>');
           if(unitsobj[j].status == "NOT SET"){
                       $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><label class="label label-warning">NOT SET </label></td>');
           }else{
                       $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><label class="label label-success">SET ON '+unitsobj[j].status+' </label></td>');
           }
            $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><button  class="btn btn-sm btn-default notify_btn"><i class="material-icons">notifications</i></button></td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><button class="btn btn-sm btn-default " data-toggle="modal" data-target="#smsModal"><i class="material-icons">message</i></button></td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><button class="btn btn-sm btn-default " data-toggle="modal" data-target="#emailModal"><i class="material-icons">email</i></button></td>');

            //for notifications
             var tableRow_n = document.createElement("tr");
            $(tableRow_n).append('<td>'+i+'</td>');
           $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].unit_code+'</td>');
            $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].fname+'</td>');
            $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].lname+'</td>');
            $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric">2 days, 5:42 hrs to go</td>');
            $(tableRow_n).append('<td class="mdl-data-table__cell--non-numeric"><i class="mdl-color-text--Green-500 material-icons" role="presentation">alarm</i><input type="checkbox" id="switch1" class="mdl-switch__input"></td>');
          
           $(tableBody).append(tableRow);
           $(tableBody_n).append(tableRow_n);
            
            if(j == 0){
                $("#li_u_class_group").text(unitsobj[i].class_group);
                $("#li_u_dept").text(unitsobj[i].dept);
            }
            
            
        }
         $(".notify_btn").on("click",function(){
             $.ajax({
              type: "POST",
              url: "http://localhost:8000/timetable/sendNotification",
              dataType: "json",
              data: ""
            }).done(function(results) { 
               
               if(results['status'] == 1){
                  
                }else{
                     alert(results['message']); 
                }
            });
            setTimeout(function(){
                 alert("Successfully Sent Notification!"); 
               var notification = document.querySelector('.mdl-js-snackbar');
                var data = {
                  message: 'Notifications Sent',
                  actionHandler: function(event) {},
                  actionText: 'All Done',
                  timeout: 5000
                };
                notification.MaterialSnackbar.showSnackbar(data);
            },3000);
            
         
        });
         $(".sendSMS").on("click",function(){
             
             var url  = "https://api.africastalking.com/restless/send?username=chrisadriane&Apikey=0c96e32597f9d00c0363f8ade6fd8377320f67bd12f11d324811878103d8c7c2&to=+254728318609&message=Dear Mr.Opiyo,Please Submit your preferences for the timetable in the Integrated Dekut Timetable System. Regards CS Scheduler.";
             $.ajax({
              type: "GET",
              url: url,
              dataType: "xml",
              data: ""
            }).done(function(results) { 
                
            });
            
            setTimeout(function(){
                alert("Successfully Sent Message!"); 
                 $("#smsModal").modal("hide");
                $("#smsAllModal").modal("hide");
                var notification = document.querySelector('.mdl-js-snackbar');
                var data = {
                  message: 'Successfully  Sent Message !',
                  actionHandler: function(event) {},
                  actionText: 'All Done',
                  timeout: 5000
                };
                notification.MaterialSnackbar.showSnackbar(data);
                
               
            },3000);
            
            
        });
        
        $(".sendMail").on("click",function(){
            socket.emit("sendMail",{});
        });
        
         socket.on("sendMail",function(){
                 alert("Successfully Sent Email!"); 
                  $("#emailModal").modal("hide");
                $("#emailAllModal").modal("hide");
             var notification = document.querySelector('.mdl-js-snackbar');
                var data = {
                  message: 'You have Sent Email !',
                  actionHandler: function(event) {},
                  actionText: 'All Done',
                  timeout: 5000
                };
                notification.MaterialSnackbar.showSnackbar(data);
            });
  
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

   });
