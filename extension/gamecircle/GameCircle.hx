package extension.gamecircle;

#if android

import extension.gamecircle.gc.GameCircleListener;
import extension.gamecircle.gc.GamesClient;
import openfl.utils.JNI;

class GameCircle {
	public var games(default, null):GamesClient;

	public function new(?listener:GameCircleListener) {
		initBindings();
		games = new GamesClient();
		
		if(listener != null) {
			setListener(listener);
		}
	}
	
	public function setListener(listener:GameCircleListener):Void {
		set_listener(listener);
	}

	private static function initBindings():Void {
		var packageName:String = "com/samcodes/gamecircle/GameCircle";
		
		if (set_listener == null) {
			set_listener = JNI.createStaticMethod(packageName, "setListener", "(Lorg/haxe/lime/HaxeObject;)V");
		}
	}
	
	private static var set_listener:Dynamic = null;
}

#end