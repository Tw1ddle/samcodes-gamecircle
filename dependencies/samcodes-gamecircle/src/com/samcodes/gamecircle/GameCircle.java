package com.samcodes.gamecircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.opengl.GLSurfaceView;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import org.haxe.lime.HaxeObject;
import org.haxe.extension.Extension;

import com.amazon.ags.api.AmazonGames;
import com.amazon.ags.api.AmazonGamesCallback;
import com.amazon.ags.api.AmazonGamesClient;
import com.amazon.ags.api.AmazonGamesStatus;
import com.amazon.ags.api.AmazonGamesFeature;
import com.amazon.ags.api.leaderboards.SubmitScoreResponse;
import com.amazon.ags.api.AGResponseCallback;
import com.amazon.ags.api.ErrorCode;
import com.amazon.ags.api.overlay.PopUpLocation;

import java.util.EnumSet;

public class GameCircle extends Extension {
	private static String tag = "SamcodesGameCircle";
	private static HaxeObject callback = null;
	private static AmazonGamesStatus gamesStatus = AmazonGamesStatus.INITIALIZING;
	private AmazonGamesClient agsClient = null;

	public GameCircle() {
		Log.d(tag, "Construct SamcodesGameCircle");
	}

	public static void start(HaxeObject haxeCallback) {
		Log.i(tag, "Starting GameCircle service");
		callback = haxeCallback;
		Log.i(tag, "GameCircle service started");
	}

	/**
	 * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when the activity had been stopped, but is now again being displayed to the user.
	 */
	public void onStart() {
		Log.i(tag, "Starting SamcodesGameCircle");
	}

	/**
	 * Called after {@link #onStop} when the current activity is being re-displayed to the user (the user has navigated back to it).
	 */
	public void onRestart() {
	}

	/**
	 * Called when the activity is no longer visible to the user, because another activity has been resumed and is covering this one.
	 */
	public void onStop() {
		Log.i(tag, "Stopping SamcodesGameCircle");
	}

	/**
	 * Called when the activity is starting.
	 */
	public void onCreate(Bundle savedInstanceState) {
	}

	/**
	 * Perform any final cleanup before an activity is destroyed.
	 */
	public void onDestroy() {
	}

	/**
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 */
	public void onPause() {
		super.onPause();

		if (agsClient != null) {
			agsClient.release();
		}
	}

	/**
	 * Called after {@link #onRestart}, or {@link #onPause}, for your activity to start interacting with the user.
	 */
	public void onResume() {
		gamesStatus = AmazonGamesStatus.INITIALIZING;

		AmazonGamesClient.initialize(this.mainActivity, new AmazonGamesCallback() {

			@Override
			public void onServiceReady(AmazonGamesClient amazonGamesClient) {
				agsClient = amazonGamesClient;
				agsClient.setPopUpLocation(PopUpLocation.TOP_CENTER);
				gamesStatus = AmazonGamesStatus.SERVICE_CONNECTED;
			}

			@Override
			public void onServiceNotReady(AmazonGamesStatus reason) {
				gamesStatus = reason;

				gamesClientError(reason.ordinal(), "onServiceNotReady");
			}

		}, EnumSet.of(AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards));
	}

	/**
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 */
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(tag, "onActivityResult");
		return true;
	}

	public static void handleException(Exception e, String where) {
		callHaxe("onException", new Object[] { e.getMessage(), where });
		Log.d(tag, "Exception at " + where + ": " + e.toString());
		e.printStackTrace();
	}

	public static void gamesClientError(int code, String where) {
		callHaxe("onError", new Object[] { "GAMES_CLIENT", code, where });
		Log.d(tag, "Error at " + where + " with code = " + code);
	}

	public static void callHaxe(final String name, final Object[] args) {
		if (callback != null) {
			callbackHandler.post(new Runnable() {
				public void run() {
					Log.d(tag, "Calling " + name + " from java");
					callback.call(name, args);
				}
			});
		}
	}

	public static boolean isSignedIn() {
		return gamesStatus == AmazonGamesStatus.SERVICE_CONNECTED;
	}

	public static boolean isAvailable() {
		return true;
	}

	public static void showAchievements() {
		callbackHandler.post(new Runnable() {
			public void run() {
				Log.v(tag, "GameCircle showAchievements");

				if (AmazonGamesClient.getInstance() != null) {
					AmazonGamesClient.getInstance().getAchievementsClient().showAchievementsOverlay();
				}
			}
		});
	}

	public static void showLeaderboard(final String leaderboardId) {
		callbackHandler.post(new Runnable() {
			public void run() {
				Log.v(tag, "GameCircle showLeaderboard " + leaderboardId);

				if (AmazonGamesClient.getInstance() != null) {
					AmazonGamesClient.getInstance().getLeaderboardsClient().showLeaderboardOverlay(leaderboardId);
				}
			}
		});
	}

	public static void showLeaderboards() {
		callbackHandler.post(new Runnable() {
			public void run() {
				Log.v(tag, "GameCircle showLeaderboards");

				if (AmazonGamesClient.getInstance() != null) {
					AmazonGamesClient.getInstance().getLeaderboardsClient().showLeaderboardsOverlay();
				}
			}
		});
	}
	
	public static void showSignInPage() {
		callbackHandler.post(new Runnable() {
			public void run() {
				Log.v(tag, "GameCircle showSignInPage");

				if (AmazonGamesClient.getInstance() != null) {
					AmazonGamesClient.getInstance().showSignInPage();
				}
			}
		});
	}

	public static void submitScore(final String leaderboardId, final long score, final String developerPayload) {
		callbackHandler.post(new Runnable() {
			public void run() {
				Log.v(tag, "GameCircle submitScore " + leaderboardId + ' ' + score);

				if (AmazonGamesClient.getInstance() != null) {
					AmazonGamesClient.getInstance().getLeaderboardsClient().submitScore(leaderboardId, score, developerPayload)
							.setCallback(new AGResponseCallback<SubmitScoreResponse>() {
								@Override
								public void onComplete(SubmitScoreResponse result) {
									if (result.isError()) {
										// Add optional error handling here. Not required since re-tries and on-device request caching are automatic
										// Add optional error handling here. Not required since re-tries and on-device request caching are automatic
										Log.v(tag, "GameCircle ERROR: " + result.getError());
									} else {
										Log.v(tag, "GameCircle OK");
										// Continue game flow
									}
								}
							});
				}
			}
		});
	}

	public static void updateAchievement(final String achievementId, final float percentComplete, final String developerPayload) {
		callbackHandler.post(new Runnable() {
			public void run() {
				Log.v(tag, "GameCircle updateAchievement " + achievementId);

				if (AmazonGamesClient.getInstance() != null) {
					AmazonGamesClient.getInstance().getAchievementsClient().updateProgress(achievementId, percentComplete, developerPayload);
				}
			}
		});
	}
}