package com.hotels.intellij.plugins.network.converter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.jpountz.lz4.LZ4BlockOutputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LZ4ByteBufToStringConverterTest {

    private LZ4ByteBufToStringConverter victim;

    @Before
    public void setUp() throws Exception {
        victim = new LZ4ByteBufToStringConverter();
    }

    @Test
    public void convertConvertsLZ4BlockAndResetsReaderIndex() throws Exception {
        //given
        String testMessageAsString = "TestMessage";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        LZ4BlockOutputStream lz4BlockOutputStream = new LZ4BlockOutputStream(byteArrayOutputStream);
        lz4BlockOutputStream.write(testMessageAsString.getBytes());
        lz4BlockOutputStream.close();

        ByteBuf byteBuf = Unpooled.copiedBuffer(byteArrayOutputStream.toByteArray());

        //when
        String decompressedString = victim.convert(byteBuf);

        //then
        assertThat(decompressedString, is(testMessageAsString));
        assertThat(byteBuf.readerIndex(), is(0));
    }

}