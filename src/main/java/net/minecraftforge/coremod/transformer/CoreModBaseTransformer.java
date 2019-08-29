package net.minecraftforge.coremod.transformer;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.minecraftforge.coremod.CoreMod;
import net.minecraftforge.coremod.CoreModTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class CoreModBaseTransformer<T> implements ITransformer<T> {
    static final Logger LOGGER = LogManager.getLogger();
    static final Marker COREMOD = MarkerManager.getMarker("COREMOD");
    final CoreMod coreMod;
    final Set<Target> targets;
    final ScriptObjectMirror function;
    final String coreName;

    public CoreModBaseTransformer(CoreMod coreMod, final String coreName, final Set<Target> targets, final ScriptObjectMirror function) {
        this.coreMod = coreMod;
        this.coreName = coreName;
        this.targets = targets;
        this.function = function;
    }

    @Nonnull
    @Override
    public T transform(T input, ITransformerVotingContext context) {
        CoreModTracker.setCoreMod(coreMod);
        T result = input;
        try {
            result = runCoremod(result);
        } catch (Exception e) {
            LOGGER.error(COREMOD, "Error occurred applying transform of coremod {} function {}", this.coreMod.getPath(), this.coreName, e);
        } finally {
            CoreModTracker.clearCoreMod();
        }
        return result;
    }

    abstract T runCoremod(T input);

    @Nonnull
    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Nonnull
    @Override
    public Set<Target> targets() {
        return targets;
    }

    @Override
    public String[] labels() {
        return new String[] { coreMod.getFile().getOwnerId(), coreName };
    }
}
