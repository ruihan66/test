class PostListScreen extends StatelessWidget {
  const PostListScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('論壇'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () {},
          ),
        ],
      ),
      body: BlocBuilder<PostBloc, PostState>(
        builder: (context, state) {
          if (state is PostLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          
          if (state is PostLoaded) {
            return RefreshIndicator(
              onRefresh: () async {
                context.read<PostBloc>().add(LoadPosts(page: 1));
              },
              child: ListView.builder(
                itemCount: state.posts.length,
                itemBuilder: (context, index) {
                  return PostCard(post: state.posts[index]);
                },
              ),
            );
          }
          
          if (state is PostError) {
            return Center(child: Text(state.message));
          }
          
          return const SizedBox.shrink();
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/post/create'),
        child: const Icon(Icons.create),
      ),
    );
  }
}

