/*global jQuery, MT, shortcut, location, setTimeout, confirm, document, window */
(function ($) {
    var sound_player, enter_pressed_event, current_tomato, change_to_state = {}, current_state, current_tutorial_step;

    function minutes(min) {
        return min * (MT.debug ? 0.2 : 60);
    }

    function twentyfive_minutes() {
        return minutes(25);
    }

    function five_minutes() {
        return minutes(5);
    }

    function numbering(num) {
        if (num < 4) {
            return ["", "first", "second", "third"][num];
        } else {
            return num + "th";
        }
    }

    function show_longer_break_options() {
        $(this).blur();
        $("#longer_break").toggleClass("longer_break_closed longer_break_open");
        return false;
    }

    function change_to_this_break_length() {
        var $anchor = $(this), length = $anchor.text() * 1;
        $.cancel_countdown();
        $("#break_left").countdown(minutes(length), change_to_state.break_over);
        $("#longer_break").fadeOut();
        return false;
    }

    function flash_until_click(color, callback) {
        $("body").
            css("cursor", "pointer").
            one("click", function () {
                $("body").css("cursor", "auto");
                $("#states").stop().css("background-color", "#fff");
                callback();
            });
        $("#states").flash_background(color);
    }

    function click_body() {
        $("body").click().unbind("click");
    }

    function now() {
        return new Date();
    }

    function todays_tomatoes() {
        return $("#done #today").next();
    }

    function num_tomatoes_today() {
        return todays_tomatoes().find("li").length;
    }

    function escape(s) {
        return s.replace(/</g, "&lt;");
    }

    function tomato() {
        var me = {
            start_time: now(),
            number: num_tomatoes_today() + 1
        };
        me.generic_description = "tomato #" + me.number + " finished";
        me.european_interval = function () {
            return this.start_time.toClock() + " - " + this.end_time.toClock();
        };
        me.american_interval = function () {
            return this.start_time.to12hrClock() + " - " + this.end_time.to12hrClock();
        };
        me.html = function () {
            return [
                "<li>",
                "<span class='eurotime'>",
                this.european_interval(),
                "</span> ",
                "<span class='ameritime'>",
                this.american_interval(),
                "</span> ",
                this.description ? escape(this.description) : this.generic_description,
                "</li>"
            ].join("");
        };
        me.parameters = function () {
            return {
                start_time: this.start_time.toTimestamp(),
                end_time: this.end_time.toTimestamp(),
                description: this.description
            };
        };
        me.save = function () {
            $.postJSON("/actions/complete_tomato", this.parameters());
        };
        return me;
    }

    function cancel_tomato() {
        if (confirm("Really squash tomato?")) {
            $.cancel_countdown();
            change_to_state.waiting();
            if (sound_player.supports_ticking) {
                sound_player.stop_ticking();
            }
            $("#flash_message").flash("tomato squashed");
        }
        return false;
    }

    function maybe_toggle_ticking() {
        if (current_state === "#working" && sound_player.supports_ticking) {
            if (this.checked) {
                sound_player.start_ticking();
            } else {
                sound_player.stop_ticking();
            }
        }
    }

    function toggle_clock_types() {
        if (this.checked) {
            $("div.european_clock").addClass("american_clock").removeClass("european_clock");
        } else {
            $("div.american_clock").addClass("european_clock").removeClass("american_clock");
        }
    }

    function disable_ticking_preference() {
        $("#ticking_preference input:checkbox").
            attr("disabled", true).
            closest("li").addClass("disabled").
            find(".note").html("<span>I'm terribly sorry, but ticking is not supported in this browser. <a href='http://www.google.com/chrome'>Try Chrome</a></span>");
    }

    function update_todays_tomato_counter() {
        var num = num_tomatoes_today(), plural = num > 1 ? "es" : "";
        $("#today span").text(num + " finished tomato" + plural);
    }

    function add_todays_list_if_first() {
        if (! $("#done #today").exists()) {
            $("<h3 id='today'><strong>today</strong> <span></span></h3><ul></ul>").prependTo("#done > div").data("date", MT.today());
        }
    }

    function complete_current_tomato() {
        current_tomato.description = $("#enter_description input").val();
        current_tomato.save();
        add_todays_list_if_first();
        $(current_tomato.html()).prependTo(todays_tomatoes()).highlight();
        update_todays_tomato_counter();
        change_to_state.on_a_break();
        return false;
    }

    function handle_enter_pressed() {
        if (enter_pressed_event) {
            enter_pressed_event();
            return false;
        }
    }

    function handle_enter_pressed_in_description_form() {
        if (current_state === "#enter_description") {
            complete_current_tomato();
        } else {
            handle_enter_pressed();
        }
        return false;
    }

    function keep_session_alive_while_working() {
        if (current_state === "#working" || current_state === "#stop_working") {
            $.postJSON("/actions/keep_session_alive", {x: "y"});
            setTimeout(keep_session_alive_while_working, 5 * 60 * 1000);
        }
    }

    function maybe_confirm_leaving_page() {
        switch (current_state) {
        case "#working":
            return "You are in the middle of a tomato.";
        case "#stop_working":
        case "#enter_description":
            return "Your finished tomato will only be saved if you enter a description.";
        }
    }

    function been_through_tutorial() {
        return current_tutorial_step === "#break_over";
    }

    function finish_tutorial() {
        $("#break_over_tutorial").fadeTo(1000, "0.3", function () {
            $("#tutorial").slideUp(2000, function () {
                $(this).remove();
            });
        });
        $.postJSON("/actions/set_preference", {name: "hide_tutorial"});
    }

    function next_tutorial_step() {
        $(current_tutorial_step + "_tutorial").fadeTo(1000, "0.3");
        current_tutorial_step = current_state;
    }

    function start_tutorial() {
        current_tutorial_step = "#waiting";
        next_tutorial_step("#working");
    }

    function start_or_finish_tutorial() {
        if (been_through_tutorial()) {
            finish_tutorial();
        } else {
            start_tutorial();
        }
    }

    function progress_tutorial() {
        if (current_state === "#waiting") {
            // do nothing
        } else if (current_state === "#working") {
            start_or_finish_tutorial();
        } else {
            next_tutorial_step();
        }
    }

    function change_state_to(state) {
        current_state = state;
        $("#states li").hide().filter(state).show();
        if ($("#tutorial").exists()) {
            progress_tutorial();
        }
    }

    /* states */

    change_to_state.working = function () {
        sound_player.load_audio();
        change_state_to("#working");
        current_tomato = tomato();
        $("#time_left").countdown(twentyfive_minutes(), change_to_state.stop_working);
        MT.make_sure_that_today_is_still_today();
        keep_session_alive_while_working();
        enter_pressed_event = false;
        if (sound_player.supports_ticking && $("#ticking_preference input:checkbox").is(":checked")) {
            sound_player.start_ticking();
        }
        return false;
    };

    change_to_state.stop_working = function () {
        change_state_to("#stop_working");
        if (sound_player.supports_ticking) {
            sound_player.stop_ticking();
        }
        sound_player.play_alarm();
        document.title = "break! - mytomatoes.com";
        flash_until_click("#ffcb6e", function () {
            sound_player.stop_alarm();
            current_tomato.end_time = now();
            document.title = "mytomatoes.com";
            change_to_state.enter_description();
        });
        enter_pressed_event = click_body;
    };

    change_to_state.enter_description = function () {
        change_state_to("#enter_description");
        $("#congratulations span").text(numbering(current_tomato.number));
        $("#enter_description input").focus().select();
        enter_pressed_event = complete_current_tomato;
    };

    change_to_state.on_a_break = function () {
        change_state_to("#on_a_break");
        $("#states").css("background-color", "#ffd");
        $("#longer_break").addClass("longer_break_closed").removeClass("longer_break_open").show();
        $("#break_left").countdown(five_minutes(), change_to_state.break_over);
        MT.make_sure_that_today_is_still_today();
        enter_pressed_event = false;
        return false;
    };

    change_to_state.break_over = function () {
        change_state_to("#break_over");
        sound_player.play_alarm();
        flash_until_click("#fff", function () {
            sound_player.stop_alarm();
            change_to_state.waiting();
        });
        MT.make_sure_that_today_is_still_today();
        enter_pressed_event = click_body;
    };

    change_to_state.waiting = function () {
        change_state_to("#waiting");
        MT.make_sure_that_today_is_still_today();
        enter_pressed_event = change_to_state.working;
    };

    MT.initialize_index = function () {
        MT.fix_day_names();
        $("#waiting a").click(change_to_state.working);
        $("#cancel a, #void a").click(cancel_tomato);
        shortcut.add("enter", handle_enter_pressed);
        $("#enter_description form").submit(handle_enter_pressed_in_description_form);
        $("#toggle_longer_break").click(show_longer_break_options);
        $("#longer_break span a").click(change_to_this_break_length);
        $("#ticking_preference input:checkbox").click(maybe_toggle_ticking);
        $("#clock_preference input:checkbox").click(toggle_clock_types);
        change_to_state.waiting();
        window.onbeforeunload = maybe_confirm_leaving_page;
        sound_player = MT.sound_player.create();
        if (!sound_player.supports_ticking) {
            disable_ticking_preference();
        }

        $("#hide_banner").click(function () {
            $.postJSON("/actions/set_preference", {name: "hide_banner_" + $(this).attr("data-id")});
            $("#banner").remove();
        });
    };

})(jQuery);
