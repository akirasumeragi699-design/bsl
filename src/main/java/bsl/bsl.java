package bsl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class bsl implements ClientModInitializer {
    /* -------- Screen Button Registry -------- */
    public record ButtonEntry(Class<? extends Screen> targetScreen, String text,
                              int x, int y, int width, int height, Runnable onPress) {}
    public static final List<ButtonEntry> BUTTONS = new ArrayList<>();

    public static void addButton(Class<? extends Screen> targetScreen, String text,
                                 int x, int y, int width, int height, Runnable onPress) {
        BUTTONS.add(new ButtonEntry(targetScreen, text, x, y, width, height, onPress));
    }

    /* -------- Client Tick Events -------- */
    public static final List<Runnable> CLIENT_TICK_EVENTS = new ArrayList<>();
    public static void onClientTick(Runnable listener) {
        CLIENT_TICK_EVENTS.add(listener);
    }

    /* -------- Resource Reload Events -------- */
    public static final List<Consumer<ResourceManager>> RESOURCE_RELOAD_EVENTS = new ArrayList<>();
    public static void onResourceReload(Consumer<ResourceManager> listener) {
        RESOURCE_RELOAD_EVENTS.add(listener);
    }

    /* -------- Keybinding API -------- */
    private static final List<KeyBinding> KEYS = new ArrayList<>();
    public static KeyBinding registerKey(String translationKey, int key) {
        KeyBinding kb = new KeyBinding(translationKey, InputUtil.Type.KEYSYM, key, "category.bsl");
        KeyBindingHelper.registerKeyBinding(kb);
        KEYS.add(kb);
        return kb;
    }
    public static boolean isKeyPressed(KeyBinding key) {
        return key.isPressed();
    }

    /* -------- Config API (JSON) -------- */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static <T> void saveConfig(String filename, T data) {
        try {
            Path configDir = MinecraftClient.getInstance().runDirectory.toPath().resolve("config");
            File file = configDir.resolve(filename).toFile();
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static <T> T loadConfig(String filename, Class<T> type, T defaultValue) {
        try {
            Path configDir = MinecraftClient.getInstance().runDirectory.toPath().resolve("config");
            File file = configDir.resolve(filename).toFile();
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    return GSON.fromJson(reader, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /* -------- Init -------- */
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CLIENT_TICK_EVENTS.forEach(r -> {
                try { r.run(); } catch (Throwable t) { t.printStackTrace(); }
            });
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("bsl", "resource_reload_listener");
            }
            @Override
            public void reload(ResourceManager manager) {
                RESOURCE_RELOAD_EVENTS.forEach(ev -> {
                    try { ev.accept(manager); } catch (Throwable t) { t.printStackTrace(); }
                });
            }
        });
    }

    /* -------- Screen Mixin -------- */
    @Mixin(Screen.class)
    public static abstract class ScreenMixin {
        @Shadow
        protected abstract <T extends net.minecraft.client.gui.Element & net.minecraft.client.gui.Drawable & net.minecraft.client.gui.Selectable> T addDrawableChild(T drawableElement);

        @Inject(method = "init", at = @At("RETURN"))
        private void bsl$addCustomButtons(CallbackInfo ci) {
            Screen self = (Screen)(Object)this;

            bsl.BUTTONS.forEach(entry -> {
                if (entry.targetScreen().isInstance(self)) {
                    this.addDrawableChild(
                        ButtonWidget.builder(Text.literal(entry.text()), b -> entry.onPress().run())
                            .dimensions(entry.x(), entry.y(), entry.width(), entry.height())
                            .build()
                    );
                }
            });
        }
    }

    /* -------- TitleScreen Mixin -------- */
    @Mixin(TitleScreen.class)
    public static abstract class TitleScreenMixin {
        @Shadow
        protected abstract <T extends net.minecraft.client.gui.Element & net.minecraft.client.gui.Drawable & net.minecraft.client.gui.Selectable> T addDrawableChild(T drawableElement);

        @Inject(method = "initWidgetsNormal", at = @At("RETURN"))
        private void addModsButton(int y, int spacingY, CallbackInfo ci) {
            this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("bsl.customButton"),
                button -> MinecraftClient.getInstance().setScreen(new SelectWorldScreen(null))
            ).dimensions(MinecraftClient.getInstance().currentScreen.width / 2 - 200 + 252, y, 50, 20).build());
        }
    }
}
