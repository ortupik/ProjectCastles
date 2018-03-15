
var mysql = require('mysql');
var contacts = require('./contacts');
var chats = require('./chats');
var chatMessages = require('./chatmessages');
var units = require('./units');
var viewTimetable = require('./view_timetable');
//var examTimetable = require('./exam_timetable');
var postCastle = require('./posts_castle');
var student = require('./student');
var hostel = require('./hostel')



/*var db_config = {
    host     : 'us-cdbr-iron-east-04.cleardb.net',
    user     : 'b002bf3f0d5b33',
    password : '9adb4021',
    database : 'heroku_45495343b6e2f33'
}*/

/*mysql -h us-cdbr-iron-east-04.cleardb.net -u b002bf3f0d5b33 -p heroku_45495343b6e2f33
 *-- imports db
 * source C:\Users\Chris\Desktop\dekut_castle.sql
 * -- chanes primary key and uto inrement
 * ALTER TABLE hostel CHANGE hostel_id hostel_id INT(11) AUTO_INCREMENT PRIMARY KEY;
 */
 
var db_config = {
      host     : '127.0.0.1',
      user     : 'root',
      password : 'chowder',
      database : 'dekut_castle',
 };

var connection;

    function handleDisconnect() {

      connection = mysql.createConnection(db_config); // Recreate the connection, since
                                                      // the old one cannot be reused.
      connection.connect(function(err) {              // The server is either down
        if(err) {                                     // or restarting (takes a while sometimes).
          console.log('error when connecting to db:', err);
          setTimeout(handleDisconnect, 2000); // We introduce a delay before attempting to reconnect,
        }  else{
          console.log("Successfully connected to Db");
        }                                   // to avoid a hot loop, and to allow our node script to
      });                                     // process asynchronous requests in the meantime.
                                              // If you're also serving http, display a 503 error.
      connection.on('error', function(err) {
        console.log('db error', err);
        if(err.code === 'PROTOCOL_CONNECTION_LOST') { // Connection to the MySQL server is usually
          handleDisconnect();                         // lost due to either server restart, or a
        } else {                                      // connnection idle timeout (the wait_timeout
          throw err;                                  // server variable configures this)
        }
      });
    }

    handleDisconnect();



 function checkUser(userData,callback){

   var query2 = connection.query('SELECT `user_id` FROM user ', function(err2,rows2, result2) {
      if(!err2){
         if(rows2.length > 300){
            console.log("Exceeded MAX Users LIMIT for Version 0.1");
            var details = {
               success:3
            }
            callback(details);    
            return;
          }else{
              
          
        var query = connection.query("SELECT `user`.`user_id`,`user`.`phoneNo`,`user_role`,`user`.`username`,`user_details`.`status`,`user_details`.`profile_photo`,`user_details`.`user_role` from `user`  LEFT JOIN `user_details` ON `user`.`user_id` = `user_details`.`user_id` WHERE ? AND ?  ",[{password:userData.password},{phoneNo:userData.phoneNo}],function(err, rows, fields) {

          if (!err){
             // console.log(query.sql)
              if(rows.length > 0){
                  console.log("user already  exist");
                  if(rows[0]['status'] === null && rows[0]['profile_photo'] === null){
                       var userDetail = {
                          user_id:rows[0]['user_id'],
                          username:rows[0]['username'],
                          phoneNo:rows[0]['phoneNo'],
                          user_role:rows[0]['user_role'],
                          success:2
                        }
                        console.log(userDetail);
                        callback(userDetail);
                  }else{
                      var userDetail = {
                      user_id:rows[0]['user_id'],
                      username:rows[0]['username'],
                      user_role:rows[0]['user_role'],
                      success:1
                    }
                     console.log(userDetail);
                     callback(userDetail);
                  }

              }else{
                  console.log("user Does NOT exist");
                  var userDetail = {
                     success:0
                  }
                  callback(userDetail);    
              }

          }else{
               console.log("errror in sql syntax<checkUser> "+err);
                 var userDetail = {
                     success:0
                  }
                  callback(userDetail);
          }
          });
          console.log(query.sql);
          }
      }else{
          callback(0);
          console.log(err2);
      }
  });
}

