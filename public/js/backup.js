$(window).load(function () {
    $messages.mCustomScrollbar();
     $("#contacts-list").mCustomScrollbar();
    $("#chats-list").mCustomScrollbar();
    initSocket();
});

var $messages = $('.messages-content'),
        d, h, m,
        i = 0;

var opponent_id = getCookie("opponent_id");
var opponent_username = getCookie("opponent_username");
var opponent_photo = getCookie("opponent_photo");


var chatList = [];

   var socket = io.connect();

    socket.on("sendProfile", function (data) {
         window.location = '/index'; 
    });
    
  if(checkCookie()){
     
     
    var user_id = {
        user_id: getCookie("user_id")
    };
    var loginData = {
        user_id: getCookie("user_id"),
        phoneNo: getCookie("phoneNo")
    };
        
      socket.emit("login", loginData);

      socket.on("autoLogin", function (data) {
         $('#internet-connection').remove();
          socket.emit("login", loginData);
    });
    
   
 }

$("#loginForm").submit(function (e) {
    e.preventDefault();
    var password_r = $("#password").val();
    var phoneNo_r = $("#phoneNo").val();

    var registerData = {
        password: password_r,
        phoneNo: phoneNo_r
    };

    socket.emit("registerUser", registerData);
    socket.on("registerUser", function (data) {
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
               // alert("welcome back !");
               
                username_r = data.username
                
                setCookie("user_id", data.user_id, 1);
                 setCookie("username", username_r, 1);
                 setCookie("phoneNo", phoneNo_r, 1); 
             
                window.location = '/index';
                
                break;

        }
    });

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
$('#regButton').click(function(){
    var id = $('#username').val();
    var files = $('#profile-image').get(0).files;
     if(!id ){
        alert("input username !");
        $('.username-div').addClass('has-error');
    }else if(files.length < 1 && id){
           alert("select image !");
    }else{
         uploadProfile();
    }
   
   
});
function initSocket() {
    var user_id = {
        user_id: getCookie("user_id")
    };

    socket.emit("getChats", user_id);
    socket.emit("getContacts", user_id);


    socket.on("getChats", function (data) {
        getChats(data);
    });
    
    socket.on("getContacts", function (data) {
       getContacts(data);
    });
     socket.on("userOnline", function (data) {
        updateUserOnlineStatus(data,1);
    });
    socket.on("userOffline", function (data) {
        updateUserOnlineStatus(data,0);
    });
    socket.on("insertChatMessage", function (data) {
        onInsertedMessage(data);
    });
    socket.on("receivedMessage",function (data) {
        onReceivedMessage(data);
    });

    if(opponent_id != null || opponent_id != "" || opponent_username != null || opponent_username != ""  ){
         $(".opponent_username").text(opponent_username);
         
         if(opponent_photo != null || opponent_photo != "" ){
             $(".opponent_photo").attr('src',opponent_photo);
         }
         
          requestMessages(opponent_id);
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
    
    $('#username').text(username);
    $('#phoneNo').text(phoneNo);
    $('#profile_photo').attr('src',"profile_images/"+profile_photo);
    
    setCookie("username", username, 1);
    setCookie("status", status, 1);
    setCookie("phoneNo", phoneNo, 1);
    setCookie("profile_photo", profile_photo, 1);
}
function requestMessages(opponent_id){
    var chatDet = {
        user_id:getCookie("user_id"),
        opponent:opponent_id
    }
    socket.emit("getChatMessages", chatDet);
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
    
    var messageContainer = $(".mCSB_container");     
     
     var date = new Date().toDateString();

     
     if (Array.isArray(chatMessages)) {
          messageContainer.empty();
          
        $(' <p class="datestamp">'+date+'</p>').appendTo($('.mCSB_container'));

        for (var i = 0; i < chatMessages.length; i++) {
            var messageObj = chatMessages[i];
            
            var message = messageObj.message;
             var message_id = messageObj.message_id;
             var whoSent = messageObj.sender;
            var created_at = messageObj.created_at;
            var type = messageObj.type;
            var filepath = messageObj.filepath;
            var delivered = messageObj.delivered;
            
            var userPhoto = getCookie('profile_photo');
            var opponentPhoto = getCookie('opponent_photo');
            
            var size = created_at.length;
            var time = formatTime(created_at);

            if(type == 'text'){
                if(whoSent == 'Me'){
                     $('<div class="message message-personal"><figure class="avatar right-avatar circle"> <img src="profile_images/'+userPhoto+'"/></figure>' + message + '<br><div id="text'+message_id+'"><img class="tick-personal delivered" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');
                     if(delivered == 1){
                        $('#text'+message_id).append('<img class="tick-personal received" src="images/msg_check_2.png" />');
                         $('.delivered').addClass('no_tick_spacing');
                     }else{
                         $('.delivered').addClass('tick_spacing');
                     }
                }else if(whoSent == 'opponent'){
                   $('<div class="message new"><figure class="avatar left-avatar circle"><img src="'+opponentPhoto+'"/></figure>' + message + '<br><span class="time-new">'+time+'</span></div>').appendTo($('.mCSB_container')).addClass('new');
                }

            }else if(type == 'image'){
                if(whoSent == 'Me'){
                    $('<div class="message message-personal" style="max-width:170px;"><figure class="avatar right-avatar circle"><img src="profile_images/'+userPhoto+'"/></figure><img src="'+filepath+'" class=" materialboxed responsive-img personal_img"/>' + message + '<br><div id="img'+message_id+'"><img class="tick-image delivered" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');  
                     if(delivered == 1){
                        $('#img'+message_id).append('<img class="tick-image received" src="images/msg_check_2.png" />');
                         $('.delivered').addClass('no_tick_spacing');
                     }else{
                         $('.delivered').addClass('tick_spacing');
                     }
                }else if(whoSent == 'opponent'){
                    $('<div class="message message new" style="max-width:170px"><figure class="avatar left-avatar circle"><img src="'+opponentPhoto+'"/></figure><span><img src="'+filepath+'" class=" materialboxed responsive-img personal_img"/></span>' + message + '<br><div><span class="time-new">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');  
  
                }
            }else if (type == 'audio'){
                if(whoSent == 'Me'){
                  $('<div class="message message-personal  " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><img src="images/play_w2.png" class="responsive-img audio_img audio-play "/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');   
               }else if(whoSent == 'opponent'){
                   $('<div class="message message new  " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');    
               }
            }else if (type == 'video'){
                if(whoSent == 'Me'){
                  $('<div class="message message-personal  " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');   
               }else if(whoSent == 'opponent'){
                  $('<div class="message message new  " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');    
               }
            }
               updateScrollbar();
            }
    }
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
     
     if(data.type == 'text'){
        $bottomStack.find('.not_sent').replaceWith('<img class="tick-personal delivered tick_spacing" src="images/msg_check_1.png" />');
    
    }else if(data.type == 'image'){
         $bottomStack.find('.not_sent').replaceWith('<img class="tick-image delivered tick_spacing" src="images/msg_check_1.png" />');
     }
    
    Materialize.toast('Message delivered!', 4000); 
    updateScrollbar();
    
}
function getChats(data) {
    if (Array.isArray(data)) {

        for (var i = 0; i < data.length; i++) {

            var chatMessage = data[i];
            var username = chatMessage.username;
            var profilePhoto = chatMessage.profile_photo;
            var user_id = chatMessage.user_id;
            var message = chatMessage.message;
            var lastMessage = message.lastMessage;
            var time_r = message.time;
            var time = time_r.slice(11,16)+" "+time_r.slice(0,10);


            var chatData = '<a class="collection-item avatar "  id="cha_'+user_id+'">\n\
                              <img src="profile_images/'+profilePhoto+'"  alt="" class="circle profile_photo ">\n\
                             <span  class="username"><b>'+username+'</b></span>\n\
                             <span class="time_chat right">'+time+'</span>\n\
                             <p  class="status" >'+lastMessage+'</p></a> ';
             
              chatList.push(user_id+""); 
              $("#chats_holder").append(chatData);
        }
      
      
    }
         $("ul#chats_holder a").click(function () {
                var rawId =$(this).prop('id');
                var id = rawId.slice(4);
                opponent_id = id;
                setCookie("opponent_id", opponent_id, 1);
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
                    $('.onlineStatus').removeClass("green-text");
                    $('.onlineStatus').text("last seen "+lastSeen);
                }
                requestMessages(id);
                         
        });
}
function createChat(msg){
    var time = getCurrentDate();
    var opponent_username = getCookie('opponent_username');
    
    var chatData = '<a class="collection-item avatar "  id="cha_'+opponent_id+'">\n\
                    <img src="images/'+opponent_photo+'" alt="" class="circle ">\n\
                    <span  class="username"><b>'+opponent_username+'</b></span>\n\
                    <span class="time_chat right">'+time+'</span>\n\
                    <p  class="status" >'+msg+'</p></a> ';
         $("#chats_holder").append(chatData);
         chatList.push(opponent_id);
}
function getContacts(data) {
    if (Array.isArray(data)) {
        
         console.log('online contacts');
         console.log(data);
         
         var size = data.length-1;
         var onlineContacts = data[size];              
        
        for (var i = 0; i < size; i++) {

             var contact = data[i];
            var username = contact.username;
            var status = contact.status;
            var profilePhoto = contact.profile_photo;
            var phoneNo = contact.phoneNo;
            var user_id = contact.user_id;
            var lastSeen = contact.last_seen;
            lastSeen = formatTime(lastSeen);
            var onlineStatus = 'away';
           
           
           if($.inArray(user_id,onlineContacts) != -1){
               onlineStatus = 'available';
           }
           
           if(opponent_id == user_id && onlineStatus == 'available' ){
               $('.onlineStatus').addClass("green-text");
               $('.onlineStatus').text("online");
           }else  if(opponent_id == user_id && onlineStatus != 'available' ){
               $('.onlineStatus').removeClass("green-text");
               $('.onlineStatus').addClass("purple-text");
               $('.onlineStatus').text("last seen "+lastSeen);
           }
              
           var contactData = '<a class="collection-item avatar" username="con_'+username+'" id="con_'+user_id+'">\n\
                        <img src="profile_images/'+profilePhoto+'" alt="" class="profile_photo circle ">\n\
                        <span  class="username"><b>'+username+'</b></span><p class="invisible right last_seen" >'+lastSeen+'</p>\n\
                        <span class="a-status '+onlineStatus+' right" id="o_'+user_id+'"></span>\n\
                        <p class="status" >'+status+'</p></a> ';
             
              $("#contacts_holder").append(contactData);
              
        } 
              
    }
    //you prick why are you troublesome -- never give down !
    $("ul#contacts_holder a").click(function () {
        var rawId =$(this).prop('id');
        var id = rawId.slice(4);
        opponent_id = id;
        setCookie("opponent_id", opponent_id, 1);
        
        var username = $(this).find(".username").text();
        $(".opponent_username").text(username);
        setCookie("opponent_username", username, 1);
        
        var profilePhoto = $(this).find(".profile_photo").attr('src');
        $(".opponent_photo").attr('src',profilePhoto);
         setCookie("opponent_photo", profilePhoto, 1);
      
        
       var lastSeen = $(this).find(".last_seen").text();

       var onlineStatus =  $(this).find(".a-status").prop('id');
       
        if($("#"+onlineStatus).hasClass('available')){
           $('.onlineStatus').addClass("green-text");
           $('.onlineStatus').text("online");
       }else{
           $('.onlineStatus').removeClass("green-text");
           $('.onlineStatus').text("last seen "+lastSeen);
       }
        requestMessages(id);
    });  

  }
  
               
  
    function updateUserOnlineStatus(data,status){
        var user_id = data.user_id;
        
        if(status == 1){
            $('#o_'+user_id).removeClass('away');
            $('#o_'+user_id).removeClass('inactive');
            $('#o_'+user_id).addClass('available');
             if(opponent_id == user_id){
                 $('.onlineStatus').addClass("green-text");
                  $('.onlineStatus').text("online");
             }
          //   Materialize.toast('user_id '+user_id+" is now online ", 6000); 

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
    var message = chatMessage.msg.message;
    var created_at = chatMessage.msg.created_at;
    var type = chatMessage.msg.type;
    var filepath = chatMessage.msg.filepath;
    var message_id = chatMessage.msg.message_id;
    
   var time = formatTime(created_at);
   
   var opponentPhoto = getCookie('opponent_photo');
   
    
    console.log(message);
    stopTyping();
    
            if(type == 'text'){
                $('<div class="message new"><figure class="avatar left-avatar circle"><img src="'+opponentPhoto+'"/></figure>' + message + '<br><span class="time-new">'+time+'</span></div>').appendTo($('.mCSB_container')).addClass('new');
            }else if(type == 'image'){
                $('<div class="message message new" style="max-width:170px"><figure class="avatar left-avatar circle"><img src="'+opponentPhoto+'"/></figure><span><img src="'+filepath+'" class=" materialboxed responsive-img personal_img"/></span>' + message + '<br><div><span class="time-new">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');  
            }else if (type == 'audio'){
                   $('<div class="message message new  " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><img src="images/play_w2.png" class="responsive-img audio_img audio-play"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');    
            }else if (type == 'video'){
                  $('<div class="message message new  " style="max-width:170px"><figure class="avatar left-avatar circle "><img src="'+opponentPhoto+'"/></figure><span class="right"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><div class="audioMessage">' + message + '</div><div id ="'+message_id+'"><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');    
            }  
            updateScrollbar();
}


  function insertMessage(type,msg,filepath,random) {
   
    if ($.trim(msg) == '' && type == 'text') {
        return false;
    }
    var time = getCurrentDate();
     
    var userPhoto = getCookie('profile_photo');
    if(type == 'text'){
        $('<div class="message message-personal"><figure class="avatar right-avatar circle"><img src="profile_images/'+userPhoto+'"/></figure>' + msg + '<br><div id='+random+'><img class="tick-personal tick_spacing not_sent" src="images/msg_clock_w.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');
    }else if(type == 'image'){
        //read from the destination !!
    }
    sendMessage(type,msg,filepath,random);
    
    updateScrollbar();

}
function sendMessage(type,msg,filepath,random) {
    var user_id = getCookie("user_id");

       if($.inArray(opponent_id,chatList) == -1){
           createChat(msg);
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
    

    socket.emit("msg", message);
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
function uploadProfile(){
    
   var files = $('#profile-image').get(0).files;
   var username = $('#username').val();
 
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
      $('#username').val("");
      $('.modal-preview_img').removeAttr('src');
     
  }
   ajaxUploadProfile(formData,username,files[0].name);
    
  }
  function sendProfileInfo(username,profile_photo){
        var profileData = {
               user_id: getCookie("user_id"),
               username:username,
               profile_photo:profile_photo
           };
      setCookie("username", username, 1);
      socket.emit("sendProfile", profileData);
    }    
function ajaxUploadProfile(formData,username,profile_photo){
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
            sendProfileInfo(username,profile_photo); 
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
          
          if(type == 'image'){
            $('<div class="message message-personal" style="max-width:170px"><figure class="avatar right-avatar circle"><img src="profile_images/'+userPhoto+'"/></figure><img src="'+filepath+'" class=" materialboxed responsive-img personal_img"/>' + message + '<br><div id ="'+random+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');
           }else if (type == 'audio'){
              $('<div class="message message-personal  " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Audio&nbsp;&nbsp;</span></span><img src="images/play_w2.png" class="responsive-img audio_img"/><br><div class="audioMessage">' + message + '</div><br><div id ="'+random+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');   
           } else if(type == 'video'){
               $('<div class="message message-personal  " style="max-width:170px"><figure class="avatar right-avatar circle "><img src="profile_images/'+userPhoto+'"/></figure><span class="left"><img src="images/doc_blue_s.png" class=" circle " style="width: 30px;height: 30px; "/><span class="yellow-text audio_text" >Video&nbsp;&nbsp;</span></span><img src="images/attach_video.png" class="responsive-img audio_img"/><br><div class="audioMessage">' + message + '</div><br><div id ="'+random+'"><img class="tick-image not_sent" src="images/msg_check_1.png" /><span class="time-personal">'+time+'</span></div></div>').appendTo($('.mCSB_container')).addClass('new');   
           }
           updateScrollbar();
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
    $('<div class="message loading new"><span></span></div>').appendTo($('.mCSB_container'));
    updateScrollbar();
}
function stopTyping() {
    $('.message.loading').remove();
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
        
        $('.mdl-demo').append('<h4 class="  mdl-color--Yellow-900  mdl-color-text--White col l9 center-align " id="internet-connection" >  No Internet Connection !</h4>');
        

       // $toastContent = $('<div class="message new mdl-color--Red-800 col l12"  float:center ">  No Internet Connection !</div>');
       // Materialize.toast($toastContent, 5000);
}
function updateScrollbar() {
    $messages.mCustomScrollbar("update").mCustomScrollbar('scrollTo', 'bottom', {
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

