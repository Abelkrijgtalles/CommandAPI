package dev.jorel.commandapi;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import dev.jorel.commandapi.nms.NMS;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

public class PaperImplementations {

	private final boolean isPaperPresent;
	private final boolean isFoliaPresent;
	private final NMS<?> nmsInstance;
	private final Class<? extends CommandSender> feedbackForwardingCommandSender;
	private final ThreadPoolExecutor paperCommandSendingPool;

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

		ThreadPoolExecutor tempPaperCommandSendingPool = null;
		try {
			// public static field
			tempPaperCommandSendingPool = (ThreadPoolExecutor) nmsInstance.getNMSCommandsClass().getField("COMMAND_SENDING_POOL").get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			// Not a big deal, if the thread pool doesn't exist, then the server also isn't using it
			//  We don't need to worry about ConcurrentModificationExceptions if Commands packets are
			//  being built synchronously
		}
		this.paperCommandSendingPool = tempPaperCommandSendingPool;
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

	/**
	 * Runs a task that modifies the command trees using Paper's {@code COMMAND_SENDING_POOL}, if it is present. This
	 * ensures that the command trees are not modified while Paper is building a Commands packet asynchronously, which
	 * may cause a ConcurrentModificationException.
	 * <p>
	 * If the {@code COMMAND_SENDING_POOL} is not present (probably because we're on Spigot), the task is run
	 * immediately, since there isn't anything building Commands packets async anyway.
	 *
	 * @param modifyTask The task to run that modifies the command trees.
	 */
	public void modifyCommandTreesAndAvoidPaperCME(Runnable modifyTask) {
		modifyCommandTreesAndAvoidPaperCME(() -> {
			modifyTask.run();
			return null;
		});
	}

	/**
	 * Runs a task that modifies the command trees using Paper's {@code COMMAND_SENDING_POOL}, if it is present. This
	 * ensures that the command trees are not modified while Paper is building a Commands packet asynchronously, which
	 * may cause a ConcurrentModificationException.
	 * <p>
	 * If the {@code COMMAND_SENDING_POOL} is not present (probably because we're on Spigot), the task is run
	 * immediately, since there isn't anything building Commands packets async anyway.
	 *
	 * @param modifyTask The task to run that modifies the command trees.
	 * @return The result of running the {@code modifyTask}.
	 * @param <T> The class of the object returned by the {@code modifyTask}.
	 */
	public <T> T modifyCommandTreesAndAvoidPaperCME(Supplier<T> modifyTask) {
		if(paperCommandSendingPool == null || paperCommandSendingPool.isShutdown()) {
			// If the server isn't building Commands packets async (probably because we're on Spigot),
			//  it is safe to run the task immediately
			return modifyTask.get();
		}
		// Otherwise, submit the modify task to the pool.
		//  The pool only runs one task at a time (see https://github.com/JorelAli/CommandAPI/pull/501#issuecomment-1773959895),
		//  so this ensures we don't modify the commands while a command-building process is reading them
		Future<T> futureResult = paperCommandSendingPool.submit(modifyTask::get);

		try {
			return futureResult.get();
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();

			// `modifyTask` may very well throw runtime exceptions, pass it on
			if(cause instanceof RuntimeException runtimeExceptionCause) throw runtimeExceptionCause;

			// `modifyTask` should not throw a checked exception, since that violates the signature of Supplier
			//  This path shouldn't happen, so it is reasonable to freak out with a generic exception
			throw new AssertionError("Unexpected exception. `Supplier modifyTask` should not throw a checked exception.", e);
        } catch (InterruptedException e) {
			// As far as I can tell, this is the best way to handle this exception (see https://stackoverflow.com/a/18926205).
			//  The InterruptedException means that this thread should stop whatever it's doing ASAP.
			//  I don't think it makes sense to add `throws InterruptedException` to the method signature since it's not
			//  going to be handled anywhere else, so we just have to wrap it in a RuntimeException and get out of here.
			//  The `modifyTask` probably did not complete correctly, so we don't have a reasonable result to return.
			Thread.currentThread().interrupt();
            throw new IllegalStateException("The commands could not be updated :(. The thread was interrupted.", e);
        }
    }
}
