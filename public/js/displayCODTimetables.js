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
    socket.emit("getTimetables",data);
    
    socket.on("getTimetables", function (data) {
        console.log(data);
        displayTimetablesTable(data);
     });

    $("#addTimetable").on("click",function(){
          var academicYear = $("#inputAcad option:selected" ).text();
          var intake = $("#inputIntake option:selected" ).text();
          var dept = "CS";
          var group = $("#inputGroups option:selected" ).text();
          
          var data = {
              academic_year:academicYear,
              intake:intake,
              dept:dept,
              group:group
          }
        socket.emit("newTimetable",data);
          
    });
    
    socket.on("newTimetable", function (data) {
        if(data.success == 1){
           alert("Successfully inserted !")
           location.reload();
        }
   });
   

function displayTimetablesTable(obj){
    
    var tableBody = $("#timetablesBody");

      var size = obj.length;
    //  size = 1;
        
        for(var j = 0; j < size; j++){
            
             i = j+1;
             
            var tableRow = document.createElement("tr");
            $(tableRow).addClass("unit_row"); 
             $(tableRow).append('<td >'+i+'</td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric ">'+obj[j].academic_year+'</td>');
            $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+obj[j].intake+'</td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+obj[j].dept+'</td>');
          $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+obj[j].group+'</td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+obj[j].created_at+'</td>');
          $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+obj[j].updated_at+'</td>');
           switch(obj[j].status){
                 case "Awaiting Draft":
                    $(tableRow).append('<td class="mdl-data-table__cell--non-numeric "><label class="label label-default">'+obj[j].status+'</label></td>');
                    $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"></td>');
                break;
                  case "Draft Submitted":
                    $(tableRow).append('<td class="mdl-data-table__cell--non-numeric "><label class="label label-primary">'+obj[j].status+'</label></td>');
                     $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><a href="cod_draft_timetable" class="mdl-button mdl-button--icon mdl-button--primary mdl-button--raised view_btn" status="'+obj[j].status+'" group='+obj[j].group+'> <i class="material-icons">forward</i></a></td>');
                break;
                  case "Approved Timetable":
                    $(tableRow).append('<td class="mdl-data-table__cell--non-numeric "><label class="label label-success">'+obj[j].status+'</label></td>');
                     $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><a href="cod_draft_timetable" class="mdl-button mdl-button--icon mdl-button--primary mdl-button--raised view_btn" status="'+obj[j].status+'" group='+obj[j].group+'> <i class="material-icons">forward</i></a></td>');  
                break;
                  case "Modification Requested":
                    $(tableRow).append('<td class="mdl-data-table__cell--non-numeric "><label class="label label-danger">'+obj[j].status+'</label></td>');
                    $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"></td>'); 
                break;
           }
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><a class="mdl-button mdl-button--icon mdl-button--accent mdl-button--raised  delete_btn"  group='+obj[j].group+'> <i class="material-icons">delete</i></a></td>');
           $(tableBody).append(tableRow);
            
        }
          $(".view_btn").on("click",function(){
            var group = $(this).attr("group");
            setCookie("class_group",group,1);
            
            var status = $(this).attr("status");
            setCookie("status",status,1);
        });
        
      $(".delete_btn").on("click",function(){
         var group = $(this).attr("group");
         var data = {
             group:group,
             dept:"CS"
         }
          socket.emit("deleteTimetable",data);
     });
     socket.on("deleteTimetable", function (data) {
        if(data.success == 1){
           alert("Deleted Successfully !");
           location.reload();
         }
     });
         
}


function setCookie(name_of_cookie,value,days){
    var exDate = new Date();
    exDate.setDate(exDate+days);
    var c_value = escape(value) + ((days==null) ? "" : ";expires="+exDate.toUTCString());
    document.cookie=name_of_cookie + "=" + c_value;
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
