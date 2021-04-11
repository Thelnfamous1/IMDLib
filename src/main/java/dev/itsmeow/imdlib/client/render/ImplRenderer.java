package dev.itsmeow.imdlib.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import dev.itsmeow.imdlib.entity.interfaces.IVariantTypes;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class ImplRenderer<T extends MobEntity, A extends EntityModel<T>> extends BaseRenderer<T, A> {

    private final TextureContainer<T, A> textureContainer;
    private final ModelContainer<T, A> modelContainer;
    private final PreRenderCallback<T> preRenderCallback;
    private final HandleRotation<T> handleRotation;
    private final ApplyRotations<T> applyRotations;
    private final SuperCallApplyRotations applyRotationsSuper;
    private final RenderLayer<T> renderLayer;

    public ImplRenderer(EntityRendererManager mgr, float shadow, @Nonnull TextureContainer<T, A> textureContainer, @Nonnull ModelContainer<T, A> modelContainer, @Nullable PreRenderCallback<T> preRenderCallback, @Nullable HandleRotation<T> handleRotation, @Nullable ApplyRotations<T> applyRotations, SuperCallApplyRotations applyRotationsSuper, RenderLayer<T> renderLayer) {
        super(mgr, modelContainer.getBaseModel(), shadow);
        this.textureContainer = textureContainer;
        this.modelContainer = modelContainer;
        this.preRenderCallback = preRenderCallback;
        this.handleRotation = handleRotation;
        this.applyRotations = applyRotations;
        this.applyRotationsSuper = applyRotationsSuper;
        this.renderLayer = renderLayer;
    }

    @Override
    protected void applyRotations(T e, MatrixStack s, float a, float y, float p) {
        if(applyRotations == null) {
            super.applyRotations(e, s, a, y, p);
        } else {
            if(applyRotationsSuper == SuperCallApplyRotations.PRE) {
                super.applyRotations(e, s, a, y, p);
            }
            applyRotations.applyRotations(e, s, a, y, p);
            if(applyRotationsSuper == SuperCallApplyRotations.POST) {
                super.applyRotations(e, s, a, y, p);
            }
        }
    }

    @Override
    protected void preRenderCallback(T e, MatrixStack s, float p) {
        if(preRenderCallback != null) {
            preRenderCallback.preRenderCallback(e, s, p);
        }
    }

    @Override
    protected float handleRotationFloat(T e, float p) {
        return handleRotation == null ? super.handleRotationFloat(e, p) : handleRotation.handleRotation(e, p);
    }

    @Override
    protected RenderType func_230042_a_(T entity, boolean visible, boolean visibleToPlayer) {
        return renderLayer == null ? super.func_230042_a_(entity, visible, visibleToPlayer) : renderLayer.renderLayer(entity, visible, visibleToPlayer, this.getEntityTexture(entity));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(T e, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        this.entityModel = (A) modelContainer.getModel(e);
        super.render(e, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return textureContainer.getTexture(entity);
    }

    public static class TextureContainer<T extends MobEntity, A extends EntityModel<T>> {

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
            switch(strategy) {
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

    public static class ModelContainer<T extends MobEntity, A extends EntityModel<T>> {

        private final Strategy strategy;
        private final A baseModel;
        private Function<T, EntityModel<T>> modelMapper;
        private A trueModel;
        private EntityModel<T> falseModel;
        private Predicate<T> condition;
        private EntityModel<T> conditionModel;

        public ModelContainer(A baseModel) {
            this.strategy = Strategy.SINGLE;
            this.baseModel = baseModel;
        }

        public ModelContainer(Function<T, EntityModel<T>> modelMapper, A baseModel) {
            this.strategy = Strategy.MAPPER;
            this.modelMapper = modelMapper;
            this.baseModel = baseModel;
        }

        public ModelContainer(Predicate<T> condition, Function<T, EntityModel<T>> modelMapper, A baseModel, EntityModel<T> conditionModel) {
            this.strategy = Strategy.MAPPER;
            this.modelMapper = modelMapper;
            this.baseModel = baseModel;
            this.conditionModel = conditionModel;
            this.condition = condition;
        }

        public ModelContainer(Predicate<T> condition, A trueModel, EntityModel<T> falseModel) {
            this.strategy = Strategy.CONDITION;
            this.condition = condition;
            this.trueModel = trueModel;
            this.falseModel = falseModel;
            this.baseModel = trueModel;
        }

        public EntityModel<T> getModel(T entity) {
            switch(strategy) {
            case SINGLE:
                return baseModel;
            case MAPPER:
                return modelMapper.apply(entity);
            case CONDITION:
                return condition.test(entity) ? trueModel : falseModel;
            case MAPPER_CONDITION:
                return condition.test(entity) ? conditionModel : modelMapper.apply(entity);
            default:
                break;
            }
            return null;
        }

        public A getBaseModel() {
            return baseModel;
        }
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

    public static class Builder<T extends MobEntity, A extends EntityModel<T>> {

        private final String modid;
        private final float shadow;
        private TextureContainer<T, A> tex;
        private ModelContainer<T, A> model;
        private PreRenderCallback<T> preRender;
        private final ArrayList<Function<BaseRenderer<T, A>, LayerRenderer<T, A>>> layers = new ArrayList<>();
        private HandleRotation<T> handleRotation;
        private ApplyRotations<T> applyRotations;
        private SuperCallApplyRotations superCallApplyRotations = SuperCallApplyRotations.NONE;
        private final Map<String, ResourceLocation> texMapper = new HashMap<>();
        private final Map<Class<? extends EntityModel<T>>, EntityModel<T>> modelMapper = new HashMap<>();
        private RenderLayer<T> renderLayer;

        protected Builder(String modid, float shadow) {
            this.modid = modid;
            this.shadow = shadow;
        }

        public Builder<T, A> layer(Function<BaseRenderer<T, A>, LayerRenderer<T, A>> layer) {
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
                if(e instanceof IVariantTypes<?>) {
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

        public Builder<T, A> tVariantCondition(Predicate<T> condition, Function<T, ResourceLocation> texMapper, String conditionTex) {
            return tVariantCondition(condition, texMapper, tex(modid, conditionTex));
        }

        public Builder<T, A> tVariantCondition(Predicate<T> condition, Function<T, ResourceLocation> texMapper, ResourceLocation conditionTex) {
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
                if(e instanceof AgeableEntity) {
                    return e.isChild();
                }
                return false;
            }, e -> {
                if(e instanceof IVariantTypes<?>) {
                    return ((IVariantTypes<?>) e).getVariantTextureOrNull();
                }
                return null;
            }, tex(modid, babyTex));
        }

        public Builder<T, A> mSingle(A model) {
            this.model = new ModelContainer<>(model);
            return this;
        }

        public Builder<T, A> mMapped(Function<T, Class<? extends EntityModel<T>>> modelMapper, A baseModel) {
            this.model = new ModelContainer<>(e -> modelStored(modelMapper.apply(e), baseModel), baseModel);
            return this;
        }

        public Builder<T, A> mCondition(Predicate<T> condition, A trueModel, EntityModel<T> falseModel) {
            this.model = new ModelContainer<>(condition, trueModel, falseModel);
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
                if(cond.test(e)) {
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
                if(cond.test(e)) {
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
                if(e instanceof AgeableEntity && e.isChild()) {
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
                if(e instanceof AgeableEntity) {
                    if(e.isChild()) {
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

        public IRenderFactory<T> done() {
            if(tex == null || model == null) {
                throw new IllegalArgumentException("Must define both a texture and a model before calling build()!");
            }
            return mgr -> new ImplRenderer<>(mgr, shadow, tex, model, preRender, handleRotation, applyRotations, superCallApplyRotations, renderLayer).layers(layers);
        }

        private ResourceLocation texStored(String location) {
            return texMapper.computeIfAbsent(location, l -> tex(modid, l));
        }

        private EntityModel<T> modelStored(Class<? extends EntityModel<T>> clazz, A defaultModel) {
            return modelMapper.computeIfAbsent(clazz, l -> {
                try {
                    return clazz.newInstance();
                } catch(InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return defaultModel;
                }
            });
        }
    }

    public static <T extends MobEntity, A extends EntityModel<T>> Builder<T, A> factory(String modid, float shadow) {
        return new Builder<>(modid, shadow);
    }

    @FunctionalInterface
    public interface PreRenderCallback<T extends MobEntity> {
        void preRenderCallback(T entity, MatrixStack stack, float partialTicks);
    }

    @FunctionalInterface
    public interface HandleRotation<T extends MobEntity> {
        float handleRotation(T entity, float partialTicks);
    }

    @FunctionalInterface
    public interface ApplyRotations<T extends MobEntity> {
        void applyRotations(T entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks);
    }

    @FunctionalInterface
    public interface RenderLayer<T extends MobEntity> {
        RenderType renderLayer(T entity, boolean visible, boolean visibleToPlayer, ResourceLocation texture);
    }

    @FunctionalInterface
    public interface RenderDef<T extends MobEntity, A extends EntityModel<T>> {
        ImplRenderer.Builder<T, A> apply(ImplRenderer.Builder<T, A> renderer);
    }

    private static ResourceLocation tex(String modid, String location) {
        return new ResourceLocation(modid, "textures/entity/" + location + ".png");
    }

}
