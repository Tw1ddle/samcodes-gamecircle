package extension.gamecircle;

import extension.gamecircle.gc.ConnectionHandler;
import extension.gamecircle.gc.GamesClient;

#if android
import openfl.utils.JNI;
#end

class GameCircle {
	private var _gamesClient:GamesClient;

	public function new(handler:ConnectionHandler) {
		#if android
		initJNI();
		_start(handler);
		#end
		_gamesClient = new GamesClient(handler);
	}

	public var games(get, never):GamesClient;
	
	public function get_games():GamesClient {
		return _gamesClient;
	}

#if android
	public function isAvailable():Bool {
		return _isAvailable();
	}

	private static function initJNI():Void {
		if(_start == null) {
		  _start = JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "start", "(Lorg/haxe/lime/HaxeObject;)V");
		}
		if(_isAvailable == null) {
		  _isAvailable = JNI.createStaticMethod("com/samcodes/gamecircle/GameCircle", "isAvailable", "()Z");
		}
	}

	private static var _start:Dynamic = null;
	private static var _isAvailable:Dynamic = null;
#else
	public function isAvailable():Bool {
		return false;
	}
#end
}
