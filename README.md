Haxe GameCircle
==============
Amazon GameCircle 2.x leaderboards and achievements support for OpenFL Android target.

Supports:
* GameCircle sign-in.
* Showing leaderboards/achievements popover screen.
* Submitting leaderboard scores.
* Unlocking achievements, updating achievement progress.

Doesn't support:
* iOS target.
* Fetching leaderboard/achievement data from the Amazon servers.
* Whispersync.

If there is something you would like adding please get in touch. Pull requests welcomed too!

### Usage ###

Project.xml

```xml
<include path="lib/samcodesgamecircle/include.nmml" />

<!-- GameCircle jars -->
<java path="libs/gamecircle.jar" />
<java path="libs/AmazonInsights-android-sdk-2.1.26.jar" />
<java path="libs/login-with-amazon-sdk.jar" />

<!-- Add your GameCircle API key. Refer to the Amazon documentation:
https://developer.amazon.com/public/apis/engage/gamecircle/docs/create-a-gamecircle-configuration#Generate API Keys
-->
<template path="android/debug_gamecircleapikey.txt" rename="assets/api_key.txt" if="debug" />
<template path="android/release_gamecircleapikey.txt" rename="assets/api_key.txt" unless="debug" />
```

AndroidManifest.xml:

```xml
<!-- Add within the <application></application> tag in your Android manifest. Refer to the Amazon documentation:
https://developer.amazon.com/appsandservices/apis/engage/gamecircle/docs/initialize-android#Step 3. Update your AndroidManifest.xml File 
-->
<activity android:name="com.amazon.ags.html5.overlay.GameCircleUserInterface" android:theme="@style/GCOverlay"></activity>

<activity android:name="com.amazon.identity.auth.device.authorization.AuthorizationActivity" 
android:theme="@android:style/Theme.NoDisplay"
android:allowTaskReparenting="true"
android:launchMode="singleTask">
  <intent-filter>
	 <action android:name="android.intent.action.VIEW" />
	 <category android:name="android.intent.category.DEFAULT" />
	 <category android:name="android.intent.category.BROWSABLE" />
	 <data android:host="::APP_PACKAGE::" android:scheme="amzn" />
  </intent-filter>
</activity>

<activity android:name="com.amazon.ags.html5.overlay.GameCircleAlertUserInterface" android:theme="@style/GCAlert"></activity>

<receiver
  android:name="com.amazon.identity.auth.device.authorization.PackageIntentReceiver"
  android:enabled="true">
  <intent-filter>
	 <action android:name="android.intent.action.PACKAGE_INSTALL" />
	 <action android:name="android.intent.action.PACKAGE_ADDED" />
	 <data android:scheme="package" />
  </intent-filter>
  
</receiver>
```

Haxe example:

```haxe
// Example wrapper
class MyGameCircleLeaderboards {
  // Doesn't really need to be a singleton
	public static var get(default, never):MyGameCircleLeaderboards = new MyGameCircleLeaderboards();
	
	private var leaderboards:GameCircle;
	private var connectionHandler:MyGameCircleConnectionHandler;
	
	public function new() {
		connectionHandler = new MyGameCircleConnectionHandler();
		leaderboards = new GameCircle(connectionHandler);
	}
	
	public function showLeaderboard(id:String):Void {
		leaderboards.games.showLeaderboard(id);
	}
	
	public function showAchievements():Void {
		leaderboards.games.showAchievements();
	}
	
	public function isSignedIn():Bool {
		return leaderboards.games.isSignedIn();
	}
	
	public function showSignInPage():Void {
		leaderboards.games.showSignInPage();
	}
	
	public function submitScore(id:String, score:Int):Void {
		leaderboards.games.submitScore(id, score, "");
	}
	
	public function updateAchievementProgress(id:String, percent:Float):Void {
		leaderboards.games.updateAchievement(id, percent, "");
	}
}

// Your connection handler gets called when GameCircle stuff happens in Java
class MyGameCircleConnectionHandler extends ConnectionHandler {
  override public function onWarning(msg:String, where:String) {
  }

  override public function onError(what:String, code:Int, where:String) {
  }

  override public function onException(msg:String, where:String) {
  }

  override public function onConnectionEstablished(what:String) {
  }

  override public function onSignedOut(what:String) {
  }
}

// Your game code
class MyGame {
  public var leaderboards:MyGameCircleLeaderboards;
  
  public function setup():Void {
    leaderboards = MyGameCircleLeaderboards.get;
    
    if (!leaderboards.isSignedIn()) {
			leaderboards.showSignInPage();
	  }
  }
  
  public function onLevelEnd() {
		if (leaderboards.isSignedIn()) {
		  var myScore:Float = 10;
		  
		  // Create your leaderboards and set their ids through the Amazon developer console
			leaderboards.submitScore("my_leaderboard_id", myScore);
		}
		
		if (leaderboards.isSignedIn()) {
		  // Create your achievements and set their ids through the Amazon developer console
		  leaderboards.updateAchievementProgress("my_achievement_id", 50); // 50% complete
		  leaderboards.updateAchievementProgress("my_other_achievement_id", 100); // Unlocks automatically at 100%
		}
  }
}
```
