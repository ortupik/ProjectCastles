
var insertChatMessage = function(connection,chatMessageDetails,callback){

  generateChatId(connection,chatMessageDetails.user_id,chatMessageDetails.opponentId,function(chatIdResult){

  var chat = {
      user_id:chatMessageDetails.user_id,
      message:chatMessageDetails.message,
      temp_message_id:chatMessageDetails.temp_message_id,
      chat_id:chatIdResult.chat_id,
      type:chatMessageDetails.type,
      attachment:chatMessageDetails.filepath,
  };
  var confirmation ={
      createdNow:chatIdResult.created_now,
      temp_message_id:chatMessageDetails.temp_message_id,
      type:chatMessageDetails.type
  };

      var query = connection.query('INSERT INTO chats SET ?', chat, function(err, result) {
          console.log(query.sql)
      // Neat!
         if(!err){
            confirmation.inserted = "true";
            var message_id = result.insertId;;
            confirmation.message_id = message_id;
          
          
             var queryTime = connection.query("SELECT `created_at`,`temp_message_id` FROM chats WHERE ? " ,{ message_id:message_id},function(err1, rows1, fields) {
                  if(!err1){
                     if(rows1.length > 0){
                       var created_at = rows1[0]['created_at'];
                       confirmation['created_at'] = created_at;
                       confirmation['temp_message_id'] = chatMessageDetails.temp_message_id;
                       callback(confirmation);
                    }
                }
             });
         } 
      });
  });
}

var updateDeliveryStatus = function(connection,message_id,callback){
    var queryTime = connection.query("UPDATE `chats` SET `delivered`= 1 WHERE ? " ,{ message_id:message_id},function(err1, rows1, fields) {
            if(!err1){
               callback({success:1});
          }else{
               console.log("errror in sql syntax "+err1);
               callback({success:0});
          }
    });
}

//insertion for group messages
var insertGroupChatMessage = function(connection,chatMessageDetails,callback){

  var chat = {
      user_id:chatMessageDetails.user_id,
      message:chatMessageDetails.message,
      group_id:chatMessageDetails.group_id,
      type:chatMessageDetails.type,
      attachment:chatMessageDetails.filepath,
  };
  var confirmation ={
      temp_message_id:chatMessageDetails.temp_message_id,
      type:chatMessageDetails.type
  };

      var query = connection.query('INSERT INTO `group_chats` SET ?', chat, function(err, result) {
      // Neat!
         if(!err){
            confirmation.inserted = "true";
            var message_id = result.insertId;;
            confirmation.message_id = message_id;
          
             var queryTime = connection.query("SELECT `created_at` FROM `group_chats` WHERE ? " ,{ message_id:message_id},function(err1, rows1, fields) {
                  if(!err1){
                     if(rows1.length > 0){
                       var created_at = rows1[0]['created_at'];
                       confirmation['created_at'] = created_at;
                       callback(confirmation);
                    }
                }
             });
         } else{
            console.log("errror in sql syntax "+err);
         }
      });
  
}

