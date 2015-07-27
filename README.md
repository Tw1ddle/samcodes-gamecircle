Haxe GameCircle
==============
Unofficial Amazon GameCircle 2.x leaderboards and achievements support for OpenFL Android target.

### Features ###

Supports:
* GameCircle sign-in.
* Showing leaderboards and achievement popover screens.
* Submitting leaderboard scores, unlocking and updating achievement progress.
* React to player login or logout with a custom listener.

Doesn't Support:
* iOS target.
* Whispersync.
* Fetching leaderboard/achievement/player data from the Amazon servers.

If there is something you would like adding let me know. Pull requests welcomed too! Here it is in action:

![Screenshot of it working](https://github.com/Tw1ddle/samcodes-gamecircle/blob/master/screenshots/screen1.png?raw=true "Screenshot")

### Install ###

```bash
haxelib install samcodes-gamecircle
```

### Usage ###

Project.xml

```xml
<haxelib name="samcodes-gamecircle" />

<!-- Add your GameCircle API key. Refer to the Amazon documentation:
https://developer.amazon.com/public/apis/engage/gamecircle/docs/create-a-gamecircle-configuration#Generate API Keys
-->
<template path="android/debug_gamecircleapikey.txt" rename="assets/api_key.txt" if="debug" />
<template path="android/release_gamecircleapikey.txt" rename="assets/api_key.txt" unless="debug" />
```

Example usage:

```haxe
// Your wrapper of the GameCircle leaderboards functionality
class GameCircleLeaderboards {
	public static var get(default, never):GameCircleLeaderboards = new GameCircleLeaderboards();
	
	private var leaderboards:GameCircle;
	
	private function new() {
		leaderboards = new GameCircle();
	}
	
	public function setListener(listener:GameCircleListener):Void {
		leaderboards.setListener(listener);
	}
	
	public function openLeaderboard(id:String):Void {
		leaderboards.games.showLeaderboard(id);
	}
	
	public function openAchievements():Void {
		leaderboards.games.showAchievements();
	}
	
	public function isSignedIn():Bool {
		return leaderboards.games.isSignedIn();
	}
	
	public function signIn():Void {
		leaderboards.games.showSignInPage();
	}
	
	public function submitScore(id:String, score:Int):Void {
		leaderboards.games.submitScore(id, score, "");
	}
	
	public function updateAchievementProgress(id:String, percent:Float):Void {
		leaderboards.games.updateAchievement(id, percent, "");
	}
	
	public function setPopUpLocation(location:PopUpLocation):Void {
		leaderboards.games.setPopUpLocation(location);
	}
}

// Your game
class MyGame {
	public var leaderboards:GameCircleLeaderboards;
	
	public function create():Void {
		leaderboards = GameCircleLeaderboards.get;
		leaderboards.setPopUpLocation(PopUpLocation.TOP_CENTER);
		
		if (!leaderboards.isSignedIn()) {
			leaderboards.signIn();
		}
	}
	
	public function onLevelEnd() {
  		if (leaderboards.isSignedIn()) {
  			// Create your leaderboards and achievements and set their ids through the Amazon developer console
  			leaderboards.submitScore("my_leaderboard_id", 1000);
			leaderboards.updateAchievementProgress("my_achievement_id", 50); // 50% complete
			leaderboards.updateAchievementProgress("my_other_achievement_id", 100); // Unlocks/100% complete
  		}
	}
}
```
