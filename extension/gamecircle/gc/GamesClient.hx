package extension.gamecircle.gc;

import openfl.utils.JNI;

#if android

class GamesClientImpl
{
	public function new(handler:ConnectionHandler) {
		initJNI();
	}

	public function isSignedIn():Bool {
		return _isSignedIn();
	}

	public function updateAchievement(achievementId : String, percentComplete : Float, developerPayload : String):Void {
		_updateAchievement(achievementId, percentComplete, developerPayload);
	}

	public function showAchievements():Void {
		_showAchievements();
	}

	public function submitScore(leaderboardId:String, score:Int, developerPayload:String):Void {
		_submitScore(leaderboardId, score, developerPayload);
	}

	public function showLeaderboard(leaderboardId:String):Void {
		_showLeaderboard(leaderboardId);
	}

	public function showLeaderboards():Void {
		_showLeaderboards();
	}

	public function showSignInPage():Void {
		_showSignInPage();
	}

	private static function initJNI():Void {
		if (_isSignedIn == null) {
			_isSignedIn = openfl.utils.JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "isSignedIn", "()Z");
		}

		if(_updateAchievement == null) {
		  _updateAchievement = openfl.utils.JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "updateAchievement", "(Ljava/lang/String;FLjava/lang/String;)V");
		}

		if(_showAchievements == null) {
		  _showAchievements = openfl.utils.JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "showAchievements", "()V");
		}

		if(_submitScore == null) {
		  _submitScore = openfl.utils.JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "submitScore", "(Ljava/lang/String;JLjava/lang/String;)V");
		}

		if(_showLeaderboard == null) {
		  _showLeaderboard = openfl.utils.JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "showLeaderboard", "(Ljava/lang/String;)V");
		}

		if(_showLeaderboards == null) {
		  _showLeaderboards = openfl.utils.JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "showLeaderboards", "()V");
		}

		if (_showSignInPage == null) {
		  _showSignInPage = openfl.utils.JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "showSignInPage", "()V");
		}
	}

	private static var _isSignedIn : Dynamic = null;
	private static var _updateAchievement : Dynamic = null;
	private static var _showAchievements : Dynamic = null;
	private static var _submitScore : Dynamic = null;
	private static var _showLeaderboard : Dynamic = null;
	private static var _showLeaderboards : Dynamic = null;
	private static var _showSignInPage : Dynamic = null;
}

typedef GamesClient = GamesClientImpl;
#else
class GamesClientFallback
{
	private var _handler:ConnectionHandler;
	private var _isSignedIn:Bool;

	public function new(handler:ConnectionHandler) {
		_handler = handler;
		_isSignedIn = false;
	}
	
	public function isSignedIn():Void {
		return _isSignedIn;
	}
	
	public function updateAchievement(achievementId:String, steps:Int):Void {
		trace(["Not implemented", "updateAchievement", achievementId, steps]);
	}
	
	public function showAchievements() {
		trace(["Not implemented", "showAchievements"]);
	}
	
	public function submitScore(leaderboardId:String, score:Int):Void {
		trace(["Not implemented", "submitScore", leaderboardId, score]);
	}
	
	public function showLeaderboard(leaderboardId:String):Void {
		trace(["Not implemented", "showLeaderboard", leaderboardId]);
	}
	
	public function showLeaderboards():Void {
		trace(["Not implemented", "showLeaderboards"]);
	}
}

typedef GamesClient = GamesClientFallback;
#end