//creating the poll room
var createPollRoom = function(connection,poll,callback){

  var pollDetails = {
      user_id:poll.user_id,
      group_id:poll.group_id,
      topic:poll.topic,
      attachment:poll.attachment,
      allow_comment:poll.allow_comment,
      expiry:poll.expiry
  };
  
   var query2 = connection.query("SELECT `vote_closed` FROM `poll` WHERE `vote_closed` = 0 " ,function(err2, rows2, fields2) {
         
        if(!err2){
         if(rows2.length > 0){
             
             var query = connection.query('INSERT INTO poll SET ?', pollDetails, function(err, result) {
                if(!err){
                   callback({success:1});
                }else{
                    console.log(err);
                   callback({success:0}); 
                } 
             });
        }else{
            callback({success:0,message:"Vote closed !"});
        }
        }else{
            console.log(err2);
            callback({success:0,message:"sql error"});
        }
    });
 
      
}
//voting in poll room
var voting = function(connection,voteDetails,callback){

  var pollDetails = {
      user_id:voteDetails.user_id,
      poll_id:voteDetails.poll_id,
      vote:voteDetails.vote,
      attachment:voteDetails.attachment
  };
  
  if(hasVoted(connection,{ user_id:voteDetails.user_id,poll_id:voteDetails.poll_id}) === true){
      
      var query = connection.query('INSERT INTO poll_room SET ?', pollDetails, function(err, result) {
       if(!err){
          callback({success:1});
       }else{
           console.log(err);
           callback({success:0}); 
       } 
    }); 
  }
   
}
var getPollResult = function(connection,voteDetails,callback){
     
    var query = connection.query("SELECT `vote` FROM poll_room WHERE  vote = 'yes' AND ? " ,{poll_id:voteDetails.poll_id},function(err1, rows1, fields) {
        if(!err1){
          var yes_votes = rows1.length;
          var query2 = connection.query("SELECT `id` FROM `group_chat_room` WHERE ? " ,{group_id:voteDetails.group_id},function(err2, rows2, fields2) {
         
            if(!err2){
             if(rows2.length > 0){
                var population = rows2.length;
                var percentage_yes = yes_votes/population * 100;
                callback({success:1,percentage_yes:percentage_yes});
              }else{
                  callback({success:0}); 
              }
        }else{
                  console.log(err2);
                  callback({success:0}); 
              }
          });
      }else{
          console.log(err1);
          callback({success:0}); 
      }
   });
};

//when the time has expired
var onTimeExpiry = function(connection,voteDetails,callback){
    var query = connection.query('INSERT INTO poll SET vote_closed = 1', function(err, result) {
       if(!err){
          callback({success:1});
       }else{
           console.log(err);
           callback({success:0}); 
       } 
    }); 
}
//destroy poll
var onDeletePoll = function(connection,pollDetails,callback){
    var query = connection.query('DELETE `poll`,`poll_room` FROM `poll`,`poll_room` WHERE `poll`.`poll_id` = `poll_room`.`poll_id` AND ?',pollDetails, function(err, result) {
       if(!err){
          callback({success:1});
       }else{
           console.log(err);
           callback({success:0}); 
       } 
    }); 
}

//check if user has voted 
function hasVoted(connection,userDetails){
  //check if 
  var query = connection.query("SELECT `vote` FROM poll_room WHERE ? " ,[{ user_id:userDetails.user_id},{poll_id:userDetails.poll_id}],function(err1, rows1, fields) {
        if(!err1){
           if(rows1.length > 0){
             return true;
          }else{
              return false;
          }
      }
   });
}