/*global jQuery */
(function ($) {

    $.fn.highlight = function () {
        var original_color = this.css("background-color");
        $(this).css("background-color", "#ffcb6e").animate({backgroundColor: original_color}, 1000);
    };

    $.fn.flash = function (message) {
        var original_color = this.css("color");
        this.css("color", "#ffcb6e").text(message).animate({color: original_color}, 3000);
    };

    $.fn.flash_background = function (color) {
        var that = this, original_color = this.css("background-color"), fade_to_new, fade_to_original;

        fade_to_original = function () {
            that.animate({backgroundColor: original_color}, 500, fade_to_new);
        };

        fade_to_new = function () {
            that.animate({backgroundColor: color}, 500, fade_to_original);
        };

        fade_to_new();
    };

})(jQuery);