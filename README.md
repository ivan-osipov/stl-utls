# stl-utls
Stl binary &amp; ascii parser with flexible policy to compute normals

```kotlin
val bytes = YourClass::class.java.getResourceAsStream("your_file.stl").readBytes()
val triangles = StlParsingManager(NormalPolicy.COMPUTED).parse(bytes) //MIXED (compute if undefined) policy by default
```
