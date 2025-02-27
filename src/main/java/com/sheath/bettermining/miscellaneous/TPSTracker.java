package com.sheath.bettermining.miscellaneous;

public class TPSTracker {
    private static final int TICK_COUNT = 100;
    private static final long[] tickTimes = new long[TICK_COUNT];
    private static int currentTick = 0;

    public static void tick(long currentTime) {
        tickTimes[currentTick % TICK_COUNT] = currentTime;
        currentTick++;
    }

    public static double getTPS() {
        if (currentTick < TICK_COUNT) {
            return 20.0; // Assume full TPS for the first few ticks
        }

        int target = (currentTick - 1) % TICK_COUNT;
        long elapsed = System.currentTimeMillis() - tickTimes[target];

        return TICK_COUNT * 1000.0 / elapsed;
    }
}
