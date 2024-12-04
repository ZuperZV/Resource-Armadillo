package net.zuperz.resource_armadillo.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.zuperz.resource_armadillo.ResourceArmadillo;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, ResourceArmadillo.MOD_ID, "locale");
    }

    @Override
    protected void addTranslations() {
        //addBlock(STOR, "naum i lille");
    }
    public void addCreativeModeTab(ResourceLocation key, String name) {
        add("creativetab." + key.getPath(), name);
    }
}