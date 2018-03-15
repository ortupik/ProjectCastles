
$(document).ready(function(){

            /* var bar = new ProgressBar.Circle("#generateProgress", {
               strokeWidth: 6,
               easing: 'easeInOut',
               duration: 10000,
               color: '#FFEA82',
               trailColor: '#eee',
               trailWidth: 1,
               svgStyle: null
             });*/

     $("#id_lecturer").select2();

     $('#smartwizard').smartWizard({
             selected: 0,
             theme: 'default',
             transitionEffect:'fade',
             showStepURLhash: true,
        });

         // Initialize the leaveStep event
         $("#smartwizard").on("leaveStep", function(e, anchorObject, stepNumber, stepDirection) {
             switch(stepNumber){
                case 0:

                  if(!executeStep1()) {
                     return false;
                  }
                    executeStep2();
                  break;
                  case 1:
                      executeStep3();
                  break;
                  case 2:
                   executeStep3();
                  break;
                   case 3:
                      executeStep4();
                    break;

             }
         });

         // Initialize the showStep event
         $("#smartwizard").on("showStep", function(e, anchorObject, stepNumber, stepDirection) {
           // alert("You are on step "+stepNumber+" now");
         });

         $("#inputStart").timepicker({
               showInputs: true
         });

       $("#generateTimetableBtn").on("click",function(){

            $("#generate_status").text("Sending Notifications...");
            $("#generate_action").text("Sending notifications to all lecturers for these groups !");
            $("#postBody").append('<div class="overlay"> <i class="fa fa-refresh fa-spin"></i></div>');

            var form_data = {
               "dept":"CS", "class_groups":"Y2S2-Y3S2-Y1S1"
            }

             $.ajax({
               type: "POST",
               url: "/timetable/sendNotification",
               contentType: "application/json",
               dataType: "json",
               data: JSON.stringify(form_data)
           }).done(function(res) {
                   if(res["status"] == 1){
                      finishedTimetableGeneration();
                   }else{
                     alert(res['message']);
                   }
           });

       });

           setCookie("dept",1,1);

           var academicYear = $("#inputAcad option:selected" ).text();
           var intake = $("#inputIntake option:selected" ).text();
           var inputStart = $("#inputStart").val();
           var inputEnd = $("#inputEnd").val();
           var inputEnd = $("#inputEnd").val();
           var dept_selected,start_time,finish_time,n1,n2;

 function executeStep1(){

       if(inputEnd && inputEnd){
               n1 = inputStart.indexOf(" ");
               n2 = inputEnd.indexOf(" ");
               start_time = inputStart.substr(0,n1);
               finish_time =  inputEnd.substr(0,n2);
              $(".time-warning").remove();
              return true;
       }else{
          $(".time-warning").remove();
          $("#form-1").prepend( '<div class = "callout callout-warning time-warning">Please Fill all the time fields !!</div>' );
          return false;
       }

  }
    function executeStep2(){

           var form_data = {
             "academic_year":academicYear,
             "intake":intake,
             "start_time":start_time,
             "finish_time":finish_time,
             "dept":"CS"
           }


           $.ajax({
               type: "POST",
               url: "/timetable/insertConfiguration",
               contentType: "application/json",
               dataType: "json",
               data: JSON.stringify(form_data)
           }).done(function(res) {
                   if(res["status"] == 1){
                      setDepts(res)
                   }else{
                     alert(res['message']);
                   }
           });
    }


     function setDepts(resData){

            data  = resData["data"];
            configuration_id = resData["configuration_id"];

            var colors = ["btn-warning","btn-success","btn-danger","btn-info"];
            var color_index;

         $("#dept_group_holder").empty();

           for(var i = 0; i < data.length; i++){

            var no = i +1;
            var mapData = data[i];
            var initial = mapData["initial"];
            var dept = mapData["dept"];
            var dept_id = mapData["dept_id"]
            var configuration_id = mapData["configuration_id"]
            var select_id = dept_id;
            var addGroupsBtn_id = initial+"btn";

            color_index = i;
            if(color_index >= data.length){
               color_index = i%4;
            }

            $("#dept_title").val("Add Courses for "+dept);

              $("#dept_group_holder").append(
                       '<div class="col col-lg-12">'+
                         '<div class="input-group input-group-md " >'+
                           '<select class=" group_select js-states form-control " id='+select_id+' multiple="multiple" style="width:550px;">'+
                               '<option value="'+initial+',Y1S1">'+initial+',Y1S1.</option>'+
                               '<option value="'+initial+',Y1S2">'+initial+',Y1S2.</option>'+
                               '<option value="'+initial+',Y2S1">'+initial+',Y2S1.</option>'+
                               '<option value="'+initial+',Y2S2">'+initial+',Y2S2.</option>'+
                               '<option value="'+initial+',Y1S1">'+initial+',Y3S1.</option>'+
                               '<option value="'+initial+',Y1S2">'+initial+',Y3S2.</option>'+
                               '<option value="'+initial+',Y2S1">'+initial+',Y4S1.</option>'+
                               'option value="'+initial+',Y2S2">'+initial+',Y4S2.</option>'+
                               '<option value="'+initial+',Y2S1">'+initial+',Y5S1.</option>'+
                               '<option value="'+initial+',Y2S2">'+initial+',Y5S2.</option>'+
                           '</select>'+
                       '</div>'+
                   '</div>'+
                       '<br><br><br>'+
                       '<div class="col col-lg-2" style="margin-right:10px;">'+
                           '<button type="button" id="'+addGroupsBtn_id+'" class="btn '+colors[i]+' ">Add Group</button>'+
                       '</div>');


                 $("#"+select_id).select2();

                $("#"+addGroupsBtn_id).on("click", function () {
                    var id = $(this).prop("id");
                    var n = id.length;
                    var s_id = id.substr(0,n-3);
                   $("#"+select_id).select2("open");
               });

             }
     }

     $(".class_group_top").on("click",function () {
         $(".class_group_top").removeClass("event");
         $(".class_group_top").removeClass("done");
         $(this).addClass("event");
         var class_group = $(this).text();
         loadUnits(class_group)
     });
     function loadUnits(class_group){
         var dept = "CS";
         setCookie("class_group",class_group,1);

         var form_data = {
             "dept":dept,
             "class_group":class_group
         }
         $.ajax({
             type: "POST",
             url: "/timetable/getTTUnits",
             contentType: "application/json",
             dataType: "json",
             data: JSON.stringify(form_data)
         }).done(function(res) {
             if(res["status"] == 1){
                 displayUnitsTable(res["data"]);
             }else{
                 alert(res['message']);
             }
         });

     }
     function executeStep3(){

       var data = $(".group_select").val();
       var deptGroupList = [];
        var r_data = $(".group_select option:selected").text();
          var data = r_data.split(".");

        for( var i = 0; i < data.length; i++){
           var data_item = data[i];
           var n = data_item.indexOf(",");
           var initialDept = data_item.substr(0,n);
           var group = data_item.substr(n+1,data_item.length);

           var mapData = {
             "initial":initialDept,
             "class_group":group,
           }

           deptGroupList.push(mapData);

        }
           var form_data = {
               "courseGroups":deptGroupList
           }
         $.ajax({
           type: "POST",
           url: "/timetable/insertCourseGroups",
           contentType: "application/json",
           dataType: "json",
           data: JSON.stringify(form_data)
       }).done(function(res) {
               if(res["status"] == 1){

               }else{
                 alert(res['message']);
               }
       });

       var dept = deptGroupList[0]["initial"];
       var class_group =  deptGroupList[0]["class_group"];

       var form_data = {
         "dept":dept,
         "class_group":"Y3S2"
       }
        $.ajax({
          type: "POST",
          url: "/timetable/getTTUnits",
          contentType: "application/json",
          dataType: "json",
          data: JSON.stringify(form_data)
      }).done(function(res) {
              if(res["status"] == 1){
                displayUnitsTable(res["data"]);
              }else{
                alert(res['message']);
              }
      });


     }
     function displayUnitsTable(unitsobj){


         var tableBody = $("#units_body");
         $(tableBody).empty();

           var size = unitsobj.length;
           console.log(unitsobj)

           var unitList = []
         //  size = 1;

             for(var j = 0; j < size; j++){

                  i = j+1;

                  unitCode = unitsobj[j].unit_code
                  param = unitsobj[j].param;

                       //for units
                       var tableRow = document.createElement("tr");
                       var trimmedUnit = (unitsobj[j].unit_code).replace(" ", "");
                       $(tableRow).attr("id",trimmedUnit);

                       $(tableRow).addClass("unit_row");
                       //$(tableRow).append('<td class="mdl-badge " data-badge="3/5">'+i+'</td>');
                        $(tableRow).append('<td >'+i+'</td>');
                      $(tableRow).append('<td class="">'+unitCode+'</td>');

                       var tableDef = document.createElement("td");
                       var params_list = param.split(",");

                         $(tableDef).append('<span class="label label-info">lesson</span>');

                       for(var k = 0; k < params_list.length; k++){
                            var extra_param = params_list[k];
                            if(extra_param != "lesson"){
                                 if(extra_param == "lab"){
                                   $(tableDef).append('&nbsp;&nbsp;<span class="label label-warning">'+extra_param+'</span>');
                                 }else{
                                   $(tableDef).append('&nbsp;&nbsp;<span class="label label-success">'+extra_param+'</span>');
                                 }
                            }

                       }
                       $(tableRow).append($(tableDef));

                      // $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].fname+'</td>');
                      // $(tableRow).append('<td class="mdl-data-table__cell--non-numeric">'+unitsobj[j].lname+'</td>');

                      $(tableBody).append(tableRow);

                     $("#"+unitCode).append('<span class="label label-info">'+param+'</span>')


             }

     }
     function addUnit(unitsobj){

       console.log("inserting unit");
       console.log(unitsobj);
       var tableBody = $(unitsobj);

        var unitCode = unitsobj.unit_code
        var extra_param = unitsobj.param;
        var len = 9;
            //for units
        var tableRow = document.createElement("tr");
        var trimmedUnit = (unitsobj.unit_code).replace(" ", "");
        $(tableRow).attr("id",trimmedUnit);
        $(tableRow).addClass("unit_row");
        $(tableRow).append('<td >'+len+'</td>');
        $(tableRow).append('<td class="">'+unitCode+'</td>');

            var tableDef = document.createElement("td");

              $(tableDef).append('<span class="label label-info">lesson</span>');

             if(extra_param != "lesson"){
                  if(extra_param == "lab"){
                    $(tableDef).append('&nbsp;&nbsp;<span class="label label-warning">'+extra_param+'</span>');
                  }else{
                    $(tableDef).append('&nbsp;&nbsp;<span class="label label-success">'+extra_param+'</span>');
                  }
             }

            $(tableRow).append($(tableDef));
            $(tableBody).append(tableRow);

          $("#"+unitCode).append('<span class="label label-info">'+param+'</span>')


     }

     function executeStep4(){

     }
  $("#add_unit_btn").on("click",function(){

            var lecturer_name = $("#id_lecturer option:selected" ).text();
            var type_of_lesson = $("#type_of_lesson option:selected" ).text();
  		    var class_group = getCookie("class_group");
  		    var unit_code = $("#inputUnitCode").val();
  		    var name = $("#inputName").val();
  		    var hours = $("#inputHours").val();
  		    var dept = "CS";

              var form_data = {
                  "lecturer_name":lecturer_name,
                  "param":type_of_lesson,
                  "class_group":class_group,
                  "name":name,
                  "unit_code":unit_code,
                  "hours":hours,
                  "dept":dept
              }

            $.ajax({
                 type: "POST",
                 url: "/timetable/insertCourseUnits",
                 contentType: "application/json",
                 dataType: "json",
                 data: JSON.stringify(form_data)
             }).done(function(res) {
                     if(res["status"] == 1){

                         var unitsObj = {
                            "unit_code":unit_code,
                            "param":type_of_lesson
                          }
                          addUnit(unitsObj);

                          alert(res["message"]);
                     }else{
                       alert(res['message']);
                     }
             });

  });
     function finishedTimetableGeneration(){
        $("#generate_box").removeClass("box-info");
         $("#generate_box").addClass("box-success");
        $("#generate_status").text("Sucesss !!");
        $("#generate_action").text("Sent Notifications Successfully !!");

        $("#generateTimetableBtn").addClass("hidden");
        $("#viewTimetableBtn").removeClass("hidden");

        $(".overlay").remove();
     }


  });


