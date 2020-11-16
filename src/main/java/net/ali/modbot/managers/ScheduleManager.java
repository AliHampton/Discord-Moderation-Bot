package net.ali.modbot.managers;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ScheduleManager {

    public final ScheduledExecutorService scheduler;
    private final Map<Guild, Future> schedules = new HashMap<>();

    public ScheduleManager() {
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void schedule(Guild guild, Runnable runnable, long delay, TimeUnit unit) {
        remove(guild);
        schedules.put(guild, scheduler.schedule(runnable, delay, unit));
    }

    public void remove(Guild guild) {
        Future currentSchedule = schedules.get(guild);
        if (currentSchedule != null) {
            if (!currentSchedule.isCancelled())
                currentSchedule.cancel(true);
            schedules.remove(guild);
        }
    }
}
