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
import com.amazon.ags.api.player.AGSignedInListener;

import java.util.EnumSet;

public class GameCircle extends Extension {
	private static final String tag = "SamcodesGameCircle";
	private static HaxeObject callback = null;
	private static AmazonGamesStatus gamesStatus = AmazonGamesStatus.INITIALIZING;
	private AmazonGamesClient agsClient = null;
	private static PopUpLocation preferredPopupLocation = PopUpLocation.BOTTOM_CENTER;

	public GameCircle() {
		Log.d(tag, "Constructed SamcodesGameCircle");
	}

	public static void setListener(HaxeObject haxeCallback) {
		Log.i(tag, "Setting GameCircle listener");
		callback = haxeCallback;
	}

	/**
	 * Called after {@link #onCreate} &mdash; or after {@link #onRestart} when the activity had been stopped, but is now again being displayed to the user.
	 */
	public void onStart() {
		super.onStart();
		Log.i(tag, "Starting SamcodesGameCircle");
	}

	/**
	 * Called after {@link #onStop} when the current activity is being re-displayed to the user (the user has navigated back to it).
	 */
	public void onRestart() {
		super.onRestart();
	}

	/**
	 * Called when the activity is no longer visible to the user, because another activity has been resumed and is covering this one.
	 */
	public void onStop() {
		super.onStop();
		Log.i(tag, "Stopping SamcodesGameCircle");
	}

	/**
	 * Called when the activity is starting.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Perform any final cleanup before an activity is destroyed.
	 */
	public void onDestroy() {
		super.onDestroy();
		
		if(agsClient != null) {
			agsClient.shutdown();
		}
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
		super.onResume();
		
		gamesStatus = AmazonGamesStatus.INITIALIZING;

		AmazonGamesClient.initialize(this.mainActivity, new AmazonGamesCallback() {

			@Override
			public void onServiceReady(AmazonGamesClient amazonGamesClient) {
				agsClient = amazonGamesClient;
				agsClient.setPopUpLocation(preferredPopupLocation);
				gamesStatus = AmazonGamesStatus.SERVICE_CONNECTED;
				callHaxe("onConnectionEstablished", new Object[] {});
				
				amazonGamesClient.getPlayerClient().setSignedInListener(new AGSignedInListener() {
					@Override
					public void onSignedInStateChange(boolean isSignedIn) {
						if(isSignedIn == true) {
							callHaxe("onSignedIn", new Object[] {});
						} else {
							callHaxe("onSignedOut", new Object[] {});
						}
					}
				});
			}

			@Override
			public void onServiceNotReady(AmazonGamesStatus reason) {
				gamesStatus = reason;
				callHaxe("onError", new Object[] { "GAMES_CLIENT", reason.ordinal(), "onServiceNotReady" });
				Log.d(tag, "Error at onServiceNotReady with code = " + reason.ordinal());
			}

		}, EnumSet.of(AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards));
	}

	public static void onException(Exception e, String where) {
		callHaxe("onException", new Object[] { e.getMessage(), where });
		Log.d(tag, "Exception at " + where + ": " + e.toString());
		e.printStackTrace();
	}

	public static void callHaxe(final String name, final Object[] args) {
		if (callback == null) {
			Log.d(tag, "Would have called " + name + " from java but did not because no GameCircle listener was installed");
			return;
		}
		
		callbackHandler.post(new Runnable() {
			public void run() {
				Log.d(tag, "Calling " + name + " from java");
				callback.call(name, args);
			}
		});
	}

	public static boolean isSignedIn() {
		return gamesStatus == AmazonGamesStatus.SERVICE_CONNECTED;
	}

	public static void showAchievements() {
		callbackHandler.post(new Runnable() {
			public void run() {
				if(AmazonGamesClient.getInstance() == null) {
					Log.v(tag, "Not showing GameCircle achievements because AmazonGamesClient instance was null");
					return;
				}
				
				Log.v(tag, "GameCircle showAchievements");
				AmazonGamesClient.getInstance().getAchievementsClient().showAchievementsOverlay();
			}
		});
	}

	public static void showLeaderboard(final String leaderboardId) {
		callbackHandler.post(new Runnable() {
			public void run() {
				if(AmazonGamesClient.getInstance() == null) {
					Log.v(tag, "Not showing GameCircle leaderboard because AmazonGamesClient instance was null");
					return;
				}
				
				Log.v(tag, "GameCircle showLeaderboard " + leaderboardId);
				AmazonGamesClient.getInstance().getLeaderboardsClient().showLeaderboardOverlay(leaderboardId);
			}
		});
	}

	public static void showLeaderboards() {
		callbackHandler.post(new Runnable() {
			public void run() {
				if(AmazonGamesClient.getInstance() == null) {
					Log.v(tag, "Not showing GameCircle leaderboards because AmazonGamesClient instance was null");
					return;
				}
				
				Log.v(tag, "GameCircle showLeaderboards");
				AmazonGamesClient.getInstance().getLeaderboardsClient().showLeaderboardsOverlay();
			}
		});
	}
	
	public static void showSignInPage() {
		callbackHandler.post(new Runnable() {
			public void run() {
				if(AmazonGamesClient.getInstance() == null) {
					Log.v(tag, "Not showing GameCircle signin page because AmazonGamesClient instance was null");
					return;
				}
				
				Log.v(tag, "GameCircle showSignInPage");
				AmazonGamesClient.getInstance().showSignInPage();
			}
		});
	}

	public static void submitScore(final String leaderboardId, final long score, final String developerPayload) {
		callbackHandler.post(new Runnable() {
			public void run() {
				if(AmazonGamesClient.getInstance() == null) {
					Log.v(tag, "Not submitting GameCircle score because AmazonGamesClient instance was null");
					return;
				}
				
				Log.v(tag, "GameCircle submitScore " + leaderboardId + ' ' + score);
				AmazonGamesClient.getInstance().getLeaderboardsClient().submitScore(leaderboardId, score, developerPayload).setCallback(new AGResponseCallback<SubmitScoreResponse>() {
					@Override
					public void onComplete(SubmitScoreResponse result) {
						if (result.isError()) {
							// Add optional error handling here. Not required since re-tries and on-device request caching are automatic
							Log.v(tag, "GameCircle ERROR: " + result.getError());
						} else {
							Log.v(tag, "GameCircle OK");
						}
					}
				});
			}
		});
	}

	public static void updateAchievement(final String achievementId, final float percentComplete, final String developerPayload) {
		callbackHandler.post(new Runnable() {
			public void run() {
				if(AmazonGamesClient.getInstance() == null) {
					Log.v(tag, "Not submitting GameCircle achievement because AmazonGamesClient instance was null");
					return;
				}
				
				Log.v(tag, "GameCircle updateAchievement " + achievementId);
				AmazonGamesClient.getInstance().getAchievementsClient().updateProgress(achievementId, percentComplete, developerPayload);
			}
		});
	}
	
	public static void setPopUpLocation(String location) {
		Log.v(tag, "GameCircle setPopUpLocation to " + location);
		preferredPopupLocation = PopUpLocation.getLocationFromString(location, PopUpLocation.BOTTOM_CENTER);

		callbackHandler.post(new Runnable() {
			public void run() {
				if(AmazonGamesClient.getInstance() != null) {
					AmazonGamesClient.getInstance().setPopUpLocation(preferredPopupLocation);
				}
			}
		});
	}
}