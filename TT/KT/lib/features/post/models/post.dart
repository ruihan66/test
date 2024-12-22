class Post {
  final String id;
  final String userId;
  final String boardId;
  final String title;
  final String content;
  final List<String> images;
  final int likesCount;
  final int commentsCount;
  final int viewsCount;
  final DateTime createdAt;
  final DateTime updatedAt;

  Post({
    required this.id,
    required this.userId,
    required this.boardId,
    required this.title,
    required this.content,
    this.images = const [],
    this.likesCount = 0,
    this.commentsCount = 0,
    this.viewsCount = 0,
    required this.createdAt,
    required this.updatedAt,
  });

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'],
      userId: json['user_id'],
      boardId: json['board_id'],
      title: json['title'],
      content: json['content'],
      images: List<String>.from(json['images'] ?? []),
      likesCount: json['likes_count'] ?? 0,
      commentsCount: json['comments_count'] ?? 0,
      viewsCount: json['views_count'] ?? 0,
      createdAt: DateTime.parse(json['created_at']),
      updatedAt: DateTime.parse(json['updated_at']),
    );
  }
}

