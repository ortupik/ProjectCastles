
var getChats = function (connection, chat_id, callback) {


    var currentUser = chat_id.user_id;

    var query = connection.query("SELECT * FROM chat_room WHERE ? OR ? ", [{user1_id: currentUser}, {user2_id: currentUser}], function (err, rows, fields) {
        // console.log(query.sql);

        if (!err) {

            if (rows.length > 0) {
                
                var size = rows.length -1;

                for (var i = 0; i < rows.length; i++) {

                    user1_id = rows[i]['user1_id'];
                    user2_id = rows[i]['user2_id'];
                    chat_id = rows[i]['chat_id'];

                    if (currentUser == user1_id) {
                        opponent = user2_id;
                    } else if (currentUser == user2_id) {
                        opponent = user1_id;
                    }
                   
                    getMessageDetails(connection, chat_id, opponent,i, function (result) {
                            callback(result);
                    });
                }

            } else {
                console.log("No rows " + rows.length);
            }

        } else {
            console.log("errror in sql syntax (chat_room) " + err);
        }
    });
}


function getMessageDetails(connection, chat_id, user_id,index, callback) {
    var chatsArray = [];

  var query2 = connection.query("SELECT *  from `user`,`user_details` WHERE (user.user_id = user_details.user_id) AND (user.user_id = '"+user_id+"'); ",function(err2, rows2, fields) {
        //console.log(query2.sql);

        if (rows2.length > 0) {
            if (!err2) {
                var username = rows2[0]['username'];
                var user_id = rows2[0]['user_id'];
                var phone_no = rows2[0]['phoneNo'];
                var profile_photo = rows2[0]['profile_photo'];
                var lastSeen = rows2[0]['last_seen'];

               // console.log(username);
               // callback(username);


            var query = connection.query("SELECT * FROM chats WHERE ? ORDER BY `created_at` DESC LIMIT 1", [{chat_id: chat_id}], function (err, rows, fields) {
               // console.log(query.sql);

                  if (!err) {

                    if (rows.length > 0) {
                        var message = {
                            lastMessage: rows[0]['message'],
                            time: rows[0]['created_at']
                        }

                        var chatDetail = {
                            username: username,
                            user_id: user_id,
                            chat_id: chat_id,
                            message: message,
                            phoneNo: phone_no,
                            profile_photo:profile_photo,
                            last_seen:lastSeen
                        }
                        
                        //pushing details to array
                        chatsArray.push(chatDetail);
                      
                        callback(chatsArray);

                    } else {
                        console.log("O rows for chats query ");
                    }


                    } else {
                        return "Error " + err;
                        console.log("errror in sql syntax (chats) " + err);
                        callback({lastMessage: "error retiving message", time: "Error Retrieving time"});
                    }

                });
            } else {
                console.log("error in sql syntax -> " + err2);
            }
        }
    });
}
var getOwnChatRoom = function(connection,user_id,callback){
     var query = connection.query("SELECT `chat_id` FROM chat_room WHERE ? AND ? " ,[{user1_id:user_id},{user2_id:user_id}], function (err, rows, fields) {
        console.log(query.sql);

        if (!err) {
            if (rows.length > 0) {
                var chatRoom = {
                    room: rows[0]['chat_id'],
                    success: 1
                }
                callback(chatRoom);

            } else {
                console.log("O rows for chats query <getCurrentChatRoom> ");
                var chatRoom = {
                    success: 0
                }
                callback(chatRoom);
            }

        } else {
            console.log("errror in sql syntax(chat room) " + err);
            var chatRoom = {
                success: 0
            }
            callback(chatRoom);
        }


    });
}
//check for possible sql injection becouse of `+`
  var getCurrentChatRoom = function (connection, user_id, opponentId, callback) {
    var query = connection.query("SELECT * FROM chat_room WHERE (`user1_id` = '" + user_id + "' AND `user2_id` = '" + opponentId + "') OR (`user2_id` = '" + user_id + "' AND `user1_id` = '" + opponentId + "')", function (err, rows, fields) {
        //console.log(query.sql);

        if (!err) {
            if (rows.length > 0) {
                var chatRoom = {
                    room: rows[0]['chat_id'],
                    success: 1
                }
                callback(chatRoom);

            } else {
                console.log("O rows for chats query <getCurrentChatRoom> ");
                var chatRoom = {
                    success: 0
                }
                callback(chatRoom);
            }

        } else {
            console.log("errror in sql syntax(chat room) " + err);
            var chatRoom = {
                success: 0
            }
            callback(chatRoom);
        }


    });
}
var getGroupChats = function(connection,userDetails,callback){
    var groupsArray = new Array();

  var main_query = connection.query("SELECT *  from `group_chat_room` WHERE ? ",{user_id:userDetails.user_id},function(err1, rows1, fields) {
     
      if (!err1){
        var no_of_rows = rows1.length;
          
        if(no_of_rows > 0){
           
        for(var j = 0; j < no_of_rows; j++){
               
             var group_id = rows1[j]['group_id'];
             var role = rows1[j]['role'];
             
                       
            getLastGroupChat(connection,{group_id:group_id},j,function(result1){
                 groupsArray.push(result1);
                 if(result1.index == no_of_rows-1 ){
                        callback(groupsArray ) ;
                        //console.log(groupsArray);
                 }
            });
        }
         // console.log(main_query.sql);
      }else{
          //callback(-1);
      }
    }else{
         console.log("errror in sql syntax "+err1);
    }
   
  });    
}
//get last group chat
  var getLastGroupChat = function(connection,chatDetails,index,callback){

    var query = connection.query("SELECT * FROM `group_chats`,`groups`,`group_details`,`user` WHERE ? AND `groups`.`group_id` = `group_details`.`group_id` AND group_chats.user_id = user.user_id AND group_chats.group_id = groups.group_id ORDER BY `group_chats`.`created_at` DESC LIMIT 1 ",{'group_chats.group_id':chatDetails.group_id},function(err, rows, fields) {

        if (!err){
            
           if(rows.length > 0){ 
            var senderId = rows[0]['user_id'];
            var username = rows[0]['username'];

               if(senderId == chatDetails.user_id){
                  whoSent = 'Me';
               }else if(username != null){
                  whoSent = username;
               }else{
                   whoSent = 'RE.USER';
               }

           var message = {
               sender:whoSent,
               profile_photo:rows[0]['profile_photo'],
               message:rows[0]['message'],
               created_at:rows[0]['created_at'],
               type:rows[0]['type'],
               filepath:rows[0]['attachment'],
               message_id:rows[0]['message_id'],
               user_id:senderId,
               group_id:rows[0]['group_id'],
               group_name:rows[0]['group_name'],
               index:index
            }

         callback(message);
         }
        }else{
          console.log("errror in sql syntax "+err);
        }
     });
}

exports.getGroupChats = getGroupChats;
exports.getCurrentChatRoom = getCurrentChatRoom;
exports.getOwnChatRoom = getOwnChatRoom;
exports.getChats = getChats;
