abstract class AuthEvent {}

class LoginEvent extends AuthEvent {
  final String username;
  final String password;
  LoginEvent({required this.username, required this.password});
}

class RegisterEvent extends AuthEvent {
  final String username;
  final String email;
  final String password;
  RegisterEvent({
    required this.username,
    required this.email,
    required this.password,
  });
}

class LogoutEvent extends AuthEvent {}

