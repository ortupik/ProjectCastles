function get_group_chat_rooms(connection,user_id,callback){
    var groupsArray = new Array();

  var main_query = connection.query("SELECT *  from `group_chat_room` WHERE ? ",{user_id:user_id},function(err1, rows1, fields) {
     
      if (!err1){
        var no_of_rows = rows1.length;
          
        if(no_of_rows > 0){
           
        for(var j = 0; j < no_of_rows; j++){
               
             var group_id = rows1[j]['group_id'];
             var role = rows1[j]['role'];
                       
            getGroupDetails(connection,user_id,group_id,j,function(result1){
                
                 groupsArray.push(result1);
                 
                 if(result1.index == no_of_rows-1 ){
                        callback(groupsArray ) ;
                 }
            });
        }
         // console.log(main_query.sql);
      }else{
          callback(-1);
      }
    }else{
         console.log("errror in sql syntax "+err1);
    }
   
  });    
}

//get the contacts in the group
function getGroupContacts(connection,my_user_id,group_id,callback){
    
  var groupContactsArray = new Array();

  var main_query = connection.query("SELECT *  from `group_chat_room` WHERE ? ",{group_id:group_id},function(err1, rows1, fields) {
     
      if (!err1){
        var no_of_rows = rows1.length;
          
        if(no_of_rows > 0){
           
        for(var j = 0; j < no_of_rows; j++){
               
             var user_id = rows1[j]['user_id'];
             var role = rows1[j]['role'];
             
             var userDetails = {
                 user_id:user_id,
                 role:role
             }
                       
            getUserDetails(connection,my_user_id,userDetails,j,function(result){
                 groupContactsArray.push(result);
                 
                if(result.index == no_of_rows-1 ){
                    if(no_of_rows == 1){
                        groupContactsArray[0].username = 'ONLY ME';
                    }
                    callback(groupContactsArray) ;
               }
            });
        }
        //  console.log(main_query.sql);
      }else{
          callback(-1);
      }
    }else{
         console.log("errror in sql syntax "+err1);
    }
   
  });       
}
//get user details
var getUserDetails = function(connection,my_user_id,userDetails,index,callback){

  var query = connection.query("SELECT *  from `user`,`user_details` WHERE (user.user_id = user_details.user_id) AND ? ",{'user.user_id':userDetails.user_id},function(err, rows, fields) {

    if (!err){
        if(rows.length > 0){
          var info = {
             role:userDetails.role,
             username:rows[0]['username'],
             user_id:rows[0]['user_id'],
             phoneNo:rows[0]['phoneNo'],
             profile_photo:rows[0]["profile_photo"],
             status:rows[0]["status"],
             last_seen:rows[0]['last_seen'],
             index:index
           }  
           
           if(userDetails.user_id == my_user_id){
               info.username = 'Me';
           }
           callback(info);
       }

    }else{
         console.log("errror in sql syntax "+err);
    }
    });
   // console.log(query.sql);
}

function getGroupDetails(connection,user_id,group_id,index,callback){
 
    var query = connection.query("SELECT *  from `groups`,`group_details` WHERE  groups.group_id = group_details.group_id AND ? ",{'groups.group_id':group_id},function(err, rows, fields) {

    if (!err){
                 
           var group_info = {
             group_id:group_id,
             group_name:rows[0]['group_name'],
             created_at:rows[0]['created_at'],
             created_by:rows[0]['created_by'],
             status:rows[0]["status"],
             profile_photo:rows[0]["profile_photo"],
             index:index
           }
           
          //console.log(query.sql);
          
          getGroupContacts(connection,user_id,group_id,function(result){
                group_info.contacts = result;
                callback(group_info);
          });
          
        
          
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
  
}
var getContacts = function(connection,details,callback){

  var contactsArray = new Array();

  var query = connection.query("SELECT *  from `user`,`user_details`,`student` WHERE (user.user_id = user_details.user_id) AND (user.user_id = student.user_id) AND (user.user_id!='"+details.user_id+"') AND ? AND ? ",[{class_group:details.class_group},{dept:details.dept}],function(err, rows, fields) {
     console.log(query.sql);
    if (!err){

        for(var i= 0; i < rows.length; i++){

          
           var info = {
             username:rows[i]['username'],
             user_id:rows[i]['user_id'],
             phoneNo:rows[i]['phoneNo'],
             profile_photo:rows[i]["profile_photo"],
             status:rows[i]["status"],
             last_seen:rows[i]['last_seen']
           }

           contactsArray.push(info);
        }
          get_group_chat_rooms(connection,details.user_id,function (result){
              var allContacts = {
              groups:result,
              contacts:contactsArray
            }
            
          callback(allContacts ) ;
          });
          
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    //console.log(query.sql);
}
var updateLastSeen = function(connection,user_id,callback){

  var query = connection.query("UPDATE `user_details` set last_seen = CURRENT_TIMESTAMP WHERE ? ",user_id,function(err, rows, fields) {
    if (!err){
        
      var queryLastSeen = connection.query("SELECT *  from `user_details` WHERE ? ",user_id,function(err1, rows1, fields) {

      if (!err1){
           var info = {
             last_seen:rows1[0]['last_seen']
           }
          callback(info) ;
     }else{
         console.log("errror in sql syntax "+err1);
     }
    });
   //  console.log(queryLastSeen.sql);
      
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    //console.log(query.sql);
}

exports.updateLastSeen = updateLastSeen;
exports.getContacts = getContacts;
