$(function(){
    
     var socket = io.connect();
     socket.emit("getCurrentGroups");
     socket.on("getCurrentGroups", function (data) {
        displayGroups(data);
     });
     
     function displayGroups(data){
         
          $("#g_l_updated").text("L.Updated "+data.updated_time);
          setCookie("cod_time_update",data.updated_time,1);
          
         var holder = $("#group_holder");
         var groups = data.groups;
         var groupArr = groups.split(",");
         for(var i = 0; i < groupArr.length; i++){
              $(holder).append('<button class="btn btn-default btn-lg group_item" style="margin-left:10px;"> <h1 class="">'+groupArr[i]+'</h1></button>');
         }
          $(".group_item").on("click",function(){
            var group =  $(this).text();
            setCookie("class_group",group);
             window.location = '/scheduler_units'; 
         });
        
     }
});