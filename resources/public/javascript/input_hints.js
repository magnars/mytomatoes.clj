/*global jQuery
*/
(function ($) {
    var add_hint_to = {};

    add_hint_to.text = function (field, hint) {
        field = $(field);
        function show_hint() {
            if (field.val() === "" || field.val() === hint) {
                field.val(hint).addClass("hint");
            }
        }
        function hide_hint() {
            if (field.val() === hint) {
                field.val("").removeClass("hint");
            }
        }
        field.blur(show_hint).focus(hide_hint).each(show_hint);
    };

    add_hint_to.password = function (field, hint) {
        var real = $(field), dummy = $("<input type='text' />").insertBefore(real).val(hint).addClass("hint");
        function show_hint() {
            if (real.val() === "") {
                real.hide();
                dummy.show();
            }
        }
        function hide_hint() {
            dummy.hide();
            real.show().focus();
        }
        real.data("hinted-password-field", true);
        real.blur(show_hint);
        dummy.focus(hide_hint);
        show_hint();
    };

    $.fn.hide_password_field = function () {
        this.hide().prev().hide();
    };

    $.fn.show_password_field = function () {
        var real = this, dummy = real.prev();
        if (real.val() === "") {
            dummy.show();
        } else {
            real.show();
        }
    };

    $.fn.add_hint = function (hint) {
        this.filter("input").each(function () {
            add_hint_to[this.type](this, hint);
        });
    };
    
})(jQuery);