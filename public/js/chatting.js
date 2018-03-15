$(window).load(function () {
  
    // progressJs().start();
    $(".messages-content").hide();
     $("#contacts-list").mCustomScrollbar();
     $("#chats-list").mCustomScrollbar();
 
      //download("http://localhost:8080/profile_images/IMG-20161014-WA0002.jpg");
    initSocket();  
});
var player = null;
$unreadMessages = 0;
  
var typeOfRoom = getCookie("typeOfRoom");
var opponent_photo = getCookie("opponent_photo");//holds even for group

var username = getCookie("username");
var phoneNo = getCookie("phoneNo");
var profile_photo = getCookie("profile_photo");    
var opponent_id = getCookie("opponent_id");
var status = getCookie("status");



$(".profileLink").click(function(){
    $("#username_profile").val(username);
    $("#status_profile").val(status);
    $("#profile_img_photo").attr("src","profile_images/"+profile_photo);
});
$("#logoutLink").click(function(){
   setCookie("user_id", null, 1);
   setCookie("username", null, 1);
   setCookie("phoneNo", null, 1); 
    window.location = '/login'; 
});



var chatList = [];
var groupChatList = [];

   var socket = io.connect();
   

    socket.on("editProfile", function (data) {
        
        //$('#profileModal').modal('hide');
         window.location = '/index'; 
    });
    socket.on("sendProfile", function (data) {
        
        $('#createProfileModal').modal('hide');
         window.location = '/index'; 
    });
    socket.on("qrLogin", function (data) {
         var loginData = {
                user_id: data.user_id,
                phoneNo: data.phoneNo
          };
        
          setCookie("user_id", data.user_id, 1);
          setCookie("username",data.username, 1);
          setCookie("phoneNo", data.phoneNo, 1); 
          setCookie("regNo", data.regNo, 1);
          setCookie("dept", data.dept, 1);
          setCookie("class_group", data.class_group, 1);
          
           // socket.emit("login", loginData);
           
        window.location = '/timetable';
    });
    
      socket.on("autoLogin", function (data) {
         $('#internet-connection').remove();
         
          if(document.title == 'login'){
                $("#qrcode").empty();
                var qrcode = new QRCode(document.getElementById("qrcode"), {
                   width: 200,
                   height: 200,
                   colorDark : "black",
                   colorLight : "#ffffff",
                   correctLevel : QRCode.CorrectLevel.H
                  });
                 qrcode.clear(); 
                 qrcode.makeCode(data.socket_id);
        }
         if(document.title == 'index' && checkCookie()){
            socket.emit("login", loginData);
         }
          
    });
  if(checkCookie()){
     

    var user_id = {
        user_id: getCookie("user_id")
    };
    var loginData = {
        user_id: getCookie("user_id"),
        phoneNo: getCookie("phoneNo")
    };
    
     if(username.length > 10){
      $('#username').addClass("small_font");  
    }
    $('#username').text(username);
    $('#phoneNo').text(phoneNo);
    $('#profile_photo').attr('src',"profile_images/"+profile_photo);
        
        socket.emit("login", loginData);

 }

