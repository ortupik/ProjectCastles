/***
 * Project Castle
 * Created by Chris Kipruto For Castle co. and Techbay company.
 * Code that dynamically generates views for units.
 */
$(function(){
    
     var socket = io.connect();
         $("#id_lecturer").select2();

    var unitsobj =  null;
     var user_id = {
        user_id: getCookie("user_id")
    };
    
    var group = $.trim(getCookie("class_group"));
    var data = {
        class_group:group,
        dept:"CS"
    }
    
    $("#groupsSelect").on("change",function(){
         var group = $("#groupsSelect option:selected" ).text();
         setCookie("class_group",group,1);
          var data = {
            class_group:group,
            dept:"CS"
          }
           socket.emit("getGroupUnits",data);
    });
    
    socket.emit("getGroupUnits",data);
    
    socket.on("getGroupUnits", function (data) {
        console.log("retriving units");
        console.log(data);
        unitsobj = data;
        displayUnitsTable();
        
     });

     

function displayUnitsTable(){
    
    var tableBody = $("#unitsBody");
    $(tableBody).empty();


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
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].room+'</td>');
          $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].hours+'</td>');
             $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><button   class="mdl-button mdl-button--icon mdl-button--primary mdl-button--raised edit_btn" unitData="'+j+'" unit="'+unitsobj[j].unit_code+'" data-toggle="modal" data-target="#updateUnitsModal"> <i class="material-icons">edit</i></button></td>');
           $(tableRow).append('<td class="mdl-data-table__cell--non-numeric"><button  class="mdl-button mdl-button--icon mdl-button--accent mdl-button--raised delete_btn" unit="'+unitsobj[j].unit_code+'"> <i class="material-icons">delete</i></button></td>');

           $(tableBody).append(tableRow);
            
            if(j == 0){
                $("#li_u_class_group").text(unitsobj[i].class_group);
                $("#li_u_dept").text(unitsobj[i].dept);
            }
            
            
        }
        $(".delete_btn").on("click",function(){
                var unit_code  = $(this).attr("unit");
                var data = {
                    unit_code:unit_code
                }
                socket.emit("deleteUnit",data);
         });
         
     socket.on("deleteUnit", function (data) {
        if(data.success == 1){
           alert("Successfully Deleted Unit !");
           location.href="/cod_manage_units";
        }
      });
       $(".edit_btn").on("click",function(){
            var new_unit_code  = $(this).attr("unit");
            var unitData = $(this).attr("unitData");
            setCookie("new_unit_code",new_unit_code,1);
             setCookie("unitData",unitData,1);
        });
         $('#updateUnitsModal').on('shown.bs.modal', function () {
              var unitPos = getCookie("unitData");
              var unitData = unitsobj[unitPos];
             
             $("#id_lecturer_u" ).val(unitData.fname+" "+unitData.lname);
             $("#room_select option:selected" ).val(unitData.room);
            $("#inputUnitCode").val(unitData.unit_code);
             $("#inputName").val(unitData.name);
             $("#inputHours").val(unitData.hours);
          
            
         });
        
        $("#update_unit_btn").on("click",function(){
            
              var new_unit_code  = getCookie("new_unit_code");
            
            var lecturer_name = $("#id_lecturer_u option:selected" ).text();
            var room = $("#room_select option:selected" ).text();
            var class_group = getCookie("class_group");
            var unit_code = $("#inputUnitCode").val();
            var name = $("#inputName").val();
            var hours = $("#inputHours").val();
            
            var new_data = {
              "lecturer_id":1,
              "extra_lessons_param":'N',
              "class_group":class_group,
              "name":name,
              "unit_code":unit_code,
              "hours":hours,
              "dept":"CS",
              "room":room
            }
            
            var old_data = {
                unit_code:new_unit_code
            }
            var data = {
                old_data:old_data,
                new_data:new_data
            }          
            socket.emit("updateUnits",data);
        });
         
     socket.on("updateUnits", function (data) {
        if(data.success == 1){
           alert("Successfully Updated Unit !");
           location.href="/cod_manage_units";
        }
      });
             
     
}

    $("#add_lec_btn").on("click",function(){
        var fname  = $("#inputFname").val();
        var lname  = $("#inputLname").val();
        
        var data = {
            fname:fname,
            lname:lname
        }
   
       socket.emit("insertLec",data);
  });
  
   socket.on("insertLec", function (data) {
        if(data.success == 1){
           alert("Successfully Lecturer !");
          location.href="/cod_manage_units";
        }
  });

  $("#add_unit_btn").on("click",function(){

        var lecturer_name = $("#id_lecturer_a option:selected" ).text();
        var room = $("#room_select_a option:selected" ).text();
        var class_group = getCookie("class_group");
        var unit_code = $("#inputUnitCode_a").val();
        var name = $("#inputName_a").val();
        var hours = $("#inputHours_a").val();
        var dept = "CS";

          var form_data = {
              "lecturer_id":1,
              "extra_lessons_param":'N',
              "class_group":class_group,
              "name":name,
              "unit_code":unit_code,
              "hours":hours,
              "dept":dept,
              "room":room
          }

       socket.emit("insertUnits",form_data);
  });
  
   socket.on("insertUnits", function (data) {
        if(data.success == 1){
           alert("Successfully Added Unit !");
          location.href="/cod_manage_units";
        }
  });

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
