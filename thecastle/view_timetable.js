var newTimetable = function(connection,data,callback){
    var query = connection.query('INSERT INTO `timetables` SET ? ',data, function(err, result) {
       console.log(query.sql); 
      if (!err){
             var response = {
                success:1,
                message:"Inserted Successfully !!"
             };
             callback(response);
      }else{
           var response = {
                success:0,
                message:"An errored occurred while inserting!!"
             };
             callback(response);
      }
     });
}
var updateTimetableStatus = function(connection,data,callback){
    var query = connection.query('UPDATE `timetables` SET `status` = ? WHERE `dept` = ? AND `group` = ?',[data.status,data.dept,data.group], function(err, result) {
       console.log(query.sql); 
      if (!err){
             var response = {
                success:1,
                message:"Updated Successfully !!"
             };
             callback(response);
      }else{
           var response = {
                success:0,
                message:"An errored occurred while updating !!"
             };
             callback(response);
      }
     });
}
var deleteTimetable = function(connection,data,callback){
    var query = connection.query('DELETE FROM `timetables` WHERE `dept` = ? AND `group` = ?',[data.dept,data.group], function(err, result) {
       console.log(query.sql); 
      if (!err){
           var query2 = connection.query('DELETE FROM `alloc_time` WHERE `dept` = ? AND `class_group` = ?',[data.dept,data.group], function(err2, result) {
           console.log(query2.sql); 
           if (!err2){
              var response = {
                success:1,
                message:"Deleted Successfully !!"
              };
              callback(response);
            }else{
                 var response = {
                success:0,
                message:"An errored occurred while deleting !!"
             };
             callback(response);
            }
        });
             
      }else{
           var response = {
                success:0,
                message:"An errored occurred while deleting !!"
             };
             callback(response);
      }
     });
}

var insertTimetable = function(connection,details,callback){
    
        var insertionDetails = {
            day:details.day,
            s_time:details.s_time,
            f_time:details.f_time,
            unit_code:details.unit_code,
            intake:details.intake,
            room:details.room,
            class_group:details.class_group,
            dept:details.dept,
            hours:details.hours
         }
         
         var unitInfo = {
             unit_code:details.unit_code,
             class_group:details.class_group,
             dept:details.dept
         }
         
      getLecturerId(connection,unitInfo,function(lecturerId){ 
           insertionDetails.lecturer_id = lecturerId.lecturer_id;
           console.log(lecturerId.lecturer_id)
           
          var query = connection.query('INSERT INTO  `alloc_time` SET ? ',insertionDetails,function(err, rows, fields) {
               console.log(query.sql);
              if(!err){
                   console.log("inserted details for ");
                   console.log(unitInfo);
                   callback({message:"inserted successfully"});
               }else{
                    callback({success:0},{message:"inserted failure"});
                   console.log(err);
               }
          });
      });

    

}

