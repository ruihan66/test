class RegisterScreen extends StatefulWidget {
  const RegisterScreen({Key? key}) : super(key: key);

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('註冊')),
      body: BlocListener<AuthBloc, AuthState>(
        listener: (context, state) {
          if (state is AuthAuthenticated) {
            context.go('/');
          } else if (state is AuthError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(state.message)),
            );
          }
        },
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                TextFormField(
                  controller: _usernameController,
                  decoration: const InputDecoration(labelText: '用戶名'),
                  validator: (value) {
                    if (value?.isEmpty ?? true) {
                      return '請輸入用戶名';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 16),
                TextFormField(
                  controller: _emailController,
                  decoration: const InputDecoration(labelText: '電子郵件'),
                  validator: (value) {
                    if (value?.isEmpty ?? true) {
                      return '請輸入電子郵件';
                    }
                    if (!value!.contains('@')) {
                      return '請輸入有效的電子郵件地址';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 16),
                TextFormField(
                  controller: _passwordController,
                  decoration: const InputDecoration(labelText: '密碼'),
                  obscureText: true,
                  validator: (value) {
                    if (value?.isEmpty ?? true) {
                      return '請輸入密碼';
                    }
                    if (value!.length < 6) {
                      return '密碼長度不能少於6個字符';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: () {
                    if (_formKey.currentState?.validate() ?? false) {
                      context.read<AuthBloc>().add(RegisterEvent(
                        username: _usernameController.text,
                        email: _emailController.text,
                        password: _passwordController.text,
                      ));
                    }
                  },
                  child: const Text('註冊'),
                ),
                TextButton(
                  onPressed: () => context.pop(),
                  child: const Text('已有帳號？返回登入'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
