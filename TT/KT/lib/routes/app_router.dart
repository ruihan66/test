import 'package:go_router/go_router.dart';
import 'package:flutter/material.dart';
import '../features/home/home_screen.dart';
import '../features/auth/login_screen.dart';
import '../features/auth/register_screen.dart';
import '../features/profile/profile_screen.dart';
import '../features/post/create_post_screen.dart';

class AppRouter {
  static final router = GoRouter(
    initialLocation: '/',
    routes: [
      GoRoute(
        path: '/',
        builder: (context, state) => const HomeScreen(),
      ),
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/profile/:userId',
        builder: (context, state) => ProfileScreen(
          userId: state.pathParameters['userId']!,
        ),
      ),
      GoRoute(
        path: '/post/create',
        builder: (context, state) => const CreatePostScreen(),
      ),
    ],
  );
}

