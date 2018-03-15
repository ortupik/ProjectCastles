var operations = require('./thecastle/dboperations');
var path = require('path');
var formidable = require('formidable');
var nodestatic = require('node-static');
var nodemailer = require("nodemailer");
var Guid = require('guid');
var express = require('express');
var bodyParser = require("body-parser");
var Mustache  = require('mustache');
var Request  = require('request');

var fs = require('fs');
var csrf_guid = "shdfshdjhsfdshdfhs";
var account_kit_api_version = 'v1.0';
var app_id = '885361471643097';
var app_secret = '82eda8926263f2d2ab3a8b6797517b13';
var me_endpoint_base_url = 'https://graph.accountkit.com/v1.0/me';
var token_exchange_base_url = 'https://graph.accountkit.com/v1.0/access_token'; 

module.exports = function (app, io,address) {

    app.get('/start', function (req, res) {
        res.render('secure_login');
    });
     app.get('/privacy', function (req, res) {
        res.render('privacy_policy');
    });
    app.get('/secure', function (req, res) {
        res.render('secure_login');
    });
    app.get('/user_login', function (req, res) {
        res.render('user_login');
    });
    function loadLogin() {
        return fs.readFileSync('views/login.html').toString();
    }
    app.get('/', function (req, res) {
        var view = {
            appId: app_id,
            csrf: csrf_guid,
            version: account_kit_api_version,
          };
          console.log("hey login");
          var html = Mustache.to_html(loadLogin(), view);
          res.send(html);
    });
    
    function loadLoginSuccess() {
      return fs.readFileSync('views/success.html').toString();
    }

    app.post('/success', function(request, response){
      console.log("hey success");
      // CSRF check
      if (request.body.csrf === csrf_guid) {
        var app_access_token = ['AA', app_id, app_secret].join('|');
        var params = {
          grant_type: 'authorization_code',
          code: request.body.code,
          access_token: app_access_token
        };

        // exchange tokens
        var token_exchange_url = token_exchange_base_url + '?' + Querystring.stringify(params);
        Request.get({url: token_exchange_url, json: true}, function(err, resp, respBody) {
          var view = {
            user_access_token: respBody.access_token,
            expires_at: respBody.expires_at,
            user_id: respBody.id,	
          };

          // get account details at /me endpoint
          var me_endpoint_url = me_endpoint_base_url + '?access_token=' + respBody.access_token;
          Request.get({url: me_endpoint_url, json:true }, function(err, resp, respBody) {
            // send login_success.html
            if (respBody.phone) {
              view.phone_num = respBody.phone.number;
            } else if (respBody.email) {
              view.email_addr = respBody.email.address;
            }
            var html = Mustache.to_html(loadLoginSuccess(), view);
            response.send(html);
          });
        });
      } 
      else {
        // login failed
        response.writeHead(200, {'Content-Type': 'text/html'});
        response.end("Something went wrong. :( ");
      }
    });
    app.get('/download', function (req, res) {
        res.render('download');
    });
   
    app.get('/home', function (req, res) {
        res.render('home');
    });
    app.get('/cod_manage_units', function (req, res) {
        res.render('cod_manage_units');
    });
   
    app.get('/cod_timetables', function (req, res) {
        res.render('cod_timetables');
    });
     app.get('/cod_draft_timetable', function (req, res) {
        res.render('cod_draft_timetable');
    });
     app.get('/cod_full_timetable', function (req, res) {
        res.render('cod_full_timetable');
    });
    app.get('/videoChats/:path',function(req,res){
        var path = req.params.path;
        console.log(path);
        console.log("Requested room "+path);
        res.render('videoChats', {"hostAddress":io});  
    });

    app.get('/index', function (req, res) {
        res.render('index');
    });
    app.get('/registration', function (req, res) {
        res.render('registration');
    });
    app.get('/blog', function (req, res) {
        res.render('blog');
    });
    app.get('/student_timetable', function (req, res) {
        res.render('student_timetable');
    });
     app.get('/edit_timetable', function (req, res) {
        res.render('edit_timetable');
    });
     app.get('/scheduler_timetable', function (req, res) {
        res.render('scheduler_timetable');
    });
    app.get('/master_timetable', function (req, res) {
        res.render('master_timetable');
    });
     app.get('/scheduler_select', function (req, res) {
        res.render('scheduler_select');
    });
     app.get('/scheduler_units', function (req, res) {
        res.render('scheduler_units');
    });
     app.get('/exam_timetable', function (req, res) {
        res.render('exam_timetable');
    });
     app.get('/units', function (req, res) {
        res.render('units');
    });
     app.get('/castles', function (req, res) {
        res.render('index_1');
    });
     app.get('/student_reg', function (req, res) {
        res.render('student_reg');
    });
    app.get('/polls', function (req, res) {
        res.render('polls');
    });
     app.get('/posts', function (req, res) {
        res.render('posts');
    });
    app.get('/profile', function (req, res) {
        res.render('profile');
    });
     app.get('/products', function (req, res) {
        res.render('products');
    });
     app.get('/products-single', function (req, res) {
        res.render('products-single');
    });
     app.get('/about', function (req, res) {
        res.render('about');
    });
    app.post('/android_upload_profile', function(req, res){
       
        var form = new formidable.IncomingForm();
        form.multiples = true;
        form.uploadDir = path.join(__dirname, '/public/profile_images/');
       
        form.on('file', function(field, file) {
            console.log("uploaded profile image "+file.name);
          fs.rename(file.path, path.join(form.uploadDir, file.name));
        });
        form.on('error', function(err) {
          console.log('An error has occured: \n' + err);
        });

       form.on('end', function() {
          res.end('success');
        });

  // parse the incoming request containing the form data
        form.parse(req);
    });
     app.post('/android_upload_images', function(req, res){
       
        var form = new formidable.IncomingForm();
        form.multiples = true;
        form.uploadDir = path.join(__dirname, '/public/image_uploads/');
       
        form.on('file', function(field, file) {
          fs.rename(file.path, path.join(form.uploadDir, file.name));
        });
        form.on('error', function(err) {
          console.log('An error has occured: \n' + err);
        });

       form.on('end', function() {
          res.end('success');
        });

  // parse the incoming request containing the form data
        form.parse(req);
    });
    
    //put this data in function later
  app.post('/upload', function(req, res){

        // create an incoming form object
        var form = new formidable.IncomingForm();

        // specify that we want to allow the user to upload multiple files in a single request
        form.multiples = true;


         form.uploadDir = path.join(__dirname, '/public/'+req.param('folder'));
       // form.uploadDir = path.join(__dirname, '/public/profile_images');
       // console.log("req.param('folder') "+req.param('folder'))

        // every time a file has been uploaded successfully,
        // rename it to it's orignal name
        form.on('file', function(field, file) {
          fs.rename(file.path, path.join(form.uploadDir, file.name));
        });

        // log any errors that occur
        form.on('error', function(err) {
          console.log('An error has occured: \n' + err);
        });

        // once all the files have been uploaded, send a response to the client
        form.on('end', function() {
          res.end('success');
  });

  // parse the incoming request containing the form data
  form.parse(req);

});

    

   //holds a list of all users who are online
   var onlineUsers = [];
   var myRooms = []
   var connectedIds = [];

    // Initialize a new socket.io application, named 'chat'
    var chat = io.on('connection', function (socket) {

        console.log("connected one user "+socket.id);
        connectedIds.push(socket.id);
        socket.emit("autoLogin",{socket_id:socket.id});
        socket.join(socket.id);
        
        
        //if(socket.user_id != null && socket.phoneNo != null){
              
       // }
       
       socket.on("qrLogin", function (data) {
            var loginData = {
                user_id: data.user_id,
                phoneNo: data.phoneNo,
                username: data.username
            };
            console.log("qrLogin");
            console.log(loginData);
            
           socket.emit('qrLogin',{message:"Successfully logged you in "});
           socket.broadcast.to(data.socket_id).emit('qrLogin',loginData);     
       });
        
        socket.on("registerPhone", function (data) {
            console.log("register Phone  " + data.phoneNo);

            operations.registerPhone(data, function (result) {
                if (result >= 1) {
                    console.log("Phone Verification Successful");
                    var command = {
                        action: "registerPhone",
                        result: 1
                    };

                } else {
                    console.log("result " + result);
                    console.log("Invalid phoneNo");
                    var command = {
                        action: "registerPhone",
                        result: 0
                    };
                }
                socket.emit("registerPhone", command);
            });
        });
        
        //Sign In user
       socket.on("signInUser", function (data) {

           operations.checkUser(data,function(userResult){
                if(userResult.success == 1){
                        var command = {
                            action: "signInUser",
                            user_id:userResult.user_id,
                            username:userResult.username,
                            user_role:userResult.user_role,
                            result:1,
                            message:"User Exists"
                        };
                      socket.emit("signInUser", command);

                }else if(userResult.success == 2){
                     var command = {
                            action: "signInUser",
                            user_id:userResult.user_id,
                            username:userResult.username,
                            result:2,
                            message:"Complete profile to proceed !"
                        };
                      socket.emit("signInUser", command);
                } else{
                     var command = {
                        action: "signInUser",
                        result: 0,
                        message:"User Does Not Exist"
                     };
                        socket.emit("signInUser", command);
                    }
               });
           });
             
        //Register User
        socket.on("registerUser", function (data) {

             operations.checkUser(data,function(userResult){
                if(userResult.success == 1){
                        var command = {
                            action: "registerUser",
                          //  user_id:userResult.user_id,
                          //  username:userResult.username,
                            result:2,
                            message:"User Already Exists !!"
                        };
                      socket.emit("registerUser", command);

                }else if(userResult.success == 3){
                    var command = {
                        action: "registerUser",
                        result: 3,
                        message:"Sorry,No more new Registrations for Now !"
                    };
                    socket.emit("registerUser", command);
                }else{
                    operations.registerUser(data, function (result) {
                        if (result.success == 1) {
                            var command = {
                                action: "registerUser",
                                user_id:result.user_id,
                               // username:result.username,
                                result: 1
                            };
                              console.log("Registration Successful for "+command.user_id);


                        } else {
                            console.log("result " + result);
                            console.log("Registration anomally");
                            var command = {
                                action: "registerUser",
                                result: 0
                            };
                        }
                        socket.emit("registerUser", command);

                    });
                }

            });
            
        });
         socket.on("getCurrentGroups",function(data){
                console.log("getCurrentGroups");
                 console.log(data);
                operations.getCurrentGroups(data,function(result){
                    console.log(result);
                    socket.emit("getCurrentGroups",result);
                });
            });
            
          socket.on("registerUserProfile", function (data) {
          //  var thePath = path.join(__dirname, '/public/profile_images/');
           //  fs.writeFile(thePath+data.phoneNo+".jpg", new Buffer(data.encodedImg, "base64").toString(), function(err) {});
             
             // data.profile_photo = data.phoneNo+".jpg";
                  console.log(data)
              operations.registerUserProfile(data,function(result){
                  console.log(result);
                  socket.emit("registerUserProfile", result);
              });         
          });
          socket.on("editProfile", function (data) {
              operations.editProfile(data,function(result){
                  console.log("updated user_id for user ");

                  console.log(result)
                  socket.emit("editProfile", result);
                  
              })         
          });
           socket.on("sendMail", function (data) {
               console.log("Sending email...");
                 sendEmail(data);
          });
           
             socket.on("insertTimetable",function(data){
                console.log("inseting class timetable");
                operations.insertTimetable(data,function(result){
                    socket.emit("insertTimetable",result);
                });
            });
            
             socket.on("newTimetable",function(data){
                console.log("COD creating new timetable");
                operations.newTimetable(data,function(result){
                    socket.emit("newTimetable",result);
                });
            });
             socket.on("deleteTimetable",function(data){
                console.log("COD deleting timetable");
                operations.deleteTimetable(data,function(result){
                    socket.emit("deleteTimetable",result);
                });
            });

             socket.on("updateTimetableStatus",function(data){
                console.log("COD updating status of timetable");
                operations.updateTimetableStatus(data,function(result){
                    socket.emit("updateTimetableStatus",result);
                });
            });
           socket.on("viewTimetable",function(data){
                console.log("viewing class timetable");
                operations.getTimetable(data,function(result){
                    socket.emit("viewTimetable",result);
                    
                });
            });
            socket.on("getTimetables",function(data){
                console.log("get timetables");
                operations.getTimetables(data,function(result){
                    socket.emit("getTimetables",result);
                });
            });
             socket.on("viewGroupTimetable",function(data){
                console.log("viewing class timetable");
                operations.getGroupTimetable(data,function(result){
                    socket.emit("viewGroupTimetable",result);
                    
                });
            });
            
            socket.on("viewExamTimetable",function(data){
                console.log("viewing exam timetable");
                 console.log(data);
                operations.getExamTimetable(data,function(result){
                    socket.emit("viewExamTimetable",result);
                    
                });
            });
             socket.on("roomTimetable",function(data){
                console.log("Room Timetable");
                operations.getRoomTimetableInfo(data,function(result){
                   // console.log(result);
                    socket.emit("roomTimetable",result);
                    
                });
            });
             socket.on("getClassRooms",function(data){
                console.log("viewing class rooms");
                operations.getAllClassRooms(data,function(result){
                   // console.log(result);
                    socket.emit("getClassRooms",result);
                    
                });
            });
             socket.on("checkLecturerDetails",function(data){
                console.log("checkLecturerDetails");
                operations.checkLecturerDetails(data,function(result){
                    console.log(result);
                    socket.emit("checkLecturerDetails",result);
                    
                });
            });
            
             socket.on("getGroupUnits", function (data) {
                console.log("retrieving Units");
                //please use socket.user_id in future chris
                operations.getGroupUnits(data,function (result) {
                //    console.log("UnitsArray result ");
                   // console.log(result);
                    socket.emit("getGroupUnits", result);
                });

            });
            socket.on("getUnits", function (data) {
                console.log("retrieving Units");
                //please use socket.user_id in future chris
                operations.getUnits(data,function (result) {
                //    console.log("UnitsArray result ");
                   // console.log(result);
                    socket.emit("getUnits", result);
                });

            });
            socket.on("insertUnits", function (data) {
                console.log("Insert Units");
                operations.insertUnits(data,function (result) {
                    console.log(result);
                    socket.emit("insertUnits", result);
                });

            });
            socket.on("updateUnits", function (data) {
                console.log("Update Units");
                operations.updateUnits(data,function (result) {
                    console.log(result);
                    socket.emit("updateUnits", result);
                });
            });
             socket.on("insertLec", function (data) {
                console.log("Insert Lec");
                operations.insertLec(data,function (result) {
                    console.log(result);
                    socket.emit("insertLec", result);
                });
            });

            socket.on("deleteUnit", function (data) {
                console.log("Delete Units");
                operations.deleteUnit(data,function (result) {
                    console.log(result);
                    socket.emit("deleteUnit", result);
                });

            });
             socket.on("getLecUnits", function (data) {
                console.log("retrieving Units");
                console.log(data)
                //please use socket.user_id in future chris
                operations.getLecUnits(data,function (result) {
                //    console.log("UnitsArray result ");
                   // console.log(result);
                    socket.emit("getLecUnits", result);
                });

            });
            socket.on("getCastlePosts",function(data){
              //  console.log("viewing posts");
                operations.getCastlePosts(data,function(result){
                   // console.log(result);
                    socket.emit("getCastlePosts",result);
                });
            });
            
             socket.on("getHostels",function(data){
                console.log("getting hostel data ");
                operations.getHostels(data,function(result){
                   console.log(result);
                  socket.emit("getHostels",result);
                });
            });
            
             socket.on("getHostelRooms",function(data){
                console.log("getting hostel rooms data ");
                operations.getHostelRooms(data,function(result){
                   console.log(result);
                    socket.emit("getHostelRooms",result);
                });
            });
            socket.on("getDekutCastleDetails",function(data){
                console.log("Getting Dekut details ");
                operations.getDekutDetails(data,function(result){
                  console.log(result)
                    socket.emit("getDekutCastleDetails",result);
                    
                });
            });
            socket.on("getDepts",function(data){
                console.log("viewing depts");
                var groups = ['Y1S1','Y1S2','Y2S2','Y3S2'];
                console.log(groups);
                socket.emit("getDepts",groups);
            });
             socket.on("studentRegistration",function(data){
                console.log("Registering student ");
                operations.registerStudent(data,function(result){
                    console.log(result)
                    socket.emit("studentRegistration",result);
                    
                });
            });
            socket.on("checkStudentRegistration",function(data){
                console.log("checking student registration ");
                operations.checkStudentRegistration(data,function(result){
                    console.log(result)
                    socket.emit("checkStudentRegistration",result);
                });
            });

        socket.on('login', function (data) {
            
           if(socket.user_id == null){  

            operations.checkLogin(data, function (result) {
                if (result.success == 1) {
                 //   console.log("Login Successful");
                  console.log( data.user_id + " " + data.phoneNo +" logged in !");
                  
                    socket.join(0);
    
                    var chatRoomRows = result.chatRooms;//List of All the Chat IDs
                        var onlineDetails = {
                            user_id:data.user_id
                        }
                       // console.log("online details");
                       // console.log(onlineDetails);
                        
                        
                    //Joining all chat rooms
                    for(var i = 0; i < chatRoomRows.length; i ++){
                        var chatRoom = chatRoomRows[i]['chat_id'];
                       socket.join(chatRoom);
                       onlineDetails['chat_id'] = chatRoom;
                       socket.broadcast.to(chatRoom).emit('userOnline',onlineDetails);  
                       myRooms.push(chatRoom);
                      console.log(data.user_id+" Joined and is online in "+chatRoom);
                      
                    }
                   
                  operations.getAllGroupChatRooms(data.user_id,function(gresult){
                    //Joinining all chat rooms in group rooms
                    if(gresult.success == 1){
                         var groupRoomRows = gresult.groupRooms;
                         console.log(groupRoomRows)
                         for(var i = 0; i < groupRoomRows.length; i ++){
                            var groupRoom = "g"+groupRoomRows[i]['group_id'];
                            socket.join(groupRoom);
                            console.log(data.user_id+" Joined and is online in  GROUP "+groupRoom);
                         }

                    } 
                });

                    //user is now online
                     var user_id = socket.user_id;
                     if(user_id != null){
                          console.log("current user id "+user_id);
                     }
                    if(user_id == null){
                        //Dont remove this unless you have a fix for !!
                        socket.user_id = data.user_id;
                        socket.phoneNo = data.phoneNo;
                        
                        console.log("setting  user id "+data.user_id);
                       // var info = socket.user_id+"*#="+socket.id;
                         var info = socket.user_id;
                         onlineUsers.push(info);
                    }
                   
                     console.log("onlineUsers");
                     console.log(onlineUsers);

                   // socket.in(1).broadcast.emit("polls");
                   
                    //console.log("user detals");
                   // console.log(result.userDetails);
                   
                    
                    socket.emit("login",result.userDetails);
                    socket.emit("onlineUsersList",{onlineUsers:onlineUsers});
                   

                  }else if(result.success == 2){
                    socket.user_id = data.user_id;
                    socket.phoneNo = data.phoneNo;
                     console.log("No Chat rooms"); 
                     
                  operations.getAllGroupChatRooms(data.user_id,function(gresult){
                    //Joinining all chat rooms in group rooms
                    if(gresult.success == 1){
                         var groupRoomRows = gresult.groupRooms;
                         console.log(groupRoomRows)
                         for(var i = 0; i < groupRoomRows.length; i ++){
                            var groupRoom = "g"+groupRoomRows[i]['group_id'];
                            socket.join(groupRoom);
                            console.log(data.user_id+" Joined and is online in  GROUP "+groupRoom);
                         }
                         
                         console.log("user detals");
                         console.log(result.userDetails);
                         
                         socket.emit("login",result.userDetails);
                         socket.emit("onlineUsersList",{onlineUsers:onlineUsers});

                    }
                    
                });
                     
                    
                } else {
                    console.log("result " + result);
                    console.log("Invalid Credentials");
                }
                

                
            });

            //handles incoming msgs
            socket.on('msg', function (chatMessageDetails) {
               handleIncomingMsgs(chatMessageDetails);
            });
            
            socket.on('acknowledgeReceive', function (chatMessageDetails) {
                operations.getOwnChatRoom(chatMessageDetails.user_id,function(result){
                    if(result.success == 1){
                        operations.updateDeliveryStatus(chatMessageDetails.message_id,function(result2){
                            if(result2.success == 1){
                                 socket.in(result.room).broadcast.emit("acknowledgeReceive",chatMessageDetails); 
                                 console.log("acknowldeging message "+chatMessageDetails.message_id);
                                 console.log(chatMessageDetails);  
                            }
                           
                        });
                    }
                });
            });
            
            socket.on('onImageSend', function (imageDetails) {

                    socket.in(1).broadcast.emit("onImageSend", {image: imageDetails});      
                    console.log("sent image");

             });

            socket.on("getContacts", function (data) {
              console.log("checking contacts");
              console.log(data);
                operations.getContacts(data,function (result) {
          
                    //  console.log("onlineUsers");
                     // console.log(onlineUsers);
                      var onlineContacts = [];
             
                if(onlineUsers.length > 0 && onlineUsers.length!=null ){
                     for(var i = 0; i < result.length; i++){
                //is there an easy way of checking whether  a value exists in array without looping?indexOf failed
                        
                        for(var j = 0;j < onlineUsers.length;j++){
                             if(result.contacts[i]['user_id']== onlineUsers[j]){
                                 console.log(result.contacts[i]['user_id'] +"->is online");
                                 onlineContacts.push(result.contacts[i]['user_id']);
                             }else{
                              //  console.log(result[i]['user_id'] +"->is offline");
                             }
                         }
                     }
                 }
                     result.contacts.push(onlineContacts);
                    // console.log(result);
                     socket.emit("getContacts", result);
                });

            });
            
           
            socket.on("getChats", function (chat_id) {
               // console.log("checking chats");
               
                
            //  console.log("chatsArray result in routes");

               
                
                    
                    //get group chats //merge when you have time
                 operations.getGroupChats(chat_id,function (result) {
                    //console.log(result);
                       if(Array.isArray(result)){
                            socket.emit("getGroupChats", result); 
                        }
                });
                   
                operations.getChats(chat_id,function (result) {
                  //  console.log(result);
                       if(Array.isArray(result)){
                            socket.emit("getChats", result); 
                        }
                });
                
                });


            socket.on("getChatMessages", function (chatDetails) {
              
               if(chatDetails.type == "individual"){
                    operations.getChatMessages(chatDetails, function (result) {
                        socket.emit("getChatMessages", result);
                        //console.log("messages result ");
                        //console.log(result);
                    });
                }else if(chatDetails.type == "groups"){
                    operations.getGroupChatMessages(chatDetails, function (result) {
                    // console.log("messages result ");
                      //console.log(result);
                        socket.emit("getChatMessages", result);
                    });
                }

            });


            //on typing
            socket.on('typing', function (data) {
                operations.getCurrentChatRoom(data.user_id,data.opponentId,function (result) {
                  if (result.success == 1) {
                   console.log("typing in "+result.room);
                  socket.in(result.room).broadcast.emit("typing");
                   }
               });
            });
            //on typing
            socket.on('stop typing', function (data) {
                operations.getCurrentChatRoom(data.user_id,data.opponentId,function (result) {
                  if (result.success == 1) {
                   console.log("stopped typing in "+result.room);
                  socket.in(result.room).broadcast.emit("stop typing");
                   }
               });
             
            });

            // When the client emits 'login', save his name and avatar,
            // and add them to the room
        }
      });

        socket.on('message', function (message) {
		log('Got message: ', message);
        socket.broadcast.to(socket.room).emit('message', message);
	});
    
	socket.on('create or join', function (message) {
        var room = message.room;
        socket.room = room;
        var participantID = message.from;
        configNameSpaceChannel(participantID);
        
		var numClients = io.sockets.clients(room).length;

		log('Room ' + room + ' has ' + numClients + ' client(s)');
		log('Request to create or join room', room);

		if (numClients == 0){
			socket.join(room);
			socket.emit('created', room);
		} else {
			io.sockets.in(room).emit('join', room);
			socket.join(room);
			socket.emit('joined', room);
		}
	});
    
    // Setup a communication channel (namespace) to communicate with a given participant (participantID)
    function configNameSpaceChannel(participantID) {
        var socketNamespace = io.of('/'+participantID);
        
        socketNamespace.on('connection', function (socket){
            socket.on('message', function (message) {
                // Send message to everyone BUT sender
                socket.broadcast.emit('message', message);
            });
        });
    }
        // Somebody left the chat
        socket.on('disconnect', function () {
            // Notify the other person in the chat room
            // that his partner has left
            
             console.log("disconnected user "+socket.id);
             connectedIds.pop(connectedIds.indexOf(socket.id),1);
             
            if(socket.user_id != null && socket.user_id != ''){
 
            //the user is now  offline

            onlineUsers.pop(onlineUsers.indexOf(socket.user_id),1);
              
             operations.updateLastSeen({user_id:socket.user_id},function (result) {
                    var offlineDetails = {
                        lastSeen:result.last_seen,
                        user_id:socket.user_id
                    }
                 console.log(result)
                  for(room in myRooms){
                        socket.broadcast.to(room).emit('userOffline',offlineDetails); 
                        myRooms.splice(myRooms.indexOf(room));
                   }
             });
            
            // leave the room
            console.log("disconnected user "+socket.user_id);
            console.log("onlineUsers");
           console.log(onlineUsers);
          }
        });


        // Handle the sending of messages
        
  
function handleIncomingMsgs(chatMessageDetails){
    
    //for individual msgs
    if(chatMessageDetails.type == "individual"){
        
       console.log("sending message to "+chatMessageDetails.message.opponentId);

     operations.getCurrentChatRoom(chatMessageDetails.message.user_id,chatMessageDetails.message.opponentId,function (result) {
        if (result.success == 1) {
               console.log("sending message in room "+result.room);
            operations.insertChatMessage(chatMessageDetails.message, function (messageresult) {
                console.log("Successfully inserted message --> "+messageresult.inserted);
                chatMessageDetails.message['message_id'] = messageresult.message_id;
                chatMessageDetails.message['created_at'] = messageresult.created_at;
                socket.in(result.room).broadcast.emit("msg", chatMessageDetails);
                console.log(chatMessageDetails);
                socket.emit("insertChatMessage",messageresult);
            });
        }else{

        }
   });
   }else if(chatMessageDetails.type == "group"){
       
           console.log("sending message in group_id "+chatMessageDetails.message.group_id);

            operations.insertGroupChatMessage(chatMessageDetails.message, function (messageresult) {
                console.log("Successfully inserted message --> "+messageresult.inserted);
                chatMessageDetails.message['message_id'] = messageresult.message_id;
                chatMessageDetails.message['created_at'] = messageresult.created_at;
                socket.in("g"+chatMessageDetails.message.group_id).broadcast.emit("msg", chatMessageDetails);
                console.log(chatMessageDetails);
                socket.emit("insertChatMessage",messageresult);
            });
        
   }
}

      function sendEmail(data){
          
             var email = "techflay@gmail.com";
             
            var transporter = nodemailer.createTransport({
                service: 'gmail',
                auth: {
                  user: 'techflay@gmail.com',
                  pass: '#tech.254-flay'
                }
            });
         
          
          var mailOptions = {
            from: email,
            to:email,
            subject: 'Dekut Timetable Scheduling System',
            text: "Dear Mr.Opiyo,Please Submit your preferences for the timetable in the Integrated Dekut Timetable System . Regards CS Scheduler."
          };

          transporter.sendMail(mailOptions, function(error, info){
            if (error) {
              console.log(error);
            } else {
               socket.emit("sendMail", info);
              console.log('Email sent: ' + info.response);
            }
          }); 
        }
  });
    
};