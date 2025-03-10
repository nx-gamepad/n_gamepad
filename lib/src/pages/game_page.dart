import 'package:flutter/material.dart';

import '../helpers/timer.dart';

import '../models/component.dart';
import '../models/game.dart';
import '../models/protocol.dart';

import '../connection.dart';

class GamePage extends StatefulWidget {
  const GamePage(this.game, this.initial, {super.key});

  final Game game;
  final StatePacket initial;

  @override
  State<GamePage> createState() => _GamePageState();
}

class _GamePageState extends State<GamePage> with WidgetsBindingObserver {
  late StatePacket previous;
  ObservableTimer? timer;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);

    previous = widget.initial;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: StreamBuilder<StatePacket>(
        initialData: widget.initial,
        stream: Connection.service.stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final current = snapshot.data!;
            final component = widget.game.build(current);

            if (component.screenTimeout == null) {
              timer?.cancel();
              timer = null;
            } else if (timer == null) {
              timer = ObservableTimer(
                component.screenTimeout!.onInteraction,
                () => Connection.gamepad.switchScreenBrightness(false),
                () => Connection.gamepad.switchScreenBrightness(true),
              );
            } else if (previous != current) {
              resetTimer(component.screenTimeout!.onStateChange);
            } else {
              resetTimer(component.screenTimeout!.onStateUpdate);
            }
            previous = current;

            return Listener(
              onPointerDown: (event) => cancelTimer(component.screenTimeout),
              onPointerUp: (event) => startTimer(component.screenTimeout),
              behavior: HitTestBehavior.translucent,
              child: Container(
                color: component.backgroundColor,
                child: component.layout,
              ),
            );
          }
          return ErrorWidget.withDetails(message: 'stream is null');
        },
      ),
    );
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);

    if (state == AppLifecycleState.inactive) {
      Navigator.of(context).popUntil(
        (route) => route.isFirst,
      );
    }
  }

  @override
  void dispose() {
    timer?.cancel();
    Connection.service.reset();
    Connection.gamepad.resetControls();
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  void resetTimer(Duration? duration) {
    if (duration != null) timer!.reset(duration);
  }

  void cancelTimer(ScreenTimeout? timeout) {
    if (timeout != null) timer!.cancel();
  }

  void startTimer(ScreenTimeout? timeout) {
    if (timeout != null) timer!.start(timeout.onInteraction);
  }
}
