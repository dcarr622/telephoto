package me.saket.telephoto.subsamplingimage.internal

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random

class ResourcePoolTest {

  @Test fun `first in last out`() = runBlocking {
    val pool = ResourcePool(listOf("a", "b"))
    repeat(5) {
      assertThat(pool.borrow { it }).isEqualTo("a")
      assertThat(pool.borrow { it }).isEqualTo("b")
    }
  }

  @Test fun `resources should be released even if borrows are canceled`() = runBlocking {
    val pool = ResourcePool(listOf("resource1", "resource2"))
    val borrow1 = launch {
      pool.borrow { delay(500) }
    }
    val borrow2 = launch {
      pool.borrow { delay(500) }
    }

    // Cancel one of the jobs to simulate a coroutine being cancelled.
    delay(100)
    borrow1.cancel()
    borrow2.join()

    assertThat(pool.removeItems(2)).containsExactly("resource1", "resource2")
  }

  @Test fun `simulate high contention for a single resource`() = runBlocking {
    val pool = ResourcePool(listOf("resource1"))

    // Launch multiple coroutines borrowing the single resource.
    val jobs = List(10) {
      launch(Dispatchers.Default) {
        pool.borrow {
          delay(Random.nextLong(100, 500)) // Simulate work.
        }
      }
    }

    // Randomly cancel some jobs to simulate stress-induced cancellation?
    delay(500)
    jobs.shuffled().take(jobs.size / 2).forEach { it.cancel() }

    jobs.joinAll()
    assertThat(pool.removeItems(1)).hasSize(1)
  }

  private suspend fun <T> ResourcePool<T>.removeItems(size: Int): List<T> {
    val pool = this
    return List(size) { pool.borrow { it } }.also {
      pool.tryClose()
    }
  }
}
