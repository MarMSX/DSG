/***************************************************************************
 *   Class PlayerData                                                      *
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

public class PlayerData {

	// Players data
	// Players: 0 = MSX DSG, 1 = Digivoix, 2 = Video Hits, 3 = MarMSX Sb4, 4 = MarMSX Sb4 Plus
	//          5 = MAP Player, 6 = PSG Sampler
	// Data format: from y = ax + b -> a, b, no_samples per byte
	private double regression[][] = { {5.0285466936277116e-06, 2.2860932064568719e-05, 8.0},
                                          {4.9500000078592367e-06, 2.1500953539584092e-05, 8.0},
                                          {5.0286556923303416e-06, 1.9437578240034441e-05, 8.0},
                                          {5.0288509695871045e-06, 3.1045975748696046e-05, 2.0},
                                          {5.0029596887580154e-06, 5.4283055491564911e-05, 2.0},
                                          {3.9120519139156144e-06, 6.0121839355563517e-05, 1.0},
                                          {3.888790076146922e-06, 6.7648678483137972e-05, 1.0}
				        };

	public int getPlayerFrequency(int player, int delay) {
		if ((player < 0) || (player > 6))
			return 0;

		return (int) (1.0 / (regression[player][0] * delay + regression[player][1]));
	}

	public double getPlayerMaxTime(int player, int delay) {
		if ((player < 0) || (player > 6))
			return 0;

		return (regression[player][0] * delay + regression[player][1]) * 16384 * regression[player][2];
	}
}
