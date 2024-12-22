class ProfileScreen extends StatelessWidget {
  final String userId;
  
  const ProfileScreen({Key? key, required this.userId}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => ProfileBloc(context.read<ApiClient>())..add(LoadProfile(userId)),
      child: Scaffold(
        body: BlocBuilder<ProfileBloc, ProfileState>(
          builder: (context, state) {
            if (state is ProfileLoading) {
              return const Center(child: CircularProgressIndicator());
            }
            
            if (state is ProfileLoaded) {
              return CustomScrollView(
                slivers: [
                  _buildProfileHeader(context, state.user),
                  _buildPostsList(state.posts),
                ],
              );
            }
            
            if (state is ProfileError) {
              return Center(child: Text(state.message));
            }
            
            return const SizedBox.shrink();
          },
        ),
      ),
    );
  }

  Widget _buildProfileHeader(BuildContext context, User user) {
    return SliverAppBar(
      expandedHeight: 320,
      pinned: true,
      flexibleSpace: FlexibleSpaceBar(
        background: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const SizedBox(height: 60),
            CircleAvatar(
              radius: 50,
              backgroundImage: user.avatar != null
                  ? NetworkImage(user.avatar!)
                  : null,
              child: user.avatar == null
                  ? Text(user.username[0].toUpperCase())
                  : null,
            ),
            const SizedBox(height: 16),
            Text(
              user.username,
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            if (user.bio != null) ...[
              const SizedBox(height: 8),
              Text(user.bio!),
            ],
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                _buildStatColumn('貼文', user.followersCount),
                const SizedBox(width: 32),
                _buildStatColumn('追蹤者', user.followersCount),
                const SizedBox(width: 32),
                _buildStatColumn('追蹤中', user.followingCount),
              ],
            ),
            const SizedBox(height: 16),
            if (user.interests.isNotEmpty)
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: user.interests.map((interest) {
                  return Chip(label: Text(interest));
                }).toList(),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatColumn(String label, int value) {
    return Column(
      children: [
        Text(
          value.toString(),
          style: const TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        Text(label),
      ],
    );
  }

  Widget _buildPostsList(List<Post> posts) {
    return SliverList(
      delegate: SliverChildBuilderDelegate(
        (context, index) {
          final post = posts[index];
          return PostCard(post: post);
        },
        childCount: posts.length,
      ),
    );
  }
}

