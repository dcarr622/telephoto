package me.saket.telephoto.subsamplingimage.internal

import assertk.assertThat
import assertk.assertions.isTrue
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import kotlin.test.Test

class ImageFormatsTest {
  @Test fun `read avif header`() {
    val pseudoAvifString = (
      "\u0000\u0000\u0000\u0024" +    // boxSize = 36, as bytes [0..3]
        "ftyp" +                      // bytes [4..7]
        "mif1" +                      // bytes [8..11] (major brand)
        "\u0000\u0000\u0000\u0000" +  // bytes [12..15] (minor version)
        "miaf" +                      // bytes [16..19] (compatible brand #1)
        "heic" +                      // bytes [20..23] (compatible brand #2)
        "junk" +                      // bytes [24..27] (compatible brand #3)
        "junk" +                      // bytes [28..31] (compatible brand #4)
        "avif"                        // bytes [32..35] (compatible brand #5)
      ).encodeUtf8()
    assertThat(isAvif(Buffer().write(pseudoAvifString))).isTrue()
  }
}
