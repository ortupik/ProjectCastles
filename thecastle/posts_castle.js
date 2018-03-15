var  general = require('./general');

var getCastlePosts = function(connection,castleDetails,callback){

     var queryPosts = connection.query("SELECT * FROM  post  WHERE ? ORDER BY `created_at` ASC" ,castleDetails,function(err, rows, fields) {
        console.log(queryPosts.sql);
      if(!err){
          
          var postArray = [];
          
       for(var j = 0; j < rows.length; j++){
             
            var post_id = rows[j]['post_id'];
            var post = rows[j]['post'];

              var postDetails = {
                 post_id:post_id
              };
            // console.log(castleDetails);

          

          var query = connection.query("SELECT * FROM reply WHERE ? ORDER BY `created_at` ASC ",postDetails,function(err1, rows1, fields) {

              var replyArray = [];
              
              console.log("for post_id "+post_id);
               console.log(query.sql);
              if (!err1){
                  if(rows1.length > 0){ 
                    for(var i = 0; i < rows1.length; i++){
                      var senderId = rows1[i]['user_id'];
                       var userDetails = {
                           user_id:senderId
                       }
                       //var username = general.getUsername(connection,userDetails,function(result){
                            //   console.log(rows1[i]['reply']);

                           var reply ={
                               username:"result",
                               reply:rows1[i]['reply'],
                               created_at:rows1[i]['created_at']
                             }
                           replyArray.push(reply);
                          // console.log("reply array ");
                           //console.log(replyArray);
                          
                           
                     // });
                    }
                      
                        var postObj = {
                            post:post,
                            reply:replyArray
                        }
                        postArray.push(postObj);
                   
                }else{
                    console.log("O rows in castle post");
                }
              }else{
                console.log("errror in sql syntax "+err1);
              }
           });
       }
          callback(postArray);

      
      }else{
         console.log("Error "+err);
      }
       });
  }

exports.getCastlePosts = getCastlePosts;