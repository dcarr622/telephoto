# Modifier.zoomablePeekOverlay()

![type:video](../assets/zoom-overlay-demo.mp4)

A `Modifier` for adding a short-lived, overlaid zoom effect inspired by Instagram. The content zooms in while the user interacts with it and, unlike `Modifier.zoomable()`, automatically returns to its normal state once the gesture is released.

### Installation

```groovy
implementation("me.saket.telephoto:zoomable-peek-overlay:{{ versions.telephoto }}")
```

```kotlin hl_lines="5"
// This example uses an image, but the modifier can be used 
// on anything, including videos (limited to TextureView).
AsyncImage(
  modifier = Modifier
    .zoomablePeekOverlay(rememberZoomablePeekOverlayState()),
  model = "https://example.com/image.jpg",
  contentDescription = "…",
)
```

### Content appearance

During zoom gestures, the overlay mirrors your content. For applying effects to your content that are only visible during zooms, `ZoomablePeekOverlayState#isZoomedIn` can be used:

```kotlin hl_lines="2 7"
val state = rememberZoomableOverlayState()
val cornerSize by animateDpAsState(if (state.isZoomedIn) 8.dp else 0.dp)

AsyncImage(
  modifier = Modifier
    .zoomablePeekOverlay(state)
    .clip(RoundedCornerShape(cornerSize)),
  model = "https://example.com/image.jpg",
  contentDescription = "…",
)
```

### Backdrop

During zoom gestures, `telephoto` draws a translucent black color behind the zoomable content to visually separate it from the background. This can be customized by passing in a different color:

```kotlin
Modifier.zoomablePeekOverlay(
  state = rememberZoomablePeekOverlayState(),
  backdrop = ZoomablePeekOverlayBackdrop.scrim(Color.Blue.copy(alpha = 0.4f)),
)
```

Alternatively, you can draw the entire backdrop yourself:

```kotlin
Modifier.zoomablePeekOverlay(
  state = rememberZoomablePeekOverlayState(),
  backdrop = ZoomablePeekOverlayBackdrop {
    Canvas(Modifier.fillMaxSize()) {
      // Go crazy here.
    }
  },
)
```
