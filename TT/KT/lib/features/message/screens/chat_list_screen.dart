class ChatListScreen extends StatelessWidget {
  const ChatListScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('私信')),
      body: BlocBuilder<MessageBloc, MessageState>(
        builder: (context, state) {
          if (state is MessageLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          
          if (state is MessagesLoaded) {
            final groupedMessages = _groupMessagesByUser(state.messages);
            return ListView.builder(
              itemCount: groupedMessages.length,
              itemBuilder: (context, index) {
                final userId = groupedMessages.keys.elementAt(index);
                final lastMessage = groupedMessages[userId]!.last;
                return _buildChatListItem(context, userId, lastMessage);
              },
            );
          }
          
          return const Center(child: Text('No messages'));
        },
      ),
    );
  }

  Map<String, List<Message>> _groupMessagesByUser(List<Message> messages) {
    return groupBy(messages, (Message m) {
      return m.senderId;
    });
  }

  Widget _buildChatListItem(
    BuildContext context,
    String userId,
    Message lastMessage,
  ) {
    return ListTile(
      leading: CircleAvatar(
        child: Text(userId.substring(0, 2)),
      ),
      title: Text(userId),
      subtitle: Text(
        lastMessage.content,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
      trailing: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            _formatMessageTime(lastMessage.createdAt),
            style: Theme.of(context).textTheme.bodySmall,
          ),
          if (!lastMessage.read)
            Container(
              margin: const EdgeInsets.only(top: 4),
              padding: const EdgeInsets.all(6),
              decoration: BoxDecoration(
                color: Theme.of(context).primaryColor,
                shape: BoxShape.circle,
              ),
            ),
        ],
      ),
      onTap: () => _openChatScreen(context, userId),
    );
  }

  String _formatMessageTime(DateTime time) {
    final now = DateTime.now();
    if (time.day == now.day) {
      return DateFormat.Hm().format(time);
    }
    return DateFormat.MMMd().format(time);
  }

  void _openChatScreen(BuildContext context, String userId) {
    context.push('/chat/$userId');
  }
}

