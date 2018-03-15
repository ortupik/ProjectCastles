/***
 * Project Castle
 * Created by Chris Kipruto For Castle co. and Techbay company.
 * Code that dynamically generates views for timetable.
 */
$(function () {

    var socket = io.connect();
    /***
     * Suppposed to emit user info
     */
    var details = {
        user_id: getCookie("user_id"),
        week:2
    };
    socket.emit("viewExamTimetable", details);

    socket.on("viewExamTimetable", function (data) {
        console.log("retriving timetable");
        console.log(data);
        displayTable(data);

    });
    socket.on("insertExamTimetable", function (data) {
        alert(data.message);
        location.reload();
    });
   
    function displayTable(viewObj) {
        var tableBody = $("#view_body");

        createTables('MON', tableBody, viewObj[0]);
        createTables('TUE', tableBody, viewObj[1]);
        createTables('WED', tableBody, viewObj[2]);
        createTables('THUR', tableBody, viewObj[3]);
        createTables('FRI', tableBody, viewObj[4]);
        
          $(".set_btn").on('click', function () {
            var rawId = $(this).prop('id');
            var time = rawId.slice(0, 2);
            var firstDigit = rawId.slice(0,1);
            if(firstDigit == '0' || firstDigit == 0 ){
                time = time.slice(1);
            }
            var day = rawId.slice(3, rawId.length);
          
            var intake = "sep-dec";
              var week =$.trim($("#select_week option:selected").index());

            $('#setModal').on('shown.bs.modal', function () {

                $('#intake').text(intake);
                $('#day').text(day);
                $('#time').text(time);
                $('#classGroup').text(classGroup);
                $('#dept').text(dept);

                $('#btn_insert').on('click', function () {
                    var date = $("#input_date").val();
                    var room = $("#input_room").val();
                    var unit_code = $("#input_unit_code").val();
                    var s_time = Math.round(time) - 2;
                    
                    var dept = 'BIT';
                    var classGroup = 'Y2S2';
                    var insertionDetails = {
                        day: day,
                        week:week,
                        s_time: s_time,
                        f_time: time,
                        unit_code: unit_code,
                        room: room,
                        class_group: classGroup,
                        dept: dept,
                        date: date
                    }
                    socket.emit("insertExamTimetable", insertionDetails);
                });
            });


        });

    }

    function createTables(day, tableBody, dayArray) {
        var dayRow = document.createElement("tr");
        $(tableBody).append($(dayRow));

        var tableHeader = document.createElement("th");
        $(tableHeader).append("<br>" + day);


        $(dayRow).append($(tableHeader));

        var initTime = 10;
        var size = dayArray.length;
          
        for (var i = 0; i < 3; i++) {
              var tempTime = initTime;
                if (tempTime < 10) {
                    tempTime = '0' + tempTime;
                }
            var tableDef = document.createElement("td");
            $(tableDef).append("<br>");
            $(tableDef).append('<button  class="btn btn-default set_btn" id = "' + tempTime + '_' + day + '" data-toggle="modal" data-target="#setModal" >SET</button>');


            for (var j = 0; j < size; j++) {
               
                if (dayArray[j]['f_time'] == initTime) {

                   // $(tableDef).attr("colspan", dayArray[j]['hours']);
                    $(tableDef).addClass("event");
                   

                   // i += dayArray[j]['hours'] - 1;
                    //initTime += dayArray[j]['hours'] - 1;

                    /* * Note to Self
                     * Guess what - you can use [] or . to access json atttribute !
                     */
                    $(tableDef).append(dayArray[j]['unit_code']);
                    $(tableDef).append("<br>");
                    $(tableDef).append(dayArray[j]['username']);
                    $(tableDef).append("<br>");
                    $(tableDef).append(dayArray[j]['room']);
                    $(tableDef).append("<br>");
                    $(tableDef).append('  <button  class="btn btn-danger edit_btn">EDIT</button>');


                    if (j == 0) {
                        $("#li_class_group").text(dayArray[j].class_group);
                        $("#li_dept").text(dayArray[j].dept);
                    }

                } else {
 
                }

            }


            initTime+=3;

            $(dayRow).append($(tableDef));

        }
   
    }
    function getCookie(c_name) {
        var i, x, y, ARRcookies = document.cookie.split(";");
        for (i = 0; i < ARRcookies.length; i++) {
            x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
            y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
            x = x.replace(/^\s+|\s+$/g, "");
            if (x == c_name) {
                return unescape(y);
            }
        }
    }
});