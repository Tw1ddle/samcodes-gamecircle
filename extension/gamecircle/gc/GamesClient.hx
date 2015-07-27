package extension.gamecircle.gc;

#if android

import openfl.utils.JNI;

class GamesClient {
	public function new() {
		initBindings();
	}

	public function isSignedIn():Bool {
		return is_signed_in();
	}

	public function updateAchievement(achievementId : String, percentComplete : Float, developerPayload : String):Void {
		update_achievement(achievementId, percentComplete, developerPayload);
	}

	public function showAchievements():Void {
		show_achievements();
	}

	public function submitScore(leaderboardId:String, score:Int, developerPayload:String):Void {
		submit_score(leaderboardId, score, developerPayload);
	}

	public function showLeaderboard(leaderboardId:String):Void {
		show_leaderboard(leaderboardId);
	}

	public function showLeaderboards():Void {
		show_leaderboards();
	}

	public function showSignInPage():Void {
		show_sign_in_page();
	}
	
	public function setPopUpLocation(location:PopUpLocation):Void {
		set_popup_location(location);
	}

	private static function initBindings():Void {
		var packageName:String = "com/samcodes/gamecircle/GameCircle";
		
		if (is_signed_in == null) {
			is_signed_in = openfl.utils.JNI.createStaticMethod(packageName, "isSignedIn", "()Z");
		}

		if(update_achievement == null) {
			update_achievement = openfl.utils.JNI.createStaticMethod(packageName, "updateAchievement", "(Ljava/lang/String;FLjava/lang/String;)V");
		}

		if(show_achievements == null) {
			show_achievements = openfl.utils.JNI.createStaticMethod(packageName, "showAchievements", "()V");
		}

		if(submit_score == null) {
			submit_score = openfl.utils.JNI.createStaticMethod(packageName, "submitScore", "(Ljava/lang/String;JLjava/lang/String;)V");
		}

		if(show_leaderboard == null) {
			show_leaderboard = openfl.utils.JNI.createStaticMethod(packageName, "showLeaderboard", "(Ljava/lang/String;)V");
		}

		if(show_leaderboards == null) {
			show_leaderboards = openfl.utils.JNI.createStaticMethod(packageName, "showLeaderboards", "()V");
		}

		if(show_sign_in_page == null) {
			show_sign_in_page = openfl.utils.JNI.createStaticMethod(packageName, "showSignInPage", "()V");
		}
		
		if(set_popup_location == null) {
			set_popup_location = openfl.utils.JNI.createStaticMethod(packageName, "setPopUpLocation", "(Ljava/lang/String;)V");
		}
	}

	private static var is_signed_in: Dynamic = null;
	private static var update_achievement: Dynamic = null;
	private static var show_achievements: Dynamic = null;
	private static var submit_score: Dynamic = null;
	private static var show_leaderboard: Dynamic = null;
	private static var show_leaderboards: Dynamic = null;
	private static var show_sign_in_page: Dynamic = null;
	private static var set_popup_location:Dynamic = null;
}

#end