//gernerates chat id if exist and creates if not exits
function generateChatId(connection,currentUser,opponent_id,callback){
  var query = connection.query("SELECT * FROM chat_room WHERE (`user1_id` = '"+currentUser+"' AND `user2_id` = '"+opponent_id+"') OR (`user2_id` = '"+currentUser+"' AND `user1_id` = '"+opponent_id+"')",function(err, rows, fields) {
   // console.log(query.sql);

    if (!err){
      //console.log("rows "+rows.length);
      if(rows.length > 0){
         chat_id = rows[0]['chat_id'];
       //  console.log("Chat id "+chat_id+" exists");

         var chat = {
            chat_id:chat_id,
            created_now:0
         };

         callback(chat) ;
      }else{
        console.log("Chat id Does NOT exists");

        var chatData = {
           user1_id:currentUser,
           user2_id:opponent_id
        };
       
              //to ensure we do not create invalid chat rooms
      if((currentUser != opponent_id) && currentUser != 0 && opponent_id != 0 && currentUser != null &&  opponent_id != null && currentUser != '' &&  opponent_id != '' ){
        
         var queryInsert = connection.query('INSERT INTO chat_room SET ?', chatData, function(err2, result2) {
           if (!err2) {
             console.log("Successfully created chat iD for user");
             var chat = {
                chat_id:result2.insertId,
                created_now:1
             };

             callback(chat) ;
           }else{
             console.log(err2);
           }
        });
    }else{
        console.log("FATAL ERROR ! chatroom could not be created");
    }


      }
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
}
//individual messages
var getChatMessages = function(connection,chatDetails,callback){

  generateChatId(connection,chatDetails.user_id,chatDetails.opponent,function(chatIdResult){

      var queryLastTime = connection.query("SELECT `created_at` FROM chats  ORDER BY `created_at` DESC LIMIT 1 " ,function(err, rows, fields) {

      if(!err){
        if(rows.length > 0){
      var  dbLastTime = rows[0]['created_at'];
      //  dbLastTime = "2016-05-24 23:22:23";
     // console.log("Chat details ");

      var fieldDetails = {
         chat_id:chatIdResult.chat_id
      };
    //  console.log(chatDetails);

    //console.log("last_time "+dbLastTime);
    var query = connection.query("SELECT * FROM chats WHERE ?  ORDER BY `created_at` ASC ",fieldDetails,function(err, rows, fields) {
      //var query = connection.query("SELECT * FROM chats WHERE `created_at` BETWEEN '"+chatDetails.last_time+"' AND '"+dbLastTime+"' ORDER BY `created_at` ASC ",function(err, rows, fields) {
      var messageArray = new Array();
        if (!err){

              for(var i = 0; i < rows.length; i++){
                var senderId = rows[i]['user_id'];

                if(senderId==chatDetails.user_id){
                   whoSent = 'Me';
                }else{
                   whoSent = 'opponent';
                }
                 var message ={
                    message:rows[i]['message'],
                    created_at:rows[i]['created_at'],
                    type:rows[i]['type'],
                    filepath:rows[i]['attachment'],
                     message_id:rows[i]['message_id'],//message id is important
                     temp_message_id:rows[i]['temp_message_id'],
                    sender:whoSent,
                    user_id:rows[i]['user_id'],
                    chat_id:rows[i]['chat_id'],
                    delivered:rows[i]['delivered']
                  }
                messageArray.push(message);
              }
             // console.log(query.sql);

              var message = {
                  type:"individual",
                  messageArray:messageArray
              }
              callback(message);

        }else{
          console.log("errror in sql syntax "+err);
        }
     });
}else{
  console.log("0 rows");
}
}else{
   console.log("Error "+err);
}
 });

 });

}

//group messaages
  var getGroupChatMessages = function(connection,chatDetails,callback){

    var query = connection.query("SELECT * FROM `group_chats`,`user_details`,`user` WHERE ? AND user.user_id = user_details.user_id AND group_chats.user_id = user.user_id ORDER BY `group_chats`.`created_at` ASC ",{group_id:chatDetails.group_id},function(err, rows, fields) {

        var messageArray = new Array();

        if (!err){
            for(var i = 0; i < rows.length; i++){
                
                 var senderId = rows[i]['user_id'];
                 var username = rows[i]['username'];

                    if(senderId == chatDetails.user_id){
                       whoSent = 'Me';
                    }else if(username != null){
                       whoSent = username;
                    }else{
                        whoSent = 'RE.USER';
                    }
                    
                var message = {
                    sender:whoSent,
                    profile_photo:rows[i]['profile_photo'],
                    message:rows[i]['message'],
                    created_at:rows[i]['created_at'],
                    type:rows[i]['type'],
                    filepath:rows[i]['attachment'],
                    message_id:rows[i]['message_id'],
                    user_id:senderId,
                    group_id:rows[i]['group_id']
                 }
               messageArray.push(message);
             }
             // console.log(query.sql);
              var message = {
                  type:"groups",
                  messageArray:messageArray
              }
              callback(message);

        }else{
          console.log("errror in sql syntax "+err);
        }
     });
}

exports.insertGroupChatMessage = insertGroupChatMessage;
exports.insertChatMessage = insertChatMessage;
exports.getChatMessages = getChatMessages;
exports.getGroupChatMessages = getGroupChatMessages;
exports.updateDeliveryStatus = updateDeliveryStatus;

