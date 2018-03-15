/**
 * Created by CHRIS on 7/29/2016.
 */

$(function () {
    var socket = io.connect();

    var userId = {
       userId:2
    };




    var loginData = {
        userId:2,
        phoneNo:"0726690119"
    };
    socket.emit("login",loginData);

    socket.emit("getChats",userId);

    socket.on("getChats", function (data) {

        console.log("retriving chats");

        if(Array.isArray(data)){

            for (var i = 0; i < data.length; i++) {
                var chatMessage = data[i];
                var username = chatMessage.username;
                var message = chatMessage.message;
                var lastMessage = message.lastMessage;
                var time = message.time;

                $(".username").text(username);
                $(".comment__text").text(lastMessage);

                console.log("username "+username);
                console.log("lm "+lastMessage)
                console.log("time "+time);
            }


        }


    });
    socket.on('msg', function (chatMessage) {
       console.log("Receiving message");

        var message = chatMessage.msg;
        console.log(message);

        $(".comment__text").text(message.message);

    });


    $("#send_button").click(function () {
        var message = {
           userId:2,
            message:$(".message-input").val(),
            opponentId:1
        };

        socket.emit("msg",message);
        $(".message-input").val('');
    });


});