var getTimetable = function(connection,userId,callback){

  var response = new Array();

  getStudentInfo(connection,userId,function(student_info){
   
  var query = connection.query('SELECT *  from alloc_time ,`lecturer` WHERE `alloc_time`.`lecturer_id` = `lecturer`.`lecturer_id` AND ? AND ? ',[student_info.dept,student_info.class_group],function(err, rows, fields) {

    if (!err){

          var monArray = [];
          var tueArray = [];
          var wedArray = [];
          var thurArray = [];
          var friArray = [];
          
        for(var i= 0; i < rows.length; i++){

               var viewcolumn = [];

                var day = rows[i]['day'];
                var s_time = rows[i]['s_time'];     
                var f_time = rows[i]['f_time'];
                var unit_code = rows[i]['unit_code'];
                var intake = rows[i]['intake'];
                var room = rows[i]['room'];
                var class_group = rows[i]['class_group'];
                var dept = rows[i]['dept'];
                var hours = rows[i]['hours'];
                var username = (rows[i]['fname']).substring(0,1)+'.'+rows[i]['lname'];
              
                  
                    var viewcolumn = {
                      day:day,
                      s_time:s_time,
                      f_time:f_time,
                      unit_code:unit_code,
                      intake:intake,
                      room:room,
                      class_group:class_group,
                      dept:dept,
                      hours:hours,
                      username:username
                  }
                  
                   if(day == 'MON'){
                      monArray.push(viewcolumn);
                   }else if(day == 'TUE'){
                      tueArray.push(viewcolumn);
                   }else if(day == 'WED'){
                      wedArray.push(viewcolumn);
                   }else if(day == 'THUR'){
                       thurArray.push(viewcolumn);
                   }else if(day == 'FRI'){
                      friArray.push(viewcolumn);
                   }
            }
            
            response.push(monArray);
            response.push(tueArray);
            response.push(wedArray);
            response.push(thurArray);
            response.push(friArray);
            
          callback(response ) ;
           console.log(response);
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
  });
}
var getGroupTimetable = function(connection,data,callback){

  var response = new Array();

   
  var query = connection.query('SELECT *  from alloc_time ,`lecturer` WHERE `alloc_time`.`lecturer_id` = `lecturer`.`lecturer_id` AND `dept` = ? AND  `class_group` = ? ',[data.dept,data.class_group],function(err, rows, fields) {
  console.log(query.sql);
  
    if (!err){

          var monArray = [];
          var tueArray = [];
          var wedArray = [];
          var thurArray = [];
          var friArray = [];
          
        for(var i= 0; i < rows.length; i++){

               var viewcolumn = [];

                var day = rows[i]['day'];
                var s_time = rows[i]['s_time'];     
                var f_time = rows[i]['f_time'];
                var unit_code = rows[i]['unit_code'];
                var intake = rows[i]['intake'];
                var room = rows[i]['room'];
                var class_group = rows[i]['class_group'];
                var dept = rows[i]['dept'];
                var hours = rows[i]['hours'];
                var username = (rows[i]['fname']).substring(0,1)+'.'+rows[i]['lname'];
              
                  
                    var viewcolumn = {
                      day:day,
                      s_time:s_time,
                      f_time:f_time,
                      unit_code:unit_code,
                      intake:intake,
                      room:room,
                      class_group:class_group,
                      dept:dept,
                      hours:hours,
                      username:username,
                      created_at:rows[i]['created_at']
                  }
                //  console.log(viewcolumn)
                  
                   if(day == 'MON'){
                      monArray.push(viewcolumn);
                   }else if(day == 'TUE'){
                      tueArray.push(viewcolumn);
                   }else if(day == 'WED'){
                      wedArray.push(viewcolumn);
                   }else if(day == 'THUR'){
                       thurArray.push(viewcolumn);
                   }else if(day == 'FRI'){
                      friArray.push(viewcolumn);
                   }
            }
            
            response.push(monArray);
            response.push(tueArray);
            response.push(wedArray);
            response.push(thurArray);
            response.push(friArray);
            
          callback(response ) ;
           console.log(response);
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
}
var getRoomTimetableInfo = function(connection,room,callback){

  var response = new Array();

  var query = connection.query('SELECT *  from alloc_time ,`lecturer` WHERE `alloc_time`.`lecturer_id` = `lecturer`.`lecturer_id` AND ? ',[room],function(err, rows, fields) {

    if (!err){

          var monArray = [];
          var tueArray = [];
          var wedArray = [];
          var thurArray = [];
          var friArray = [];
          
        for(var i= 0; i < rows.length; i++){

               var viewcolumn = [];

                var day = rows[i]['day'];
                var s_time = rows[i]['s_time'];     
                var f_time = rows[i]['f_time'];
                var unit_code = rows[i]['unit_code'];
                var intake = rows[i]['intake'];
                var room = rows[i]['room'];
                var class_group = rows[i]['class_group'];
                var dept = rows[i]['dept'];
                var hours = rows[i]['hours'];
                var username = (rows[i]['fname']).substring(0,1)+'.'+rows[i]['lname'];
              
                  
                    var viewcolumn = {
                      day:day,
                      s_time:s_time,
                      f_time:f_time,
                      unit_code:unit_code,
                      intake:intake,
                      room:room,
                      class_group:class_group,
                      dept:dept,
                      hours:hours,
                      username:username
                  }
                  
                   if(day == 'MON'){
                      monArray.push(viewcolumn);
                   }else if(day == 'TUE'){
                      tueArray.push(viewcolumn);
                   }else if(day == 'WED'){
                      wedArray.push(viewcolumn);
                   }else if(day == 'THUR'){
                       thurArray.push(viewcolumn);
                   }else if(day == 'FRI'){
                      friArray.push(viewcolumn);
                   }
            }
            
            response.push(monArray);
            response.push(tueArray);
            response.push(wedArray);
            response.push(thurArray);
            response.push(friArray);
            
          callback(response ) ;
           //console.log(response);
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
}

//gets All Classrooms
var getAllClassRooms = function(connection, time, callback) {
    var classesArray = [];

  var query = connection.query("SELECT `class_rooms`.`room`,`alloc_id`,`location`,`dept`,`class_group`,`unit_code` FROM `class_rooms` LEFT JOIN `alloc_time` ON `class_rooms`.`room` = `alloc_time`.`room` AND `s_time` <= ? AND `f_time` > ? AND `day` = ? ORDER BY `class_rooms`.`room` ASC  ",[time.currentTime,time.currentTime,time.day],function(err, rows, fields) {
        console.log(query.sql);

        if (rows.length > 0) {
           if (!err) {
                
            for(var i= 0; i < rows.length; i++){
                 var room = rows[i]['room'];
                 var location = rows[i]['location'];
                 var alloc_id = rows[i]['alloc_id'];
                 var unit_code = rows[i]['unit_code'];
                 
                 if(alloc_id === null){
                     var roomDetails = {
                         room:room,
                         classNow:0,
                         location:location
                     }
                      classesArray.push(roomDetails);
                 }else{
                     
                     var dept = rows[i]['dept'];
                     var group = rows[i]['class_group'];
                     
                     var roomDetails = {
                         room:room,
                         classNow:1,
                         location:location,
                         dept:dept,
                         group:group,
                         unit_code:unit_code
                     }
                     classesArray.push(roomDetails);
                                            
                 }
                 
              } 
              console.log(classesArray);
              callback(classesArray);
            } else {
                console.log("error in sql syntax -> " + err);
            }
        }
    
    });
            
}

function getStudentInfo(connection,userId,callback){
    var query = connection.query('SELECT *  from `student` WHERE ? ',userId,function(err, rows, fields) {
        if(!err){
            
            //look for a better way chris
            var dept = {
                dept:rows[0]["dept"]
            }
            var class_group = {
                class_group:rows[0]["class_group"]
            }
            
            var info = {
               dept:dept,
               class_group:class_group
            }
          
         callback(info);
        }
        console.log(query.sql);
        
    });
}
function getLecturerId(connection,unitInfo,callback){
    var query = connection.query('SELECT *  from `units` WHERE `unit_code` = ?  AND `class_group` = ? AND `dept` = ? ',[unitInfo.unit_code,unitInfo.class_group,unitInfo.dept],function(err, rows, fields) {
          console.log(query.sql);

        if(!err){
            if (rows.length > 0) {
                 callback({lecturer_id:rows[0]['lecturer_id']});
            }else{
                 callback({lecturer_id:1000});
            }
          
        }else{
            console.log(err);
        }
     
        
    });
}
var getTimetables = function(connection,userId,callback){

    var timeTTArray = new Array();

  var query = connection.query('SELECT * FROM `timetables` ;',function(err, rows, fields) {

    if (!err){

        for(var i= 0; i < rows.length; i++){

           var info = {
             academic_year:rows[i]['academic_year'],
             intake:rows[i]['intake'],
             dept:rows[i]["dept"],
             group:rows[i]["group"],
             status:rows[i]["status"],
             created_at:rows[i]["created_at"],
             updated_at:rows[i]["updated_at"]
           }
           timeTTArray.push(info);
        }
          callback(timeTTArray ) ;
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
}

exports.getGroupTimetable = getGroupTimetable;
exports.updateTimetableStatus = updateTimetableStatus;
exports.getTimetable = getTimetable;
exports.getTimetables = getTimetables;
exports.deleteTimetable = deleteTimetable;
exports.insertTimetable = insertTimetable;
exports.newTimetable = newTimetable;
exports.getRoomTimetableInfo = getRoomTimetableInfo;
exports.getAllClassRooms = getAllClassRooms;
