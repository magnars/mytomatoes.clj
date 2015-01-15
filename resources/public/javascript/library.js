/*global MT, jQuery, location, alert
*/
var MT = MT || {};

(function ($) {
    MT.debug = $.url.param("debug") || $.browser.mozilla && location.host === "local.mytomatoes.com:3001";

    function today() {
        return new Date().moveToMidnight();
    }
    
    MT.today = today;

    function yesterday() {
        return today().addDays(-1);
    }
    
    function fix_day_name() {
        var header = $(this),
            day = $(this).find("strong"),
            date = Date.parseExact(day.text(), "yyyy-MM-dd");
        if (date === null) {
            return; // do nothing
        } else if (date.equals(today())) {
            header.attr("id", "today");
            day.text("today");
        } else if (date.equals(yesterday())) {
            day.text("yesterday");
        } else {
            day.text(date.toString("dddd dd. MMM").toLowerCase());
        }
        header.data("date", date);
    }
    
    MT.fix_day_names = function () {
        $("#done h3").each(fix_day_name);
    };

    MT.reload_done_div = function () {
        $("#done").load("views/completed_tomatoes", MT.fix_day_names);
    };
    
    MT.make_sure_that_today_is_still_today = function () {
        if ($("#today").exists() && ! $("#today").data("date").equals(today())) {
            MT.reload_done_div();
        }
    };
        
    $.postJSON = function (url, parameters, callback, error_callback) {
        var handle_error = function (err) {
            if (error_callback && error_callback(err)) {
                // handled by callback, do nothing
            } else if (MT.debug) {
                alert("ERROR");
            } else {
                location.href = 'error';
            }
        };
        $.ajax({
            data: parameters,
            type: "POST",
            url: url,
            timeout: 20000,
            dataType: 'json',
            error: handle_error,
            success: function (json) {
                if (json.result === "ok") {
                    if (callback) {
                        callback(json);
                    }
                } else if (json.result === "not_logged_in") {
                    location.href = location.href + "?session=expired";
                } else {
                    handle_error(json);
                }
            }
        });
    };
    
    $.fn.exists = function () {
        return this.length > 0;
    };
    
    Date.prototype.toTimestamp = function () {
        return this.toString("yyyy-MM-dd HH:mm:ss"); 
    };

    Date.prototype.toClock = function () {
        return this.toString("HH:mm");
    };
    
    Date.prototype.to12hrClock = function () {
        return this.toString("hh:mm tt");
    };
    
    Date.prototype.moveToMidnight = function () { 
        return Date.parseExact(this.toString("yyyy-MM-dd"), "yyyy-MM-dd");
    };
  
})(jQuery);