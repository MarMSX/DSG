-= MAP Player - MSX Sample Player =-

Authors: Laurens Holst and Marcelo Silveira
Language: Assemby Z-80 for MSX computers
Country: The Netherlands and Brazil
E-mails: map at grauw.nl and flamar98 at hotmail.com
Project home-pages:
http://marmsx.msxall.com/projetos/dsg/english.php
http://map.grauwl.nl
License: BSD-2-Clause - https://opensource.org/licenses/BSD-2-Clause

Short description:

Plays 8-bit samples on MSX 1 or better.


D000  		   10 		ORG  &HD000		; Initial address
D000  F3	   20 		DI 			; Disable interruptions
D001  3E 07	   30 		LD   A,7		;  
D003  D3 A0	   40 		OUT  (&HA0),A		; Sel. PSG reg. 7
D005  3E B8	   50 		LD   A,&B10111000	; Sel. channels A,B,C
D007  D3 A1	   60 		OUT  (&HA1),A		; Change PSG Mixer
D009  06 07	   70 		LD   B,7		; 
D00B  78	   80 LPSG:	LD   A,B		; 
D00C  3D	   90 		DEC  A			; Zero regs. 0-6
D00D  D3 A0	  100 		OUT  (&HA0),A		; 
D00F  AF	  110 		XOR  A			; 
D010  D3 A1	  120 		OUT  (&HA1),A		; 
D012  10 F7	  130 		DJNZ LPSG		; 
D014  DB A8	  140 		IN   A,(&HA8)		; Read slots
D016  5F	  150 		LD   E,A		;
D017  06 04	  160 		LD   B,4		;
D019  CB 3F	  170 ROT:	SRL  A			; Sets all RAM
D01B  10 FC	  180 		DJNZ ROT		;
D01D  83	  190 		ADD  A,E		;
D01E  D3 A8	  200 		OUT  (&HA8),A		;

D020  21 00 00	  210 		LD   HL,0		; Data initial address
D023  11 00 80	  220 		LD   DE,&H8000		; Data length
D026  D9	  230 		EXX 			;
D027  0E A1	  240 		LD   C,&HA1		;
D029  16 00	  250 		LD   D,0		;
D02B  D9	  260 		EXX 			;
D02C  7E	  270 LOOP:	LD   A,(HL)		;
D02D  23	  280 		INC  HL			;
D02E  D9	  290 		EXX 			;
D02F  5F	  300 		LD   E,A		;
D030  21 00 D2	  310 		LD   HL,&HD200		; Link psg_table
D033  19	  320 		ADD  HL,DE		;
D034  46	  330 		LD   B,(HL)		;
D035  24	  340 		INC  H			;
D036  5E	  350 		LD   E,(HL)		;
D037  24	  360 		INC  H			;
D038  66	  370 		LD   H,(HL)		;
D039  3E 08	  380 		LD   A,8		;
D03B  D3 A0	  390 		OUT  (&HA0),A		;
D03D  3C	  400 		INC  A			;
D03E  ED 41	  410 		OUT  (C),B		;
D040  D3 A0	  420 		OUT  (&HA0),A		;
D042  ED 59	  430 		OUT  (C),E		;
D044  3C	  440 		INC  A			;
D045  D3 A0	  450 		OUT  (&HA0),A		;
D047  ED 61	  460 		OUT  (C),H		;
D049  06 08	  470 		LD   B,8		; Delay = 8
D04B  10 FE	  480 WAITLOOP:	DJNZ WAITLOOP		;
D04D  D9	  490 		EXX 			;
D04E  1B	  500 		DEC  DE			;
D04F  7A	  510 		LD   A,D		;
D050  B3	  520 		OR   E			;
D051  C2 2C D0	  530 		JP   NZ,LOOP		;

D054  DB A8	  540 		IN   A,(&HA8)		; 
D056  E6 F0	  550 		AND  &HF0		; Reset slots to 00xx
D058  D3 A8	  560 		OUT  (&HA8),A		; 
D05A  FB	  570 		EI 			; Enable interruptions
D05B  CD 90 00	  580 		CALL &H90		; Clear PSG
D05E  C9	  590 		RET 			; Return
