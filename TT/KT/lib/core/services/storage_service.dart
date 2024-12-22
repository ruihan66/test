import 'package:shared_preferences.dart';

class StorageService {
  final SharedPreferences _prefs;

  StorageService(this._prefs);

  static const String keyToken = 'auth_token';
  static const String keyUserId = 'user_id';

  Future<void> setToken(String token) async {
    await _prefs.setString(keyToken, token);
  }

  String? getToken() => _prefs.getString(keyToken);

  Future<void> setUserId(String userId) async {
    await _prefs.setString(keyUserId, userId);
  }

  String? getUserId() => _prefs.getString(keyUserId);

  Future<void> clearAll() async {
    await _prefs.clear();
  }
}

