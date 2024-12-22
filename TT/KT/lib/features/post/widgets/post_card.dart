class PostCard extends StatelessWidget {
  final Post post;

  const PostCard({Key? key, required this.post}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: InkWell(
        onTap: () => context.push('/post/${post.id}'),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildHeader(context),
              const SizedBox(height: 12),
              Text(
                post.title,
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                post.content,
                maxLines: 3,
                overflow: TextOverflow.ellipsis,
              ),
              if (post.images.isNotEmpty) ...[
                const SizedBox(height: 12),
                _buildImagePreview(),
              ],
              const SizedBox(height: 12),
              _buildFooter(context),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Row(
      children: [
        CircleAvatar(
          radius: 20,
          child: Text(post.userId.substring(0, 2)),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                post.userId,
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              Text(
                TimeAgo.format(post.createdAt),
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ],
          ),
        ),
        IconButton(
          icon: const Icon(Icons.more_horiz),
          onPressed: () => _showPostOptions(context),
        ),
      ],
    );
  }

  Widget _buildImagePreview() {
    if (post.images.isEmpty) return const SizedBox.shrink();

    return SizedBox(
      height: 200,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: post.images.length,
        itemBuilder: (context, index) {
          return Container(
            width: 200,
            margin: const EdgeInsets.only(right: 8),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8),
              image: DecorationImage(
                image: NetworkImage(post.images[index]),
                fit: BoxFit.cover,
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildFooter(BuildContext context) {
    return Row(
      children: [
        IconButton(
          icon: const Icon(Icons.thumb_up_outlined),
          onPressed: () => context.read<PostBloc>().add(LikePost(post.id)),
        ),
        Text(post.likesCount.toString()),
        const SizedBox(width: 16),
        IconButton(
          icon: const Icon(Icons.comment_outlined),
          onPressed: () => context.push('/post/${post.id}'),
        ),
        Text(post.commentsCount.toString()),
      ],
    );
  }

  void _showPostOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) {
        return SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              ListTile(
                leading: const Icon(Icons.share),
                title: const Text('分享'),
                onTap: () {
                  Navigator.pop(context);
                  // 實現分享功能
                },
              ),
              ListTile(
                leading: const Icon(Icons.report),
                title: const Text('檢舉'),
                onTap: () {
                  Navigator.pop(context);
                  // 實現檢舉功能
                },
              ),
            ],
          ),
        );
      },
    );
  }
}

