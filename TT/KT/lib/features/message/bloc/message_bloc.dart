class MessageBloc extends Bloc<MessageEvent, MessageState> {
  final ApiClient _apiClient;
  final WebSocketService _wsService;

  MessageBloc(this._apiClient, this._wsService) : super(MessageInitial()) {
    on<LoadMessages>(_onLoadMessages);
    on<SendMessage>(_onSendMessage);
    on<ReceiveMessage>(_onReceiveMessage);
    _initWebSocket();
  }

  void _initWebSocket() {
    _wsService.messages.listen((message) {
      add(ReceiveMessage(Message.fromJson(message)));
    });
  }

  Future<void> _onLoadMessages(
    LoadMessages event,
    Emitter<MessageState> emit,
  ) async {
    try {
      emit(MessageLoading());
      final response = await _apiClient.get('/messages/${event.userId}');
      final messages = (response['data'] as List)
          .map((json) => Message.fromJson(json))
          .toList();
      emit(MessagesLoaded(messages));
    } catch (e) {
      emit(MessageError(e.toString()));
    }
  }

  Future<void> _onSendMessage(
    SendMessage event,
    Emitter<MessageState> emit,
  ) async {
    try {
      final response = await _apiClient.post(
        '/messages/${event.receiverId}',
        {'content': event.content},
      );
      final message = Message.fromJson(response);
      if (state is MessagesLoaded) {
        final currentMessages = (state as MessagesLoaded).messages;
        emit(MessagesLoaded([...currentMessages, message]));
      }
    } catch (e) {
      emit(MessageError(e.toString()));
    }
  }

  void _onReceiveMessage(
    ReceiveMessage event,
    Emitter<MessageState> emit,
  ) {
    if (state is MessagesLoaded) {
      final currentMessages = (state as MessagesLoaded).messages;
      emit(MessagesLoaded([...currentMessages, event.message]));
    }
  }
}

