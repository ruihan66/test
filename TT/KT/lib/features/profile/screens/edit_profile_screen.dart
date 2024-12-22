class EditProfileScreen extends StatefulWidget {
  final User user;

  const EditProfileScreen({Key? key, required this.user}) : super(key: key);

  @override
  State<EditProfileScreen> createState() => _EditProfileScreenState();
}

class _EditProfileScreenState extends State<EditProfileScreen> {
  late final TextEditingController _bioController;
  List<String> _interests = [];
  String? _newAvatarPath;

  @override
  void initState() {
    super.initState();
    _bioController = TextEditingController(text: widget.user.bio);
    _interests = List.from(widget.user.interests);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('編輯個人資料'),
        actions: [
          TextButton(
            onPressed: _saveProfile,
            child: const Text('儲存', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: Stack(
                children: [
                  CircleAvatar(
                    radius: 50,
                    backgroundImage: _newAvatarPath != null
                        ? FileImage(File(_newAvatarPath!))
                        : (widget.user.avatar != null
                            ? NetworkImage(widget.user.avatar!)
                            : null) as ImageProvider?,
                    child: widget.user.avatar == null && _newAvatarPath == null
                        ? Text(widget.user.username[0].toUpperCase())
                        : null,
                  ),
                  Positioned(
                    right: 0,
                    bottom: 0,
                    child: IconButton(
                      onPressed: _pickAvatar,
                      icon: const Icon(Icons.camera_alt),
                      style: IconButton.styleFrom(
                        backgroundColor: Theme.of(context).primaryColor,
                        foregroundColor: Colors.white,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            const Text('個人簡介'),
            TextField(
              controller: _bioController,
              maxLines: 3,
              decoration: const InputDecoration(
                hintText: '介紹一下自己吧...',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),
            _buildInterestsSection(),
          ],
        ),
      ),
    );
  }

  Widget _buildInterestsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('興趣'),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            ..._interests.map((interest) => Chip(
              label: Text(interest),
              onDeleted: () {
                setState(() {
                  _interests.remove(interest);
                });
              },
            )),
            ActionChip(
              label: const Text('新增興趣'),
              onPressed: _addInterest,
            ),
          ],
        ),
      ],
    );
  }

  Future<void> _pickAvatar() async {
    // 實現選擇頭像的邏輯
  }

  Future<void> _addInterest() async {
    // 實現添加興趣的邏輯
  }

  void _saveProfile() {
    context.read<ProfileBloc>().add(UpdateProfile(
      bio: _bioController.text,
      interests: _interests,
      avatarPath: _newAvatarPath,
    ));
    context.pop();
  }
}
