//register student

var checkStudentRegistration = function (connection, details, callback) {
     var query2 = connection.query("SELECT * FROM `student` WHERE ? ", [{user_id: details.user_id}], function (err, rows, fields) {
        console.log(query2.sql);
         if(!err){
              if (rows.length > 0) {
                  var studentDetails = {
                        user_id: details.user_id,
                        castle_id: rows[0]['castle_id'],
                        regNo: rows[0]['regNo'],
                        dept: rows[0]['dept'],
                        class_group: rows[0]['class_group'],
                        success: 1
                    }
                    callback(studentDetails);
              }else{
                 callback({success: 0, message: "Student not registered !"}); 
              }
         }else{
             console.log(err);
         }
     
     });
}
var registerStudent = function (connection, student, callback) {

    var query2 = connection.query("SELECT `user`.`phoneNo` FROM `student`,`user` WHERE `student`.`user_id` = `user`.`user_id` AND ? ", [{'user.user_id': student.user_id}, {phoneNo: student.phoneNo}], function (err2, rows2, fields2) {
        console.log(query2.sql);
        if (!err2) {
            if (rows2.length > 0) {
                var phoneNo = rows2[0]['phoneNo'];
                if (phoneNo != student.phoneNo) {
                    callback({success: 0, message: "Student already registered with another Phone number !"});
                } else {
                    console.log("inserted student")
                    var studentDetails = {
                        user_id: student.user_id,
                        castle_id: rows2[0]['castle_id'],
                        regNo: rows2[0]['regNo'],
                        dept: rows2[0]['dept'],
                        class_group: rows2[0]['class_group'],
                        success: 1
                    }
                    callback(studentDetails);
                }

            } else {
                var studentDetails = {
                       user_id: student.user_id,
                       castle_id: student.castle_id,
                       regNo: student.regNo,
                       dept: student.dept,
                       class_group: student.class_group
                   };
                   console.log(studentDetails)
                var query = connection.query('INSERT INTO student SET ?', studentDetails, function (err, result) {
                    console.log(query.sql)
                    if (!err) {
                        console.log("inserted student")
                        studentDetails['success'] = 1;
                        callback(studentDetails);
                    } else {
                        console.log(err);
                        callback({success: 0});
                    }
                });
            }
        } else {
            console.log(err2);
            callback({success: 0, message: "sql error"});
        }

    });

}
var getDekutDetails = function (connection, details, callback) {
    var query = connection.query("SELECT * FROM `student` WHERE `castle_id` = 3 ", function (err, rows, fields) {
       console.log(query.sql);
       console.log(details)
        if (!err) {
            var dekutPopulation = rows.length;
            

            var query2 = connection.query("SELECT * FROM `student` WHERE `castle_id` = 3 AND ? AND ? ", [{dept: details.dept}, {class_group: details.class_group}], function (err2, rows2, fields2) {
                console.log(query2.sql);
                if (!err2) {
                    var groupPopulation = rows2.length;
                    var studentDetails = {
                        castle_pop: dekutPopulation,
                        group_pop: groupPopulation
                    }
                    callback(studentDetails);
                } else {
                    console.log(err2)
                }

            });

        } else {
            console.log(err)
        }
    });
}
exports.checkStudentRegistration = checkStudentRegistration;
exports.registerStudent = registerStudent;
exports.getDekutDetails = getDekutDetails;