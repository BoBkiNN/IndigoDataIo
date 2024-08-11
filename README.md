<p align="center">
<img style="align: center" 
src="https://jitpack.io/v/BoBkiNN/IndigoDataIo.svg" alt="JitPack release">
<img alt="GitHub Release" 
src="https://img.shields.io/github/v/release/BoBkiNN/IndigoDataIO">
</p>

---
## IndigoDataIo

This is library that adds a new way to manipulate keyed map-like data.
Keys is path like `inside.some.object.value` that will be used to get `value` from deep object.

Since version 3.0.0 TypeOps interface was introduced.
This interface adds methods to create types for serialization.
For example, we want to put int in gson's JsonObject, 
but we need to wrap it into JsonPrimitive for it and this operation of wrapping performs 
[GsonOps](indigodataio-gson/src/main/java/xyz/bobkinn/indigodataio/gson/GsonOps.java).
This system was based on Mojang's DataFixerUpper library

### Usage in maven:
Repository:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Gson artifact: Provides support for gson library and `GsonOps` with `GsonData` and `JsonIo`
```xml
<dependency>
    <groupId>com.github.BoBkiNN.IndigoDataIo</groupId>
    <artifactId>gson</artifactId>
    <version>3.0.1</version>
</dependency>
```
Version with only `MapOps` (Simple java Map using `NestedKeyMap`) support
```xml
<dependency>
    <groupId>com.github.BoBkiNN.IndigoDataIo</groupId>
    <artifactId>indigodataio</artifactId>
    <version>3.0.1</version>
</dependency>
```
