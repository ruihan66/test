abstract class ProfileEvent {}

class LoadProfile extends ProfileEvent {
  final String userId;
  LoadProfile(this.userId);
}

class UpdateProfile extends ProfileEvent {
  final String? bio;
  final List<String>? interests;
  final String? avatarPath;
  UpdateProfile({this.bio, this.interests, this.avatarPath});
}