var registerUser = function (data,callback){
 
  
  var query = connection.query('INSERT INTO user SET ?', data, function(err, result) {
  // Neat!
      if (!err){
             var response = {
                success:1,
                user_id:result.insertId
             };
            
          //creating own chat room
          var chatData = {
              user1_id:result.insertId,
              user2_id:result.insertId
          }
       var queryInsert = connection.query('INSERT INTO chat_room SET ?', chatData, function(err2, result2) {
           if (!err2) {
             console.log("Successfully created own chat iD for");
           }else{
             console.log(err2);
           }
        });
        //automatically placing you in a group -- Wrong
        var groupData = {
            user_id:result.insertId,
            group_id:"1",
            role:"user"
        }
        var queryInsertGroup = connection.query('INSERT INTO group_chat_room SET ?', groupData, function(err2, result2) {
           if (!err2) {
             console.log("Successfully inserted you to group 1 automatically");
             callback(response) ;
           }else{
             console.log(err2);
           }
        });
            

      }else{
          callback(0) ;
        console.log("errror "+err);
      }
//  console.log("result "+result.insertId);
     console.log(query.sql);
  });
}


//handle profile
var registerUserProfile = function (data,callback){

  var query = connection.query('UPDATE user SET ? WHERE ?',[{username:data.username},{user_id:data.user_id}], function(err, result) {
   console.log(query.sql);
   
      if (!err){
          var data2 = {
               status:data.status,
               profile_photo:data.profile_photo,
               user_id:data.user_id
          };
          var query2 = connection.query('INSERT INTO user_details SET ?', data2, function(err2, result2) {

               if (!err2){

                var response = {
                    success:1,
                    user_id:data.user_id
                };

                 callback(response) ;
 
               console.log("user_id for user "+result.insertId);
               }else{
                var response = {
                    success:0,
                    user_id:null
                };
                callback(response) ;
               console.log("errror "+err2);
               }
                    console.log(query2.sql);

          });


      }else{
          callback(0) ;
        console.log("errror "+err);
      }
//  console.log("result "+result.insertId);
     console.log(query.sql);
  });
}
//edit profile
var editProfile = function (data,callback){

  var query = connection.query('UPDATE user SET ? WHERE ?',[{username:data.username},{user_id:data.user_id}], function(err, result) {
   console.log(query.sql);
   
      if (!err){
          var data2 = {
               status:data.status,
               profile_photo:data.profile_photo
          };
          var query2 = connection.query('UPDATE user_details SET ? WHERE ? ',[data2,{user_id:data.user_id}] , function(err2, result2) {
              console.log(query2.sql);
               if (!err2){

                var response = {
                    success:1,
                    user_id:data.user_id
                };

                 callback(response) ;
 
               }else{ 
                   console.log("errror "+err2);
                var response = {
                    success:0,
                    user_id:null
                };
                callback(response) ;
             
               }
                   

          });


      }else{
          callback(0) ;
        console.log("errror "+err);
      }
//  console.log("result "+result.insertId);
     console.log(query.sql);
  });
}
//Loggigin IN
var checkLogin = function (data,callback){

  var query = connection.query("SELECT * from `user`,`user_details` WHERE (user.user_id = user_details.user_id) AND ? AND ? ",[{'user.user_id':data.user_id},{'user.phoneNo':data.phoneNo}],function(err, rows, fields) {
   console.log(query.sql);

    if (!err){
        if(rows.length > 0){
              
             var info = {
                username:rows[0]['username'],
                phoneNo:rows[0]['phoneNo'],
                profile_photo:rows[0]["profile_photo"],
                status:rows[0]["status"]
            }
            
           getAllChatRooms(data.user_id,function(chatResult){
              if(chatResult.success == 1){
                 var chatRooms= {
                     success:1,
                     userDetails:info,
                    chatRooms:chatResult.chatRooms
                };

                  callback(chatRooms);
              }else{
                  var chatRooms= {
                      userDetails:info,
                      success:2
                  };
                 callback(chatRooms);
              }
           });
        }else{
            console.log("Invalid Credentials rows ");
                 var chatRooms= {
                  success:0
                };
              callback(chatRooms);
        }
    }else{
        console.log("error in check login "+err);
    }

 
    });
}
   

