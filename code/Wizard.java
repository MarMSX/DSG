/***************************************************************************
 *   Class Main                                                            *
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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import javax.swing.Box;
import java.util.ArrayList;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Image;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Wizard extends JDialog {

	private JPanel contentPanel = new JPanel();
	private ArrayList<JPanel> panel_list = new ArrayList<JPanel>();
	private JButton backButton;
	private JButton nextButton;
	private int page = 0;

	public Wizard() {
		setBounds(100, 100, 550, 400);
		setTitle("MSX DGS Wizard");
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(null);
		add(contentPanel, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new BorderLayout(0, 0));
		
		buttonPane.add(new JSeparator(), BorderLayout.NORTH);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBorder(new EmptyBorder(5, 10, 5, 10));
		
		backButton = new JButton("< Back");
		horizontalBox.add(backButton);
		getRootPane().setDefaultButton(backButton);
		backButton.setEnabled(false);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setPrevPage();
			}
		});
		
		horizontalBox.add(Box.createHorizontalStrut(10));

		nextButton = new JButton("Next >");		
		horizontalBox.add(nextButton);
		getRootPane().setDefaultButton(nextButton);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNextPage();
			}
		});
		
		horizontalBox.add(Box.createHorizontalStrut(30));

		JButton okButton = new JButton("Ok");
		horizontalBox.add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		buttonPane.add(horizontalBox, BorderLayout.EAST);

		startPanels();	
	}

	// Change page
	private void changePage() {
		contentPanel.removeAll();
		contentPanel.add(panel_list.get(page));
		contentPanel.repaint();
	}

	// Set next page
	private void setNextPage() {
		if (page >= panel_list.size() - 1)
			return;
		page++;

		if (page == panel_list.size() - 1)
			nextButton.setEnabled(false);

		backButton.setEnabled(true);
		changePage();
	}

	// Set previous page
	private void setPrevPage() {
		if (page <= 0)
			return;
		page--;

		if (page == 0)
			backButton.setEnabled(false);

		nextButton.setEnabled(true);
		changePage();
	}

	// Panel factory
	private JPanel createPanel(String msg, String [] img_path, Rectangle [] bounds, int no_imgs) {
		JLabel imgLabel;
		Image img;
		JPanel panel = new JPanel();

		panel.setBounds(0, 0, 548, 334);
		panel.setLayout(null);
		
		// Component 0 - title
		JLabel lblMsxFontEditor = new JLabel("MSX DSG Wizard");
		lblMsxFontEditor.setHorizontalAlignment(SwingConstants.CENTER);
		lblMsxFontEditor.setFont(new Font("Dialog", Font.BOLD, 30));
		lblMsxFontEditor.setBounds(0, 12, 548, 36);
		lblMsxFontEditor.setBackground(new Color(192, 192, 192));
		lblMsxFontEditor.setOpaque(true);
		panel.add(lblMsxFontEditor);

		// Component 1 - description text
		JLabel msgLabel = new JLabel(msg);
		msgLabel.setVerticalAlignment(SwingConstants.TOP);
		msgLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		msgLabel.setBounds(10, 60, 526, 262);
		panel.add(msgLabel);

		if (img_path == null)
			return panel;

		// Images
		for (int i=0; i<no_imgs; i++) {
			imgLabel = new JLabel("");
			panel.add(imgLabel);
			img = new ImageIcon(this.getClass().getResource(img_path[i])).getImage();
			imgLabel.setBounds(bounds[i]);
			imgLabel.setIcon(new ImageIcon(img));
		}

		return panel;
	}

	//
	// Here, the wizard data is filled
	//

	private void startPanels() {
		JPanel panel;
		String msg;
		String [] img_path = new String[2];
		Rectangle [] bounds = new Rectangle[2];

		// Page 1
		msg = "<html>MSX Digitized Sound Generator works together with a PC sound editor for converting mp3 or wav files. We strongly recommend to use Audacity.<br><br>Click on the next button ...</html>";
		panel = createPanel(msg, null, null, 0);
		panel_list.add(panel);
		contentPanel.add(panel);

		// Page 1.5
		msg = "<html>Players available:<br><br><table border=1><tr><th bgcolor=#dddddd>Player</th><th bgcolor=#dddddd>Type</th><th bgcolor=#dddddd>Max. Freq.</th></tr><tr><td>MarMSX SND</td><td>1-bit</td><td>35.8 KHz</td></tr><tr><td>Digivoix</td><td>1-bit</td><td>37.8 KHz</td></tr><tr><td>Video Hits</td><td>1-bit</td><td>40.8 KHz</td></tr><tr><td>MarMSX S4b</td><td>4-bit</td><td>27.7 Khz</td></tr><tr><td>MarMSX S4b Plus</td><td>4-bit</td><td>16.8 KHz</td></tr><tr><td>MAP Player</td><td>8-bit</td><td>15.6 KHz</td></tr><tr><td>PSG Sampler</td><td>8-bit</td><td>13.9 KHz</td></tr></table></html>";
		panel = createPanel(msg, null, null, 0);
		panel_list.add(panel);
		contentPanel.add(panel);

		// Page 2
		msg = "<html>Open Audacity and then the desired sound to be digitized. Our first task is to convert the audio to mono, if it is stereo.</html>";
		img_path[0] = "wizard/pic_001.png";
		bounds[0] = new Rectangle(100, 130, 300, 141);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 3
		msg = "<html>MSX memory is very limited to play digitized sounds. The maximum length of the sound varies depending on the MSX player and the selected delay. The delay is the way that the MSX players synchronize to the original sound frequency.</html>";
		panel = createPanel(msg, null, null, 0);
		panel_list.add(panel);

		// Page 4
		msg = "<html>Notice that the higher the frequency, the better the sound quality, but the shorter the sound duration.</html>";
		img_path[0] = "wizard/pic_002.png";
		bounds[0] = new Rectangle(100, 100, 300, 215);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 5
		msg = "<html>MSX DSG tells us the max sound duration per 16 KB block sizes.<br>The max blocks each player can hold is:<ul><li>MarMSX SND - 2 blocks</li><li>Digivoix - 1 block</li><li>Video Hits - 2 blocks</li><li>MarMSX S4b - 2 blocks</li><li>MarMSX S4b Plus - 2 blocks</li><li>MAP Player - 2 blocks</li><li>PSG sampler - up to MSX memory mapper size</li></ul></html>";
		panel = createPanel(msg, null, null, 0);
		panel_list.add(panel);

		// Page 6
		msg = "<html>Select in the MSX DSG the MSX player and delay. Pay attention to the frequency and max duration per block that is shown.</html>";
		img_path[0] = "wizard/pic_003.png";
		bounds[0] = new Rectangle(100, 130, 350, 127);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 7
		msg = "<html>Back to the Audacity, let's select a part of the music with the maximum duration per block shown in the MSX DSG. If you use two blocks, then multiply the sound duration per 2.</html>";
		img_path[0] = "wizard/pic_004.png";
		bounds[0] = new Rectangle(100, 150, 350, 44);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 8
		msg = "<html>The PC sounds today are generally sampled to 44,100 Hz. We must resample the sound to the same frequency shown in the MSX DSG. So, change the Audacity project's frequency value.<br>The wrong frequency sampling will result in lower or faster sound play.</html>";
		img_path[0] = "wizard/pic_005.png";
		bounds[0] = new Rectangle(50, 180, 400, 35);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 9
		msg = "<html>The next step is to save the sound sampling as RAW file. For that, do the following:<ol><li>Open the save dialog</li><li>Choose: 'Another files without compression'</li><li>Then, 'Options ...'</li><li>Header: RAW (header-less)</li><li>Codification: Unsigned 8-bit PCM</li></ol></html>";
		panel = createPanel(msg, null, null, 0);
		panel_list.add(panel);

		// Page 10
		msg = "<html>If the player chosen is PSG Sampler, our work ends here. Change (or set) file extension to '.psg'.<br>Do not break psg files into 16 KB blocks, once PSG Sampler can play big size files (until fit the MSX memory maper size).</html>";
		panel = createPanel(msg, null, null, 0);
		panel_list.add(panel);

		// Page 11
		msg = "<html>After generating the RAW file, open it in the MSX DSG. First, choose the MSX Player file type. Then click on the open file button.</html>";
		img_path[0] = "wizard/pic_006.png";
		bounds[0] = new Rectangle(150, 130, 250, 162);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 12
		msg = "<html>The sound frequency is set in the Sound pre-processing panel, as we already did.<br>You may preview the sound by pressing the play button. Press it again to stop the sound playing.</html>";
		img_path[0] = "wizard/pic_007.png";
		bounds[0] = new Rectangle(150, 150, 250, 159);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 13
		msg = "<html>The final step is to convert the 8-bit sound to the one of the MSX players available. For that, choose the destiny sound type, and then save.</html>";
		img_path[0] = "wizard/pic_008.png";
		bounds[0] = new Rectangle(150, 130, 250, 160);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Page 14
		msg = "<html>For MSX DSG, DSG 4-bit and Videohits, you must divide the file into blocks, if the length is greater than 16 kbytes. For that, check the split option. You may also choose the number of the blocks to save, e.g. 2, if it exceeds 2 blocks.</html>";
		img_path[0] = "wizard/pic_009.png";
		bounds[0] = new Rectangle(150, 150, 250, 160);
		panel = createPanel(msg, img_path, bounds, 1);
		panel_list.add(panel);

		// Final Page
		msg = "<html><u>MSX DSG</u><br><br>Developed by: Marcelo Silveira<br>Homepage: http://marmsx.msxall.com<br>E-mail: flamar98@hotmail.com<br><br>License: GNU-GPL v. 3.x</html>";
		panel = createPanel(msg, null, null, 0);
		panel_list.add(panel);
		contentPanel.add(panel);
	}

}
