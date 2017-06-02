package com.tencent.trc.util.status;

public class StatusPrinter extends Thread {

	final private Printer printer;
	final private int printInterval_ms;

	public StatusPrinter(Printer printer, int printInterval_ms,
			String printerName) {
		super(printerName);
		this.printer = printer;
		this.printInterval_ms = printInterval_ms;
		this.setDaemon(true);
	}

	private long laststatistictime = System.currentTimeMillis();

	@Override
	public void run() {
		while (true) {
			try {
				long ctime = System.currentTimeMillis();
				if (ctime - laststatistictime > printInterval_ms) {
					laststatistictime = ctime;
					printer.printStatus();
				}
				Thread.sleep(1000);
			} catch (Throwable e) {
			}
		}
	}

	public static interface Printer {
		public void printStatus();
	}

}
