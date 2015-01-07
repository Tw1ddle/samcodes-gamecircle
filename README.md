HaxeGameCircle
==============
Amazon GameCircle 2.x leaderboards and achievements support for OpenFL.

Supports:
* GameCircle sign-in.
* Showing leaderboards/achievements interface.
* Submitting leaderboard/achievement scores and progress.

Doesn't support:
* Fetching leaderboard/achievement stats from the server.
* Cloud syncing or Whispersync.

### Usage ###

Project.xml

```xml
<!-- Include the lib -->
<include path="lib/samcodesgamecircle/include.nmml" />

<!-- Include GameCircle jars -->
<java path="libs/gamecircle.jar" />
<java path="libs/AmazonInsights-android-sdk-2.1.26.jar" />
<java path="libs/login-with-amazon-sdk.jar" />

<!-- Add your GameCircle API key. Refer to the Amazon documentation: https://developer.amazon.com/public/apis/engage/gamecircle/docs/create-a-gamecircle-configuration#Generate API Keys -->
<template path="android/debug_gamecircleapikey.txt" rename="assets/api_key.txt" if="debug" />
<template path="android/release_gamecircleapikey.txt" rename="assets/api_key.txt" unless="debug" />
```

Example AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="::APP_PACKAGE::" android:versionCode="::APP_BUILD_NUMBER::" android:versionName="::APP_VERSION::" android:installLocation="::ANDROID_INSTALL_LOCATION::">
	
	<uses-feature android:glEsVersion="0x00020000" android:required="true" />
	
	<uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.VIBRATE" />
	
	<uses-sdk android:minSdkVersion="::ANDROID_MINIMUM_SDK_VERSION::" android:targetSdkVersion="::ANDROID_TARGET_SDK_VERSION::"/>
	
	<application android:label="::APP_TITLE::" android:debuggable="::DEBUG::"::if (HAS_ICON):: android:icon="@drawable/icon"::end::>
		
		<activity android:name="MainActivity" android:launchMode="singleTask" android:label="::APP_TITLE::" android:configChanges="keyboard|keyboardHidden|orientation|screenSize" android:screenOrientation="sensorLandscape">
			
			<intent-filter>
				
				<action android:name="android.intent.action.MAIN" />
				<category android:name="android.intent.category.LAUNCHER" />
				<category android:name="tv.ouya.intent.category.GAME" />
				
			</intent-filter>
			
		</activity>
		
		<!-- These are the bits needed for GameCircle. Refer to the Amazon documentation: https://developer.amazon.com/appsandservices/apis/engage/gamecircle/docs/initialize-android#Step 3. Update your AndroidManifest.xml File -->
		
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
		
	</application>
	
</manifest>
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

// Your connection handler receives events from Java when GameCircle stuff happens
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
		  var myProgress:Float = 50; // 50% complete
		  
		  // Create your achievements and set their ids through the Amazon developer console
		  // Unlocked achievements aren't affected by this, so no need for complicated client-side tracking
		  leaderboards.updateAchievementProgress("my_achievement_id", myProgress);
		}
  }
}
```