function getAllChatRooms(user_id,callback){
 var query2 = connection.query("SELECT * FROM chat_room WHERE ? OR ? " ,[{user1_id:user_id},{user2_id:user_id}],function(err, rows, fields) {
  //  console.log(query2.sql);

    if (!err){

         if(rows.length > 0){
          
                var chatRooms= {
                    success:1,
                    chatRooms:rows,
                };
               console.log("rows "+rows.length);
              callback(  chatRooms) ;
      
      }else{
        console.log("O rows for chats Rooms during login");
             callback({success:0 });
     
       }
    }else{
         console.log("errror in sql syntax "+err);
           var chatRooms= {
             success:0
          };
         callback(chatRooms) ;
    }
});
}
function getAllGroupChatRooms(user_id,callback){
 var query = connection.query("SELECT * FROM group_chat_room WHERE ? " ,{user_id:user_id},function(err, rows, fields) {
    if (!err){
      if(rows.length > 0){

        var groupRooms= {
             success:1,
             groupRooms:rows
        };
        console.log("group rows "+rows.length);
        callback(groupRooms);
      }else{
        console.log("O rows for group chats Rooms during login");
         callback({success:0}) ;
      }
    }else{
         console.log("errror in sql syntax "+err);
         callback({success:0}) ;
    }
});
}
 function checkLecturerDetails(userData,callback){

   var query2 = connection.query('SELECT * FROM `lecturer`  WHERE ? ',[userData], function(err2,rows2, result2) {
      if(!err2){
         if(rows2.length > 1){
            console.log("Lecturer");
            var details = {
               lecturer_id:rows2[0]["lecturer_id"]
            }
            callback({success:1,data:details});    
          }else{
               callback({success:0});  
          }
      }
  });
 }

var getContacts = function(user_id,callback){
     contacts.getContacts(connection,user_id,function(result) {
       callback(result);
 });
}
var updateLastSeen = function(user_id,callback){
     contacts.updateLastSeen(connection,user_id,function(result) {
       callback(result);
 });
}
  var getUnits = function(user_id,callback){
      units.getUnits(connection,user_id,function(result) {
          callback(result);
      });
  }
   var insertUnits = function(data,callback){
      units.insertUnits(connection,data,function(result) {
          callback(result);
      });
  }
  var deleteUnit = function(data,callback){
      units.deleteUnit(connection,data,function(result) {
          callback(result);
      });
  }
  var updateUnits = function(data,callback){
      units.updateUnits(connection,data,function(result) {
          callback(result);
      });
  }
  var insertLec = function(data,callback){
      units.insertLec(connection,data,function(result) {
          callback(result);
      });
  }

    var getGroupUnits = function(user_id,callback){
      units.getGroupUnits(connection,user_id,function(result) {
          callback(result);
      });
  }
  
   var getCurrentGroups = function(data,callback){
      units.getCurrentGroups(connection,data,function(result) {
          callback(result);
      });
  }
  var getLecUnits = function(data,callback){
      units.getLecUnits(connection,data,function(result) {
          callback(result);
      });
  }
var getChats = function(chat_id,callback){
       chats.getChats(connection,chat_id,function(result) {
         callback(result);
  });
}
var getGroupChats = function(userDetails,callback){
       chats.getGroupChats(connection,userDetails,function(result) {
         callback(result);
  });
}
var getCurrentChatRoom = function(user_id,opponentId,callback){
       chats.getCurrentChatRoom(connection,user_id,opponentId,function(result) {
         callback(result);
  });
}
var getOwnChatRoom = function(user_id,callback){
       chats.getOwnChatRoom(connection,user_id,function(result) {
         callback(result);
  });
}
var insertChatMessage = function(chatMessageDetails,callback){
       chatMessages.insertChatMessage(connection,chatMessageDetails,function(result) {
         callback(result);
  });
}
var updateDeliveryStatus = function(message_id,callback){
       chatMessages.updateDeliveryStatus(connection,message_id,function(result) {
         callback(result);
  });
}
var insertGroupChatMessage = function(chatMessageDetails,callback){
       chatMessages.insertGroupChatMessage(connection,chatMessageDetails,function(result) {
         callback(result);
  });
}
var getChatMessages = function(chatDetails,callback){
       chatMessages.getChatMessages(connection,chatDetails,function(result) {
         callback(result);
  });
}
var getGroupChatMessages = function(chatDetails,callback){
       chatMessages.getGroupChatMessages(connection,chatDetails,function(result) {
         callback(result);
  });
}
var getTimetable = function(user_id,callback){
     viewTimetable.getTimetable(connection,user_id,function(result) {
         callback(result);
  });
}
var getTimetables = function(data,callback){
     viewTimetable.getTimetables(connection,data,function(result) {
         callback(result);
  });
}
var getGroupTimetable = function(data,callback){
     viewTimetable.getGroupTimetable(connection,data,function(result) {
         callback(result);
  });
}

