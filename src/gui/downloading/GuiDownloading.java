/*
    MangaWatcher - a manga management program. 
    Copyright (C) 2013 David Siewert

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
import lombok.Getter;
import data.Manga;
import data.MangaLibrary;

public @Getter class GuiDownloading extends JPanel {

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
	

	private Date waitingDate;
	private ScheduledFuture<?> waitingHandler;
	
	private Runnable waitingProcess = new Runnable() {
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
	private Runnable downloadingProcess = new Runnable() {
		public void run() {
			waitingHandler.cancel(false);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					enableButtons(false);
					executors.runOnNetworkThread(new Runnable() {
						@Override
						public void run() {
							logic.updateShallow();
							library.save(executors);
							enableButtonsInvoked(true);
							scheduleWaiting();
						}
					});
				}
			});
			
		}
	};

	public GuiDownloading(final GuiFrame frame) {
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
				enableButtons(false);
				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						logic.updateAvailable();
						library.saveAvailable(executors);
						enableButtonsInvoked(true);
						enableCountdownDisplayInvoked();
					}
				});
			}
		});
		panel.add(available);
		
		shallow = new JButton("Update latest releases");
		shallow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				waitingHandler.cancel(false);
				enableButtons(false);
				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						logic.updateShallow();
						library.save(executors);
						enableButtonsInvoked(true);	
						enableCountdownDisplayInvoked();
						frame.getTray().update();
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
				enableButtons(false);
				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						logic.updateDeep();	
						library.save(executors);
						enableButtonsInvoked(true);	
						enableCountdownDisplayInvoked();
						frame.getTray().update();
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
		scheduler.schedule(downloadingProcess, wait * 60+1, SECONDS);
		
	}

	
	public void updateDeep(final Manga manga){
		waitingHandler.cancel(false);
		enableButtons(false);
		executors.runOnNetworkThread(new Runnable() {
			@Override
			public void run() {
				logic.updateDeep(manga);
				library.save(executors);
				enableButtonsInvoked(true);	
				enableCountdownDisplayInvoked();
				frame.getTray().update();
			}
		});		
	}
	
	
	

	
	public void enableButtons(boolean enabled){
		available.setEnabled(enabled);
		shallow.setEnabled(enabled);
		deep.setEnabled(enabled);
	}
	
	public void enableButtonsInvoked(final boolean enabled){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				available.setEnabled(enabled);
				shallow.setEnabled(enabled);
				deep.setEnabled(enabled);				
			}
		});
	}
	public void enableCountdownDisplayInvoked(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(!waitingHandler.isDone())
			waitingHandler = scheduler.scheduleAtFixedRate(waitingProcess, 0, 1, SECONDS);
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
	public static String getTime(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
}