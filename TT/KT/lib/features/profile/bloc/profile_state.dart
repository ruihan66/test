abstract class ProfileState {}

class ProfileInitial extends ProfileState {}
class ProfileLoading extends ProfileState {}
class ProfileLoaded extends ProfileState {
  final User user;
  final List<Post> posts;
  ProfileLoaded(this.user, this.posts);
}
class ProfileError extends ProfileState {
  final String message;
  ProfileError(this.message);
}

