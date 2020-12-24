
uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
attribute vec2 aTextureCoordinate;
varying vec2 vTexCoordinate;
void main() {
  gl_Position = uMVPMatrix * vPosition;
  vTexCoordinate = aTextureCoordinate;
}