package com.bapplications.maplemobile.opengl.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class QuadsBuffer {

    private int len;
    ByteBuffer quadsBuffer;

    public QuadsBuffer(){
        this(1000000);
    }

    public QuadsBuffer(int size)
    {
        len = 0;
        quadsBuffer = ByteBuffer.allocate(size);
    }


    public void put(Quad quad) {
        for(Quad.Vertex v : quad.vertices) {
            quadsBuffer.putShort((short) v.localx);
            quadsBuffer.putShort((short) v.localy);
            quadsBuffer.putShort((short) v.taxturex);
            quadsBuffer.putShort((short) v.taxturey);
//            quadsBuffer.putInt(v.color.getEnc());

            len += Quad.Vertex.SIZEOF;
        }
    }

    public void put(int left, int right, int top, int bottom, Offset offset, Color color, float angle) {
        put(new Quad(left, right, top, bottom, offset, color, angle));
    }

    public void clear()
    {
        len = 0;
        quadsBuffer.clear();
    }

    public int size() {
        return len;
    }

    public Buffer data() {
        quadsBuffer.rewind();
        return quadsBuffer;
    }
}
