package extension.gamecircle;

#if android

class GameCircleListener {
	public function new() {}

	public function onWarning(msg:String, where:String) {
	}

	public function onError(what:String, errorCode:Int, where:String) {
	}

	public function onException(msg:String, where:String) {
	}

	public function onConnectionEstablished() {
	}
	
	public function onSignedIn() {
	}

	public function onSignedOut() {
	}
}

#end