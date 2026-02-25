# Frame-Based Film Timeline for BBS Mod

## Overview

The film system in BBS Mod is internally **tick-based** (20 ticks per second), but the editor can expose a **frame-based timeline** driven by the **Video Export FPS** setting. This allows you to think and work in frames (0, 1, 2, …) while all existing film data and logic continue to use ticks under the hood.

- Internal time unit: **ticks** (20 TPS).
- Visual editor time unit: **frames**, derived from the export FPS.
- Export FPS is the bridge between frame numbers and film time.

## Core Concepts

### Ticks

- All core systems remain keyed to ticks:
    - Replay keyframes (position, rotation, pose, etc.).
    - Film replays, actions, properties.
    - Camera clips and transitions.
- Minecraft logic and playback speed are defined in terms of 20 ticks per second.

### Frames

- Frames are a **UI representation of time** in the film editor.
- The frame ruler and cursor use the Video Export FPS value:
    - FPS = 24 → timeline shows 24 frames per second.
    - FPS = 60 → timeline shows 60 frames per second.
- A frame number `F` corresponds to a specific film time and tick value.

## Time Mapping

Given:

- `videoFPS` – FPS chosen in the **Video Export** settings.
- `F` – frame index on the film timeline (0-based).
- `T` – film time in seconds.
- `tick` – film tick (float), where 20 ticks = 1 second.

Conversions:

- **Frame → time**

    `T = F / videoFPS`

- **Time → tick**

    `tickFloat = T * 20`

    `tick = round(tickFloat)` (when an integer tick is required)

- **Tick → frame** (for drawing keyframes on the timeline)

    `F = tick * videoFPS / 20`

The film engine always evaluates animations and actions using `tickFloat`. Frames are used only for how time is displayed and manipulated in the UI.

## Placing and Editing Keyframes in Frames

When you work in the editor:

1. You position the cursor at a frame `F` on the timeline.
2. You add or move a keyframe at that frame.

Internally, the editor:

1. Converts frame → time:

    `T = F / videoFPS`

2. Converts time → tick:

    `tickFloat = T * 20`

3. Stores the keyframe at `tick = round(tickFloat)` in the underlying film data.

When you scrub back to frame `F`, the editor performs the same mapping in reverse (frame → time → tick) and evaluates the film at that tick. From the user’s perspective, the keyframe “lives at frame F”.

## Frame-Based / Flipbook Animation

Frame or flipbook-style animation can be defined using the same export FPS timeline:

- During video export, each **video frame index** `n` is rendered at time:

    `T = n / videoFPS`

- The film is evaluated at:

    `tickFloat = T * 20`

- A flipbook frame index can be derived from `n`, for example:
    - **1:1 mapping** – one flipbook step per video frame:

        `flipbookFrame = n % totalFrames`

    - **Ratio mapping** – one flipbook step every `k` video frames:

        `flipbookFrame = floor(n / k) % totalFrames`

In both cases, flipbook timing is implicitly tied to the export FPS, so no extra “flipbook FPS” option is required.

## Editor Experience

With a frame-based visual timeline:

- The ruler and cursor show frame indices instead of ticks.
- Adding a keyframe at a frame uses the mapping above to place it at the corresponding tick.
- Scrubbing the timeline moves through frames, which in turn maps to continuous time and ticks.
- Frame-based behaviors (such as flipbooks) can step per frame or per N frames, while the rest of the film continues to use tick-based keyframes.

## Design Goals and Benefits

- **No change to core logic**  
  The film engine remains fully tick-based, preserving compatibility and deterministic behavior tied to Minecraft’s 20 TPS.

- **Single FPS source**  
  The Video Export FPS setting is the only global FPS control. The frame timeline and any frame-based animation derive from this value.

- **Frame-first authoring**  
  Creators can reason in familiar film terms (“frame 0–143”) when placing keyframes, designing timing, or setting up flipbook-like animations.

- **Consistent preview and export**  
  The editor uses the same frame/time mapping as the video exporter, so what you see in the frame timeline aligns with the final rendered video.

