@file:Suppress("FunctionName")

package me.saket.telephoto.subsamplingimage.internal

import android.app.ActivityManager
import android.content.Context
import android.graphics.BitmapRegionDecoder
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.core.content.getSystemService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import me.saket.telephoto.subsamplingimage.SubSamplingImageSource
import me.saket.telephoto.subsamplingimage.internal.ImageRegionDecoder.DecodeResult

/**
 * Maintains a pool of decoders to load multiple bitmap regions in parallel. Without this,
 * a single [BitmapRegionDecoder] can only be used for one region at a time because it
 * synchronizes its APIs internally.
 * */
internal class PooledAndroidImageRegionDecoder private constructor(
  override val imageSize: IntSize,
  private val decoders: ResourcePool<ImageRegionDecoder>,
) : ImageRegionDecoder {

  override suspend fun decodeRegion(region: IntRect, sampleSize: Int): DecodeResult {
    return decoders.borrow { decoder ->
      decoder.decodeRegion(region, sampleSize)
    }
  }

  override fun close() {
    decoders.tryClose()
  }

  companion object {
    @VisibleForTesting
    internal var overriddenPoolCount: Int? = null

    fun Factory(imageSource: SubSamplingImageSource, createDecoder: (context: Context) -> BitmapRegionDecoder) =
      ImageRegionDecoder.Factory { params ->
        check(params is AndroidImageDecoderFactoryParams)
        val delegate = AndroidImageRegionDecoder.Factory(
          imageSource = imageSource,
          exif = ExifMetadata.read(params.context, imageSource),
          createDecoder = { createDecoder(params.context) },
        )
        val decoders = buildList<ImageRegionDecoder> {
          add(delegate.create(params))
          repeat(calculatePoolCount(params.context, first().imageSize) - 1) {
            add(delegate.create(params))
          }
        }
        PooledAndroidImageRegionDecoder(
          imageSize = decoders.first().imageSize,
          decoders = ResourcePool(decoders),
        )
      }

    private fun calculatePoolCount(context: Context, imageSize: IntSize): Int {
      overriddenPoolCount?.let {
        return it
      }
      val activityManager = context.getSystemService<ActivityManager>()!!
      val memoryInfo = ActivityManager.MemoryInfo().apply(activityManager::getMemoryInfo)
      if (memoryInfo.lowMemory || activityManager.isLowRamDevice) {
        return 1
      }
      // BitmapRegionDecoders are expensive on android. Folks working on android's graphics
      // have suggested not using more than 2 instances to keep memory footprint low.
      // For large images, err on the side of caution and use a single decoder to reduce
      // memory usage, especially for progressive JPEGs.
      return if (imageSize.minDimension < 2_160) {
        Runtime.getRuntime().availableProcessors().coerceAtMost(2)
      } else {
        1
      }
    }
  }
}

internal class ResourcePool<T>(resources: List<T>) {
  private val resources = ArrayDeque(resources)
  private val semaphore = Semaphore(permits = resources.size)
  private val mutex = Mutex()

  suspend fun <R> borrow(handler: suspend (T) -> R): R {
    return semaphore.withPermit {
      val borrowed = mutex.withLock { resources.removeFirst() }
      try {
        handler(borrowed)
      } finally {
        mutex.withLock { resources.addLast(borrowed) }
      }
    }
  }

  fun tryClose() {
    resources.clear()
  }
}
