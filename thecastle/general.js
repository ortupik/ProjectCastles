var getUsername = function(connection,userDetails,callback){

     var queryUser = connection.query("SELECT * FROM  user  WHERE ? " ,userDetails,function(err, rows, fields) {
      if(!err){
          
           var info = {
             username:rows[0]['username'],
           }
           callback(info);
        
    }else{
        console.log("Error at "+err);
    }
    });
 }
exports.getUsername = getUsername;
