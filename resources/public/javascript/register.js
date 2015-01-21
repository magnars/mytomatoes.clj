/*global jQuery, location */
(function ($) {

    function switch_to_login_form() {
        $("#welcome form").attr("action", "login");
        $("#welcome h3").text("login");
        $("#welcome #password2").hide_password_field();
        $("#welcome #toggle_register_login").text("new here?").blur();
        $("#welcome #username").focus().select();
    }

    function switch_to_register_form() {
        $("#welcome form").attr("action", "register");
        $("#welcome h3").text("register");
        $("#welcome #password2").show_password_field();
        $("#welcome #toggle_register_login").text("already registered?").blur();
    }

    function toggle_register_login() {
        var current = $("#welcome form").attr("action");
        if (current === "register") {
            switch_to_login_form();
        } else {
            switch_to_register_form();
        }
        $("#welcome form").find(".validation_error, .double_error").remove();
        return false;
    }

    function username() {
        return $("#welcome #username").val();
    }

    function logged_in() {
        location.href = location.href.replace('?session=expired', '');
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

    function current_action() {
        return $("#welcome h3").text();
    }

    var wrong_password_before = false;

    function error_when_logging_in(json) {
        switch (json.result) {
        case "unavailable_username":
            $("#username").show_validation_error("sorry, that username is unavailable").focus().select();
            return true;
        case "unknown_username":
            $("#username").show_validation_error("sorry, this username is new to me").focus().select();
            return true;
        case "missing_username":
            $("#username").show_validation_error("you need a username to " + current_action()).focus();
            return true;
        case "wrong_password":
            if (wrong_password_before) {
                $("#password").show_validation_error("that's not right either - <a href='/recovery?username=" + $("#username").val() + "'>need help?</a>").focus().select();
            } else {
                wrong_password_before = true;
                $("#password").show_validation_error("sorry, that's not the right password").focus().select();
            }
            return true;
        case "missing_password":
            $("#password").show_validation_error("you need a password to " + current_action()).prev().trigger("focus").next().focus();
            return true;
        case "mismatched_passwords":
            show_mismatched_password_validation();
            return true;
        default:
            return false;
        }
    }

    function submit_register_login_form(event) {
        var form = $(this);
        event.preventDefault();
        $.postJSON("actions/" + form.attr("action"), form.serializeArray(), logged_in, error_when_logging_in);
        return false;
    }

    function should_show_login_form_as_default() {
        return $("#session_expired").exists() || username() !== "username";
    }

    function initialize() {
        $("#username").add_hint("username");
        $("#password").add_hint("password");
        $("#password2").add_hint("password again");
        $("#toggle_register_login").click(toggle_register_login);
        $("#welcome form").submit(submit_register_login_form);
        if (should_show_login_form_as_default()) {
            switch_to_login_form();
        }
        $("#submit").val("let's start").attr("disabled", "");
    }

    initialize();

})(jQuery);