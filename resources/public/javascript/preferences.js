/*global MT, jQuery
*/

var MT = MT || {};

(function ($) {

	var toggle_preferences_pane = function () {
		$("#preferences").toggleClass("open");
	},
	
	preference_changed = function () {
		MT.ajax_service.save_preference(this.name, this.checked);
	};

	MT.initialize_preferences = function () {
		$("#preferences h3").bind("click", toggle_preferences_pane);
		$("#preferences input:checkbox").bind("click", preference_changed);
	};

}(jQuery));