package me.saket.telephoto.zoomable.internal

import androidx.compose.ui.geometry.Offset
import me.saket.telephoto.zoomable.GestureState
import me.saket.telephoto.zoomable.UserZoomFactor

@AndroidParcelize
@Suppress("DataClassPrivateConstructor")
internal data class ZoomableSavedState private constructor(
  private val offsetX: Float?,
  private val offsetY: Float?,
  private val centroidX: Float?,
  private val centroidY: Float?,
  private val userZoom: Float?,
) : AndroidParcelable {

  constructor(gestureState: GestureState?) : this(
    offsetX = gestureState?.offset?.x,
    offsetY = gestureState?.offset?.y,
    centroidX = gestureState?.lastCentroid?.x,
    centroidY = gestureState?.lastCentroid?.y,
    userZoom = gestureState?.userZoom?.value,
  )

  fun asGestureState(): GestureState? {
    return GestureState(
      offset = Offset(
        x = offsetX ?: return null,
        y = offsetY ?: return null
      ),
      userZoom = UserZoomFactor(
        value = userZoom ?: return null
      ),
      lastCentroid = Offset(
        x = centroidX ?: return null,
        y = centroidY ?: return null,
      ),
    )
  }
}
