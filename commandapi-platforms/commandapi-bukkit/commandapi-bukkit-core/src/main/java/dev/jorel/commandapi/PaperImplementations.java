package dev.jorel.commandapi;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import dev.jorel.commandapi.nms.NMS;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;

public class PaperImplementations {

	private final boolean isPaperPresent;
	private final boolean isFoliaPresent;
	private final NMS<?> nmsInstance;
	private final Class<? extends CommandSender> feedbackForwardingCommandSender;

	/**
	 * Constructs a PaperImplementations object
	 * 
	 * @param isPaperPresent Whether this is a Paper server or not
	 * @param isFoliaPresent Whether this is a Folia server or not
	 * @param nmsInstance    The instance of NMS
	 */
	@SuppressWarnings("unchecked")
	public PaperImplementations(boolean isPaperPresent, boolean isFoliaPresent, NMS<?> nmsInstance) {
		this.isPaperPresent = isPaperPresent;
		this.isFoliaPresent = isFoliaPresent;
		this.nmsInstance = nmsInstance;
		
		Class<? extends CommandSender> tempFeedbackForwardingCommandSender = null;
		try {
			tempFeedbackForwardingCommandSender = (Class<? extends CommandSender>) Class.forName("io.papermc.paper.commands.FeedbackForwardingSender");
		} catch (ClassNotFoundException e) {
			// uhh...
		}
		
		this.feedbackForwardingCommandSender = tempFeedbackForwardingCommandSender;
	}

	/**
	 * Hooks into Paper's {@link ServerResourcesReloadedEvent} to detect if
	 * {@code /minecraft:reload} is called, and registers a reload handler that
	 * automatically calls the CommandAPI's internal datapack reloading function
	 * 
	 * @param plugin the plugin that the CommandAPI is being used from
	 */
	public void registerReloadHandler(Plugin plugin) {
		if (isPaperPresent && CommandAPIBukkit.getConfiguration().shouldHookPaperReload()) {
			Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

				@EventHandler
				public void onServerReloadResources(ServerResourcesReloadedEvent event) {
					CommandAPI.logNormal("/minecraft:reload detected. Reloading CommandAPI commands!");
					nmsInstance.reloadDataPacks();
				}

			}, plugin);
			CommandAPI.logNormal("Hooked into Paper ServerResourcesReloadedEvent");
		} else {
			CommandAPI.logNormal("Did not hook into Paper ServerResourcesReloadedEvent");
		}
	}

	/**
	 * @return Bukkit's {@link CommandMap}
	 */
	public CommandMap getCommandMap() {
		if (isPaperPresent) {
			return Bukkit.getServer().getCommandMap();
		} else {
			return nmsInstance.getSimpleCommandMap();
		}
	}
	
	/**
	 * @return whether we're using paper or not
	 */
	public boolean isPaperPresent() {
		return this.isPaperPresent;
	}
	
	/**
	 * @return whether we're using folia or not
	 */
	public boolean isFoliaPresent() {
		return this.isFoliaPresent;
	}
	
	/**
	 * @return a class reference pointing to {@code io.papermc.paper.commands.FeedbackForwardingSender}
	 */
	public Class<? extends CommandSender> getFeedbackForwardingCommandSender() {
		return this.feedbackForwardingCommandSender;
	}
	

}
