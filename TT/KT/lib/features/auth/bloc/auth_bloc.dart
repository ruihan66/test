import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../core/services/storage_service.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final StorageService _storageService;
  final ApiClient _apiClient;

  AuthBloc(this._storageService, this._apiClient) : super(AuthInitial()) {
    on<LoginEvent>(_onLogin);
    on<RegisterEvent>(_onRegister);
    on<LogoutEvent>(_onLogout);
  }

  Future<void> _onLogin(LoginEvent event, Emitter<AuthState> emit) async {
    try {
      emit(AuthLoading());
      final response = await _apiClient.post('/auth/login', {
        'username': event.username,
        'password': event.password,
      });
      
      await _storageService.setToken(response['token']);
      await _storageService.setUserId(response['user_id']);
      emit(AuthAuthenticated());
    } catch (e) {
      emit(AuthError(e.toString()));
    }
  }

  Future<void> _onRegister(RegisterEvent event, Emitter<AuthState> emit) async {
    try {
      emit(AuthLoading());
      final response = await _apiClient.post('/auth/register', {
        'username': event.username,
        'email': event.email,
        'password': event.password,
      });
      
      await _storageService.setToken(response['token']);
      await _storageService.setUserId(response['user_id']);
      emit(AuthAuthenticated());
    } catch (e) {
      emit(AuthError(e.toString()));
    }
  }

  Future<void> _onLogout(LogoutEvent event, Emitter<AuthState> emit) async {
    await _storageService.clearAll();
    emit(AuthUnauthenticated());
  }
}

