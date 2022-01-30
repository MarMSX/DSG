/***************************************************************************
 *   Class PlaySound                                                       *
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

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class PlaySound {

	Clip clip;
	DSG dsg;

	public PlaySound(DSG _dsg) {
		dsg = _dsg;
	}

	public void play(byte [] data, int frequency) {
		int sampleRate=frequency, sampleSizeInBits=8, channels=1;
		boolean signed=false, bigEndian=true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

		try {
			if (clip != null)
				clip.stop();
			clip = AudioSystem.getClip();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(format, data, 0, data.length);
			clip.addLineListener(new LineListener() {
				public void update(LineEvent e) {
			        	if (e.getType() == LineEvent.Type.STOP)
				            dsg.resetPlays();
				}
			});
			clip.start();
		
		} catch(Exception e) {
			System.out.println("Erro ao reproduzir o arquivo de som: " + e.getMessage());
		}
	}

	public void stop() {
		clip.stop();
	}
}
