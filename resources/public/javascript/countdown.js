/*global jQuery, setTimeout, document */
(function ($) {
    $.fn.countdown = function (total_seconds, callback) {
        var that = this, 
            countdown_active = true,
            original_title = document.title,
            seconds_left = total_seconds,
            start_time;

        function now() {
            return new Date().getTime();
        }

        function calculate_seconds_left() {
            return Math.round((start_time - now()) / 1000) + total_seconds;
        }
        
        function minutes_left() {
            return Math.floor((seconds_left + 6) / 60);
        }

        function rounded_minutes_left() {
            return Math.floor((seconds_left + 40) / 60) + " min";
        }

        function tenth_of_seconds_left() {
            return Math.floor((seconds_left - 60 * minutes_left() + 6) / 10) + "0";
        }

        function rounded_seconds_left() {
            return minutes_left() + ":" + tenth_of_seconds_left();
        }

        function time_left() {
            if (seconds_left > 90) {
                return rounded_minutes_left();
            } else if (seconds_left > 15) {
                return rounded_seconds_left();
            } else {
                return "0:" + (seconds_left > 9 ? seconds_left : "0" + seconds_left);
            }
        }

        function update_display() {
            var display_text = time_left();
            if (that.text() !== display_text) {
                that.text(display_text);
                document.title = display_text + " - " + original_title;
            }
        }

        function count_down() {
            if (! countdown_active) {
                if (original_title) {
					document.title = original_title;
				}
                return;
            }
            seconds_left = calculate_seconds_left();
            update_display();
            if (seconds_left > 0) {
                setTimeout(count_down, 1000);
            } else {
                document.title = original_title;
                callback();
            }
        }
        
        if ($.cancel_countdown) {
            $.cancel_countdown();
        }
        $.cancel_countdown = function () {
            countdown_active = false;
            document.title = original_title;
			original_title = false;
        };

        start_time = now();
        update_display();
        setTimeout(count_down, 1000);
    };
    
})(jQuery);