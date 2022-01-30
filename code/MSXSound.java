/***************************************************************************
 *   Class MSXSound                                                        *
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

import java.io.*;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.lang.Math.*;

public class MSXSound {

	// SND data contains the wave intensity on each bit (octets)
	// S4b data contains the wave intensity on each nibble (4 bits)
	// PCM data contains the wave intensity on each byte (8-bit unsigned)
	private byte sound_data[];

	// File types:
	// -1 - Empty
	// 0 - MarMSX SND, 1-bit
	// 1 - Digivoix, 1-bit
	// 2 - Video Hits, 1-bit
	// 3 - MarMSX S4b, 4-bit
	// 4 - MarMSX S4b Plus, 4-bit
	// 5 - MAP Player, 8-bit
	// 6 - PSG Player, 8-bit
	// 7 - PCM Raw, 8-bit
	private int current_file_type = -1;

	// Samples per byte
	private int sampb[] = {8, 8, 8, 2, 2, 1, 1, 1};

	// Total MSX Player formats
	private final int max_formats = 7;


	//
	// Get info
	//

	public boolean isEmpty() {
		return (current_file_type == -1);
	}

	public int getFileSize() {
		if ((current_file_type == -1) || (sound_data == null))
			return 0;

		return sound_data.length;
	}

	public int getFileBitstreams() {
		if ((current_file_type == -1) || (sound_data == null))
			return 0;
		return getFileSize() * sampb[current_file_type];
	}

	public double getFileDuration(double frequency) {
		return getFileBitstreams() / frequency;
	}


	// Return the number of 16 KB blocks
	public int getFileToSaveNumOfBlocks(int filetype) {
		int block_size = 1024 * 16;
		int file_size = getFileToSaveSize(filetype) - 1;
		int blocks = 1 + (file_size / block_size);
		int remain_data = Math.abs(block_size * (blocks - 1) - file_size);

		if ((remain_data < 100) && (blocks > 1))
			blocks--;

		return blocks;
	}

	public int getFileToSaveSize(int filetype) {
		int size = getFileBitstreams();

		return size / sampb[current_file_type];
	}

	// Get current sound in PCM 8 bits
	public byte [] getAudioStream() {
		if ((current_file_type == -1) || (sound_data == null))
			return null;

		switch (current_file_type) {
			case 3: return S4btoPCM();
			case 4: return S4btoPCM();
			case 5: return sound_data.clone();
			case 6: return sound_data.clone();
			case 7: return sound_data.clone();
			default: return SNDtoPCM();
		}
	}

	// Get destiny sound converted to PCM 8 bits (preview sound)
	public byte [] getAudioStream(int format) {
		if (format < 0 || format > max_formats)
			return null;

		// Sound -> PCM
		byte [] data = getAudioStream(), back_up;
		int pcm2snd_method = 1, back_format;

		// Save original sound
		back_up = sound_data;
		sound_data = data;

		// PCM -> New Sound
		switch (format) {
			case 3: data = PCMtoS4b(); break;
			case 4: data = PCMtoS4b(); break;
			case 5: data = sound_data; break;
			case 6: data = sound_data; break; 
			case 7: data = sound_data; break; 
			default: data = PCMtoSND(pcm2snd_method);
		}

		sound_data = data;
		back_format = current_file_type;
		current_file_type = format;
		data = getAudioStream();
		current_file_type = back_format;

		// Recover
		sound_data = back_up;

		return data;
	}


	//
	// Public I/O operations
	//

	public int openFile(String filename, int filetype) {
		int flag;
		switch (filetype) {
			case 1 : flag = loadFRED(filename); break;
			case 3 : flag = loadS4b(filename, true); break;
			case 4 : flag = loadS4b(filename, true); break;
			case 5 : flag = loadPCM(filename, true); break;
			case 6 : flag = loadPCM(filename, false); break;
			case 7 : flag = loadPCM(filename, false); break;
			default : flag = loadSND(filename, true); break;
		}

		if (flag == 1)
			current_file_type = filetype;

		return flag;
	}

	public int saveFile(String filename, int filetype, int pcm2snd_method, int block_no) {
		if ((current_file_type == -1) || (sound_data == null))
			return -1;

		if ((filetype < 0) || (filetype > max_formats))
			return 0;

		// Get sound format to save
		byte [] save_data = getConvertedSaveData(filetype, pcm2snd_method, block_no);

		switch (filetype) {
			case 1 : return saveFRED(filename, save_data);
			case 3 : return saveS4b(filename, save_data, block_no, true);
			case 4 : return saveS4b(filename, save_data, block_no, true);
			case 5 : return savePCM(filename, save_data, block_no, true);
			case 6 : return savePCM(filename, save_data, 0, false);
			case 7 : return savePCM(filename, save_data, 0, false);
			default : return saveSND(filename, save_data, block_no, true);
		}
	}


	//
	// Automatic file conversion to save
	//

	// This method converts first current data to PCM and then to the required format
	private byte[] getConvertedSaveData(int filetype, int pcm2snd_method, int block_no) {
		if (filetype == current_file_type)
			return sound_data;

		byte [] save_data, back_up;

		// Change for a moment current type
		back_up = sound_data;

		// Current format to PCM
		switch (current_file_type) {
			case 3: sound_data = S4btoPCM(); break;
			case 4: sound_data = S4btoPCM(); break;
			case 5: break;
			case 6: break;
			case 7: break;
			default: sound_data = SNDtoPCM();
		}

		// PCM to required format
		switch (filetype) {
			case 3: save_data = PCMtoS4b(); break;
			case 4: save_data = PCMtoS4b(); break;
			case 5: save_data = sound_data; break;
			case 6: save_data = sound_data; break;
			case 7: save_data = sound_data; break;
			default: save_data = PCMtoSND(pcm2snd_method);
		}

		// Restore original data
		sound_data = back_up;

		return save_data;
	}


	//
	// Video Hits SND format
	//

	private int loadSND(String filename, boolean is_marmsx) {
		byte data_load[];

		File arq = new File(filename);
		data_load = new byte[(int) arq.length()];
		int header_size = (is_marmsx) ? 7 : 0;

		try {
			InputStream is = new FileInputStream(filename);
			is.read(data_load);
			// Remove header - scr, i, dest, j, size
			sound_data = new byte[data_load.length - header_size];
			System.arraycopy(data_load, header_size, sound_data, 0, sound_data.length);
		}
		catch (IOException e) {
			return 0;
		}
		return 1;
	}

	// Generic SND saving
	private int saveSND(String filename, byte [] data, boolean is_marmsx) {
		if (data == null)
			return 0;

		int extra_data = data.length % 256, header_size = (is_marmsx) ? 7 : 0;
		int size = data.length + ((is_marmsx && extra_data > 0) ? 256 - extra_data : 0);
		byte data_save[] = new byte[size + header_size];

		// Copy sound data to file data
		System.arraycopy(data, 0, data_save, header_size, data.length);

		// Add header and data - scr, i, dest, j, size
		if (is_marmsx) {
			int end_address = 0x9000 + size - 1;
			data_save[0] = (byte) 0xFE; data_save[1] = 0; data_save[2] = (byte) 0x90;
			data_save[5] = 0; data_save[6] = (byte) 0x90;
			data_save[3] = (byte) (end_address & 0xFF);
			data_save[4] = (byte) ((end_address >> 8) & 0xFF);
		}

		try {
			OutputStream os = new FileOutputStream(filename);
			os.write(data_save);
		}
		catch (IOException e) {
			return 0;
		}

		return 1;
	}

	// Return SND blocks of 16 KB
	private byte [] getSNDBlock(byte [] save_data, int block_no) {
		if ((block_no < 1) || save_data == null)
			return null;

		// If SND size is less than 16 KB, cannot be block 2
		if ((save_data.length < 16385) && (block_no == 2))
			return null;
		
		byte data[] = new byte[16384];
		int ini_pos = 16384 * (block_no - 1);
		int size = save_data.length - ini_pos;

		if (size > 16384)
			size = 16384;

		// Copy block
		System.arraycopy(save_data, ini_pos, data, 0, size);

		return data;
	}

	// Save file using 16 KB blocks - block_no indicates the 16 KB block number (1 or 2)
	// If block is 0, save full file
	private int saveSND(String filename, byte [] save_data, int block_no, boolean is_marmsx) {
		if (block_no == 0)
			return saveSND(filename, save_data, is_marmsx);

		byte data[] = getSNDBlock(save_data, block_no);

		if (data == null)
			return 0;

		return saveSND(filename, data, is_marmsx);
	}


	//
	// S4b file format - use 4 bits
	//

	private int loadS4b(String filename, boolean is_marmsx) {
		return loadSND(filename, is_marmsx);
	}

	private int saveS4b(String filename, byte [] save_data, int block_no, boolean is_marmsx) {
		return saveSND(filename, save_data, block_no, is_marmsx);
	}


	//
	// Digivoix FRED format (it is a SND format saved as text)
	//

	private int loadFRED(String filename) {
		// Flag codes: 1= success, 0=read error, -1=not a FRED file
		int start_add=0, end_add=0, fsize=0, p=0;
		File arq = new File(filename);
		String line;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(arq));
			// Read file hader
			for (int i=0; i<4; i++) {
				line = reader.readLine();
				if (line == null)
					return -1;
				if (i==2)
					start_add = (int) Integer.parseInt(line.trim());
				if (i==3)
					end_add = (int) Integer.parseInt(line.trim());
			}
			// Check if are valid addresses
			if ((start_add < 16384) || (start_add >= end_add) || (end_add > 32768))
				return -1;
			fsize = end_add - start_add + 1;
			sound_data = new byte[fsize];
			while ((line = reader.readLine()) != null) {
				if (p >= fsize)
					break;
				sound_data[p++] = (byte) (Integer.parseInt(line.trim()) & 0xFF);
			}
		}
		catch (IOException e) {
			return 0;
		}
		catch (NumberFormatException e) {
			return 0;
		}

		return 1;
	}

	private int saveFRED(String filename, byte [] save_data) {
		if (sound_data == null)
			return 0;

		Formatter output;

		try {
			output = new Formatter(filename);
		} catch (Exception e) {
			return 0;
		}

		int initial_address = 0x4000;
		int final_address = initial_address + save_data.length - 1;
		char data; // This is unsigned

		// File size limit is 16 KB
		if (final_address > 32768)
			final_address = 32768;

		// Write FRED data
		try {
			// Add header
			output.format(" 1%c%c", 13, 10);
			output.format("MarMSX DSG%c%c", 13, 10);
			output.format(" %d%c%c", initial_address, 13, 10);
			output.format(" %d%c%c", final_address, 13, 10);

			// Add data
			for (int i=0; i<save_data.length; i++) {
				if (i >= 16384)
					break;
				data = (char) save_data[i];
				output.format(" %d%c%c", (int) (data & 0xFF), 13, 10);
			}
		} catch (FormatterClosedException e) {
			return 0;
		}

		output.close();

		return 1;
	}


	//
	// 8-bit unsigned PCM format
	//

	private int loadPCM(String filename, boolean header) {
		File arq = new File(filename);
		byte [] data_load = new byte[(int) arq.length()];
		int header_size = (header) ? 7 : 0;

		try {
			InputStream is = new FileInputStream(filename);
			is.read(data_load);
			sound_data = new byte[data_load.length - header_size];
			System.arraycopy(data_load, header_size, sound_data, 0, sound_data.length);
		}
		catch (IOException e) {
			return 0;
		}

		return 1;
	}

	private int savePCM(String filename, byte [] save_data, int block_no, boolean header) {
		if (sound_data == null)
			return 0;

		byte [] data;
		if (block_no > 0)
			data = getSNDBlock(save_data, block_no);
		else
			 data = save_data;

		byte [] header_data = {(byte) 0xFE, 0x00, (byte) 0x90, 0x00, 0x00, 0x00, (byte) 0x90};
		header_data[3] = (byte) ((data.length + 0x9000) % 256);
		header_data[4] = (byte) ((data.length + 0x9000) / 256);

		try {
			OutputStream os = new FileOutputStream(filename);
			if (header)
				os.write(header_data);
			os.write(data);
		}
		catch (IOException e) {
			return 0;
		}

		return 1;
	}


	//
	// Format convertion tools
	//

	private byte getPixelBit(byte pixel, int b) {
		int mask = 1 << b;

		return (byte) ((pixel & mask) >> b);
	}

	private byte setBit(byte data, int b) {
		int mask= 1 << b;

		return (byte) (data | mask);
	}

	private byte [] SNDtoPCM() {
		if (sound_data == null)
			return null;

		byte [] save_data = new byte[sound_data.length * 8];

		int p=0;
		for (int i=0; i<sound_data.length; i++) {
			for (int b=7; b>=0; b--) {
				save_data[p++] = (byte) (getPixelBit(sound_data[i], b) == 1 ? 158 : 94);
			}
		}

		return save_data;
	}

	// 8-bit PCM has 256 levels of volume or intensity. MSX SND has only 1 bit or 2 levels.
	private byte [] PCMtoSND(int method) {
		if (sound_data == null)
			return null;

		// Method 0 = threshold, 1 = gradient
		if ((method < 0) || (method > 1))
			return null;

		byte [] save_data = new byte[sound_data.length / 8];

		int p=0;
		byte data;
		char pcm, prev_pcm = 127; // byte is a signed number, char is unsigned
		for (int i=0; i<save_data.length; i++) {
			data = 0;
			for (int b=7; b>=0; b--) {
				pcm = (char) sound_data[p];
				if (i != 0)
					prev_pcm = (char) sound_data[p-1];
				p++;

				// Gradient				
				if ((pcm - prev_pcm > 0) && (method == 1))
					data = setBit(data, b);

				// Threshold
				if ((pcm > 127) && (method == 0))
					data = setBit(data, b);
			}
			save_data[i] = data;
		}

		return save_data;
	}

	// PSG to PCM
	// Filter: 0 - Linear, 1 - Exp
	private byte PSG_to_PCM(int n, int filter) {
		switch (filter) {
			case 1 : return (byte) (Math.pow(2.0, -(15-n)/2.0) * 255.0);
			default : return (byte) (n * 255.0 / 15.0);
		}
	}

	// SND 4-bit uses MSX volume to code wave format 
	private byte [] S4btoPCM() {
		if (sound_data == null)
			return null;

		byte [] save_data = new byte[sound_data.length * 2];

		int p=0;
		for (int i=0; i<sound_data.length; i++) {
			save_data[p++] = PSG_to_PCM((sound_data[i] >> 4) & 0XF, 0);
			save_data[p++] = PSG_to_PCM(sound_data[i] & 0XF, 0);
		}

		return save_data;
	}

	// PCM to PSG
	// Filter: 0 - Linear, 1 - Log
	private char PCM_to_PSG(int y, int filter) {
		if (y == 0)
			return 0;
		double n;
		switch (filter) {
			case 1 : n = Math.round(2.0 * Math.log(y/255.0) / Math.log(1.5) + 15.0); if (n<0) n=0; return (char) n;
			default : return (char) (y * 16.0/256.0);
		}
	}

	// 8-bit PCM has 256 levels of volume or intensity. MSX SND 4-bit has 4 bits or 16 levels.
	private byte [] PCMtoS4b() {
		if (sound_data == null)
			return null;

		byte [] save_data = new byte[sound_data.length / 2];

		int p=0;
		byte data;
		char snd1, snd2; // Get nibbles (half of byte)
		for (int i=0; i<save_data.length; i++) {
			data=0;
			snd1 = (char) PCM_to_PSG(sound_data[p++], 0);
			snd2 = (char) PCM_to_PSG(sound_data[p++], 0);
			data = (byte) ((snd1 << 4) + snd2);
			save_data[i] = data;
		}

		return save_data;
	}


	//
	// Degub function
	//

	private void printSND() {
		if ((sound_data == null) || (current_file_type == -1)) {
			System.out.println("Sound data is empty.");
			return;
		}

		switch (current_file_type) {
			case 1: System.out.println("Digivoix FRED data");
			case 3: System.out.println("S4b data");
			case 4: System.out.println("S4b data");
			case 5: System.out.println("PCM 8-bit unsigned Data");
			case 6: System.out.println("PCM 8-bit unsigned Data");
			case 7: System.out.println("PCM 8-bit unsigned Data");
			default: System.out.println("SND Data");
		}
		System.out.println(" File size: " + getFileSize());
		System.out.println(" Bitstreams: " + getFileBitstreams());
		System.out.print(" The first 10 bytes are: ");
		for (int i=0; i<10; i++) {
			if (i<sound_data.length)
				System.out.printf("%02X ",sound_data[i]);
			else
				System.out.print("xx ");
		}
		System.out.println("");
	}

}
