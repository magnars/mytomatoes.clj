/*global jQuery */

var MT = this.MT || {};

(function ($) {

    function big_success() {
        location.href = "/";
    }

    function show_mismatched_password_validation() {
        var error = $("<div class='double_error'>these aren't equal (they should be)</div>").insertAfter("#password").hide().fadeIn(500);
        if ($.browser.opera) {
            error.css("margin-top", "5px");
        }
        $("#password, #password2").keyup(function () {
            var p1 = $("#password").val(), p2 = $("#password2").val();
            if (p1 === p2) {
                error.fadeOut(500, function () {
                    error.remove();
                });
                $("#password, #password2").unbind("keyup");
            }
        });
    }

    function failure(json) {
        switch (json.result) {
        case "wrong_code":
            location.href = "/recovery?code=invalid";
            return true;
        case "missing_password":
            $("#password").show_validation_error("write your new password here").prev().trigger("focus").next().focus();
            return true;
        case "mismatched_passwords":
            show_mismatched_password_validation();
            return true;
        default:
            return false;
        }
    }

    function submit_form(event) {
        var form = $(this);
        event.preventDefault();
        $.postJSON("actions/change-password", form.serializeArray(), big_success, failure);
        return false;
    }

    function initialize() {
        $("#password").add_hint("password");
        $("#password2").add_hint("password again");
        $("#welcome form").submit(submit_form);
        $("#submit").val("this one I'll remember").attr("disabled", "");
    }

    initialize();

}(jQuery));


