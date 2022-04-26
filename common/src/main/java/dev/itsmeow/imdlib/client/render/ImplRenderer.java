package dev.itsmeow.imdlib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.itsmeow.imdlib.entity.interfaces.IVariantTypes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ImplRenderer<T extends Mob, A extends EntityModel<T>> extends BaseRenderer<T, A> {

    private final TextureContainer<T, A> textureContainer;
    private final ModelContainer<T, A> modelContainer;
    private final PreRenderCallback<T> preRenderCallback;
    private final HandleRotation<T> handleRotation;
    private final ApplyRotations<T> applyRotations;
    private final SuperCallApplyRotations applyRotationsSuper;
    private final RenderLayer<T> renderLayer;
    private final BlockLightLevel<T> blockLightLevel;

    public ImplRenderer(EntityRendererProvider.Context ctx, float shadow, TextureContainer<T, A> textureContainer, ModelContainer<T, A> modelContainer, PreRenderCallback<T> preRenderCallback, HandleRotation<T> handleRotation, ApplyRotations<T> applyRotations, SuperCallApplyRotations applyRotationsSuper, RenderLayer<T> renderLayer, BlockLightLevel<T> blockLightLevel) {
        super(ctx, modelContainer.getBaseModel(ctx), shadow);
        this.textureContainer = textureContainer;
        this.modelContainer = modelContainer;
        this.preRenderCallback = preRenderCallback;
        this.handleRotation = handleRotation;
        this.applyRotations = applyRotations;
        this.applyRotationsSuper = applyRotationsSuper;
        this.renderLayer = renderLayer;
        this.blockLightLevel = blockLightLevel;
    }

    public static <T extends Mob, A extends EntityModel<T>> Builder<T, A> factory(String modid, float shadow) {
        return new Builder<>(modid, shadow);
    }

    private static ResourceLocation tex(String modid, String location) {
        return new ResourceLocation(modid, "textures/entity/" + location + ".png");
    }

    private static ModelLayerLocation mll(String modid, String location) {
        return new ModelLayerLocation(new ResourceLocation(modid, location), "main");
    }

    @Override
    protected void setupRotations(T e, PoseStack s, float a, float y, float p) {
        if (applyRotations == null) {
            super.setupRotations(e, s, a, y, p);
        } else {
            if (applyRotationsSuper == SuperCallApplyRotations.PRE) {
                super.setupRotations(e, s, a, y, p);
            }
            applyRotations.applyRotations(e, s, a, y, p);
            if (applyRotationsSuper == SuperCallApplyRotations.POST) {
                super.setupRotations(e, s, a, y, p);
            }
        }
    }

    @Override
    protected void scale(T e, PoseStack s, float p) {
        if (preRenderCallback != null) {
            preRenderCallback.preRenderCallback(e, s, p);
        }
    }

    @Override
    protected float getBob(T e, float p) {
        return handleRotation == null ? super.getBob(e, p) : handleRotation.handleRotation(e, p);
    }

    @Override
    protected RenderType getRenderType(T entity, boolean visible, boolean visibleToPlayer, boolean glowing) {
        return renderLayer == null ? super.getRenderType(entity, visible, visibleToPlayer, glowing) : renderLayer.renderLayer(entity, visible, visibleToPlayer, glowing, this.getTextureLocation(entity));
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos blockPos) {
        return blockLightLevel == null ? super.getBlockLightLevel(entity, blockPos) : blockLightLevel.blockLightLevel(entity, blockPos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(T e, float p_225623_2_, float p_225623_3_, PoseStack p_225623_4_, MultiBufferSource p_225623_5_, int p_225623_6_) {
        this.model = (A) modelContainer.getModel(e);
        super.render(e, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return textureContainer.getTexture(entity);
    }

    public enum Strategy {
        SINGLE,
        MAPPER,
        MAPPER_CONDITION,
        CONDITION
    }

    public enum SuperCallApplyRotations {
        PRE,
        NONE,
        POST
    }

    @FunctionalInterface
    public interface PreRenderCallback<T extends Mob> {
        void preRenderCallback(T entity, PoseStack stack, float partialTicks);
    }

    @FunctionalInterface
    public interface HandleRotation<T extends Mob> {
        float handleRotation(T entity, float partialTicks);
    }

    @FunctionalInterface
    public interface ApplyRotations<T extends Mob> {
        void applyRotations(T entity, PoseStack stack, float ageInTicks, float rotationYaw, float partialTicks);
    }

    @FunctionalInterface
    public interface RenderLayer<T extends Mob> {
        RenderType renderLayer(T entity, boolean visible, boolean visibleToPlayer, boolean glowing, ResourceLocation texture);
    }

    @FunctionalInterface
    public interface RenderDef<T extends Mob, A extends EntityModel<T>> {
        ImplRenderer.Builder<T, A> apply(ImplRenderer.Builder<T, A> renderer);
    }

    @FunctionalInterface
    public interface BlockLightLevel<T extends Mob> {
        int blockLightLevel(T entity, BlockPos pos);
    }

    public static class TextureContainer<T extends Mob, A extends EntityModel<T>> {

        private final Strategy strategy;
        private ResourceLocation singleTexture;
        private Function<T, ResourceLocation> texMapper;
        private ResourceLocation trueTex;
        private ResourceLocation falseTex;
        private Predicate<T> condition;
        private ResourceLocation conditionTex;

        public TextureContainer(ResourceLocation singleTexture) {
            this.strategy = Strategy.SINGLE;
            this.singleTexture = singleTexture;
        }

        public TextureContainer(Function<T, ResourceLocation> texMapper) {
            this.strategy = Strategy.MAPPER;
            this.texMapper = texMapper;
        }

        public TextureContainer(Predicate<T> condition, Function<T, ResourceLocation> texMapper, ResourceLocation conditionTex) {
            this.strategy = Strategy.MAPPER_CONDITION;
            this.texMapper = texMapper;
            this.condition = condition;
            this.conditionTex = conditionTex;
        }

        public TextureContainer(Predicate<T> condition, ResourceLocation trueTex, ResourceLocation falseTex) {
            this.strategy = Strategy.CONDITION;
            this.condition = condition;
            this.trueTex = trueTex;
            this.falseTex = falseTex;
        }

        public ResourceLocation getTexture(T entity) {
            switch (strategy) {
                case SINGLE:
                    return singleTexture;
                case MAPPER:
                    return texMapper.apply(entity);
                case CONDITION:
                    return condition.test(entity) ? trueTex : falseTex;
                case MAPPER_CONDITION:
                    return condition.test(entity) ? conditionTex : texMapper.apply(entity);
                default:
                    break;
            }
            return null;
        }
    }

    public static class ModelContainer<T extends Mob, A extends EntityModel<T>> {

        private final String modId;
        private final Strategy strategy;
        private final Function<EntityRendererProvider.Context, A> baseModelProvider;
        private final Set<Pair<String, Function<EntityRendererProvider.Context, EntityModel<T>>>> modelEntries = new HashSet<>();
        private final Map<String, EntityModel<T>> builtModelMap = new HashMap<>();
        private Function<T, String> modelKeyMapper;
        private Function<EntityRendererProvider.Context, EntityModel<T>> falseModelProvider;
        private Predicate<T> condition;
        private A baseModel;
        private A trueModel;
        private EntityModel<T> falseModel;

        public ModelContainer(String modid, Function<ModelPart, A> baseModel, String modelLayerLocation) {
            this.modId = modid;
            this.strategy = Strategy.SINGLE;
            this.baseModelProvider = ctx -> baseModel.apply(ctx.bakeLayer(mll(modelLayerLocation)));
        }

        public ModelContainer(String modid, Function<T, String> modelMapper, Function<ModelPart, A> baseModel, String baseModelLocation) {
            this.modId = modid;
            this.strategy = Strategy.MAPPER;
            this.modelKeyMapper = modelMapper;
            this.baseModelProvider = convert(baseModel, baseModelLocation);
        }

        public ModelContainer(String modid, Predicate<T> condition, Function<ModelPart, A> trueModel, String trueLayerLocation, Function<ModelPart, EntityModel<T>> falseModel, String falseLayerLocation) {
            this.modId = modid;
            this.strategy = Strategy.CONDITION;
            this.condition = condition;
            this.baseModelProvider = convert(trueModel, trueLayerLocation);
            this.falseModelProvider = convert(falseModel, falseLayerLocation);
        }

        private <Z extends EntityModel<T>> Function<EntityRendererProvider.Context, Z> convert(Function<ModelPart, Z> m, String location) {
            return ctx -> m.apply(ctx.bakeLayer(mll(location)));
        }

        public void addMapperEntry(Function<ModelPart, EntityModel<T>> modelSupplier, String modelLayerLocation) {
            modelEntries.add(Pair.of(modelLayerLocation, convert(modelSupplier, modelLayerLocation)));
        }

        public void provideContext(EntityRendererProvider.Context ctx) {
            this.baseModel = baseModelProvider.apply(ctx);
            if(strategy == Strategy.MAPPER) {
                for (Pair<String, Function<EntityRendererProvider.Context, EntityModel<T>>> pair : modelEntries) {
                    builtModelMap.put(pair.getLeft(), pair.getRight().apply(ctx));
                }
            } else if(strategy == Strategy.CONDITION) {
                this.trueModel = baseModel;
                this.falseModel = falseModelProvider.apply(ctx);
            }
        }

        public EntityModel<T> getModel(T entity) {
            if(baseModel == null) {
                throw new RuntimeException("getModel called before provideContext!");
            }
            switch (strategy) {
                case SINGLE:
                    return baseModel;
                case MAPPER:
                    return getModelForKey(modelKeyMapper.apply(entity));
                case CONDITION:
                    return condition.test(entity) ? trueModel : falseModel;
                default:
                    return baseModel;
            }
        }

        public EntityModel<T> getModelForKey(String key) {
            return builtModelMap.getOrDefault(key, baseModel);
        }

        public A getBaseModel(EntityRendererProvider.Context ctx) {
            if(baseModel == null) {
                this.provideContext(ctx);
            }
            return baseModel;
        }

        private ModelLayerLocation mll(String loc) {
            return ImplRenderer.mll(modId, loc);
        }
    }

    public static class Builder<T extends Mob, A extends EntityModel<T>> {

        private final String modid;
        private final float shadow;
        private final ArrayList<Function<BaseRenderer<T, A>, net.minecraft.client.renderer.entity.layers.RenderLayer<T, A>>> layers = new ArrayList<>();
        private final Map<String, ResourceLocation> texMapper = new HashMap<>();
        private TextureContainer<T, A> tex;
        private ModelContainer<T, A> model;
        private PreRenderCallback<T> preRender;
        private HandleRotation<T> handleRotation;
        private ApplyRotations<T> applyRotations;
        private SuperCallApplyRotations superCallApplyRotations = SuperCallApplyRotations.NONE;
        private RenderLayer<T> renderLayer;
        private BlockLightLevel<T> blockLightLevel;

        protected Builder(String modid, float shadow) {
            this.modid = modid;
            this.shadow = shadow;
        }

        public Builder<T, A> layer(Function<BaseRenderer<T, A>, net.minecraft.client.renderer.entity.layers.RenderLayer<T, A>> layer) {
            layers.add(layer);
            return this;
        }

        public Builder<T, A> tSingle(String texture) {
            this.tex = new TextureContainer<>(tex(modid, texture));
            return this;
        }

        public Builder<T, A> tCondition(Predicate<T> condition, String trueTex, String falseTex) {
            this.tex = new TextureContainer<>(condition, tex(modid, trueTex), tex(modid, falseTex));
            return this;
        }

        public Builder<T, A> tMapped(Function<T, String> texMapper) {
            this.tex = new TextureContainer<>(entity -> texStored(texMapper.apply(entity)));
            return this;
        }

        public Builder<T, A> tSingleRaw(ResourceLocation texture) {
            this.tex = new TextureContainer<>(texture);
            return this;
        }

        public Builder<T, A> tConditionRaw(Predicate<T> condition, ResourceLocation trueTex, ResourceLocation falseTex) {
            this.tex = new TextureContainer<>(condition, trueTex, falseTex);
            return this;
        }

        public Builder<T, A> tMappedRaw(Function<T, ResourceLocation> texMapper) {
            this.tex = new TextureContainer<>(texMapper);
            return this;
        }

        public Builder<T, A> tVariant() {
            return tMappedRaw(e -> {
                if (e instanceof IVariantTypes<?>) {
                    return ((IVariantTypes<?>) e).getVariantTextureOrNull();
                }
                return null;
            });
        }

        public Builder<T, A> tMappedConditionRaw(Predicate<T> condition, Function<T, ResourceLocation> texMapper, String conditionTex) {
            return tMappedConditionRaw(condition, texMapper, tex(modid, conditionTex));
        }

        public Builder<T, A> tMappedConditionRaw(Predicate<T> condition, Function<T, ResourceLocation> texMapper, ResourceLocation conditionTex) {
            this.tex = new TextureContainer<>(condition, texMapper, conditionTex);
            return this;
        }

        public Builder<T, A> tVariantCondition(Predicate<T> condition, String conditionTex) {
            return tVariantCondition(condition, tex(modid, conditionTex));
        }

        public Builder<T, A> tVariantCondition(Predicate<T> condition, ResourceLocation conditionTex) {
            this.tex = new TextureContainer<>(condition, e -> {
                if (e instanceof IVariantTypes<?>) {
                    return ((IVariantTypes<?>) e).getVariantTextureOrNull();
                }
                return null;
            }, conditionTex);
            return this;
        }

        public Builder<T, A> tBabyVariant(String babyTex) {
            return tVariantCondition(e -> {
                if (e instanceof AgeableMob) {
                    return e.isBaby();
                }
                return false;
            }, tex(modid, babyTex));
        }

        public Builder<T, A> mSingle(Function<ModelPart, A> modelSupplier, String layerLocation) {
            this.model = new ModelContainer<>(modid, modelSupplier, layerLocation);
            return this;
        }

        public Builder<T, A> mMapped(Function<T, String> modelLocationMapper, Function<ModelPart, A> baseModelSupplier, String baseModelLayerLocation) {
            this.model = new ModelContainer<>(modid, modelLocationMapper, baseModelSupplier, baseModelLayerLocation);
            return this;
        }

        public Builder<T, A> mEntry(Function<ModelPart, EntityModel<T>> modelSupplier, String modelLayerLocation) {
            if(this.model == null || this.model.strategy != Strategy.MAPPER) {
                throw new RuntimeException("Must call mMapped before mEntry!");
            }
            this.model.addMapperEntry(modelSupplier, modelLayerLocation);
            return this;
        }

        public Builder<T, A> mCondition(Predicate<T> condition, Function<ModelPart, A> trueModelSupplier, String trueLayerLocation,Function<ModelPart, EntityModel<T>> falseModelSupplier, String falseLayerLocation) {
            this.model = new ModelContainer<>(modid, condition, trueModelSupplier, trueLayerLocation, falseModelSupplier, falseLayerLocation);
            return this;
        }

        public Builder<T, A> preRender(PreRenderCallback<T> preRender) {
            this.preRender = preRender;
            return this;
        }

        public Builder<T, A> simpleScale(Function<T, Float> function) {
            preRender((e, s, p) -> {
                float scale = function.apply(e);
                s.scale(scale, scale, scale);
            });
            return this;
        }

        public Builder<T, A> condScale(Predicate<T> cond, float xScale, float yScale, float zScale) {
            preRender((e, s, p) -> {
                if (cond.test(e)) {
                    s.scale(xScale, yScale, zScale);
                }
            });
            return this;
        }

        public Builder<T, A> condScale(Predicate<T> cond, float scale) {
            return condScale(cond, scale, scale, scale);
        }

        public Builder<T, A> condDualScale(Predicate<T> cond, float truexScale, float trueyScale, float truezScale, float falsexScale, float falseyScale, float falsezScale) {
            preRender((e, s, p) -> {
                if (cond.test(e)) {
                    s.scale(truexScale, trueyScale, truezScale);
                } else {
                    s.scale(falsexScale, falseyScale, falsezScale);
                }
            });
            return this;
        }

        public Builder<T, A> condDualScale(Predicate<T> cond, float trueScale, float falseScale) {
            return condDualScale(cond, trueScale, trueScale, trueScale, falseScale, falseScale, falseScale);
        }

        public Builder<T, A> childScale(float xScale, float yScale, float zScale) {
            preRender((e, s, p) -> {
                if (e instanceof AgeableMob && e.isBaby()) {
                    s.scale(xScale, yScale, zScale);
                }
            });
            return this;
        }

        public Builder<T, A> childScale(float scale) {
            return childScale(scale, scale, scale);
        }

        public Builder<T, A> ageScale(float adultxScale, float adultyScale, float adultzScale, float childxScale, float childyScale, float childzScale) {
            preRender((e, s, p) -> {
                if (e instanceof AgeableMob) {
                    if (e.isBaby()) {
                        s.scale(childxScale, childyScale, childzScale);
                    } else {
                        s.scale(adultxScale, adultyScale, adultzScale);
                    }
                }
            });
            return this;
        }

        public Builder<T, A> ageScale(float adultScale, float childScale) {
            return ageScale(adultScale, adultScale, adultScale, childScale, childScale, childScale);
        }

        public Builder<T, A> handleRotation(HandleRotation<T> handleRotationFunc) {
            this.handleRotation = handleRotationFunc;
            return this;
        }

        public Builder<T, A> applyRotations(ApplyRotations<T> applyRotationsFunc, SuperCallApplyRotations superCall) {
            this.superCallApplyRotations = superCall;
            return applyRotations(applyRotationsFunc);
        }

        public Builder<T, A> applyRotations(ApplyRotations<T> applyRotationsFunc) {
            this.applyRotations = applyRotationsFunc;
            return this;
        }

        public Builder<T, A> renderLayer(RenderLayer<T> renderLayerFunc) {
            this.renderLayer = renderLayerFunc;
            return this;
        }

        public Builder<T, A> blockLightLevel(BlockLightLevel<T> blockLightLevel) {
            this.blockLightLevel = blockLightLevel;
            return this;
        }

        public EntityRendererProvider done() {
            if (tex == null || model == null) {
                throw new IllegalArgumentException("Must define both a texture and a model before calling build()!");
            }
            return ctx -> new ImplRenderer<>(ctx, shadow, tex, model, preRender, handleRotation, applyRotations, superCallApplyRotations, renderLayer, blockLightLevel).layers(layers);
        }

        private ResourceLocation texStored(String location) {
            return texMapper.computeIfAbsent(location, l -> tex(modid, l));
        }
    }

}
