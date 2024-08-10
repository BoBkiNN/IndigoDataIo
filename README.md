<p align="center">
<img style="align: center" 
src="https://jitpack.io/v/BoBkiNN/IndigoDataIo.svg" alt="JitPack release">
</p>

---
## IndigoDataIo

This is library that adds a new way to manipulate keyed map-like data.
keys are like `inside.some.object.value` so there are no

Since version 3.0.0 TypeOps interface was introduced.
This interface adds methods to create types for serialization.
For example, we want to put int in gson's JsonObject, 
but we need to wrap it into JsonPrimitive for it and this operation of wrapping performs 
[GsonOps](indigodataio-gson/src/main/java/xyz/bobkinn/indigodataio/gson/GsonOps.java).
This system was based on Mojang's DataFixerUpper library
