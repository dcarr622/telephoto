# Sub-sampling

![type:video](../assets/subsampling_small.mp4)

For displaying large images that may not fit into memory, `ZoomableImage` automatically divides them into tiles so that they can be loaded lazily.

If `ZoomableImage` ^^can't^^ be used or if sub-sampling of images is always desired, you could potentially use `SubSamplingImage()` directly.

```groovy
implementation("me.saket.telephoto:sub-sampling-image:{{ versions.telephoto }}")
```

```kotlin
val zoomableState = rememberZoomableState()
val imageState = rememberSubSamplingImageState(
  zoomableState = zoomableState,
  imageSource = SubSamplingImageSource.asset("fox.jpg")
)

SubSamplingImage(
  modifier = Modifier
    .fillMaxSize()
    .zoomable(zoomableState),
  state = imageState,
  contentDescription = …,
)
```

`SubSamplingImage()` is an adaptation of the excellent [subsampling-scale-image-view](https://github.com/davemorrissey/subsampling-scale-image-view) by Dave Morrissey.

### Custom image sources

`SubSamplingImage()` isn't limited to simple images. It can also be used to display custom content, such as PDFs or small maps, by using custom implementations of `SubSamplingImageSource`:

```kotlin
val imageSource = FooImageSource(…)
val zoomableState = rememberZoomableState()

SubSamplingImage(
  modifier = Modifier
    .fillMaxSize()
    .zoomable(zoomableState),
  state = rememberSubSamplingImageState(imageSource, zoomableState),
  contentDescription = …,
)
```

```kotlin
class FooImageSource(
  override val preview: ImageBitmap?
) : SubSamplingImageSource {

  override suspend fun decoder(): ImageRegionDecoder.Factory {
    return ImageRegionDecoder.Factory { params ->
      TODO("Create an image decoder that can load images for your content")
    }
  }
}
```

Custom `SubSamplingImageSource` values can also be used with [`Zoomable*Image()`](https://saket.github.io/telephoto/zoomableimage/) via a [custom image loader](https://saket.github.io/telephoto/zoomableimage/custom-image-loaders/).
