package dev.nokee.commons.gradle;

final class RunOnce implements Runnable {
	private final Runnable delegate;
	private boolean ran = false;

	public RunOnce(Runnable delegate) {
		this.delegate = delegate;
	}

	@Override
	public void run() {
		if (!ran) {
			try {
				delegate.run();
			} finally {
				ran = true;
			}
		}
	}

	public static Runnable once(Runnable runnable) {
		return new RunOnce(runnable);
	}
}
