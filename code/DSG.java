/***************************************************************************
 *   Class DSG                                                             *
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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

class DSG {

	private DSGUI window;
	File current_dir;
	MSXSound sound;
	PlaySound ps;

	public DSG() {
		// Main form
		sound = new MSXSound();
		ps = new PlaySound(this);
		window = new DSGUI(this);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getPlayerInfo();
		window.setVisible(true);
	}


	//
	// Events
	//

	public void previewSoundData(int format) {
		window.updatePreviewData(sound.getFileBitstreams(), sound.getFileToSaveSize(format));
	}

	public double getFileDuration(double frequency) {
		return sound.getFileDuration(frequency);
	}

	public void onDelayValueChanged(int player, int delay) {
		PlayerData pd = new PlayerData();
		int freq = pd.getPlayerFrequency(player, delay);
		double time = pd.getPlayerMaxTime(player, delay);
		window.setPlayerInfo(freq, time);
	}

	public void onPlaySourceFile(int frequency) {
		ps.play(sound.getAudioStream(), frequency);
	}

	public void onPlayDestinyFile(int format, int frequency) {
		ps.play(sound.getAudioStream(format), frequency);
	}

	public void onPlayStop() {
		ps.stop();
	}

	public void resetPlays() {
		window.resetPlays();
	}

	// File types: 0 = MSX SND, 1 = MSX FRED, 2 = MSX S4B, 3 = PC RAW
	private FileNameExtensionFilter getFilter(int filetype) {
		String filters[][] = {{"MSX SND (*.snd)", "snd"}, {"Digivoix FRED (*.fre)", "fre"}, {"Video Hits (*.snd)", "snd"}, {"S4b - 4 bits (*.s4b)", "s4b"}, {"S4b - 4 bits (*.s4b)", "s4b"}, {"MAP Player (*.psg)", "psg"}, {"PSG Sampler (*.psg)", "psg"}, {"PCM 8 bits unsigned (*.raw)", "raw"}};
		FileNameExtensionFilter filter = new FileNameExtensionFilter(filters[filetype][0],filters[filetype][1]);

		return filter;
	}

	private String getFileExtension(int filetype) {
		if ((filetype < 0) || (filetype > 7))
			return "";

		String extensions [] = {".snd", ".fre", ".snd", ".s4b", ".s4b", ".psg", ".psg", ".raw"};
		return extensions[filetype];
	}

	public void onOpenClicked(int filetype) {
		JFileChooser fc_load = new JFileChooser();
		fc_load.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc_load.setMultiSelectionEnabled(false);
		fc_load.setFileFilter(getFilter(filetype));
		fc_load.setCurrentDirectory(current_dir);

		int result = fc_load.showOpenDialog(window);

		if (result == JFileChooser.CANCEL_OPTION)
			return;

		File filename = fc_load.getSelectedFile();
		current_dir = fc_load.getCurrentDirectory();

		// Open file
		int flag = sound.openFile(filename.toString(), filetype);

		if (flag == 0) {
			JOptionPane.showMessageDialog(window, "Error while opening file.", "Load Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (flag == -1) {
			JOptionPane.showMessageDialog(window, "Invalid Digivoix FRED file.", "Load Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update file info
		window.setSoundFileInfo(sound.getFileBitstreams(), sound.getFileSize(), filename.getName());

		// Enable saving
		window.enableSave();
	}


	public void onSaveClicked(int filetype, boolean is_split, int blocks) {
		JFileChooser fc_save = new JFileChooser();
		fc_save.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc_save.setFileFilter(getFilter(filetype));
		fc_save.setCurrentDirectory(current_dir);

		// Check if sound buffer is empty
		if (sound.isEmpty()) {
			JOptionPane.showMessageDialog(window, "There is no sound file to save.", "Save error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int no_saves = 1;

		if (is_split)
			no_saves = blocks;

		int result, flag, blk=0;
		File filename;
		String extension;

		fc_save.setDialogTitle("Save whole file");

		for (int i=0; i<no_saves; i++) {
			if (is_split)
				fc_save.setDialogTitle(String.format("Save file #%d", i+1));
			result = fc_save.showSaveDialog(window);
			if (result == JFileChooser.CANCEL_OPTION) 
				return;

			filename = fc_save.getSelectedFile();
			current_dir = fc_save.getCurrentDirectory();

			// Check for extension
			extension = getFileExtension(filetype);
			if (!checkFileExtension(filename.getName().toString(), extension))
				filename = new File(filename.toString() + extension);

			// Check if file exists
			if (filename.exists()) {
				if (JOptionPane.showConfirmDialog(window, "This file already exists. Overwite?", "Warning", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
					return;
			}

			// Save file
			if (is_split)
				blk = i+1;

			flag = sound.saveFile(filename.toString(), filetype, 1, blk);

			if (flag == 0) {
				JOptionPane.showMessageDialog(window, "Error while saving file.", "Save Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

	private boolean checkFileExtension(String name, String ext) {
		if (name.length() < 4)
			return false;

		return name.toLowerCase().substring(name.length()-4, name.length()).equals(ext);
	}
}
