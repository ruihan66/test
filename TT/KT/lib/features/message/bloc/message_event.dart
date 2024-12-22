abstract class MessageEvent {}

class LoadMessages extends MessageEvent {
  final String userId;
  LoadMessages(this.userId);
}

class SendMessage extends MessageEvent {
  final String receiverId;
  final String content;
  SendMessage(this.receiverId, this.content);
}

class ReceiveMessage extends MessageEvent {
  final Message message;
  ReceiveMessage(this.message);
}

