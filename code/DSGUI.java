/***************************************************************************
 *   Class DSGUI                                                           *
 *                                                                         *
 *   Copyright (C) 2020 by Marcelo Silveira                                *
 *   MSX Digitized Sound Generator: http://marmsx.msxall.com               *
 *   Contact: flamar98@hotmail.com                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class DSGUI extends JFrame {

	private JPanel contentPane;
	private DSG dsg;
	private JLabel labelBts;
	private JLabel labelFSize;
	private JLabel labelTime;
	private JLabel labelBts2;
	private JLabel labelFSize2;
	private JLabel labelTime2;
	private JLabel labelFrequency;
	private JLabel labelMaxTime;
	private JLabel labelCurrent;
	private JLabel labelName;
	private JSpinner spinner;
	private JSpinner spinner2;
	private JComboBox comboBox;
	private JComboBox comboBoxDest;
	private JComboBox comboBox2;
	private JButton btnSave;
	private JButton btnPlay;
	private JButton btnPlay2;
	private JLabel labelMaxBlocks;
	private JCheckBox splitBlocks;
	private	JLabel lblBlocks;
	private Image imgPlay, imgStop;
	private boolean play1_state, play2_state;
	private String file_list[] = new String[] {"MarMSX SND", "Digivoix", "Video Hits", "MarMSX S4b", "MarMSX S4b Plus", "MAP Player", "PSG-Sampler", "PC PCM 8-bit"};
	private String player_list[] = new String[] {"MarMSX SND (1-bit)", "Digivoix (1-bit)", "Video Hits (1-bit)", "MarMSX S4b (4-bit)", "MarMSX S4b Plus (4-bit)", "MAP Player (8-bit)", "PSG-Sampler (8-bit)"};


	public void setPlayerInfo(int freq, double time) {
		labelFrequency.setText(Integer.toString(freq)+" Hz");
		labelMaxTime.setText(String.format("%.2f",time)+" sec");
		updateFileTime();
	}

	public void getPlayerInfo() {
		Integer delay = (Integer) spinner.getValue();
		dsg.onDelayValueChanged(comboBox2.getSelectedIndex(), (int) delay);
	}

	public void setSoundFileInfo(int bts, int size, String filename) {
		labelBts.setText(Integer.toString(bts));
		labelFSize.setText(Integer.toString(size)+" bytes");
		labelCurrent.setText((String) comboBox.getSelectedItem());
		labelName.setText(filename);
		btnPlay.setEnabled(true);
		btnPlay2.setEnabled(true);
		btnSave.setEnabled(true);
		comboBoxDest.setEnabled(true);
		dsg.previewSoundData(comboBoxDest.getSelectedIndex());
		updateFileTime();
	}

	private void updateFileTime() {
		String str_freq = labelFrequency.getText();
		double freq = Double.parseDouble(str_freq.substring(0, str_freq.length() - 3));
		double time = dsg.getFileDuration(freq);
		labelTime.setText(String.format("%.2f",time));
		labelTime2.setText(String.format("%.2f",time));
	}

	public void enableSave() {
		btnSave.setEnabled(true);
	}

	private void setBlocksEnabled(boolean enable) {
		lblBlocks.setEnabled(enable);
		labelMaxBlocks.setEnabled(enable);
		spinner2.setEnabled(enable);
	}

	public void updatePreviewData(int bts, int size) {
		labelBts2.setText(Integer.toString(bts));
		labelFSize2.setText(Integer.toString(size)+" bytes");
		int blocks = 1 + (size-1)/16384;
		labelMaxBlocks.setText("/ "+Integer.toString(blocks));
		spinner2.setModel(new SpinnerNumberModel(blocks, 1, blocks, 1));
		int filetype = comboBoxDest.getSelectedIndex();
		if (filetype == 1 || filetype == 6 || filetype == 7) {
			splitBlocks.setSelected(false);
			setBlocksEnabled(false);
			splitBlocks.setEnabled(false);
		}
		else
			splitBlocks.setEnabled(true);
		updateFileTime();
	}

	public void setPlay1() {
		if (play1_state)
			btnPlay.setIcon(new ImageIcon(imgPlay));
		else
			btnPlay.setIcon(new ImageIcon(imgStop));
	}

	public void setPlay2() {
		if (play2_state)
			btnPlay2.setIcon(new ImageIcon(imgPlay));
		else
			btnPlay2.setIcon(new ImageIcon(imgStop));
	}

	public void resetPlays() {
		play1_state = play2_state = true;
		setPlay1();
		setPlay2();
	}

	public DSGUI(DSG new_dsg) {
		
		// Link DSG object here
		dsg = new_dsg;

		setTitle("MSX Digitized Sound Generator 1.4 - MarMSX 2022");
		setBounds(100, 100, 635, 385);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		play1_state = true;
		play2_state = true;

		// App icon
		Image img = new ImageIcon(this.getClass().getResource("imgs/icon.png")).getImage();
		setIconImage(img);


		//
		// Panel file - source
		//
		
		JPanel panel_file = new JPanel();
		panel_file.setBounds(12, 170, 297, 170);
		panel_file.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Source Sound", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_file.setLayout(null);
		contentPane.add(panel_file);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(file_list));
		comboBox.setBounds(86, 22, 130, 24);
		comboBox.setToolTipText("Sound file type to open");
		panel_file.add(comboBox);

		JButton btnOpen = new JButton("");
		btnOpen.setBounds(235, 20, 50, 30);
		img = new ImageIcon(this.getClass().getResource("imgs/fileopen.png")).getImage();
		btnOpen.setIcon(new ImageIcon(img));
		btnOpen.setToolTipText("Open a sound file");
		panel_file.add(btnOpen);
		btnOpen.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				dsg.onOpenClicked(comboBox.getSelectedIndex());
			}
		});

		btnPlay = new JButton("");
		btnPlay.setBounds(235, 80, 50, 30);
		imgPlay = new ImageIcon(this.getClass().getResource("imgs/play.png")).getImage();
		imgStop = new ImageIcon(this.getClass().getResource("imgs/stop.png")).getImage();
		btnPlay.setIcon(new ImageIcon(imgPlay));
		btnPlay.setToolTipText("Play loaded file");
		btnPlay.setEnabled(false);
		panel_file.add(btnPlay);
		btnPlay.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (play1_state) {
					String freq = labelFrequency.getText();
					dsg.onPlaySourceFile(Integer.parseInt(freq.substring(0, freq.length() - 3)));
					play2_state = true;
					play1_state = !play1_state;
				}
				else
					dsg.onPlayStop();
				setPlay1();
				setPlay2();
			}
		});

		JLabel lblSoundType = new JLabel("File type:");
		lblSoundType.setBounds(8, 27, 92, 15);
		panel_file.add(lblSoundType);

		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(8, 60, 90, 24);
		panel_file.add(lblFile);

		JLabel lblCurrent = new JLabel("Type:");
		lblCurrent.setBounds(8, 80, 90, 24);
		panel_file.add(lblCurrent);
		
		JLabel lblBitstreams = new JLabel("Bitstreams:");
		lblBitstreams.setBounds(8, 100, 90, 24);
		panel_file.add(lblBitstreams);
		
		JLabel lblFSize = new JLabel("Size:");
		lblFSize.setBounds(8, 120, 90, 24);
		panel_file.add(lblFSize);
		
		JLabel lblDuration = new JLabel("Time:");
		lblDuration.setBounds(8, 140, 90, 24);
		panel_file.add(lblDuration);

		labelName = new JLabel("None");
		labelName.setBounds(100, 60, 125, 24);
		panel_file.add(labelName);

		labelCurrent = new JLabel("None");
		labelCurrent.setBounds(100, 80, 125, 24);
		panel_file.add(labelCurrent);
		
		labelBts = new JLabel("0");
		labelBts.setBounds(100, 100, 125, 24);
		panel_file.add(labelBts);
		
		labelFSize = new JLabel("0");
		labelFSize.setBounds(100, 120, 125, 24);
		panel_file.add(labelFSize);
		
		labelTime = new JLabel("0");
		labelTime.setBounds(100, 140, 125, 24);
		panel_file.add(labelTime);


		//
		// Panel file - destiny
		//
		
		JPanel panel_dest = new JPanel();
		panel_dest.setBounds(321, 170, 297, 170);
		panel_dest.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Destiny Sound", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_dest.setLayout(null);
		contentPane.add(panel_dest);
		
		comboBoxDest = new JComboBox();
		comboBoxDest.setModel(new DefaultComboBoxModel(file_list));
		comboBoxDest.setBounds(86, 22, 130, 24);
		comboBoxDest.setToolTipText("Sound file type to save");
		comboBoxDest.setEnabled(false);
		panel_dest.add(comboBoxDest);
		comboBoxDest.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				dsg.previewSoundData(comboBoxDest.getSelectedIndex());
			}
		});

		btnSave = new JButton("");
		btnSave.setBounds(235, 20, 50, 30);
		img = new ImageIcon(this.getClass().getResource("imgs/msxdisk.png")).getImage();
		btnSave.setIcon(new ImageIcon(img));
		btnSave.setToolTipText("Convert and save a sound file");
		btnSave.setEnabled(false);
		panel_dest.add(btnSave);
		btnSave.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				dsg.onSaveClicked(comboBoxDest.getSelectedIndex(), splitBlocks.isSelected(), (Integer) spinner2.getValue());
			}
		});

		btnPlay2 = new JButton("");
		btnPlay2.setBounds(235, 80, 50, 30);
		btnPlay2.setIcon(new ImageIcon(imgPlay));
		btnPlay2.setToolTipText("Preview converted file");
		btnPlay2.setEnabled(false);
		panel_dest.add(btnPlay2);
		btnPlay2.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				if (play2_state) {
					String freq = labelFrequency.getText();
					dsg.onPlayDestinyFile(comboBoxDest.getSelectedIndex(), Integer.parseInt(freq.substring(0, freq.length() - 3)));
					play1_state = true;
					play2_state = !play2_state;
				}
				else
					dsg.onPlayStop();
				setPlay1();
				setPlay2();
			}
		});
		
		JLabel lblSoundType2 = new JLabel("File type:");
		lblSoundType2.setBounds(8, 27, 92, 15);
		panel_dest.add(lblSoundType2);

		JLabel lblBitstreams2 = new JLabel("Bitstreams:");
		lblBitstreams2.setBounds(8, 60, 90, 24);
		panel_dest.add(lblBitstreams2);
		
		JLabel lblFSize2 = new JLabel("Size:");
		lblFSize2.setBounds(8, 80, 90, 24);
		panel_dest.add(lblFSize2);
		
		JLabel lblDuration2 = new JLabel("Time:");
		lblDuration2.setBounds(8, 100, 90, 24);
		panel_dest.add(lblDuration2);

		lblBlocks = new JLabel("16 KB blocks:");
		lblBlocks.setBounds(8, 130, 95, 24);
		panel_dest.add(lblBlocks);

		labelBts2 = new JLabel("0");
		labelBts2.setBounds(100, 60, 125, 24);
		panel_dest.add(labelBts2);
		
		labelFSize2 = new JLabel("0");
		labelFSize2.setBounds(100, 80, 125, 24);
		panel_dest.add(labelFSize2);
		
		labelTime2 = new JLabel("0");
		labelTime2.setBounds(100, 100, 125, 24);
		panel_dest.add(labelTime2);

		spinner2 = new JSpinner();
		spinner2.setModel(new SpinnerNumberModel(1, 1, 1, 1));
		spinner2.setBounds(110, 132, 50, 23);
		spinner2.setToolTipText("Number of blocks to save");
		panel_dest.add(spinner2);
		spinner2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				getPlayerInfo();
			}
		});

		labelMaxBlocks = new JLabel("/ 1");
		labelMaxBlocks.setBounds(170, 130, 100, 24);
		panel_dest.add(labelMaxBlocks);

		splitBlocks = new JCheckBox();
		splitBlocks.setBounds(230, 132, 60, 23);
		splitBlocks.setText("Split");
		panel_dest.add(splitBlocks);
		splitBlocks.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				setBlocksEnabled(splitBlocks.isSelected());
			}
		});

		setBlocksEnabled(false);


		//
		// Panel player
		//
		
		JPanel panel_player = new JPanel();
		panel_player.setBounds(12, 10, 480, 157);
		panel_player.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Sound pre-processing", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel_player);
		panel_player.setLayout(null);
		
		JLabel lblPlayer = new JLabel("MSX Player:");
		lblPlayer.setBounds(8, 27, 100, 15);
		panel_player.add(lblPlayer);
		
		comboBox2 = new JComboBox();
		comboBox2.setModel(new DefaultComboBoxModel(player_list));
		comboBox2.setBounds(105, 22, 190, 24);
		comboBox2.setToolTipText("MSX Player");
		panel_player.add(comboBox2);
		comboBox2.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				int value = (Integer) spinner.getValue(), min=1, max=100;
				if (comboBox2.getSelectedIndex() == 6) {
					min=0;
					max=100;
				}
				if (value < min) value = min;
				if (value > max) value = max;
				spinner.setModel(new SpinnerNumberModel(value, min, max, 1));
				getPlayerInfo();
			}
		});
		
		JLabel lblDelay = new JLabel("Player delay:");
		lblDelay.setBounds(320, 27, 100, 15);
		panel_player.add(lblDelay);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(14, 1, 100, 1));
		spinner.setBounds(420, 22, 50, 23);
		spinner.setToolTipText("<html>Default value is 14.<br>Less, the sound is better but the size is greater.<br>More, the sound is worse but the size is smaller.</html>");
		panel_player.add(spinner);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				getPlayerInfo();
			}
		});

		JLabel lblAudioTitle = new JLabel("Open PC audio editor (Audacity):");
		lblAudioTitle.setBounds(8, 57, 300, 15);
		panel_player.add(lblAudioTitle);

		JLabel lblAudio1st = new JLabel("1. Set audio as MONO.");
		lblAudio1st.setBounds(8, 75, 300, 15);
		panel_player.add(lblAudio1st);

		JLabel lblAudio2nd = new JLabel("2. Select an area with max duration per block:");
		lblAudio2nd.setBounds(8, 92, 330, 15);
		panel_player.add(lblAudio2nd);

		JLabel lblAudio3rd = new JLabel("3. Set sound frequency as:");
		lblAudio3rd.setBounds(8, 109, 190, 15);
		panel_player.add(lblAudio3rd);

		JLabel lblAudio4th = new JLabel("4. Export sound as PCM 8-bit unsigned.");
		lblAudio4th.setBounds(8, 126, 280, 15);
		panel_player.add(lblAudio4th);
		
		labelMaxTime = new JLabel("0");
		labelMaxTime.setBounds(340, 92, 79, 15);
		panel_player.add(labelMaxTime);

		labelFrequency = new JLabel("0");
		labelFrequency.setBounds(205, 109, 79, 15);
		panel_player.add(labelFrequency);


		//
		// Misc
		//

		JButton btnHelp = new JButton("");
		btnHelp.setBounds(430, 120, 40, 30);
		img = new ImageIcon(this.getClass().getResource("imgs/help.png")).getImage();
		btnHelp.setIcon(new ImageIcon(img));
		btnHelp.setToolTipText("Open help");
		panel_player.add(btnHelp);
		btnHelp.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				Wizard help_window = new Wizard();
				help_window.setModal(true);
				help_window.setVisible(true);
			}
		});

		
		JLabel lblLogo = new JLabel("logo");
		lblLogo.setBounds(515, 20, 100, 100);
		img = new ImageIcon(this.getClass().getResource("imgs/logo.png")).getImage();
		lblLogo.setIcon(new ImageIcon(img));
		contentPane.add(lblLogo);

		JLabel lblMarMSXLogo = new JLabel("logo");
		lblMarMSXLogo.setBounds(515, 125, 100, 27);
		img = new ImageIcon(this.getClass().getResource("imgs/marmsx.png")).getImage();
		lblMarMSXLogo.setIcon(new ImageIcon(img));
		contentPane.add(lblMarMSXLogo);
	}
}
