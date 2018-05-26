package me.kangarko.ui.model;

import lombok.RequiredArgsConstructor;

/**
 * This runnable only runs itself when the
 * run method is called for the first time.
 */
@RequiredArgsConstructor
public final class OneTimeRunnable {

	private final Runnable runnable;

	private boolean hasBeenRun = false;

	public final void runIfHasnt() {
		if (hasBeenRun)
			return;

		try {
			runnable.run();

		} finally {
			hasBeenRun = true;
		}
	}

	public final boolean hasBeenRun() {
		return hasBeenRun;
	}
}
