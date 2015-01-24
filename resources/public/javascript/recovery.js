/*global jQuery */

var MT = this.MT || {};

(function ($) {

    var invalidWord = function (s) {
        s = $.trim(s).toLowerCase();
        if (s.toLocaleLowerCase) { s = s.toLocaleLowerCase(); }

        if (s.length === 0) { return "we need all four words for the test"; }
        if (s.length === 1) { return "that's too short for our purposes"; }
        if (MT.commonWords[s]) { return "sorry, but this word is too common"; }
    };

    var validateWord = function (input) {
        var msg = invalidWord(input.value);
        if (msg) {
            $(input).show_validation_error(msg).focus();
            return false;
        }
        return true;
    };

    $("#fields .word").blur(function () {
        validateWord(this);
    });

    var validateUsername = function (input) {
        if ($.trim(input.value).length === 0) {
            $(input).show_validation_error("your old username goes here, okay?").focus();
            return false;
        }
        return true;
    };

    var byId = function (id) { return document.getElementById(id); };

    var bigSuccess = function (json) {
        window.location = json.url;
    };

    var failure = function (json) {
        switch (json.result) {
        case "already_logged_in": window.location = "/"; return true;
        case "unknown_username": return $("#username").show_validation_error("sorry, this username is new to me").focus().select();
        case "missing_username": return $("#username").show_validation_error("you need a username to do this").focus();
        case "missing_word1": return $("#word1").show_validation_error("we need all four words for the test").focus();
        case "missing_word2": return $("#word2").show_validation_error("we need all four words for the test").focus();
        case "missing_word3": return $("#word3").show_validation_error("we need all four words for the test").focus();
        case "missing_word4": return $("#word4").show_validation_error("we need all four words for the test").focus();
        case "duplicate_words": alert("We need four different words to make this work."); return true;
        case "too_common_words": alert("Some of the words you used are too common."); return true;
        case "not_enough_matches": alert("I'm sorry, but it doesn't look\nlike you've used all of these\nwords in your tomatoes."); return true;
        case "missed_word1": return $("#word1").show_validation_error("almost there, but this one isn't right").focus();
        case "missed_word2": return $("#word2").show_validation_error("almost there, but this one isn't right").focus();
        case "missed_word3": return $("#word3").show_validation_error("almost there, but this one isn't right").focus();
        case "missed_word4": return $("#word4").show_validation_error("almost there, but this one isn't right").focus();
        case "no_matches":
            alert("From what I can tell, you haven't used\nany of these words in your tomatoes.\n\nAre you sure this is the right username?");
            $("#username").show_validation_error("could you have used another name?").focus().select();
            return true;
        default: return false;
        }
    };

    var validateDuplicate = function (input, other) {
        var s1 = $.trim(input.value);
        var s2 = $.trim(other.value);

        if (s1 === s2) {
            $(input).show_validation_error("you already wrote this word").focus();
            return false;
        }
        return true;
    };

    $("#the-form").submit(function (e) {
        e.preventDefault();

        if (!validateUsername(byId("username"))) return false;
        if (!validateWord(byId("word1"))) return false;
        if (!validateWord(byId("word2"))) return false;
        if (!validateWord(byId("word3"))) return false;
        if (!validateWord(byId("word4"))) return false;

        if (!validateDuplicate(byId("word2"), byId("word1"))) return false;
        if (!validateDuplicate(byId("word3"), byId("word2"))) return false;
        if (!validateDuplicate(byId("word3"), byId("word1"))) return false;
        if (!validateDuplicate(byId("word4"), byId("word1"))) return false;
        if (!validateDuplicate(byId("word4"), byId("word2"))) return false;
        if (!validateDuplicate(byId("word4"), byId("word3"))) return false;

        $.postJSON("/actions/check-my-words", {
            username: byId("username").value,
            word1: byId("word1").value,
            word2: byId("word2").value,
            word3: byId("word3").value,
            word4: byId("word4").value
        }, bigSuccess, failure);
    });

    $("#submit").val("try these").attr("disabled", false);

}(jQuery));
