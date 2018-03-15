
var getHostels = function(connection, details, callback){
    
var query = connection.query("SELECT  * FROM `hostel` ", function (err, rows, fields) {
 console.log(query.sql);

  var hostelArray = [];
  
  if (!err) {
      
    if (rows.length > 0) {
        
         for(var i = 0; i < rows.length; i++){
             
             var hostelName = rows[i]['hostel_name'];
             var hostelDistance = rows[i]['hostel_distance'];
             var ratings = rows[i]['hostel_ratings'];
             
             var hostelDetails = {             
                 hostelName:hostelName,
                 hostelDistance:hostelDistance,
                 ratings:ratings
             }
             
             hostelArray.push(hostelDetails);
         }
         
         callback(hostelArray);
         
     }else {
         console.log("O rows for getHostels ");
     }
  }else{
      console.log(err);
  }
 });
 }
 var getHostelRooms = function(connection, details, callback){
    
var query = connection.query("SELECT * FROM `hostel_room_def`,`hostel_room` WHERE `hostel_room_def`.`room_def_id` = `hostel_room`.`room_def_id` AND  `hostel_room`.`hostel_id` = ?  ",[details.hostel_id], function (err, rows, fields) {
 console.log(query.sql);

  var roomsArray = [];
  
  if (!err) {
      
    if (rows.length > 0) {
        
         for(var i = 0; i < rows.length; i++){
             
             var roomPrice = rows[i]['price'];
             var roomType = rows[i]['type'];
             
             var roomDetails = {             
                 roomPrice:roomPrice,
                 roomType:roomType,
             }
            
             roomsArray.push(roomDetails);
         }
                  
         //get Images
        var query2 = connection.query("SELECT  * FROM `hostel_room_def_media`,`hostel_room` WHERE `hostel_room_def_media`.`room_def_id` = `hostel_room`.`room_def_id` AND  ? ",[{hostel_id:details.hostel_id}], function (err2, rows, fields) {
         console.log(query2.sql);
         
         var detailsArray = [];

         if (!err2) {
          if (rows.length > 0) {

               for(var i = 0; i < rows.length; i++){

                   var img_path = rows[i]['img_path'];
                   var description = rows[i]['description'];

                   var hostelDetails = {             
                       img_path:img_path,
                       description:description,
                   }

                   detailsArray.push(hostelDetails);
               }

               var hostelinfo = {
                   detailsArray:detailsArray,
                   roomsArray:roomsArray
               }
               callback(hostelinfo);

           }else {
               console.log("O rows for getHostels ");
           }
        }else{
            console.log(err);
        }
       });
         
     }else {
         console.log("O rows for getHostelRooms ");
     }
  }else{
      console.log(err);
  }
 });
 } 
exports.getHostels = getHostels;
exports.getHostelRooms = getHostelRooms;

