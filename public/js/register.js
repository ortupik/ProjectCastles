$(window).load(function() {

  $loading = $('.loading');

  if(getCookie("step") == null){
     setCookie("step",0,1);
  }else{
     //alert(getCookie("step"))
  }

       $('#smartwizard').smartWizard({
             selected: 0,
             theme: 'default',
             transitionEffect:'fade',
             showStepURLhash: true,
        });

     $('#smartwizard').smartWizard({
             selected: 0,
             theme: 'default',
             transitionEffect:'fade',
             showStepURLhash: true,
        });

         // Initialize the leaveStep event
         $("#smartwizard").on("leaveStep", function(e, anchorObject, stepNumber, stepDirection) {
             switch(stepNumber){
                case 0:

                  break;
                  case 1:
                   // executeStep2();
                  break;
                  case 2:
                  //  executeStep3();
                  break;
                   case 3:
                     // executeStep4();
                    break;
                     case 5:
                        executeStep5();
                      break;
             }
         });

         // Initialize the showStep event
         $("#smartwizard").on("showStep", function(e, anchorObject, stepNumber, stepDirection) {
           // alert("You are on step "+stepNumber+" now");
         });


 $("#registerBtn").on('click',function(){


   var firstName = $("#firstName").val();
   var lastName = $("#lastName").val();
   var email = $("#email").val();
   var phoneNo = $("#phoneNo").val();
   var password = $("#password").val();
   var c_password = $("#c_password").val();

   if(password === c_password){

       var regData = {
         "firstName":firstName,
         "lastName":lastName,
         "email":email,
         "phoneNo":phoneNo,
         "password":password
       }
       $.ajax({
           type: "POST",
           url: "/aunthetication/register",
           contentType: "application/json",
           dataType: "json",
           data: JSON.stringify(regData)
       }).done(function(res) {
               if(res["status"] == 1){
                 //alert(res['message']);
                 afterSignUp();
               }else{
                 alert(res['message']);
               }
       });
   }else{
     alert("passwords dont match")
   }

   });

   $("#submitVerifyCode").on("click",function(){
       var verificationCode = {
          "verification_code":$("#vCodeInput").val()
       };

        $.ajax({
              type: "POST",
              url: "/aunthetication/verifyEmail",
              contentType: "application/json",
              dataType: "json",
              data: JSON.stringify(verificationCode)
          }).done(function(res) {
                  if(res["status"] == 1){
                    //alert(res['message']);
                    afterVerification();
                  }else{
                    alert(res['message']);
                  }
          });

   });

   $('#avatarFile').on('change',function (event) {
               var filePath = $(this).val();
               var filePath = URL.createObjectURL(event.target.files[0]);
               $("#avatarHolder").attr("src",filePath);
    });
   $("#submitProfile").on("click",function(){
          var username = $("#usernameInput").val();
          var files = $('#avatarFile').get(0).files;

           if(!username ){
              alert("input username !");
          }else if(files.length < 1){
              alert("Select Profile Image!");
          }else if(files.length > 0 && username){
             var formData = new FormData();
                for (var i = 0; i < files.length; i++) {
                  var file = files[i];
                  formData.append('avatar_file', file, file.name);
                }
                $loading.fadeIn();
                   $.ajax({
                      url: '/upload?username='+username,
                      type: 'POST',
                      data: formData,
                      processData: false,
                      contentType: false,
                      success: function(data){
                          afterProfile();
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

                            }

                          }

                        }, false);

                        return xhr;
                      }
                    });
          }
   });

function afterVerification(){
    setCookie("step",2,1);
    thisObj.goForward();
}
  function afterSignUp(){
    setCookie("step",1,1);
    thisObj.goForward();
  }
  function afterProfile(){
      setCookie("step",0,1);
       $loading.fadeOut();
       thisObj.goForward();
      window.location.href="/index";

    }

});


