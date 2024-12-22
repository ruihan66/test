class Message {
  final String id;
  final String senderId;
  final String receiverId;
  final String content;
  final bool read;
  final DateTime createdAt;

  Message({
    required this.id,
    required this.senderId,
    required this.receiverId,
    required this.content,
    required this.read,
    required this.createdAt,
  });

  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      id: json['id'],
      senderId: json['sender_id'],
      receiverId: json['receiver_id'],
      content: json['content'],
      read: json['read'] ?? false,
      createdAt: DateTime.parse(json['created_at']),
    );
  }
}

