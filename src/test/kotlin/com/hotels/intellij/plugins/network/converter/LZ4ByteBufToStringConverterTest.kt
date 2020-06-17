package com.hotels.intellij.plugins.network.converter

import io.netty.buffer.Unpooled
import net.jpountz.lz4.LZ4BlockOutputStream
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.ByteArrayOutputStream

class LZ4ByteBufToStringConverterTest {

    private var victim = LZ4ByteBufToStringConverter()

    @Test
    @Throws(Exception::class)
    fun convertConvertsLZ4BlockAndResetsReaderIndex() {
        //given
        val testMessageAsString = "TestMessage"
        val byteArrayOutputStream = ByteArrayOutputStream()
        val lz4BlockOutputStream = LZ4BlockOutputStream(byteArrayOutputStream)
        lz4BlockOutputStream.write(testMessageAsString.toByteArray())
        lz4BlockOutputStream.close()
        val byteBuf = Unpooled.copiedBuffer(byteArrayOutputStream.toByteArray())

        //when
        val decompressedString = victim.convert(byteBuf)

        //then
        assertThat(decompressedString, `is`(testMessageAsString))
        assertThat(byteBuf.readerIndex(), `is`(0))
    }
}