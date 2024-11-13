package me.saket.telephoto.zoomable.internal

import androidx.compose.runtime.RememberObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal abstract class RememberWorker : RememberObserver {
  private var scope: CoroutineScope? = null

  abstract suspend fun work()

  override fun onRemembered() {
    scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    scope!!.launch { work() }
  }

  override fun onForgotten() {
    scope?.cancel()
  }

  override fun onAbandoned() {
    check(scope == null) {
      "onRemembered() shouldn't have been called as per RememberObserver's documentation"
    }
  }
}
