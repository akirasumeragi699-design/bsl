# bsl (byby standard library) - Fabric Helper Library Documentation

## Overview

bsl is a lightweight helper library designed to supplement Fabric mod development by providing convenient APIs for UI buttons, keybindings, client tick events, resource reload handling, and JSON config management. It aims to help mod developers easily add features common to Fabric mods, enabling feature parity closer to Quilt and other mod loaders.

---

## Integration

### 1. Using bsl as part of your mod source code

If you are developing your Fabric mod and want to use bsl directly within the same project (recommended for easy debugging and iteration):

- Include the entire `bsl` source code inside your project's `src/main/java` directory under the `bsl` package.

- In your mod code, import and use bsl normally:

```java
import bsl.bsl;

public class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register a button on a specific screen
        bsl.addButton(SomeScreen.class, "Click me!", 10, 10, 100, 20, () -> {
            System.out.println("Button clicked!");
        });

        // Register client tick event
        bsl.onClientTick(() -> {
            // Your tick logic here
        });
    }
}

No special build setup is needed beyond standard Fabric Loom configuration.
2. Using bsl as a separate JAR dependency

If you want to distribute bsl as a standalone library for reuse in multiple projects or keep it modular:

    Build bsl into a JAR file.

    Add it to your mod’s libs folder or publish it to a Maven repository.

    Add the dependency in your build.gradle file:

dependencies {
    modImplementation files('libs/bsl.jar')
    // or if published:
    // modImplementation 'com.byby:bsl:1.0.0'
}

    Import bsl in your mod code as usual:

import bsl.bsl;

Make sure the Fabric API and Minecraft dependencies are consistent across your mod and bsl builds.
Features
Screen Button Registry

Easily register buttons on any Minecraft screen.

bsl.addButton(SomeScreen.class, "Button Text", x, y, width, height, () -> {
    // Button pressed action
});

Client Tick Events

Register callbacks that run every client tick.

bsl.onClientTick(() -> {
    // Tick logic
});

Resource Reload Events

Register callbacks for resource reload events.

bsl.onResourceReload(manager -> {
    // Reload logic
});

Keybinding API

Register and query keybindings simply.

KeyBinding myKey = bsl.registerKey("key.my_mod.my_key", GLFW.GLFW_KEY_K);

if (bsl.isKeyPressed(myKey)) {
    // Do something when key is pressed
}

JSON Config API

Save/load JSON config files in the config folder.

MyConfig cfg = bsl.loadConfig("myconfig.json", MyConfig.class, new MyConfig());
bsl.saveConfig("myconfig.json", cfg);

Important Notes

    bsl depends on Fabric API and Minecraft classes and must be compiled with Fabric Loom and appropriate mappings.

    When including bsl source in your mod project, make sure your IDE and build system recognize the package structure correctly.

    When building bsl as a JAR, ensure that the target Minecraft and Fabric API versions match those of your mod to avoid runtime incompatibilities.

    Some @Shadow warnings during compilation may appear if the target methods or fields are missing in the Minecraft version used; these usually do not prevent the mod from working but should be addressed for clean builds.

Example: Basic usage in a mod client initializer

package mymod;

import bsl.bsl;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.TitleScreen;

public class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        bsl.addButton(TitleScreen.class, "Hello bsl!", 10, 10, 100, 20, () -> {
            System.out.println("Button clicked!");
        });
    }
}