$("#loginForm").submit(function (e) {
    e.preventDefault();
    var password_r = $("#password").val();
    var phoneNo_r = $("#phoneNo").val();

    var signInData = {
        password: password_r,
        phoneNo: phoneNo_r
    };

    socket.emit("signInUser", signInData);
     socket.on("signInUser", function (data) {
         switch (data.result) {
            case 0:
                alert("Could not register you at this time !");
                break;
            case 1:
                setCookie("user_id", data.user_id, 1);
                setCookie("username", data.username, 1);
                
                if(data.user_role  == "Student"){
                    socket.emit("checkStudentRegistration",{user_id:data.user_id});
                }else if(data.user_role == "Scheduler"){
                     window.location = '/scheduler_select';
                }else if(data.user_role == "COD"){
                     window.location = '/cod_timetables';
                }
                break;
        }
     });
     
      socket.on("checkStudentRegistration", function (data) {
         switch (data.success) {
            case 0:
                alert("Register as a student first in Student Life android app !");
                break;
            case 1:
                
                setCookie("phoneNo", phoneNo_r, 1); 
                setCookie("regNo", data.regNo, 1);
                setCookie("dept", data.dept, 1);
                setCookie("class_group", data.class_group, 1);
               // alert("Welcome back!");
                window.location = '/student_timetable';
                break;
        }
      });
    /* socket.emit("registerUser", registerData);
     * socket.on("registerUser", function (data) {
        switch (data.result) {
            case 0:
                alert("Could not register you at this time !");
                break;
            case 1:
                alert("Registration Successfull !");
               
                setCookie("user_id", data.user_id, 1);
                setCookie("phoneNo", phoneNo_r, 1);
                
                window.location = '/registration';
                
                break;
            case 2:
          //    alert("welcome back !");
               
                username_r = data.username
                
                setCookie("user_id", data.user_id, 1);
                 setCookie("username", username_r, 1);
                 setCookie("phoneNo", phoneNo_r, 1); 
             
                window.location = '/index';
                
                break;

        }
    });*/

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

$('.edit-profile-button').click(function(){
    var username = $("#username_profile").val();
    var status = $("#status_profile").val();
    var files = $('#upload-profile-image').get(0).files;
    
     if(!username ){
        alert("input username !");
        $('.username-div').addClass('has-error');
    }else  if(!status ){
        alert("input status !");
    }else if(files.length < 1 && username && status){
          sendProfileInfo(username,status,profile_photo,"edit");
    }else{
      uploadProfile(files,username,status,"edit");
    }
});
$('#regButton').click(function(){
    var username = $('#username').val();
    var status = $('#status').val();
    var files = $('#profile-image').get(0).files;
     if(!username ){
        alert("input username !");
        $('.username-div').addClass('has-error');
    }else  if(!status ){
        alert("input status !");
    }else if(files.length < 1 && username && status){
           alert("select image !");
    }else{
       // alert(status)
       
       var files = $('#profile-image').get(0).files;
       var username = $('#username').val();
       var status = $('#status').val();
       
         uploadProfile(files,username,status,"registration");
    }
   
   
});
function initSocket() {

    var user_id = {
        user_id: getCookie("user_id")
    };

	var details = {
	    user_id: getCookie("user_id"),
		dept:getCookie("dept"),
		class_group:getCookie("class_group")
	}
    socket.emit("getChats", user_id);
    socket.emit("getContacts", details);


    socket.on("getChats", function (data) {
        getChats(data,"individual");
    });
    socket.on("getGroupChats", function (data) {
        getChats(data,"group");
    });
    

    socket.on("getContacts", function (data) {
        console.log(data)
       getContacts(data);
    });
     socket.on("userOnline", function (data) {
        updateUserOnlineStatus(data.user_id,1);
    });
    socket.on("userOffline", function (data) {
        updateUserOnlineStatus(data.user_id,0);
    });
    socket.on("onlineUsersList", function (data) {
       var onlineUsers = data.onlineUsers;
        for(var j = 0;j < onlineUsers.length; j++){
            updateUserOnlineStatus(onlineUsers[j],1);
        }
    });
    socket.on("insertChatMessage", function (data) {
        onInsertedMessage(data);
    });
    socket.on('acknowledgeReceive', function (data) {
            onReceivedMessage(data);
    });
   
  if(typeOfRoom == "individual"){
      var opponent_id = getCookie("opponent_id");
      var opponent_username = getCookie("opponent_username");
      
    if(opponent_id != null || opponent_id != "" || opponent_username != null || opponent_username != ""  ){
         $(".opponent_username").text(opponent_username);
         
         if(opponent_photo != null || opponent_photo != "" ){
             $(".opponent_photo").attr('src',opponent_photo);
         }
         
          //requestMessages("individual",opponent_id);
    }
  }else if(typeOfRoom == "group"){
      var group_id = getCookie("group_id");
      var group_name = getCookie("group_name");
      var groupUsers = getCookie("groupUsers");
      
      
       if(group_id != null || group_id != "" || group_name != null || group_name != "" ){
           $(".opponent_username").text(group_name);

            if(opponent_photo != null || opponent_photo != "" ){
             $(".opponent_photo").attr('src',opponent_photo);
         }
         requestMessages("groups",group_id);
       }
  }
    
     socket.on("getChatMessages", function (chatMessages) {
        displayMessages(chatMessages);  
     });


    socket.on('msg', function (chatMessage) {
        console.log("Receiving message");
        receiveMessage(chatMessage);

    });
    socket.on('typing', function (data) {
        userTyping();
    });
    socket.on('stop typing', function (data) {
        stopTyping();
    });
    socket.on('login', function (data) {
         setUserInfo(data);
    });
    socket.on('disconnect', function () {
        handleDisconnect();
    });
    
}
function setUserInfo(data){
    
    var username = data.username;
    var status = data.status;
    var phoneNo = data.phoneNo;
    var profile_photo = data.profile_photo;
    
    if(username.length > 10){
      $('#username').addClass("small_font");  
    }
    
    $('#username').text(username);
    $('#phoneNo').text(phoneNo);
    $('#profile_photo').attr('src',"profile_images/"+profile_photo);
    
    setCookie("username", username, 1);
    setCookie("status", status, 1);
    setCookie("phoneNo", phoneNo, 1);
    setCookie("profile_photo", profile_photo, 1);
}
function requestMessages(type,id){
     
    
    
    if(type == "individual"){
        var chatDet = {
            user_id:getCookie("user_id"),
            opponent:id,
            type:type
        }
        socket.emit("getChatMessages", chatDet);
        
     var res = alasql("SELECT * FROM `chats` where chat_id = 3 ");
     console.log(res)
     
     $messagesLength = res.length;
     
      for (var i = 0; i < $messagesLength; i++) {
            var messageObj = res[i];
            displayIndividualMessages(messageObj);
          }
          
   }else if(type == "groups"){
        var chatDet = {
            user_id:getCookie("user_id"),
            group_id:id,
            type:type
        }  
        socket.emit("getChatMessages", chatDet);
   }
}


$('.message-input').change(function (){
    
    var details = {
        user_id:user_id ,
        opponentId: opponent_id
    };
    socket.emit("typing",details);
});
$('.message-submit').click(function () {
     var msg = $('.message-input').val();
     var random = Math.round(Math.random()*10000000000);
    insertMessage("text",msg,"",random); 
    $('.message-input').val(null);
});
$('#audio_img').on('click', function (){
  alert("play");
});


function displayMessages(chatMessages){
    
    //offline mode and speed enhancement
    alasql.tables.chats.data = chatMessages.messageArray;

    var messageContainer = $(".messages-content");     
    var date = new Date().toDateString();

     if (Array.isArray(chatMessages.messageArray)) {
          messageContainer.empty();

          $messagesLength = chatMessages.messageArray.length;
          
          $("#chats_anchor").attr("data-badge",$messagesLength);
          $(' <p class="datestamp"><u>'+date+'</u></p>').appendTo($('.messages-content'));
        
        for (var i = 0; i < $messagesLength; i++) {
            var messageObj = chatMessages.messageArray[i];
                if(chatMessages.type == 'individual'){
                    displayIndividualMessages(messageObj);
                }else if(chatMessages.type == 'groups'){
                    displayGroupMessages(messageObj);
                }
          }
            $(".messagesLoader").hide();
            $(".messages").addClass("mCustomScrollbar");
             $('.messages').mCustomScrollbar();
            $(messageContainer).show();
            
    }
   
    
    updateScrollbar();
}

    function  displayGroupMessages(messageObj){
        
            

            var message = messageObj.message;
             var message_id = messageObj.message_id;        
            var whoSent = messageObj.sender;
            var created_at = messageObj.created_at;
            var type = messageObj.type;
            var filepath = messageObj.filepath;            
            var size = created_at.length;
            var time = formatTime(created_at);
            
            
            
            var userPhoto = getCookie('profile_photo');
            var opponentPhoto = "profile_images/"+messageObj.profile_photo;
           
          var  color = 'green';
          
          if(whoSent != 'Me'){
            var fLetter = whoSent.substring(0,1).toUpperCase();
            if(fLetter <= "D" ){
                color = 'green';
            }else if(fLetter > "D" && fLetter <= "I"){
                 color = 'brown';
            }else if(fLetter > "I" && fLetter <= "P"){
                 color = '#009999';
            }else if(fLetter > "P" && fLetter <= "U"){
                 color = 'purple';
            }else if(fLetter > "U" && fLetter <= "Z"){
                color = '#ff6600';
            }
        }

            if(type == 'text'){
                if(whoSent == 'Me'){
                     $('<div class="message message-text message-personal-groups z-depth-1"><figure class="avatar right-avatar circle"> <img src="profile_images/'+userPhoto+'"/></figure>' + message + '<br><div id="text'+message_id+'"><img class="tick-personal delivered tick_spacing" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('');
                }else {
                   $('<div class="message message-text other-groups z-depth-1"><figure class="avatar left-avatar circle"><img src="'+opponentPhoto+'"/></figure>' + message + '<br><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span><span class="time-group-other right">'+time+'</span></div>').appendTo($('.messages-content')).addClass('');
                }
                
             // $('<div class="message message-personal-groups z-depth-1" style="max-width:170px;"><figure class="avatar right-avatar circle"><img src="profile_images/'+userPhoto+'"/></figure><img src="'+filepath+'" class=" materialboxed responsive-img personal_img"/>' + message + '<br><div id="img'+random+'"><img class="tick-image delivered" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass(''); 


            }else if(type == 'image'){
                if(whoSent == 'Me'){
                     $(' <div class="demo-gallery message message-image message-personal-groups z-depth-1" style="max-width:170px; max-height:250px;"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="'+whoSent+'">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="white-text">'+message+'</div>\n\
                     <div id="img'+message_id+'"><img class="tick-image delivered" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');                   
            }else {
                $(' <div class="demo-gallery message message-image other-groups z-depth-1" style="max-width:170px; max-height:250px;"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="'+whoSent+'">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="black-text">'+message+'<br><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span></div>\n\
                     <span class="time-group-other right">'+time+'</span></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');  
                }
              initPhotoSwipe();
            }else if (type == 'audio'){
                if(whoSent == 'Me'){
                  $('<div class="message message-audio message-personal-groups " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class=" audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><img class="tick-image not_sent" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
               }else {
                   $('<div class="message message-audio other-groups z-depth-1" style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="teal-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class="audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span><div><span class="time-group-other right">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('');    
               }
            }else if (type == 'video'){
                if(whoSent == 'Me'){
                  $('<div class="message message-video message-personal-groups" style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><span class="video_div"><a class="waves-effect waves-light modal-trigger2" href="#playVideoModal"><img src="images/attach_video.png" class="responsive-img audio_img"/></a><span class="video_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><img class="tick-image not_sent" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
               }else {
                  $('<div class="message message-video other-groups z-depth-1 " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="teal-text audio_text" >Video&nbsp;&nbsp;</span></span><span class="video_div"><a class="waves-effect waves-light modal-trigger2" href="#playVideoModal"><img src="images/attach_video.png" class="responsive-img audio_img"/></a><span class="video_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span><div><span class="time-group-other right">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('');    
               }
            }
            
            var selection = null;
            
            $(".audio_div").click(function(){
                audioTone();
                $selection = $(this);
                $path = $selection.find('.audio_path').text();
                $selection.find(".audio-play").attr("src","images/pause_w2.png");
                 $('#playAudioModal').openModal();
                   
                    var name  = $path.slice(14,$path.length);
                     player = new Player([
                      {
                        title: name,
                        file: $path,
                        howl: null
                      }
                    ]);
                   
            });
            $('.modal-trigger').leanModal({
                dismissible: true, // Modal can be dismissed by clicking outside of the modal
                opacity: .5, // Opacity of modal background
                in_duration: 300, // Transition in duration
                out_duration: 200, // Transition out duration
                ready: function() {
                    player.play();
                }, 
                complete: function() {
                    $selection.find(".audio-play").attr("src","images/play_w2.png");
                    player.pause(); 
                } 
              }
            );  
            $(".video_div").click(function(){
                audioTone();
                $selection = $(this);
                $path = $selection.find('.video_path').text();
               // $selection.find(".audio-play").attr("src","images/pause_w2.png");
                 $('#playVideoModal').openModal();
                   
                    var name  = $path.slice(14,$path.length);
                    
                   
            });
            $('.modal-trigger2').leanModal({
                dismissible: true, // Modal can be dismissed by clicking outside of the modal
                opacity: .5, // Opacity of modal background
                in_duration: 300, // Transition in duration
                out_duration: 200, // Transition out duration
                ready: function() {
                }, 
                complete: function() {
                    //$selection.find(".audio-play").attr("src","images/play_w2.png");
                } 
              }
            );     

             
           
    }
    
     function displayIndividualMessages(messageObj){
         
              //$('.materialboxed').materialbox();

             var message = messageObj.message;
             var message_id = messageObj.message_id;
             var temp_message_id = messageObj.temp_message_id;
             var whoSent = messageObj.sender;
            var created_at = messageObj.created_at;
            var type = messageObj.type;
            var filepath = messageObj.filepath;
            var delivered = messageObj.delivered;
            
            var userPhoto = getCookie('profile_photo');
            var opponentPhoto = getCookie('opponent_photo');
            
            var time = formatTime(created_at);
            
            if(type == 'text'){
                if(whoSent == 'Me'){
                     $('<div class="message message-text message-personal z-depth-1"><figure class="avatar right-avatar circle"> <img src="profile_images/'+userPhoto+'"/></figure>' + message + '<br><div id="text'+message_id+'"><img class="tick-personal delivered" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');
                     if(delivered == 1){
                        $('#text'+message_id).append('<img class="tick-personal received" src="images/msg_check_2.png" />');
                         $('.delivered').addClass('no_tick_spacing');
                     }else{
                         $('.delivered').addClass('tick_spacing');
                     }
                }else if(whoSent == 'opponent'){
                   $('<div class="message new z-depth-1 "><figure class="avatar left-avatar circle"><img src="'+opponentPhoto+'"/></figure>' + message + '<br><span class="time-new">'+time+'</span></div>').appendTo($('.messages-content')).addClass('new');
                }

            }else if(type == 'image'){
                if(whoSent == 'Me'){
                 $(' <div class="demo-gallery message message-image message-personal z-depth-1" style="max-width:170px; max-height:250px;"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="'+whoSent+'">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="white-text">'+message+'<br></div>\n\
                     <div id="img'+message_id+'"><img class="tick-image delivered" src="images/msg_check_1.png" /><span class="time-new right">'+time+'</span></div></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');  
                
               initPhotoSwipe();  
              if(delivered == 1){
                        $('#img'+message_id).append('<img class="tick-image received" src="images/msg_check_2.png" />');
                         $('.delivered').addClass('no_tick_spacing');
                     }else{
                         $('.delivered').addClass('tick_spacing');
                     }
                }else if(whoSent == 'opponent'){
                    $(' <div class="demo-gallery message message-image new z-depth-1" style="max-width:170px; max-height:250px;"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="'+whoSent+'">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="white-text">'+message+'<br></div>\n\
                     <span class="time-new">'+time+'</span></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');  
                
                  initPhotoSwipe();
  
                }
            }else if (type == 'audio'){
                if(whoSent == 'Me'){
                  $('<div class="message message-audio message-personal z-depth-1 " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class="audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
               }else if(whoSent == 'opponent'){
                   $('<div class="message message-audio new  " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class="audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');    
               }
            }else if (type == 'video'){
                if(whoSent == 'Me'){
                  $('<div class="message message-personal message-video z-depth-1 " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
               }else if(whoSent == 'opponent'){
                  $('<div class="message message message-video new z-depth-1 " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');    
               }
            }
             if(delivered == 0  && whoSent == 'opponent'){
                var data = {
                    message_id:message_id,
                    temp_message_id:temp_message_id,
                    time:getCurrentDate(),
                    user_id:getCookie("opponent_id")
                }
                
              socket.emit("acknowledgeReceive",data);
            }
             
            //listeners
            $(".personal_img").click(function(){
               // alert("ddsd")
            });
              
            $(".audio_div").click(function(){
                $path = $(this).find('.audio_path').text()
                 $('#playAudioModal').openModal();
                   
                    var name  = $path.slice(14,$path.length);
                     player = new Player([
                      {
                        title: name,
                        file: $path,
                        howl: null
                      }
                    ]);
                   
            });
            $('.modal-trigger').leanModal({
                dismissible: true, // Modal can be dismissed by clicking outside of the modal
                opacity: .5, // Opacity of modal background
                in_duration: 300, // Transition in duration
                out_duration: 200, // Transition out duration
                ready: function() {
                    player.play();
                }, 
                complete: function() {
                    player.pause(); 
                } 
              }
            );
}
 
 function formatTime(created_at){
     
           var  time = created_at.slice(11,16);
            var hour = created_at.slice(11,13);
            var min = created_at.slice(13,16);

           var ext = null;
            if(hour > 12){
               hour = hour -12;
               ext = "PM";
            }else if(hour == 12 && min == ':00'){
               ext="";
            }else if(hour == 12){
                ext = "PM";
            }else{
                ext = "AM";
            }
            time = hour +min+ " "+ext; 
            return time;
 }
function onInsertedMessage(data){
     $bottomStack =  $('#'+data.temp_message_id);
     
      $bottomStack.find('.not_sent').replaceWith('<img class="tick-personal delivered tick_spacing" src="images/msg_check_1.png" />');
    
     
    updateScrollbar();
     audioTone();
    
}
function onReceivedMessage(data){
    
         $bottomStack =  $('#'+data.temp_message_id);
          $bottomStack.append('<img class="tick-personal received" src="images/msg_check_2.png" />');
          $('.delivered').addClass('no_tick_spacing');    
         
           // Materialize.toast('Received! @'+data.time, 4000); 
            updateScrollbar(); 
           // audioTone();
            
}
function audioTone(){
    var sound = new Howl({
      src: ['../audio/pop.m4A']
    });
    sound.play()
}
function getChats(data ,chatType) {
    
  if(chatType == 'individual'){
      
    if (Array.isArray(data)) {
        
        for (var i = 0; i < data.length; i++) {

            var chatMessage = data[i];
            var username = chatMessage.username;
            var profilePhoto = chatMessage.profile_photo;
            var user_id = chatMessage.user_id;
            var message = chatMessage.message;
            var lastMessage = message.lastMessage;
            var time_r = message.time;
            
            if(lastMessage.length >  36){
              lastMessage = lastMessage.slice(0,35)+" ...";
            }
            var time = time_r.slice(11,16);
           // +" "+time_r.slice(0,10);


            var chatData = '<span class="active_li" ><a class="collection-item avatar chat_item "  id="cha_'+user_id+'">\n\
                              <img src="profile_images/'+profilePhoto+'"  alt="" class="circle profile_photo ">\n\
                             <span  class="username"><b>'+username+'</b></span>\n\
                             <span class="time_chat right ">'+time+'</span>\n\
                             <p  class="status" >'+lastMessage+'</p></a></span> ';
             
              chatList.push(user_id+""); 
              $("#chats_holder").append(chatData);
        }
    }
  }else if(chatType == 'group'){
      
    if (Array.isArray(data)) {
       
        for (var j = 0; j < data.length; j++) {
            var chatMessage = data[j];
            var group_name = chatMessage.group_name;
            var profilePhoto = chatMessage.profile_photo;
            var group_id = chatMessage.group_id;
            var lastMessage = chatMessage.message;
            
            if(lastMessage.length >  36){
              lastMessage = lastMessage.slice(0,35)+" ...";
            }
           
            var time_r= chatMessage.created_at;            
            var time = time_r.slice(11,16);
            //var time = time_r.slice(0,10);

            
            var chatData = '<span><a class="collection-item avatar chat_item "  id="gri_'+group_id+'">\n\
                              <img src="profile_images/'+profilePhoto+'"  alt="" class="circle profile_photo ">\n\
                             <span  class="groupname"><b>'+group_name+'</b></span>\n\
                             <span class="time_chat right">'+time+'</span>\n\
                             <p  class="status" >'+lastMessage+'</p></a> </span>';
                
              groupChatList.push(group_id+"");
              $("#chats_holder").append(chatData);
        }
        
    }
      
     
   }
      $("ul#chats_holder a").click(function () {
             var rawId =$(this).prop('id');
             $(".chat_item").removeClass("active_nav");
             $(".chat_item").removeClass("mdl-navigation__link");
             $(".chat_item").removeClass("mdl-color-text--red-800");
             $(this).addClass("mdl-navigation__link mdl-color-text--red-800 active_nav");
         
        //check type if group or individual
        var type = rawId.slice(0,4);
        var id = rawId.slice(4);
        
        if(type == 'gri_'){
            
            setCookie("group_id", id, 1);
            setCookie("typeOfRoom","group",1);
            var groupname = $(this).find(".groupname").text();
            $(".opponent_username").text(groupname);
            setCookie("group_name", groupname, 1);
            
            var profilePhoto = $(this).find(".profile_photo").attr('src');
            $(".opponent_photo").attr('src',profilePhoto);
            setCookie("opponent_photo", profilePhoto, 1);
            
            var allUsernames = $('#gro_'+id).find(".allUsernames").text();
             $('.onlineStatus').removeClass("orange-text");
             $('.onlineStatus').addClass("green-text");
            $('.onlineStatus').text(allUsernames);
            setCookie("groupUsers", allUsernames, 1);
            
            requestMessages("groups",id);
            
        }else if(type == 'cha_'){
            opponent_id = id;
            setCookie("opponent_id", opponent_id, 1);
            setCookie("typeOfRoom","individual",1);
            
            var username = $(this).find(".username").text();
            $(".opponent_username").text(username);
            setCookie("opponent_username", username, 1);
            
             var profilePhoto = $(this).find(".profile_photo").attr('src');
              $(".opponent_photo").attr('src',profilePhoto);
             setCookie("opponent_photo", profilePhoto, 1);
                
                var lastSeen = $('#con_'+id).find(".last_seen").text();
                var onlineStatus =  $('#con_'+id).find(".a-status").prop('id');
                
                if($("#"+onlineStatus).hasClass('available')){
                    $('.onlineStatus').addClass("green-text");
                    $('.onlineStatus').text("online");
                }else {
                    $('.onlineStatus').addClass("orange-text");
                    $('.onlineStatus').removeClass("green-text");
                    $('.onlineStatus').text("last seen "+lastSeen);
                }
            requestMessages("individual",id);
        }
                
        });
            }
    function createChat(msg,typeOfRoom){
        var time = getCurrentDate();
         var profilePhoto = getCookie('opponent_photo');
         
         if(msg.length >  36){
             msg = msg.slice(0,35)+" ...";
         }

       
        if(typeOfRoom == 'individual'){
            var opponent_username = getCookie('opponent_username');

            var chatData = '<a class="collection-item avatar "  id="cha_'+opponent_id+'">\n\
                            <img src="'+profilePhoto+'" alt="" class="circle ">\n\
                            <span  class="username"><b>'+opponent_username+'</b></span>\n\
                            <span class="time_chat right">'+time+'</span>\n\
                            <p  class="status" >'+msg+'</p></a> ';
                 $("#chats_holder").append(chatData);
                 chatList.push(opponent_id);
                 
         }else if(typeOfRoom == 'group'){
             
             var group_id = getCookie('group_id');
             var group_name = getCookie('group_name');
             
             
             
            var chatData = '<a class="collection-item avatar "  id="gri_'+group_id+'">\n\
                              <img src="'+profilePhoto+'"  alt="" class="circle profile_photo ">\n\
                             <span  class="groupname"><b>'+group_name+'</b></span>\n\
                             <span class="time_chat right">'+time+'</span>\n\
                             <p  class="status" >'+msg+'</p></a> ';
                
              groupChatList.push(group_id+"");
              
              $("#chats_holder").append(chatData);
         }
             
             
    }
    function getContacts(data) {
        if (  Array.isArray(data.groups) ) {

           //for groups
             var size_of_groups = data.groups.length;
             var groups = data.groups[size_of_groups]; 

              for (var j = 0; j < size_of_groups; j++) {

                  var group = data.groups[j];
                  var group_id = group.group_id;
                  var group_name = group.group_name;
                  var created_at = group.created_at;
                  var created_by = group.created_by;
                  var status = group.status;
                  var profilePhoto = group.profile_photo;
                  var contacts_in_group = group.contacts;
                  var allUsernames = '';
                  
                  if(status.length >  36){
                    status = status.slice(0,35)+" ...";
                  }


                   for (var i = 0; i < contacts_in_group.length; i++) {
                        var contact = contacts_in_group[i];
                        var username = contact.username;
                        allUsernames+=username;
                        if( i < contacts_in_group.length-1){
                             allUsernames+=",";
                        }

                   }
                   if(allUsernames.length > 28){
                     allUsernames = allUsernames.slice(0,26)+"..."; 
                   }
                   
                   if(typeOfRoom == "group"){
                        $('.onlineStatus').removeClass("orange-text");
                       $('.onlineStatus').addClass("green-text");
                      $('.onlineStatus').text(allUsernames);
                   }
                  

                   var groupData = '<span><a class="collection-item avatar chat_item" groupname="gro_'+group_name+'" id="gro_'+group_id+'">\n\
                            <img src="profile_images/'+profilePhoto+'" alt="" class="profile_photo circle ">\n\
                            <span  class="groupname"><b>'+group_name+'</b></span><span class="allUsernames visuallyhidden">'+allUsernames+'</span>\n\
                            <span class="right">Group</span>\n\
                            <p class="status" >'+status+'</p></a></span> ';

                  $("#contacts_holder").append(groupData);


              }
              
              }

           if(Array.isArray(data.contacts)){

             var size = data.contacts.length-1;
             var onlineContacts = data.contacts[size]; 
             var opponent_id = getCookie("opponent_id");

            for (var i = 0; i < size; i++) {

                 var contact = data.contacts[i];
                var username = contact.username;
                var status = contact.status;
                var profilePhoto = contact.profile_photo;
                var phoneNo = contact.phoneNo;
                var user_id = contact.user_id;
                var lastSeen = contact.last_seen;
                lastSeen = formatTime(lastSeen);
                var onlineStatus = 'away';
           
                    if(status.length >  36){
                    status = status.slice(0,35)+" ...";
                  }
           if($.inArray(user_id,onlineContacts) != -1){
               onlineStatus = 'available';
           }
           
           if(opponent_id == user_id && onlineStatus == 'available' ){
               $('.onlineStatus').addClass("green-text");
               $('.onlineStatus').text("online");
           }else  if(opponent_id == user_id && onlineStatus != 'available' && typeOfRoom == 'individual'){
               $('.onlineStatus').removeClass("green-text");
               $('.onlineStatus').addClass("orange-text");
               $('.onlineStatus').text("last seen "+lastSeen);
           }
              
           var contactData = '<span><a class="collection-item avatar chat_item" username="con_'+username+'" id="con_'+user_id+'">\n\
                        <img src="profile_images/'+profilePhoto+'" alt="" class="profile_photo circle ">\n\
                        <span  class="username"><b>'+username+'</b></span><p class="invisible right last_seen" >'+lastSeen+'</p>\n\
                        <span class="a-status '+onlineStatus+' right" id="o_'+user_id+'"></span>\n\
                        <p class="status" >'+status+'</p></a></span> ';
             
              $("#contacts_holder").append(contactData);
              
              
        }
       
          
    }
    
    $("ul#contacts_holder a").click(function () {
        
            $(".chat_item").removeClass("active_nav");
             $(".chat_item").removeClass("mdl-navigation__link");
             $(".chat_item").removeClass("mdl-color-text--red-800");
             $(this).addClass("mdl-navigation__link mdl-color-text--red-800 active_nav");
             
         var rawId =$(this).prop('id');
         
        //check type if group or individual
        var type = rawId.slice(0,4);
        var id = rawId.slice(4);

        if(type == 'gro_'){
            
            setCookie("group_id", id, 1);
            setCookie("typeOfRoom","group",1);
            var groupname = $(this).find(".groupname").text();
            $(".opponent_username").text(groupname);
            setCookie("group_name", groupname, 1);
            requestMessages("groups",id);
            var allUsernames = $(this).find(".allUsernames").text();
             $('.onlineStatus').removeClass("orange-text");
             $('.onlineStatus').addClass("green-text");
            $('.onlineStatus').text(allUsernames);
            
            setCookie("groupUsers", allUsernames, 1);
            
        }else if(type == 'con_'){
            opponent_id = id;
            setCookie("opponent_id", opponent_id, 1);
            setCookie("typeOfRoom","individual",1);
            
            var username = $(this).find(".username").text();
            $(".opponent_username").text(username);
            setCookie("opponent_username", username, 1);
            
             var lastSeen = $(this).find(".last_seen").text();

            var onlineStatus =  $(this).find(".a-status").prop('id');

             if($("#"+onlineStatus).hasClass('available')){
                $('.onlineStatus').addClass("green-text");
                $('.onlineStatus').text("online");
            }else{
                $('.onlineStatus').removeClass("green-text");
                 $('.onlineStatus').addClass("orange-text");
                $('.onlineStatus').text("last seen "+lastSeen);
            }
            requestMessages("individual",id);
        }

        var profilePhoto = $(this).find(".profile_photo").attr('src');
        $(".opponent_photo").attr('src',profilePhoto);
         setCookie("opponent_photo", profilePhoto, 1);

    });  
   
    $(".loader").fadeOut("slow");
  }
  
               
  
    function updateUserOnlineStatus(data,status){
        var user_id = data;
        
       // Materialize.toast('user_id '+user_id+" is now online ", 6000); 
        
        if(status == 1){
            $('#o_'+user_id).removeClass('away');
            $('#o_'+user_id).removeClass('inactive');
            $('#o_'+user_id).addClass('available');
        if(opponent_id == user_id){
            $('.onlineStatus').addClass("green-text");
            $('.onlineStatus').text("online");
        }
          

        }else if(status == 0){
            var lastSeen = data.lastSeen;
            lastSeen = formatTime(lastSeen);
            $('#o_'+user_id).removeClass('inactive');
            $('#o_'+user_id).addClass('away');
            $('#o_'+user_id).removeClass('available');  
             if(opponent_id == user_id){
                 $('.onlineStatus').removeClass("green-text");
                 $('.onlineStatus').text("last seen "+lastSeen);
             }
              $('#con_'+user_id).find(".last_seen").text(lastSeen);
            // Materialize.toast('user_id '+user_id+" is now offline - last seen "+lastSeen, 6000); 
        }
        
        //a hack for server disconnection to read correct online /offline status
       // socket.broadcast.to(chatRoom).emit('acknowlegedOnlineStatus',data);  
    }

function receiveMessage(chatMessage) {
    
       audioTone();
       var typeOfRoom = getCookie("typeOfRoom");

        var message = chatMessage.message.message;
        var created_at = chatMessage.message.created_at;
        var type = chatMessage.message.type;
        var filepath = chatMessage.message.filepath;
        var message_id = chatMessage.message.message_id;
        var temp_message_id = chatMessage.message.temp_message_id;;
        var whoSent = "opponent";
        
         var time = formatTime(created_at);
         
        var  color = 'green';
          
          if(whoSent != 'Me'){
            var fLetter = whoSent.substring(0,1).toUpperCase();
            if(fLetter <= "D" ){
                color = 'green';
            }else if(fLetter > "D" && fLetter <= "I"){
                 color = 'brown';
            }else if(fLetter > "I" && fLetter <= "P"){
                 color = '#009999';
            }else if(fLetter > "P" && fLetter <= "U"){
                 color = 'purple';
            }else if(fLetter > "U" && fLetter <= "Z"){
                color = '#ff6600';
            }
        }
       
    if(chatMessage.type == 'individual'){
        
        var user_id = chatMessage.message.user_id;
        var opponent_id = getCookie("opponent_id");

        
        if(opponent_id == user_id ){
        
          var opponentPhoto = getCookie('opponent_photo');

      //  console.log(message);
        stopTyping();

      if( typeOfRoom == 'individual'){
           
              if(type == 'text'){
                   $('<div class="message message-text new"><figure class="avatar left-avatar circle"><img src="'+opponentPhoto+'"/></figure>' + message + '<br><span class="time-new">'+time+'</span></div>').appendTo($('.messages-content')).addClass('new');
              }else if(type == 'image'){
                      $(' <div class="demo-gallery message message-image new z-depth-1" style="max-width:170px; max-height:250px;"><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="'+whoSent+'">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="white-text">'+message+'<br></div>\n\
                     <span class="time-new">'+time+'</span></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');  
                
                  initPhotoSwipe();
          }else if (type == 'audio'){
                   $('<div class="message message message-audio new  " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class="audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');    
        }else if (type == 'video'){
              $('<div class="message message new message-video " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');    
        }
                updateScrollbar();
        }else{
            manageChatsAnchor(); 
        }
                 if($.inArray(opponent_id,chatList) == -1){
                   createChat(chatMessage.message.message,"individual");
                  }else{
                    if(type == 'text'){
                        $('#cha_'+opponent_id).find('.status').text(chatMessage.message.message);
                   }
                  }
                
                var data = {
                    message_id:message_id,
                    temp_message_id:temp_message_id,
                    time:getCurrentDate(),
                    user_id:opponent_id
                }
                
                socket.emit("acknowledgeReceive",data);
           
            }else{
              manageChatsAnchor(); 
            }
             updateScrollbar();
             
             
             
     }else if (chatMessage.type == 'group'){
            var opponentPhoto = getCookie('opponent_photo');
            var the_group_id = getCookie('group_id');
            var group_id = chatMessage.message.group_id;
            var whoSent = chatMessage.message.username;
            var phoneNo = chatMessage.message.phoneNo;
            var profile_photo = "profile_images/"+chatMessage.message.profile_photo;
            
    
            if(the_group_id == group_id){

               if(typeOfRoom == 'group'){
                if(type == 'text'){
                     $('<div class="message message-text other-groups z-depth-1"><figure class="avatar left-avatar circle"><img src="'+profile_photo+'"/></figure>' + message + '<br><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span><span class="time-group-other right">'+time+'</span></div>').appendTo($('.messages-content')).addClass('');
                }else if(type == 'image'){
                    $(' <div class="demo-gallery message message-image other-groups z-depth-1" style="max-width:170px; max-height:250px;"><figure class="avatar left-avatar circle"><img src="'+profile_photo+'"/></figure><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="'+whoSent+'">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="black-text">'+message+'<br><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span></div>\n\
                     <span class="time-group-other right">'+time+'</span></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');  
                     initPhotoSwipe();
                 }else if (type == 'audio'){
                   $('<div class="message message-audio other-groups z-depth-1" style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="teal-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class="audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span><div><span class="time-group-other right">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('');    
                }else if (type == 'video'){
                      $('<div class="message message-video other-groups z-depth-1 " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+profile_photo+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="teal-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="left username_in_group" style="color:'+color+'"> - '+whoSent+'</span><div><span class="time-group-other right">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('');    
                }
                updateScrollbar();
            }else{
                manageChatsAnchor(); 
            }
                if($.inArray(group_id,groupChatList )== -1){
                      createChat(chatMessage.message.message,"group");
                }else{
                 if(type == 'text'){
                     $('#gri_'+group_id).find('.status').text(chatMessage.message.message);
                 }
               }
        }else{
            manageChatsAnchor(); 
        }
     }
     $(".audio_div").click(function(){
            $path = $(this).find('.audio_path').text()
             $('#playAudioModal').openModal();

                var name  = $path.slice(14,$path.length);
                 player = new Player([
                  {
                    title: name,
                    file: $path,
                    howl: null
                  }
                ]);

        });
        $('.modal-trigger').leanModal({
                dismissible: true, // Modal can be dismissed by clicking outside of the modal
                opacity: .5, // Opacity of modal background
                in_duration: 300, // Transition in duration
                out_duration: 200, // Transition out duration
                ready: function() {
                    player.play();
                }, 
                complete: function() {
                    player.pause(); 
                } 
              }
            );
     
     
     
    }
    function manageChatsAnchor(){
        //Hint : Store user_id and count for unread messages
        $unreadMessages++;
           $("#chats_anchor").attr("data-badge",$unreadMessages);
    }


  function insertMessage(type,msg,filepath,random) {
      
      
   
    if ($.trim(msg) == '' && type == 'text') {
        return false;
    }
    var time = getCurrentDate();
     
    var userPhoto = getCookie('profile_photo');
    
    var typeOfRoom = getCookie("typeOfRoom");
    
    
      if(typeOfRoom == 'individual'){
        if(type == 'text'){
            $('<div class="message message-personal message-text " ><figure class="avatar right-avatar circle"><img src="profile_images/'+userPhoto+'"/></figure>' + msg + '<br><div id='+random+'><img class="tick-personal tick_spacing not_sent" src="images/msg_clock_w.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');
        }
      }else if(typeOfRoom == 'group'){
          if(type == 'text'){
             $('<div class="message message-text message-personal-groups z-depth-1"><figure class="avatar right-avatar circle"> <img src="profile_images/'+userPhoto+'"/></figure>' + msg + '<br><div id="text'+random+'"><img class="tick-personal delivered" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('');
        }
      }
           


    sendMessage(type,msg,filepath,random);
    
    updateScrollbar();

}
function sendMessage(type,msg,filepath,random) {
    
    $('.message-input').empty();
    
    var typeOfRoom = getCookie("typeOfRoom");
    var user_id = getCookie("user_id");
    var opponent_id = getCookie("opponent_id");
    
    if(typeOfRoom == 'individual'){
          if($.inArray(opponent_id,chatList) == -1){
           createChat(msg,"individual");
          }else{
           if(type == 'text'){
               $('#cha_'+opponent_id).find('.status').text(msg);
           }
         }
         var message = {
             
            user_id:user_id ,
            message: msg,
            temp_message_id:random,
            opponentId: opponent_id,
            type:type,
            filepath:filepath
        };
    }else if(typeOfRoom == 'group'){
        var group_id = getCookie("group_id");
        
        if($.inArray(group_id,groupChatList )== -1){
           createChat(msg,"group");
          }else{
           if(type == 'text'){
               $('#gri_'+group_id).find('.status').text(msg);
           }
         }
         
         var username = getCookie("username");
         var phoneNo = getCookie("phoneNo");
         var profile_photo = getCookie("profile_photo");
         
         var message = {
            user_id:user_id ,
            message: msg,
            temp_message_id:random,
            group_id: group_id,
            type:type,
            filepath:filepath,
            username:username,
            profile_photo:profile_photo,
            phoneNo:phoneNo
        };
    }
        
       var messageEnvelope = {
           type:typeOfRoom,
           message:message
       }
      socket.emit("msg", messageEnvelope);
}

function readUrl(input,type){
    if(input.files && input.files[0]){
        var reader = new FileReader();
        reader.onload = function(e){
            if(type == 'audio'){
            $('#audio-player').attr('src',e.target.result);
           }else if(type == 'image'){
                $('.modal-preview_img').attr('src',e.target.result);
           }
        }
        reader.readAsDataURL(input.files[0]);
    }
}

function uploadProfile(files,username,status,action){
    
   
 
  if (files.length > 0){
    // create a FormData object which will be sent as the data payload in the
    // AJAX request
    var formData = new FormData();
   
     
    // loop through all the selected files and add them to the formData object
    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      //sending image
      // add the files to formData object for the data payload
      formData.append('uploads[]', file, file.name);
    }
    
    if(action == "registration"){
      $('#username').val("");
      $('#status').val("");
    }else if(action == "edit"){
         $("#username_profile").val("");
        $("#status_profile").val("");
    }
    
      $('.modal-preview_img').removeAttr('src');
      
     
  }
   ajaxUploadProfile(formData,username,status,files[0].name,action);
    
  }
  function sendProfileInfo(username,status,profile_photo,action){
      
        var profileData = {
               user_id: getCookie("user_id"),
               username:username,
               status:status,
               profile_photo:profile_photo
           };
      setCookie("username", username, 1);
      
      if(action == "registration"){
          socket.emit("sendProfile", profileData);
      }else if(action == "edit") {
         socket.emit("editProfile", profileData); 
      }
      
    }    
function ajaxUploadProfile(formData,username,status,profile_photo,action){
    
    if(action == "registration"){
         $('#createProfileModal').modal('show');
    }
    

     $.ajax({
      url: '/upload?folder=profile_images',
      type: 'POST',
      data: formData,
      processData: false,
      contentType: false,
      success: function(data){
          
          console.log('upload successful!\n' + data);
         
          
         // Materialize.toast('upload successful!', 4000); 
       // alert('upload successful!')
           
      },
      xhr: function() {
        // create an XMLHttpRequest
        var xhr = new XMLHttpRequest();

        // listen to the 'progress' event
        xhr.upload.addEventListener('progress', function(evt) {

          if (evt.lengthComputable) {
            // calculate the percentage of upload completed
            var percentComplete = evt.loaded / evt.total;
            percentComplete = parseInt(percentComplete * 100);

            // update the Bootstrap progress bar with the new percentage
          
           /* var progressBar = $('#createProfileModal').find("#createProfileProgress");
            $(progressBar).text(percentComplete + '%');
            $(progressBar).width(percentComplete + '%');*/

            // once the upload reaches 100%, set the progress bar text to done
            if (percentComplete === 100) {
                 setCookie("profile_photo", profile_photo, 1);
                sendProfileInfo(username,status,profile_photo,action); 
            }

          }

        }, false);

        return xhr;
      }
    });
}

$('#upload-image-btn').on('click', function (){
   upload('image');
});
$('#upload-audio-btn').on('click', function (){
   upload('audio');
});
$('#upload-video-btn').on('click', function (){
   upload('video');
});
$("#upload-audio").change(function(){
    readUrl(this,'audio');
});
$("#upload-image").change(function(){
    readUrl(this,'image');
});
$("#upload-profile-image").change(function(){
    readUrl(this,'image');
});

$("#upload-video").change(function(){
    readUrl(this,'video');
});
$("#profile-image").change(function(){
    readUrl(this,'image');
});
function upload(type){
    $('.progress-bar').text('0%');
    $('.progress-bar').width('0%');

//for image
  if(type == 'image'){
        var files = $('#upload-image').get(0).files;
 
  if (files.length > 0){
      $('#imageModal').closeModal();
    // create a FormData object which will be sent as the data payload in the
    // AJAX request
    var formData = new FormData();
     var random = Math.round(Math.random()*10000000000);
     
      var messageCaption = $(".caption").val();
     
    // loop through all the selected files and add them to the formData object
    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      //sending image
      
       var filepath = "image_uploads/"+file.name;
      
       insertMessage('image',messageCaption,filepath,random);
       $(".caption").val("");
      // add the files to formData object for the data payload
      formData.append('uploads[]', file, file.name);
    }
      $('#upload-input').val("");
      $('.modal-preview_img').removeAttr('src');
  }
        ajaxUpload(formData,messageCaption,"image_uploads",filepath,random,'image');
         updateScrollbar();

  //for audio
   }else if(type == 'audio'){
        var files = $('#upload-audio').get(0).files;
        var messageCaption = $("#audioCaption").val();

        if (files.length > 0){
            $('#audioModal').closeModal();
          // create a FormData object which will be sent as the data payload in the
          // AJAX request
          var formData = new FormData();
           var random = Math.round(Math.random()*10000000000);


          // loop through all the selected files and add them to the formData object
          for (var i = 0; i < files.length; i++) {
            var file = files[i];
            //sending image

             var filepath = "audio_uploads/"+file.name;

             insertMessage('audio',messageCaption,filepath,random);
           
            // add the files to formData object for the data payload
            formData.append('uploads[]', file, file.name);
          }
            $("#audioCaption").val("");
            $('#upload-audio').val("");
            $('#audio-player').removeAttr('src');
        
            ajaxUpload(formData,messageCaption,"audio_uploads",filepath,random,'audio');
            updateScrollbar();
        }


  }else if(type == 'video'){
        var files = $('#upload-video').get(0).files;
        var messageCaption = $("#videoCaption").val();

 
        if (files.length > 0){
            $('#videoModal').closeModal();
          // create a FormData object which will be sent as the data payload in the
          // AJAX request
          var formData = new FormData();
           var random = Math.round(Math.random()*10000000000);


          // loop through all the selected files and add them to the formData object
          for (var i = 0; i < files.length; i++) {
            var file = files[i];
            //sending image

             var filepath = "video_uploads/"+file.name;

             insertMessage('video',messageCaption,filepath,random);
             
            // add the files to formData object for the data payload
            formData.append('uploads[]', file, file.name);
          }
            $('#upload-video').val("");
            $("#videoCaption").val("");
        
            ajaxUpload(formData,messageCaption,"video_uploads",filepath,random,'video');
            updateScrollbar();
        }


  }else{
      alert("please select at least one  file");
  }
  updateScrollbar();
}

function ajaxUpload(formData,messageCaption,folder,filepath,random,type){
    $.ajax({
      url: '/upload?folder='+folder,
      type: 'POST',
      data: formData,
      processData: false,
      contentType: false,
      success: function(data){
          
          console.log('upload successful!\n' + data);
          Materialize.toast('upload successful!', 4000); 
          
          
          var message = messageCaption;
          var time = getCurrentDate();
          var userPhoto = getCookie('profile_photo');
          
          var typeOfRoom = getCookie("typeOfRoom");
    
      if(typeOfRoom == 'individual'){
          if(type == 'image'){
                   $(' <div class="demo-gallery message message-image message-personal z-depth-1" style="max-width:170px; max-height:250px;"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="Me">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="white-text">'+message+'<br></div>\n\
                     <div id="'+random+'"><img class="tick-image delivered" src="images/msg_check_1.png" /><span class="time-new right">'+time+'</span></div></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');  
               initPhotoSwipe();  
              }else if (type == 'audio'){
                  $('<div class="message message-audio message-personal z-depth-1 " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class="audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+random+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
           } else if(type == 'video'){
               $('<div class="message message-personal message-audio  " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><br><div class="audioMessage">' + message + '</div><br><div id ="'+random+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
           }
      }else if(typeOfRoom == 'group'){
         if(type == 'image'){
               $(' <div class="demo-gallery message message-image message-personal-groups z-depth-1" style="max-width:170px; max-height:250px;"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/><a href="'+filepath+'" data-size="1600x1067" data-med="'+filepath+'" data-med-size="1024x683" data-author="Me">\n\
                      <img src="'+filepath+'" alt="" class="  responsive-img personal_img"/><div class="white-text">'+message+'</div>\n\
                     <div id="img'+random+'"><img class="tick-image delivered" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div> </a>\n\
                     </div>').appendTo($('.messages-content')).addClass('');   
                    initPhotoSwipe(); 
                }else if(type == 'audio'){
              $('<div class="message message-audio message-personal-groups " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><span class=" audio_div"><a class="waves-effect waves-light modal-trigger" href="#playAudioModal"><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/></a><span class="audio_path visuallyhidden">'+filepath+'</span></span><div class="audioMessage">' + message + '</div><div id ="'+random+'"><img class="tick-image not_sent" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
         }else if(type == 'video'){
             $('<div class="message message-audio message-personal-groups" style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+random+'"><img class="tick-image not_sent" src="images/msg_check_2.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.messages-content')).addClass('new');   
         }
      }
                 updateScrollbar();

            $(".audio_div").click(function(){
                $path = $(this).find('.audio_path').text()
                 $('#playAudioModal').openModal();
                   
                    var name  = $path.slice(14,$path.length);
                     player = new Player([
                      {
                        title: name,
                        file: $path,
                        howl: null
                      }
                    ]);
                   
            });
        $('.modal-trigger').leanModal({
                dismissible: true, // Modal can be dismissed by clicking outside of the modal
                opacity: .5, // Opacity of modal background
                in_duration: 300, // Transition in duration
                out_duration: 200, // Transition out duration
                ready: function() {
                    player.play();
                }, 
                complete: function() {
                    player.pause(); 
                } 
              }
            );
      },
      xhr: function() {
        // create an XMLHttpRequest
        var xhr = new XMLHttpRequest();

        // listen to the 'progress' event
        xhr.upload.addEventListener('progress', function(evt) {

          if (evt.lengthComputable) {
            // calculate the percentage of upload completed
            var percentComplete = evt.loaded / evt.total;
            percentComplete = parseInt(percentComplete * 100);

            // update the Bootstrap progress bar with the new percentage
            $('.progress-bar').text(percentComplete + '%');
            $('.progress-bar').width(percentComplete + '%');

            // once the upload reaches 100%, set the progress bar text to done
            if (percentComplete === 100) {
              $('.progress-bar').html('Done');
            }

          }

        }, false);

        return xhr;
      }
    });
}
function onPlayAudio(){
    $bottomStack.find('.not_sent').replaceWith('<img class="tick-personal delivered tick_spacing" src="images/msg_check_1.png" />');
}

//cookie management
function checkCookie(){
    var username = getCookie("username");
    var phoneNo = getCookie("phoneNo");
    var user_id = getCookie("user_id");

    if (username != null && username != "" && phoneNo != null && phoneNo != "" && user_id != null && user_id != ""){
        //alert("Welcome again " + username);
        return true;
    }else{
       return false;
    }
}
function userTyping() {
    //$('<div class="message loading new"><span></span></div>').appendTo($('.messages-content'));
    updateScrollbar();
}
function stopTyping() {
   // $('.message.loading').remove();
}

function getCurrentDate() {
    var d = new Date();
        var m = d.getMinutes();
        var h = d.getHours();
       var time = h + ':' + m ;
       var hour = time.slice(0,2);
     var min = time.slice(2,5);
     var ext = null;
       if(hour > 12){
        hour = hour -12;
        ext = "PM";
     }else if(hour == 12 && min == ':00'){
        ext="";
     }else if(hour == 12){
         ext = "PM";
     }else{
         ext = "AM";
     }
      return  time = hour +min+ " "+ext;
   
}
function handleDisconnect(){
    
      //change contact status
        $('.a-status').removeClass('away');
        $('.a-status').addClass('inactive');
        $('.a-status').removeClass('available');  
        $('.onlineStatus').removeClass("green-text");
        $('.onlineStatus').text("offline");
        
        $('.mdl-demo').append('<h5 class="  mdl-color--Yellow-900  mdl-color-text--White col l9 center-align " id="internet-connection" >&nbsp;&nbsp; Connection to Server Lost !&nbsp;&nbsp;</h5>');
        

       // $toastContent = $('<div class="message new mdl-color--Red-800 col l12"  float:center ">  No Internet Connection !</div>');
       // Materialize.toast($toastContent, 5000);
}
function updateScrollbar() {
    $('.messages').mCustomScrollbar("update").mCustomScrollbar('scrollTo', 'bottom', {
        scrollInertia: 10,
        timeout: 0
    });
}


$(window).on('keydown', function (e) {
    if (e.which == 13) {
        //insertMessage();
        return false;
    }
});
function initPhotoSwipe(){
    var initPhotoSwipeFromDOM = function (gallerySelector) {

        var parseThumbnailElements = function (el) {
            var thumbElements = el.childNodes,
                    numNodes = thumbElements.length,
                    items = [],
                    el,
                    childElements,
                    thumbnailEl,
                    size,
                    item;

            for (var i = 0; i < numNodes; i++) {
                el = thumbElements[i];

                // include only element nodes 
                if (el.nodeType !== 1) {
                    continue;
                }

                childElements = el.children;

                size = el.getAttribute('data-size').split('x');

                // create slide object
                item = {
                    src: el.getAttribute('href'),
                    w: parseInt(size[0], 10),
                    h: parseInt(size[1], 10),
                    author: el.getAttribute('data-author')
                };

                item.el = el; // save link to element for getThumbBoundsFn

                if (childElements.length > 0) {
                    item.msrc = childElements[0].getAttribute('src'); // thumbnail url
                    if (childElements.length > 1) {
                        item.title = childElements[1].innerHTML; // caption (contents of figure)
                    }
                }


                var mediumSrc = el.getAttribute('data-med');
                if (mediumSrc) {
                    size = el.getAttribute('data-med-size').split('x');
                    // "medium-sized" image
                    item.m = {
                        src: mediumSrc,
                        w: parseInt(size[0], 10),
                        h: parseInt(size[1], 10)
                    };
                }
                // original image
                item.o = {
                    src: item.src,
                    w: item.w,
                    h: item.h
                };

                items.push(item);
            }

            return items;
        };

        // find nearest parent element
        var closest = function closest(el, fn) {
            return el && (fn(el) ? el : closest(el.parentNode, fn));
        };

        var onThumbnailsClick = function (e) {
            e = e || window.event;
            e.preventDefault ? e.preventDefault() : e.returnValue = false;

            var eTarget = e.target || e.srcElement;

            var clickedListItem = closest(eTarget, function (el) {
                return el.tagName === 'A';
            });

            if (!clickedListItem) {
                return;
            }

            var clickedGallery = clickedListItem.parentNode;

            var childNodes = clickedListItem.parentNode.childNodes,
                    numChildNodes = childNodes.length,
                    nodeIndex = 0,
                    index;

            for (var i = 0; i < numChildNodes; i++) {
                if (childNodes[i].nodeType !== 1) {
                    continue;
                }

                if (childNodes[i] === clickedListItem) {
                    index = nodeIndex;
                    break;
                }
                nodeIndex++;
            }

            if (index >= 0) {
                openPhotoSwipe(index, clickedGallery);
            }
            return false;
        };

        var photoswipeParseHash = function () {
            var hash = window.location.hash.substring(1),
                    params = {};

            if (hash.length < 5) { // pid=1
                return params;
            }

            var vars = hash.split('&');
            for (var i = 0; i < vars.length; i++) {
                if (!vars[i]) {
                    continue;
                }
                var pair = vars[i].split('=');
                if (pair.length < 2) {
                    continue;
                }
                params[pair[0]] = pair[1];
            }

            if (params.gid) {
                params.gid = parseInt(params.gid, 10);
            }

            return params;
        };

        var openPhotoSwipe = function (index, galleryElement, disableAnimation, fromURL) {
            var pswpElement = document.querySelectorAll('.pswp')[0],
                    gallery,
                    options,
                    items;

            items = parseThumbnailElements(galleryElement);

            // define options (if needed)
            options = {
                galleryUID: galleryElement.getAttribute('data-pswp-uid'),
                getThumbBoundsFn: function (index) {
                    // See Options->getThumbBoundsFn section of docs for more info
                    var thumbnail = items[index].el.children[0],
                            pageYScroll = window.pageYOffset || document.documentElement.scrollTop,
                            rect = thumbnail.getBoundingClientRect();

                    return {x: rect.left, y: rect.top + pageYScroll, w: rect.width};
                },
                addCaptionHTMLFn: function (item, captionEl, isFake) {
                    if (!item.title) {
                        captionEl.children[0].innerText = '';
                        return false;
                    }
                    captionEl.children[0].innerHTML = item.title + '<br/><small>Photo: ' + item.author + '</small>';
                    return true;
                }

            };


            if (fromURL) {
                if (options.galleryPIDs) {
                    // parse real index when custom PIDs are used 
                    // http://photoswipe.com/documentation/faq.html#custom-pid-in-url
                    for (var j = 0; j < items.length; j++) {
                        if (items[j].pid == index) {
                            options.index = j;
                            break;
                        }
                    }
                } else {
                    options.index = parseInt(index, 10) - 1;
                }
            } else {
                options.index = parseInt(index, 10);
            }

            // exit if index not found
            if (isNaN(options.index)) {
                return;
            }



            var radios = document.getElementsByName('gallery-style');
            for (var i = 0, length = radios.length; i < length; i++) {
                if (radios[i].checked) {
                    if (radios[i].id == 'radio-all-controls') {

                    } else if (radios[i].id == 'radio-minimal-black') {
                        options.mainClass = 'pswp--minimal--dark';
                        options.barsSize = {top: 0, bottom: 0};
                        options.captionEl = false;
                        options.fullscreenEl = false;
                        options.shareEl = false;
                        options.bgOpacity = 0.85;
                        options.tapToClose = true;
                        options.tapToToggleControls = false;
                    }
                    break;
                }
            }

            if (disableAnimation) {
                options.showAnimationDuration = 0;
            }

            // Pass data to PhotoSwipe and initialize it
            gallery = new PhotoSwipe(pswpElement, PhotoSwipeUI_Default, items, options);

            // see: http://photoswipe.com/documentation/responsive-images.html
            var realViewportWidth,
                    useLargeImages = false,
                    firstResize = true,
                    imageSrcWillChange;

            gallery.listen('beforeResize', function () {

                var dpiRatio = window.devicePixelRatio ? window.devicePixelRatio : 1;
                dpiRatio = Math.min(dpiRatio, 2.5);
                realViewportWidth = gallery.viewportSize.x * dpiRatio;


                if (realViewportWidth >= 1200 || (!gallery.likelyTouchDevice && realViewportWidth > 800) || screen.width > 1200) {
                    if (!useLargeImages) {
                        useLargeImages = true;
                        imageSrcWillChange = true;
                    }

                } else {
                    if (useLargeImages) {
                        useLargeImages = false;
                        imageSrcWillChange = true;
                    }
                }

                if (imageSrcWillChange && !firstResize) {
                    gallery.invalidateCurrItems();
                }

                if (firstResize) {
                    firstResize = false;
                }

                imageSrcWillChange = false;

            });

            gallery.listen('gettingData', function (index, item) {
                if (useLargeImages) {
                    item.src = item.o.src;
                    item.w = item.o.w;
                    item.h = item.o.h;
                } else {
                    item.src = item.m.src;
                    item.w = item.m.w;
                    item.h = item.m.h;
                }
            });

            gallery.init();
        };

        // select all gallery elements
        var galleryElements = document.querySelectorAll(gallerySelector);
        for (var i = 0, l = galleryElements.length; i < l; i++) {
            galleryElements[i].setAttribute('data-pswp-uid', i + 1);
            galleryElements[i].onclick = onThumbnailsClick;
        }

        // Parse URL and open gallery if it contains #&pid=3&gid=1
        var hashData = photoswipeParseHash();
        if (hashData.pid && hashData.gid) {
            openPhotoSwipe(hashData.pid, galleryElements[ hashData.gid - 1 ], true, true);
        }
    };

    initPhotoSwipeFromDOM('.demo-gallery');
                  

                 
                                
}



