var express    = require("express");
var mysql      = require('mysql');

  var connection = mysql.createConnection({
      host     : 'localhost',
      user     : 'root',
      password : '',
      database : 'dekut_castle'
  });
var app = express();

connection.connect(function(err){
if(!err) {
    console.log("Database is connected ... nn");

    var userDetails  = {username:"robina", password: 'robina123',phoneNo:"072345453"};
    var query = connection.query('INSERT INTO user SET ?', userDetails, function(err, result) {
    // Neat!
    console.log("result "+result.insertId);
    });

    var newusername= "katerina";
    connection.query("UPDATE user SET ? WHERE ? ", [{username:"johnny"},{username:"john"}]);

   connection.query('DELETE FROM user WHERE ?',{username:"chris"}, function (err, result) {
      if (err) throw err;

      console.log('deleted ' + result.affectedRows + ' rows');
    });
    connection.query('SELECT `username` AS solution from user ', function(err, rows, fields) {
    connection.end();

      if (!err){
        for(var i= 0; i < rows.length; i++){
          console.log( rows[i].solution);
        }
      }else{
        console.log('Error while performing Query.');
      }
      });


  //console.log(query.sql); // INSERT INTO posts SET `id` = 1, `title` = 'Hello MySQL'


} else {
    console.log("Error connecting database ... nn");
}
});



app.listen(3000);
