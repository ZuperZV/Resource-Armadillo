package net.zuperz.resource_armadillo.datagen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.zuperz.resource_armadillo.block.ModBlocks;
import net.zuperz.resource_armadillo.item.ModItems;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.ARMADILLO_HIVE.get());
        dropSelf(ModBlocks.ROOST.get());

        dropSelf(ModBlocks.CHROMIUM_BLOCK.get());
        dropSelf(ModBlocks.CENTRIFUGE.get());

        this.add(ModBlocks.CHROMIUM_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.CHROMIUM_ORE.get(), ModItems.RAW_CHROMIUM.get(), 1, 3));
        this.add(ModBlocks.CHROMIUM_DEEPSLATE_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.CHROMIUM_DEEPSLATE_ORE.get(), ModItems.RAW_CHROMIUM.get(), 2, 4));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    protected LootTable.Builder createMultipleItemLogDrops(Block pBlock, Item log, Item item, float minDrops, float maxDrops) {
        LootPool.Builder logPool = LootPool.lootPool()
                .add(LootItem.lootTableItem(log)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))));

        LootPool.Builder itemPool = LootPool.lootPool()
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .when(LootItemRandomChanceCondition.randomChance(0.8f)));

        return LootTable.lootTable()
                .withPool(logPool)
                .withPool(itemPool);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}