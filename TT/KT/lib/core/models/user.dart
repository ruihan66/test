class User {
  final String id;
  final String username;
  final String? email;
  final String? avatar;
  final String? bio;
  final List<String> interests;
  final int followersCount;
  final int followingCount;
  final DateTime createdAt;

  User({
    required this.id,
    required this.username,
    this.email,
    this.avatar,
    this.bio,
    this.interests = const [],
    this.followersCount = 0,
    this.followingCount = 0,
    required this.createdAt,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      username: json['username'],
      email: json['email'],
      avatar: json['avatar'],
      bio: json['bio'],
      interests: List<String>.from(json['interests'] ?? []),
      followersCount: json['followers_count'] ?? 0,
      followingCount: json['following_count'] ?? 0,
      createdAt: DateTime.parse(json['created_at']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'username': username,
      'email': email,
      'avatar': avatar,
      'bio': bio,
      'interests': interests,
      'followers_count': followersCount,
      'following_count': followingCount,
      'created_at': createdAt.toIso8601String(),
    };
  }
}
