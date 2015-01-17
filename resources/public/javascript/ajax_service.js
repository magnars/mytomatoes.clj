/*global jQuery */

var MT = MT || {};

(function ($) {

    MT.ajax_service = {

        save_preference: function (name, value) {
            this.contact_server("actions/set_preference", {name: name, value: value});
        },

        contact_server: function (url_stub, params) {
            $.ajax({
                type: "POST",
                url: "/" + url_stub,
                data: params,
                dataType: "json"
            });
        }
    };


}(jQuery));