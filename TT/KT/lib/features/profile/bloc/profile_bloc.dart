class ProfileBloc extends Bloc<ProfileEvent, ProfileState> {
  final ApiClient _apiClient;

  ProfileBloc(this._apiClient) : super(ProfileInitial()) {
    on<LoadProfile>(_onLoadProfile);
    on<UpdateProfile>(_onUpdateProfile);
  }

  Future<void> _onLoadProfile(LoadProfile event, Emitter<ProfileState> emit) async {
    try {
      emit(ProfileLoading());
      final userResponse = await _apiClient.get('/users/${event.userId}');
      final postsResponse = await _apiClient.get('/users/${event.userId}/posts');
      
      final user = User.fromJson(userResponse);
      final posts = (postsResponse['data'] as List)
          .map((json) => Post.fromJson(json))
          .toList();
      
      emit(ProfileLoaded(user, posts));
    } catch (e) {
      emit(ProfileError(e.toString()));
    }
  }

  Future<void> _onUpdateProfile(UpdateProfile event, Emitter<ProfileState> emit) async {
    try {
      emit(ProfileLoading());
      final updateData = {
        if (event.bio != null) 'bio': event.bio,
        if (event.interests != null) 'interests': event.interests,
        if (event.avatarPath != null) 'avatar': event.avatarPath,
      };
      
      final response = await _apiClient.put('/users/me', updateData);
      final user = User.fromJson(response);
      emit(ProfileLoaded(user, []));
    } catch (e) {
      emit(ProfileError(e.toString()));
    }
  }
}

