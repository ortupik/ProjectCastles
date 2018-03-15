$(window).load(function() {

    $.ajax({
       type: "POST",
       url: "/timetable/generate",
       contentType: "application/json",
       dataType: "json",
       data: ''
   }).done(function(res) {
           if(res["status"] == 1){
              alert(res['message'])
           }else{
             alert(res['message']);
           }
   });


});
