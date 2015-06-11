/*******************************************************************************
 * Copyright 2015, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/

package biomesoplenty.common.world.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import biomesoplenty.api.biome.generation.BOPGeneratorBase;
import biomesoplenty.common.util.biome.GeneratorUtils;
import biomesoplenty.common.util.block.BlockQuery;
import biomesoplenty.common.util.block.BlockQuery.BlockQueryMaterial;
import biomesoplenty.common.util.block.BlockQuery.BlockQueryBlock;
import biomesoplenty.common.util.block.BlockQuery.BlockQueryParseException;
import biomesoplenty.common.util.block.BlockQuery.BlockQueryState;
import biomesoplenty.common.util.block.BlockQuery.IBlockPosQuery;
import biomesoplenty.common.util.config.BOPConfig.IConfigObj;

public class GeneratorColumns extends BOPGeneratorBase
{
    
    public static class Builder implements IGeneratorBuilder<GeneratorColumns>
    {
        protected float amountPerChunk = 1.0F;
        protected IBlockPosQuery placeOn = new BlockQueryMaterial(Material.ground, Material.grass);
        protected IBlockState with = Blocks.cobblestone.getDefaultState();
        protected int minHeight = 2;
        protected int maxHeight = 4;
        protected int generationAttempts = 12;
        
        public Builder amountPerChunk(float a) {this.amountPerChunk = a; return this;}
        public Builder placeOn(IBlockPosQuery a) {this.placeOn = a; return this;}
        public Builder placeOn(String a) throws BlockQueryParseException {this.placeOn = BlockQuery.parseQueryString(a); return this;}
        public Builder placeOn(Block a) {this.placeOn = new BlockQueryBlock(a); return this;}
        public Builder placeOn(IBlockState a) {this.placeOn = new BlockQueryState(a); return this;}        
        public Builder with(IBlockState a) {this.with = a; return this;}
        public Builder minHeight(int a) {this.minHeight = a; return this;}
        public Builder maxHeight(int a) {this.maxHeight = a; return this;}
        public Builder generationAttempts(int a) {this.generationAttempts = a; return this;}


        @Override
        public GeneratorColumns create()
        {
            return new GeneratorColumns(this.amountPerChunk, this.with, this.minHeight, this.maxHeight, this.placeOn, this.generationAttempts);
        }
    }
    
    
    protected IBlockPosQuery placeOn;
    protected IBlockState with;
    protected int minHeight;
    protected int maxHeight;
    protected int generationAttempts;

    public GeneratorColumns(float amountPerChunk, IBlockState with, int minHeight, int maxHeight, IBlockPosQuery placeOn, int generationAttempts)
    {
        super(amountPerChunk);
        this.with = with;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.placeOn = placeOn;
        this.generationAttempts = generationAttempts;
    }
    
    @Override
    public BlockPos getScatterY(World world, Random random, int x, int z)
    {
        // always at world surface
        return world.getHeight(new BlockPos(x, 0, z));
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        for (int i = 0; i < this.generationAttempts; ++i)
        {
            BlockPos genPos = pos.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));
                
            // see if we can place the column
            if (this.placeOn.matches(world, genPos.down()))
            {
                // choose random target height
                int height = GeneratorUtils.nextIntBetween(rand, this.minHeight, this.maxHeight);
                
                // keep placing blocks upwards (if there's room)
                while(height > 0 && world.isAirBlock(genPos))
                {
                    world.setBlockState(genPos, this.with);
                    genPos = genPos.up();
                    height--;
                }
            }
        }
        return true;
    }
    
    @Override
    public void configure(IConfigObj conf)
    {          
        this.amountPerChunk = conf.getFloat("amountPerChunk", this.amountPerChunk);
        this.with = conf.getBlockState("with", this.with);
        this.minHeight = conf.getInt("minHeight", this.minHeight);
        this.maxHeight = conf.getInt("maxHeight", this.maxHeight);
        this.generationAttempts = conf.getInt("generationAttempts", this.generationAttempts);
        this.placeOn = conf.getBlockPosQuery("placeOn", this.placeOn);
    }
    

}