import 'package:flutter/material.dart';

abstract class Component {
  const Component(
    this.layout, {
    this.backgroundColor,
    this.screenTimeout = const ScreenTimeout(
      onInteraction: Duration(seconds: 10),
      onStateChange: Duration(seconds: 5),
    ),
  });

  final Widget layout;
  final Color? backgroundColor;
  final ScreenTimeout? screenTimeout;
}

class ScreenTimeout {
  const ScreenTimeout({
    required this.onInteraction,
    this.onStateChange,
    this.onStateUpdate,
  });

  final Duration onInteraction;
  final Duration? onStateChange;
  final Duration? onStateUpdate;
}
