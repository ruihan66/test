class PostBloc extends Bloc<PostEvent, PostState> {
  final ApiClient _apiClient;

  PostBloc(this._apiClient) : super(PostInitial()) {
    on<LoadPosts>(_onLoadPosts);
    on<CreatePost>(_onCreatePost);
    on<LikePost>(_onLikePost);
  }

  Future<void> _onLoadPosts(LoadPosts event, Emitter<PostState> emit) async {
    try {
      emit(PostLoading());
      final response = await _apiClient.get('/posts?page=${event.page}');
      final posts = (response['data'] as List)
          .map((json) => Post.fromJson(json))
          .toList();
      emit(PostLoaded(posts));
    } catch (e) {
      emit(PostError(e.toString()));
    }
  }

  Future<void> _onCreatePost(CreatePost event, Emitter<PostState> emit) async {
    try {
      emit(PostLoading());
      await _apiClient.post('/posts', {
        'title': event.title,
        'content': event.content,
        'board_id': event.boardId,
        'images': event.images,
      });
      emit(PostCreated());
    } catch (e) {
      emit(PostError(e.toString()));
    }
  }

  Future<void> _onLikePost(LikePost event, Emitter<PostState> emit) async {
    try {
      await _apiClient.post('/posts/${event.postId}/like', {});
    } catch (e) {
      emit(PostError(e.toString()));
    }
  }
}

