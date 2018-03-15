var profile_photo = getCookie("profile_photo");
var username = getCookie("username");

$(window).load(function() {

            $.ajax({
             type: "POST",
             url: "/timetable/getCurrentClassInfo",
             contentType: "application/json",
             dataType: "json",
             async: true,
             data: ""
         }).done(function(res) {
                 if(res["status"] == 1 ){
                    setClassInfo(res["data"],1);
                 }else if (res["status"] == 2){
                    setClassInfo(res["data"],2);
                 }else{
                   alert(res['message']);
                 }
         });

        $.ajax({
             type: "POST",
             url: "/trends/getTrends",
             contentType: "application/json",
             dataType: "json",
             async: true,
             data: ""
         }).done(function(res) {
                 if(res["status"] == 1){
                   loadTrends(res["data"],"initial");
                 }else{
                   alert(res['message']);
                 }
         });

         $.ajax({
             type: "POST",
             url: "/trends/getSuggestions",
             contentType: "application/json",
             dataType: "json",
             async: true,
             data: ""
         }).done(function(res) {
                 if(res["status"] == 1){
                   loadSuggestions(res["data"],"initial");
                 }else{
                   alert(res['message']);
                 }
         });

         $('#attachedFile').on('change',function (event) {
                        var fileName = $(this).val();
                        var filePath = URL.createObjectURL(event.target.files[0]);

                        $("#attachmentHolder").append('<li>'+
                                   '<span class="mailbox-attachment-icon has-img"><img src="'+filePath+'" alt="Attachment"></span>'+
                                   '<div class="mailbox-attachment-info">'+
                                       '<a href="#" class="mailbox-attachment-name"><i class="fa fa-camera"></i>'+fileName+'</a>'+
                                       '<span class="mailbox-attachment-size">'+
                                         '0.3 + MB'+
                                         '<a href="#" class="btn btn-default btn-xs pull-right"><i class="fa fa-close"></i></a>'+
                                       '</span>'+
                                   '</div>'+
                               '</li>');

                        $("#attachmentHolder").attr("src",filePath);

         });

           $("#postCommentBtn").on("click",function(){

                   $("#postBody").append('<div>Uploading Image</div><div class="overlay"> <i class="fa fa-refresh fa-spin"></i></div>');

                     var comment_text = $("#compose-textarea").val();
                       comment_text = $.trim(comment_text)
                       var files = $('#attachedFile').get(0).files;

                                if(!comment_text ){
                                   alert("input comment text !");
                               }else if(files.length < 1){
                                   alert("Proceed without Image!");
                               }else if(files.length > 0 && comment_text){
                                  var formData = new FormData();
                                     for (var i = 0; i < files.length; i++) {
                                       var file = files[i];
                                       formData.append('attachedFile', file, file.name);
                                     }
                                        $.ajax({
                                           url: '/comment/postComment?comment_text='+comment_text+'&has_image=Y',
                                           type: 'POST',
                                           data: formData,
                                           processData: false,
                                           contentType: false,
                                           success: function(data){
                                              var post_details = [];


                                               var details = {
                                                 "comment_text":comment_text,
                                                 "username":username,
                                                 "comment_id":1000,
                                                 "has_image":"Y",
                                                 "time_gone":"Just Now",
                                                 "image_path":file.name,
                                                 "has_liked":false,
                                                 "likes":0
                                               }

                                               var post_data = {
                                                   "comments":details,
                                                   "replies":[]
                                                 }

                                               post_details.push(post_data);

                                               var data = {
                                                 "comments":post_details
                                               }
                                              loadComments(data,"me");
                                           },
                                           xhr: function() {
                                             // create an XMLHttpRequest
                                             var xhr = new XMLHttpRequest();

                                             xhr.upload.addEventListener('progress', function(evt) {

                                               if (evt.lengthComputable) {
                                                 // calculate the percentage of upload completed
                                                 var percentComplete = evt.loaded / evt.total;
                                                 percentComplete = parseInt(percentComplete * 100);

                                                 if (percentComplete === 100) {
                                                     $(".overlay").remove();
                                                     $('#post_modal').modal('hide')
                                                 }

                                               }

                                             }, false);

                                             return xhr;
                                           }
                                         });
                               }


                  });
       });



    function setClassInfo(data,option){

      if (option == 1){


      var unit_code = data["unit_code"];
      var time_range = data["s_time"] + " - "+ data["f_time"];
      var room = data["room"];
      var info = data["info"];

          if(info == "coming"){
               var day = data["day"];
               $("#class_info_ul").append('<li><a href="#"><span>'+unit_code+'   in  Room '+room+'</span>'+
                                              ' <span class="pull-right label label-info"> ON '+day+'</span>'+
                                             ' <span class="pull-right  "style="margin-right:20px;"> '+time_range+' hrs</span></a> </li>');
          }else if(info == "NOW"){

                $("#class_info_ul").append('<li class=""><a href="#"><span>'+unit_code+'   in  Room '+room+'</span>'+
                                               ' <span class="pull-right label bg-red">'+info+'</span>'+
                                              ' <span class="pull-right  "style="margin-right:20px;"> '+time_range+' hrs</span></a> </li>');

         }else if(info == "NEXT TODAY" ){

                         $("#class_info_ul").append('<li><a href="#"><span>'+unit_code+'   in  Room '+room+'</span>'+
                         ' <span class="pull-right label label-orange">'+info+'</span>'+
                        ' <span class="pull-right  "style="margin-right:20px;"> '+time_range+' hrs</span></a> </li>');
         }
         }else if (option == 2){
             $("#class_info_ul").append('<li><a href="#"><span>No more classes this week</span>'+
                                             ' <span class="pull-right badge badge-default "style="margin-right:20px;">You are Free</span></a> </li>');
         }
    }

     function loadTrends(data,timeOfLoad){

        var first_castle_id = -1;

       for (var i = 0; i < data.length; i++){
           var map = data[i];

           var castle_id = map["castle_id"];

           if (i == 0){
              first_castle_id = castle_id
           }

           var name = map["name"];
           var topic = map["topic"];
           var avatar_url = $.trim(map["avatar_url"]);

          $("#trending_ul").append(' <li class="item">'+
                       '<div class="product-img">'+
                           '<img  class="" src="static/uploads/images'+avatar_url+'" alt="Image">'+
                       '</div>'+
                       '<div class="product-info">'+
                           '<a id="a_'+castle_id+'" class="product-title trending_link">'+topic+
                               '<span class="label label-info pull-right"></span>'+
                           '</a>'+
                           '<span class="product-description">'+
                              name+
                           '</span>'+
                       '</div>'+
                   '</li>');
       }

         $(".trending_link").on("click",function(){
            var id = $(this).prop("id");
            var castle_id = id.substr(2,id.length);
            $("#comments_box").prepend('<div class="overlay"> <i class="fa fa-refresh fa-spin "></i></div>');
              fetchCastleData(castle_id);
            fetchComments(castle_id);

         });

         if ( first_castle_id != -1 && timeOfLoad == "initial"){
             $("#comments_box").prepend('<div class="overlay"> <i class="fa fa-refresh fa-spin "></i></div>');
              fetchCastleData(first_castle_id);
            fetchComments(first_castle_id);
         }



     }

          function loadSuggestions(data,timeOfLoad){

             var first_castle_id = -1;

            for (var i = 0; i < data.length; i++){
                var map = data[i];

                var castle_id = map["castle_id"];

                if (i == 0){
                   first_castle_id = castle_id
                }

                var name = map["name"];
                var members = map["members"];
                var avatar_url = $.trim(map["avatar_url"]);

               $("#suggestions_ul").append(' <li class="item">'+
                            '<div class="product-img">'+
                                '<img  class="" src="static/uploads/images'+avatar_url+'" alt="Image">'+
                            '</div>'+
                            '<div class="product-info">'+
                                '<a id="a_'+castle_id+'" class="product-title trending_link">'+name+
                                    '<span class="btn btn-info btn-sm pull-right">Join</span>'+
                                '</a>'+
                                '<span class="product-description">'+
                                   members+
                                ' Members</span>'+
                            '</div>'+
                        '</li>');
            }

              $(".trending_link").on("click",function(){
                 var id = $(this).prop("id");
                 var castle_id = id.substr(2,id.length);
                 $("#comments_box").prepend('<div class="overlay"> <i class="fa fa-refresh fa-spin "></i></div>');
                   fetchCastleData(castle_id);
                 fetchComments(castle_id);

              });

              if ( first_castle_id != -1 && timeOfLoad == "initial"){
                  $("#comments_box").prepend('<div class="overlay"> <i class="fa fa-refresh fa-spin "></i></div>');
                   fetchCastleData(first_castle_id);
                 fetchComments(first_castle_id);
              }



          }
     function fetchCastleData(castle_id){

      var form_data = {
       "castle_id":String(castle_id)
      }
       $.ajax({
         type: "POST",
         url: "/castle/getCastleData",
         contentType: "application/json",
         dataType: "json",
         data: JSON.stringify(form_data)
     }).done(function(res) {
             if(res["status"] == 1){
                 loadCastleData(res["data"]);
             }else{
               alert(res['message']);
             }
     });

     }

    function fetchComments(castle_id){


     var form_data = {
           "castle_id":String(castle_id)
     }
     $.ajax({
          type: "POST",
          url: "/comment/viewPosts",
          contentType: "application/json",
          dataType: "json",
          async: true,
          data:JSON.stringify(form_data)
      }).done(function(res) {
              if(res["status"] == 1){
                loadComments(res,"all");
              }else{
                alert(res['message']);
              }
      });

    }
    function loadCastleData(map){


          var castle_id = map["castle_id"];
          var name = map["name"];
          var topic = map["topic"];
          var info = map["info"];
          var post_no = String(map["no_of_posts"]);
          var followers_no = String(map["followers"]);
          var avatar_url = $.trim(map["avatar_url"]);
          var cover_url = $.trim(map["cover_url"]);

          $("#castle_info").text(info);
          $("#castle_name").text(name);
          $("#parent_name").text("DeKut");
          $("#castle_avatar").attr("src","/static/uploads/images"+avatar_url);
          $("#post_topic").text(topic);
          $("#posts_no").text(post_no);
          $("#followers_no").text(followers_no);

          let imageUrl = "/static/uploads/images"+cover_url;
          $('#castle_cover_image').css("background-image", `url(${imageUrl})` );
          $('#castle_cover_image').css("float", " center")



    }
    function loadComments(details,type){

      // alert(details[0]["comment_text"])

      var comments_data = details["comments"]

       var hasReplies = false

     //var ul_commentHolder = document.createElement("ul");
     if(type == "all"){
          $('#comment_content').empty();
          $('#comment_content').append("<div id='ul_holder'></div>");
     }

    for(var i = 0; i < comments_data.length; i++ ){

      var comments = comments_data[i]["comments"]
      var replies_data = comments_data[i]["replies"]

      var username = comments["username"];
      var comment =  comments["comment_text"];
      var comment_id =  comments["comment_id"];
      var has_image =  comments["has_image"];
      var image_path =  comments["image_path"];
       var time_gone =  comments["time_gone"];
       var has_liked =  comments["has_liked"];
       var likes =  comments["likes"];


       var row_div = document.createElement("span");
       $(row_div).attr("id",comment_id);
      // $(row_div).addClass("row row-centered");
        if(type == "all"){
             $("#ul_holder").append($(row_div));
         }else if(type == "me"){
                $("#ul_holder").prepend($(row_div));
         }


        var col_div = document.createElement("div");
     // $(col_div).addClass("col-md-12 col-centered");
       $(row_div).append($(col_div));

       var box_widget_div = document.createElement("div");
        $(box_widget_div).attr("id","box_widget_div"+comment_id);
       $(box_widget_div).addClass("  box  box-info " );
       $(col_div).append($(box_widget_div));

       $(box_widget_div).append('<div class="box-header bg-info  ">'+
                            '<div class="user-block">'+
                                '<img class="img-circle" src="/static/uploads/images/profile/'+profile_photo+'" alt="User Image">'+
                                '<span class="username"><a href="#">'+username+'</a></span>'+
                                '<span class="description">'+time_gone+'</span>'+
                            '</div>'+
                        '</div>');

            var box_body_div = document.createElement("div");
            $(box_body_div).addClass("box-body box_body_comments");

           var replies = replies_data["replies"];

            if(has_image == "Y"){
                  $(box_body_div).append('<img class="img-responsive pad" src="/static/uploads/images/posts/'+image_path+'" alt="Photo">');
            }


            $(box_body_div).append(' <p>'+comment+'</p>');
           // $(box_body_div).append('<button type="button" class="btn btn-default btn-xs"><i class="fa fa-share"></i> Share</button>');
            $(box_body_div).append('<button type="button" class="btn btn-default btn-xs like_button pull-left" value='+comment_id+'><i class="fa fa-heart like_icon" id="like'+comment_id+'"  ></i> Like </button>');

             if( has_liked == true){
                $(box_body_div).find(".like_icon").css("color","red");
             }

            if(replies_data["status"] == 0 || replies == null ){
              $(box_body_div).append('<span class="pull-right text-muted ">'+likes+' likes - 0 <i class="fa fa-comments-o margin-r-5"></i>comments</span>');
            }
            $(box_widget_div).append( $(box_body_div));


             //handle replies
            if(replies_data["status"] = 1 && replies != null ){

                     var no_replies = replies.length;
                     $(box_body_div).append('<span class="pull-left text-teal " style="margin-left:8px;">'  +likes+' likes </span>');
                     $(box_body_div).append('<span class="pull-right text-teal "> <i class="fa fa-comments-o margin-r-5"></i> '+no_replies+'  comments </span>');
                   // console.log(replies_data)
                   var box_footer_div = document.createElement("div");
                   $(box_footer_div).addClass("box-footer box-comments  ");
                  $(box_widget_div).append($(box_footer_div));

                 for (var j = 0; j < replies.length; j++){

                             var reply_text = replies[j]["reply_text"];
                             var username_reply = replies[j]["username"];
                             var time_gone_reply = replies[j]["time_gone"];

                             var hasReplyImage = false;
                             if(replies[j]['has_image']) {
                                 hasReplyImage = true;

                                }

                             var box_comment_div = document.createElement("div");
                             $(box_comment_div).addClass("box-comment");

                             $(box_comment_div).append('<img class="img-circle img-sm" src="/static/uploads/images/profile/'+profile_photo+'" alt="User Image">');
                             $(box_comment_div).append('<div class="comment-text">'+
                                                          '<span class="username "> '+username_reply+'<span class="text-muted pull-right">'+time_gone_reply+'</span></span>'+reply_text+
                                                              '</div>');
                            $(box_comment_div).append("<hr class='hr-spacing'>")

                             $(box_footer_div).append( $(box_comment_div));
                 }

           }else{
           }

            $(box_widget_div).append('<div class="box-footer" id="comment_box'+comment_id+'">'+
               '<div >'+
                   '<img class="img-responsive img-circle img-sm" src="/static/uploads/images/profile/'+profile_photo+'" alt="Alt Text">'+
                   '<!-- .img-push is used to add margin to elements next to floating images -->'+
                   '<div class="img-push">'+
                       '<input type="text" class="form-control input-sm replyField" id='+comment_id+' placeholder="Press enter to post comment">'+
                   '</div>'+
               '</div>'+
               '</div>');
         }

      $(".replyField").keypress(function(e) {

                        if(e.which == 13) {

                           var reply_text = $(this).val();
                           var comment_id = $(this).prop('id');
                           var form_data = {
                             "comment_id":comment_id,
                             "reply_text":reply_text
                           }

                           $.ajax({
                               type: "POST",
                               url: "/comment/replyComment",
                               contentType: "application/json",
                               dataType: "json",
                               data: JSON.stringify(form_data)
                           }).done(function(res) {
                                   if(res["status"] == 1){
                                     // window.location.href = "/";

                                   }else{
                                     alert(res['message']);
                                   }
                           });


                         var box_footer_div = document.createElement("div");
                         $(box_footer_div).addClass("box-footer box-comments  ");
                        $("#box_widget_div"+comment_id).append($(box_footer_div));

                      //  $("#comment_box"+comment_id).remove();

                            var box_comment_div = document.createElement("div");

                           var time_gone_reply = "Just Now";
                           var reply_text = reply_text;
                            $(box_comment_div).addClass("box-comment");
                            // $(box_comment_div).append("<hr>")
                            $(box_comment_div).append('<img class="img-circle img-sm" src="/static/uploads/images/profile/'+profile_photo+'" alt="User Image">');
                           $(box_comment_div).append('<div class="comment-text">'+
                             '<span class="username "> '+username+'<span class="text-muted pull-right">'+time_gone_reply+'</span></span>'+
                             reply_text+ '</div>');
                           $(box_footer_div).append( $(box_comment_div));

                           /*$("#box_widget_div"+comment_id).append('<div class="box-footer " id="comment_box'+comment_id+'">'+
                                          '<div >'+
                                              '<img class="img-responsive img-circle img-sm" src="/static/uploads/images/profile/'+profile_photo+'" alt="Alt Text">'+
                                              '<!-- .img-push is used to add margin to elements next to floating images -->'+
                                              '<div class="img-push">'+
                                                  '<input type="text" class="form-control input-sm replyField" id='+comment_id+' placeholder="Press enter to post comment">'+
                                              '</div>'+
                                          '</div>'+
                                          '</div>');*/




                        }
             });


     $(".overlay").remove();

      $(".like_button").on("click",function(){

         var comment_id = $(this).attr("value");
                 var form_data = {
                    "comment_id":String(comment_id)
                  }
                  $.ajax({
                       type: "POST",
                       url: "/comment/postLike",
                       contentType: "application/json",
                       dataType: "json",
                       async: true,
                       data:JSON.stringify(form_data)
                   }).done(function(res) {
                           if(res["status"] == 1){
                                  if(res["action"] == 1){
                                      $("#like"+comment_id).css("color","grey");
                                  }else if(res["action"] == 2){
                                     $("#like"+comment_id).css("color","red");
                                  }
                           }else{
                             alert(res['message']);
                           }
                   });
          });
    }




