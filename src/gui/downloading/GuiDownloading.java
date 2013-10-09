package gui.downloading;

import static java.util.concurrent.TimeUnit.SECONDS;
import gui.GuiFrame;
import gui.menu.GuiProgressBar;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class GuiDownloading extends JPanel {

	GuiFrame frame;
	ScheduledExecutorService scheduler;

	GuiProgressBar progress;
	JScrollPane scroll;
	JTextArea text;

	Date waitingDate;
	ScheduledFuture<?> waitingHandler;
	Runnable waitingProcess = new Runnable() {
		public void run() {

			Date nowDate = new Date();
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

			long diff = Math.max(waitingDate.getTime() - nowDate.getTime(),0);
			long total = frame.getOptions().getCheckInterval() * 60 * 1000;

			final int percent = (int) ((total - diff) * 100 / total);
			String output = getTime(diff);
			progressInvoked(percent, "Recheck in "+output);
		}
	};
	Runnable waitingCancel = new Runnable() {
		public void run() {
			waitingHandler.cancel(false);
		}
	};

	public GuiDownloading(GuiFrame frame) {
		this.setLayout(new BorderLayout());
		this.frame = frame;
		this.scheduler = frame.getExecutors().getScheduler();
		
		progress = new GuiProgressBar(frame);
		this.add(progress, BorderLayout.NORTH);

		text = new JTextArea();
		text.setColumns(30);

		scroll = new JScrollPane(text);
		scroll.getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		this.add(scroll, BorderLayout.CENTER);

		scheduleWaiting();
	}

	public void scheduleWaiting() {

		int wait = frame.getOptions().getCheckInterval();
		waitingDate = new Date(System.currentTimeMillis() + wait * 60 * 1000);
		// M.print("targetTime: " + new SimpleDateFormat("HH:mm:ss").format(dateNext));

		//		scheduler.schedule(waitingProgress, 1, SECONDS);
		waitingHandler = scheduler.scheduleAtFixedRate(waitingProcess, 0, 1, SECONDS);
		scheduler.schedule(waitingCancel, wait * 60+1, SECONDS);
	}

	public void scheduleDownloading() {

	}

	public void progressInvoked(final int percent, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress(percent, text);
			}
		});
	}

	public void progress(int percent, String txt) {
		progress.setValue(percent);
		progress.setText(txt);
		progress.repaint();
	}

	public void infoInvoked(final int percent, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				info(percent, text);
			}
		});
	}

	public void info(int percent, String txt) {
		progress.setValue(percent);
		progress.setText(txt);
		text.append(txt);
		progress.repaint();
	}	
	

	public static String getTime(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
}

/*



	private void progress(final int percent, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progress.setValue(percent);
				progress.setText(text);
				progress.repaint();
			}
		});
	}

*/