var insertTimetable = function(details,callback){
     viewTimetable.insertTimetable(connection,details,function(result) {
         callback(result);
  });
}
var newTimetable = function(details,callback){
     viewTimetable.newTimetable(connection,details,function(result) {
         callback(result);
  });
}
var deleteTimetable = function(details,callback){
     viewTimetable.deleteTimetable(connection,details,function(result) {
         callback(result);
  });
}
var updateTimetableStatus = function(details,callback){
     viewTimetable.updateTimetableStatus(connection,details,function(result) {
         callback(result);
  });
}
var insertExamTimetable = function(details,callback){
     examTimetable.insertExamTimetable(connection,details,function(result) {
         callback(result);
  });
}

var getExamTimetable = function(details,callback){
     examTimetable.getExamTimetable(connection,details,function(result) {
         callback(result);
  });
}
var getRoomTimetableInfo = function(room,callback){
     viewTimetable.getRoomTimetableInfo(connection,room,function(result) {
         callback(result);
  });
}
var getAllClassRooms = function(time,callback){
     viewTimetable.getAllClassRooms(connection,time,function(result) {
         callback(result);
  });
}
var getCastlePosts = function(castleId,callback){
     postCastle.getCastlePosts(connection,castleId,function(result) {
       callback(result);
 });
}
var registerStudent = function(studentDetails,callback){
     student.registerStudent(connection,studentDetails,function(result) {
       callback(result);
 });
}
var checkStudentRegistration = function(Details,callback){
     student.checkStudentRegistration(connection,Details,function(result) {
       callback(result);
 });
}
var getDekutDetails = function(studentDetails,callback){
     student.getDekutDetails(connection,studentDetails,function(result) {
       callback(result);
 });
}
var getHostels = function(details,callback){
     hostel.getHostels(connection,details,function(result) {
       callback(result);
 });
}
var getHostelRooms = function(details,callback){
     hostel.getHostelRooms(connection,details,function(result) {
       callback(result);
 });
}
function disconnect(){
   connection.end();
}

exports.checkLecturerDetails = checkLecturerDetails;
exports.getHostelRooms = getHostelRooms;
exports.getHostels = getHostels;
exports.checkLogin = checkLogin;
exports.checkUser = checkUser;
exports.registerUser = registerUser;
exports.registerUserProfile = registerUserProfile;
exports.editProfile = editProfile;
exports.getContacts = getContacts;
exports.updateLastSeen = updateLastSeen;
exports.getUnits = getUnits;
exports.getGroupUnits = getGroupUnits;
exports.getLecUnits = getLecUnits;
exports.getCurrentGroups = getCurrentGroups;
exports.getChats = getChats;
exports.getGroupChats = getGroupChats;
exports.getCurrentChatRoom = getCurrentChatRoom;
exports.getOwnChatRoom = getOwnChatRoom;
exports.getAllGroupChatRooms = getAllGroupChatRooms;
exports.insertChatMessage = insertChatMessage;
exports.insertGroupChatMessage = insertGroupChatMessage;
exports.getChatMessages = getChatMessages;
exports.getGroupChatMessages = getGroupChatMessages;
exports.getTimetable = getTimetable;
exports.getTimetables = getTimetables;
exports.insertTimetable = insertTimetable;
exports.newTimetable = newTimetable;
exports.updateTimetableStatus = updateTimetableStatus;
exports.insertExamTimetable = insertExamTimetable;
exports.getExamTimetable = getExamTimetable; 
exports.getRoomTimetableInfo = getRoomTimetableInfo;
exports.getAllClassRooms = getAllClassRooms;
exports.getCastlePosts = getCastlePosts;
exports.updateDeliveryStatus = updateDeliveryStatus;
exports.registerStudent = registerStudent;
exports.checkStudentRegistration = checkStudentRegistration;
exports.getDekutDetails = getDekutDetails;
exports.getGroupTimetable = getGroupTimetable;
exports.deleteTimetable = deleteTimetable;
exports.insertUnits = insertUnits;
exports.deleteUnit = deleteUnit;
exports.updateUnits = updateUnits;
 exports.insertLec = insertLec;

