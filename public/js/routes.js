$(window).load(function() {

    $("#loginForm").submit(function(e){
         e.preventDefault();

         var loginData = {
            email:$("#email").val(),
            password:$("#password").val()
         }

         console.log(loginData)

       $.ajax({
           type: "POST",
           url: "/aunthetication/login",
           contentType: "application/json",
           dataType: "json",
           data: JSON.stringify(loginData)
       }).done(function(res) {
               if(res["status"] == 1){
                  setCookieData(res["data"]);
                  window.location.href="/generateTimetable";
               }else{
                 alert(res['message']);
               }
       });

      });

     function setCookieData(data){
        setCookie("user_id",data["user_id"],1);
        setCookie("username",data["username"],1);
        setCookie("profile_photo",data["profile_photo"],1);
        setCookie("user_role",data["user_role"],1);
        setCookie("regNo",data["regNo"],1);
        setCookie("initial",data["initial"],1);
        setCookie("dept",data["dept"],1);
     }

});
