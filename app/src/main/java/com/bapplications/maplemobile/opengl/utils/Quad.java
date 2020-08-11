package com.bapplications.maplemobile.opengl.utils;

public class Quad {

    public static final int LENGTH = 4;

    public static class Vertex {
        public static final int SIZEOF = 8;
        int localx;
        int localy;
        int taxturex;
        int taxturey;
        Color color;

        public Vertex(int lx, int ly, int tx, int ty, Color color){
            localx = lx;
            localy = ly;
            taxturex = tx;
            taxturey = ty;
            this.color = color;
        }
    }

    Vertex[] vertices;
    public Quad(int left, int right, int top, int bottom, Offset offset, Color color, float rotation)
    {
        vertices = new Vertex[LENGTH];
        vertices[0] = new Vertex(left, top, offset.left, offset.top, color);
        vertices[1] = new Vertex(left, bottom, offset.left, offset.bottom, color);
        vertices[2] = new Vertex(right, bottom, offset.right, offset.bottom, color);
        vertices[3] = new Vertex(right, top, offset.right, offset.top, color);

        if (rotation != 0.0f)
        {
            float cos = (float) Math.cos(rotation);
            float sin = (float) Math.sin(rotation);
            float center_x = (float) ((left + right) / 2.);
            float center_y = (float) ((top + bottom) / 2.);

            for (int i = 0; i < LENGTH; i++)
            {
                short vertice_x = (short) (vertices[i].localx - center_x);
                short vertice_y = (short) (vertices[i].localy - center_y);
                float rounded_x = (float) Math.floor(vertice_x * cos - vertice_y * sin);
                float rounded_y = (float) Math.floor(vertice_x * sin + vertice_y * cos);
                vertices[i].localx = (short) (rounded_x + center_x);
                vertices[i].localy = (short) (rounded_y + center_y);
            }
        }
    }
}
