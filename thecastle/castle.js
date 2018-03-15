
var getDekutDetails = function(connection,details,callback){
    var query = connection.query("SELECT * FROM `student` WHERE `castle_id` = 3 ",function(err, rows, fields) {
        if(!err){
           var dekutPopulation = rows.length;
               var query2 = connection.query("SELECT * FROM `student` WHERE `castle_id` = 3 AND ? ",[{dept:details.dept},{class_group:details.class_group}],function(err2, rows2, fields2) {
                   var groupPopulation = rows2.length;
               });
        }else{
            console.log(err)
        }
    });
}