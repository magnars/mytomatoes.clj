/*global Audio, AC_FL_RunContent, window, document, navigator */
var MT = MT || {};

(function () {

    var dummy_swf = {
        playSound: function () {},
        stopSound: function () {}
    };

    var get_mp3player_swf = function () {
        var player;
        if (navigator.appName.indexOf("Microsoft") !== -1) {
            player = window.player;
        } else {
            player = document.player;
        }
        return player || dummy_swf;
    };

    var expand_audio = function (audio) {
        return {
            with_loop: function (other) {
                audio.stop = function () {
                    this.currentTime = 0;
                    this.pause();
                };
                audio.addEventListener("ended", function () {
                    this.stop();
                    other.play();
                }, false);
            }
        };
    };

    MT.sound_player = {

        audio_tag_supported: function () {
            return window.Audio && !!(new Audio().canPlayType);
        },

        create: function () {
            return this.audio_tag_supported() ? this.create_audio_player() : this.create_flash_player();
        },

        create_flash_player: function () {
            var create_flash_object = AC_FL_RunContent; // silence jslint
            create_flash_object('codebase', 'http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0', 'name', 'player', 'width', '1', 'height', '1', 'title', 'mp3player', 'id', 'player', 'src', 'mp3player', 'quality', 'high', 'pluginspage', 'http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash', 'movie', 'mp3player');
            return {
                type: "flash",
                swf: get_mp3player_swf(),
                load_audio: function () {
                },
                play_alarm: function () {
                    this.swf.playSound("sounds/alarm.mp3");
                },
                stop_alarm: function () {
                    this.swf.stopSound();
                },
                supports_ticking: false
            };
        },

        get_audio_elements: function () {
            return {
                alarm: document.getElementById("alarm_audio"),
                ticking_1: document.getElementById("ticking_audio_1"),
                ticking_2: document.getElementById("ticking_audio_2")
            };
        },

        create_audio_player: function () {
            var elements = this.get_audio_elements();

            expand_audio(elements.ticking_1).with_loop(elements.ticking_2);
            expand_audio(elements.ticking_2).with_loop(elements.ticking_1);

            return {
                type: "audio",
                load_audio: function () {
                    if (!elements.alarm.readyState) {
                        elements.alarm.load();
                    }
                    if (!elements.ticking_1.readyState) {
                        elements.ticking_1.load();
                    }
                    if (!elements.ticking_2.readyState) {
                        elements.ticking_2.load();
                    }
                },
                play_alarm: function () {
                    if (elements.alarm.readyState) {
                        elements.alarm.currentTime = 0;
                        elements.alarm.play();
                    }
                },
                stop_alarm: function () {
                    if (elements.alarm.readyState) {
                        elements.alarm.pause();
                    }
                },
                supports_ticking: true,
                start_ticking: function () {
                    if (elements.ticking_1.readyState && elements.ticking_2.readyState) {
                        elements.ticking_1.play();
                    }
                },
                stop_ticking: function () {
                    if (elements.ticking_1.readyState && elements.ticking_2.readyState) {
                        elements.ticking_1.stop();
                        elements.ticking_2.stop();
                    }
                }
            };
        }
    };
}());