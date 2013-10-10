package gui.downloading;

import static java.util.concurrent.TimeUnit.SECONDS;
import gui.GuiFrame;
import gui.menu.GuiProgressBar;
import gui.threading.BackgroundExecutors;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import logic.MangaLogic;
import data.MangaLibrary;

public class GuiDownloading extends JPanel {

	private final GuiFrame frame;
	private final MangaLogic logic;
	private final MangaLibrary library;
	private final BackgroundExecutors executors;
	private final ScheduledExecutorService scheduler;

	private final GuiProgressBar progress;
	private final JScrollPane scroll;
	private final JTextArea text;
	private final JButton shallow;
	private final JButton deep;
	private final JButton available;
	

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
//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
//					progress.setIndeterminate(true);
//				}
//			});
		}
	};

	public GuiDownloading(GuiFrame frame) {
		this.setLayout(new BorderLayout());
		this.frame = frame;
		this.logic = frame.getLogic();
		this.library = frame.getLibrary();
		this.executors = frame.getExecutors();
		this.scheduler = frame.getExecutors().getScheduler();
		
		progress = new GuiProgressBar(frame);
		this.add(progress, BorderLayout.NORTH);

		text = new JTextArea();
		text.setColumns(30);
		text.setWrapStyleWord(true);
		text.setFont(frame.getOptions().getTextFont());
		DefaultCaret caret = (DefaultCaret)text.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scroll = new JScrollPane(text);
		scroll.getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		this.add(scroll, BorderLayout.CENTER);

		JPanel panel = new JPanel(new GridLayout());
		this.add(panel,BorderLayout.SOUTH);

		available = new JButton("Refresh manga list");
		available.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				waitingHandler.cancel(false);
				((JButton)e.getSource()).setEnabled(false);
				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						logic.updateAvailable();
						library.save(executors);
						enableInvoked((JButton)e.getSource());
						
					}
				});
				
			}
		});
		panel.add(available);
		
		shallow = new JButton("Update from latest releases");
		shallow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				waitingHandler.cancel(false);
				((JButton)e.getSource()).setEnabled(false);
				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						logic.updateShallow();
						library.save(executors);
						enableInvoked((JButton)e.getSource());
					}
				});			
			}
		});
		panel.add(shallow);
		
		deep = new JButton("Update all");
		deep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				waitingHandler.cancel(false);
				((JButton)e.getSource()).setEnabled(false);
				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						logic.updateDeep();	
						library.save(executors);
						enableInvoked((JButton)e.getSource());
					}
				});			
			}
		});
		panel.add(deep);
		
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

//	public void progressInvoked(final int percent, final String text) {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				progressInvoked(percent, text);
//			}
//		});
//	}
	
	public void enableInvoked(final JButton button){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				button.setEnabled(true);
			}
		});
		
	}


	public void progressStartInvoked(final String txt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress.setValue(100);
				progress.setText(txt);
				progress.repaint();
			}
		});
	}

	public void progressStartIndeterminateInvoked(final String txt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress.setText(txt);
				progress.setIndeterminate(true);
				progress.repaint();
			}
		});
	}

	public void progressInvoked(final int percent, final String txt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress.setValue(percent);
				progress.setText(txt);
				progress.repaint();
			}
		});
	}
	
	public void progressInvoked(final String txt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress.setText(txt);
				progress.repaint();
			}
		});
	}
	public void progressInvoked(final int percent) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress.setValue(percent);
				progress.repaint();
			}
		});
	}

	public void progressEndInvoked(final String txt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progress.setIndeterminate(false);
				progress.setValue(100);
				progress.setText(txt);
				progress.repaint();
			}
		});
	}

	public void textInvoked(final String txt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				text.append(txt+"\n");
				text.repaint();
			}
		});
	}	
	
//	public void printInvoked(final MangaSource source, final  int percent, final String txt) {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				progress.setValue(percent);
//				progress.setText(txt);
//				text.append(txt);
//				repaint();
//			}
//		});
//	}	
//	
//	public void printInvoked(final MangaSource source, final String txt) {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				progress.setText(txt);
//				text.append(txt);
//				repaint();
//			}
//		});
//	}
	
//	public void progress(MangaSource source, int percent, String txt) {
//		progress.setValue(percent);
//		progress.setText(txt);
//		text(source, txt);
//		progress.repaint();
//	}	
//
//	public void text(MangaSource source, String txt) {
//		text.append(txt+"\n");
//		text.repaint();
//	}	
	

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