<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>bsl (byby standard library) - Fabric Helper Library Documentation</title>
</head>
<body>

<h1>bsl (byby standard library) - Fabric Helper Library Documentation</h1>

<h2>Overview</h2>
<p>
    bsl is a lightweight helper library designed to supplement Fabric mod development by providing convenient APIs for UI buttons, keybindings, client tick events, resource reload handling, and JSON config management. It aims to help mod developers easily add features common to Fabric mods, enabling feature parity closer to Quilt and other mod loaders.
</p>

<hr />

<h2>Integration</h2>

<h3>1. Using bsl as part of your mod source code</h3>
<p>If you are developing your Fabric mod and want to use bsl directly within the same project (recommended for easy debugging and iteration):</p>
<ul>
    <li>Include the entire <code>bsl</code> source code inside your project's <code>src/main/java</code> directory under the <code>bsl</code> package.</li>
    <li>In your mod code, import and use bsl normally:</li>
</ul>
<pre><code>import bsl.bsl;

public class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register a button on a specific screen
        bsl.addButton(SomeScreen.class, "Click me!", 10, 10, 100, 20, () -&gt; {
            System.out.println("Button clicked!");
        });

        // Register client tick event
        bsl.onClientTick(() -&gt; {
            // Your tick logic here
        });
    }
}
</code></pre>
<p>No special build setup is needed beyond standard Fabric Loom configuration.</p>

<hr />

<h3>2. Using bsl as a separate JAR dependency</h3>
<p>If you want to distribute bsl as a standalone library for reuse in multiple projects or keep it modular:</p>
<ul>
    <li>Build bsl into a JAR file.</li>
    <li>Add it to your mod’s <code>libs</code> folder or publish it to a Maven repository.</li>
    <li>Add the dependency in your <code>build.gradle</code> file:</li>
</ul>
<pre><code>dependencies {
    modImplementation files('libs/bsl.jar')
    // or if published:
    // modImplementation 'com.byby:bsl:1.0.0'
}
</code></pre>
<p>Import bsl in your mod code as usual:</p>
<pre><code>import bsl.bsl;
</code></pre>
<p>Make sure the Fabric API and Minecraft dependencies are consistent across your mod and bsl builds.</p>

<hr />

<h2>Features</h2>

<h3>Screen Button Registry</h3>
<p>Easily register buttons on any Minecraft screen.</p>
<pre><code>bsl.addButton(SomeScreen.class, "Button Text", x, y, width, height, () -&gt; {
    // Button pressed action
});
</code></pre>

<h3>Client Tick Events</h3>
<p>Register callbacks that run every client tick.</p>
<pre><code>bsl.onClientTick(() -&gt; {
    // Tick logic
});
</code></pre>

<h3>Resource Reload Events</h3>
<p>Register callbacks for resource reload events.</p>
<pre><code>bsl.onResourceReload(manager -&gt; {
    // Reload logic
});
</code></pre>

<h3>Keybinding API</h3>
<p>Register and query keybindings simply.</p>
<pre><code>KeyBinding myKey = bsl.registerKey("key.my_mod.my_key", GLFW.GLFW_KEY_K);

if (bsl.isKeyPressed(myKey)) {
    // Do something when key is pressed
}
</code></pre>

<h3>JSON Config API</h3>
<p>Save/load JSON config files in the config folder.</p>
<pre><code>MyConfig cfg = bsl.loadConfig("myconfig.json", MyConfig.class, new MyConfig());
bsl.saveConfig("myconfig.json", cfg);
</code></pre>

<hr />

<h3>Important Notes</h3>
<ul>
    <li>bsl depends on Fabric API and Minecraft classes and must be compiled with Fabric Loom and appropriate mappings.</li>
    <li>When including bsl source in your mod project, make sure your IDE and build system recognize the package structure correctly.</li>
    <li>When building bsl as a JAR, ensure that the target Minecraft and Fabric API versions match those of your mod to avoid runtime incompatibilities.</li>
    <li>Some @Shadow warnings during compilation may appear if the target methods or fields are missing in the Minecraft version used; these usually do not prevent the mod from working but should be addressed for clean builds.</li>
</ul>

<hr />

<h3>Example: Basic usage in a mod client initializer</h3>
<pre><code>package mymod;

import bsl.bsl;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.TitleScreen;

public class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        bsl.addButton(TitleScreen.class, "Hello bsl!", 10, 10, 100, 20, () -&gt; {
            System.out.println("Button clicked!");
        });
    }
}
</code></pre>

</body>
</html>
