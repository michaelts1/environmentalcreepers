package fi.dy.masa.environmentalcreepers.mixin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import fi.dy.masa.environmentalcreepers.EnvironmentalCreepers;
import fi.dy.masa.environmentalcreepers.config.Configs;

@Mixin(Explosion.class)
public abstract class MixinExplosion
{
    @Shadow @Final private World world;
    @Shadow @Final private Entity entity;
    @Shadow @Final private ObjectArrayList<BlockPos> affectedBlocks;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final @Mutable private float power;

    @Shadow @org.jetbrains.annotations.Nullable public abstract LivingEntity getCausingEntity();

    @Inject(method = "affectWorld", at = @At("HEAD"), cancellable = true)
    private void envc_disableExplosionBlockDamageOrCompletely(CallbackInfo ci)
    {
        if (this.world.isClient == false)
        {
            EnvironmentalCreepers.logInfo(this::envc_printExplosionInfo);
        }

        if (this.entity instanceof CreeperEntity)
        {
            EnvironmentalCreepers.logInfo("MixinExplosion.envc_disableExplosionBlockDamageOrCompletely: clearAffectedBlockPositions(), type: 'Creeper'");
            this.affectedBlocks.clear();
        }
    }

    private String envc_printExplosionInfo()
    {
        return String.format("Explosion @ [%.5f, %.5f, %.5f], power: %.2f - type: '%s' - explosion class: '%s', placer: '%s'",
                             this.x, this.y, this.z, this.power,
                             (this.entity instanceof CreeperEntity) ? "Creeper" : "Other",
                             this.getClass().getName(),
                             this.getCausingEntity() != null ? this.getCausingEntity().getClass().getName() : "<null>");
    }
}
