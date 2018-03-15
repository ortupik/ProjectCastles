var getUnits = function(connection,userId,callback){

  getStudentInfo(connection,userId,function(student_info){
    var unitsArray = new Array();

  var query = connection.query('SELECT *  from `units`,`lecturer` WHERE `units`.`lecturer_id` = `lecturer`.`lecturer_id` AND ? AND ? ',[student_info.dept,student_info.class_group],function(err, rows, fields) {

    if (!err){

        for(var i= 0; i < rows.length; i++){

           var info = {
             unit_code:rows[i]['unit_code'],
             name:rows[i]['name'],
             dept:rows[i]["dept"],
             fname:rows[i]["fname"],
             lname:rows[i]["lname"],
             class_group:rows[i]["class_group"],
              noOfStudents:'40'
           }

           unitsArray.push(info);
        }
          callback(unitsArray ) ;
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
   });
}
var getGroupUnits = function(connection,data,callback){

    var unitsArray = new Array();

  var query = connection.query('SELECT *,IFNULL((SELECT `day` FROM `alloc_time` WHERE `alloc_time`.`unit_code` = `units`.`unit_code`  LIMIT 1),"NOT SET") AS `status` from `units`,`lecturer` WHERE `units`.`lecturer_id` = `lecturer`.`lecturer_id` AND `dept` = ? AND `class_group` = ?',[data.dept,data.class_group],function(err, rows, fields) {

    if (!err){

        for(var i= 0; i < rows.length; i++){

           var info = {
             unit_code:rows[i]['unit_code'],
             name:rows[i]['name'],
             dept:rows[i]["dept"],
             fname:rows[i]["fname"],
             lname:rows[i]["lname"],
             hours:rows[i]["hours"],
              status:rows[i]["status"],
              room:rows[i]["room"],
             class_group:rows[i]["class_group"],
              noOfStudents:'40'
           }

           unitsArray.push(info);
        }
          callback(unitsArray ) ;
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
}

var getCurrentGroups = function(connection,data,callback){

  var query = connection.query('SELECT *  from `current_groups`  WHERE `dept` = "CS" ',function(err, rows, fields) {

    if (!err){
           var info = {
              groups:rows[0]['groups'],
              updated_time:rows[0]['timestamp'],
           }
          callback(info) ;
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
}

var getLecUnits = function(connection,data,callback){

  getLecInfo(connection,data.user_id,function(info){
    var unitsArray = new Array();

    var SQL = 'SELECT *  from `units`,`lecturer`,`alloc_time` WHERE `units`.`lecturer_id` = `lecturer`.`lecturer_id` AND `lecturer`.`lecturer_id` = ? AND  `units`.`lecturer_id` = `alloc_time`.`lecturer_id`';
   if(data.type == "pending"){
            SQL = 'SELECT `units`.`unit_code`,`units`.`name`,`units`.`dept`,`units`.`lecturer_id`,`units`.`unit_code`,`units`.`hours`,`units`.`class_group` from `units` LEFT JOIN `alloc_time` ON `alloc_time`.`unit_code` = `units`.`unit_code` INNER JOIN `lecturer` ON `lecturer`.`lecturer_id` = `units`.`lecturer_id` AND `lecturer`.`lecturer_id` = 1 AND ISNULL(`alloc_time`.`unit_code`) ';
   }else if(data.type == "set"){
            SQL = 'SELECT *  from `units`,`lecturer`,`alloc_time` WHERE `units`.`lecturer_id` = `lecturer`.`lecturer_id` AND `lecturer`.`lecturer_id` = ? AND  `units`.`unit_code` = `alloc_time`.`unit_code`';
   }
  var query = connection.query(SQL,[info.lecturer_id],function(err, rows, fields) {

    if (!err){

        for(var i= 0; i < rows.length; i++){

           var info = {
             unit_code:rows[i]['unit_code'],
             name:rows[i]['name'],
             dept:rows[i]["dept"],
             hours:rows[i]["hours"],
             class_group:rows[i]["class_group"],
              noOfStudents:'40'
           }
           if(data.type == "set"){
               info.status = rows[i]['day'] +" "+rows[i]['s_time'] +" to "+rows[i]['f_time'];
           }
           unitsArray.push(info);
        }
          callback(unitsArray ) ;
    }else{
         console.log("errror in sql syntax "+err);
    }
    });
    console.log(query.sql);
   });
}

function getLecInfo(connection,userId,callback){
    var query = connection.query('SELECT *  from `lecturer` WHERE  `user_id` = ? ',userId,function(err, rows, fields) {
        if(!err){
            //look for a better way chris
            var info = {
                lecturer_id:rows[0]["lecturer_id"]
            }
         callback(info);
        }
       // console.log(query.sql);
        
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
var insertUnits = function(connection,data,callback){
    var query = connection.query('INSERT INTO `units` SET ? ',data, function(err, result) {
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
var updateUnits = function(connection,data,callback){
    var query = connection.query('UPDATE `units` SET ?  WHERE  ?',[data.new_data,data.old_data], function(err, result) {
       console.log(query.sql); 
      if (!err){
             var response = {
                success:1,
                message:"Updated Unit Successfully !!"
             };
             callback(response);
      }else{
           var response = {
                success:0,
                message:"An errored occurred while updating unit!!"
             };
             callback(response);
      }
     });
}
var deleteUnit = function(connection,data,callback){
    var query = connection.query('DELETE FROM `units`  WHERE ?',data, function(err, result) {
       console.log(query.sql); 
      if (!err){
             var response = {
                success:1,
                message:"Deleted Unit Successfully !!"
             };
             callback(response);
      }else{
           var response = {
                success:0,
                message:"An errored occurred while deleting unit!!"
             };
             callback(response);
      }
     });
}
var insertLec = function(connection,data,callback){
    var query = connection.query('INSERT INTO `lecturer` SET ? ',data, function(err, result) {
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
exports.insertLec = insertLec;
exports.deleteUnit = deleteUnit;
exports.updateUnits = updateUnits;
exports.insertUnits = insertUnits;
exports.getCurrentGroups = getCurrentGroups;
exports.getLecUnits = getLecUnits;
exports.getUnits = getUnits;
exports.getGroupUnits = getGroupUnits;

