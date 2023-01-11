package dev.jorel.commandapi.test;
import static org.mockito.ArgumentMatchers.any;

import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;
import org.mockito.Mockito;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import dev.jorel.commandapi.arguments.ArgumentSubType;
import dev.jorel.commandapi.arguments.SuggestionProviders;
import dev.jorel.commandapi.nms.NMS;
import dev.jorel.commandapi.wrappers.FunctionWrapper;
import dev.jorel.commandapi.wrappers.Location2D;
import dev.jorel.commandapi.wrappers.MathOperation;
import dev.jorel.commandapi.wrappers.ParticleData;
import dev.jorel.commandapi.wrappers.Rotation;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Argument related method implementations
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class ArgumentNMS extends BlankNMS {

	public ArgumentNMS(NMS<?> baseNMS) {
		super(baseNMS);
	}

	@Override
	public ArgumentType<?> _ArgumentAxis() {
		return BASE_NMS._ArgumentAxis();
	}

	@Override
	public final ArgumentType<?> _ArgumentBlockPredicate() {
		return BASE_NMS._ArgumentBlockPredicate();
	}

	@Override
	public final ArgumentType<?> _ArgumentBlockState() {
		return BASE_NMS._ArgumentBlockState();
	}
	
	@Override
	public ArgumentType<?> _ArgumentChat() {
		return BASE_NMS._ArgumentChat();
	}

	@Override
	public ArgumentType<?> _ArgumentChatFormat() {
		return BASE_NMS._ArgumentChatFormat();
	}
	
	@Override
	public ArgumentType<?> _ArgumentChatComponent() {
		return BASE_NMS._ArgumentChatComponent();
	}
	
	@Override
	public ArgumentType<?> _ArgumentDimension() {
		return BASE_NMS._ArgumentDimension();
	}

	@Override
	public ArgumentType<?> _ArgumentEnchantment() {
		return BASE_NMS._ArgumentEnchantment();
	}

	@Override
	public ArgumentType<?> _ArgumentEntity(ArgumentSubType subType) {
		return BASE_NMS._ArgumentEntity(subType);
	}

	@Override
	public ArgumentType<?> _ArgumentEntitySummon() {
		return BASE_NMS._ArgumentEntitySummon();
	}

	@Override
	public final ArgumentType<?> _ArgumentItemPredicate() {
		return BASE_NMS._ArgumentItemPredicate();
	}

	@Override
	public final ArgumentType<?> _ArgumentItemStack() {
		// We can't use BASE_NMS for this, because that requires a COMMAND_BUILD_CONTEXT.
		// The COMMAND_BUILD_CONTEXT is only defined for CraftServer instances, otherwise
		// it'll return null.
		CommandBuildContext buildContextMock = Mockito.mock(CommandBuildContext.class);
		Mockito
			.when(buildContextMock.holderLookup(any(ResourceKey.class)))
			.thenReturn(HolderLookup.forRegistry(Registry.ITEM)); // Registry.ITEM
		return ItemArgument.item(buildContextMock);
	}

	@Override
	public ArgumentType<?> _ArgumentMathOperation() {
		return BASE_NMS._ArgumentMathOperation();
	}
	
	@Override
	public ArgumentType<?> _ArgumentMinecraftKeyRegistered() {
		return BASE_NMS._ArgumentMinecraftKeyRegistered();
	}

	@Override
	public ArgumentType<?> _ArgumentMobEffect() {
		return BASE_NMS._ArgumentMobEffect();
	}
	
	@Override
	public ArgumentType<?> _ArgumentParticle() {
		return BASE_NMS._ArgumentParticle();
	}

	@Override
	public ArgumentType<?> _ArgumentPosition() {
		return BASE_NMS._ArgumentPosition();
	}

	@Override
	public ArgumentType<?> _ArgumentPosition2D() {
		return BASE_NMS._ArgumentPosition2D();
	}

	@Override
	public ArgumentType<?> _ArgumentProfile() {
		return BASE_NMS._ArgumentProfile();
	}

	@Override
	public ArgumentType<?> _ArgumentRotation() {
		return BASE_NMS._ArgumentRotation();
	}

	@Override
	public ArgumentType<?> _ArgumentScoreboardTeam() {
		return BASE_NMS._ArgumentScoreboardTeam();
	}
	
	@Override
	public final ArgumentType<?> _ArgumentSyntheticBiome() {
		return BASE_NMS._ArgumentSyntheticBiome();
	}

	@Override
	public ArgumentType<?> _ArgumentTime() {
		return BASE_NMS._ArgumentTime();
	}

	@Override
	public ArgumentType<?> _ArgumentUUID() {
		return BASE_NMS._ArgumentUUID();
	}

	@Override
	public ArgumentType<?> _ArgumentVec2() {
		return BASE_NMS._ArgumentVec2();
	}

	@Override
	public ArgumentType<?> _ArgumentVec3() {
		return BASE_NMS._ArgumentVec3();
	}

	@Override
	public org.bukkit.advancement.Advancement getAdvancement(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getAdvancement((CommandContext) cmdCtx, key);
	}

	@Override
	public Component getAdventureChat(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws CommandSyntaxException {
		return BASE_NMS.getAdventureChat((CommandContext) cmdCtx, key);
	}

	@Override
	public Component getAdventureChatComponent(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getAdventureChatComponent((CommandContext) cmdCtx, key);
	}

	@Override
	public EnumSet<Axis> getAxis(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getAxis((CommandContext) cmdCtx, key);
	}

	@Override
	public Object getBiome(CommandContext<CommandSourceStack> cmdCtx, String key, ArgumentSubType subType) throws CommandSyntaxException {
		return BASE_NMS.getBiome((CommandContext) cmdCtx, key, subType);
	}

	@Override
	public Predicate<Block> getBlockPredicate(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws CommandSyntaxException {
		return BASE_NMS.getBlockPredicate((CommandContext) cmdCtx, key);
	}

	@Override
	public BlockData getBlockState(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getBlockState((CommandContext) cmdCtx, key);
	}

	@Override
	public ChatColor getChatColor(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getChatColor((CommandContext) cmdCtx, key);
	}
	
	@Override
	public BaseComponent[] getChatComponent(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getChatComponent((CommandContext) cmdCtx, key);
	}

	@Override
	public World getDimension(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getDimension((CommandContext) cmdCtx, key);
	}

	@Override
	public Enchantment getEnchantment(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getEnchantment((CommandContext) cmdCtx, key);
	}

	@Override
	public Object getEntitySelector(CommandContext<CommandSourceStack> cmdCtx, String key, ArgumentSubType subType) throws CommandSyntaxException {
		return BASE_NMS.getEntitySelector((CommandContext) cmdCtx, key, subType);
	}

	@Override
	public EntityType getEntityType(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws CommandSyntaxException {
		return BASE_NMS.getEntityType((CommandContext) cmdCtx, key);
	}

	@Override
	public FunctionWrapper[] getFunction(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws CommandSyntaxException {
		return BASE_NMS.getFunction((CommandContext) cmdCtx, key);
	}

	@Override
	public ItemStack getItemStack(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws CommandSyntaxException {
		return BASE_NMS.getItemStack((CommandContext) cmdCtx, key);
	}

	@Override
	public Predicate<ItemStack> getItemStackPredicate(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws CommandSyntaxException {
		return BASE_NMS.getItemStackPredicate((CommandContext) cmdCtx, key);
	}

	@Override
	public Location2D getLocation2DBlock(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getLocation2DBlock((CommandContext) cmdCtx, key);
	}

	@Override
	public Location2D getLocation2DPrecise(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getLocation2DPrecise((CommandContext) cmdCtx, key);
	}

	@Override
	public Location getLocationBlock(CommandContext<CommandSourceStack> cmdCtx, String str) throws CommandSyntaxException {
		return BASE_NMS.getLocationBlock((CommandContext) cmdCtx, str);
	}

	@Override
	public final Location getLocationPrecise(CommandContext<CommandSourceStack> cmdCtx, String str) throws CommandSyntaxException {
		return BASE_NMS.getLocationPrecise((CommandContext) cmdCtx, str);
	}

	@Override
	public LootTable getLootTable(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getLootTable((CommandContext) cmdCtx, key);
	}

	@Override
	public <NBTContainer> Object getNBTCompound(CommandContext<CommandSourceStack> cmdCtx, String key,
			Function<Object, NBTContainer> nbtContainerConstructor) {
		return BASE_NMS.getNBTCompound((CommandContext) cmdCtx, key, nbtContainerConstructor);
	}

	@Override
	public MathOperation getMathOperation(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getMathOperation((CommandContext) cmdCtx, key);
	}

	@Override
	public String getObjective(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws IllegalArgumentException, CommandSyntaxException {
		return BASE_NMS.getObjective((CommandContext) cmdCtx, key);
	}

	@Override
	public String getObjectiveCriteria(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getObjectiveCriteria((CommandContext) cmdCtx, key);
	}

	@Override
	public ParticleData<?> getParticle(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getParticle((CommandContext) cmdCtx, key);
	}
	
	@Override
	public Player getPlayer(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getPlayer((CommandContext) cmdCtx, key);
	}

	@Override
	public PotionEffectType getPotionEffect(CommandContext<CommandSourceStack> cmdCtx, String key)
			throws CommandSyntaxException {
		return BASE_NMS.getPotionEffect((CommandContext) cmdCtx, key);
	}

	@Override
	public Rotation getRotation(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getRotation((CommandContext) cmdCtx, key);
	}

	@Override
	public SuggestionProvider getSuggestionProvider(SuggestionProviders provider) {
		return BASE_NMS.getSuggestionProvider(provider);
	}
	
	@Override
	public Recipe getRecipe(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getRecipe((CommandContext) cmdCtx, key);
	}

	@Override
	public Object getSound(CommandContext<CommandSourceStack> cmdCtx, String key, ArgumentSubType subType) {
		return BASE_NMS.getSound((CommandContext) cmdCtx, key, subType);
	}

	@Override
	public String getTeam(CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return BASE_NMS.getTeam((CommandContext) cmdCtx, key);
	}

	@Override
	public int getTime(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getTime((CommandContext) cmdCtx, key);
	}

	@Override
	public Message generateMessageFromJson(final String json) {
		return BASE_NMS.generateMessageFromJson(json);
	}

	@Override
	public UUID getUUID(CommandContext<CommandSourceStack> cmdCtx, String key) {
		return BASE_NMS.getUUID((CommandContext) cmdCtx, key);
	}

}