package com.danielkkrafft.wilddungeons.world.structure;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WDStructureTemplateManager {
    public static WDStructureTemplateManager INSTANCE;
    public static final String STRUCTURE_RESOURCE_DIRECTORY_NAME = "structure";
    private static final String STRUCTURE_GENERATED_DIRECTORY_NAME = "structures";
    private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
    private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
    private final Map<ResourceLocation, Optional<WDStructureTemplate>> structureRepository = Maps.newConcurrentMap();
    private final DataFixer fixerUpper;
    private ResourceManager resourceManager;
    private final Path generatedDir;
    private final List<Source> sources;
    private final HolderGetter<Block> blockLookup;
    private static final FileToIdConverter RESOURCE_LISTER = new FileToIdConverter(STRUCTURE_RESOURCE_DIRECTORY_NAME, STRUCTURE_FILE_EXTENSION);

    public WDStructureTemplateManager(ResourceManager resourceManager, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer fixerUpper, HolderGetter<Block> blockLookup) {
        this.resourceManager = resourceManager;
        this.fixerUpper = fixerUpper;
        this.generatedDir = levelStorageAccess.getLevelPath(LevelResource.GENERATED_DIR).normalize();
        this.blockLookup = blockLookup;
        ImmutableList.Builder<Source> builder = ImmutableList.builder();
        builder.add(new Source(this::loadFromGenerated, this::listGenerated));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            builder.add(new Source(this::loadFromTestStructures, this::listTestStructures));
        }

        builder.add(new Source(this::loadFromResource, this::listResources));
        this.sources = builder.build();
    }

    public static void init(ResourceManager resourceManager, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer fixerUpper, HolderGetter<Block> blockLookup) {
        INSTANCE = new WDStructureTemplateManager(resourceManager, levelStorageAccess, fixerUpper, blockLookup);
    }

    public WDStructureTemplate getOrCreate(ResourceLocation id) {
        Optional<WDStructureTemplate> optional = this.get(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            WDStructureTemplate structureTemplate = new WDStructureTemplate();
            this.structureRepository.put(id, Optional.of(structureTemplate));
            return structureTemplate;
        }
    }

    public Optional<WDStructureTemplate> get(ResourceLocation id) {
//        return tryLoad(id);
        return this.structureRepository.computeIfAbsent(id, this::tryLoad);
    }

    private Optional<WDStructureTemplate> tryLoad(ResourceLocation id) {
        for(Source structuretemplatemanager$source : this.sources) {
            try {
                Optional<WDStructureTemplate> optional = structuretemplatemanager$source.loader().apply(id);
                if (optional.isPresent()) {
                    return optional;
                }
            } catch (Exception ignored) {
            }
        }

        return Optional.empty();
    }

    public static PreparableReloadListener StructureTemplateManagerReloadListener = new PreparableReloadListener() {
        @Override
        public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller, @NotNull ProfilerFiller profilerFiller1, @NotNull Executor executor, @NotNull Executor executor1) {
            profilerFiller.startTick();
            profilerFiller.push("structure_template_reload");

            // Use the background executor for preparation work
            return CompletableFuture.supplyAsync(() -> {
                        if (WDStructureTemplateManager.INSTANCE != null) {
                            WDStructureTemplateManager.INSTANCE.onResourceManagerReload(resourceManager);
                        }
                        return null;
                    }, executor)
                    .thenCompose(preparationBarrier::wait)  // Wait for all preparation work to complete
                    .thenAcceptAsync(unused -> {
                        // Apply phase runs on the game thread (executor1)
                        profilerFiller1.startTick();
                        profilerFiller1.push("apply_structure_templates");
                        profilerFiller1.pop();
                        profilerFiller1.endTick();
                    }, executor1)
                    .whenComplete((unused, throwable) -> {
                        if (throwable != null) {
                            WildDungeons.getLogger().error("Failed to reload structure templates", throwable);
                        }
                        profilerFiller.pop();
                        profilerFiller.endTick();
                    });
        }
    };

    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.structureRepository.clear();
    }

    private Optional<WDStructureTemplate> loadFromResource(ResourceLocation id) {
        ResourceLocation resourcelocation = RESOURCE_LISTER.idToFile(id);
        return this.load(() -> this.resourceManager.open(resourcelocation), (p_230366_) ->  WildDungeons.getLogger().error("Couldn't load structure {}", id, p_230366_));
    }


    public Stream<ResourceLocation> listResources() {
        return RESOURCE_LISTER.listMatchingResources(this.resourceManager).keySet().stream()
                .map(RESOURCE_LISTER::fileToId);
    }

    private Optional<WDStructureTemplate> loadFromTestStructures(ResourceLocation id) {
        return this.loadFromSnbt(id, Paths.get(StructureUtils.testStructuresDir));
    }

    private Stream<ResourceLocation> listTestStructures() {
        Path path = Paths.get(StructureUtils.testStructuresDir);
        if (!Files.isDirectory(path)) {
            return Stream.empty();
        } else {
            List<ResourceLocation> list = new ArrayList<>();
            Objects.requireNonNull(list);
            this.listFolderContents(path, "minecraft", STRUCTURE_TEXT_FILE_EXTENSION, list::add);
            return list.stream();
        }
    }

    private Optional<WDStructureTemplate> loadFromGenerated(ResourceLocation id) {
        if (!Files.isDirectory(this.generatedDir)) {
            return Optional.empty();
        } else {
            Path path = this.createAndValidatePathToGeneratedStructure(id, STRUCTURE_FILE_EXTENSION);
            return this.load(() -> new FileInputStream(path.toFile()), (p_230400_) ->  WildDungeons.getLogger().error("Couldn't load structure from {}", path, p_230400_));
        }
    }

    public Stream<ResourceLocation> listGenerated() {
        if (!Files.isDirectory(this.generatedDir)) {
            return Stream.empty();
        } else {
            try {
                List<ResourceLocation> list = new ArrayList<>();

                try (DirectoryStream<Path> directorystream = Files.newDirectoryStream(this.generatedDir, Files::isDirectory)) {
                    for(Path path : directorystream) {
                        String s = path.getFileName().toString();
                        Path path1 = path.resolve(STRUCTURE_GENERATED_DIRECTORY_NAME);
                        if (Files.isDirectory(path1)) {
                            Objects.requireNonNull(list);
                            this.listFolderContents(path1, s, STRUCTURE_FILE_EXTENSION, list::add);
                        }
                    }
                }

                return list.stream();
            } catch (IOException var9) {
                return Stream.empty();
            }
        }
    }

    private void listFolderContents(Path folder, String namespace, String extension, Consumer<ResourceLocation> output) {
        int i = extension.length();
        Function<String, String> function = (p_230358_) -> p_230358_.substring(0, p_230358_.length() - i);

        try (Stream<Path> stream = Files.find(folder, Integer.MAX_VALUE, (p_352038_, p_352039_) -> p_352039_.isRegularFile() && p_352038_.toString().endsWith(extension))) {
            stream.forEach((p_352044_) -> {
                try {
                    output.accept(ResourceLocation.fromNamespaceAndPath(namespace, function.apply(this.relativize(folder, p_352044_))));
                } catch (ResourceLocationException resourcelocationexception) {
                    WildDungeons.getLogger().error("Invalid location while listing folder {} contents", folder, resourcelocationexception);
                }

            });
        } catch (IOException ioexception) {
            WildDungeons.getLogger().error("Failed to list folder {} contents", folder, ioexception);
        }

    }

    private String relativize(Path root, Path path) {
        return root.relativize(path).toString().replace(File.separator, "/");
    }

    private Optional<WDStructureTemplate> loadFromSnbt(ResourceLocation id, Path p_path) {
        if (!Files.isDirectory(p_path)) {
            return Optional.empty();
        } else {
            Path path = FileUtil.createPathToResource(p_path, id.getPath(), STRUCTURE_TEXT_FILE_EXTENSION);

            try (BufferedReader bufferedreader = Files.newBufferedReader(path)) {
                String s = IOUtils.toString(bufferedreader);
                return Optional.of(this.readStructure(NbtUtils.snbtToStructure(s)));
            } catch (NoSuchFileException var10) {
                return Optional.empty();
            } catch (IOException | CommandSyntaxException ioexception) {
                WildDungeons.getLogger().error("Couldn't load structure from {}", path, ioexception);
                return Optional.empty();
            }
        }
    }

    private Optional<WDStructureTemplate> load(InputStreamOpener inputStream, Consumer<Throwable> onError) {
        try {
            try (
                    InputStream inputstream = inputStream.open();
                    InputStream fastBufferedInputStream = new FastBufferedInputStream(inputstream)
            ) {
                WDStructureTemplate wdStructureTemplate = this.readStructure(fastBufferedInputStream);
                if (wdStructureTemplate == null)
                    return Optional.empty();
                return Optional.of(wdStructureTemplate);
            }
        } catch (FileNotFoundException var12) {
            return Optional.empty();
        } catch (Throwable throwable1) {
            onError.accept(throwable1);
            return Optional.empty();
        }
    }

    private WDStructureTemplate readStructure(InputStream stream) throws IOException {
        CompoundTag compoundtag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        return this.readStructure(compoundtag);
    }

    public WDStructureTemplate readStructure(CompoundTag nbt) {
        WDStructureTemplate structuretemplate = new WDStructureTemplate();
        int i = NbtUtils.getDataVersion(nbt, 500);
        structuretemplate.load(this.blockLookup, DataFixTypes.STRUCTURE.updateToCurrentVersion(this.fixerUpper, nbt, i));
        if (structuretemplate.innerTemplates.isEmpty())
            return null;
        return structuretemplate;
    }

    public boolean save(ResourceLocation id) {
        Optional<WDStructureTemplate> optional = this.structureRepository.get(id);
        if (optional.isEmpty()) {
            return false;
        } else {
            WDStructureTemplate structuretemplate = optional.get();
            Path path = this.createAndValidatePathToGeneratedStructure(id, STRUCTURE_FILE_EXTENSION);
            Path path1 = path.getParent();
            if (path1 == null) {
                return false;
            } else {
                try {
                    Files.createDirectories(Files.exists(path1) ? path1.toRealPath() : path1);
                } catch (IOException var13) {
                    WildDungeons.getLogger().error("Failed to create parent directory: {}", path1);
                    return false;
                }

                CompoundTag compoundtag = structuretemplate.save(new CompoundTag());

                try {
                    try (OutputStream outputstream = new FileOutputStream(path.toFile())) {
                        NbtIo.writeCompressed(compoundtag, outputstream);
                    }

                    return true;
                } catch (Throwable var12) {
                    return false;
                }
            }
        }
    }

    public Path createAndValidatePathToGeneratedStructure(ResourceLocation location, String extension) {
        if (location.getPath().contains("//")) {
            throw new ResourceLocationException("Invalid resource path: " + location);
        } else {
            try {
                Path path = this.generatedDir.resolve(location.getNamespace());
                Path path1 = path.resolve(STRUCTURE_GENERATED_DIRECTORY_NAME);
                Path path2 = FileUtil.createPathToResource(path1, location.getPath(), extension);
                if (path2.startsWith(this.generatedDir) && FileUtil.isPathNormalized(path2) && FileUtil.isPathPortable(path2)) {
                    return path2;
                } else {
                    throw new ResourceLocationException("Invalid resource path: " + path2);
                }
            } catch (InvalidPathException invalidpathexception) {
                throw new ResourceLocationException("Invalid resource path: " + location, invalidpathexception);
            }
        }
    }

    public void remove(ResourceLocation id) {
        this.structureRepository.remove(id);
    }

    record Source(Function<ResourceLocation, Optional<WDStructureTemplate>> loader, Supplier<Stream<ResourceLocation>> lister) {

        public Function<ResourceLocation, Optional<WDStructureTemplate>> loader() {
            return this.loader;
        }
    }

    @FunctionalInterface
    interface InputStreamOpener {
        InputStream open() throws IOException;
    }

    public HolderGetter<Block> getBlockLookup() {
        return blockLookup;
    }
}
