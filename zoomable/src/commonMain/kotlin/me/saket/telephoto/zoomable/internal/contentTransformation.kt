package me.saket.telephoto.zoomable.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ScaleFactor
import me.saket.telephoto.zoomable.ZoomableContentTransformation

internal data class RealZoomableContentTransformation(
  override val isSpecified: Boolean,
  override val scale: ScaleFactor,
  override val scaleMetadata: ScaleMetadata,
  override val offset: Offset,
  override val centroid: Offset?,
  override val contentSize: Size,
  override val rotationZ: Float = 0f,
) : ZoomableContentTransformation {

  data class ScaleMetadata(
    override val initialScale: ScaleFactor,
    override val userZoom: Float,
  ) : ZoomableContentTransformation.ScaleMetadata
}
